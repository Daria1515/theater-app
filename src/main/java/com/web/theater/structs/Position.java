package com.web.theater.structs;

import org.springframework.web.util.pattern.PathPattern;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО ДОЛЖНОСТИ
public class Position {
	private int id;
	String name, description;

	//геттеры
	public int getId() {return id;}
	public String getName() {return name;}
	public String getDescription() {return description;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setDescription(String description) {this.description = description;}
}
