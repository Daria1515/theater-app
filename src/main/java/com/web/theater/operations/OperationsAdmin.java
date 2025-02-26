package com.web.theater.operations;

import com.google.gson.Gson;
import com.web.theater.Service;
import com.web.theater.structs.*;
import org.json.JSONException;
import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//ОПЕРАЦИИ АДМИНИСТРАТОРА
public class OperationsAdmin {
	OperationsGeneral ClGeneral = new OperationsGeneral();
	OperationsUser ClUser = new OperationsUser();
	//получение должностей
	public List<Position> getPositions(){
		List<Position> result = new ArrayList<>();
		Position data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("SELECT id,name,description FROM positions ORDER BY name");
			rs = stmt.executeQuery();
			while(rs.next()){
				data = new Position();
				data.setId(rs.getInt("id"));
				data.setName(rs.getString("name"));
				data.setDescription(rs.getString("description"));
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

	//добавление или изменение должности
	public void addEditPosition(Position data){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = Service.getConnection();
			if(data.getId() == 0){//добавление
				stmt = conn.prepareStatement("INSERT INTO positions (name,description)VALUES(?,?)");
				stmt.setString(1, data.getName());
				stmt.setString(2, data.getDescription());
			}
			if(data.getId() > 0){//изменение
				stmt = conn.prepareStatement("UPDATE positions SET name=?,description=? WHERE id=?");
				stmt.setString(1, data.getName());
				stmt.setString(2, data.getDescription());
				stmt.setInt(3, data.getId());
			}
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//удаление должности
	public void deletePosition(int id_position){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = Service.getConnection();
			//удаляем рейтинг актеров
			stmt = conn.prepareStatement("DELETE FROM rating_actors WHERE id_position=?");
			stmt.setInt(1, id_position);
			stmt.execute();stmt.close();
			//удаляем служащих
			stmt = conn.prepareStatement("DELETE FROM workers WHERE id_position=?");
			stmt.setInt(1, id_position);
			stmt.execute();stmt.close();
			//удаляем должность
			stmt = conn.prepareStatement("DELETE FROM positions WHERE id=?");
			stmt.setInt(1, id_position);
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//добавление или изменение данных по сотруднику
	public void addEditWorker(Worker data){
		Connection conn;
		PreparedStatement stmt = null;
		byte[] bytes;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			if(data.getId() == 0){//добавление
				stmt = conn.prepareStatement("INSERT INTO workers (id_position,name,date_birth,description,social_link,image)VALUES(?,?,?,?,?,?)");
				stmt.setInt(1, data.getId_position());
				stmt.setString(2, data.getName());
				stmt.setDate(3, data.getDate_birth());
				stmt.setString(4, data.getDescription());
				if(!data.getSocial_link().isEmpty())stmt.setString(5, data.getSocial_link());
				else stmt.setNull(5, Types.VARCHAR);
				if(!data.getImage().isEmpty()) {
					bytes = data.getImage().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(6, image_blob);
				}else stmt.setNull(6, Types.BLOB);
			}
			if(data.getId() > 0){//изменение
				stmt = conn.prepareStatement("UPDATE workers SET id_position=?,name=?,date_birth=?,description=?,social_link=? WHERE id=?");
				stmt.setInt(1, data.getId_position());
				stmt.setString(2, data.getName());
				stmt.setDate(3, data.getDate_birth());
				stmt.setString(4, data.getDescription());
				if(!data.getSocial_link().isEmpty())stmt.setString(5, data.getSocial_link());
				else stmt.setNull(5, Types.VARCHAR);
				stmt.setInt(6, data.getId());
				if(data.getOn_change_image()>0) updateImageWorker(data.getOn_change_image(), data.getId(), data.getImage().getBytes());
			}
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//обновление фото сотрудника oper = 1 - изменить, 2 - удалить
	private void updateImageWorker(int oper, int id_worker, byte [] image_bytes){
		Connection conn;
		PreparedStatement stmt = null;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("UPDATE workers SET image=? WHERE id=?");
			switch (oper){
				case 1:
					image_blob = new SerialBlob(image_bytes);
					stmt.setBlob(1, image_blob);
					break;
				case 2:
					stmt.setNull(1, Types.BLOB);
					break;
			}
			stmt.setInt(2, id_worker);
			stmt.execute();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//удаление сотрудника
	public int deleteWorker(int id_worker, int id_position, String name){
		int all_pages = 0, all_units = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			//удаляем рейтинг актеров
			stmt = conn.prepareStatement("DELETE FROM rating_actors WHERE id_worker=?");
			stmt.setInt(1, id_worker);
			stmt.execute();stmt.close();
			//удаляем сотрудника
			stmt = conn.prepareStatement("DELETE FROM workers WHERE id=?");
			stmt.setInt(1, id_worker);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			String sql_count="SELECT COUNT(*) AS count FROM workers";
			if(id_position > 0) sql_count+=" WHERE id_position=" + id_position;
			if(!name.isEmpty()){
				if(id_position > 0) sql_count+=" AND name LIKE '%" + name + "%'";
				else sql_count+=" WHERE name LIKE '%" + name + "%'";
			}
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) all_units=rs.getInt("count");
			all_pages=(int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}

	//добавление или редактирование спектакля
	public void addEditPerformance(Performance data){
		Connection conn;
		PreparedStatement stmt = null;
		byte[] bytes;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			if(data.getId() == 0){//добавление
				stmt = conn.prepareStatement("INSERT INTO directory_performances (name,age,description,positions_json,roles_json,children,image1,image2,image3,image4,image5)VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				stmt.setString(1, data.getName());
				stmt.setInt(2, data.getAge());
				stmt.setString(3, data.getDescription());
				stmt.setString(4, data.getList_text_positions());
				stmt.setString(5, data.getList_text_roles());
				stmt.setInt(6, data.getChildren());
				if(!data.getImage1().isEmpty()) {
					bytes = data.getImage1().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(7, image_blob);
				}else stmt.setNull(7, Types.BLOB);
				if(!data.getImage2().isEmpty()) {
					bytes = data.getImage2().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(8, image_blob);
				}else stmt.setNull(8, Types.BLOB);
				if(!data.getImage3().isEmpty()) {
					bytes = data.getImage3().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(9, image_blob);
				}else stmt.setNull(9, Types.BLOB);
				if(!data.getImage4().isEmpty()) {
					bytes = data.getImage4().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(10, image_blob);
				}else stmt.setNull(10, Types.BLOB);
				if(!data.getImage5().isEmpty()) {
					bytes = data.getImage5().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(11, image_blob);
				}else stmt.setNull(11, Types.BLOB);
			}
			if(data.getId() > 0){//изменение
				stmt = conn.prepareStatement("UPDATE directory_performances SET name=?,age=?,description=?,positions_json=?,roles_json=?,children=? WHERE id=?");
				stmt.setString(1, data.getName());
				stmt.setInt(2, data.getAge());
				stmt.setString(3, data.getDescription());
				stmt.setString(4, data.getList_text_positions());
				stmt.setString(5, data.getList_text_roles());
				stmt.setInt(6, data.getChildren());
				stmt.setInt(7, data.getId());
				if(data.getOn_change_image1()>0) updateImagePerformance(1, data.getOn_change_image1(), data.getId(), data.getImage1().getBytes());
				if(data.getOn_change_image2()>0) updateImagePerformance(2, data.getOn_change_image2(), data.getId(), data.getImage2().getBytes());
				if(data.getOn_change_image3()>0) updateImagePerformance(3, data.getOn_change_image3(), data.getId(), data.getImage3().getBytes());
				if(data.getOn_change_image4()>0) updateImagePerformance(4, data.getOn_change_image4(), data.getId(), data.getImage4().getBytes());
				if(data.getOn_change_image5()>0) updateImagePerformance(5, data.getOn_change_image5(), data.getId(), data.getImage5().getBytes());
				editPerformancePoster(data.getId(), data.getList_text_positions(), data.getList_text_roles());
			}
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//изменение должностей и ролей по спектаклю в постановках в афише
	public void editPerformancePoster(int id_directory_performance, String list_positions, String list_roles) throws JSONException {
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null;
		List<Data1> list_positions_performance, list_roles_performance;
		List<Data2> list_positions_poster, list_roles_poster;
		Data2 data;
		list_positions_performance = ClGeneral.getListPositionsPerformance(list_positions);
		list_roles_performance = ClGeneral.getListRolesPerformance(list_roles);
		String list;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,positions_json,actors_roles_json,theater FROM poster WHERE id_directory_performance=?");
			stmt.setInt(1, id_directory_performance);
			rs = stmt.executeQuery();
			while (rs.next()){
				list = rs.getString("positions_json");
				int theater = rs.getInt("theater");
				list_positions_poster = ClGeneral.getListPositionsPoster(list, theater);
				list = rs.getString("actors_roles_json");
				list_roles_poster = ClGeneral.getListRolesPoster(list, id_directory_performance, theater);
				boolean flag_change, flag_delete=false;
				//проверяем должности
				for(int i=0; i<list_positions_poster.size();i++){
					flag_change = true;
					int id_position_poster = list_positions_poster.get(i).getId_position();
					for(int j=0; j<list_positions_performance.size(); j++)
						if (id_position_poster == list_positions_performance.get(j).getId()) {
							flag_change = false;
							break;
						}
					if(flag_change){
						list_positions_poster.remove(i);
						flag_delete = true;
						i--;
					}
				}
				if(flag_delete){
					list = "[";
					for(int i=0; i<list_positions_poster.size(); i++){
						data = list_positions_poster.get(i);
						list += "{\"id_position\":" + data.getId_position() + ",\"id_person\":" + data.getId_person();
						if(theater == 0) list += ",\"name_person:\"" + data.getName_person() + ",\"social_link:\"" + data.getSocial_link();
						list += "}";
						if(i<list_positions_poster.size() - 1) list += ",";
					}
					list += "]";
					stmt1 = conn.prepareStatement("UPDATE poster SET positions_json=? WHERE id=?");
					stmt1.setString(1, list);
					stmt1.setInt(2, rs.getInt("id"));
					stmt1.execute();stmt1.close();
				}
				//проверяем роли
				flag_delete = false;
				for(int i=0; i<list_roles_poster.size();i++){
					flag_change = true;
					int id_role_poster = list_roles_poster.get(i).getId_position();
					for(int j=0; j<list_roles_performance.size(); j++){
						if(id_role_poster == list_roles_performance.get(j).getId())
							flag_change = false;
							break;
						}
					if(flag_change){
						list_roles_poster.remove(i);
						flag_delete = true;
						i--;
					}
				}
				if(flag_delete){
					list = "[";
					for(int i=0; i<list_roles_poster.size(); i++){
						data = list_roles_poster.get(i);
						list += "{\"id_position\":" + data.getId_position() + ",\"id_person\":" + data.getId_person();
						if(theater == 0) list += ",\"name_person:\"" + data.getName_person() + ",\"social_link:\"" + data.getSocial_link();
						list += "}";
						if(i<list_roles_poster.size() - 1) list += ",";
					}
					list += "]";
					stmt1 = conn.prepareStatement("UPDATE poster SET actors_roles_json=? WHERE id=?");
					stmt1.setString(1, list);
					stmt1.setInt(2, rs.getInt("id"));
					stmt1.execute();stmt1.close();
				}
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//обновление фото спектакля oper = 1 - изменить, 2 - удалить
	private void updateImagePerformance(int number, int oper, int id_performance, byte [] image_bytes){
		Connection conn;
		PreparedStatement stmt = null;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("UPDATE directory_performances SET image" + number + "=? WHERE id=?");
			switch (oper){
				case 1:
					image_blob = new SerialBlob(image_bytes);
					stmt.setBlob(1, image_blob);
					break;
				case 2:
					stmt.setNull(1, Types.BLOB);
					break;
			}
			stmt.setInt(2, id_performance);
			stmt.execute();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//удаление данных по спектаклю
	public int deletePerformance(int id_performance, int view, String name){
		int all_pages = 0, all_units = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			//удаляем заказы
			stmt = conn.prepareStatement("DELETE FROM orders WHERE id_directory_performance=?");
			stmt.setInt(1, id_performance);
			stmt.execute();stmt.close();
			//удаляем рейтинги по спектаклю
			stmt = conn.prepareStatement("DELETE FROM rating_poster WHERE id_directory_performance=?");
			stmt.setInt(1, id_performance);
			stmt.execute();stmt.close();
			//удаляем в афише данный спектакль
			stmt = conn.prepareStatement("DELETE FROM poster WHERE id_directory_performance=?");
			stmt.setInt(1, id_performance);
			stmt.execute();stmt.close();
			//удаляем спектакль
			stmt = conn.prepareStatement("DELETE FROM directory_performances WHERE id=?");
			stmt.setInt(1, id_performance);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			String sql_count="SELECT COUNT(*) AS count FROM directory_performances";
			if(view > 0){
				int view1 = 0;
				if(view == 1) view1 = 0;
				else view1 = 1;
				sql_count+=" WHERE children=" + view1;
			}
			if(!name.isEmpty()){
				if(view > 0) sql_count+=" AND name LIKE '%" + name + "%'";
				else sql_count+=" WHERE name LIKE '%" + name + "%'";
			}
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) all_units=rs.getInt("count");
			all_pages=(int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}

	//получение настроек
	public Settings getSettings(){
		Settings result = new Settings();
		result.setPassword(Service.decrypt(Service.getSetting("admin_password")));
		result.setNumber_rows(Integer.parseInt(Service.getSetting("number_rows")));
		result.setNumber_places_row(Integer.parseInt(Service.getSetting("number_places_row")));
		result.setNumber_rows_price(Integer.parseInt(Service.getSetting("number_rows_price")));
		result.setPercent_price(Integer.parseInt(Service.getSetting("percent_price")));
		return result;
	}

	//сохранение настроек
	public void saveSettings(Settings data){
		Service.setSetting("admin_password", Service.encrypt(data.getPassword()));
		Service.setSetting("number_rows", Integer.toString(data.getNumber_rows()));
		Service.setSetting("number_places_row", Integer.toString(data.getNumber_places_row()));
		Service.setSetting("number_rows_price", Integer.toString(data.getNumber_rows_price()));
		Service.setSetting("percent_price", Integer.toString(data.getPercent_price()));
	}

	//получение списка всех спектаклей
	//в виде код, наименование, детский или взрослый
	//используется для выбора чего-то
	public List<Data1> getListPerformances(){
		List<Data1> result = new ArrayList<>();
		Data1 data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,name,children FROM directory_performances ORDER BY name");
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Data1();
				data.setId(rs.getInt("id"));
				String text = rs.getString("name");
				if(rs.getInt("children") == 0) text += " (взрослый)";
				else text += " (детский)";
				data.setName(text);
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

	//получение списка всех сотрудников
	//в виде код, ФИО, должность
	//используется для выбора чего-то
	public List<Data1> getListPositions(){
		List<Data1> result = new ArrayList<>();
		Data1 data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id,id_position,name FROM workers ORDER BY name");
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Data1();
				data.setId(rs.getInt("id"));
				String text = rs.getString("name");
				text += " (" + Service.getPosition(rs.getInt("id_position")).getName() + ")";
				data.setName(text);
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

	//получение должностей и актеров у выбранной постановки в афише
	//возвращает строку в формате json
	public String getPositionsActorsPoster(int id_directory_performance, int theater){
		String result = "";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Data1> list_positions, list_roles;
		List<Data2> list_positions_result = new ArrayList<>(), list_roles_result = new ArrayList<>();
		Data1 data;
		Data2 data_result;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT positions_json,roles_json FROM directory_performances WHERE id=?");
			stmt.setInt(1, id_directory_performance);
			rs = stmt.executeQuery();
			if(rs.next()){
				list_positions = ClGeneral.getListPositionsPerformance(rs.getString("positions_json"));
				list_roles = ClGeneral.getListRolesPerformance(rs.getString("roles_json"));
				list_positions_result = new ArrayList<>();
				for(int i=0;i<list_positions.size();i++){
					data_result = new Data2();
					data = list_positions.get(i);
					data_result.setId_position(data.getId());
					data_result.setName_position(data.getName());
					if(theater == 1) data_result.setId_person(0);
					else data_result.setId_person(i + 1);
					data_result.setName_person("");
					if(theater == 0) data_result.setSocial_link("");
					list_positions_result.add(data_result);
				}
				list_roles_result = new ArrayList<>();
				for(int i=0;i<list_roles.size();i++){
					data_result = new Data2();
					data = list_roles.get(i);
					data_result.setId_position(data.getId());
					data_result.setName_position(data.getName());
					if(theater == 1) data_result.setId_person(0);
					else data_result.setId_person(i + 1);
					data_result.setName_person("");
					if(theater == 0) data_result.setSocial_link("");
					list_roles_result.add(data_result);
				}
			}
			rs.close();stmt.close();
			result = "{\"list_positions\":" + new Gson().toJson(list_positions_result) + ", \"list_roles\":" + new Gson().toJson(list_roles_result) + "}";
			conn.close();
		} catch (SQLException | ClassNotFoundException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//добавление или изменение постановки в афише
	public void addEditPoster(Poster data){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = Service.getConnection();
			if(data.getId() == 0){//добавление
				stmt = conn.prepareStatement("INSERT INTO poster (id_directory_performance,date_time,minutes,price_max,positions_json,actors_roles_json,theater,description)VALUES(?,?,?,?,?,?,?,?)");
				stmt.setInt(1, data.getId_directory_performance());
				stmt.setString(2, data.getDate_time_local().toString().replace("T", " "));
				stmt.setInt(3, data.getMinutes());
				stmt.setInt(4, data.getPrice_max());
				stmt.setString(5, data.getList_text_positions());
				stmt.setString(6, data.getList_text_roles());
				stmt.setInt(7, data.getTheater());
				if(!data.getDescription().isEmpty())stmt.setString(8, data.getDescription());
				else stmt.setNull(8, Types.VARCHAR);
			}
			if(data.getId() > 0){//изменение
				stmt = conn.prepareStatement("UPDATE poster SET id_directory_performance=?,date_time=?,minutes=?,price_max=?,positions_json=?,actors_roles_json=?,description=? WHERE id=?");
				stmt.setInt(1, data.getId_directory_performance());
				stmt.setString(2, data.getDate_time_local().toString().replace("T", " "));
				stmt.setInt(3, data.getMinutes());
				stmt.setInt(4, data.getPrice_max());
				stmt.setString(5, data.getList_text_positions());
				stmt.setString(6, data.getList_text_roles());
				if(!data.getDescription().isEmpty())stmt.setString(7, data.getDescription());
				else stmt.setNull(7, Types.VARCHAR);
				stmt.setInt(8, data.getId());
			}
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//удаление постановки в афише
	public void deletePoster(int id_poster){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = Service.getConnection();
			//удаляем заказы
			stmt = conn.prepareStatement("DELETE FROM orders WHERE id_poster=?");
			stmt.setInt(1, id_poster);
			stmt.execute();stmt.close();
			//удаляем постановку в афише
			stmt = conn.prepareStatement("DELETE FROM poster WHERE id=?");
			stmt.setInt(1, id_poster);
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//получаем данные по обращению
	public Guestbook getGuestbook(int id_guestbook){
		Guestbook result = new Guestbook();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("SELECT id_user,title,question,date_question,answer,date_answer FROM guestbook WHERE id=?");
			stmt.setInt(1, id_guestbook);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_guestbook);
				result.setId_user(rs.getInt("id_user"));
				result.setTitle(rs.getString("title"));
				result.setDate_question(rs.getDate("date_question"));
				result.setDate_question_text(Service.getDateText(result.getDate_question()));
				String answer = rs.getString("answer");
				if(!rs.wasNull()){
					result.setAnswer(answer);
					result.setDate_answer(rs.getDate("date_answer"));
					result.setDate_answer_text(Service.getDateText(result.getDate_answer()));
				}else result.setAnswer(null);
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//удаление обращения
	public int deleteGuestbook(int id_guestbook){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int all_units = 0, all_pages = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("DELETE FROM guestbook WHERE id=?");
			stmt.setInt(1, id_guestbook);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			String sql_count="SELECT COUNT(*) AS count FROM guestbook";
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) all_units = rs.getInt("count");
			all_pages = (int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}

	//изменение ответа на обращение
	public void updateAnswerGuestbook(int id_guestion, String answer){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = Service.getConnection();
			String date = Service.convertCalendarDateToStringMySQL(Calendar.getInstance());
			stmt = conn.prepareStatement("UPDATE guestbook SET date_answer=?,answer=? WHERE id=?");
			stmt.setString(1, date);
			stmt.setString(2, answer);
			stmt.setInt(3, id_guestion);
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//добавить или изменить новость
	public void addEditNews(News data){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		byte[] bytes;
		Blob image_blob;
		String date = Service.convertCalendarDateToStringMySQL(Calendar.getInstance());
		try {
			conn = Service.getConnection();
			if(data.getId() == 0){//добавление
				stmt = conn.prepareStatement("INSERT INTO news (title,date,description,image1,image2,image3)VALUES(?,?,?,?,?,?)");
				stmt.setString(1, data.getTitle());
				stmt.setString(2, date);
				stmt.setString(3, data.getDescription());
				if(!data.getImage1().isEmpty()) {
					bytes = data.getImage1().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(4, image_blob);
				}else stmt.setNull(4, Types.BLOB);
				if(!data.getImage2().isEmpty()) {
					bytes = data.getImage2().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(5, image_blob);
				}else stmt.setNull(5, Types.BLOB);
				if(!data.getImage3().isEmpty()) {
					bytes = data.getImage3().getBytes();
					image_blob = new SerialBlob(bytes);
					stmt.setBlob(6, image_blob);
				}else stmt.setNull(6, Types.BLOB);
			}
			if(data.getId() > 0){//изменение
				stmt = conn.prepareStatement("UPDATE news SET title=?,date=?,description=? WHERE id=?");
				stmt.setString(1, data.getTitle());
				stmt.setString(2, date);
				stmt.setString(3, data.getDescription());
				stmt.setInt(4, data.getId());
				if(data.getOn_change_image1()>0) updateImageNews(1, data.getOn_change_image1(), data.getId(), data.getImage1().getBytes());
				if(data.getOn_change_image2()>0) updateImageNews(2, data.getOn_change_image2(), data.getId(), data.getImage2().getBytes());
				if(data.getOn_change_image3()>0) updateImageNews(3, data.getOn_change_image3(), data.getId(), data.getImage3().getBytes());
			}
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//обновление фото новости oper = 1 - изменить, 2 - удалить
	private void updateImageNews(int number, int oper, int id_news, byte [] image_bytes){
		Connection conn;
		PreparedStatement stmt = null;
		Blob image_blob;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("UPDATE news SET image" + number + "=? WHERE id=?");
			switch (oper){
				case 1:
					image_blob = new SerialBlob(image_bytes);
					stmt.setBlob(1, image_blob);
					break;
				case 2:
					stmt.setNull(1, Types.BLOB);
					break;
			}
			stmt.setInt(2, id_news);
			stmt.execute();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//удаление новости
	public int deleteNews(int id_news){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int all_units = 0, all_pages = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("DELETE FROM news WHERE id=?");
			stmt.setInt(1, id_news);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			String sql_count="SELECT COUNT(*) AS count FROM news";
			//подсчитываем количество записей
			stmt = conn.prepareStatement(sql_count);
			rs = stmt.executeQuery();
			if(rs.next()) all_units = rs.getInt("count");
			all_pages = (int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}

	//получение данных по статистике 1
	public List<Statistics> getStatistics1(boolean flag_all, String date_start, String date_end){
		List<Statistics> result = new ArrayList<>();
		Statistics data;
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;
		date_start += " 00:00:00";
		date_end += " 23:59:00";
		try {
			conn = Service.getConnection();
			//получаем наименование спектаклей
			stmt = conn.prepareStatement("SELECT id,name FROM directory_performances");
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Statistics();
				data.setId(rs.getInt("id"));
				data.setName(rs.getString("name"));
				result.add(data);
			}
			rs.close();stmt.close();
			int sum = 0;
			//формируем количество
			for(int i=0;i<result.size();i++){
				int count = 0;
				boolean check;
				data = result.get(i);
				stmt = conn.prepareStatement("SELECT id_poster FROM orders WHERE id_directory_performance=?");
				stmt.setInt(1, data.getId());
				rs = stmt.executeQuery();
				while (rs.next()){
					check = false;
					if(flag_all) check = true;
					else {
						int id_poster = rs.getInt("id_poster");
						stmt1 = conn.prepareStatement("SELECT id FROM poster WHERE id=" + id_poster + " AND (date_time>='"+date_start+"' AND date_time<='"+date_end+"')");
						rs1 = stmt1.executeQuery();
						if(rs1.next()) check = true;
						rs1.close();stmt1.close();
					}
					if(check) count++;
				}
				rs.close();stmt.close();
				sum += count;
				data.setNumber(count);
				result.set(i, data);
			}
			//формируем процент
			getPercent(result, sum);
			//сортировка
			sortStatistics(result);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по статистике 2
	public List<Statistics> getStatistics2(boolean flag_all, String date_start, String date_end){
		List<Statistics> result = new ArrayList<>();
		Statistics data;
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;
		date_start += " 00:00:00";
		date_end += " 23:59:00";
		try {
			conn = Service.getConnection();
			//получаем наименование спектаклей
			stmt = conn.prepareStatement("SELECT id,name FROM directory_performances");
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Statistics();
				data.setId(rs.getInt("id"));
				data.setName(rs.getString("name"));
				result.add(data);
			}
			rs.close();stmt.close();
			//формируем рейтинг
			for(int i=0;i<result.size();i++){
				boolean check;
				data = result.get(i);
				stmt = conn.prepareStatement("SELECT id_poster,rating FROM rating_poster WHERE id_directory_performance=?");
				stmt.setInt(1, data.getId());
				rs = stmt.executeQuery();
				int count = 0, sum = 0;
				while (rs.next()){
					check = false;
					int id_poster = rs.getInt("id_poster");
					if(flag_all) check = true;
					else {
						stmt1 = conn.prepareStatement("SELECT id FROM poster WHERE id=" + id_poster + " AND (date_time>='"+date_start+"' AND date_time<='"+date_end+"')");
						rs1 = stmt1.executeQuery();
						if(rs1.next()) check = true;
						rs1.close();stmt1.close();
					}
					//высчитываем общий рейтинг
					if(check){
						sum += rs.getInt("rating");
						count++;
					}
					data.setNumber(count);
					if(count > 0) data.setPercent((double) sum / count);
					else data.setPercent(0);
					data.setPercent_text(String.format("%.2f", data.getPercent()));
					result.set(i, data);
				}
				rs.close();stmt.close();
			}
			//сортировка
			sortStatistics(result);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по статистике 3
	public List<Statistics> getStatistics3(boolean flag_all, String date_start, String date_end){
		List<Statistics> result = new ArrayList<>();
		Statistics data;
		Connection conn;
		PreparedStatement stmt = null, stmt1 = null;
		ResultSet rs = null, rs1 = null;
		date_start += " 00:00:00";
		date_end += " 23:59:00";
		try {
			conn = Service.getConnection();
			//получаем имена с должностями сотрудников
			stmt = conn.prepareStatement("SELECT id,id_position,name FROM workers");
			rs = stmt.executeQuery();
			while (rs.next()){
				data = new Statistics();
				data.setId(rs.getInt("id"));
				data.setName(rs.getString("name") + " (" + Service.getPosition(rs.getInt("id_position")).getName() + ")");
				result.add(data);
			}
			rs.close();stmt.close();
			//формируем рейтинг
			for(int i=0;i<result.size();i++){
				int count = 0, sum = 0;
				boolean check;
				data = result.get(i);
				if(flag_all) {
					stmt = conn.prepareStatement("SELECT rating FROM rating_actors WHERE id_worker=?");
					stmt.setInt(1, data.getId());
				}else stmt = conn.prepareStatement("SELECT rating FROM rating_actors WHERE id_worker=" + data.getId() + " AND (date>='"+date_start+"' AND date<='"+date_end+"')");
				rs = stmt.executeQuery();
				while (rs.next()){
					count++;
					sum += rs.getInt("rating");
				}
				rs.close();stmt.close();
				data.setNumber(count);
				if(count > 0) data.setPercent((double) sum / count);
				else data.setPercent(0);
				data.setPercent_text(String.format("%.2f", data.getPercent()));
				result.set(i, data);
			}
			//сортировка
			sortStatistics(result);
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//формируем процент
	public void getPercent(List<Statistics> list, int sum){
		Statistics data;
		for(int i=0;i<list.size();i++){
			data = list.get(i);
			if(data.getNumber() > 0) data.setPercent(data.getNumber() / (double) sum * 100);
			data.setPercent_text(String.format("%.2f", data.getPercent()));
			list.set(i, data);
		}
	}

	//сортировка по проценту в порядке убывания
	public void sortStatistics(List<Statistics> list){
		Statistics data1, data2;
		for(int i=0;i<list.size();i++)
			for(int j=0;j<list.size() - 1;j++){
				data1 = list.get(j);
				data2 = list.get(j + 1);
				if(data1.getPercent() < data2.getPercent()){
					list.set(j, data2);
					list.set(j + 1, data1);
				}
			}
	}

	//получение данных по всем пользователям
	public List<User> getUsers(int select_page){
		List<User> result = new ArrayList<>();
		User data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = Service.getConnection();
			//подсчитываем количество записей
			stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM users");
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			stmt = conn.prepareStatement("SELECT id,name,date_birth,email,login FROM users ORDER BY name");
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new User();
					data.setId(rs.getInt("id"));
					data.setName(rs.getString("name"));
					data.setEmail(rs.getString("email"));
					data.setDate_birth(rs.getDate("date_birth"));
					data.setDate_birth_text(Service.getDateText(data.getDate_birth()) + " (" + Service.getAge(data.getDate_birth()) + " лет)");
					String login = rs.getString("login");
					if(!rs.wasNull()) data.setLogin(login);
					else data.setLogin("");
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
				stmt = conn.prepareStatement("SELECT image FROM users WHERE id=?");
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

	//удаление пользователя из системы
	public int deleteUser(int id_user){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int all_units = 0, all_pages = 0;
		try {
			conn = Service.getConnection();
			stmt = conn.prepareStatement("DELETE FROM rating_actors WHERE id_user=?");
			stmt.setInt(1, id_user);
			stmt.execute();stmt.close();
			stmt = conn.prepareStatement("DELETE FROM rating_poster WHERE id_user=?");
			stmt.setInt(1, id_user);
			stmt.execute();stmt.close();
			stmt = conn.prepareStatement("DELETE FROM guestbook WHERE id_user=?");
			stmt.setInt(1, id_user);
			stmt.execute();stmt.close();
			stmt = conn.prepareStatement("DELETE FROM orders WHERE id_user=?");
			stmt.setInt(1, id_user);
			stmt.execute();stmt.close();
			stmt = conn.prepareStatement("DELETE FROM users WHERE id=?");
			stmt.setInt(1, id_user);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			//подсчитываем количество записей
			stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM users");
			rs = stmt.executeQuery();
			if(rs.next()) all_units = rs.getInt("count");
			all_pages = (int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}

	//получение отзывов пользователей
	//view = 1 - отзывы по постановкам, 2 - отзывы по актерам
	public List<Rating> getRatings(int view, int select_page){
		List<Rating> result = new ArrayList<>();
		Rating data;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		OperationsUser cl_user = new OperationsUser();
		Poster poster;
		User user;
		try {
			conn = Service.getConnection();
			//подсчитываем количество записей
			if(view == 1) stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rating_poster");
			else stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rating_actors");
			rs = stmt.executeQuery();
			if(rs.next()) Service.CountAll=rs.getInt("count");
			rs.close();stmt.close();
			if(view == 1) stmt = conn.prepareStatement("SELECT id,id_poster,id_directory_performance,id_user,date,rating,review FROM rating_poster ORDER BY date DESC");
			else stmt = conn.prepareStatement("SELECT id,id_poster,id_worker,id_user,date,rating,review FROM rating_actors ORDER BY date DESC");
			rs = stmt.executeQuery();
			int count_unit = 0, count_pages = 1;
			while (rs.next()){
				if(count_pages == select_page) {
					data = new Rating();
					data.setId(rs.getInt("id"));
					poster = getPosterRating(conn, rs.getInt("id_poster"));
					user = cl_user.getData(rs.getInt("id_user"));
					data.setName_user(user.getName());
					data.setName_directory_performance(poster.getName_performance());
					data.setRating(rs.getInt("rating"));
					String review = rs.getString("review");
					if(!rs.wasNull()) data.setReview(review);
					else data.setReview(null);
					if(view == 2){
						Worker worker = Service.getWorker(rs.getInt("id_worker"));
						data.setName_actor(worker.getName() + " (" + worker.getName_position() + ")");
					}
					data.setDate(rs.getDate("date"));
					data.setDate_text(Service.getDateText(data.getDate()));
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

	//получение данных о постановке без лишних данных
	public Poster getPosterRating(Connection conn, int id_poster){
		Poster result = new Poster();
		Performance performance;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String temp;
		int id_directory_performance = 0;
		try {
			stmt = conn.prepareStatement("SELECT id_directory_performance,theater FROM poster WHERE id=?");
			stmt.setInt(1, id_poster);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_poster);
				id_directory_performance = rs.getInt("id_directory_performance");
				performance = ClUser.getPerformance(conn, id_directory_performance);
				temp = performance.getName();
				if(performance.getChildren() == 0) temp += "( взрослый)";
				else temp += " (детский)";
				result.setName_performance(temp);
				result.setTheater(rs.getInt("theater"));
			}
			rs.close();stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//удаление оценки пользователя
	//view = 1 - отзывы по постановкам, 2 - отзывы по актерам
	public int deleteRating(int view, int id_rating){
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int all_units = 0, all_pages = 0;
		try {
			conn = Service.getConnection();
			if(view == 1) stmt = conn.prepareStatement("DELETE FROM rating_poster WHERE id=?");
			else stmt = conn.prepareStatement("DELETE FROM rating_actors WHERE id=?");
			stmt.setInt(1, id_rating);
			stmt.execute();stmt.close();
			//определяем сколько страниц после удаления
			//подсчитываем количество записей
			if(view == 1) stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rating_poster");
			else stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM rating_actors");
			rs = stmt.executeQuery();
			if(rs.next()) all_units = rs.getInt("count");
			all_pages = (int) Math.ceil(all_units / (double) Service.NumberUnitsPage);
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return all_pages;
	}
}
