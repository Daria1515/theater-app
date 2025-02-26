package com.web.theater.structs.list_pages_struct;
//класс описывающий данные по разделу страницы
public class PageStruct {
    private int number;//номер страницы
    private int select;//выделение страницы (которая выбрана)

    //геттеры
    public int getNumber(){return number;}
    public int getSelect() {return select;}

    //сеттеры
    public void setNumber(int number) {this.number = number;}
    public void setSelect(int select) {this.select = select;}
}
