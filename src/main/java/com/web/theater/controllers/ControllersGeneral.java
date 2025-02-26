package com.web.theater.controllers;

import com.web.theater.Service;
import com.web.theater.operations.OperationsAdmin;
import com.web.theater.operations.OperationsGeneral;
import com.web.theater.structs.*;
import jakarta.servlet.http.HttpSession;
import org.json.JSONException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.List;

//ОБЩИЕ КОНТРОЛЛЕРЫ
@Controller
public class ControllersGeneral {
    OperationsGeneral ClGeneral = new OperationsGeneral();
    OperationsAdmin ClAdmin = new OperationsAdmin();
    String TextInfo;//текст сообщения

    //главная страница
    @RequestMapping(
            value = {"/", "/index"},
            method = {RequestMethod.GET}
    )
    public String indexController(Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 0);
        model.addAttribute("poster", ClGeneral.getPosterIndex());
        model.addAttribute("news", ClGeneral.getNewsIndex());
        return "index";
    }

    //регистрация и авторизация через Google
    @RequestMapping(
            value = {"/reg_sig_google"},
            method = {RequestMethod.GET}
    )
    public String regSigGoogleController(@AuthenticationPrincipal OAuth2User user, Model model, HttpSession session) {
        //System.out.println("USER="+user.getAttribute("name"));
        //System.out.println("EMAIL="+user.getAttribute("email"));
        //System.out.println("IMAGE="+user.getAttribute("photo"));
        String name = user.getAttribute("name");
        String email = user.getAttribute("email");
        String message = ClGeneral.regSignInGoogle(name, email, session);
        model.addAttribute("number_razdel", 0);
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("type_message", 2);
        model.addAttribute("message", "Авторизация пользователя через аккаунт Google прошла успешно.");
        return "info";
    }

    //переход на страницу регистрации
    @RequestMapping(
            value = {"/ShowRegistrationController"},
            method = {RequestMethod.GET}
    )
    public String showRegistrationController(Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 7);
        return "registration";
    }

    //регистрация нового пользователя
    @RequestMapping(
            value = {"/RegistrationUserController"},
            method = {RequestMethod.POST}
    )
    public String registrationUserController(@ModelAttribute User data, Model model, HttpSession session) {
        model.addAttribute("number_razdel", 0);
        String result = ClGeneral.registrationUser(session, data);
        if(result.equals("OK")) {
            TextInfo = "Регистрация нового пользователя прошла успешно.";
            model.addAttribute("type_message", 2);
        }else{
            TextInfo = result;
            model.addAttribute("type_message", 1);
        }
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("message", TextInfo);
        return "info";
    }

    //переход на страницу авторизации пользователя
    @RequestMapping(
            value = {"/ShowSignInUserController"},
            method = {RequestMethod.GET}
    )
    public String showSignInUserController(Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 8);
        return "sign_in";
    }

    //авторизация пользователя
    @RequestMapping(
            value = {"/SignInUserController"},
            method = {RequestMethod.POST}
    )
    public String signInUserController(@ModelAttribute User data, Model model, HttpSession session) {
        model.addAttribute("number_razdel", 0);
        //проверяем если администратор
        if(data.getLogin().equals("admin")){
            if(data.getPassword().equals(Service.decrypt(Service.getSetting("admin_password")))){
                session.setAttribute("UserStatus", "Admin");
                session.setAttribute("IdUser", 0);
                TextInfo = "Авторизация администратора прошла успешно.";
                model.addAttribute("type_message", 2);
            }else TextInfo = "Неправильный логин и/или пароль.";
        }else {//иначе пользователь
            String result = ClGeneral.signInUser(session, data.getLogin(), data.getPassword());
            if (result.equals("OK")) {
                TextInfo = "Авторизация пользователя прошла успешно.";
                model.addAttribute("type_message", 2);
            } else {
                TextInfo = result;
                model.addAttribute("type_message", 1);
            }
        }
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("message", TextInfo);
        return "info";
    }

    //получение аккаунта пользователя
    @RequestMapping(value = { "/GetAvatarUserController" }, method = RequestMethod.POST)
    public ResponseEntity<String> getAvatarUserController(HttpSession session) throws ClassNotFoundException, JSONException {
        String response = "";
        HttpHeaders headers = null;
        if (Service.checkSession(session)){
            response = ClGeneral.getAvatarUser(session);
            headers = new HttpHeaders();
            headers.add("Content-Type", "text/html; charset=utf-8");
        }
        return new ResponseEntity<String>(response, headers, HttpStatus.OK);
    }

    //переход на страницу людей театра
    @RequestMapping(
            value = {"/ShowWorkersController"},
            method = {RequestMethod.GET}
    )
    public String showWorkersController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int id_position,
                                        @RequestParam(defaultValue = "") String name, Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 3);
        model.addAttribute("workers", ClGeneral.getWorkers(page, id_position, name));
        model.addAttribute("list_pages", Service.getListPages(page));
        model.addAttribute("page", page);
        model.addAttribute("id_position", id_position);
        model.addAttribute("name", name);
        model.addAttribute("positions", ClAdmin.getPositions());
        return "workers";
    }

    //переход на страницу подробнее о сотруднике
    @RequestMapping(
            value = {"/AboutWorkerController"},
            method = {RequestMethod.GET}
    )
    public String aboutWorkerController(@RequestParam(defaultValue = "1") int id_worker, Model model, HttpSession session) {
        model.addAttribute("worker", Service.getWorker(id_worker));
        model.addAttribute("ratings", ClGeneral.getRatingWorkerUsers(id_worker));
        return "about_worker";
    }

    //переход на страницу спектаклей
    //view - вид спектакля 0 -все, 1 - взрослый, 2 - детский
    @RequestMapping(
            value = {"/ShowPerformancesController"},
            method = {RequestMethod.GET}
    )
    public String showPerformancesController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int view,
                                        @RequestParam(defaultValue = "") String name, Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 2);
        model.addAttribute("performances", ClGeneral.getPerformances(page, view, name));
        model.addAttribute("list_pages", Service.getListPages(page));
        model.addAttribute("page", page);
        model.addAttribute("view", view);
        model.addAttribute("name", name);
        return "performances";
    }

    //переход на страницу о спектакле, не о постановке в назначенное время
    @RequestMapping(
            value = {"/AboutPerformanceController"},
            method = {RequestMethod.GET}
    )
    public String aboutPerformanceController(@RequestParam(defaultValue = "1") int id_performance, Model model, HttpSession session) {
        model.addAttribute("performance", ClGeneral.getPerformance(id_performance));
        model.addAttribute("ratings", ClGeneral.getRatingPerformanceUsers(id_performance));
        return "about_performance";
    }

    //переход на страницу о постановке в назначенное время
    @RequestMapping(
            value = {"/AboutPosterController"},
            method = {RequestMethod.GET}
    )
    public String aboutPosterController(@RequestParam(defaultValue = "0") int id_poster, Model model, HttpSession session) {
        model.addAttribute("poster", ClGeneral.getPoster(id_poster));
        return "about_poster";
    }

    //переход на страницу о должности в спектакле
    @RequestMapping(
            value = {"/AboutPositionController"},
            method = {RequestMethod.GET}
    )
    public String aboutPositionController(@RequestParam(defaultValue = "0") int id_position, Model model, HttpSession session) {
        model.addAttribute("position", Service.getPosition(id_position));
        return "about_position";
    }

    //переход на страницу афиша
    @RequestMapping(
            value = {"/ShowPosterController"},
            method = {RequestMethod.GET}
    )
    public String showPosterController(@RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
                                       Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 1);
        model.addAttribute("view", view);
        model.addAttribute("name", name);
        List<CalendarPoster> poster = ClGeneral.getPoster(view, name);
        model.addAttribute("poster", poster);
        model.addAttribute("size_poster", poster.size());
        return "poster";
    }

    //переход на страницу покупки билетов
    @RequestMapping(
            value = {"/BuyTicketsController"},
            method = {RequestMethod.GET}
    )
    public String buyTicketsController(@RequestParam(defaultValue = "0") int id_poster, @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
                                       Model model, HttpSession session) throws JSONException {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 0);
        session.setAttribute("ViewPoster", view);
        session.setAttribute("NamePoster", name);
        model.addAttribute("places", ClGeneral.GetPlaces(id_poster));
        int number_rows = Integer.parseInt(Service.getSetting("number_rows"));
        int number_places_row = Integer.parseInt(Service.getSetting("number_places_row"));
        model.addAttribute("number_rows", number_rows);
        model.addAttribute("number_places_row", number_places_row);
        //формируем наименование спектакля, дату и время
        Poster poster = ClGeneral.getPoster(id_poster);
        int id_directory_performance = poster.getId_directory_performance();
        Performance performance = ClGeneral.getPerformance(id_directory_performance);
        String name_poster = performance.getName();
        if(performance.getChildren() == 0) name_poster += " (взрослый)";
        else name_poster += " (детский)";
        model.addAttribute("name", name_poster);
        model.addAttribute("date_time", poster.getDate_time());
        model.addAttribute("id_poster", id_poster);
        return "buy_tickets";
    }

    //получение кратких данных о постановках в назначенный день в json
    @RequestMapping(value = { "/GetDataDayPosterController" }, method = RequestMethod.POST)
    public ResponseEntity<String> getDataDayPosterController(@RequestParam(defaultValue = "") String date_mysql) throws ClassNotFoundException, JSONException {
        String response = "";
        response = ClGeneral.getDataDayPoster(date_mysql);
        HttpHeaders headers = null;
        headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");
        return new ResponseEntity<String>(response, headers, HttpStatus.OK);
    }

    //оплата билетов на постановки
    @RequestMapping(
            value = {"/PayPosterController"},
            method = {RequestMethod.GET}
    )
    public String payPosterController(@RequestParam(defaultValue = "0") int id_poster, @RequestParam(defaultValue = "0") int method_pay, @RequestParam(defaultValue = "") String list_tickets,
                                       Model model, HttpSession session) {
        int id_user = 0;
        if (Service.checkSession(session))
            if(session.getAttribute("UserStatus").toString().equals("User"))
                id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 0);
        String result = ClGeneral.payPoster(id_poster, id_user, method_pay, list_tickets);
        model.addAttribute("result", result);
        return "result_buy_tickets";
    }

    //переход на страницу гостевой книги
    //view - вид спектакля 0 -все, 1 - взрослый, 2 - детский
    @RequestMapping(
            value = {"/ShowGuestbookController"},
            method = {RequestMethod.GET}
    )
    public String showGuestbookController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "false") boolean only_me,
                                          Model model, HttpSession session) {
        int id_user = 0;
        if (Service.checkSession(session))
            if(session.getAttribute("UserStatus").toString().equals("User")) {
                id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
                model.addAttribute("only_me", only_me);
            }
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 6);
        model.addAttribute("guestbook", ClGeneral.getGuestbook(id_user, only_me, page));
        model.addAttribute("page", page);
        model.addAttribute("id_user", id_user);
        model.addAttribute("list_pages", Service.getListPages(page));
        return "guestbook";
    }

    //переход на страницу новостей
    @RequestMapping(
            value = {"/ShowNewsController"},
            method = {RequestMethod.GET}
    )
    public String showNewsController(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 5);
        model.addAttribute("news", ClGeneral.getPageNews(page));
        model.addAttribute("page", page);
        model.addAttribute("list_pages", Service.getListPages(page));
        return "news";
    }

    //переход на страницу подробных данных о новости
    @RequestMapping(
            value = {"/AboutNewsController"},
            method = {RequestMethod.GET}
    )
    public String aboutNewsController(@RequestParam(defaultValue = "0") int id_news, Model model, HttpSession session) {
        model.addAttribute("news", ClGeneral.getNews(id_news));
        return "about_news";
    }

    //распечатать билет
    @RequestMapping(
            value = {"/PrintTicketController"},
            method = {RequestMethod.GET}
    )
    public String printTicketController(@RequestParam(defaultValue = "") String list_id_tickets, Model model, HttpSession session) throws IOException, JSONException {
        if (Service.checkSession(session))
            if(session.getAttribute("UserStatus").toString().equals("Admin")) {
                TextInfo = "Несанкционированный доступ";
                model.addAttribute("type_message", 1);
                model.addAttribute("message", TextInfo);
                model.addAttribute("number_razdel", 0);
                return "info";
            }
        model.addAttribute("tickets", ClGeneral.getPrintTickets(list_id_tickets));
        return "print_tickets";
    }

    //переход на страницу авторизации пользователя
    @RequestMapping(
            value = {"/GalleryController"},
            method = {RequestMethod.GET}
    )
    public String galleryController(Model model, HttpSession session) {
        model.addAttribute("user_status", session.getAttribute("UserStatus"));
        model.addAttribute("number_razdel", 4);
        return "gallery";
    }

    //выход из аккаунта
    @RequestMapping(
            value = {"/SignOutController"},
            method = {RequestMethod.GET}
    )
    public String signOutController(Model model, HttpSession session) {
        TextInfo="Выход из аккаунта выполнен.";
        if(session != null)session.invalidate();
        model.addAttribute("number_razdel", 0);
        model.addAttribute("type_message", 2);
        model.addAttribute("message", TextInfo);
        return "info";
    }
}
