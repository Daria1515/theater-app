package com.web.theater.structs;

import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;

//КЛАСС ОПИСЫВАЮЩИЙ НОВОСТЬ
public class News {
	private int id = 0, on_change_image1, on_change_image2, on_change_image3;
	private String title, description, date_text, image_str1, image_str2, image_str3;
	private Date date;
	private MultipartFile image1, image2, image3;

	//геттеры
	public int getId() {return id;}
	public String getTitle() {return title;}
	public String getDescription() {return description;}
	public String getDate_text() {return date_text;}
	public String getImage_str1() {return image_str1;}
	public String getImage_str2() {return image_str2;}
	public String getImage_str3() {return image_str3;}
	public Date getDate() {return date;}
	public MultipartFile getImage1() {return image1;}
	public MultipartFile getImage2() {return image2;}
	public MultipartFile getImage3() {return image3;}
	public int getOn_change_image1() {return on_change_image1;}
	public int getOn_change_image2() {return on_change_image2;}
	public int getOn_change_image3() {return on_change_image3;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setTitle(String title) {this.title = title;}
	public void setDescription(String description) {this.description = description;}
	public void setDate_text(String date_text) {this.date_text = date_text;}
	public void setImage_str1(String image_str1) {this.image_str1 = image_str1;}
	public void setImage_str2(String image_str2) {this.image_str2 = image_str2;}
	public void setImage_str3(String image_str3) {this.image_str3 = image_str3;}
	public void setDate(Date date) {this.date = date;}
	public void setImage1(MultipartFile image1) {this.image1 = image1;}
	public void setImage2(MultipartFile image2) {this.image2 = image2;}
	public void setImage3(MultipartFile image3) {this.image3 = image3;}
	public void setOn_change_image1(int on_change_image1) {this.on_change_image1 = on_change_image1;}
	public void setOn_change_image2(int on_change_image2) {this.on_change_image2 = on_change_image2;}
	public void setOn_change_image3(int on_change_image3) {this.on_change_image3 = on_change_image3;}
}
