package com.web.theater.structs;

import java.sql.Date;

//КЛАСС ОПИСЫВАЮЩИЙ ДАННЫЕ ПО ГОСТЕВОЙ КНИГЕ
public class Guestbook {
	private int id, id_user, oper;
	private String name_user, title, question, date_question_text, answer, date_answer_text;
	private Date date_question, date_answer;
	private boolean flag_answer;//true - ответ есть, false - ответа нет

	//геттеры
	public int getId() {return id;}
	public int getId_user() {return id_user;}
	public String getName_user() {return name_user;}
	public String getTitle() {return title;}
	public String getQuestion() {return question;}
	public String getDate_question_text() {return date_question_text;}
	public String getAnswer() {return answer;}
	public String getDate_answer_text() {return date_answer_text;}
	public Date getDate_question() {return date_question;}
	public Date getDate_answer() {return date_answer;}
	public boolean isFlag_answer() {return flag_answer;}
	public int getOper() {return oper;}

	//геттеры
	public void setId(int id) {this.id = id;}
	public void setId_user(int id_user) {this.id_user = id_user;}
	public void setName_user(String name_user) {this.name_user = name_user;}
	public void setTitle(String title) {this.title = title;}
	public void setQuestion(String question) {this.question = question;}
	public void setDate_question_text(String date_question_text) {this.date_question_text = date_question_text;}
	public void setAnswer(String answer) {this.answer = answer;}
	public void setDate_answer_text(String date_answer_text) {this.date_answer_text = date_answer_text;}
	public void setDate_question(Date date_question) {this.date_question = date_question;}
	public void setDate_answer(Date date_answer) {this.date_answer = date_answer;}
	public void setFlag_answer(boolean flag_answer) {this.flag_answer = flag_answer;}
	public void setOper(int oper) {this.oper = oper;}
}
