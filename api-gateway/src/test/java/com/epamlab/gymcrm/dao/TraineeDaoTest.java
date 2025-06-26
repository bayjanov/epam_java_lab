//package com.epamlab.gymcrm.dao;
//
//import com.epamlab.gymcrm.trainee.dao.TraineeDao;
//import com.epamlab.gymcrm.trainee.model.Trainee;
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
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TraineeDaoTest {
//
//    @Mock
//    private SessionFactory sessionFactory;
//
//    @Mock
//    private Session session;
//
//    @InjectMocks
//    private TraineeDao traineeDao;
//
//    private Trainee testTrainee;
//
//    @BeforeEach
//    void setUp() {
//        // Prepare a sample Trainee object
//        testTrainee = new Trainee("John", "Doe", true,
//                LocalDate.of(1990, 1, 1), "123 Main St");
//        testTrainee.setId(1L);
//        testTrainee.setUsername("test.user");
//        testTrainee.setPassword("password123");
//        testTrainee.setFirstName("John");
//        testTrainee.setLastName("Doe");
//        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
//        testTrainee.setAddress("123 Main St");
//        testTrainee.setActive(true);
//
//        when(sessionFactory.openSession()).thenReturn(session);
//    }
//
//
//    @Test
//    void save_ShouldPersistTrainee() {
//        Transaction transaction = mock(Transaction.class);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        traineeDao.save(testTrainee);
//
//        verify(session).persist(testTrainee);
//        verify(transaction).commit();
//        verify(session).close();
//    }
//
//    @Test
//    void save_ShouldRollbackOnError() {
//        Transaction transaction = mock(Transaction.class);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        // Force a RuntimeException during persist
//        doThrow(RuntimeException.class).when(session).persist(any());
//
//        assertThrows(RuntimeException.class, () -> traineeDao.save(testTrainee));
//        verify(transaction).rollback();
//    }
//
//
//
//    @Test
//    void update_ShouldMergeTrainee() {
//        Transaction transaction = mock(Transaction.class);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        traineeDao.update(testTrainee);
//
//        verify(session).merge(testTrainee);
//        verify(transaction).commit();
//    }
//
//
//    @Test
//    void delete_ShouldRemoveTrainee() {
//        Transaction transaction = mock(Transaction.class);
//        when(session.beginTransaction()).thenReturn(transaction);
//
//        traineeDao.delete(testTrainee);
//
//        verify(session).remove(testTrainee);
//        verify(transaction).commit();
//    }
//
//    @Test
//    void findById_ShouldReturnTrainee() {
//        // Only stub what we call here: session.get(...)
//        when(session.get(Trainee.class, 1L)).thenReturn(testTrainee);
//
//        Trainee result = traineeDao.findById(1L);
//        assertEquals(testTrainee, result);
//    }
//
//    @Test
//    void findById_NotFound() {
//        when(session.get(Trainee.class, 999L)).thenReturn(null);
//
//        Trainee result = traineeDao.findById(999L);
//        assertNull(result);
//    }
//
//
//    @Test
//    void findByUsername_ShouldReturnTrainee() {
//        @SuppressWarnings("unchecked")
//        Query<Trainee> query = mock(Query.class);
//
//        when(session.createQuery("FROM Trainee WHERE username = :username", Trainee.class))
//                .thenReturn(query);
//        when(query.setParameter("username", "test.user")).thenReturn(query);
//        when(query.uniqueResult()).thenReturn(testTrainee);
//
//        Trainee result = traineeDao.findByUsername("test.user");
//        assertEquals(testTrainee, result);
//    }
//
//    @Test
//    void findByUsername_NotFound() {
//        @SuppressWarnings("unchecked")
//        Query<Trainee> query = mock(Query.class);
//
//        when(session.createQuery("FROM Trainee WHERE username = :username", Trainee.class))
//                .thenReturn(query);
//        when(query.setParameter("username", "nonexistent")).thenReturn(query);
//        when(query.uniqueResult()).thenReturn(null);
//
//        Trainee result = traineeDao.findByUsername("nonexistent");
//        assertNull(result);
//    }
//
//
//    @Test
//    void findAll_ShouldReturnAllTrainees() {
//        @SuppressWarnings("unchecked")
//        Query<Trainee> query = mock(Query.class);
//
//        when(session.createQuery("FROM Trainee", Trainee.class)).thenReturn(query);
//        when(query.list()).thenReturn(List.of(testTrainee));
//
//        List<Trainee> result = traineeDao.findAll();
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(testTrainee, result.get(0));
//    }
//}
