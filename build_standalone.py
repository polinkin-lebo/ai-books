from pathlib import Path
import base64,json,mimetypes,re,hashlib

root=Path(__file__).resolve().parent

def uri(path):
    kind=mimetypes.guess_type(str(path))[0] or 'application/octet-stream'
    return 'data:'+kind+';base64,'+base64.b64encode(path.read_bytes()).decode()

assets={str(p.relative_to(root)):uri(p) for p in (root/'assets').rglob('*') if p.is_file()}
html=(root/'index.html').read_text()
for name in ['style.css','ui-v2.css','carousels.css','data.js','app.js','ui-v2.js','carousels.js']:
    version=hashlib.sha256((root/name).read_bytes()).hexdigest()[:10]
    html=re.sub(r'(?<=")'+re.escape(name)+r'(?:\?v=[^"]*)?(?=")',lambda m:name+'?v='+version,html)
(root/'index.html').write_text(html)
for name in ['style.css','ui-v2.css','carousels.css']:
    css=(root/name).read_text()
    css=re.sub(r"url\('([^']+)'\)",lambda m:"url('"+uri(root/m[1])+"')",css)
    html=re.sub(r'<link rel="stylesheet" href="'+re.escape(name)+r'(?:\?[^"]*)?">',lambda m:'<style>'+css+'</style>',html)

html=re.sub(r'<script src="data\.js(?:\?[^"]*)?"></script>',lambda m:'<script>'+(root/'data.js').read_text()+'\nconst ASSETS='+json.dumps(assets)+';</script>',html)
for name in ['app.js','ui-v2.js','carousels.js']:
    js=(root/name).read_text()
    js=js.replace("im.src='assets/'+n.id.replace(':','-')+'.png'", "im.src=ASSETS['assets/'+n.id.replace(':','-')+'.png']")
    for asset,value in sorted(assets.items(),key=lambda x:len(x[0]),reverse=True):
        js=js.replace('src="'+asset+'"','src="'+value+'"').replace("'"+asset+"'", "'"+value+"'")
    js=js.replace('src="assets/design/${file}"','src="${ASSETS[\'assets/design/\'+file]}"')
    html=re.sub(r'<script src="'+re.escape(name)+r'(?:\?[^"]*)?"></script>',lambda m:'<script>'+js+'</script>',html)

target=root/'standalone.html'
target.write_text(html)
(root/'standalone.html').write_text(html)
print(f'{target.name}: {len(html.encode()):,} bytes, {len(assets)} embedded assets')
