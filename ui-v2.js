// Панели и дискавери: размеры и ассеты из 4057:14853, 14705, 14742, 14825.
const icon=(file)=>`<img class="ui-icon" src="assets/design/${file}" alt="" draggable="false">`;
let densityOpen=false,editing=false,dragging=false,keyboardLatin=false,keyboardShift=false;
const nativeIOS=/iPad|iPhone|iPod/.test(navigator.userAgent)||(navigator.platform==='MacIntel'&&navigator.maxTouchPoints>1);phone.classList.toggle('native-ios',nativeIOS);
density=100;
morning.remove();
homeState=()=>{};
$('#bookbar').innerHTML=`<button id="collapse" aria-label="Свернуть чтение">${icon('6d690.svg')}</button><div class="book-label"><b id="booktitle">Анна Каренина</b><span id="author">Лев Толстой</span></div><div class="header-spacer"></div><button id="settings" aria-label="Настройки чтения">${icon('b584a.svg')}</button><button id="more" aria-label="Меню книги">${icon('85b76.svg')}</button>`;
$('#footer').innerHTML=`<button id="mini" hidden aria-label="Вернуться к чтению"><img id="miniCover" src="assets/design/666ab.png" alt=""><span class="mini-title"><b></b><small></small></span><span class="mini-spacer"></span><span class="mini-cta">К чтению</span></button><div id="progress" hidden><input id="seek" type="range" aria-label="Позиция в ленте" min="0" max="10000" step="1" value="0"></div><div class="controlrow"><button id="spark" class="round glass" aria-label="Настроить количество ИИ-вставок">${icon('4dcdf.svg')}</button><form id="queryForm" class="glass"><span id="discoverySpark">${icon('e6a43.svg')}</span><input id="ask" type="text" inputmode="text" enterkeyhint="send" autocomplete="off" spellcheck="false" aria-label="Запрос" placeholder="Что будем читать?"><button id="sendInline" type="submit" hidden aria-label="Отправить запрос">↑</button></form><div id="densityInline" class="glass" hidden><div class="density-track"><div id="densityFill"></div><div class="density-ticks"></div><div class="density-ticks filled-ticks"></div></div><input aria-label="Степень ИИ" type="range" min="0" max="100" step="1" value="100"></div><button id="startAudio" class="round glass" aria-label="Включить плеер">${icon('c257f.svg')}</button><div id="audio" hidden><button id="rewind" class="glass" aria-label="Назад 15 секунд">${icon('aa08f.svg')}<small>15</small></button><button id="play" class="glass" aria-label="Пауза">${icon('ce6bc.svg')}</button><button id="forward" class="glass" aria-label="Вперёд 30 секунд">${icon('76457.svg')}<small>30</small></button></div></div><div id="context"></div>`;
$('#floating').innerHTML=icon('4dcdf.svg');$('#floating').classList.add('glass');
const discovery=document.createElement('div');discovery.id='discovery';discovery.hidden=true;
discovery.innerHTML=`<img class="discovery-avatar" src="assets/design/b686d.png" alt="Полина"><h2>Доброе утро, Полина!<br>найдем что-то новое?</h2><div class="discovery-recs"><div class="discovery-columns"></div></div><h3>Хотели прочитать</h3><div class="discovery-saved"><div class="discovery-columns"></div></div>`;
phone.insertBefore(discovery,$('#status'));
const titles=['Снова сложная любовь,<br>но короче','Нежнее и светлее Карениной','Больше горькой любви'];
for(const [sel,files] of [['.discovery-recs',['f5167.png','068ff.png','ed691.png']],['.discovery-saved',['dbece.png','4528b.png','9edd4.png']]]){
 files.forEach((file,i)=>{const b=document.createElement('button');b.className='discovery-book';b.innerHTML=`<img src="assets/design/${file}" alt="${sel.includes('recs')?['Тысячеликая героиня','Ася','Первая любовь'][i]:['Книга из отложенных','Мир','Самая страшная книга'][i]}">${sel.includes('recs')?'<span>'+titles[i]+'</span>':''}`;b.onclick=()=>go('4079:21824');discovery.querySelector(sel+' .discovery-columns').append(b);});
}
const keyboard=document.createElement('section');keyboard.id='iosKeyboard';keyboard.hidden=true;keyboard.setAttribute('aria-label','Клавиатура iOS');phone.append(keyboard);
function drawKeyboard(){
 const rows=keyboardLatin?['qwertyuiop','asdfghjkl','zxcvbnm']:['йцукенгшщзх','фывапролджэ','ячсмитьбю'];
 keyboard.innerHTML='<div class="keyboard-accessory"><button type="button" data-key="close" aria-label="Скрыть клавиатуру">⌄</button></div>'+rows.map((r,i)=>'<div class="key-row row-'+i+'">'+(i===2?'<button class="modifier" data-key="shift" aria-label="Заглавные буквы">⇧</button>':'')+[...r].map(k=>'<button data-key="'+k+'">'+(keyboardShift?k.toUpperCase():k)+'</button>').join('')+(i===2?'<button class="modifier" data-key="backspace" aria-label="Удалить символ">⌫</button>':'')+'</div>').join('')+'<div class="key-row bottom-row"><button class="modifier" data-key="numbers">123</button><button class="modifier language" data-key="lang" aria-label="Сменить язык">◎</button><button class="space" data-key="space">пробел</button><button class="return" data-key="send">отправить</button></div><div class="keyboard-bottom"><span>◎</span><span>♩</span></div><div class="home-indicator"></div>';
 keyboard.querySelectorAll('button').forEach(b=>{b.onpointerdown=e=>e.preventDefault();b.onclick=()=>keyPress(b.dataset.key);});
}
function keyPress(key){const input=$('#ask');if(key==='close'){endEditing();return}if(key==='send'){sendQuery();return}if(key==='lang'){keyboardLatin=!keyboardLatin;drawKeyboard();return}if(key==='shift'){keyboardShift=!keyboardShift;drawKeyboard();return}if(key==='numbers'){keyboardLatin=true;keyboard.innerHTML='<div class="keyboard-accessory"><button data-key="close">⌄</button></div>'+['1234567890','-/:;()₽&@"','.,?!\''].map(r=>'<div class="key-row">'+[...r].map(k=>'<button>'+k+'</button>').join('')+'</div>').join('')+'<div class="key-row"><button data-key="lang">АБВ</button><button data-key="space">пробел</button><button data-key="send">отправить</button></div><div class="home-indicator"></div>';keyboard.querySelectorAll('button').forEach(b=>{b.onpointerdown=e=>e.preventDefault();b.onclick=()=>b.dataset.key?keyPress(b.dataset.key):insertText(b.textContent)});return}if(key==='backspace'){const start=input.selectionStart??input.value.length,end=input.selectionEnd??start;input.setRangeText('',start===end?Math.max(0,start-1):start,end,'end');input.dispatchEvent(new Event('input'));return}insertText(key==='space'?' ':keyboardShift?key.toUpperCase():key);if(keyboardShift){keyboardShift=false;drawKeyboard()}}
function insertText(text){const input=$('#ask');input.setRangeText(text,input.selectionStart??input.value.length,input.selectionEnd??input.value.length,'end');input.dispatchEvent(new Event('input'));}
function beginEditing(){editing=true;densityOpen=false;showControls(true);phone.classList.add('editing');keyboard.hidden=nativeIOS;if(!nativeIOS)drawKeyboard();update();}
function endEditing(){editing=false;keyboard.hidden=true;phone.classList.remove('editing');$('#ask').blur();update();}
composer=(prefill='')=>{close();if(prefill)$('#ask').value=prefill;beginEditing();$('#ask').focus();};
function sendQuery(){const q=$('#ask').value.trim();if(!q)return;$('#ask').value='';endEditing();if(collapsed){collapsed=false;discovery.hidden=true;sc.style.visibility='visible';sc.scrollTop=saved;}submit(q);update();}
$('#queryForm').onsubmit=e=>{e.preventDefault();sendQuery()};$('#ask').onfocus=beginEditing;$('#ask').oninput=()=>{$('#sendInline').hidden=!$('#ask').value.trim();};
phone.addEventListener('pointerdown',e=>{if(editing&&!e.target.closest('#footer,#iosKeyboard'))endEditing();});
document.addEventListener('keydown',e=>{if(e.key==='Escape')endEditing()});
const insetIds=['4079:21681','4079:21692','4079:21723','4079:21738','4079:21748','4079:21768','cinema','4079:21797','4079:21811','4079:21820','4079:21824'];
const levels=[10,35,20,90,45,60,75,10,55,70,85];
function setDensity(value){
 const visible=[...sections.values()].filter(s=>s.style.display!=='none');let anchor=visible.find(s=>s.offsetTop+s.offsetHeight>sc.scrollTop+100);const delta=anchor?sc.scrollTop-anchor.offsetTop:0;density=Number(value);scrollLock=true;
 insetIds.forEach((id,i)=>sections.get(id).style.display=density>=levels[i]?'':'none');
 if(anchor&&anchor.style.display!=='none')sc.scrollTop=anchor.offsetTop+delta;else if(anchor){const next=[...sections.values()].find(s=>s.style.display!=='none'&&s.offsetTop>=sc.scrollTop);if(next)sc.scrollTop=next.offsetTop;}
 $('#densityFill').style.width=density+'%';$('#densityInline').style.setProperty('--density',density+'%');$('#densityInline input').value=density;setTimeout(()=>scrollLock=false,180);update();
}
$('#densityInline input').oninput=e=>setDensity(e.target.value);
$('#spark').onclick=()=>{if(editing)endEditing();densityOpen=!densityOpen;update();};
$('#startAudio').onclick=()=>{audio=true;playing=true;showControls();update();};
$('#play').onclick=()=>{playing=false;audio=false;update();};
$('#rewind').onclick=()=>{pos=Math.max(0,pos-15);sc.scrollTop=Math.max(0,sc.scrollTop-93);showControls();};
$('#forward').onclick=()=>{pos+=30;sc.scrollTop=Math.min(sc.scrollHeight-sc.clientHeight,sc.scrollTop+186);showControls();};
$('#collapse').onclick=()=>{endEditing();saved=sc.scrollTop;suspendedBook=book;collapsed=true;densityOpen=false;scrollLock=true;sc.style.visibility='hidden';discovery.hidden=false;showControls();update();setTimeout(()=>scrollLock=false,180);};
$('#mini').onclick=()=>{collapsed=false;discovery.hidden=true;sc.style.visibility='visible';scrollLock=true;sc.scrollTop=saved;showControls();update();setTimeout(()=>scrollLock=false,180);};
const goOriginal=go;
go=(id,options={})=>{if(editing)endEditing();discovery.hidden=true;sc.style.visibility='visible';densityOpen=false;goOriginal(id,options);};
$('#settings').onclick=settings;
$('#more').onclick=()=>{sheet('<h2>'+$('#booktitle').textContent+'</h2><button class="pill" id="bookChapters">Мои главы</button><button class="pill" id="bookEnd">К концу книги</button>');$('#bookChapters').onclick=()=>{close();go('4079:21723')};$('#bookEnd').onclick=()=>{close();go('4079:21797')};};
$('#seek').onpointerdown=()=>{dragging=true;scrollLock=true;$('#progress').classList.add('dragging');showControls();};
$('#seek').oninput=e=>{scrollLock=true;sc.scrollTop=(+e.target.value/10000)*(sc.scrollHeight-sc.clientHeight);update();showControls();};
function endSeek(){if(!dragging)return;dragging=false;$('#progress').classList.remove('dragging');update();setTimeout(()=>scrollLock=false,180);}
$('#seek').onpointerup=endSeek;$('#seek').onpointercancel=endSeek;$('#seek').onchange=()=>{endSeek();setTimeout(()=>scrollLock=false,180);};
update=function(){
 const y=sc.scrollTop+150,rec=sections.get('4079:21824').offsetTop,next=sections.get('4079:21846').offsetTop,start=sections.get('4079:21681').offsetTop;
 book=collapsed?suspendedBook:y>=next?'chekhov':'anna';mode=collapsed?'discovery':y<start?'home':density>0&&sections.get('4079:21824').style.display!=='none'&&y>=rec&&y<next?'recommend':'reading';
 const reading=mode==='reading'||dragging,disco=mode==='discovery';phone.classList.toggle('discovery-mode',disco);phone.classList.toggle('home-mode',!reading);phone.classList.toggle('density-mode',densityOpen);phone.classList.toggle('audio-mode',audio&&reading&&!densityOpen&&!editing);
 $('#bookbar').hidden=!reading;$('#booktitle').textContent=book==='anna'?'Анна Каренина':'Дама с собачкой';$('#author').textContent=book==='anna'?'Лев Толстой':'Антон Чехов';
 $('#mini').hidden=!disco;$('#mini .mini-title b').textContent=$('#booktitle').textContent;$('#mini .mini-title small').textContent=$('#author').textContent;$('#miniCover').src=book==='anna'?'assets/4079-21644.png':'assets/design/666ab.png';
 $('#footer').classList.toggle('home',!reading);$('#spark').hidden=disco;$('#queryForm').hidden=densityOpen;$('#densityInline').hidden=!densityOpen;
 $('#startAudio').hidden=!reading||audio||densityOpen||editing;$('#audio').hidden=!reading||!audio||densityOpen||editing;$('#progress').hidden=!reading||!audio||densityOpen||editing;
 $('#ask').placeholder=reading?'Спросить про книгу и не только':'Что будем читать?';$('#ask').hidden=false;$('#discoverySpark').hidden=reading||densityOpen;$('#sendInline').hidden=!editing||!$('#ask').value.trim();
 $('#play').innerHTML=icon(playing?'ce6bc.svg':'c257f.svg');$('#play').setAttribute('aria-label',playing?'Пауза':'Воспроизвести');
 $('#context').textContent=densityOpen?'Больше вставок, подсказок и рассуждений':reading?`В контексте: «${$('#booktitle').textContent}» · глава ${book==='anna'?'XXXIV':'I'}`:'';$('#context').classList.toggle('density-caption',densityOpen);
 if(!dragging){const v=sc.scrollTop/Math.max(1,sc.scrollHeight-sc.clientHeight);$('#seek').value=Math.round(v*10000);$('#progress').style.setProperty('--progress',v*100+'%');}else $('#progress').style.setProperty('--progress',$('#seek').value/100+'%');
 const current=[...labels].reverse().find(([,id])=>sections.get(id).style.display!=='none'&&sc.scrollTop+180>=sections.get(id).offsetTop);document.querySelectorAll('nav button').forEach(b=>b.classList.toggle('active',b.dataset.id===current?.[1]));
};
$('#reset').onclick=()=>{endEditing();close();document.querySelectorAll('.reply').forEach(r=>r.remove());collapsed=false;audio=false;playing=true;densityOpen=false;discovery.hidden=true;sc.style.visibility='visible';fontSize=21;document.querySelectorAll('.reader p').forEach(p=>p.style.cssText='');setDensity(100);go('4079:21636',{instant:true});};
$('#floating').onclick=()=>{densityOpen=true;update();};
setDensity(100);update();window.prototype.getState=()=>({mode,book,collapsed,audio,playing,density,densityOpen,editing,hidden,scroll:sc.scrollTop});

