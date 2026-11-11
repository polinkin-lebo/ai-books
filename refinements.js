// Edge-to-edge viewports retain equal leading and trailing content insets.
function carouselTail(row,left,width){const tail=document.createElement('span');tail.className='carousel-tail';tail.style.cssText=`position:absolute;left:${left}px;top:0;width:${width}px;height:1px`;row.append(tail);}
Object.assign(currentBooks.style,{left:'0',width:'375px'});for(const id of ['4079:21642','4079:21649'])byId.get(id).el.style.left=(parseFloat(byId.get(id).el.style.left)+20)+'px';carouselTail(currentBooks,538,20);
Object.assign(heroes.style,{left:'-20px',width:'335px',padding:'0 20px',boxSizing:'border-box'});
byId.get('4079:21703').el.style.overflow='visible';
Object.assign(reviews.style,{left:'0',width:'335px'});
byId.get('4079:21773').el.style.left='16px';byId.get('4079:21784').el.style.left='268px';carouselTail(reviews,508,16);
Object.assign(cinemaRow.style,{left:'0',width:'375px'});for(const el of [...cinemaRow.children])el.style.left=(parseFloat(el.style.left)+20)+'px';carouselTail(cinemaRow,592,20);
for(const id of ['4057:15295','4057:15305'])byId.get(id).el.style.background='#f4f4f4';
for(const {n,el}of byId.values())if(n.text&&Math.abs(n.size-11)<.1){el.textContent=n.text.toLocaleUpperCase('ru');el.style.letterSpacing='1px';el.style.whiteSpace='nowrap';el.style.width='max-content';}
const headerMask=document.createElement('div');headerMask.id='headerMask';phone.insertBefore(headerMask,$('#bookbar'));
$('aside').setAttribute('aria-hidden','true');$('aside').inert=true;

// Discovery follows frame 4106:2514; real catalog covers are bundled locally.
function catalogAsset(file){const path='assets/catalog/'+file;return typeof ASSETS!=='undefined'?ASSETS[path]:path;}
discovery.innerHTML='';const avatar=document.createElement('img');avatar.className='discovery-avatar';avatar.src=carouselAsset('b686d.png');avatar.alt='Полина';discovery.append(avatar);
function discoveryShelf(title,items,compact=false){const heading=document.createElement('h3');heading.textContent=title;discovery.append(heading);const row=document.createElement('div');row.className='discovery-shelf'+(compact?' started-shelf':'');for(const item of items){const button=document.createElement('button');button.className=compact?'started-book':'discovery-book';button.setAttribute('aria-label',item.title);const img=document.createElement('img');img.src=item.catalog?catalogAsset(item.file):carouselAsset(item.file);img.alt=item.title;img.dataset.coverKey=item.file;button.append(img);const text=document.createElement('span');if(compact){const b=document.createElement('b');b.textContent=item.title;const author=document.createElement('small');author.textContent=item.author;const progress=document.createElement('small');progress.className='started-progress';progress.textContent=item.progress+'% прочитано';text.append(b,author,progress);button.append(text);}else if(item.caption){text.textContent=item.caption;button.append(text);}button.onclick=()=>composer('Хочу прочитать «'+item.title+'»');row.append(button);}discovery.append(row);makeCarousel(row,title);}
discoveryShelf('Начинали читать',[
 {file:'master.jpg',catalog:true,title:'Мастер и Маргарита',author:'Михаил Булгаков',progress:36},
 {file:'4d3fe.png',title:'Скандинавские мифы',author:'Кэролин Ларрингон',progress:13}],true);
