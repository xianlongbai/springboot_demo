package com.bxl.dao;

import com.bxl.entity.*;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 2018/9/19.
 */

public interface UserDao {

    public void insert(User user);

    public User findUserById (int userId);

    public List<User> findAllUsers();

    int saveUser(User user);

    Student findStudentById(String s);

    Course findCourseByCid(int s);

    int saveUserToMap(Map<String, Object> map);

    List<Book> queryAll();

    Category findCtegoryByCid(int i);
}
