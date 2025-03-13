//package com.epamlab.gymcrm.dao;
//
//import com.epamlab.gymcrm.trainee.model.Trainee;
//import com.epamlab.gymcrm.trainer.model.Trainer;
//import com.epamlab.gymcrm.training.dao.TrainingDao;
//import com.epamlab.gymcrm.training.model.Training;
//import com.epamlab.gymcrm.training.model.TrainingType;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//import org.hibernate.query.Query;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TrainingDaoTest {
//
//    @Mock
//    private SessionFactory sessionFactory;
//
//    @Mock
//    private Session session;
//
//    @Mock
//    private Transaction transaction;
//
//    @Mock
//    private Trainee trainee;
//
//    @Mock
//    private Trainer trainer;
//
//    @Mock
//    private Query<Training> query;
//
//    @InjectMocks
//    private TrainingDao trainingDao;
//
//    @BeforeEach
//    void setUp() {
//        // Stub the openSession()
//        when(sessionFactory.openSession()).thenReturn(session);
//    }
//
//    @Test
//    void save_ShouldPersistTraining() {
//        Training training = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainingDao.save(training);
//
//        verify(session).persist(training);
//        verify(transaction).commit();
//        verify(session).close();
//    }
//
//    @Test
//    void findById_ShouldReturnTraining() {
//        Long id = 1L;
//        Training training = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
//        when(session.get(Training.class, id)).thenReturn(training);
//
//        Training result = trainingDao.findById(id);
//
//        assertEquals(training, result);
//        verify(session).get(Training.class, id);
//    }
//
//    @Test
//    void findByTraineeUsername_ShouldReturnTrainings() {
//        String username = "trainee1";
//        List<Training> expected = List.of(new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60));
//
//        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(query);
//        when(query.setParameter("username", username)).thenReturn(query);
//        when(query.list()).thenReturn(expected);
//
//        List<Training> result = trainingDao.findByTraineeUsername(username);
//
//        assertEquals(expected, result);
//        verify(session).createQuery("FROM Training t WHERE t.trainee.username = :username", Training.class);
//    }
//
//    @Test
//    void findTraineeTrainingsWithFilters_AllParams() {
//        String username = "trainee1";
//        LocalDate fromDate = LocalDate.now();
//        LocalDate toDate = LocalDate.now().plusDays(1);
//        String trainerName = "Smith";
//        TrainingType type = TrainingType.CARDIO;
//
//        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(query);
//        when(query.list()).thenReturn(List.of());
//
//        trainingDao.findTraineeTrainingsWithFilters(username, fromDate, toDate, trainerName, type);
//
//        verify(query).setParameter("traineeUsername", username);
//        verify(query).setParameter("fromDate", fromDate.atStartOfDay());
//        verify(query).setParameter("toDate", toDate.atTime(23, 59, 59));
//        verify(query).setParameter("trainerName", "%Smith%");
//        verify(query).setParameter("trainingType", type);
//    }
//
//    @Test
//    void findTrainerTrainingsWithFilters_PartialParams() {
//        String username = "trainer1";
//        LocalDate toDate = LocalDate.now().plusDays(1);
//        String traineeName = "Doe";
//
//        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(query);
//        when(query.list()).thenReturn(List.of());
//
//        trainingDao.findTrainerTrainingsWithFilters(username, null, toDate, traineeName);
//
//        verify(query, never()).setParameter(eq("fromDate"), any());
//        verify(query).setParameter("toDate", toDate.atTime(23, 59, 59));
//        verify(query).setParameter("traineeName", "%Doe%");
//    }
//
//    @Test
//    void update_ShouldMergeTraining() {
//        Training training = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainingDao.update(training);
//
//        verify(session).merge(training);
//        verify(transaction).commit();
//    }
//
//    @Test
//    void delete_ShouldRemoveTraining() {
//        Training training = new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainingDao.delete(training);
//
//        verify(session).remove(training);
//        verify(transaction).commit();
//    }
//
//    @Test
//    void findByDuration_ShouldReturnMatchingTrainings() {
//        int duration = 60;
//        List<Training> expected = List.of(new Training(trainer, trainee, "Morning Yoga", TrainingType.STRENGTH, LocalDate.of(2024, 3, 7), 60));
//
//        when(session.createQuery(anyString(), eq(Training.class))).thenReturn(query);
//        when(query.setParameter("duration", duration)).thenReturn(query);
//        when(query.list()).thenReturn(expected);
//
//        List<Training> result = trainingDao.findByDuration(duration);
//
//        assertEquals(expected, result);
//    }
//}
