package com.web.theater.structs.list_pages_struct;

import java.util.ArrayList;
import java.util.List;

//класс описывающий всю таблицу перехода по страницам
public class TablePagesStruct {
    private List<PageStruct> list_pages;

    //геттеры
    public List<PageStruct> getList_pages() {return list_pages;}

    //сеттеры
    public void setList_pages(List<PageStruct> list_pages) {
        this.list_pages=new ArrayList<>();
        for(int i=0;i<list_pages.size();i++){
            PageStruct data=list_pages.get(i);
            this.list_pages.add(data);
        }
    }
}
