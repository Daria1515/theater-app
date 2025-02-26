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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;

//КОНТРОЛЛЕРЫ АДМИНИСТРАТОРА
@Controller
public class ControllersAdmin {
	OperationsAdmin ClAdmin = new OperationsAdmin();
	ControllersGeneral ConGeneral = new ControllersGeneral();
	OperationsGeneral ClGeneral = new OperationsGeneral();
	String TextInfo;//текст сообщения

	//переход на страницу должностей
	@RequestMapping(
			value = {"/ShowPositionsController"},
			method = {RequestMethod.GET}
	)
	public String showPositionsController(Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 12);
				model.addAttribute("positions", ClAdmin.getPositions());
				return "admin/positions";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу пользователей
	@RequestMapping(
			value = {"/ShowUsersController"},
			method = {RequestMethod.GET}
	)
	public String showUsersController(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 15);
				model.addAttribute("users", ClAdmin.getUsers(page));
				model.addAttribute("list_pages", Service.getListPages(page));
				model.addAttribute("page", page);
				return "admin/users";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление пользователя из системы
	@RequestMapping(
			value = {"/DeleteUserController"},
			method = {RequestMethod.GET}
	)
	public String deleteUserController(@RequestParam(defaultValue = "0") int id_user, @RequestParam(defaultValue = "1") int page,
									   Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				int all_pages = ClAdmin.deleteUser(id_user);
				if(page > 1)
					if(page > all_pages) page = all_pages;
				return showUsersController(page, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу отзывов пользователей
	//view = 1 - отзывы по постановкам, 2 - отзывы по актерам
	@RequestMapping(
			value = {"/ShowRatingsController"},
			method = {RequestMethod.GET}
	)
	public String showRatingsController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "1") int view,
										Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 16);
				model.addAttribute("ratings", ClAdmin.getRatings(view, page));
				model.addAttribute("list_pages", Service.getListPages(page));
				model.addAttribute("page", page);
				model.addAttribute("view", view);
				return "admin/ratings";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и редактирования должности
	@RequestMapping(
			value = {"/ShowAddEditPositionController"},
			method = {RequestMethod.GET}
	)
	public String showAddEditPositionController(@RequestParam(defaultValue = "0") int id_position, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 0);
				Position data = new Position();
				if(id_position > 0) data = Service.getPosition(id_position);
				model.addAttribute("data", data);
				return "admin/add_edit_position";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление или изменение данных по должности
	@RequestMapping(
			value = {"/AddEditPositionController"},
			method = {RequestMethod.POST}
	)
	public String addEditPositionController(@ModelAttribute Position data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.addEditPosition(data);
				return showPositionsController(model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление должности
	@RequestMapping(
			value = {"/DeletePositionController"},
			method = {RequestMethod.GET}
	)
	public String deletePositionController(@RequestParam(defaultValue = "0") int id_position, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.deletePosition(id_position);
				return showPositionsController(model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и редактирования сотрудника
	@RequestMapping(
			value = {"/ShowAddEditWorkerController"},
			method = {RequestMethod.GET}
	)
	public String showAddEditWorkerController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int id_worker,
											  @RequestParam(defaultValue = "0") int id_position, @RequestParam(defaultValue = "") String name,
											  Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 0);
				Worker data = new Worker();
				if(id_worker > 0) data = Service.getWorker(id_worker);
				model.addAttribute("data", data);
				model.addAttribute("positions", ClAdmin.getPositions());
				session.setAttribute("PageWorker", page);
				session.setAttribute("IdPositionWorker", id_position);
				session.setAttribute("NameWorker", name);
				return "admin/add_edit_worker";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление или изменение данных по сотруднику
	@RequestMapping(
			value = {"/AddEditWorkerController"},
			method = {RequestMethod.POST}
	)
	public String addEditWorkerController(@ModelAttribute Worker data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.addEditWorker(data);
				int page = Integer.parseInt(session.getAttribute("PageWorker").toString());
				int id_position = Integer.parseInt(session.getAttribute("IdPositionWorker").toString());
				String name = session.getAttribute("NameWorker").toString();
				return ConGeneral.showWorkersController(page, id_position, name,model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление сотрудника
	@RequestMapping(
			value = {"/DeleteWorkerController"},
			method = {RequestMethod.GET}
	)
	public String deleteWorkerController(@RequestParam(defaultValue = "0") int id_worker, @RequestParam(defaultValue = "1") int page,
										 @RequestParam(defaultValue = "0") int id_position, @RequestParam(defaultValue = "") String name,
										 Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				int all_pages = ClAdmin.deleteWorker(id_worker, id_position, name);
				if(page > 1)
					if(page > all_pages) page = all_pages;
				return ConGeneral.showWorkersController(page, id_position, name, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("nessage", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и редактирования спектакля
	@RequestMapping(
			value = {"/ShowAddEditPerformanceController"},
			method = {RequestMethod.GET}
	)
	public String showAddEditPerformanceController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int id_performance,
											  @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
											  Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 0);
				Performance data = new Performance();
				if(id_performance > 0) data = ClGeneral.getPerformance(id_performance);
				model.addAttribute("data", data);
				model.addAttribute("positions", ClAdmin.getPositions());
				session.setAttribute("PagePerformance", page);
				session.setAttribute("ViewPerformance", view);
				session.setAttribute("NamePerformance", name);
				return "admin/add_edit_performance";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление или изменение данных по спектаклю
	@RequestMapping(
			value = {"/AddEditPerformanceController"},
			method = {RequestMethod.POST}
	)
	public String addEditPerformanceController(@ModelAttribute Performance data, Model model, HttpSession session) throws JSONException {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.addEditPerformance(data);
				int page = Integer.parseInt(session.getAttribute("PagePerformance").toString());
				int view = Integer.parseInt(session.getAttribute("ViewPerformance").toString());
				String name = session.getAttribute("NamePerformance").toString();
				return ConGeneral.showPerformancesController(page, view, name, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление данных по спектаклю
	@RequestMapping(
			value = {"/DeletePerformanceController"},
			method = {RequestMethod.GET}
	)
	public String deletePerformanceController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int id_performance,
											  @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
											  Model model, HttpSession session) throws JSONException {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				int all_pages = ClAdmin.deletePerformance(id_performance, view, name);
				if(page > 1)
					if(page > all_pages) page = all_pages;
				return ConGeneral.showPerformancesController(page, view, name, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу настроек
	@RequestMapping(
			value = {"/ShowSettingsController"},
			method = {RequestMethod.GET}
	)
	public String showSettingsController(Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 13);
				model.addAttribute("settings", ClAdmin.getSettings());
				return "admin/settings";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//сохранение настроек
	@RequestMapping(
			value = {"/SaveSettingsController"},
			method = {RequestMethod.POST}
	)
	public String saveSettingsController(@ModelAttribute Settings data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.saveSettings(data);
				return showSettingsController(model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и изменения данных по постановке в афише
	@RequestMapping(
			value = {"/ShowAddEditPosterController"},
			method = {RequestMethod.GET}
	)
	public String showAddEditPosterController(@RequestParam(defaultValue = "0") int id_poster, @RequestParam(defaultValue = "1") int theater,
											  @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
											  Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				model.addAttribute("number_razdel", 0);
				Poster poster = new Poster();
				if(id_poster == 0) poster.setTheater(theater);
				else poster = ClGeneral.getPoster(id_poster);
				model.addAttribute("poster", poster);
				model.addAttribute("performances", ClAdmin.getListPerformances());
				model.addAttribute("workers", ClAdmin.getListPositions());
				session.setAttribute("ViewPoster", view);
				session.setAttribute("NamePoster", name);
				return "admin/add_edit_poster";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//получение данных по должностям и актерам у выбранной постановки
	@RequestMapping(
			value = {"/GetPositionsActorsPosterController"},
			method = {RequestMethod.POST}
	)
	public ResponseEntity<String> getPositionsActorsPosterController(@RequestParam(defaultValue = "0") int id_directory_performance, @RequestParam(defaultValue = "0") int theater,
															   Model model, HttpSession session) {
		String response = "ERROR";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/html; charset=utf-8");
		if (Service.checkSession(session))
			if(session.getAttribute("UserStatus").toString().equals("Admin")) response = ClAdmin.getPositionsActorsPoster(id_directory_performance, theater);
		return new ResponseEntity<String>(response, headers, HttpStatus.OK);
	}

	//добавление или изменение данных по постановке в афише
	@RequestMapping(
			value = {"/AddEditPosterController"},
			method = {RequestMethod.POST}
	)
	public String addEditPosterController(@ModelAttribute Poster data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.addEditPoster(data);
				int view = Integer.parseInt(session.getAttribute("ViewPoster").toString());
				String name = session.getAttribute("NamePoster").toString();
				return ConGeneral.showPosterController(view, name, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление постановки
	@RequestMapping(
			value = {"/DeletePosterController"},
			method = {RequestMethod.GET}
	)
	public String deletePosterController(@RequestParam(defaultValue = "0") int id_poster, @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "") String name,
										 Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.deletePoster(id_poster);
				return ConGeneral.showPosterController(view, name, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и изменения ответа на обращение
	//oper = 1 - дать ответ, 2 - изменить ответ
	@RequestMapping(
			value = {"/ShowAddEditGuestbookController"},
			method = {RequestMethod.GET}
	)
	public String ShowAddEditGuestbookController(@RequestParam(defaultValue = "0") int oper, @RequestParam(defaultValue = "0") int id_guestbook, @RequestParam(defaultValue = "1") int page,
											  Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				session.setAttribute("AddEditGuestbookPage", page);
				model.addAttribute("number_razdel", 0);
				Guestbook guestbook = new Guestbook();
				guestbook = ClAdmin.getGuestbook(id_guestbook);
				guestbook.setOper(oper);
				model.addAttribute("guestbook", guestbook);
				return "admin/add_edit_guestbook";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление и изменение ответа на обращение
	@RequestMapping(
			value = {"/AddEditGuestbookController"},
			method = {RequestMethod.POST}
	)
	public String addEditGuestbookController(@ModelAttribute Guestbook data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.updateAnswerGuestbook(data.getId(), data.getAnswer());
				int page = Integer.parseInt(session.getAttribute("AddEditGuestbookPage").toString());
				ControllersGeneral cl = new ControllersGeneral();
				return cl.showGuestbookController(page, false, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу добавления и изменения новости
	//oper = 1 - дать ответ, 2 - изменить ответ
	@RequestMapping(
			value = {"/ShowAddEditNewsController"},
			method = {RequestMethod.GET}
	)
	public String showAddEditNewsController(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "0") int id_news,
											Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				session.setAttribute("AddEditNewsPage", page);
				model.addAttribute("number_razdel", 0);
				News news = new News();
				if(id_news > 0) news = ClGeneral.getNews(id_news);
				model.addAttribute("news", news);
				return "admin/add_edit_news";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//добавление и изменение новости
	@RequestMapping(
			value = {"/AddEditNewsController"},
			method = {RequestMethod.POST}
	)
	public String addEditNewsController(@ModelAttribute News data, Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				ClAdmin.addEditNews(data);
				int page = Integer.parseInt(session.getAttribute("AddEditNewsPage").toString());
				ControllersGeneral cl = new ControllersGeneral();
				return cl.showNewsController(page, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление новости
	@RequestMapping(
			value = {"/DeleteNewsController"},
			method = {RequestMethod.GET}
	)
	public String deleteNewsController(@RequestParam(defaultValue = "0") int id_news, @RequestParam(defaultValue = "1") int page,
										 Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				int all_pages = ClAdmin.deleteNews(id_news);
				if(page > 1)
					if(page > all_pages) page = all_pages;
				return ConGeneral.showNewsController(page, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//переход на страницу статистики
	@RequestMapping(
			value = {"/StatisticsController"},
			method = {RequestMethod.GET}
	)
	public String statisticsController(@RequestParam(defaultValue = "1") int view, @RequestParam(defaultValue = "true") boolean flag_all,
									   @RequestParam(defaultValue = "") String date_start, @RequestParam(defaultValue = "") String date_end,
									   Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				String text_view = "";
				model.addAttribute("number_razdel", 14);
				model.addAttribute("view", view);
				model.addAttribute("flag_all", flag_all);
				model.addAttribute("date_start", date_start);
				model.addAttribute("date_end", date_end);
				switch (view){
					case 1:
						text_view="По количеству покупаемых билетов на спектакли";
						break;
					case 2:
						text_view="По оценочному рейтингу на спектакли";
						break;
					case 3:
						text_view="По оценочному рейтингу актеров";
						break;
				}
				if(flag_all)text_view+=" за весь период";
				else text_view+=" с "+Service.convertDateMySQLToDateString(date_start)+" по "+Service.convertDateMySQLToDateString(date_end);
				model.addAttribute("text_view", text_view);
				List<Statistics> statistics = new ArrayList<>();
				switch (view){
					case 1:
						statistics = ClAdmin.getStatistics1(flag_all, date_start, date_end);
						break;
					case 2:
						statistics = ClAdmin.getStatistics2(flag_all, date_start, date_end);
						break;
					case 3:
						statistics = ClAdmin.getStatistics3(flag_all, date_start, date_end);
						break;
				}
				model.addAttribute("statistics", statistics);
				return "admin/statistics";
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}

	//удаление оценки
	@RequestMapping(
			value = {"/DeleteRatingController"},
			method = {RequestMethod.GET}
	)
	public String deleteRatingController(@RequestParam(defaultValue = "1") int id_rating, @RequestParam(defaultValue = "0") int view, @RequestParam(defaultValue = "1") int page,
									   Model model, HttpSession session) {
		model.addAttribute("user_status", session.getAttribute("UserStatus"));
		if (Service.checkSession(session)){
			if(session.getAttribute("UserStatus").toString().equals("Admin")){
				int all_pages = ClAdmin.deleteRating(view, id_rating);
				if(page > 1)
					if(page > all_pages) page = all_pages;
				return showRatingsController(page, view, model, session);
			}else{
				TextInfo = "Несанкционированный доступ";
				model.addAttribute("type_message", 1);
				model.addAttribute("message", TextInfo);
				model.addAttribute("number_razdel", 0);
				return "info";
			}
		}else {
			TextInfo="Закончилось время сессии.";
			model.addAttribute("type_message", 2);
			model.addAttribute("message", TextInfo);
			model.addAttribute("number_razdel", 0);
			return "info";
		}
	}
}
