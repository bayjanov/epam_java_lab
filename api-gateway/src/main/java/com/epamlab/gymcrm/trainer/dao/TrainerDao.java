//package com.epamlab.gymcrm.trainer.dao;
//
//import com.epamlab.gymcrm.trainer.model.Trainer;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class TrainerDao {
//
//    private final SessionFactory sessionFactory;
//
//
//
//    @Autowired
//    public TrainerDao(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public void save(Trainer trainer) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.persist(trainer);
//            transaction.commit();
//        }
//    }
//
//    public Trainer findById(Long id) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.get(Trainer.class, id);
//        }
//    }
//
//    public Trainer findByUsername(String username) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Trainer WHERE username = :username", Trainer.class)
//                    .setParameter("username", username)
//                    .uniqueResult();
//        }
//    }
//
//    public List<Trainer> findBySpecialization(String specialization) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                    "FROM Trainer t WHERE t.specialization = :specialization", Trainer.class)
//                    .setParameter("specialization", specialization)
//                    .list();
//        }
//    }
//
//    public List<Trainer> findUnassignedTrainersForTrainee(Long traineeId) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "SELECT t FROM Trainer t WHERE t.id NOT IN " +
//                                    "(SELECT tr.id FROM Trainee tr JOIN tr.trainers assignedTrainers WHERE tr.id = :traineeId)", Trainer.class)
//                    .setParameter("traineeId", traineeId)
//                    .list();
//        }
//    }
//
//
//    public void update(Trainer trainer) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.merge(trainer);
//            transaction.commit();
//        }
//    }
//
//    public void delete(Trainer trainer) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.remove(trainer);
//            transaction.commit();
//        }
//    }
//
//    public List<Trainer> findAll() {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Trainer", Trainer.class).list();
//        }
//    }
//
//
//}