//package com.epamlab.gymcrm.dao;
//
//import com.epamlab.gymcrm.trainer.dao.TrainerDao;
//import com.epamlab.gymcrm.trainer.model.Trainer;
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
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TrainerDaoTest {
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
//    private Query<Trainer> query;
//
//    @InjectMocks
//    private TrainerDao trainerDao;
//
//    private Trainer testTrainer;
//
//    @BeforeEach
//    void setUp() {
//        // Createing sample Trainer object.
//        testTrainer = new Trainer("Trainer", "One", "Cardio", true);
//        testTrainer.setId(1L);
//        testTrainer.setUsername("trainer.test");
//        testTrainer.setPassword("password123");
//        testTrainer.setFirstName("John");
//        testTrainer.setLastName("Doe");
//        testTrainer.setSpecialization("Strength");
//        testTrainer.setActive(true);
//    }
//
//    @Test
//    void save_ShouldPersistTrainer() {
//        // Stubbing openSession and beginTransaction
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainerDao.save(testTrainer);
//
//        verify(session).persist(testTrainer);
//        verify(transaction).commit();
//        verify(session).close();
//    }
//
//    @Test
//    void findById_ShouldReturnTrainer() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.get(Trainer.class, 1L)).thenReturn(testTrainer);
//
//        Trainer result = trainerDao.findById(1L);
//        assertEquals(testTrainer, result);
//    }
//
//    @Test
//    void findById_NotFound() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.get(Trainer.class, 999L)).thenReturn(null);
//
//        Trainer result = trainerDao.findById(999L);
//        assertNull(result);
//    }
//
//    @Test
//    void findByUsername_ShouldReturnTrainer() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.createQuery("FROM Trainer WHERE username = :username", Trainer.class))
//                .thenReturn(query);
//        when(query.setParameter("username", "trainer.test")).thenReturn(query);
//        when(query.uniqueResult()).thenReturn(testTrainer);
//
//        Trainer result = trainerDao.findByUsername("trainer.test");
//        assertEquals(testTrainer, result);
//    }
//
//    @Test
//    void findByUsername_NotFound() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
//        when(query.setParameter("username", "nonexistent")).thenReturn(query);
//        when(query.uniqueResult()).thenReturn(null);
//
//        Trainer result = trainerDao.findByUsername("nonexistent");
//        assertNull(result);
//    }
//
//    @Test
//    void findBySpecialization_ShouldReturnList() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.createQuery("FROM Trainer t WHERE t.specialization = :specialization", Trainer.class))
//                .thenReturn(query);
//        when(query.setParameter("specialization", "Strength")).thenReturn(query);
//        when(query.list()).thenReturn(List.of(testTrainer));
//
//        List<Trainer> result = trainerDao.findBySpecialization("Strength");
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(testTrainer, result.get(0));
//    }
//
//    @Test
//    void findUnassignedTrainersForTrainee_ShouldReturnList() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
//        when(query.setParameter("traineeId", 1L)).thenReturn(query);
//        when(query.list()).thenReturn(List.of(testTrainer));
//
//        List<Trainer> result = trainerDao.findUnassignedTrainersForTrainee(1L);
//        assertFalse(result.isEmpty());
//    }
//
//    @Test
//    void update_ShouldMergeTrainer() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainerDao.update(testTrainer);
//
//        verify(session).merge(testTrainer);
//        verify(transaction).commit();
//    }
//
//    @Test
//    void delete_ShouldRemoveTrainer() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        trainerDao.delete(testTrainer);
//
//        verify(session).remove(testTrainer);
//        verify(transaction).commit();
//    }
//
//    @Test
//    void findAll_ShouldReturnAllTrainers() {
//        when(sessionFactory.openSession()).thenReturn(session);
//        when(session.createQuery("FROM Trainer", Trainer.class)).thenReturn(query);
//        when(query.list()).thenReturn(List.of(testTrainer));
//
//        List<Trainer> result = trainerDao.findAll();
//        assertEquals(1, result.size());
//    }
//}
