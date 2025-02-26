package com.web.theater.structs;

import org.springframework.web.multipart.MultipartFile;
import java.sql.Date;

//КЛАСС ОПИСЫВАЮЩИЙ ПОЛЬЗОВАТЕЛЕЙ
public class User {
	private int id, age, on_change_image;
	private String name, email, login, password, repeat_password, image_str, date_birth_text;
	private Date date_birth;
	private MultipartFile image;

	//геттеры
	public int getId() {return id;}
	public int getAge() {return age;}
	public int getOn_change_image() {return on_change_image;}
	public String getName() {return name;}
	public String getEmail() {return email;}
	public String getLogin() {return login;}
	public String getPassword() {return password;}
	public String getRepeat_password() {return repeat_password;}
	public String getImage_str() {return image_str;}
	public Date getDate_birth() {return date_birth;}
	public String getDate_birth_text() {return date_birth_text;}
	public MultipartFile getImage() {return image;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setAge(int age) {this.age = age;}
	public void setOn_change_image(int on_change_image) {this.on_change_image = on_change_image;}
	public void setName(String name) {this.name = name;}
	public void setEmail(String email) {this.email = email;}
	public void setLogin(String login) {this.login = login;}
	public void setPassword(String password) {this.password = password;}
	public void setRepeat_password(String repeat_password) {this.repeat_password = repeat_password;}
	public void setImage_str(String image_str) {this.image_str = image_str;}
	public void setDate_birth(Date date_birth) {this.date_birth = date_birth;}
	public void setDate_birth_text(String date_birth_text) {this.date_birth_text = date_birth_text;}
	public void setImage(MultipartFile image) {this.image = image;}
}
