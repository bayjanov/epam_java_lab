//package com.epamlab.gymcrm.training.dao;
//
//import com.epamlab.gymcrm.training.model.Training;
//import com.epamlab.gymcrm.training.model.TrainingType;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public class TrainingDao {
//
//    private final SessionFactory sessionFactory;
//
//    public TrainingDao(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public void save(Training training) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.persist(training);
//            transaction.commit();
//        }
//    }
//
//    public Training findById(Long id) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.get(Training.class, id);
//        }
//    }
//
//    public List<Training> findByTraineeUsername(String traineeUsername) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "FROM Training t WHERE t.trainee.username = :username", Training.class)
//                    .setParameter("username", traineeUsername)
//                    .list();
//        }
//    }
//
//    public List<Training> findByTrainerUsername(String trainerUsername) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "FROM Training t WHERE t.trainer.username = :username", Training.class)
//                    .setParameter("username", trainerUsername)
//                    .list();
//        }
//    }
//
//    public List<Training> findByTrainingType(TrainingType trainingType) {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                    "FROM Training t WHERE t.trainingType = :trainingType", Training.class)
//                    .setParameter("trainingType", trainingType)
//                    .list();
//        }
//    }
//
//    public List<Training> findByTrainingDate(LocalDate date) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "FROM Training t WHERE DATE(t.trainingDate) = :trainingDate", Training.class)
//                    .setParameter("trainingDate", date) // Only matching date part
//                    .list();
//        }
//    }
//
//    public List<Training> findByDuration(int durationMinutes) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "FROM Training t WHERE t.durationMinutes = :duration", Training.class)
//                    .setParameter("duration", durationMinutes)
//                    .list();
//        }
//    }
//
//
//    public List<Training> findTraineeTrainingsWithFilters(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, TrainingType trainingType) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "FROM Training t WHERE t.trainee.username = :traineeUsername";
//
//            if (fromDate != null) {
//                hql += " AND t.trainingDate >= :fromDate";
//            }
//            if (toDate != null) {
//                hql += " AND t.trainingDate <= :toDate";
//            }
//            if (trainerName != null && !trainerName.isEmpty()) {
//                hql += " AND t.trainer.lastName LIKE :trainerName";
//            }
//            if (trainingType != null) {
//                hql += " AND t.trainingType = :trainingType";
//            }
//
//            var query = session.createQuery(hql, Training.class);
//            query.setParameter("traineeUsername", traineeUsername);
//
//            if (fromDate != null) query.setParameter("fromDate", fromDate.atStartOfDay());
//            if (toDate != null) query.setParameter("toDate", toDate.atTime(23, 59, 59));
//            if (trainerName != null && !trainerName.isEmpty()) query.setParameter("trainerName", "%" + trainerName + "%");
//            if (trainingType != null) query.setParameter("trainingType", trainingType);
//
//            return query.list();
//        }
//    }
//
//    public List<Training> findTrainerTrainingsWithFilters(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
//        try (Session session = sessionFactory.openSession()) {
//            String hql = "FROM Training t WHERE t.trainer.username = :trainerUsername";
//
//            if (fromDate != null) {
//                hql += " AND t.trainingDate >= :fromDate";
//            }
//            if (toDate != null) {
//                hql += " AND t.trainingDate <= :toDate";
//            }
//            if (traineeName != null && !traineeName.isEmpty()) {
//                hql += " AND t.trainee.lastName LIKE :traineeName";
//            }
//
//            var query = session.createQuery(hql, Training.class);
//            query.setParameter("trainerUsername", trainerUsername);
//
//            if (fromDate != null) query.setParameter("fromDate", fromDate.atStartOfDay());
//            if (toDate != null) query.setParameter("toDate", toDate.atTime(23, 59, 59));
//            if (traineeName != null && !traineeName.isEmpty()) query.setParameter("traineeName", "%" + traineeName + "%");
//
//            return query.list();
//        }
//    }
//
//
//
//    public void update(Training training) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.merge(training);
//            transaction.commit();
//        }
//    }
//
//    public void delete(Training training) {
//        try(Session session = sessionFactory.openSession()) {
//            Transaction transaction = session.beginTransaction();
//            session.remove(training);
//            transaction.commit();
//        }
//    }
//
//    public List<Training> findAll() {
//        try(Session session = sessionFactory.openSession()) {
//            return session.createQuery("FROM Training", Training.class).list();
//        }
//    }
//}