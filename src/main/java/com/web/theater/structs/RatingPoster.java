package com.web.theater.structs;

import java.sql.Date;
import java.util.List;

//КЛАСС ОПИСЫВАЮЩИЙ РЕЙТИНГ И ОТЗЫВ ПО ПОСТАНОВКЕ И АКТЕРАМ В ПОСТАНОВКЕ ПОЛЬЗОВАТЕЛЯ
public class RatingPoster {
	private int id, id_poster, id_directory_performance, rating = 4, theater, oper;
	private String name_performance, date_text, review, list_text_positions, list_text_roles;
	private Date date;
	private List<Data2> list_positions, list_roles;

	//геттеры
	public int getId() {return id;}
	public int getId_poster() {return id_poster;}
	public int getId_directory_performance() {return id_directory_performance;}
	public int getRating() {return rating;}
	public String getName_performance() {return name_performance;}
	public String getDate_text() {return date_text;}
	public String getReview() {return review;}
	public Date getDate() {return date;}
	public String getList_text_positions() {return list_text_positions;}
	public String getList_text_roles() {return list_text_roles;}
	public List<Data2> getList_positions() {return list_positions;}
	public List<Data2> getList_roles() {return list_roles;}
	public int getTheater() {return theater;}
	public int getOper() {return oper;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setId_poster(int id_poster) {this.id_poster = id_poster;}
	public void setId_directory_performance(int id_directory_performance) {this.id_directory_performance = id_directory_performance;}
	public void setRating(int rating) {this.rating = rating;}
	public void setName_performance(String name_performance) {this.name_performance = name_performance;}
	public void setDate_text(String date_text) {this.date_text = date_text;}
	public void setReview(String review) {this.review = review;}
	public void setDate(Date date) {this.date = date;}
	public void setList_text_positions(String list_text_positions) {this.list_text_positions = list_text_positions;}
	public void setList_text_roles(String list_text_roles) {this.list_text_roles = list_text_roles;}
	public void setList_positions(List<Data2> list_positions) {this.list_positions = list_positions;}
	public void setList_roles(List<Data2> list_roles) {this.list_roles = list_roles;}
	public void setTheater(int theater) {this.theater = theater;}
	public void setOper(int oper) {this.oper = oper;}
}
