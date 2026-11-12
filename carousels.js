// Source geometry normalized from the 640 px Figma presentation to 375 px.
function carouselAsset(file){const path='assets/design/'+file;return typeof ASSETS!=='undefined'?ASSETS[path]:path;}
function makeCarousel(el,label){
 el.classList.add('horizontal-carousel');el.setAttribute('aria-label',label);el.setAttribute('role','region');el.tabIndex=0;
 el.querySelectorAll('img').forEach(img=>img.draggable=false);
 let gesture=null,lastDrag=0;
 el.addEventListener('pointerdown',e=>{if(e.pointerType==='touch'||e.button!==0)return;gesture={id:e.pointerId,x:e.clientX,y:e.clientY,left:el.scrollLeft,moved:false};});
 el.addEventListener('pointermove',e=>{if(!gesture)return;const dx=e.clientX-gesture.x,dy=e.clientY-gesture.y;if(!gesture.moved&&Math.abs(dy)>14&&Math.abs(dy)>Math.abs(dx)){gesture=null;return;}if(Math.abs(dx)>14&&Math.abs(dx)>Math.abs(dy)*1.6){gesture.moved=true;lastDrag=Date.now();el.setPointerCapture(e.pointerId);el.classList.add('is-dragging');}if(gesture.moved){e.preventDefault();el.scrollLeft=gesture.left-dx;}});
 const finish=()=>{if(gesture?.moved)lastDrag=Date.now();gesture=null;el.classList.remove('is-dragging');};
 el.addEventListener('pointerup',finish);el.addEventListener('pointercancel',finish);el.addEventListener('lostpointercapture',finish);
 el.addEventListener('click',e=>{if(Date.now()-lastDrag<250){e.preventDefault();e.stopImmediatePropagation();}},true);
 el.addEventListener('wheel',e=>{if(Math.abs(e.deltaY)>=Math.abs(e.deltaX)/1.6){e.preventDefault();(el.closest('#discovery')||sc).scrollTop+=e.deltaY;return;}if(Math.abs(e.deltaX)<3){e.preventDefault();return;}const before=el.scrollLeft;el.scrollLeft+=e.deltaX*.7;if(el.scrollLeft!==before)e.preventDefault();},{passive:false});
 el.addEventListener('keydown',e=>{if(e.target!==el)return;if(e.key==='ArrowRight'||e.key==='ArrowLeft'){e.preventDefault();el.scrollBy({left:e.key==='ArrowRight'?180:-180,behavior:'smooth'});}});
}

// Current books: retain the original two 255×84 cards and their content.
const currentBooks=byId.get('4079:21641').el;
Object.assign(currentBooks.style,{width:'335px',overflowX:'auto',overflowY:'hidden'});
sections.get('4079:21640').querySelectorAll('.hot').forEach(el=>el.remove());
byId.get('4079:21642').el.onclick=()=>go('4079:21681');
for(const [id,name]of [['4079:21642','Продолжить Анну Каренину'],['4079:21649','Скандинавские мифы']]){const el=byId.get(id).el;el.setAttribute('role','button');el.setAttribute('aria-label',name);el.tabIndex=0;el.onkeydown=e=>{if(e.key==='Enter')el.click();};}
byId.get('4079:21649').el.onclick=()=>composer('Хочу скандинавские мифы');
for(const [id,file]of [['4079:21644','cbdea.png'],['4079:21651','4d3fe.png']]){const img=byId.get(id).el.querySelector('img');img.src=carouselAsset(file);img.style.objectFit='cover';}
makeCarousel(currentBooks,'Читаю сейчас');

// Characters: exact circular 60×60 slots, 12 px gap, 6 px label spacing.
const heroes=byId.get('4079:21705').el;
heroes.innerHTML='';Object.assign(heroes.style,{width:'303px',height:'80px'});heroes.classList.add('hero-carousel');
const heroNames=['Анна','Вронский','Каренин','Левин','Китти'];
const heroFiles=['df600.png','10b5a.png','c799d.png','91410.png','70534.png'];
const heroHotspots=[...sections.get('4079:21692').querySelectorAll('.hot')].filter(el=>el.getAttribute('aria-label')?.startsWith('О герое:'));
heroNames.forEach((name,i)=>{const button=document.createElement('button');button.className='hero-item';button.setAttribute('aria-label','О герое: '+name);const img=document.createElement('img');img.src=carouselAsset(heroFiles[i]);img.alt=name;const caption=document.createElement('span');caption.textContent=name;button.append(img,caption);button.onclick=heroHotspots[i]?.onclick;heroes.append(button);});
heroHotspots.forEach(el=>el.remove());makeCarousel(heroes,'Герои книги');

