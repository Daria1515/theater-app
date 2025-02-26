//СКРИПТЫ ШАПКИ
//событие при окончании загрузки формы
$(window).on('load', function() {
    SelectOptionMenu();
    GetAvatarUser();
});

//выделение выбранного раздела сайта
function SelectOptionMenu(){
    var element;
    if(NumberRazdel==1){//афиша
        element=document.getElementById('poster');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==2){//спектакли
        element=document.getElementById('performances');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==3){//люди театра
        element=document.getElementById('workers');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==4){//галерея
        element=document.getElementById('gallery');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==5){//новости
        element=document.getElementById('news');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==6){//гостевая
        element=document.getElementById('guestbook');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==7){//регистрация
        element=document.getElementById('registration');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==8){//авторизация
        element=document.getElementById('sign_in');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==9){//личные данные пользователя
        element=document.getElementById('data');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==10){//мои билеты (пользователь)
        element=document.getElementById('my_tickets');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==12){//должности (администратор)
        element=document.getElementById('positions');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==13){//настройки (администратор)
        element=document.getElementById('settings');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==14){//статистика (администратор)
        element=document.getElementById('statistics');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==15){//пользователи (администратор)
        element=document.getElementById('users');
        element.style.color = '#ea3d4e';
    }
    if(NumberRazdel==16){//отзывы (администратор)
        element=document.getElementById('ratings');
        element.style.color = '#ea3d4e';
    }
}

//переход по разделам посетителя
function GoRazdel(num){
    switch(num){
        case 1://афиша
            window.open("/ShowPosterController", "_self");
            break;
        case 2://спектакли
            window.open("/ShowPerformancesController", "_self");
            break;
        case 3://люди театра
            window.open("/ShowWorkersController", "_self");
            break;
        case 4://галерея
            window.open("/GalleryController", "_self");
            break;
        case 5://новости
            window.open("/ShowNewsController", "_self");
            break;
        case 6://гостевая
            window.open("/ShowGuestbookController", "_self");
            break;
        case 7://регистрация
            window.open("/ShowRegistrationController", "_self");
            break;
        case 8://авторизация
            window.open("/ShowSignInUserController", "_self");
            break;
        case 9://личные данные пользователя
            window.open("/ShowDataUserController", "_self");
            break;
        case 10://мои билеты (пользователь)
            window.open("/ShowMyTicketsController", "_self");
            break;
        case 11://выход из аккаунта
            window.open("/SignOutController", "_self");
            break;
        case 12://должности (администратор)
            window.open("/ShowPositionsController", "_self");
            break;
        case 13://настройки (администратор)
            window.open("/ShowSettingsController", "_self");
            break;
        case 14://статистика (администратор)
            window.open("/StatisticsController", "_self");
            break;
        case 15://пользователи (администратор)
            window.open("/ShowUsersController", "_self");
            break;
        case 16://отзывы (администратор)
            window.open("/ShowRatingsController", "_self");
            break;
    }
}

//получение аватара пользователя
function GetAvatarUser(){
    $.ajax({
        type: "POST",
        url: "/GetAvatarUserController",
        success: function( data ){
            var img, status;
            img = document.getElementById('img_avatar');
            status = document.getElementById('status');
            if(data.length > 0){
                var data_json=JSON.parse(data);
                if(data_json.flag_src_image)img.src=data_json.src_image;
                else img.src="data:image/gif;base64," + data_json.image;
                img.title=data_json.hint;
                status.innerHTML=data_json.role;
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            alert('Ошибка: ' + textStatus);
        }
    })
}

//открытие модального окна
function OpenModal(name_modal){
	darkLayer = document.createElement('div'); // слой затемнения
    darkLayer.id = 'shadow'; // id чтобы подхватить стиль
    document.body.appendChild(darkLayer); // включаем затемнение
	modalWin = document.getElementById(name_modal); // находим наше "окно"
	modalWin.style.display = 'block'; // "включаем" его
}

//закрытие модального окна
function CloseModal(){
    darkLayer.parentNode.removeChild(darkLayer); // удаляем затемнение
    modalWin.style.display = 'none'; // делаем окно невидимым
}