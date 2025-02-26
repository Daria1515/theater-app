package com.web.theater.operations;

import com.aspose.barcode.EncodeTypes;
import com.aspose.barcode.generation.BarcodeGenerator;
import com.google.gson.Gson;
import com.web.theater.Service;
import com.web.theater.structs.*;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.bind.DatatypeConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

//КЛАСС ПО ОБЩИМ ОПЕРАЦИЯМ ПОЛЬЗОВАТЕЛЕЙ
public class OperationsGeneral {
	//регистрация и авторизация через Google
	public String regSignInGoogle(String name, String email, HttpSession session){
		String result = "";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id_user = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id FROM users WHERE email_google=?");
			stmt.setString(1, email);
			rs = stmt.executeQuery();
			if(rs.next()) id_user = rs.getInt("id");
			rs.close();stmt.close();
			//регистрация нового пользователя
			if(id_user == 0){
				stmt = conn.prepareStatement("INSERT INTO users (name,date_birth,email,email_google)VALUES(?,?,?,?)");
				if(!name.isEmpty())stmt.setString(1, name);
				else stmt.setString(1, "User");
				stmt.setString(2, "2000-01-01");
				stmt.setString(3, email);
				stmt.setString(4, email);
				stmt.execute();stmt.close();
				//получение макс. id пользователя
				stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM users");
				rs = stmt.executeQuery();
				if(rs.next()) id_user = rs.getInt("max_id");
				rs.close();stmt.close();
			}
			session.setAttribute("UserStatus", "User");
			session.setAttribute("IdUser", id_user);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//регистрация нового пользователя
	public String registrationUser(HttpSession session, User data){
		String result = "";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if(data.getPassword().equals("admin")){
			result = "Логин 'admin' зарезервирован.";
			return result;
		}
		if(data.getName().length() < 10){
			result = "ФИО должно состоять из 10 и более символов.";
			return result;
		}
		if(data.getEmail().length() < 5){
			result = "Электронная почта должна состоять из 5 и более символов.";
			return result;
		}
		if(Service.getAge(data.getDate_birth()) < 5){
			result = "Возраст должен быть от 5 и более лет.";
			return result;
		}
		if(data.getLogin().length() < 5){
			result = "Логин должен состоять из 5 и более символов.";
			return result;
		}
		if(data.getPassword().length() < 5){
			result = "Пароль должен состоять из 5 и более символов.";
			return result;
		}
		if(!data.getPassword().equals(data.getRepeat_password())){
			result = "Пароли не совпадают.";
			return result;
		}
		if(Service.getIdUserLogin(data.getLogin()) > 0){
			result = "Логин " + data.getLogin() + " уже присутствует в системе.";
			return result;
		}
		//регистрация нового пользователя
		int id_user = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("INSERT INTO users (name,date_birth,email,login,password)VALUES(?,?,?,?,?)");
			stmt.setString(1, data.getName());
			stmt.setDate(2, data.getDate_birth());
			stmt.setString(3, data.getEmail());
			stmt.setString(4, data.getLogin());
			stmt.setString(5, Service.encrypt(data.getPassword()));
			stmt.execute();
			stmt.close();
			//получение макс. id пользователя
			stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM users");
			rs = stmt.executeQuery();
			if(rs.next())id_user = rs.getInt("max_id");
			rs.close();
			stmt.close();
			session.setAttribute("UserStatus", "User");
			session.setAttribute("IdUser", id_user);
			result = "OK";
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//авторизация пользователя
	public String signInUser(HttpSession session, String login, String password){
		String result = "OK";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		if(login.length() < 5){
			result = "Логин должен состоять из 5 и более символов.";
			return result;
		}
		if(password.length() < 5){
			result = "Пароль должен состоять из 5 и более символов.";
			return result;
		}
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,password FROM users WHERE login=?");
			stmt.setString(1, login);
			rs = stmt.executeQuery();
			if(rs.next()){
				if(password.equals(Service.decrypt(rs.getString("password")))){
					session.setAttribute("UserStatus", "User");
					session.setAttribute("IdUser", rs.getInt("id"));
				}else result = "Не правильный логин и/или пароль.";
			}else result = "Не правильный логин и/или пароль.";
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение аватара и роли пользователя
	public String getAvatarUser(HttpSession session) throws JSONException {
		String result = "", image_str = "";
		JSONObject obj = new JSONObject();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		switch (session.getAttribute("UserStatus").toString()){
			case "Admin":
				obj.put("flag_src_image", true);
				obj.put("src_image", "images/admin_icon.gif");
				obj.put("hint", "Администратор");
				obj.put("role", "АДМИНИСТРАТОР");
				break;
			case "User":
				obj.put("role", "ПОЛЬЗОВАТЕЛЬ");
				try {
					conn = Service.getConnection();
					//проверяем в пациентах
					stmt = conn.prepareStatement("SELECT name,image FROM users WHERE id=?");
					stmt.setInt(1, Integer.parseInt(session.getAttribute("IdUser").toString()));
					rs = stmt.executeQuery();
					if(rs.next()){
						obj.put("hint", rs.getString("name"));
						image_str = Service.convertBlobToString64(rs.getBlob("image"));
						if(image_str != null){
							obj.put("flag_src_image", false);
							obj.put("image", image_str);
						}else{
							obj.put("flag_src_image", true);
							obj.put("src_image", "images/user_icon.gif");
						}
					}
					rs.close();stmt.close();
					conn.close();
				} catch (SQLException | ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
		}
		result = obj.toString();
		return result;
	}

	//получение данных по работникам театра
	public List<Worker> getWorkers(int select_page, int id_position, String name){
		List<Worker> result = new ArrayList<>();
		Worker data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			String sql="SELECT id,id_position,name,date_birth,description,social_link FROM workers";
			String sql_count="SELECT COUNT(*) AS count FROM workers";
			if(id_position > 0){
				sql+=" WHERE id_position=" + id_position;
				sql_count+=" WHERE id_position=" + id_position;
			}
			if(!name.isEmpty()){
				if(id_position > 0){
					sql+=" AND name LIKE '%" + name + "%'";
					sql_count+=" AND name LIKE '%" + name + "%'";
				}else{
					sql+=" WHERE name LIKE '%" + name + "%'";
					sql_count+=" WHERE name LIKE '%" + name + "%'";
				}
			}
			sql+=" ORDER BY name";
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new Worker();
					data.setId(rs.getInt("id"));
					data.setId_position(rs.getInt("id_position"));
					data.setName_position(Service.getPosition(data.getId_position()).getName());
					data.setName(rs.getString("name"));
					data.setDate_birth(rs.getDate("date_birth"));
					data.setAge(Service.getAge(data.getDate_birth()));
					String description = rs.getString("description");
					if(description.length() > 400) description = description.substring(0, 400) + "...";
					data.setDescription(description);
					data.setSocial_link(rs.getString("social_link"));
					data.setRating(Service.getRatingWorker(data.getId()));
					data.setRating_text(String.format("%.2f", data.getRating()));
					result.add(data);
				}
				count_unit++;
				if(count_unit == Service.NumberUnitsPage){
					count_unit = 0;
					count_pages++;
					if(count_pages > select_page) break;
				}
			}
			rs.close();stmt.close();
			//получаем изображения
			for(int i=0;i<result.size();i++){
				data = result.get(i);
				stmt = conn.prepareStatement("SELECT image FROM workers WHERE id=?");
				stmt.setInt(1, data.getId());
				rs = stmt.executeQuery();
				if(rs.next()) data.setImage_str(Service.convertBlobToString64(rs.getBlob("image")));
				rs.close();stmt.close();
				result.set(i, data);
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по спектаклю, не по постановке в назначенное время
	public Performance getPerformance(int id_performance){
		Performance result = new Performance();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT name,age,description,positions_json,roles_json,children,image1,image2,image3,image4,image5 FROM directory_performances WHERE id=?");
			stmt.setInt(1, id_performance);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_performance);
				result.setName(rs.getString("name"));
				result.setAge(rs.getInt("age"));
				result.setDescription(rs.getString("description"));
				result.setChildren(rs.getInt("children"));
				result.setImage_str1(Service.convertBlobToString64(rs.getBlob("image1")));
				result.setImage_str2(Service.convertBlobToString64(rs.getBlob("image2")));
				result.setImage_str3(Service.convertBlobToString64(rs.getBlob("image3")));
				result.setImage_str4(Service.convertBlobToString64(rs.getBlob("image4")));
				result.setImage_str5(Service.convertBlobToString64(rs.getBlob("image5")));
				result.setList_positions(getListPositionsPerformance(rs.getString("positions_json")));
				result.setList_text_positions(new Gson().toJson(result.getList_positions()));
				result.setList_text_roles(rs.getString("roles_json"));
				result.setList_roles(getListRolesPerformance(rs.getString("roles_json")));
				result.setRating(getRatingPerformance(id_performance));
				result.setRating_text(String.format("%.2f", result.getRating()));
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение списка профессий для спектакля
	//на вход подается строка в json, на выходе список
	public List<Data1> getListPositionsPerformance(String list) throws JSONException {
		List<Data1> result = new ArrayList<>();
		Data1 data;
		JSONArray array = new JSONArray(list);
		for(int i=0;i<array.length();i++){
			data = new Data1();
			data.setId(array.getInt(i));
			data.setName(Service.getPosition(data.getId()).getName());
			result.add(data);
		}
		return result;
	}

	//получение списка ролей для спектакля
	//на вход подается строка в json, на выходе список
	public List<Data1> getListRolesPerformance(String list) throws JSONException {
		List<Data1> result = new ArrayList<>();
		JSONObject data_json;
		Data1 data;
		JSONArray array = new JSONArray(list);
		for(int i=0;i<array.length();i++){
			data = new Data1();
			data_json = (JSONObject) array.get(i);
			data.setId(data_json.getInt("id"));
			data.setName(data_json.getString("name"));
			data.setDescription(data_json.getString("description"));
			result.add(data);
		}
		return result;
	}

	//получение списка должностей для постановки в афише
	public List<Data2> getListPositionsPoster(String list, int theater) throws JSONException {
		List<Data2> result = new ArrayList<>();
		JSONObject data_json;
		Data2 data;
		JSONArray array = new JSONArray(list);
		for(int i=0;i<array.length();i++){
			data = new Data2();
			data_json = (JSONObject) array.get(i);
			data.setId_position(data_json.getInt("id_position"));
			data.setName_position(Service.getPosition(data.getId_position()).getName());
			if(theater == 1) data.setId_person(data_json.getInt("id_person"));
			if(theater == 0){
				data.setName_person(data_json.getString("name_person"));
				data.setSocial_link(data_json.getString("social_link"));
			}else data.setName_person(Service.getWorker(data.getId_person()).getName());
			result.add(data);
		}
		return result;
	}

	//получение списка ролей для постановки в афише
	public List<Data2> getListRolesPoster(String list_text_roles, int id_directory_performance, int theater){
		List<Data2> result = new ArrayList<>();
		Data1 data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT roles_json FROM directory_performances WHERE id=?");
			stmt.setInt(1, id_directory_performance);
			rs = stmt.executeQuery();
			if(rs.next()){
				String list_text_roles_performance = rs.getString("roles_json");
				List<Data1> list_roles_performance = getListRolesPerformance(list_text_roles_performance);
				JSONArray array = new JSONArray(list_text_roles);
				JSONObject obj;
				for(int i=0;i<array.length();i++){
					obj = (JSONObject) array.get(i);
					int id_position = obj.getInt("id_position");
					for(int j=0;j<list_roles_performance.size();j++) {
						data = list_roles_performance.get(j);
						if (id_position == data.getId()){
							Data2 data_add = new Data2();
							data_add.setId_position(id_position);
							data_add.setName_position(data.getName());
							if(theater == 1){
								data_add.setId_person(obj.getInt("id_person"));
								data_add.setName_person(Service.getWorker(data_add.getId_person()).getName());
							}else{
								data_add.setName_person(obj.getString("name_person"));
								data_add.setSocial_link(obj.getString("social_link"));
							}
							result.add(data_add);
						}
					}
				}
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение списка спектаклей
	public List<Performance> getPerformances(int select_page, int view, String name){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Performance> result = new ArrayList<>();
		Performance data;
		try {
			conn = Service.getConnection();
			String sql="SELECT id,name,age,children,description FROM directory_performances";
			String sql_count="SELECT COUNT(*) AS count FROM directory_performances";
			if(view > 0){
				if(view == 1) view = 0;
				else view = 1;
				sql+=" WHERE children=" + view;
				sql_count+=" WHERE children=" + view;
			}
			if(!name.isEmpty()){
				if(view > 0){
					sql+=" AND name LIKE '%" + name + "%'";
					sql_count+=" AND name LIKE '%" + name + "%'";
				}else{
					sql+=" WHERE name LIKE '%" + name + "%'";
					sql_count+=" WHERE name LIKE '%" + name + "%'";
				}
			}
			sql+=" ORDER BY name";
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new Performance();
					data.setId(rs.getInt("id"));
					data.setName(rs.getString("name"));
					data.setAge(rs.getInt("age"));
					data.setChildren(rs.getInt("children"));
					data.setRating(getRatingPerformance(data.getId()));
					data.setRating_text(String.format("%.2f", data.getRating()));
					String description = rs.getString("description");
					if(description.length() > 400) description = description.substring(0, 400) + "...";
					data.setDescription(description);
					result.add(data);
				}
				count_unit++;
				if(count_unit == Service.NumberUnitsPage){
					count_unit = 0;
					count_pages++;
					if(count_pages > select_page) break;
				}
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение рейтинга по спектаклю в целом, то есть учитывая оценки по всем спектаклям данной постановки
	public double getRatingPerformance(int id_performance){
		double result = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		double sum = 0;
		int count = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT rating FROM rating_poster WHERE id_directory_performance=?");
			stmt.setInt(1, id_performance);
			rs = stmt.executeQuery();
			while (rs.next()){
				sum += rs.getDouble("rating");
				count++;
			}
			rs.close();stmt.close();
			if(sum > 0) result = sum / count;
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	class DataPoster{
		public int id, id_directory_performance;
		public String date_time_mysql;
	}

	//получение календаря по постановкам для афиши
	public List<CalendarPoster> getPoster(int view, String name){
		List<CalendarPoster> result = new ArrayList<>();
		Calendar date_time_now = Calendar.getInstance();
		String date_time_now_mysql = Service.convertCalendarDateTimeToStringMySQL(date_time_now);
		List<DataPoster> list_poster = new ArrayList<>();
		DataPoster data;
		CalendarPoster data_result;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_directory_performance,date_time FROM poster WHERE date_time>=? ORDER BY date_time");
			stmt.setString(1, date_time_now_mysql);
			rs = stmt.executeQuery();
			//собираем все постановки от текущей даты
			while (rs.next()){
				data = new DataPoster();
				data.id = rs.getInt("id");
				data.id_directory_performance = rs.getInt("id_directory_performance");
				data.date_time_mysql = rs.getString("date_time");
				list_poster.add(data);
			}
			rs.close();stmt.close();
			//если нужен определенный вид постановок, то убираем постановки с не заданным видом
			if(view > 0){
				if(view == 1) view = 0;
				else view = 1;
				for(int i = 0; i < list_poster.size(); i++){
					data = list_poster.get(i);
					stmt = conn.prepareStatement("SELECT children FROM directory_performances WHERE id=?");
					stmt.setInt(1, data.id_directory_performance);
					rs = stmt.executeQuery();
					if(rs.next()){
						if(rs.getInt("children") != view){
							list_poster.remove(i);
							i--;
						}
					}
					rs.close();stmt.close();
				}
			}
			//проверка по наименованию спектакля
			if(!name.isEmpty()){
				for(int i = 0; i < list_poster.size(); i++) {
					data = list_poster.get(i);
					stmt = conn.prepareStatement("SELECT name FROM directory_performances WHERE id=?");
					stmt.setInt(1, data.id_directory_performance);
					rs = stmt.executeQuery();
					if(rs.next()){
						if(rs.getString("name").toLowerCase().contains(name.toLowerCase()) == false){
							list_poster.remove(i);
							i--;
						}
					}
					rs.close();stmt.close();
				}
			}
			//формируем результат
			Calendar date_now = Calendar.getInstance();
			String date_now_mysql = Service.convertCalendarDateToStringMySQL(date_now);
			if(list_poster.size() > 0) {
				String date_end_mysql = list_poster.get(list_poster.size() - 1).date_time_mysql;
				date_end_mysql = date_end_mysql.substring(0, 10);
				boolean flag_stop = false;
				int count_days = 0;
				while (flag_stop == false) {
					int all = 0, children = 0, not_children = 0;
					//подсчитываем количество спектаклей в заданный день
					for (int i = 0; i < list_poster.size(); i++) {
						data = list_poster.get(i);
						if (date_now_mysql.equals(data.date_time_mysql.substring(0, 10))) {
							all++;
							stmt = conn.prepareStatement("SELECT children FROM directory_performances WHERE id=?");
							stmt.setInt(1, data.id_directory_performance);
							rs = stmt.executeQuery();
							if (rs.next())
								if (rs.getInt("children") == 1) children++;
								else not_children++;
							rs.close();
							stmt.close();
						}
					}
					//если в заданный день есть спектакли, то помещаем в результирующий список
					if (all > 0) {
						data_result = new CalendarPoster();
						data_result.setDate_mysql(date_now_mysql);
						data_result.setNumber_all_poster(all);
						data_result.setNumber_children_poster(children);
						data_result.setNumber_not_children_poster(not_children);
						data_result.setDate_text(Service.convertDateMySQLToDateString(date_now_mysql));
						int number_day = date_now.get(Calendar.DAY_OF_WEEK) - 1;
						data_result.setName_day(Service.getDayNameWeek(number_day));
						count_days++;
						if (count_days == Service.NumberDaysStroke) {
							count_days = 0;
							if (!date_now_mysql.equals(date_end_mysql)) data_result.setNew_stroke(1);
							else data_result.setNew_stroke(0);
						} else data_result.setNew_stroke(0);
						result.add(data_result);
					}
					if (date_now_mysql.equals(date_end_mysql)) flag_stop = true;
					//увеличиваем день на 1
					date_now.add(Calendar.DATE, 1);
					date_now_mysql = Service.convertCalendarDateToStringMySQL(date_now);
				}
				//устанавливаем id дней
				int count_id = 0;
				for(int i=0;i<result.size();i++){
					data_result = result.get(i);
					data_result.setId_day(count_id);
					result.set(i, data_result);
					count_id++;
				}
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение кратких данных о постановках в назначенный день
	public String getDataDayPoster(String date_mysql) throws JSONException {
		String result = "";
		JSONObject obj_result = new JSONObject();
		JSONObject obj;
		JSONArray array = new JSONArray();
		obj_result.put("date", Service.convertDateMySQLToDateString(date_mysql));
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;
		String name = "", image = "";
		int age = 0, children = 0, price_max = 0;
		int id_directory_performance;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_directory_performance,date_time,minutes,price_max,theater,description FROM poster WHERE date_time LIKE '%" + date_mysql + "%' ORDER BY date_time");
			rs = stmt.executeQuery();
			while (rs.next()){
				int id_poster = rs.getInt("id");
				obj = new JSONObject();
				//код постановки
				obj.put("id", id_poster);
				id_directory_performance = rs.getInt("id_directory_performance");
				obj.put("id_directory_performance", id_directory_performance);
				stmt1 = conn.prepareStatement("SELECT name,age,children,image1 FROM directory_performances WHERE id=?");
				stmt1.setInt(1, id_directory_performance);
				rs1 = stmt1.executeQuery();
				if(rs1.next()){
					name = rs1.getString("name");
					age = rs1.getInt("age");
					children = rs1.getInt("children");
					image = Service.convertBlobToString64(rs1.getBlob("image1"));
					if(image == null) image = "";
				}
				rs1.close();stmt1.close();
				//наименование
				if(children == 1) name += " (детский)";
				else name += " (взрослый)";
				obj.put("name", name);
				//возраст
				obj.put("age", age);
				//изображение
				obj.put("image", image);
				//время
				obj.put("time", rs.getString("date_time").substring(11, 16));
				//минимальная и максимальная цена билета
				price_max = rs.getInt("price_max");
				obj.put("min_max_ticket", getMinMaxPriceTicket(price_max) + " - " + price_max + " руб.");
				//количество оставшихся билетов
				obj.put("free_tickets", getFreeTicketsPoster(id_poster));
				//рейтинг спектакля
				double rating = getRatingPerformance(id_directory_performance);
				obj.put("rating", rating);
				obj.put("rating_text", String.format("%.2f", rating));
				//чья постановка
				int theater = rs.getInt("theater");
				obj.put("theater", theater);
				if(theater == 0){
					String description = rs.getString("description");
					if(!description.isEmpty()) obj.put("about_theater", description);
				}
				array.put(obj);
			}
			obj_result.put("poster", array);
			result = obj_result.toString();
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//вычисление минимальной цены билета
	public int getMinMaxPriceTicket(int price_max){
		int result = price_max, number_rows, number_rows_price, percent_price;
		number_rows = Integer.parseInt(Service.getSetting("number_rows"));
		number_rows_price = Integer.parseInt(Service.getSetting("number_rows_price"));
		percent_price = Integer.parseInt(Service.getSetting("percent_price"));
		int now_number_row = number_rows_price;
		int minus_sum = price_max / 100 * percent_price;
		do{
			now_number_row += number_rows_price;
			result -= minus_sum;
		}while(now_number_row < number_rows);
		return result;
	}

	//вычисляем количество билетов на постановку
	public int getFreeTicketsPoster(int id_poster){
		int result = 0, number_buy_tickets = 0, all_tickets = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			all_tickets = Integer.parseInt(Service.getSetting("number_rows")) * Integer.parseInt(Service.getSetting("number_places_row"));
			stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM orders WHERE id_poster=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			if(rs.next()) number_buy_tickets=rs.getInt("count");
			result = all_tickets - number_buy_tickets;
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных о постановке
	public Poster getPoster(int id_poster){
		Poster result = new Poster();
		Performance performance;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id_directory_performance,date_time,minutes,price_max,positions_json,actors_roles_json,theater,description FROM poster WHERE id=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_poster);
				result.setId_directory_performance(rs.getInt("id_directory_performance"));
				performance = getPerformance(rs.getInt("id_directory_performance"));
				result.setDate_time(Service.convertDateMySQLToDateTimeString(rs.getString("date_time")));
				LocalDateTime local_date_time = rs.getTimestamp("date_time").toLocalDateTime();
				result.setDate_time_local(local_date_time);
				result.setDate_time_start(rs.getTimestamp("date_time"));
				result.setMinutes(rs.getInt("minutes"));
				result.setPrice_max(rs.getInt("price_max"));
				result.setPrice_ticket("от " + getMinMaxPriceTicket(result.getPrice_max()) + " до " + result.getPrice_max() + " руб.");
				result.setFree_tickets(getFreeTicketsPoster(id_poster));
				temp = performance.getName();
				if(performance.getChildren() == 0) temp += "( взрослый)";
				else temp += " (детский)";
				result.setName_performance(temp);
				result.setTheater(rs.getInt("theater"));
				if(result.getTheater() == 0) result.setDescription(rs.getString("description"));
				result.setAbout_performance(performance.getDescription());
				List<Data2> list_positions = getListPositionsPoster(rs.getString("positions_json"), result.getTheater());
				List<Data2> list_roles = getListRolesPoster(rs.getString("actors_roles_json"), rs.getInt("id_directory_performance"), result.getTheater());
				result.setList_text_positions(new Gson().toJson(list_positions));
				result.setList_text_roles(new Gson().toJson(list_roles));
				result.setList_positions(list_positions);
				result.setList_roles(list_roles);
				result.setDescription(rs.getString("description"));
			}
			rs.close();stmt.close();
			//получаем картинку
			stmt = conn.prepareStatement("SELECT image1 FROM directory_performances WHERE id=?");
			stmt.setInt(1, result.getId_directory_performance());
			rs = stmt.executeQuery();
			if(rs.next()) result.setImage_str(Service.convertBlobToString64(rs.getBlob("image1")));
			else result.setImage_str(null);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по местам в зале в json формате
	public String GetPlaces(int id_poster) throws JSONException {
		String result = "";
		JSONArray array = new JSONArray();
		JSONObject obj;
		int number_rows, number_places_row, number_rows_price, percent_price, count = 0, price;
		//получаем настройки
		number_rows = Integer.parseInt(Service.getSetting("number_rows"));
		number_places_row = Integer.parseInt(Service.getSetting("number_places_row"));
		number_rows_price = Integer.parseInt(Service.getSetting("number_rows_price"));
		percent_price = Integer.parseInt(Service.getSetting("percent_price"));
		Poster poster = getPoster(id_poster);//получаем данные по постановке
		List<Place> list_not_free_places = getNotFreePlacesPoster(id_poster);
		for(int i=1;i<=number_rows;i++){
			price = getPricePlace(i, poster.getPrice_max(), number_rows_price, percent_price);
			for(int j=1;j<=number_places_row;j++){
				obj = new JSONObject();
				obj.put("row", i);
				obj.put("place", j);
				obj.put("price", price);
				obj.put("free", getFreePlace(list_not_free_places, i, j));
				obj.put("buy", false);
				array.put(obj);
			}
		}
		result = array.toString();
		return result;
	}

	class Place{
		public int row, place;
	}

	//получаем в виде массива все уже купленные места на постановку в афише
	public List<Place> getNotFreePlacesPoster(int id_poster){
		List<Place> result = new ArrayList<>();
		Place data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT number_row,number_place FROM orders WHERE id_poster=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Place();
				data.row = rs.getInt("number_row");
				data.place = rs.getInt("number_place");
				result.add(data);
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//определяем цену билета на место в зале
	public int getPricePlace(int row, int price_max, int number_rows_price, int percent_price){
		int result = 0;
		int minus_sum = price_max / 100 * percent_price;
		result = price_max;
		int now_row = number_rows_price;
		for(;;){
			if(row<=now_row) break;
			now_row += number_rows_price;
			result -= minus_sum;
		}
		return result;
	}

	//определяем свободное ли место в зале, если true, то место свободно
	public boolean getFreePlace(List<Place> list, int row, int place){
		boolean result = true;
		Place data;
		for(int i=0;i<list.size();i++){
			data = list.get(i);
			if(row == data.row && place == data.place){
				result = false;
				break;
			}
		}
		return result;
	}

	//покупка билетов на постановку
	public String payPoster(int id_poster, int id_user, int method_pay, String list){
		String result = "";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id_directory_performance = getPoster(id_poster).getId_directory_performance();
		List<Place> list_not_free_place = getNotFreePlacesPoster(id_poster);
		int count_buy_tickets = 0;
		try {
			conn = Service.getConnection();
			JSONArray list_tickets = new JSONArray(list);
			JSONArray list_id_tickets = new JSONArray();
			JSONObject obj;
			String date_time_pay = Service.convertCalendarDateTimeToStringMySQL(Calendar.getInstance());
			for(int i=0;i<list_tickets.length();i++){
				obj = (JSONObject) list_tickets.get(i);
				//определяем свободно ли еще место в зале
				if(getFreePlace(list_not_free_place, obj.getInt("row"), obj.getInt("place"))) {
					stmt = conn.prepareStatement("INSERT INTO orders (id_user,id_poster,id_directory_performance,date_time_pay,method_pay,number_row,number_place,price,number_code)VALUES(?,?,?,?,?,?,?,?,?)");
					if (id_user > 0) stmt.setInt(1, id_user);
					else stmt.setNull(1, Types.INTEGER);
					stmt.setInt(2, id_poster);
					stmt.setInt(3, id_directory_performance);
					stmt.setString(4, date_time_pay);
					stmt.setInt(5, method_pay);
					stmt.setInt(6, obj.getInt("row"));
					stmt.setInt(7, obj.getInt("place"));
					stmt.setInt(8, obj.getInt("price"));
					stmt.setString(9, Service.encrypt(generatorNumber()));
					stmt.execute();stmt.close();
					//получаем id нового билета
					stmt = conn.prepareStatement("SELECT MAX(id) AS max_id FROM orders");
					rs = stmt.executeQuery();
					if(rs.next()) list_id_tickets.put(rs.getInt("max_id"));
					rs.close();stmt.close();
					count_buy_tickets++;
				}
			}
			obj = new JSONObject();
			if(count_buy_tickets == list_tickets.length()) obj.put("message", "Все заказанные билеты или билет успешно приобретены.");
			else obj.put("message", "Приобретено " + count_buy_tickets + " из " + list_tickets.length() + " билетов, так как некоторые билеты уже были приобретены другими покупателями.");
			obj.put("list_id", list_id_tickets);
			result = obj.toString();
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//проверка есть ли отзыв по определенной постановке определенного пользователя
	//если true, то отзыв есть
	public boolean checkRatingUserPoster(int id_poster, int id_user){
		boolean result = false;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id FROM rating_poster WHERE id_poster=? AND id_user=?");
			stmt.setInt(1, id_poster);
			stmt.setInt(2, id_user);
			rs = stmt.executeQuery();
			if(rs.next()) result = true;
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение оценок и отзывов по постановке определенного пользователя
	public RatingPoster getRatingPosterUser(int id_user, int id_poster){
		RatingPoster result = new RatingPoster();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Poster poster = getPoster(id_poster);
		Performance performance = getPerformance(poster.getId_directory_performance());
		if(performance.getChildren() == 1) result.setName_performance(performance.getName() + " (детский)" + " " + poster.getDate_time());
		else result.setName_performance(performance.getName() + " (взрослый)" + " " + poster.getDate_time());
		result.setId_poster(id_poster);
		result.setTheater(poster.getTheater());
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_directory_performance,rating,date,review FROM rating_poster WHERE id_poster=? AND id_user=?");
			stmt.setInt(1, id_poster);
			stmt.setInt(2, id_user);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(rs.getInt("id"));
				result.setId_directory_performance(rs.getInt("id_directory_performance"));
				result.setRating(rs.getInt("rating"));
				result.setDate(rs.getDate("date"));
				result.setDate_text(Service.getDateText(result.getDate()));
				String review = rs.getString("review");
				if(rs.wasNull()) review = "";
				result.setReview(review);
				result.setTheater(poster.getTheater());
			}
			rs.close();stmt.close();
			//получаем оценки по должностям и актерам
			List<Data2> list_positions = getListPositionsPoster(poster.getList_text_positions(), poster.getTheater());
			List<Data2> list_roles = getListRolesPoster(poster.getList_text_roles(), poster.getId_directory_performance(), poster.getTheater());
			if(result.getTheater() == 1){
				getSetRatingWorkers(id_user, id_poster, list_positions);
				getSetRatingWorkers(id_user, id_poster, list_roles);
			}
			result.setList_text_positions(poster.getList_text_positions());
			result.setList_text_roles(poster.getList_text_roles());
			result.setList_positions(list_positions);
			result.setList_roles(list_roles);
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получаем установленные оценки и отзывы на своих сотрудников театра
	public void getSetRatingWorkers(int id_user, int id_poster, List<Data2> list){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Data2 data;
		try {
			conn = Service.getConnection();
			for(int i=0;i<list.size();i++){
				data = list.get(i);
				stmt = conn.prepareStatement("SELECT rating,date,review FROM rating_actors WHERE id_poster=? AND id_user=? AND id_worker=?");
				stmt.setInt(1, id_poster);
				stmt.setInt(2, id_user);
				stmt.setInt(3, data.getId_person());
				rs = stmt.executeQuery();
				if(rs.next()){
					data.setRating(rs.getInt("rating"));
					String review = rs.getString("review");
					if(rs.wasNull()) review = "";
					data.setReview(review);
					data.setDate(rs.getDate("date"));
					data.setDate_text(Service.getDateText(data.getDate()));
					list.set(i, data);
				}
				rs.close();stmt.close();
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//получение данных по гостевой книге
	public List<Guestbook> getGuestbook(int id_user, boolean only_my, int select_page){
		List<Guestbook> result = new ArrayList<>();
		Guestbook data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "", sql_count = "";
		OperationsUser ClUser = new OperationsUser();
		try {
			conn = Service.getConnection();
			if(only_my == false){
				sql = "SELECT id,id_user,title,question,date_question,answer,date_answer FROM guestbook ORDER BY date_question DESC";
				sql_count = "SELECT COUNT(*) AS count FROM guestbook";
			}else{
				sql = "SELECT id,id_user,title,question,date_question,answer,date_answer FROM guestbook WHERE id_user=" + id_user + " ORDER BY date_question DESC";
				sql_count = "SELECT COUNT(*) AS count FROM guestbook WHERE id_user=" + id_user;
			}
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new Guestbook();
					data.setId(rs.getInt("id"));
					int idUser = rs.getInt("id_user");
					if(rs.wasNull()){
						data.setId_user(0);
						data.setName_user("Посетитель");
					}else{
						data.setId_user(idUser);
						data.setName_user(ClUser.getData(idUser).getName());
					}
					data.setTitle(rs.getString("title"));
					data.setDate_question(rs.getDate("date_question"));
					data.setDate_question_text(Service.getDateText(data.getDate_question()));
					data.setQuestion(rs.getString("question"));
					String answer = rs.getString("answer");
					if(rs.wasNull()) data.setFlag_answer(false);
					else{
						data.setFlag_answer(true);
						data.setDate_answer(rs.getDate("date_answer"));
						data.setDate_answer_text(Service.getDateText(data.getDate_answer()));
						data.setAnswer(rs.getString("answer"));
					}
					result.add(data);
				}
				count_unit++;
				if(count_unit == Service.NumberUnitsPage){
					count_unit = 0;
					count_pages++;
					if(count_pages > select_page) break;
				}
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение новостей
	public List<News> getPageNews(int select_page){
		List<News> result = new ArrayList<>();
		News data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "", sql_count = "";
		OperationsUser ClUser = new OperationsUser();
		try {
			conn = Service.getConnection();
			sql = "SELECT id,title,date,description FROM news ORDER BY date DESC";
			sql_count = "SELECT COUNT(*) AS count FROM news";
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new News();
					data.setId(rs.getInt("id"));
					data.setTitle(rs.getString("title"));
					data.setDate(rs.getDate("date"));
					data.setDate_text(Service.getDateText(data.getDate()));
					String description = rs.getString("description");
					if(!rs.wasNull()) {
						if(description.length() > 450) description = description.substring(0, 450) + "...";
						data.setDescription(description);
					}else data.setDescription("");
					result.add(data);
				}
				count_unit++;
				if(count_unit == Service.NumberUnitsPage){
					count_unit = 0;
					count_pages++;
					if(count_pages > select_page) break;
				}
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по определенной новости
	public News getNews(int id_news){
		News result = new News();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT title,date,description,image1,image2,image3 FROM news WHERE id=?");
			stmt.setInt(1, id_news);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_news);
				result.setTitle(rs.getString("title"));
				result.setDate(rs.getDate("date"));
				result.setDate_text(Service.getDateText(result.getDate()));
				result.setDescription(rs.getString("description"));
				result.setImage_str1(Service.convertBlobToString64(rs.getBlob("image1")));
				result.setImage_str2(Service.convertBlobToString64(rs.getBlob("image2")));
				result.setImage_str3(Service.convertBlobToString64(rs.getBlob("image3")));
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение последних свежих анонсов в виде постановок на спектакли
	public List<Poster> getPosterIndex(){
		List<Poster> result = new ArrayList<>();
		Poster data;
		Calendar date_time_now = Calendar.getInstance();
		date_time_now.add(Calendar.HOUR_OF_DAY, 2);//увеличиваем время на 2 часа
		String date_sql = Service.convertCalendarDateTimeToStringMySQL(date_time_now);
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id FROM poster WHERE date_time>=? ORDER BY date_time");
			stmt.setString(1, date_sql);
			rs = stmt.executeQuery();
			int count = 0;
			while (rs.next()){
				data = getPosterForIndex(conn, rs.getInt("id"));
				String about = data.getAbout_performance();
				if(about.length() > 450){
					about = about.substring(0, 450);
					data.setAbout_performance(about + "...");
				}
				result.add(data);
				count++;
				if(count == Service.NumberIndex) break;
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных о постановке
	public Poster getPosterForIndex(Connection conn, int id_poster){
		Poster result = new Poster();
		Performance performance;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp;
		int id_directory_performance = 0;
		try {
			stmt = conn.prepareStatement("SELECT id_directory_performance,date_time,theater,description FROM poster WHERE id=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_poster);
				id_directory_performance = rs.getInt("id_directory_performance");
				result.setId_directory_performance(id_directory_performance);
				performance = getPerformance(id_directory_performance);
				result.setDate_time(Service.convertDateMySQLToDateTimeString(rs.getString("date_time")));
				LocalDateTime local_date_time = rs.getTimestamp("date_time").toLocalDateTime();
				result.setDate_time_local(local_date_time);
				result.setDate_time_start(rs.getTimestamp("date_time"));
				temp = performance.getName();
				if(performance.getChildren() == 0) temp += "( взрослый)";
				else temp += " (детский)";
				result.setName_performance(temp);
				result.setTheater(rs.getInt("theater"));
				if(result.getTheater() == 0) result.setDescription(rs.getString("description"));
				result.setAbout_performance(performance.getDescription());
				result.setDescription(rs.getString("description"));
			}
			rs.close();stmt.close();
			//получаем картинку
			stmt = conn.prepareStatement("SELECT image1 FROM directory_performances WHERE id=?");
			stmt.setInt(1, result.getId_directory_performance());
			rs = stmt.executeQuery();
			if(rs.next()) result.setImage_str(Service.convertBlobToString64(rs.getBlob("image1")));
			else result.setImage_str(null);
			rs.close();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение последних новостей для главной страницы
	public List<News> getNewsIndex(){
		List<News> result = new ArrayList<>();
		News data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id FROM news ORDER BY date DESC");
			rs = stmt.executeQuery();
			int count = 0;
			while (rs.next()){
				data = getNewsForIndex(conn, rs.getInt("id"));
				String description = data.getDescription();
				if(description.length() > 450){
					description = description.substring(0, 450);
					data.setDescription(description + "...");
				}
				result.add(data);
				count++;
				if(count == Service.NumberIndex) break;
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по определенной новости
	public News getNewsForIndex(Connection conn, int id_news){
		News result = new News();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT title,date,description,image1 FROM news WHERE id=?");
			stmt.setInt(1, id_news);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_news);
				result.setTitle(rs.getString("title"));
				result.setDate(rs.getDate("date"));
				result.setDate_text(Service.getDateText(result.getDate()));
				result.setDescription(rs.getString("description"));
				result.setImage_str1(Service.convertBlobToString64(rs.getBlob("image1")));
			}
			rs.close();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//формирование данных по билетам для печати
	public List<Ticket> getPrintTickets(String list_id_tickets) throws JSONException {
		List<Ticket> result = new ArrayList<>();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Poster poster;
		Ticket ticket;
		JSONArray array = new JSONArray(list_id_tickets);
		try {
			conn = Service.getConnection();
			for (int i = 0; i < array.length(); i++) {
				stmt = conn.prepareStatement("SELECT id_poster,method_pay,number_row,number_place,price,number_code FROM orders WHERE id=?");
				stmt.setInt(1, array.getInt(i));
				rs = stmt.executeQuery();
				if (rs.next()) {
					ticket = new Ticket();
					//формирование текстовых данных по билету
					poster = getPoster(rs.getInt("id_poster"));
					if (rs.getInt("method_pay") == 1) ticket.setMethod_pay("Оплата по карте");
					else ticket.setMethod_pay("Оплата через кошелёк ЮMoney");
					ticket.setRow(rs.getInt("number_row"));
					ticket.setPlace(rs.getInt("number_place"));
					ticket.setPrice(rs.getInt("price"));
					ticket.setName_performance(poster.getName_performance());
					ticket.setDate_time_start_poster(poster.getDate_time());
					//формирование изображения штрих кода
					BarcodeGenerator generator = new BarcodeGenerator(EncodeTypes.CODE_128, "");
					generator.setCodeText(Service.decrypt(rs.getString("number_code")));
					generator.getParameters().setResolution(70);
					BufferedImage buff = generator.generateBarCodeImage();
					ticket.setImage_str_number_code(fromImage(buff));
					result.add(ticket);
				}
				rs.close();stmt.close();
			}
			conn.close();
		} catch(SQLException | ClassNotFoundException | IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String fromImage(BufferedImage image) throws IOException {
		String imageString = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		byte[] imageBytes = out.toByteArray();
		imageString = DatatypeConverter.printBase64Binary(imageBytes);
		out.close();
		return imageString;
	}

	//генерация чисел
	public String generatorNumber(){
		String result = "";
		int number = 0;
		Random r = new Random();
		for(int i=0;i<10;i++){
			number = r.nextInt(10);
			result += Integer.toString(number);
		}
		return result;
	}

	//получение оценок по спектаклю в json
	public List<Rating> getRatingPerformanceUsers(int id_directory_performance){
		List<Rating> result = new ArrayList<>();
		Rating data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id_poster = 0, id_user = 0;
		User user;
		Poster poster;
		OperationsUser cl_user = new OperationsUser();
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_poster,id_user,date,rating,review FROM rating_poster WHERE id_directory_performance=? ORDER BY date DESC");
			stmt.setInt(1, id_directory_performance);
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Rating();
				data.setId(rs.getInt("id"));
				id_user = rs.getInt("id_user");
				id_poster = rs.getInt("id_poster");
				user = cl_user.getData(id_user);
				data.setName_user(user.getName());
				poster = getPoster(id_poster);
				data.setDate_time_poster(poster.getDate_time());
				data.setRating(rs.getInt("rating"));
				String review = rs.getString("review");
				if(!rs.wasNull()) data.setReview(review);
				else data.setReview(null);
				data.setDate(rs.getDate("date"));
				data.setDate_text(Service.getDateText(data.getDate()));
				result.add(data);
			}
			rs.close();stmt.close();
			conn.close();
		} catch(SQLException | ClassNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение списка оценок и отзывов по сотруднику театра
	public List<Rating> getRatingWorkerUsers(int id_worker){
		List<Rating> result = new ArrayList<>();
		Rating data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int id_poster = 0, id_user = 0;
		User user;
		Poster poster;
		OperationsUser cl_user = new OperationsUser();
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_poster,id_user,date,rating,review FROM rating_actors WHERE id_worker=? ORDER BY date DESC");
			stmt.setInt(1, id_worker);
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Rating();
				data.setId(rs.getInt("id"));
				id_user = rs.getInt("id_user");
				id_poster = rs.getInt("id_poster");
				user = cl_user.getData(id_user);
				data.setName_user(user.getName());
				poster = getPoster(id_poster);
				data.setName_directory_performance(poster.getName_performance());
				data.setDate_time_poster(poster.getDate_time());
				data.setRating(rs.getInt("rating"));
				String review = rs.getString("review");
				if(!rs.wasNull()) data.setReview(review);
				else data.setReview(null);
				data.setDate(rs.getDate("date"));
				data.setDate_text(Service.getDateText(data.getDate()));
				result.add(data);
			}
			rs.close();stmt.close();
			conn.close();
		} catch(SQLException | ClassNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