// Reviews retain their editable source text and 240 px cards.
const reviews=byId.get('4079:21772').el;
Object.assign(reviews.style,{width:'303px',height:'185px',overflowX:'auto',overflowY:'hidden'});
for(const [id,file]of [['4079:21777','3f0aa.png'],['4079:21788','d8563.png']]){const el=byId.get(id).el;Object.assign(el.style,{width:'40px',height:'40px',borderRadius:'50%',overflow:'hidden'});const img=el.querySelector('img');img.src=carouselAsset(file);img.style.objectFit='cover';}
makeCarousel(reviews,'Отзывы читателей');

// Move the existing film/theatre section directly below the book rating.
const cinema=sections.get('cinema');sections.get('4079:21797').after(cinema);
cinema.style.height='653px';byId.get('4057:15291').el.style.height='653px';
const cinemaHeading=document.createElement('h3');cinemaHeading.className='cinema-heading';cinemaHeading.textContent='Другой взгляд на сюжет';cinema.append(cinemaHeading);
const cinemaRow=byId.get('4057:15292').el;
Object.assign(cinemaRow.style,{width:'355px',height:'413px',top:'58px',overflowX:'auto',overflowY:'hidden'});
byId.get('4057:15313').el.style.top='483px';byId.get('4057:15314').el.style.fontSize='18px';
for(const [id,file]of [['4057:15294','1cda8.png'],['4057:15304','329c0.png']]){const imageSlot=byId.get(id).el;imageSlot.style.borderRadius=id==='4057:15294'?'12px 12px 0 0':'16px 16px 0 0';imageSlot.style.overflow='hidden';const img=imageSlot.querySelector('img');img.src=carouselAsset(file);img.style.objectFit='cover';}
for(const id of ['4057:15295','4057:15305'])byId.get(id).el.style.borderRadius='0 0 16px 16px';
const cinemaActions=[...cinema.querySelectorAll('.hot')];
for(const id of ['4057:15302','4057:15312']){const target=byId.get(id).el;const original=cinemaActions.find(el=>el.getAttribute('aria-label')==='Где посмотреть');target.classList.add('clickable-source');target.setAttribute('role','button');target.tabIndex=0;target.onclick=original?.onclick;target.onkeydown=e=>{if(e.key==='Enter'||e.key===' '){e.preventDefault();target.click();}};}
for(const [id,label]of [['4057:15316','Что изменилось относительно книги?'],['4057:15317','Что есть в театре?']]){const el=byId.get(id).el;el.style.background='transparent';el.style.boxShadow='inset 0 0 0 .5px #258fac';el.classList.add('clickable-source');el.setAttribute('role','button');el.tabIndex=0;el.onclick=()=>composer(label);el.onkeydown=e=>{if(e.key==='Enter')el.click();};}
cinemaActions.forEach(el=>el.remove());makeCarousel(cinemaRow,'Кино и театр');

// Book covers and their captions move together; Chekhov opens immediately.
function buildBooksSection(id,title,files,names,captions=[]){
 const section=sections.get(id);section.innerHTML='';section.classList.add('books-section');
 const heading=document.createElement('h3');heading.textContent=title;section.append(heading);
 const row=document.createElement('div');row.className='book-carousel';
 files.forEach((file,i)=>{const button=document.createElement('button');button.className='book-option';button.setAttribute('aria-label',names[i]);const img=document.createElement('img');img.src=carouselAsset(file);img.alt=names[i];button.append(img);if(captions[i]){const text=document.createElement('span');text.textContent=captions[i];button.append(text);}button.onclick=()=>i===0&&id==='4079:21811'?go('4079:21846'):composer('Хочу прочитать «'+names[i]+'»');row.append(button);});
 section.append(row);makeCarousel(row,title);
}
buildBooksSection('4079:21811','Почитать дальше',['666ab.png','068ff.png','ed691.png'],['Дама с собачкой','Ася','Первая любовь'],['Снова сложная любовь — только в нескольких десятках страниц','Нежнее и светлее Карениной','Больше горькой любви']);
buildBooksSection('4079:21820','Понравилось ранее',['dbece.png','4528b.png','9edd4.png'],['Страшные русские сказки','Мирэ','Самая страшная книга'],['Знакомые сказки — темнее и взрослее','Сильная героиня и сложный выбор','Мрачные истории для твоего вечера']);
discovery.querySelectorAll('.discovery-recs,.discovery-saved').forEach(el=>makeCarousel(el,el.classList.contains('discovery-recs')?'Рекомендации':'Хотели прочитать'));
// Keep traversal and scenario selection in the same order as the final feed.
const orderedSections=[...sections.entries()].sort((a,b)=>a[1].offsetTop-b[1].offsetTop);sections.clear();orderedSections.forEach(([id,node])=>sections.set(id,node));
const updateBeforeCarousels=update;
update=function(){updateBeforeCarousels();const current=labels.filter(([,id])=>sections.get(id).style.display!=='none'&&sections.get(id).offsetTop<=sc.scrollTop+180).sort((a,b)=>sections.get(b[1]).offsetTop-sections.get(a[1]).offsetTop)[0];document.querySelectorAll('nav button').forEach(b=>b.classList.toggle('active',b.dataset.id===current?.[1]));};
update();
