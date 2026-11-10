// Edge-to-edge viewports retain equal leading and trailing content insets.
function carouselTail(row,left,width){const tail=document.createElement('span');tail.className='carousel-tail';tail.style.cssText=`position:absolute;left:${left}px;top:0;width:${width}px;height:1px`;row.append(tail);}
currentBooks.style.width='355px';carouselTail(currentBooks,518,20);
Object.assign(heroes.style,{left:'-20px',width:'335px',padding:'0 20px',boxSizing:'border-box'});
byId.get('4079:21703').el.style.overflow='visible';
Object.assign(reviews.style,{left:'0',width:'335px'});
byId.get('4079:21773').el.style.left='16px';byId.get('4079:21784').el.style.left='268px';carouselTail(reviews,508,16);
carouselTail(cinemaRow,572,20);
for(const id of ['4057:15295','4057:15305'])byId.get(id).el.style.background='#f4f4f4';
for(const {n,el}of byId.values())if(n.text&&Math.abs(n.size-11)<.1){el.textContent=n.text.toLocaleUpperCase('ru');el.style.letterSpacing='1px';el.style.whiteSpace='nowrap';el.style.width='max-content';}
const headerMask=document.createElement('div');headerMask.id='headerMask';phone.insertBefore(headerMask,$('#bookbar'));
$('aside').setAttribute('aria-hidden','true');$('aside').inert=true;

// Discovery follows frame 4106:2514; real catalog covers are bundled locally.
function catalogAsset(file){const path='assets/catalog/'+file;return typeof ASSETS!=='undefined'?ASSETS[path]:path;}
discovery.innerHTML='';const avatar=document.createElement('img');avatar.className='discovery-avatar';avatar.src=carouselAsset('b686d.png');avatar.alt='Полина';discovery.append(avatar);
function discoveryShelf(title,items,compact=false){const heading=document.createElement('h3');heading.textContent=title;discovery.append(heading);const row=document.createElement('div');row.className='discovery-shelf'+(compact?' started-shelf':'');for(const item of items){const button=document.createElement('button');button.className=compact?'started-book':'discovery-book';button.setAttribute('aria-label',item.title);const img=document.createElement('img');img.src=item.catalog?catalogAsset(item.file):carouselAsset(item.file);img.alt=item.title;button.append(img);const text=document.createElement('span');if(compact){const b=document.createElement('b');b.textContent=item.title;const author=document.createElement('small');author.textContent=item.author;const progress=document.createElement('small');progress.className='started-progress';progress.textContent=item.progress+'% прочитано';text.append(b,author,progress);button.append(text);}else if(item.caption){text.textContent=item.caption;button.append(text);}button.onclick=()=>composer('Хочу прочитать «'+item.title+'»');row.append(button);}discovery.append(row);makeCarousel(row,title);}
discoveryShelf('Начинали читать',[
 {file:'master.jpg',catalog:true,title:'Мастер и Маргарита',author:'Михаил Булгаков',progress:36},
 {file:'4d3fe.png',title:'Скандинавские мифы',author:'Кэролин Ларрингон',progress:13}],true);
discoveryShelf('Хотели прочитать',[{file:'dbece.png',title:'Книга из отложенных'},{file:'4528b.png',title:'Мир'},{file:'9edd4.png',title:'Самая страшная книга'}]);
discoveryShelf('Новое про любовь',[{file:'f5167.png',title:'Тысячеликая героиня',caption:'Женские истории,\nкоторые меняют\nвзгляд на мир'},{file:'068ff.png',title:'Ася',caption:'Нежнее\nи светлее\nКарениной'},{file:'ed691.png',title:'Первая любовь',caption:'Больше горькой\nлюбви'}]);
discoveryShelf('Любопытное из популярного',[
 {file:'daughters.jpg',title:'Дочери Колыбели',caption:'Магия и дружба\nс испытаниями\nна прочность'},
 {file:'fox.png',title:'Смех лисы',caption:'Каникулы,\nкоторые хочется\nпродлить'},
 {file:'souls.jpg',title:'Семь способов засолки душ',caption:'Прошлое не спит.\nСамое время\nузнать его тайны'}].map(x=>({...x,catalog:true})));
