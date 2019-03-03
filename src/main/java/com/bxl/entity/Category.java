package com.bxl.entity;

import java.util.List;

/**
 * Created by root on 2018/9/25.
 */
public class Category {

    private int cid;
    private String cname;


    private List<Book> books;


    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
