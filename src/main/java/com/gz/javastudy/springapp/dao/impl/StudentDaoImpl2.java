package com.gz.javastudy.springapp.dao.impl;

import com.gz.javastudy.springapp.dao.StudentDao;
import org.springframework.stereotype.Repository;

/**
 * @author gaozhen
 * @title: StudentDao1
 * @projectName simple-spring
 * @description: studentdao单例的实现
 * @date 2019-10-1915:34
 */
@Repository

public class StudentDaoImpl2 implements StudentDao {

    @Override
    public void getStudentById(long id) {
        System.out.println("student2");
    }
}
