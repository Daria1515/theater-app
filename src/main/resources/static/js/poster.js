//СКРИПТЫ ПО АФИШЕ
var SelectTheaterEl = document.getElementById('theater');
var SelectViewEl = document.getElementById('view');
var NameEl = document.getElementById('name');

//открытие окна выбора постановки какого театра
function ShowModalTheater(){
    OpenModal('modal_theater');
}

//переход на страницу добавления постановки
function Add(){
    var theater = SelectTheaterEl.value;
    var view = SelectViewEl.value;
    var name = NameEl.value;
    window.open("/ShowAddEditPosterController?theater=" + theater + "&view=" + view + "&name=" + name, "_self");
}

//поиск
function Search(){
    var view = SelectViewEl.value;
    var name = NameEl.value;
    window.open("/ShowPosterController?view=" + view + "&name=" + name, "_self");
}

//получение спектаклей в назначенный день
function GetDataDayPoster(date_mysql, id_day){
    var i;
    //устанавливаем выделение в календаре выбранного дня
    for(i=0;i<SizePoster;i++)
        if(i == id_day) document.getElementById('date'+i).className='text_poster_bold';
        else document.getElementById('date'+i).className='text_poster';
    $.ajax({
        type: "POST",
        data:{
            date_mysql: date_mysql
        },
        url: "/GetDataDayPosterController",
        success: function( data ){
            var i, table, div_poster, div_table, cell, row, cell1, row1, img, label, button, data;
            var buttons_buy_tickets = [], buttons_performance = [], buttons_poster = [], buttons_edit = [], buttons_delete = [];
            var data_json = JSON.parse(data);
            document.getElementById('name_day').innerHTML = data_json.date;
            var poster = data_json.poster;
            div_poster = document.getElementById("div_poster");
            div_poster.innerHTML="";
            for(i=0; i<poster.length;i++){
                data = poster[i];
                //создаем div
                div_table = document.createElement('div');
                div_table.className = 'div1';
                //создаем table в div
                table = document.createElement('table');
                table.setAttribute('width', '100%');
                table.setAttribute('cellpadding', '0');
                table.setAttribute('cellspacing', '0');
                row = table.insertRow();
                //изображение
                cell = row.insertCell();
                cell.setAttribute('rowspan', '100');
                cell.setAttribute('width', '130px');
                cell.setAttribute('style','margin-right: 3px');
                img=document.createElement('img');
                img.style.borderRadius = '5px';
                img.style.width = '130px';
                img.style.display = 'block';
                if(data.image.length > 0) img.src = 'data:image/jpeg;base64,' + data.image;
                else img.src = "/images/no_image.gif";
                cell.append(img);
                row = table.insertRow();
                //наименование постановки
                cell = row.insertCell();
                cell.setAttribute('colspan', '2');
                cell.setAttribute('vertical-align', 'top');
                label=document.createElement('label');
                label.className='text3';
                label.innerHTML=data.name;
                label.style.marginLeft = '3px';
                cell.append(label);
                //линия
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                hr=document.createElement('hr');
                hr.style.marginLeft = '3px';
                cell.setAttribute('colspan', '2');
                cell.append(hr);
                //время и от какого возраста
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                label=document.createElement('label');
                label.className='text5';
                label.innerHTML=data.time;
                label.style.marginLeft = '3px';
                cell.append(label);
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                cell.style.textAlign='right';
                label=document.createElement('label');
                label.className='text5';
                label.innerHTML='возраст от ' + data.age + ' лет';
                cell.append(label);
                //линия
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                hr=document.createElement('hr');
                hr.style.marginLeft = '3px';
                cell.setAttribute('colspan', '2');
                cell.append(hr);
                //количество билетов и цена
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                label=document.createElement('label');
                label.className='text5';
                label.innerHTML= 'Билетов: ' + data.free_tickets + ' шт.';
                label.style.marginLeft = '3px';
                cell.append(label);
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                cell.style.textAlign='right';
                label=document.createElement('label');
                label.className='text5';
                label.innerHTML='Цена: ' + data.min_max_ticket;
                cell.append(label);
                //линия
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                hr=document.createElement('hr');
                hr.style.marginLeft = '3px';
                cell.setAttribute('colspan', '2');
                cell.append(hr);
                //какой театр и рейтинг
                if(data.theater == 1){
                    row = table.insertRow();
                    cell = row.insertCell();
                    cell.setAttribute('vertical-align', 'top');
                    label=document.createElement('label');
                    label.className='text5';
                    label.innerHTML= 'Постановка своего театра';
                    label.style.marginLeft = '3px';
                    cell.append(label);
                    cell = row.insertCell();
                    cell.setAttribute('vertical-align', 'top');
                    cell.style.textAlign='right';
                    label=document.createElement('label');
                    label.className='text5';
                    label.innerHTML='Рейтинг: ' + data.rating_text;
                    cell.append(label);
                }//если не свой театр, то рейтинг и какой театр
                else{
                    row = table.insertRow();
                    cell = row.insertCell();
                    cell.setAttribute('vertical-align', 'top');
                    cell.setAttribute('colspan', '2');
                    label=document.createElement('label');
                    label.style.marginLeft = '3px';
                    label.className='text5';
                    label.innerHTML='Рейтинг: ' + data.rating_text;
                    cell.append(label);
                    //линия
                    row = table.insertRow();
                    cell = row.insertCell();
                    cell.setAttribute('vertical-align', 'top');
                    hr=document.createElement('hr');
                    hr.style.marginLeft = '3px';
                    cell.setAttribute('colspan', '2');
                    cell.append(hr);
                    //о театре
                    row = table.insertRow();
                    cell = row.insertCell();
                    cell.setAttribute('vertical-align', 'top');
                    cell.setAttribute('colspan', '2');
                    label=document.createElement('label');
                    label.style.marginLeft = '3px';
                    label.className='text4';
                    label.innerHTML='Постановка театра: ' + data.about_theater;
                    cell.append(label);
                }
                //линия
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                hr=document.createElement('hr');
                hr.style.marginLeft = '3px';
                cell.setAttribute('colspan', '2');
                cell.append(hr);
                //кнопки
                row = table.insertRow();
                cell = row.insertCell();
                cell.setAttribute('vertical-align', 'top');
                cell.setAttribute('colspan', '2');
                cell.style.textAlign='right';
                if(UserStatus == 'Admin'){
                    //кнопка изменить
                    button = document.createElement('button');
                    button.className = 'button3';
                    button.innerText = 'Удалить';
                    buttons_delete.push(button);
                    buttons_delete[i].id_poster=data.id;
                    buttons_delete[i].addEventListener("click",
                        function(){
                            var isDel = confirm('Удалить постановку?');
                            if(isDel){
                                var view = SelectViewEl.value;
                                var name = NameEl.value;
                                window.open("/DeletePosterController?id_poster=" + this.id_poster + "&view=" + view + "&name=" + name, "_self");
                            }
                        }, false);
                    cell.append(button);
                    cell.append(' ');
                    //кнопка изменить
                    button = document.createElement('button');
                    button.className = 'button2';
                    button.innerText = 'Изменить';
                    buttons_edit.push(button);
                    buttons_edit[i].id_poster=data.id;
                    buttons_edit[i].theater = data.theater;
                    buttons_edit[i].addEventListener("click",
                        function(){
                            var view = SelectViewEl.value;
                            var name = NameEl.value;
                            window.open("/ShowAddEditPosterController?id_poster=" + this.id_poster + "&theater=" + this.theater + "&view=" + view + "&name=" + name, "_self");
                        }, false);
                    cell.append(button);
                    cell.append(' ');
                }
                //кнопка о постановке
                button = document.createElement('button');
                button.className = 'button3';
                button.innerText = 'О постановке';
                buttons_poster.push(button);
                buttons_poster[i].id_poster=data.id;
                buttons_poster[i].addEventListener("click",
                    function(){
                        window.open("/AboutPosterController?id_poster=" + this.id_poster, "_blank");
                    }, false);
                cell.append(button);
                cell.append(' ');
                //кнопка о спектакле
                button = document.createElement('button');
                button.className = 'button3';
                button.innerText = 'О спектакле';
                buttons_performance.push(button);
                buttons_performance[i].id_directory_performance=data.id_directory_performance;
                buttons_performance[i].addEventListener("click",
                    function(){
                        window.open("/AboutPerformanceController?id_performance=" + this.id_directory_performance, "_blank");
                    }, false);
                cell.append(button);
                //кнопка покупки билета
                if(UserStatus != 'Admin'){
                    cell.append(' ');
                    button = document.createElement('button');
                    button.className = 'button2';
                    button.innerText = 'Купить билет';
                    buttons_buy_tickets.push(button);
                    buttons_buy_tickets[i].id_poster = data.id;
                    buttons_buy_tickets[i].free_tickets = data.free_tickets;
                    buttons_buy_tickets[i].addEventListener("click",
                        function(){
                            if(this.free_tickets > 0){
                                var view = SelectViewEl.value;
                                var name = NameEl.value;
                                window.open("/BuyTicketsController?id_poster=" + this.id_poster + "&view=" + view + "&name=" + name, "_self");
                            }else alert('Нет свободных билетов.');
                        }, false);
                    cell.append(button);
                }
                div_table.append(table);
                div_poster.appendChild(div_table);
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert('Ошибка: ' + textStatus);
        }
    })
}