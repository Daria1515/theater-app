package com.web.theater.structs;

//КЛАСС ПОИСЫВАЮЩИЙ ДАННЫЕ ПО СТАТИСТИКЕ
public class Statistics {
	private String name, percent_text = "0.00";
	private int id, number;
	private double percent = 0;

	//геттеры
	public int getId() {return id;}
	public String getName() {return name;}
	public double getPercent() {return percent;}
	public String getPercent_text() {return percent_text;}
	public int getNumber() {return number;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setName(String name) {this.name = name;}
	public void setPercent(double percent) {this.percent = percent;}
	public void setPercent_text(String percent_text) {this.percent_text = percent_text;}
	public void setNumber(int number) {this.number = number;}
}
