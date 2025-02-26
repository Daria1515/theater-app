package com.web.theater.structs;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО НАСТРОЙКАМ АДМИНИСТРАТОРА
public class Settings {
	private String password;
	private int number_rows, number_places_row;//количество рядов и мест в ряде
	private int number_rows_price, percent_price;//сколько рядов по определенной цене и процент снижения цены для последующих рядов

	//геттеры
	public String getPassword() {return password;}
	public int getNumber_rows() {return number_rows;}
	public int getNumber_places_row() {return number_places_row;}
	public int getNumber_rows_price() {return number_rows_price;}
	public int getPercent_price() {return percent_price;}

	//сеттеры
	public void setPassword(String password) {this.password = password;}
	public void setNumber_rows(int number_rows) {this.number_rows = number_rows;}
	public void setNumber_places_row(int number_places_row) {this.number_places_row = number_places_row;}
	public void setNumber_rows_price(int number_rows_price) {this.number_rows_price = number_rows_price;}
	public void setPercent_price(int percent_price) {this.percent_price = percent_price;}
}