if(nativeIOS&&window.visualViewport)visualViewport.addEventListener('resize',()=>{phone.style.setProperty('--native-keyboard-height',Math.max(0,innerHeight-visualViewport.height)+'px');});
sections.get('4079:21688').style.marginTop='-8px';
// Обводки и исходная векторная оценка сохранены из макета.
for(const id of ['4079:21659','4079:21660','4079:21661','4079:21764']){const el=byId.get(id)?.el;if(el){el.style.boxShadow='inset 0 0 0 .5px rgb(37,143,172)';el.style.background='transparent';}}
const sourceRating=byId.get('4079:21803').el;
byId.get('4079:21801').el.append(sourceRating);rating.remove();
for(let value=1;value<=5;value++){const b=document.createElement('button');b.className='source-rating-hit';b.setAttribute('aria-label','Оценка '+value+' из 5');b.style.cssText='position:absolute;top:0;left:'+((value-1)*52)+'px;width:48px;height:48px;background:transparent;padding:0';b.onclick=()=>{sourceRating.dataset.rating=value;toast('Ваша оценка: '+value+' из 5')};sourceRating.append(b);}

for(const layer of document.querySelectorAll('.density-ticks')){for(let i=0;i<10;i++){const tick=document.createElement('span');tick.style.left=(8.13+i*(84.1/9))+'%';layer.append(tick);}}
