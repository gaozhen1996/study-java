package com.gz.javastudy.springapp.dao.impl;

import com.gz.javastudy.springapp.dao.StudentDao;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * @author gaozhen
 * @title: StudentDao1
 * @projectName simple-spring
 * @description: studentdao非单例的实现
 * @date 2019-10-19 15:34
 */
@Repository
@Scope("prototype")
public class StudentDaoImpl1 implements StudentDao {

    @Override
    public void getStudentById(long id) {
        System.out.println("student1");
    }
}
