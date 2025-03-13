//package com.epamlab.gymcrm.trainee.dao;
//
//import com.epamlab.gymcrm.trainee.model.Trainee;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class TraineeDao {
//
//    private final SessionFactory sessionFactory;
//
//
//    @Autowired
//    public TraineeDao(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public void save(Trainee trainee) {
//        Transaction transaction = null;
//        try (Session session = sessionFactory.openSession()) {
//            transaction = session.beginTransaction();
//            session.persist(trainee);
//            transaction.commit();
//        } catch (Exception e) {
//            if (transaction != null) transaction.rollback();
//            throw e;
//        }
//    }
//
//    public Trainee findById(Long id) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.get(Trainee.class, id);
//        }
//    }
//
//    public Trainee findByUsername(String username) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Trainee WHERE username = :username", Trainee.class)
//                    .setParameter("username", username)
//                    .uniqueResult();
//        }
//    }
//
//    public void update(Trainee trainee) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.merge(trainee);
//            transaction.commit();
//        }
//    }
//
//    public void delete(Trainee trainee) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.remove(trainee);
//            transaction.commit();
//        }
//    }
//
//    public List<Trainee> findAll() {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Trainee", Trainee.class).list();
//        }
//    }
//}