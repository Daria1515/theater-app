package com.web.theater.structs;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО РАБОТНИКУ ТЕАТРА
public class Worker {
	private int id, id_position, age, on_change_image;
	private double rating;
	private String name_position, name, date_birth_text, description, rating_text, social_link, image_str;
	private Date date_birth;
	private MultipartFile image;

	//геттеры
	public int getId() {return id;}
	public int getId_position() {return id_position;}
	public String getName_position() {return name_position;}
	public String getName() {return name;}
	public String getDate_birth_text() {return date_birth_text;}
	public String getDescription() {return description;}
	public String getImage_str() {return image_str;}
	public int getAge() {return age;}
	public Date getDate_birth() {return date_birth;}
	public MultipartFile getImage() {return image;}
	public double getRating() {return rating;}
	public String getRating_text() {return rating_text;}
	public int getOn_change_image() {return on_change_image;}
	public String getSocial_link() {return social_link;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setId_position(int id_position) {this.id_position = id_position;}
	public void setName_position(String name_position) {this.name_position = name_position;}
	public void setName(String name) {this.name = name;}
	public void setDate_birth_text(String date_birth_text) {this.date_birth_text = date_birth_text;}
	public void setDescription(String description) {this.description = description;}
	public void setImage_str(String image_str) {this.image_str = image_str;}
	public void setAge(int age) {this.age = age;}
	public void setDate_birth(Date date_birth) {this.date_birth = date_birth;}
	public void setImage(MultipartFile image) {this.image = image;}
	public void setRating(double rating) {this.rating = rating;}
	public void setRating_text(String rating_text) {this.rating_text = rating_text;}
	public void setOn_change_image(int on_change_image) {this.on_change_image = on_change_image;}
	public void setSocial_link(String social_link) {this.social_link = social_link;}
}