discoveryShelf('Хотели прочитать',[{file:'dbece.png',title:'Страшные сказки',caption:'Знакомые сказки\nс неожиданной\nтёмной стороной'},{file:'4528b.png',title:'Мирэ',caption:'Для вечера\nс необычной\nгероиней'},{file:'9edd4.png',title:'Самая страшная книга',caption:'Когда хочется\nпощекотать\nсебе нервы'}]);
discoveryShelf('Новое про любовь',[{file:'f5167.png',title:'Тысячеликая героиня',caption:'Истории женщин,\nменяющие\nвзгляд на мир'},{file:'068ff.png',title:'Ася',caption:'Нежнее\nи светлее\nКарениной'},{file:'ed691.png',title:'Первая любовь',caption:'Больше горькой\nлюбви'}]);
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
function activeReadingBook(){if(collapsed||mode==='home')return null;const y=sc.scrollTop+102,visible=visibleReadingBook();if(visible==='chekhov')return'chekhov';if(visible==='anna'&&y<sections.get('4079:21681').offsetTop)return'anna';if(y>=sections.get('4079:21846').offsetTop-1)return'chekhov';const end=sections.get('4079:21795');if(y>=sections.get('4079:21681').offsetTop-1&&y<end.offsetTop+end.offsetHeight-1)return'anna';return null;}
function bookPosition(name){const segments=bookSegments(name);const total=segments.reduce((n,s)=>n+s.length,0);const rawDistance=segments.reduce((n,s)=>n+Math.max(0,Math.min(s.length,sc.scrollTop-s.start)),0),distance=rawDistance<1?0:rawDistance;return{segments,total,distance};}
function seekBook(name,distance){const {segments,total}=bookPosition(name);let remaining=Math.max(0,Math.min(total,distance));for(let i=0;i<segments.length;i++){const segment=segments[i];if(remaining<=segment.length||i===segments.length-1){sc.scrollTop=segment.start+remaining;break;}remaining-=segment.length;}last=sc.scrollTop;}
$('#seek').setAttribute('aria-label','Прогресс чтения книги');
$('#seek').onpointerdown=()=>{scrubBook=activeReadingBook();dragging=true;scrollLock=true;$('#progress').classList.add('dragging');showControls();};
$('#seek').oninput=e=>{const name=scrubBook||activeReadingBook();if(!name)return;scrollLock=true;seekBook(name,Number(e.target.value)/10000*bookPosition(name).total);update();showControls();};
$('#seek').onchange=()=>{endSeek();scrubBook=null;update();setTimeout(()=>scrollLock=false,180);};
$('#seek').onpointerup=()=>{endSeek();scrubBook=null;update();};
function skipBook(pixels){const name=activeReadingBook();if(!name)return;scrollLock=true;seekBook(name,bookPosition(name).distance+pixels);showControls();setTimeout(()=>scrollLock=false,180);}
$('#rewind').onclick=()=>{if(!$('#rewind').disabled)skipBook(-93);};$('#forward').onclick=()=>skipBook(186);
const updateBeforeRefinements=update;
update=function(){updateBeforeRefinements();const active=activeReadingBook(),name=dragging?scrubBook||active:active;const position=name?bookPosition(name):null;$('#progress').hidden=!name||densityOpen||editing||collapsed;if(position){const fraction=position.distance/Math.max(1,position.total);$('#seek').value=Math.round(fraction*10000);$('#progress').style.setProperty('--progress',fraction*100+'%');}$('#rewind').disabled=!position||position.distance<.5;$('#rewind').setAttribute('aria-disabled',String($('#rewind').disabled));headerMask.hidden=$('#bookbar').hidden||hidden;for(const id of ['progress','context','startAudio','audio'])$('#'+id).setAttribute('aria-hidden',String(editing));const send=$('#keyboardSend');if(send){send.setAttribute('aria-hidden',String(!editing));send.tabIndex=editing?0:-1;}};
// Small covers retain the original rounded slots.
for(const id of ['4079:21644','4079:21651']){const el=byId.get(id).el;el.style.borderRadius='4px';el.style.overflow='hidden';}
Object.assign(byId.get('4079:21799').el.style,{left:'0',width:'335px',textAlign:'center',letterSpacing:'2px'});
const bookIntro=sections.get('4079:21671');bookIntro.classList.add('book-intro');bookIntro.style.height='155px';bookIntro.innerHTML=bookHeading('Анна Каренина','Лев Толстой','XXXIV');
function visibleReadingBook(){if(collapsed)return null;const viewportTop=sc.scrollTop+40,viewportBottom=sc.scrollTop+772;for(const id of [...annaText,'4079:21846']){const section=sections.get(id);if(section.style.display==='none')continue;for(const p of section.querySelectorAll('p')){const top=section.offsetTop+p.offsetTop,bottom=top+p.offsetHeight;if(top<viewportBottom&&bottom>viewportTop)return id==='4079:21846'?'chekhov':'anna';}}return null;}
for(const img of document.querySelectorAll('.discovery-book>img,.book-option>img')){const frame=document.createElement('span');frame.className='cover-frame';if(/(?:slow|thinking|talk)\.(?:jpg|png)/.test(img.dataset.coverKey||img.src))frame.classList.add('light-cover');img.replaceWith(frame);frame.append(img);}
for(const img of discovery.querySelectorAll('.started-book>img'))img.style.borderRadius='4px';
const sparkBeforeMorph=$('#spark').onclick;$('#spark').onclick=()=>{if(editing){endEditing();return;}if(mode==='home'||collapsed){composer();return;}sparkBeforeMorph();};
function spaceBookShelves(){for(const section of document.querySelectorAll('.books-section')){const row=section.querySelector('.book-carousel'),height=Math.max(...[...row.children].map(e=>e.offsetHeight));row.style.height=height+'px';section.style.height=Math.max(section.offsetHeight,58+height+24)+'px';}update();}
document.fonts.ready.then(spaceBookShelves);
// Titles and keyboard geometry are taken from the supplied Figma frames.
function bookHeading(title,author,chapter){return '<div class="book-title-author"><h2>'+title+'</h2><div class="book-author">'+author+'</div></div><div class="book-chapter">Глава '+chapter+'</div>';}
const chekhovReader=sections.get('4079:21846');chekhovReader.querySelector('h2').remove();chekhovReader.querySelector('div').remove();const chekhovHeading=document.createElement('div');chekhovHeading.className='book-heading';chekhovHeading.innerHTML=bookHeading('Дама с собачкой','Антон Чехов','I');chekhovReader.prepend(chekhovHeading);chekhovReader.style.paddingTop='0';
const keyboardSend=document.createElement('button');keyboardSend.id='keyboardSend';keyboardSend.type='submit';keyboardSend.setAttribute('form','queryForm');keyboardSend.textContent='Отправить';$('.controlrow').append(keyboardSend);
// Persistent playback uses the same controls and handlers in a separate dock.
for(const [id,label] of [['4057:15302','Смотреть кино'],['4057:15312','Купить билет']]){const button=byId.get(id).el;button.setAttribute('aria-label',label);button.classList.add('adaptation-action');const text=button.querySelector('.text');text.textContent=label;text.style.cssText='position:static;width:auto;height:auto;font:500 14px/17px YS;letter-spacing:.14px;color:white;white-space:nowrap';}
const playerSpace=document.createElement('div');playerSpace.className='player-space';$('#startAudio').before(playerSpace);const audioDock=document.createElement('div');audioDock.id='audioDock';audioDock.setAttribute('role','group');audioDock.setAttribute('aria-label','Управление воспроизведением');audioDock.append($('#startAudio'),$('#audio'));phone.append(audioDock);
$('#play').onclick=()=>{playing=false;audio=false;showControls(true);update();$('#startAudio').focus({preventScroll:true});};
const updateBeforeAudioDock=update;update=function(){updateBeforeAudioDock();const dockActive=mode==='reading'&&!densityOpen&&!editing&&!collapsed;audioDock.setAttribute('aria-hidden',String(!dockActive||hidden&&!audio));$('#audio').setAttribute('aria-hidden',String(!dockActive||!audio));$('#startAudio').setAttribute('aria-hidden',String(!dockActive||audio||hidden));for(const button of audioDock.querySelectorAll('button'))button.tabIndex=(button.closest('#audio')?dockActive&&audio:dockActive&&!audio&&!hidden)?0:-1;};
byId.get('4079:21842').el.innerHTML=icon('4dcdf.svg');
update();
