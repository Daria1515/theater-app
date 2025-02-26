//СКРИПТЫ ПО СТАТИСТИКЕ
var SelectView=document.getElementById("view");
var CheckboxFlagAll=document.getElementById("flag_all");
var DateStart=document.getElementById("date_start");
var DateEnd=document.getElementById("date_end");

//нажата кнопка 'Анализ'
function Analysis(){
    var view=SelectView.value;
    var flag_all;
    if(CheckboxFlagAll.checked)flag_all=true;
    else flag_all=false;
    var date_start=DateStart.value;
    var date_end=DateEnd.value;
    if(flag_all==false){
        if(date_start=="" || date_end==""){
            alert("Введите даты начала и конца.");
            return;
        }
        if(date_end<date_start){
            alert("Дата начала должна быть меньше или равной дате конца.");
            return;
        }
    }
    window.open("/StatisticsController?view="+view+"&flag_all="+flag_all+"&date_start="+date_start+"&date_end="+date_end, "_self");
}

//печать
function Print(elem){
    Popup($(elem).html());
}

function Popup(data){
    var mywindow = window.open('', 'print-div', 'height=600,width=800');
    mywindow.document.write('</head><body >');
    mywindow.document.write(data);
    mywindow.document.write('</body></html>');
    mywindow.document.close(); // necessary for IE >= 10
    mywindow.focus(); // necessary for IE >= 10
    mywindow.print();
    mywindow.close();
    return true;
}