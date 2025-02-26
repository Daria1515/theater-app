package com.web.theater.structs;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО СПЕКТАКЛЮ
public class Performance {
	private int id, age, children, on_change_image1, on_change_image2, on_change_image3, on_change_image4, on_change_image5;
	private String name, description, rating_text, list_text_positions, list_text_roles, image_str1, image_str2, image_str3, image_str4, image_str5;
	private List<Data1> list_positions;
	private List<Data1> list_roles;
	double rating;
	private MultipartFile image1, image2, image3, image4, image5;

	//геттеры
	public int getId() {return id;}
	public int getAge() {return age;}
	public int getChildren() {return children;}
	public String getName() {return name;}
	public String getDescription() {return description;}
	public String getImage_str1() {return image_str1;}
	public String getImage_str2() {return image_str2;}
	public String getImage_str3() {return image_str3;}
	public String getImage_str4() {return image_str4;}
	public String getImage_str5() {return image_str5;}
	public MultipartFile getImage1() {return image1;}
	public MultipartFile getImage2() {return image2;}
	public MultipartFile getImage3() {return image3;}
	public MultipartFile getImage4() {return image4;}
	public MultipartFile getImage5() {return image5;}
	public List<Data1> getList_positions() {return list_positions;}
	public List<Data1> getList_roles() {return list_roles;}
	public String getList_text_positions() {return list_text_positions;}
	public String getList_text_roles() {return list_text_roles;}
	public int getOn_change_image1() {return on_change_image1;}
	public int getOn_change_image2() {return on_change_image2;}
	public int getOn_change_image3() {return on_change_image3;}
	public int getOn_change_image4() {return on_change_image4;}
	public int getOn_change_image5() {return on_change_image5;}
	public double getRating() {return rating;}
	public String getRating_text() {return rating_text;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setAge(int age) {this.age = age;}
	public void setChildren(int children) {this.children = children;}
	public void setName(String name) {this.name = name;}
	public void setDescription(String description) {this.description = description;}
	public void setImage1(MultipartFile image1) {this.image1 = image1;}
	public void setImage2(MultipartFile image2) {this.image2 = image2;}
	public void setImage3(MultipartFile image3) {this.image3 = image3;}
	public void setImage4(MultipartFile image4) {this.image4 = image4;}
	public void setImage5(MultipartFile image5) {this.image5 = image5;}
	public void setImage_str1(String image_str1) {this.image_str1 = image_str1;}
	public void setImage_str2(String image_str2) {this.image_str2 = image_str2;}
	public void setImage_str3(String image_str3) {this.image_str3 = image_str3;}
	public void setImage_str4(String image_str4) {this.image_str4 = image_str4;}
	public void setImage_str5(String image_str5) {this.image_str5 = image_str5;}
	public void setList_roles(List<Data1> list_roles) {this.list_roles = list_roles;}
	public void setList_positions(List<Data1> list_positions) {this.list_positions = list_positions;}
	public void setList_text_positions(String list_text_positions) {this.list_text_positions = list_text_positions;}
	public void setList_text_roles(String list_text_roles) {this.list_text_roles = list_text_roles;}
	public void setOn_change_image1(int on_change_image1) {this.on_change_image1 = on_change_image1;}
	public void setOn_change_image2(int on_change_image2) {this.on_change_image2 = on_change_image2;}
	public void setOn_change_image3(int on_change_image3) {this.on_change_image3 = on_change_image3;}
	public void setOn_change_image4(int on_change_image4) {this.on_change_image4 = on_change_image4;}
	public void setOn_change_image5(int on_change_image5) {this.on_change_image5 = on_change_image5;}
	public void setRating(double rating) {this.rating = rating;}
	public void setRating_text(String rating_text) {this.rating_text = rating_text;}
}
