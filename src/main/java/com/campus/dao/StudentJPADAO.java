package com.campus.dao;

import com.campus.model.Student;
import com.campus.util.JPAUtil;

import jakarta.persistence.EntityManager;
import java.util.List;

public class StudentJPADAO {
    //add method to add a student(persist)
    public void addStudent(Student student) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            em.getTransaction().begin();

            em.persist(student);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }

    //method to get student by id(find)
    public Student getStudentById(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.find(
                    Student.class,
                    id
            );

        } finally {

            em.close();
        }
    }

    //method to get all students(createQuery)
    public List<Student> getAllStudents() {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            return em.createQuery(
                    //HQL query to get all students
                    //HQL-"Hypertext Query Language"
                    //HQL is object oriented query language 
                    //s is alias name of Student class
                    "SELECT s FROM Student s",
                    Student.class
            ).getResultList();

        } finally {

            em.close();
        }
    }

    //method to update student(merge)
    public void updateStudent(Student student) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            em.getTransaction().begin();

            em.merge(student);

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }

    //method to delete student(remove)
    public void deleteStudent(int id) {

        EntityManager em =
                JPAUtil.getEntityManager();

        try {

            em.getTransaction().begin();

            Student student =
                    em.find(Student.class, id);

            if (student != null) {

                em.remove(student);
            }

            em.getTransaction().commit();

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();

        } finally {

            em.close();
        }
    }
}