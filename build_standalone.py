from pathlib import Path
import base64,json,mimetypes,re

root=Path(__file__).resolve().parent

def uri(path):
    kind=mimetypes.guess_type(str(path))[0] or 'application/octet-stream'
    return 'data:'+kind+';base64,'+base64.b64encode(path.read_bytes()).decode()

assets={str(p.relative_to(root)):uri(p) for p in (root/'assets').rglob('*') if p.is_file()}
html=(root/'index.html').read_text()
for name in ['style.css','ui-v2.css']:
    css=(root/name).read_text()
    css=re.sub(r"url\('([^']+)'\)",lambda m:"url('"+uri(root/m[1])+"')",css)
    html=html.replace('<link rel="stylesheet" href="'+name+'">','<style>'+css+'</style>')

html=html.replace('<script src="data.js"></script>','<script>'+(root/'data.js').read_text()+'\nconst ASSETS='+json.dumps(assets)+';</script>')
for name in ['app.js','ui-v2.js']:
    js=(root/name).read_text()
    js=js.replace("im.src='assets/'+n.id.replace(':','-')+'.png'", "im.src=ASSETS['assets/'+n.id.replace(':','-')+'.png']")
    for asset,value in sorted(assets.items(),key=lambda x:len(x[0]),reverse=True):
        js=js.replace('src="'+asset+'"','src="'+value+'"').replace("'"+asset+"'", "'"+value+"'")
    js=js.replace('src="assets/design/${file}"','src="${ASSETS[\'assets/design/\'+file]}"')
    html=html.replace('<script src="'+name+'"></script>','<script>'+js+'</script>')

target=root/'standalone.html'
target.write_text(html)
(root/'standalone.html').write_text(html)
print(f'{target.name}: {len(html.encode()):,} bytes, {len(assets)} embedded assets')
