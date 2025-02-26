//СКРИПТЫ ПО ЛИЧНЫМ ДАННЫМ ПОЛЬЗОВАТЕЛЯ
//событие при окончании загрузки страницы
$(window).on('load', function() {
    document.getElementById('on_change_image').value='0';
});

//событие при выборе изображения
document.getElementById('image').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения
function DeleteImage(){
    var image_input=document.getElementById('image');
    var image=document.getElementById('image_str');
    var change=document.getElementById('on_change_image');
    change.value='2';
    image.src='';
    image_input.value='';
}