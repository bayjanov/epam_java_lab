package com.epamlab.gymcrm.service.trainee;

import com.epamlab.gymcrm.dao.TraineeDao;
import com.epamlab.gymcrm.model.Trainee;
import com.epamlab.gymcrm.service.user.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

    private TraineeDao traineeDao;
    private UserProfileService userProfileService;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    public void createTrainee(Trainee trainee) {
        String username = userProfileService.generateUniqueUsername(trainee.getFirstName(), trainee.getLastName());
        trainee.setUsername(username);
        trainee.setPassword(userProfileService.generatePassword(10));
        traineeDao.create(trainee);
        logger.info("Created trainee: {}", trainee);
    }

    public Trainee getTrainee(Long id) {
        Trainee trainee = traineeDao.read(id);
        if (trainee != null) {
            logger.info("Retrieved trainee: {}", trainee);
        } else {
            logger.warn("Trainee ID {} not found", id);
        }
        return trainee;
    }

    public void updateTrainee(Trainee trainee) {
        traineeDao.update(trainee);
        logger.info("Updated trainee: {}", trainee);
    }

    public void deleteTrainee(Long id) {
        Trainee trainee = traineeDao.read(id);
        if (trainee != null) {
            traineeDao.delete(id);
            logger.info("Deleted trainee: {}", trainee);
        } else {
            logger.warn("Delete failed - no trainee with ID {}", id);
        }
    }

    public Map<Long, Trainee> listAllTrainees() {
        logger.info("Listing all trainees");
        return traineeDao.listAll();
    }
}