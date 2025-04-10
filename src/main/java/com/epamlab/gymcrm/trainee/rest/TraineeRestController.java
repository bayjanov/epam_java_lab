package com.epamlab.gymcrm.trainee.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.training.model.TrainingType;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.metrics.MetricsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Tag(name = "Trainee Management", description = "Endpoints for managing Trainee actions")
@RestController
@RequestMapping("/api/trainees")
public class TraineeRestController {

    private static final Logger logger = LoggerFactory.getLogger(TraineeRestController.class);
    private final GymFacade gymFacade;
    private final MetricsService metricsService;

    public TraineeRestController(GymFacade gymFacade, MetricsService metricsService) {
        this.gymFacade = gymFacade;
        this.metricsService = metricsService;
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    // TRAINEE REGISTRATION (POST)
    @Operation(summary = "Register a new trainee", description = "Automatically generates username and password.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerTrainee(@RequestBody Trainee trainee) {
        String txId = generateTransactionId();
        logger.info("[{}] POST /api/trainees/register => {}", txId, trainee);

        // facade sets username & password
        gymFacade.createTrainee(trainee);

        Map<String, String> response = new HashMap<>();
        response.put("username", trainee.getUsername());
        response.put("password", trainee.getPassword());

        logger.info("[{}] Trainee registered with username: {}", txId, trainee.getUsername());
        metricsService.incrementTraineeLogin();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // LOGIN (GET)
    @Operation(summary = "Trainee login", description = "Checks the provided username/password for a Trainee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @GetMapping("/login")
    public ResponseEntity<String> traineeLogin(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] GET /api/trainees/login?username={}", txId, username);

        boolean isValid = gymFacade.authenticateTrainee(username, password);
        if (isValid) {
            logger.info("[{}] Trainee login successful: {}", txId, username);
            metricsService.incrementTraineeLogin();
            return ResponseEntity.ok("Login successful");
        } else {
            logger.warn("[{}] Trainee login failed: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    // CHANGE LOGIN (PUT)
    @Operation(summary = "Change trainee password", description = "Requires old password to authenticate.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/change-login")
    public ResponseEntity<String> changeTraineePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] PUT /api/trainees/change-login?username={}", txId, username);

        boolean isValid = gymFacade.authenticateTrainee(username, oldPassword);
        if (!isValid) {
            logger.warn("[{}] Old password mismatch for trainee: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password");
        }

        Trainee t = gymFacade.getTraineeByUsername(username);
        if (t == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }

        t.setPassword(newPassword);
        gymFacade.updateTrainee(username, newPassword, t);
        logger.info("[{}] Password changed for trainee: {}", txId, username);
        return ResponseEntity.ok("Password changed");
    }

    // GET TRAINEE PROFILE (GET)
    @Operation(summary = "Retrieve full trainee profile", description = "Requires trainee authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getTraineeProfile(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] GET /api/trainees/profile?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to get trainee profile: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Trainee trainee = gymFacade.getTraineeByUsername(username);
        if (trainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("firstName", trainee.getFirstName());
        resp.put("lastName", trainee.getLastName());
        resp.put("dateOfBirth", trainee.getDateOfBirth());
        resp.put("address", trainee.getAddress());
        resp.put("isActive", trainee.isActive());

        // Trainer list
        List<Map<String, Object>> trainersInfo = new ArrayList<>();
        for (Trainer tr : trainee.getTrainers()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("trainerUsername", tr.getUsername());
            m.put("trainerFirstName", tr.getFirstName());
            m.put("trainerLastName", tr.getLastName());
            m.put("trainerSpecialization", tr.getSpecialization());
            trainersInfo.add(m);
        }
        resp.put("trainersList", trainersInfo);

        return ResponseEntity.ok(resp);
    }

    // UPDATE TRAINEE PROFILE (PUT)
    @Operation(summary = "Update trainee profile fields",
            description = "Update firstName, lastName, dateOfBirth, address, and isActive.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @PutMapping("/profile")
    public ResponseEntity<?> updateTraineeProfile(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody Map<String, Object> updates
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] PUT /api/trainees/profile?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to update trainee profile: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Trainee trainee = gymFacade.getTraineeByUsername(username);
        if (trainee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }

        // Required fields check
        if (!updates.containsKey("firstName")
                || !updates.containsKey("lastName")
                || !updates.containsKey("isActive")) {
            return ResponseEntity.badRequest().body("Missing required fields: firstName, lastName, isActive");
        }

        trainee.setFirstName((String) updates.get("firstName"));
        trainee.setLastName((String) updates.get("lastName"));
        trainee.setActive(Boolean.parseBoolean(updates.get("isActive").toString()));

        // Optional: dateOfBirth, address
        if (updates.containsKey("dateOfBirth")) {
            String dobStr = (String) updates.get("dateOfBirth");
            if (dobStr != null && !dobStr.isBlank()) {
                try {
                    trainee.setDateOfBirth(LocalDate.parse(dobStr));
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Invalid dateOfBirth format (use YYYY-MM-DD)");
                }
            }
        }
        if (updates.containsKey("address")) {
            trainee.setAddress((String) updates.get("address"));
        }

        // Save changes
        gymFacade.updateTrainee(username, password, trainee);

        // build  final response
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("username", trainee.getUsername());
        result.put("firstName", trainee.getFirstName());
        result.put("lastName", trainee.getLastName());
        result.put("dateOfBirth", trainee.getDateOfBirth());
        result.put("address", trainee.getAddress());
        result.put("isActive", trainee.isActive());

        // Trainer list
        List<Map<String, Object>> trainersList = new ArrayList<>();
        for (Trainer tr : trainee.getTrainers()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("trainerUsername", tr.getUsername());
            m.put("trainerFirstName", tr.getFirstName());
            m.put("trainerLastName", tr.getLastName());
            m.put("trainerSpecialization", tr.getSpecialization());
            trainersList.add(m);
        }
        result.put("trainersList", trainersList);

        logger.info("[{}] Trainee profile updated for username={}", txId, username);
        return ResponseEntity.ok(result);
    }

    // DELETE TRAINEE PROFILE (DELETE)
    @Operation(summary = "Delete trainee profile by username",
            description = "Hard delete with cascade on relevant trainings.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteTraineeProfile(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] DELETE /api/trainees/profile?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to delete trainee: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Trainee t = gymFacade.getTraineeByUsername(username);
        if (t == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainee not found");
        }

        gymFacade.deleteTraineeByUsername(username);
        logger.info("[{}] Trainee deleted: {}", txId, username);
        return ResponseEntity.ok("Trainee profile deleted");
    }

    // GET NOT ASSIGNED ON TRAINEE ACTIVE TRAINERS (GET)
    @Operation(summary = "Get active trainers not assigned to the specified trainee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<?> getUnassignedActiveTrainers(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] GET /api/trainees/not-assigned-trainers?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to get unassigned trainers: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        List<Trainer> unassigned = gymFacade.getAvailableTrainersForTrainee(username);

        // Filter active
        List<Map<String, Object>> results = new ArrayList<>();
        for (Trainer tr : unassigned) {
            if (tr.isActive()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("trainerUsername", tr.getUsername());
                map.put("trainerFirstName", tr.getFirstName());
                map.put("trainerLastName", tr.getLastName());
                map.put("trainerSpecialization", tr.getSpecialization());
                results.add(map);
            }
        }
        return ResponseEntity.ok(results);
    }

    // UPDATE TRAINEE'S TRAINER LIST (PUT)
    @Operation(summary = "Update the set of trainers assigned to a trainee.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainers updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer or Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PutMapping("/trainers-list")
    public ResponseEntity<?> updateTraineeTrainers (
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody Map<String, List<String>> request
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] PUT /api/trainees/trainers-list?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to update trainers: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        List<String> trainerUsernames = request.get("trainersList");
        if (trainerUsernames == null || trainerUsernames.isEmpty()) {
            return ResponseEntity.badRequest().body("Must provide non-empty 'trainersList'");
        }

        List<Long> trainerIds = new ArrayList<>();
        for (String trUser : trainerUsernames) {
            Trainer t = gymFacade.getTrainerByUsername(trUser);
            if (t == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found: " + trUser);
            }
            trainerIds.add(t.getId());
        }

        gymFacade.updateTraineeTrainers(username, password, trainerIds);

        // Build response
        Trainee updated = gymFacade.getTraineeByUsername(username);
        List<Map<String, Object>> trainerList = new ArrayList<>();
        for (Trainer t : updated.getTrainers()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("trainerUsername", t.getUsername());
            map.put("trainerFirstName", t.getFirstName());
            map.put("trainerLastName", t.getLastName());
            map.put("trainerSpecialization", t.getSpecialization());
            trainerList.add(map);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trainersList", trainerList);

        logger.info("[{}] Updated trainers for trainee: {}", txId, username);
        return ResponseEntity.ok(result);
    }

    // GET TRAINEE TRAININGS LIST (GET)
    @Operation(summary = "Get trainee’s trainings (with optional date range, trainer name, type).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    @GetMapping("/trainings")
    public ResponseEntity<?> getTraineeTrainings(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodTo,
            @RequestParam(required = false) String trainerName,
            @RequestParam(required = false) TrainingType trainingType
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] GET /api/trainees/trainings?username={}", txId, username);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to get trainee trainings: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        LocalDate from = null;
        LocalDate to = null;
        try {
            if (periodFrom != null && !periodFrom.isBlank()) {
                from = LocalDate.parse(periodFrom);
            }
            if (periodTo != null && !periodTo.isBlank()) {
                to = LocalDate.parse(periodTo);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD");
        }

        List<Training> trainings = gymFacade.getTraineeTrainings(username, from, to, trainerName, trainingType);

        List<Map<String, Object>> results = new ArrayList<>();
        for (Training tr : trainings) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("trainingName", tr.getTrainingName());
            m.put("trainingDate", tr.getTrainingDate());
            m.put("trainingType", tr.getTrainingType());
            m.put("trainingDuration", tr.getDurationMinutes());
            m.put("trainerName", tr.getTrainer().getFirstName() + " " + tr.getTrainer().getLastName());
            results.add(m);
        }

        logger.info("[{}] Returning {} trainings for trainee: {}", txId, results.size(), username);
        return ResponseEntity.ok(results);
    }

    // ACTIVATE/DE-ACTIVATE TRAINEE (PATCH)
    @Operation(summary = "Activate or deactivate the trainee (not idempotent).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PatchMapping("/activate")
    public ResponseEntity<String> activateOrDeactivateTrainee(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam boolean isActive
    ) {
        String txId = generateTransactionId();
        logger.info("[{}] PATCH /api/trainees/activate?username={}&isActive={}", txId, username, isActive);

        if (!gymFacade.authenticateTrainee(username, password)) {
            logger.warn("[{}] Unauthorized to patch trainee: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        if (isActive) {
            gymFacade.activateTrainee(username, password);
        } else {
            gymFacade.deactivateTrainee(username, password);
        }

        logger.info("[{}] Trainee isActive set to {} for: {}", txId, isActive, username);
        return ResponseEntity.ok("Trainee activation updated");
    }
}
