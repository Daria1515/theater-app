package com.web.theater.structs;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО ПОСТАНОВКЕ В РАСПИСАНИИ АФИШЫ
public class Poster {
	private int id, id_directory_performance, minutes, price_max, theater, free_tickets;
	private String date_time, description, list_text_positions, list_text_roles, price_ticket, name_performance, about_performance, image_str;
	private LocalDateTime date_time_local;
	private Timestamp date_time_start;
	private List<Data2> list_positions, list_roles;

	//геттеры
	public int getId() {return id;}
	public int getId_directory_performance() {return id_directory_performance;}
	public int getMinutes() {return minutes;}
	public int getPrice_max() {return price_max;}
	public int getTheater() {return theater;}
	public String getDescription() {return description;}
	public String getList_text_positions() {return list_text_positions;}
	public String getList_text_roles() {return list_text_roles;}
	public LocalDateTime getDate_time_local() {return date_time_local;}
	public String getDate_time() {return date_time;}
	public String getPrice_ticket() {return price_ticket;}
	public int getFree_tickets() {return free_tickets;}
	public String getName_performance() {return name_performance;}
	public String getAbout_performance() {return about_performance;}
	public List<Data2> getList_positions() {return list_positions;}
	public List<Data2> getList_roles() {return list_roles;}
	public Timestamp getDate_time_start() {return date_time_start;}
	public String getImage_str() {return image_str;}

	//сеттеры
	public void setId(int id) {this.id = id;}
	public void setId_directory_performance(int id_directory_performance) {this.id_directory_performance = id_directory_performance;}
	public void setMinutes(int minutes) {this.minutes = minutes;}
	public void setPrice_max(int price_max) {this.price_max = price_max;}
	public void setTheater(int theater) {this.theater = theater;}
	public void setDescription(String description) {this.description = description;}
	public void setList_text_positions(String list_text_positions) {this.list_text_positions = list_text_positions;}
	public void setList_text_roles(String list_text_roles) {this.list_text_roles = list_text_roles;}
	public void setDate_time_local(LocalDateTime date_time_local) {this.date_time_local = date_time_local;}
	public void setDate_time(String date_time) {this.date_time = date_time;}
	public void setPrice_ticket(String price_ticket) {this.price_ticket = price_ticket;}
	public void setFree_tickets(int free_tickets) {this.free_tickets = free_tickets;}
	public void setName_performance(String name_performance) {this.name_performance = name_performance;}
	public void setAbout_performance(String about_performance) {this.about_performance = about_performance;}
	public void setList_positions(List<Data2> list_positions) {this.list_positions = list_positions;}
	public void setList_roles(List<Data2> list_roles) {this.list_roles = list_roles;}
	public void setDate_time_start(Timestamp date_time_start) {this.date_time_start = date_time_start;}
	public void setImage_str(String image_str) {this.image_str = image_str;}
}
