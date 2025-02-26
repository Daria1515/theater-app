//СКРИПТЫ ПО ОТЗЫВАМ У АДМИНИСТРАТОРА
var SelectView = document.getElementById('view');
//событие при изменении в списке статуса
SelectView.addEventListener('change',function(){
    var view = SelectView.value;
    window.open("/ShowRatingsController?view=" + view, "_self");
});

//переход на выбранную страницу
function GoPage(page){
    window.open("/ShowRatingsController?page=" + page + "&view=" + View, "_self");
}

//удаление
function Delete(id_rating){
    var isDel = confirm('Удалить оценку?');
    if(isDel) window.open("/DeleteRatingController?id_rating=" + id_rating + "&view=" + View + "&page=" + Page, "_self");
}