discoveryShelf('Чуть бережнее к себе',[
 {file:'gentle.jpg',title:'К себе нежно',caption:'Чуть больше\nтепла и заботы\nк самому себе'},
 {file:'slow.jpg',title:'Я больше не пытаюсь все успеть',caption:'Разрешение\nзамедлиться\nи просто жить'},
 {file:'cats.jpg',title:'Если все кошки в мире исчезнут',caption:'О мелочах,\nкоторые значат\nочень многое'}].map(x=>({...x,catalog:true})));
discoveryShelf('Понять себя и других',[
 {file:'love.jpg',title:'Искусство любить',caption:'Любовь — это\nтоже искусство.\nПопробуем понять'},
 {file:'thinking.jpg',title:'Думай медленно… Решай быстро',caption:'Как мы думаем\nи почему порой\nсебе мешаем'},
 {file:'talk.jpg',title:'Как разговаривать с мудаками',caption:'Спокойнее\nв непростых\nразговорах'}].map(x=>({...x,catalog:true})));

// Book position sums only the reading sections, skipping every editorial insert.
const annaText=['4079:21688','4079:21721','4079:21735','4079:21745','4079:21766','4079:21795'];
let scrubBook=null;
function bookSegments(name){const max=Math.max(0,sc.scrollHeight-sc.clientHeight);return(name==='anna'?annaText:['4079:21846']).map(id=>{const el=sections.get(id);const start=Math.max(0,Math.min(max,el.offsetTop-102));const end=Math.max(start,Math.min(max,el.offsetTop+el.offsetHeight-102));return{start,end,length:end-start};});}
function activeReadingBook(){if(collapsed||mode==='home')return null;const y=sc.scrollTop+102;if(y>=sections.get('4079:21846').offsetTop-1)return'chekhov';const end=sections.get('4079:21795');if(y>=sections.get('4079:21681').offsetTop-1&&y<end.offsetTop+end.offsetHeight-1)return'anna';return null;}
function bookPosition(name){const segments=bookSegments(name);const total=segments.reduce((n,s)=>n+s.length,0);const distance=segments.reduce((n,s)=>n+Math.max(0,Math.min(s.length,sc.scrollTop-s.start)),0);return{segments,total,distance};}
function seekBook(name,distance){const {segments,total}=bookPosition(name);let remaining=Math.max(0,Math.min(total,distance));for(let i=0;i<segments.length;i++){const segment=segments[i];if(remaining<=segment.length||i===segments.length-1){sc.scrollTop=segment.start+remaining;break;}remaining-=segment.length;}last=sc.scrollTop;}
$('#seek').setAttribute('aria-label','Прогресс чтения книги');
$('#seek').onpointerdown=()=>{scrubBook=activeReadingBook();dragging=true;scrollLock=true;$('#progress').classList.add('dragging');showControls();};
$('#seek').oninput=e=>{const name=scrubBook||activeReadingBook();if(!name)return;scrollLock=true;seekBook(name,Number(e.target.value)/10000*bookPosition(name).total);update();showControls();};
$('#seek').onchange=()=>{endSeek();scrubBook=null;update();setTimeout(()=>scrollLock=false,180);};
$('#seek').onpointerup=()=>{endSeek();scrubBook=null;update();};
function skipBook(pixels){const name=activeReadingBook();if(!name)return;scrollLock=true;seekBook(name,bookPosition(name).distance+pixels);showControls();setTimeout(()=>scrollLock=false,180);}
$('#rewind').onclick=()=>{if(!$('#rewind').disabled)skipBook(-93);};$('#forward').onclick=()=>skipBook(186);
const updateBeforeRefinements=update;
update=function(){updateBeforeRefinements();const active=activeReadingBook(),name=dragging?scrubBook||active:active;const position=name?bookPosition(name):null;$('#progress').hidden=!name||densityOpen||editing||collapsed;if(position){const fraction=position.distance/Math.max(1,position.total);$('#seek').value=Math.round(fraction*10000);$('#progress').style.setProperty('--progress',fraction*100+'%');}$('#rewind').disabled=!position||position.distance<.5;$('#rewind').setAttribute('aria-disabled',String($('#rewind').disabled));headerMask.hidden=$('#bookbar').hidden||hidden;};
update();
