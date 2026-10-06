package com.campus.dao;

import com.campus.model.Department;
import com.campus.util.JPAUtil;

import jakarta.persistence.EntityManager;

import java.util.List;

public class DepartmentJPADAO {

    public List<Department> getAllDepartments() {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    "SELECT d FROM Department d " +
                    "ORDER BY d.name",
                    Department.class
            ).getResultList();

        } finally {

            em.close();
        }
    }


    public Department getDepartmentById(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.find(
                    Department.class,
                    id
            );

        } finally {

            em.close();
        }
    }
}