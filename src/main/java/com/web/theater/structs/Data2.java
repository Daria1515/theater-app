package com.web.theater.structs;

import java.sql.Date;

//КЛАСС ПОИСЫВАЮЩИЙ ДАННЫЕ ПО ДОЛЖНОСТЯМ И АКТЕРАМ АФИШЫ
public class Data2 {
	private int id_position, id_person;
	private String name_position, name_person, social_link, rating_text = "0.00", review = "", date_text = "";
	private double rating = 0;
	private Date date;

	//геттеры
	public int getId_position() {return id_position;}
	public int getId_person() {return id_person;}
	public String getName_position() {return name_position;}
	public String getName_person() {return name_person;}
	public String getSocial_link() {return social_link;}
	public double getRating() {return rating;}
	public String getRating_text() {return rating_text;}
	public String getReview() {return review;}
	public Date getDate() {return date;}
	public String getDate_text() {return date_text;}

	//сеттеры
	public void setId_position(int id_position) {this.id_position = id_position;}
	public void setId_person(int id_person) {this.id_person = id_person;}
	public void setName_position(String name_position) {this.name_position = name_position;}
	public void setName_person(String name_person) {this.name_person = name_person;}
	public void setSocial_link(String social_link) {this.social_link = social_link;}
	public void setRating(double rating) {this.rating = rating;}
	public void setRating_text(String rating_text) {this.rating_text = rating_text;}
	public void setReview(String review) {this.review = review;}
	public void setDate(Date date) {this.date = date;}
	public void setDate_text(String date_text) {this.date_text = date_text;}
}
