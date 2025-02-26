//СКРИПТЫ ПО ПОКУПКЕ БИЛЕТА
var ListPlaces = [];//общий список мест в зале
var ListBuyPlaces = [];//список забронированных мест
var DivPlaces = [];//массив мест при событии выбора
var ButtonBuyEl = document.getElementById('button_buy');//кнопка купить билеты
var MethodPay = 0;//метод оплаты - 1 банковская карта, 2 ЮMoney
var NumberCardEl = document.getElementById('number_card');
var CodeCVCEl = document.getElementById('cvc_card');
var NumberYMoneyEl = document.getElementById('number_ymoney');
var SelectDateMonthCardEl = document.getElementById('date_month_card');
var SelectDateYearCardEl = document.getElementById('date_year_card');

//событие при окончании загрузки страницы
$(window).on('load', function() {
    ListPlaces = JSON.parse(ListTextPlaces);
    RepaintPlaces();
});


//перерисовка зала
function RepaintPlaces(){
    var i, j, table, row, cell, label, data_place, div_place, count_places = 0, text;
    var div_places = document.getElementById('div_places');
    div_places.innerHTML = "";
    table = document.createElement('table');
    table.setAttribute('cellpadding', '1');
    table.setAttribute('cellspacing', '1');
    table.setAttribute('table-layout', 'fixed');
    for(i=0;i<NumberRows;i++){
        row = table.insertRow();
        //номер ряда
        cell = row.insertCell();
        cell.setAttribute('vertical-align', 'center');
        cell.setAttribute('width', '30');
        label=document.createElement('label');
        label.className='text3';
        label.innerHTML= i + 1;
        cell.append(label);
        for(j=0;j<NumberPlacesRow;j++){
            //место
            data_place = ListPlaces[count_places];
            cell = row.insertCell();
            cell.setAttribute('width', '30');
            cell.setAttribute('align', 'center');
            div_place = document.createElement('div');
            label=document.createElement('label');
            label.className='text7';
            label.innerHTML = j + 1;
            label.style.cursor = 'pointer';
            div_place.append(label);
            text = 'Ряд: ' + data_place.row + '\n' + 'Место: ' + data_place.place + '\n';
            if(data_place.free) text += 'Стоимость: ' + data_place.price + ' руб.';
            else text += 'Забронировано';
            div_place.setAttribute('title', text);
            div_place.setAttribute('id', 'place' + count_places);
            if(data_place.free){//если место свободно
                if(data_place.buy == false) div_place.className = 'div_free_place';
                else div_place.className = 'div_free_place_buy';
            }else{//если место не свободно
                div_place.className = 'div_not_free_place';
            }
            DivPlaces.push(div_place);
            DivPlaces[count_places].position = count_places;
            DivPlaces[count_places].addEventListener("click",
                function(){
                    var i, data, div_place;
                    ListPlaces[this.position].buy = !ListPlaces[this.position].buy;
                    data = ListPlaces[this.position];
                    if(data.free){
                        div_place = document.getElementById('place' + this.position);
                        if(data.buy == false){//отмена покупки билета
                            div_place.className = 'div_free_place';
                            var data_buy;
                            for(i=0;i<ListBuyPlaces.length;i++){
                                data_buy = ListBuyPlaces[i];
                                if(data_buy.row == data.row && data_buy.place == data.place){
                                    ListBuyPlaces.splice(i, 1);
                                    break;
                                }
                            }
                        }
                        else{//покупка билета
                            div_place.className = 'div_free_place_buy';
                            ListBuyPlaces.push(data);
                        }
                        if(ListBuyPlaces.length > 0 ) ButtonBuyEl.style.display = 'block';
                        else ButtonBuyEl.style.display = 'none';
                        RepaintTableBuyTickets();
                    }
                }, false);
            cell.append(div_place);
            count_places++;
        }
    }
    div_places.appendChild(table);
}



//формируем таблицу купленных билетов
function RepaintTableBuyTickets(){
    var i, table, row, cell, data, sum = 0, text;
    var div = document.getElementById('div_buy_tickets');
    div.innerHTML = '';
    table = document.createElement('table');
    table.setAttribute('cellpadding', '0');
    table.setAttribute('cellspacing', '0');
    for(i=0;i<ListBuyPlaces.length;i++){
        data = ListBuyPlaces[i];
        row = table.insertRow();
        cell = row.insertCell();
        cell.className = 'text5';
        text = 'ряд: ' + data.row + ' место: ' + data.place + ' цена: ' + data.price + ' руб.'
        cell.append(text);
        sum += data.price;
    }
    row = table.insertRow();
    cell = row.insertCell();
    cell.className = 'text5';
    cell.innerHTML = 'Итого: ' + sum + ' руб.';
    div.appendChild(table);
}

//открытие модального окна способа оплаты
function OpenSelectMethodPay(){
    MethodPay = 0;
    var method_pay1, method_pay2;
    method_pay1 = document.getElementById('method_pay1');
    method_pay2 = document.getElementById('method_pay2');
    method_pay1.className = 'text8';
    method_pay2.className = 'text8';
    OpenModal('modal_select_pay');
}

//выделение способа оплаты в модальном окне
function SelectMethodPay(method){
    MethodPay = method;
    var method_pay1, method_pay2;
    method_pay1 = document.getElementById('method_pay1');
    method_pay2 = document.getElementById('method_pay2');
    switch(method){
        case 1:
            method_pay1.className = 'text9';
            method_pay2.className = 'text8';
            break;
        case 2:
            method_pay1.className = 'text8';
            method_pay2.className = 'text9';
            break;
    }
}

//открытие модального окна ввода платежных данных
function OpenDataPay(){
    if(MethodPay == 0){
        alert('Укажите способ оплаты.');
        return;
    }
    CloseModal();
    switch(MethodPay){
        case 1:
            OpenModal('modal_data_card');
            break;
        case 2:
            OpenModal('modal_data_ymoney');
            break;
    }
}

//проверка на корректность ввода платежных данных
function CorrectDataPay(){
    switch(MethodPay){
        case 1:
            var number_card = NumberCardEl.value;
            var cvc_card = CodeCVCEl.value;
            number_card = number_card.trim(number_card);
            if(number_card.length != 16 && number_card.length != 19){
                alert('Номер карты должен быть 16-ти или 19-ти значным числом.');
                return false;
            }
            number_card = Number(number_card);
            if(number_card % 1 !== 0){
                alert('Номер карты должен быть целым числом.');
                return false;
            }
            if(cvc_card.length != 3){
                alert('Код cvc должен быть 3-ёх значным числом.');
                return false;
            }
            cvc_card = Number(cvc_card);
            if(cvc_card % 1 !== 0){
                alert('Код cvc должен быть целым числом.');
                return false;
            }
            break;
        case 2:
            var number_ymoney = NumberYMoneyEl.value;
            if(number_ymoney.length != 10){
                alert('Номер кошелька должен быть 10-ти значным числом.');
                return false;
            }
            number_ymoney = Number(number_ymoney);
            if(number_ymoney % 1 !== 0){
                alert('Номер кошелька должен быть целым числом.');
                return false;
            }
            break;
    }
    return true;
}

//оплата
function Pay(){
    if(CorrectDataPay()){
        CloseModal();
        OpenModal('modal_wait');
        var list_tickets = JSON.stringify(ListBuyPlaces);
        window.open("/PayPosterController?id_poster=" + IdPoster + "&list_tickets=" + encodeURI(list_tickets) + "&method_pay=" + MethodPay, "_self");
    }
}