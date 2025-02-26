package com.web.theater.structs;

import java.sql.Date;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО ОЦЕНКЕ
public class Rating {
	private int id, rating;
	private String review, date_text, name_user, name_actor, name_directory_performance, date_time_poster;
	private Date date;

	//геттеры
	public int getId() {return id;}
	public int getRating() {return rating;}
	public String getReview() {return review;}
	public String getDate_text() {return date_text;}
	public String getName_user() {return name_user;}
	public String getName_actor() {return name_actor;}
	public String getDate_time_poster() {return date_time_poster;}
	public Date getDate() {return date;}
	public String getName_directory_performance() {return name_directory_performance;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setRating(int rating) {this.rating = rating;}
	public void setReview(String review) {this.review = review;}
	public void setDate_text(String date_text) {this.date_text = date_text;}
	public void setName_user(String name_user) {this.name_user = name_user;}
	public void setName_actor(String name_actor) {this.name_actor = name_actor;}
	public void setDate_time_poster(String date_time_poster) {this.date_time_poster = date_time_poster;}
	public void setDate(Date date) {this.date = date;}
	public void setName_directory_performance(String name_directory_performance) {this.name_directory_performance = name_directory_performance;}
}
