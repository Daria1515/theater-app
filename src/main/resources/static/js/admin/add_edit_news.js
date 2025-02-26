//СКРИПТЫ ДОБАВЛЕНИЯ И ИЗМЕНЕНИЯ НОВОСТИ

//событие при окончании загрузки страницы
$(window).on('load', function() {
    document.getElementById('on_change_image1').value='0';
    document.getElementById('on_change_image2').value='0';
    document.getElementById('on_change_image3').value='0';
});
//событие при выборе изображения 1
document.getElementById('image1').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str1').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image1');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 1
function DeleteImage1(){
    var image_input=document.getElementById('image1');
    var image=document.getElementById('image_str1');
    var change=document.getElementById('on_change_image1');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 2
document.getElementById('image2').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str2').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image2');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 2
function DeleteImage2(){
    var image_input=document.getElementById('image2');
    var image=document.getElementById('image_str2');
    var change=document.getElementById('on_change_image2');
    change.value='2';
    image.src='';
    image_input.value='';
}

//событие при выборе изображения 3
document.getElementById('image3').onchange = function (evt) {
    var tgt = evt.target || window.event.srcElement,
    files = tgt.files;
    //если поддерживает загрузку файлов
    if (FileReader && files && files.length) {
        var fr = new FileReader();
        fr.onload = function () {
            document.getElementById('image_str3').src = fr.result;
        }
        fr.readAsDataURL(files[0]);
        var change=document.getElementById('on_change_image3');
        change.value='1';
    }
    //если не поддерживает загрузку файлов
    else {
        alert('Загрузка изображения не поддерживается.');
    }
}

//удаление изображения 3
function DeleteImage3(){
    var image_input=document.getElementById('image3');
    var image=document.getElementById('image_str3');
    var change=document.getElementById('on_change_image3');
    change.value='2';
    image.src='';
    image_input.value='';
}