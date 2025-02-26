package com.web.theater.controllers;

import com.web.theater.Service;
import com.web.theater.operations.OperationsAdmin;
import com.web.theater.operations.OperationsGeneral;
import com.web.theater.operations.OperationsUser;
import com.web.theater.structs.*;
import jakarta.servlet.http.HttpSession;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

//КОНТРОЛЛЕРЫ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ
@Controller
public class ControllersUser {
	String TextInfo;//текст сообщения
	OperationsUser ClUser = new OperationsUser();
	OperationsGeneral ClGeneral = new OperationsGeneral();
	OperationsAdmin ClAdmin = new OperationsAdmin();

	//переход на страницу личных данных
	@RequestMapping(
			value = {"/ShowDataUserController"},
			method = {RequestMethod.GET}
	)
	public String showDataUserController(Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")) {
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				model.addAttribute("data", ClUser.getData(id_user));
				model.addAttribute("number_razdel", 9);
				return "user/data";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else{
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//сохранение личных данных пользователя
	@RequestMapping(
			value = {"/SaveDataController"},
			method = {RequestMethod.POST}
	)
	public String saveDataController(@ModelAttribute User data, Model model, HttpSession session) {
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")) {
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				data.setId(id_user);
				String result = ClUser.saveData(data);
				if(result.equals("OK")) return showDataUserController(model, session);
				else{
					model.addAttribute("type_message", 1);
					model.addAttribute("message", result);
					model.addAttribute("number_razdel", 0);
					return "info";
				}
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else{
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу мои билеты
	//параметр view - временной статус - 0 = все, 1 = прошедшие, 2 = текущие и будущие
	@RequestMapping(
			value = {"/ShowMyTicketsController"},
			method = {RequestMethod.GET}
	)
	public String showMyTicketsController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "2") int status,
										  Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")) {
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				model.addAttribute("tickets", ClUser.getTicketsUser(id_user, page, status));
				model.addAttribute("number_razdel", 10);
				model.addAttribute("status", status);
				model.addAttribute("page", page);
				model.addAttribute("list_pages", Service.getListPages(page));
				return "user/my_tickets";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else{
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу установки оценки по постановке
	//oper - 1 = добавить оценку, 2 = изменить оценку
	@RequestMapping(
			value = {"/ShowSetRatingController"},
			method = {RequestMethod.GET}
	)
	public String showSetRatingController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "2") int status,
										  @RequestParam(defaultValue = "1") int oper, @RequestParam(defaultValue = "0") int id_poster,
										  Model model, HttpSession session) throws JSONException {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")) {
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				Poster poster = ClGeneral.getPoster(id_poster);
				model.addAttribute("number_razdel", 0);
				model.addAttribute("theater", poster.getTheater());
				model.addAttribute("id_poster", id_poster);
				session.setAttribute("SetRatingPage", page);
				session.setAttribute("SetRatingStatus", status);
				RatingPoster rating_poster = new RatingPoster();
				if(oper == 1){
					rating_poster.setId_poster(id_poster);
					rating_poster.setTheater(poster.getTheater());
					rating_poster.setList_text_positions(poster.getList_text_positions());
					rating_poster.setList_text_roles(poster.getList_text_roles());
					List<Data2> list_positions = ClGeneral.getListPositionsPoster(poster.getList_text_positions(), poster.getTheater());
					List<Data2> list_roles = ClGeneral.getListRolesPoster(poster.getList_text_roles(), poster.getId_directory_performance(), poster.getTheater());
					rating_poster.setList_positions(list_positions);
					rating_poster.setList_roles(list_roles);
					Performance performance = ClGeneral.getPerformance(poster.getId_directory_performance());
					if(performance.getChildren() == 1) rating_poster.setName_performance(performance.getName() + " (детский)" + " " + poster.getDate_time());
					else rating_poster.setName_performance(performance.getName() + " (взрослый)" + " " + poster.getDate_time());
				}
				else rating_poster = ClGeneral.getRatingPosterUser(id_user, id_poster);
				model.addAttribute("rating_poster", rating_poster);
				model.addAttribute("oper", oper);
				model.addAttribute("size_positions", rating_poster.getList_positions().size());
				model.addAttribute("size_roles", rating_poster.getList_roles().size());
				return "user/set_rating";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else{
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление или изменение оценки по постановке
	@RequestMapping(
			value = {"/AddEditRatingPosterController"},
			method = {RequestMethod.POST}
	)
	public String addEditRatingPosterController(@ModelAttribute RatingPoster data, Model model, HttpSession session) throws IOException {
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")) {
				//проверка на нецензурные слова в обращении
				if(Service.checkWarningWorlds(data.getReview()) == true){
					TextInfo = "Нецензурные слова в обращении.";
					model.addAttribute("type_message", 1);
					model.addAttribute("message", TextInfo);
					return "info";
				}
				if(Service.checkWarningWorlds(data.getList_text_positions()) == true){
					TextInfo = "Нецензурные слова в обращении.";
					model.addAttribute("type_message", 1);
					model.addAttribute("message", TextInfo);
					return "info";
				}
				if(Service.checkWarningWorlds(data.getList_text_roles()) == true){
					TextInfo = "Нецензурные слова в обращении.";
					model.addAttribute("type_message", 1);
					model.addAttribute("message", TextInfo);
					return "info";
				}
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				ClUser.addEditRatingPoster(data, id_user);
				int page = Integer.parseInt(session.getAttribute("SetRatingPage").toString());
				int status = Integer.parseInt(session.getAttribute("SetRatingStatus").toString());
				return showMyTicketsController(page, status, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else{
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления обращения в гостевой книге
	@RequestMapping(
			value = {"/ShowAddGuestbookController"},
			method = {RequestMethod.GET}
	)
	public String showAddGuestbookController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "false") boolean only_me,
											 Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		model.addAttribute("number_razdel", 0);
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("Admin")) {
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				return "info";
			}
		}
		session.setAttribute("GuestbookPage", page);
		session.setAttribute("GuestbookOnlyMe", only_me);
		return "add_guestbook";
	}

	//добавление обращения в гостевой книге
	@RequestMapping(
			value = {"/AddGuestbookController"},
			method = {RequestMethod.POST}
	)
	public String addGuestbookController(@ModelAttribute Guestbook data, Model model, HttpSession session) throws IOException {
		int id_user = 0;
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("Admin")) {
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				return "info";
			}
			id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
		}
		//проверка на нецензурные слова в обращении
		if(Service.checkWarningWorlds(data.getTitle()) == true){
			TextInfo = "Нецензурные слова в обращении.";
			model.addAttribute("type_message", 1);
			model.addAttribute("message", TextInfo);
			return "info";
		}
		if(Service.checkWarningWorlds(data.getQuestion()) == true){
			TextInfo = "Нецензурные слова в обращении.";
			model.addAttribute("type_message", 1);
			model.addAttribute("message", TextInfo);
			return "info";
		}
		ClUser.addGuestbook(id_user, data.getTitle(), data.getQuestion());
		int page = Integer.parseInt(session.getAttribute("GuestbookPage").toString());
		boolean only_me = Boolean.parseBoolean(session.getAttribute("GuestbookOnlyMe").toString());
		ControllersGeneral cl = new ControllersGeneral();
		return cl.showGuestbookController(page, only_me, model, session);
	}

	//удаление обращения
	@RequestMapping(
			value = {"/DeleteGuestbookController"},
			method = {RequestMethod.GET}
	)
	public String deleteGuestbookController(@RequestParam(defaultValue = "0") int id_guestbook, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "false") boolean only_me,
											Model model, HttpSession session) {
		if (Service.checkSession(session)) {
			if(session.getAttribute("UserStatus").toString().equals("User")){
				int id_user = Integer.parseInt(session.getAttribute("IdUser").toString());
				Guestbook guestbook = ClAdmin.getGuestbook(id_guestbook);
				if(id_user != guestbook.getId_user()){
					TextInfo = "Несанкционированный доступ";
					model.addAttribute("type_message", 1);
					model.addAttribute("message", TextInfo);
					return "info";
				}
			}
			int all_pages = ClAdmin.deleteGuestbook(id_guestbook);
			if(page > 1)
				if(page > all_pages) page = all_pages;
		}
		ControllersGeneral cl = new ControllersGeneral();
		return cl.showGuestbookController(page, only_me, model, session);
	}
}
