//СКРИПТЫ ПО УСТАНОВКЕ ОЦЕНКИ ДЛЯ ПРОСМОТРЕННЫХ ПОСТАНОВОК
//событие перед отправкой данных на сервер из формы
$('#form_data').submit(function() {
    var i, data, data_add, rating, review, list_positions = [], list_roles = [];
    if(Theater == 1){
        for(i=0;i<SizePositions;i++){
            data = ListPositions[i];
            rating = Number(document.getElementById('rating_position' + i).value);
            review = document.getElementById('review_position' + i).value;
            data_add = {'id_worker': data.id_person, 'id_poster': IdPoster, 'rating': rating, 'review': review}
            list_positions.push(data_add);
        }
        for(i=0;i<SizeRoles;i++){
            data = ListRoles[i];
            rating = Number(document.getElementById('rating_role' + i).value);
            review = document.getElementById('review_role' + i).value;
            data_add = {'id_worker': data.id_person, 'id_poster': IdPoster, 'rating': rating, 'review': review}
            list_roles.push(data_add);
        }
    }
    document.getElementById('list_text_positions').value=JSON.stringify(list_positions);
    document.getElementById('list_text_roles').value=JSON.stringify(list_roles);
    return true;
});

//переход на страницу о сотруднике театра
function ShowAboutWorkerTheater(id_worker){
    window.open("/AboutWorkerController?id_worker=" + id_worker, "_blank");
}

//переход на страницу о человеке по медиа ссылке
function ShowAboutSocialLink(link){
    window.open(link, "_blank");
}