package com.web.theater.structs;

import java.sql.Timestamp;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО БИЛЕТУ
//flag_set_rating - 0 = рейтинг нельзя установить (для будущих постановок), 1 - рейтинг можно добавить (еще нет), 2 = рейтинг можно изменить (уже есть)
//flag_delete - 0 - удалить нельзя (постановка еще будет), 1 - удалить можно (постановка уже прошла)
public class Ticket {
	private int id, id_poster, id_directory_performance, row, place, price, flag_set_rating, flag_delete;
	private String name_performance, start_time_poster, method_pay, date_time_pay, date_time_start_poster, image_str_number_code;
	private Timestamp date_time_poster;

	//геттеры
	public int getId() {return id;}
	public int getId_poster() {return id_poster;}
	public int getId_directory_performance() {return id_directory_performance;}
	public int getRow() {return row;}
	public int getPlace() {return place;}
	public int getPrice() {return price;}
	public String getName_performance() {return name_performance;}
	public String getStart_time_poster() {return start_time_poster;}
	public String getMethod_pay() {return method_pay;}
	public String getDate_time_pay() {return date_time_pay;}
	public String getDate_time_start_poster() {return date_time_start_poster;}
	public Timestamp getDate_time_poster() {return date_time_poster;}
	public int getFlag_set_rating() {return flag_set_rating;}
	public int getFlag_delete() {return flag_delete;}
	public String getImage_str_number_code() {return image_str_number_code;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setId_poster(int id_poster) {this.id_poster = id_poster;}
	public void setId_directory_performance(int id_directory_performance) {this.id_directory_performance = id_directory_performance;}
	public void setRow(int row) {this.row = row;}
	public void setPlace(int place) {this.place = place;}
	public void setPrice(int price) {this.price = price;}
	public void setName_performance(String name_performance) {this.name_performance = name_performance;}
	public void setStart_time_poster(String start_time_poster) {this.start_time_poster = start_time_poster;}
	public void setMethod_pay(String method_pay) {this.method_pay = method_pay;}
	public void setDate_time_pay(String date_time_pay) {this.date_time_pay = date_time_pay;}
	public void setDate_time_start_poster(String date_time_start_poster) {this.date_time_start_poster = date_time_start_poster;}
	public void setDate_time_poster(Timestamp date_time_poster) {this.date_time_poster = date_time_poster;}
	public void setFlag_set_rating(int flag_set_rating) {this.flag_set_rating = flag_set_rating;}
	public void setFlag_delete(int flag_delete) {this.flag_delete = flag_delete;}
	public void setImage_str_number_code(String image_str_number_code) {this.image_str_number_code = image_str_number_code;}
}
