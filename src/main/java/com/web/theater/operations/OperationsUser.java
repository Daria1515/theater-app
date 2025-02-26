package com.web.theater.operations;

import com.web.theater.Service;
import com.web.theater.structs.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//КЛАСС ПО ОПЕРАЦИЯМ ПОЛЬЗОВАТЕЛЯ
public class OperationsUser {
	OperationsGeneral ClGeneral = new OperationsGeneral();

	//получение личных данных пользователя
	public User getData(int id_user){
		User result = new User();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("SELECT name,date_birth,email,login,password,image FROM users WHERE id=?");
			stmt.setInt(1, id_user);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_user);
				result.setName(rs.getString("name"));
				result.setDate_birth(rs.getDate("date_birth"));
				result.setDate_birth_text(Service.getDateText(result.getDate_birth()));
				result.setEmail(rs.getString("email"));
				result.setLogin(rs.getString("login"));
				String password = rs.getString("password");
				if(!rs.wasNull()) result.setPassword(Service.decrypt(rs.getString("password")));
				else result.setPassword("");
				result.setImage_str(Service.convertBlobToString64(rs.getBlob("image")));
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//сохранение личных данных
	public String saveData(User data){
		String result = "OK";
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
		int id_base = Service.getIdUserLogin(data.getLogin());
		if(id_base > 0)
			if(data.getId() != id_base){
				result = "Пользователь с логином '" + data.getLogin() + "' существует.";
				return result;
			}
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("UPDATE users SET name=?,date_birth=?,email=?,login=?,password=? WHERE id=?");
			stmt.setString(1, data.getName());
			stmt.setDate(2, data.getDate_birth());
			stmt.setString(3, data.getEmail());
			stmt.setString(4, data.getLogin());
			stmt.setString(5, Service.encrypt(data.getPassword()));
			stmt.setInt(6, data.getId());
			stmt.execute();
			stmt.close();
			if(data.getOn_change_image()>0) updateImage(data.getOn_change_image(), data.getId(), data.getImage().getBytes());
			conn.close();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//обновление фото пользователя oper = 1 - изменить, 2 - удалить
	private void updateImage(int oper, int id_patient, byte [] image_bytes){
		Connection conn;
		PreparedStatement stmt = null;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("UPDATE users SET image=? WHERE id=?");
			switch (oper){
				case 1:
					image_blob = new SerialBlob(image_bytes);
					stmt.setBlob(1, image_blob);
					break;
				case 2:
					stmt.setNull(1, Types.BLOB);
					break;
			}
			stmt.setInt(2, id_patient);
			stmt.execute();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//класс для сортировки билетов по дате постановки
	class TicketSort{
		public int id;
		public Timestamp date_time;
	}

	//получение билетов пользователя
	public List<Ticket> getTicketsUser(int id_user, int select_page, int status){
		List<Ticket> result = new ArrayList<>();
		Ticket data;
		TicketSort data_sort;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		boolean flag_add;
		Calendar date_time = Calendar.getInstance();
		Timestamp date_time_start_poster, date_time_now = null;
		Poster poster;
		List<TicketSort> list_id_orders = new ArrayList<>();
		switch (status){
			case 1://прошедшие
				date_time_now = new Timestamp(date_time.getTimeInMillis());
				break;
			case 2://текущие
				date_time.add(Calendar.HOUR_OF_DAY, -3);//уменьшаем часы на 3 часа
				date_time_now = new Timestamp(date_time.getTimeInMillis());
				break;
		}
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_poster FROM orders WHERE id_user=?");
			stmt.setInt(1, id_user);
			rs = stmt.executeQuery();
			while (rs.next()){
				flag_add = false;
				int id_poster = rs.getInt("id_poster");
				poster = getPoster(conn, id_poster);
				date_time_start_poster = poster.getDate_time_start();
				//проверяем условие по дате начала постановки
				if(status == 0) flag_add = true;
				else{
					if(status == 1)
						if(date_time_start_poster.before(date_time_now)) flag_add = true;
					if(status == 2)
						if(date_time_start_poster.after(date_time_now)) flag_add = true;
				}
				if(flag_add){
					data_sort = new TicketSort();
					data_sort.id = rs.getInt("id");
					data_sort.date_time = poster.getDate_time_start();
					list_id_orders.add(data_sort);
				}
			}
			rs.close();stmt.close();
			//подсчитываем количество записей
			Service.CountAll = list_id_orders.size();
			//сортировка
			sortPoster(list_id_orders);
			//формируем результат для страинцы
			int count_unit = 0, count_pages = 1;
			date_time_now = new Timestamp(date_time.getTimeInMillis());
			for(int i=0;i<list_id_orders.size();i++){
				if(count_pages == select_page){
					stmt = conn.prepareStatement("SELECT id,id_poster,id_directory_performance,date_time_pay,method_pay,number_row,number_place,price FROM orders WHERE id=?");
					stmt.setInt(1, list_id_orders.get(i).id);
					rs = stmt.executeQuery();
					if(rs.next()){
						poster = getPoster(conn, rs.getInt("id_poster"));
						data = new Ticket();
						data.setId(rs.getInt("id"));
						data.setId_poster(rs.getInt("id_poster"));
						data.setId_directory_performance(rs.getInt("id_directory_performance"));
						data.setDate_time_pay(Service.convertDateMySQLToDateTimeString(rs.getString("date_time_pay")));
						if(rs.getInt("method_pay") == 1) data.setMethod_pay("оплата по карте");
						else data.setMethod_pay("оплата через кошелёк ЮMoney");
						data.setRow(rs.getInt("number_row"));
						data.setPlace(rs.getInt("number_place"));
						data.setPrice(rs.getInt("price"));
						data.setName_performance(poster.getName_performance());
						data.setDate_time_start_poster(poster.getDate_time());
						data.setDate_time_poster(poster.getDate_time_start());
						if(data.getDate_time_poster().after(date_time_now)){
							data.setFlag_set_rating(0);
							data.setFlag_delete(0);
						}else{
							data.setFlag_delete(1);
							if(ClGeneral.checkRatingUserPoster(data.getId_poster(), id_user) == false) data.setFlag_set_rating(1);
							else data.setFlag_set_rating(2);
						}
						result.add(data);
					}
					rs.close();stmt.close();
				}
				count_unit++;
				if(count_unit == Service.NumberUnitsPage){
					count_unit = 0;
					count_pages++;
					if(count_pages > select_page) break;
				}
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных о постановке без лишних данных
	public Poster getPoster(Connection conn, int id_poster){
		Poster result = new Poster();
		Performance performance;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp;
		int id_directory_performance = 0;
		try {
			stmt = conn.prepareStatement("SELECT id_directory_performance,date_time FROM poster WHERE id=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_poster);
				id_directory_performance = rs.getInt("id_directory_performance");
				result.setId_directory_performance(id_directory_performance);
				performance= getPerformance(conn, id_directory_performance);
				result.setDate_time(Service.convertDateMySQLToDateTimeString(rs.getString("date_time")));
				LocalDateTime local_date_time = rs.getTimestamp("date_time").toLocalDateTime();
				result.setDate_time_local(local_date_time);
				result.setDate_time_start(rs.getTimestamp("date_time"));
				temp = performance.getName();
				if(performance.getChildren() == 0) temp += "( взрослый)";
				else temp += " (детский)";
				result.setName_performance(temp);
			}
			rs.close();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение имени спектакля, не по постановке в назначенное время
	public Performance getPerformance(Connection conn, int id_performance){
		Performance result = new Performance();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement("SELECT name,children FROM directory_performances WHERE id=?");
			stmt.setInt(1, id_performance);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setName(rs.getString("name"));
				result.setChildren(rs.getInt("children"));
			}
			rs.close();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//сортировка по убыванию по дате начала постановки
	public void sortPoster(List<TicketSort> list){
		TicketSort data;
		Timestamp date_time1, date_time2;
		for(int i=0;i<list.size();i++)
			for(int j=0;j<list.size() - 1;j++){
				date_time1 = list.get(j).date_time;
				date_time2 = list.get(j + 1).date_time;
				if(date_time1.after(date_time2)){
					data = list.get(j + 1);
					list.set(j + 1, list.get(j));
					list.set(j, data);
				}
			}
	}

	//добавление или изменение оценки по постановке
	public void addEditRatingPoster(RatingPoster data, int id_user){
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null;
		String date_mysql = Service.convertCalendarDateToStringMySQL(Calendar.getInstance());
		Poster poster = ClGeneral.getPoster(data.getId_poster());
		try {
			conn = Service.getConnection();
			//устанавливаем оценку по постановке
			switch (data.getOper()){
				case 1://добавить
					stmt = conn.prepareStatement("INSERT INTO rating_poster (id_poster,id_directory_performance,id_user,rating,date,review)VALUES(?,?,?,?,?,?)");
					stmt.setInt(1, data.getId_poster());
					stmt.setInt(2, poster.getId_directory_performance());
					stmt.setInt(3, id_user);
					stmt.setInt(4, data.getRating());
					stmt.setString(5, date_mysql);
					if(!data.getReview().isEmpty()) stmt.setString(6, data.getReview());
					else stmt.setNull(6, Types.VARCHAR);
					break;
				case 2://изменить
					stmt = conn.prepareStatement("UPDATE rating_poster SET id_poster=?,id_directory_performance=?,id_user=?,rating=?,date=?,review=? WHERE id=?");
					stmt.setInt(1, data.getId_poster());
					stmt.setInt(2, poster.getId_directory_performance());
					stmt.setInt(3, id_user);
					stmt.setInt(4, data.getRating());
					stmt.setString(5, date_mysql);
					if(!data.getReview().isEmpty()) stmt.setString(6, data.getReview());
					else stmt.setNull(6, Types.VARCHAR);
					stmt.setInt(7, data.getId());
					break;
			}
			stmt.execute();stmt.close();
			//устанавливаем оценки по должностям
			JSONArray list_positions = new JSONArray(data.getList_text_positions());
			for(int i=0;i<list_positions.length();i++){
				JSONObject obj = (JSONObject) list_positions.get(i);
				if(obj.getInt("rating") == 0){
					stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
					stmt.setInt(1, id_user);
					stmt.setInt(2, data.getId_poster());
					stmt.setInt(3, obj.getInt("id_worker"));
					rs = stmt.executeQuery();
					if(rs.next()){
						stmt1 = conn.prepareStatement("DELETE FROM rating_actors WHERE id=?");
						stmt1.setInt(1, rs.getInt("id"));
						stmt1.execute();stmt1.close();
					}
					rs.close();stmt.close();
				}else{
					String review = "";
					int id_position = Service.getWorker(obj.getInt("id_worker")).getId_position();
					boolean flag_add = true;
					stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
					stmt.setInt(1, id_user);
					stmt.setInt(2, data.getId_poster());
					stmt.setInt(3, obj.getInt("id_worker"));
					rs = stmt.executeQuery();
					if(rs.next()) flag_add = false;
					rs.close();stmt.close();
					if(flag_add) {
						//добавить
						stmt = conn.prepareStatement("INSERT INTO rating_actors (id_worker,id_position,id_user,id_poster,rating,date,review)VALUES(?,?,?,?,?,?,?)");
						stmt.setInt(1, obj.getInt("id_worker"));
						stmt.setInt(2, id_position);
						stmt.setInt(3, id_user);
						stmt.setInt(4, data.getId_poster());
						stmt.setInt(5, obj.getInt("rating"));
						stmt.setString(6, date_mysql);
						review = obj.getString("review");
						if (!review.isEmpty()) stmt.setString(7, review);
						else stmt.setNull(7, Types.VARCHAR);
					}
					else {//изменить
						int id = 0;
						stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
						stmt.setInt(1, id_user);
						stmt.setInt(2, data.getId_poster());
						stmt.setInt(3, obj.getInt("id_worker"));
						rs = stmt.executeQuery();
						if (rs.next()) id = rs.getInt("id");
						rs.close();
						stmt.close();
						stmt = conn.prepareStatement("UPDATE rating_actors SET id_worker=?,id_position=?,id_user=?,id_poster=?,rating=?,date=?,review=? WHERE id=?");
						stmt.setInt(1, obj.getInt("id_worker"));
						stmt.setInt(2, id_position);
						stmt.setInt(3, id_user);
						stmt.setInt(4, data.getId_poster());
						stmt.setInt(5, obj.getInt("rating"));
						stmt.setString(6, date_mysql);
						review = obj.getString("review");
						if (!review.isEmpty()) stmt.setString(7, review);
						else stmt.setNull(7, Types.VARCHAR);
						stmt.setInt(8, id);
					}
					stmt.execute();stmt.close();
				}
			}
			//устанавливаем оценки по ролям
			JSONArray list_roles = new JSONArray(data.getList_text_roles());
			for(int i=0;i<list_roles.length();i++){
				JSONObject obj = (JSONObject) list_roles.get(i);
				if(obj.getInt("rating") == 0){
					stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
					stmt.setInt(1, id_user);
					stmt.setInt(2, data.getId_poster());
					stmt.setInt(3, obj.getInt("id_worker"));
					rs = stmt.executeQuery();
					if(rs.next()){
						stmt1 = conn.prepareStatement("DELETE FROM rating_actors WHERE id=?");
						stmt1.setInt(1, rs.getInt("id"));
						stmt1.execute();stmt1.close();
					}
					rs.close();stmt.close();
				}else{
					String review = "";
					int id_position = Service.getWorker(obj.getInt("id_worker")).getId_position();
					boolean flag_add = true;
					stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
					stmt.setInt(1, id_user);
					stmt.setInt(2, data.getId_poster());
					stmt.setInt(3, obj.getInt("id_worker"));
					rs = stmt.executeQuery();
					if(rs.next()) flag_add = false;
					rs.close();stmt.close();
					if(flag_add) {
						//добавить
						stmt = conn.prepareStatement("INSERT INTO rating_actors (id_worker,id_position,id_user,id_poster,rating,date,review)VALUES(?,?,?,?,?,?,?)");
						stmt.setInt(1, obj.getInt("id_worker"));
						stmt.setInt(2, id_position);
						stmt.setInt(3, id_user);
						stmt.setInt(4, data.getId_poster());
						stmt.setInt(5, obj.getInt("rating"));
						stmt.setString(6, date_mysql);
						review = obj.getString("review");
						if (!review.isEmpty()) stmt.setString(7, review);
						else stmt.setNull(7, Types.VARCHAR);
					}
					else {//изменить
						int id = 0;
						stmt = conn.prepareStatement("SELECT id FROM rating_actors WHERE id_user=? AND id_poster=? AND id_worker=?");
						stmt.setInt(1, id_user);
						stmt.setInt(2, data.getId_poster());
						stmt.setInt(3, obj.getInt("id_worker"));
						rs = stmt.executeQuery();
						if (rs.next()) id = rs.getInt("id");
						rs.close();
						stmt.close();
						stmt = conn.prepareStatement("UPDATE rating_actors SET id_worker=?,id_position=?,id_user=?,id_poster=?,rating=?,date=?,review=? WHERE id=?");
						stmt.setInt(1, obj.getInt("id_worker"));
						stmt.setInt(2, id_position);
						stmt.setInt(3, id_user);
						stmt.setInt(4, data.getId_poster());
						stmt.setInt(5, obj.getInt("rating"));
						stmt.setString(6, date_mysql);
						review = obj.getString("review");
						if (!review.isEmpty()) stmt.setString(7, review);
						else stmt.setNull(7, Types.VARCHAR);
						stmt.setInt(8, id);
					}
					stmt.execute();stmt.close();
				}
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//добавление обращения
	public void addGuestbook(int id_user, String title, String question){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			String date = Service.convertCalendarDateToStringMySQL(Calendar.getInstance());
			stmt = conn.prepareStatement("INSERT INTO guestbook (id_user,title,question,date_question)VALUES(?,?,?,?)");
			if(id_user > 0)stmt.setInt(1, id_user);
			else stmt.setNull(1, Types.INTEGER);
			stmt.setString(2, title);
			stmt.setString(3, question);
			stmt.setString(4, date);
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}