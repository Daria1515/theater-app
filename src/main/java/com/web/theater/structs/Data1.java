package com.web.theater.structs;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ДОЛЖНОСТЯМ И РОЛЯМ СПЕКТАКЛЕЙ
public class Data1 {
	private int id;
	private String name, description;

	//геттеры
	public int getId() {return id;}
	public String getName() {return name;}
	public String getDescription() {return description;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setDescription(String description) {this.description = description;}
}