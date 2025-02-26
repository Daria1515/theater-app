//СКРИПТЫ ПО РЕЗУЛЬТАТАМ ПОКУПКИ БИЛЕТОВ
var DataResult, ListIdTickets = [];

//событие при окончании загрузки страницы
$(window).on('load', function() {
    DataResult = JSON.parse(Result);
    document.getElementById('message').innerHTML = DataResult.message;
    ListIdTickets = DataResult.list_id;
});

//распечатка билетов
function PrintTickets(){
    window.open("/PrintTicketController?list_id_tickets=" + encodeURI(JSON.stringify(ListIdTickets)), "_blank");
}