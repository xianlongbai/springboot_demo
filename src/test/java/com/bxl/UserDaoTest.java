package com.bxl;

import com.bxl.dao.UserDao;
import com.bxl.entity.*;
import com.bxl.factory.mybatisfactory.ExampleObjectFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 2018/9/19.
 */
public class UserDaoTest {


    @Test
    public void findUserById() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        User user = userMapper.findUserById(1);
        System.out.println("查询结果为年龄为："+user.getAge());

    }

    @Test
    public void saveUser() {
        SqlSession sqlSession = getSessionFactory().openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
//1、方式一
//        User user = new User();
//        user.setName("libai");
//        user.setAge(20);
//        user.setPassword("abcdef");
//        user.setDeleteFlag(1);
//        int count = userMapper.saveUser(user)；
//2、方式二
        Map<String,Object> map = new HashMap();
        map.put("name","习大大");
        map.put("password",789);
        map.put("age",52);
        map.put("deleteFlag",0);
        int count = userMapper.saveUserToMap(map);

        sqlSession.commit();
        System.out.println("ok");
    }


    //Mybatis 通过SqlSessionFactory获取SqlSession, 然后才能通过SqlSession与数据库进行交互
    private static SqlSessionFactory getSessionFactory() {
        SqlSessionFactory sessionFactory = null;
        String resource = "configuration.xml";
        try {
            sessionFactory = new SqlSessionFactoryBuilder().build(Resources
                    .getResourceAsReader(resource));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sessionFactory;
    }


    @Test
    public void testDefaultObjectFactory() {
        SqlSession sqlSession = getSessionFactory().openSession();

        ExampleObjectFactory e = new ExampleObjectFactory();
        User p = (User) e.create(User.class);
        System.out.println(p.getName());

    }


    //测试一对多
    @Test
    public void findCourseById() {
        SqlSessionFactory sqlSessionFactory = getSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        Student student = userMapper.findStudentById("20140101");
        List<Course> courseList = student.getCourseList();
        for (Course course: courseList) {
            System.out.println(course.getId() + "   " + course.getName());
        }
    }

    //测试多对一
    @Test
    public void findCourseByCid() {
        SqlSessionFactory sqlSessionFactory = getSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        Course course = userMapper.findCourseByCid(5);
        System.out.println(course.getStudent().getName());
    }

    //测试多对多
    @Test
    public void find多对多() {
        SqlSessionFactory sqlSessionFactory = getSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        List<Book> books = userMapper.queryAll();
        for (Book book : books) {
            System.out.print("["+book.getBname()+"]类型为：");
            for(Category category :book.getCategories()){
                System.out.print(category.getCname()+"、");
            }
            System.out.println("\n");
        }
    }


    @Test
    public void find多对多2() {
        SqlSessionFactory sqlSessionFactory = getSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserDao userMapper = sqlSession.getMapper(UserDao.class);
        Category category = userMapper.findCtegoryByCid(1);
        for (Book book : category.getBooks()) {
            System.out.println("书名："+book.getBname());
        }
    }





}
