package com.epamlab.gymcrm.trainer.rest;

import com.epamlab.gymcrm.facade.GymFacade;
import com.epamlab.gymcrm.security.bruteforce.LoginAttemptService;
import com.epamlab.gymcrm.security.jwt.JwtTokenProvider;
import com.epamlab.gymcrm.trainer.dto.TrainerRegistrationResponse;
import com.epamlab.gymcrm.trainer.model.Trainer;
import com.epamlab.gymcrm.trainee.model.Trainee;
import com.epamlab.gymcrm.training.model.Training;
import com.epamlab.gymcrm.metrics.MetricsService;

import com.epamlab.gymcrm.user.dto.RegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Tag(name = "Trainer Management", description = "Endpoints for managing Trainer actions")
@RestController
@RequestMapping("/api/trainers")
public class TrainerRestController {

    private static final Logger logger = LoggerFactory.getLogger(TrainerRestController.class);
    private final GymFacade gymFacade;
    private final MetricsService metricsService;
    private final LoginAttemptService loginAttemptService;
    private final JwtTokenProvider jwtTokenProvider;


    public TrainerRestController(GymFacade gymFacade,
                                 MetricsService metricsService,
                                 LoginAttemptService loginAttemptService,
                                 JwtTokenProvider jwtTokenProvider) {
        this.gymFacade = gymFacade;
        this.metricsService = metricsService;
        this.loginAttemptService = loginAttemptService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private String generateTxId() {
        return UUID.randomUUID().toString();
    }

    //  TRAINER REGISTRATION (POST)
    @Operation(summary = "Register a new trainer", description = "Returns auto-generated username & password.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/register")
    public ResponseEntity<TrainerRegistrationResponse> registerTrainer(@RequestBody Trainer trainer) {
        String txId = generateTxId();
        logger.info("[{}] POST /api/trainers/register => {}", txId, trainer);

        TrainerRegistrationResponse result = gymFacade.createTrainer(trainer);

        logger.info("[{}] Trainer registered with username: {}", txId, trainer.getUsername());
        metricsService.incrementTrainerLogin();
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // TRAINER LOGIN (GET)
    @Operation(summary = "Login as a trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/login")
    public ResponseEntity<String> loginTrainer(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTxId();
        logger.info("[{}] GET /api/trainers/login?username={}", txId, username);

        if (loginAttemptService.isBlocked(username)) {
            logger.warn("[{}] Trainer account locked: {}", txId, username);
            return ResponseEntity.status(423).body("Account temporarily locked due to failed attempts");
        }

        boolean isValid = gymFacade.authenticateTrainer(username, password);
        if (isValid) {
            loginAttemptService.loginSucceeded(username);
            metricsService.incrementTrainerLogin();

            String token = jwtTokenProvider.generateToken(username);
            logger.info("[{}] Trainer login success: {}", txId, username);
            return ResponseEntity.ok(token);
        } else {
            loginAttemptService.loginFailed(username);
            logger.warn("[{}] Trainer login failed: {}", txId, username);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // CHANGE LOGIN (PUT)
    @Operation(summary = "Change trainer password", description = "Requires old password.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/change-login")
    public ResponseEntity<String> changeTrainerPassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        String txId = generateTxId();
        logger.info("[{}] PUT /api/trainers/change-login?username={}", txId, username);

        boolean isValid = gymFacade.authenticateTrainer(username, oldPassword);
        if (!isValid) {
            logger.warn("[{}] Old password mismatch for trainer: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password");
        }

        Trainer trainer = gymFacade.getTrainerByUsername(username);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }

        trainer.setPassword(newPassword);
        gymFacade.updateTrainer(username, newPassword, trainer);

        logger.info("[{}] Password changed for trainer: {}", txId, username);
        return ResponseEntity.ok("Password changed");
    }

    // GET TRAINER PROFILE (GET)
    @Operation(summary = "Retrieve full trainer profile", description = "Requires authentication.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<?> getTrainerProfile(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String txId = generateTxId();
        logger.info("[{}] GET /api/trainers/profile?username={}", txId, username);

        if (!gymFacade.authenticateTrainer(username, password)) {
            logger.warn("[{}] Unauthorized to get trainer profile: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Trainer trainer = gymFacade.getTrainerByUsername(username);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("firstName", trainer.getFirstName());
        resp.put("lastName", trainer.getLastName());
        resp.put("specialization", trainer.getSpecialization());
        resp.put("isActive", trainer.isActive());

        // Trainees list
        List<Map<String, Object>> traineesInfo = new ArrayList<>();
        for (Trainee tr : trainer.getTrainees()) {
            Map<String, Object> traineeMap = new LinkedHashMap<>();
            traineeMap.put("traineeUsername", tr.getUsername());
            traineeMap.put("traineeFirstName", tr.getFirstName());
            traineeMap.put("traineeLastName", tr.getLastName());
            traineesInfo.add(traineeMap);
        }
        resp.put("traineesList", traineesInfo);

        return ResponseEntity.ok(resp);
    }

    // UPDATE TRAINER PROFILE (PUT)
    @Operation(summary = "Update trainer profile",
            description = "firstName, lastName, isActive fields. Specialization is read-only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/profile")
    public ResponseEntity<?> updateTrainerProfile(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody Map<String, Object> updates
    ) {
        String txId = generateTxId();
        logger.info("[{}] PUT /api/trainers/profile?username={}", txId, username);

        if (!gymFacade.authenticateTrainer(username, password)) {
            logger.warn("[{}] Unauthorized to update trainer: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        Trainer trainer = gymFacade.getTrainerByUsername(username);
        if (trainer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Trainer not found");
        }

        // firstName, lastName, isActive
        if (!updates.containsKey("firstName")
                || !updates.containsKey("lastName")
                || !updates.containsKey("isActive")) {
            return ResponseEntity.badRequest().body("Missing required fields: firstName, lastName, isActive");
        }

        trainer.setFirstName((String) updates.get("firstName"));
        trainer.setLastName((String) updates.get("lastName"));
        trainer.setActive(Boolean.parseBoolean(updates.get("isActive").toString()));

        gymFacade.updateTrainer(username, password, trainer);

        // Build response
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("username", trainer.getUsername());
        resp.put("firstName", trainer.getFirstName());
        resp.put("lastName", trainer.getLastName());
        resp.put("specialization", trainer.getSpecialization());
        resp.put("isActive", trainer.isActive());

        // Trainees
        List<Map<String, Object>> traineesInfo = new ArrayList<>();
        for (Trainee tr : trainer.getTrainees()) {
            Map<String, Object> traineeMap = new LinkedHashMap<>();
            traineeMap.put("traineeUsername", tr.getUsername());
            traineeMap.put("traineeFirstName", tr.getFirstName());
            traineeMap.put("traineeLastName", tr.getLastName());
            traineesInfo.add(traineeMap);
        }
        resp.put("traineesList", traineesInfo);

        return ResponseEntity.ok(resp);
    }

    // GET TRAINER TRAININGS LIST (GET)
    @Operation(summary = "Get trainer’s trainings", description = "Optional date range, traineeName filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/trainings")
    public ResponseEntity<?> getTrainerTrainings(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String periodFrom,
            @RequestParam(required = false) String periodTo,
            @RequestParam(required = false) String traineeName
    ) {
        String txId = generateTxId();
        logger.info("[{}] GET /api/trainers/trainings?username={}", txId, username);

        if (!gymFacade.authenticateTrainer(username, password)) {
            logger.warn("[{}] Unauthorized to get trainer trainings: {}", txId, username);
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

        List<Training> trainings = gymFacade.getTrainerTrainings(username, from, to, traineeName);

        List<Map<String, Object>> results = new ArrayList<>();
        for (Training tr : trainings) {
            Map<String, Object> trainingsMap = new LinkedHashMap<>();
            trainingsMap.put("trainingName", tr.getTrainingName());
            trainingsMap.put("trainingDate", tr.getTrainingDate());
            trainingsMap.put("trainingType", tr.getTrainingType());
            trainingsMap.put("trainingDuration", tr.getDurationMinutes());
            trainingsMap.put("traineeName", tr.getTrainee().getFirstName() + " " + tr.getTrainee().getLastName());
            results.add(trainingsMap);
        }

        logger.info("[{}] Returning {} trainings for trainer: {}", txId, results.size(), username);
        return ResponseEntity.ok(results);
    }

    // ACTIVATE/DEACTIVATE TRAINER (PATCH)
    @Operation(summary = "Activate or deactivate the trainer", description = "Not idempotent action.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Activation updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/activate")
    public ResponseEntity<String> activateOrDeactivateTrainer(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam boolean isActive
    ) {
        String txId = generateTxId();
        logger.info("[{}] PATCH /api/trainers/activate?username={}&isActive={}", txId, username, isActive);

        if (!gymFacade.authenticateTrainer(username, password)) {
            logger.warn("[{}] Unauthorized to patch trainer: {}", txId, username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        if (isActive) {
            gymFacade.activateTrainer(username, password);
        } else {
            gymFacade.deactivateTrainer(username, password);
        }

        logger.info("[{}] Trainer isActive set to {} for: {}", txId, isActive, username);
        return ResponseEntity.ok("Trainer activation updated");
    }
}
