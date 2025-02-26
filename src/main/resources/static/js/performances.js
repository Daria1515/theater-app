//СКРИПТЫ ПО СПЕКТАКЛЯМ
var SelectViewEl = document.getElementById("view");
var EditNameEl = document.getElementById("name");

//переход на страницу добавления спектакля
function Add(children, name){
    var view = SelectViewEl.value;
    var name = EditNameEl.value;
    window.open("/ShowAddEditPerformanceController?page=" + Page + "&view=" + view + "&name=" + name, "_self");
}

//переход на страницу редактирования спектакля
function Edit(id_performance){
    var view = SelectViewEl.value;
    var name = EditNameEl.value;
    window.open("/ShowAddEditPerformanceController?page=" + Page + "&id_performance=" + id_performance + "&view=" + view + "&name=" + name, "_self");
}

//удаление спектакля
function Delete(id_performance){
    var isDel = confirm("Удалить спектакль? После удаления удалятся все данные связанные со спектаклем.");
    if(isDel){
        var view = SelectViewEl.value;
        var name = EditNameEl.value;
        window.open("/DeletePerformanceController?page=" + Page + "&id_performance=" + id_performance + "&view=" + view + "&name=" + name, "_self");
    }
}

//нажата кнопка поиска
function Search(){
    var view = SelectViewEl.value;
    var name = EditNameEl.value;
    window.open("/ShowPerformancesController?view=" + view + "&name=" + name, "_self");
}

//переход по страницам
function GoPage(page){
    var view = SelectViewEl.value;
    var name = EditNameEl.value;
    window.open("/ShowPerformancesController?page=" + page + "&view=" + view + "&name=" + name, "_self");
}

//переход на страницу подробных данных о спектакле
function About(id_performance){
    window.open("/AboutPerformanceController?id_performance=" + id_performance, "_blank");
}