package com.web.theater;

import com.web.theater.structs.Position;
import com.web.theater.structs.Worker;
import com.web.theater.structs.list_pages_struct.PageStruct;
import com.web.theater.structs.list_pages_struct.TablePagesStruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

//КЛАСС ПО ОБЩИМ СЕРВИСНЫМ ОПЕРАЦИЯМ
public class Service {
	public static int NumberPages;//сколько всего страниц при выборке данных
	public static int NumberUnitsPage=10;//сколько данных отображать на одной странице
	public static int NumberRow=20;//сколько номеров страниц отображать в один ряд
	public static int CountAll;//общее число данных при поиске
	public static int NumberDaysStroke = 7;//количество в строке дней в афише
	public static int NumberIndex = 5;//сколько анонсов и новостей выводить на главной странице

	//подключение к серверу MySQL
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName(ApplicationProperties.getProperty(ApplicationProperties.DRIVER_SQL));
		return DriverManager.getConnection(ApplicationProperties.getProperty(ApplicationProperties.CONNECTION_STRING_SQL),
				ApplicationProperties.getProperty(ApplicationProperties.USER_SQL),
				ApplicationProperties.getProperty(ApplicationProperties.PASSWORD_SQL));
	}

	static String secretKey = "SECRETKEYSECRETKEYSECRETKEYSECRETKEYSECRETKEY123456890SECRETKEYSECRETKEY";//секретный ключ
	//шифрование текста
	public static String encrypt(String plainText) {
		StringBuffer encryptedString = new StringBuffer();
		for(int i = 0; i < plainText.length(); ++i) {
			int plainTextInt = plainText.charAt(i) - 32;
			int secretKeyInt = secretKey.charAt(i) - 32;
			int encryptedInt = (plainTextInt + secretKeyInt) % 93;
			encryptedString.append((char)(encryptedInt + 32));
		}
		return encryptedString.toString();
	}

	//дешифрование текста
	public static String decrypt(String decryptedText) {
		StringBuffer decryptedString = new StringBuffer();
		for(int i = 0; i < decryptedText.length(); ++i) {
			int decryptedTextInt = decryptedText.charAt(i) - 32;
			int secretKeyInt = secretKey.charAt(i) - 32;
			int decryptedInt = decryptedTextInt - secretKeyInt;
			if (decryptedInt < 1) decryptedInt += 93;
			decryptedString.append((char)(decryptedInt + 32));
		}
		return decryptedString.toString();
	}

	//проверка на нецензурные слова в сообщении, false - таких слов нет, true - такие слова присутствуют
	public static boolean checkWarningWorlds(String input) throws IOException {
		List<String> list_input_words;//список слов входного текста
		List<String> list_warning_words;
		String word="", result_word="";
		list_input_words=new ArrayList<>();
		list_warning_words=new ArrayList<>();
		//читаем нецензурные слова из текстового файла в массив
		Resource resource = new ClassPathResource("/static/WarningWords.txt");
		File file = resource.getFile();
		BufferedReader buf = new BufferedReader(new FileReader(file));
		while((word = buf.readLine()) != null)list_warning_words.add(word);
		buf.close();
		word="";
		//формируем слова входного текста в список
		for(int i=0;i<input.length();i++){
			char s=input.charAt(i);
			if(!Character.isLetter(s) || i==input.length()-1){
				if(i==input.length()-1)word+=s;
				//убираем в слове все символы не буквы
				result_word="";
				for(int j=0;j<word.length();j++){
					s=word.charAt(j);
					if(Character.isLetter(s))result_word+=Character.toString(s);
				}
				if(result_word.length()>0)list_input_words.add(result_word);
				word="";
			}else word+=s;
		}
		//проверяем входной текст на нецензурные слова
		for(int i=0;i<list_input_words.size();i++)
			for(int j=0;j<list_warning_words.size();j++)
				if(search1(list_input_words.get(i), list_warning_words.get(j)))return true;
		return false;
	}

	//поиск по совпадению слова, true - совпадает, false - не совпадает
	public static boolean search1(String s1, String s2) {
		if (s1.length() != s2.length())return false;
		s1=s1.toLowerCase();s2=s2.toLowerCase();
		if(s1.equals(s2))return true;
		else return false;
	}

	//проверка на существование сессии
	public static boolean checkSession(HttpSession session) {
		if (session.getAttribute("UserStatus") == null) {
			return false;
		} else {
			return !session.getAttribute("UserStatus").toString().equals("");
		}
	}

	//преобразование Calendar Date в DateMySQL в формате даты и времени
	public static String convertCalendarDateTimeToStringMySQL(Calendar date){
		String result = "";
		int year, month, day, hours, minutes;
		year = date.get(Calendar.YEAR);
		month = date.get(Calendar.MONTH) + 1;
		day = date.get(Calendar.DAY_OF_MONTH);
		hours = date.get(Calendar.HOUR_OF_DAY);
		minutes = date.get(Calendar.MINUTE);
		result = Integer.toString(year) + "-";
		if(month < 10) result += "0";
		result += Integer.toString(month) + "-";
		if(day < 10) result += "0";
		result += Integer.toString(day) + " ";
		if(hours < 10) result += "0";
		result += Integer.toString(hours) + ":";
		if(minutes < 10) result += "0";
		result += Integer.toString(minutes) + ":00";
		return result;
	}

	//преобразование Calendar Date в DateMySQL в формате даты
	public static String convertCalendarDateToStringMySQL(Calendar date){
		String result = "";
		int year, month, day, hours, minutes;
		year = date.get(Calendar.YEAR);
		month = date.get(Calendar.MONTH) + 1;
		day = date.get(Calendar.DAY_OF_MONTH);
		hours = date.get(Calendar.HOUR_OF_DAY);
		minutes = date.get(Calendar.MINUTE);
		result = Integer.toString(year) + "-";
		if(month < 10) result += "0";
		result += Integer.toString(month) + "-";
		if(day < 10) result += "0";
		result += Integer.toString(day);
		return result;
	}

	//получаем дату из mysql в нормальную дату - день, наименование месяца, год
	public static String convertDateMySQLToDateString(String date_mysql){
		String result = "", year, month, day;
		year = date_mysql.substring(0, 4);
		month = date_mysql.substring(5, 7);
		day = date_mysql.substring(8, 10);
		result = day + " " + getNameMonth(Integer.parseInt(month)) + " " + year;
		return result;
	}

	//получаем дату и время из mysql в нормальную дату и время - день, наименование месяца, год, часы:минуты
	public static String convertDateMySQLToDateTimeString(String date_mysql){
		String result = "", year, month, day, hours, minutes;
		year = date_mysql.substring(0, 4);
		month = date_mysql.substring(5, 7);
		day = date_mysql.substring(8, 10);
		hours = date_mysql.substring(11, 13);
		minutes = date_mysql.substring(14, 16);
		result = day + " " + getNameMonth(Integer.parseInt(month)) + " " + year + " " + hours + ":" + minutes;
		return result;
	}

	//получаем наименование месяца
	public static String getNameMonth(int number_month){
		String result = "";
		switch (number_month){
			case 1:
				result = "Январь";
				break;
			case 2:
				result = "Февраль";
				break;
			case 3:
				result = "Март";
				break;
			case 4:
				result = "Апрель";
				break;
			case 5:
				result = "Май";
				break;
			case 6:
				result = "Июнь";
				break;
			case 7:
				result = "Июль";
				break;
			case 8:
				result = "Август";
				break;
			case 9:
				result = "Сентябрь";
				break;
			case 10:
				result = "Октябрь";
				break;
			case 11:
				result = "Ноябрь";
				break;
			case 12:
				result = "Декабрь";
				break;
		}
		return result;
	}

	//определяем день недели
	//0 - воскресенье, 1 - понедельник и так по возрастающей
	public static String getDayNameWeek(int number_day){
		String result = "";
		switch (number_day){
			case 0:
				result = "Воскресенье";
				break;
			case 1:
				result = "Понедельник";
				break;
			case 2:
				result = "Вторник";
				break;
			case 3:
				result = "Среда";
				break;
			case 4:
				result = "Четверг";
				break;
			case 5:
				result = "Пятница";
				break;
			case 6:
				result = "Суббота";
				break;

			default:
				throw new IllegalStateException("Unexpected value: " + number_day);
		}
		return result;
	}

	//преобразование blob в string64 для отображения фото
	public static String convertBlobToString64(Blob image_blob) throws SQLException {
		byte[] bytes_img;
		String photo64 = null;
		if(image_blob!=null) {
			bytes_img = image_blob.getBytes(1, (int) image_blob.length());
			photo64=new String(Base64.getEncoder().encode(bytes_img));
		}
		return photo64;
	}

	//получение даты в текстовом формате (конвертация даты SQL в текст)
	public static String getDateText(Date date){
		String result = "";
		if(date!=null) {
			int year, month, day;
			LocalDate localDate = date.toLocalDate();
			day = localDate.getDayOfMonth();
			month = localDate.getMonthValue();
			year = localDate.getYear();
			if (day < 10) result = "0" + Integer.toString(day) + ".";
			else result += Integer.toString(day) + ".";
			if (month < 10) result += "0" + Integer.toString(month) + ".";
			else result += Integer.toString(month) + ".";
			result += Integer.toString(year);
		}
		return result;
	}

	//получение возраста человека по дате рождения
	public static int getAge(Date date) {
		int result=0;
		if(date!=null) {
			LocalDate date_birth = date.toLocalDate();
			LocalDate date_now = LocalDate.now();
			result = date_now.getYear() - date_birth.getYear();
			if (date_now.getDayOfMonth() < date_birth.getDayOfMonth() && date_now.getMonthValue() <= date_birth.getMonthValue())
				--result;
		}
		return result;
	}

	//формирование структуры списка перехода по страницам на сайте
	public static List<TablePagesStruct> getListPages(int select_page){
		List<TablePagesStruct> result=new ArrayList<>();
		int count_pages_row=0;
		PageStruct page_struct;
		List<PageStruct> list_pages_row=new ArrayList<>();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			NumberPages=(int) Math.ceil(CountAll / (double) NumberUnitsPage);
			if(select_page > NumberPages) select_page = NumberPages;
			for(int i=0;i<NumberPages;i++){
				page_struct=new PageStruct();
				page_struct.setNumber(i+1);
				if(i+1==select_page)page_struct.setSelect(1);
				else page_struct.setSelect(0);
				list_pages_row.add(page_struct);
				count_pages_row++;
				if(count_pages_row==NumberRow || i==NumberPages-1){
					count_pages_row=0;
					TablePagesStruct data=new TablePagesStruct();
					data.setList_pages(list_pages_row);
					list_pages_row.clear();
					result.add(data);
				}
			}
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение настройки
	public static String getSetting(String name){
		String result = "";
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("SELECT value FROM settings WHERE name=?");
			stmt.setString(1, name);
			rs = stmt.executeQuery();
			if(rs.next()) result = rs.getString("value");
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//установка настройки
	public static void setSetting(String name, String value){
		Connection conn;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("UPDATE settings SET value=? WHERE name=?");
			stmt.setString(1, value);
			stmt.setString(2, name);
			stmt.execute();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//получение id пользователя по логину
	public static int getIdUserLogin(String login){
		int result = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("SELECT id FROM users WHERE login=?");
			stmt.setString(1, login);
			rs = stmt.executeQuery();
			if(rs.next()) result = rs.getInt("id");
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение данных по должности
	public static Position getPosition(int id_position){
		Position result = new Position();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			//проверяем в пациентах
			stmt = conn.prepareStatement("SELECT name,description FROM positions WHERE id=?");
			stmt.setInt(1, id_position);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_position);
				result.setName(rs.getString("name"));
				result.setDescription(rs.getString("description"));
			}
			rs.close();stmt.close();
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	//получение рейтинга сотрудника театра
	public static double getRatingWorker(int id_worker){
		double result = 0;
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		double sum = 0;
		int count = 0;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT rating FROM rating_actors WHERE id_worker=?");
			stmt.setInt(1, id_worker);
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

	//получение данных по сотруднику
	public static Worker getWorker(int id_worker){
		Worker result = new Worker();
		Connection conn;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement("SELECT id_position,name,date_birth,description,social_link,image FROM workers WHERE id=?");
			stmt.setInt(1, id_worker);
			rs = stmt.executeQuery();
			if(rs.next()){
				result.setId(id_worker);
				result.setId_position(rs.getInt("id_position"));
				result.setName_position(Service.getPosition(result.getId_position()).getName());
				result.setName(rs.getString("name"));
				result.setDate_birth(rs.getDate("date_birth"));
				result.setDate_birth_text(getDateText(result.getDate_birth()));
				result.setAge(Service.getAge(result.getDate_birth()));
				result.setDescription(rs.getString("description"));
				result.setSocial_link(rs.getString("social_link"));
				result.setRating(Service.getRatingWorker(result.getId()));
				result.setRating_text(String.format("%.2f", result.getRating()));
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
}
