package com.epamlab.gymcrm.training.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.metrics.MetricsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Tag(name = "Training Management", description = "Endpoints for managing Training sessions")
@RestController
@RequestMapping("/api/trainings")
public class TrainingRestController {

    private static final Logger logger = LoggerFactory.getLogger(TrainingRestController.class);
    private final GymFacade gymFacade;
    private final MetricsService metricsService;

    public TrainingRestController(GymFacade gymFacade, MetricsService metricsService) {
        this.gymFacade = gymFacade;
        this.metricsService = metricsService;
    }

    private String generateTxId() {
        return UUID.randomUUID().toString();
    }

    // ADD TRAINING (POST)
    @Operation(summary = "Add a new training session", description = "Requires an authenticated trainer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addTraining(
            @RequestParam String trainerUsername,
            @RequestParam String trainerPassword,
            @RequestBody Map<String, Object> payload
    ) {
        String txId = generateTxId();
        logger.info("[{}] POST /api/trainings/add by trainer={}", txId, trainerUsername);

        // Check if the user is an authenticated trainer
        if (!gymFacade.authenticateTrainer(trainerUsername, trainerPassword)) {
            logger.warn("[{}] Unauthorized to add training: {}", txId, trainerUsername);

            return ResponseEntity.status(401).body("Not an authenticated trainer");
        }

        // Required fields
        if (!payload.containsKey("traineeUsername") ||
                !payload.containsKey("trainerUsername") ||
                !payload.containsKey("trainingName")     ||
                !payload.containsKey("trainingDate")     ||
                !payload.containsKey("trainingDuration")) {
            return ResponseEntity.badRequest().body("Missing required fields in request body");
        }

        String traineeUsername = (String) payload.get("traineeUsername");
        String usedTrainerUsername = (String) payload.get("trainerUsername");
        String trainingName = (String) payload.get("trainingName");
        String trainingDateStr = (String) payload.get("trainingDate");

        int duration;
        try {
            duration = Integer.parseInt(payload.get("trainingDuration").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid 'trainingDuration' - must be a number");
        }

        Trainer trainer = gymFacade.getTrainerByUsername(usedTrainerUsername);
        if (trainer == null) {
            return ResponseEntity.status(404).body("Trainer not found: " + usedTrainerUsername);
        }
        Trainee trainee = gymFacade.getTraineeByUsername(traineeUsername);
        if (trainee == null) {
            return ResponseEntity.status(404).body("Trainee not found: " + traineeUsername);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(trainingDateStr);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid trainingDate format (use YYYY-MM-DD)");
        }

        // Optional: trainingType
        TrainingType trainingType = TrainingType.STRENGTH;
        if (payload.containsKey("trainingType")) {
            try {
                trainingType = TrainingType.valueOf(payload.get("trainingType").toString());
            } catch (IllegalArgumentException ex) {
                return ResponseEntity.badRequest().body("Invalid trainingType");
            }
        }

        Training newTraining = new Training(trainer, trainee, trainingName, trainingType, date, duration);
        gymFacade.addTraining(newTraining);

        logger.info("[{}] Training added: {} for trainee={} by trainer={}",
                txId, trainingName, traineeUsername, usedTrainerUsername);

        metricsService.incrementTrainingAdded(); // ro increment the training creation count

        return ResponseEntity.ok("Training added successfully");
    }

    // GET TRAINING TYPES (GET)
    @Operation(summary = "Retrieve the constant list of training types from the application.")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getTrainingTypes() {
        String txId = generateTxId();
        logger.info("[{}] GET /api/trainings/types", txId);

        List<Map<String, Object>> results = new ArrayList<>();
        for (TrainingType tt : TrainingType.values()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("trainingType", tt.name());
            item.put("trainingTypeId", tt.ordinal());
            results.add(item);
        }

        return ResponseEntity.ok(results);
    }
}
