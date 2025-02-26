package com.web.theater.structs;

//КЛАСС ОПИСЫВАЮЩИЙ НА ДЕНЬ ПОСТАНОВКИ В АФИША
public class CalendarPoster {
	private int id_day;
	private String date_text, date_mysql, name_day;
	private int number_all_poster, number_children_poster, number_not_children_poster, new_stroke;

	//геттеры
	public int getId_day() {return id_day;}
	public String getDate_text() {return date_text;}
	public String getDate_mysql() {return date_mysql;}
	public String getName_day() {return name_day;}
	public int getNumber_all_poster() {return number_all_poster;}
	public int getNumber_children_poster() {return number_children_poster;}
	public int getNumber_not_children_poster() {return number_not_children_poster;}
	public int getNew_stroke() {return new_stroke;}

	//сеттеры
	public void setId_day(int id_day) {this.id_day = id_day;}
	public void setDate_text(String date_text) {this.date_text = date_text;}
	public void setDate_mysql(String date_mysql) {this.date_mysql = date_mysql;}
	public void setName_day(String name_day) {this.name_day = name_day;}
	public void setNumber_all_poster(int number_all_poster) {this.number_all_poster = number_all_poster;}
	public void setNumber_children_poster(int number_children_poster) {this.number_children_poster = number_children_poster;}
	public void setNumber_not_children_poster(int number_not_children_poster) {this.number_not_children_poster = number_not_children_poster;}
	public void setNew_stroke(int new_stroke) {this.new_stroke = new_stroke;}
}
