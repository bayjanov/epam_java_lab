package com.epamlab.gymcrm.user.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "JWT token-based authentication endpoints")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout", description = "Logs out the user. This is stateless; the client should discard the JWT.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        logger.info("Logout endpoint called. JWT should be discarded by client.");
        return ResponseEntity.ok("Logged out successfully. Please discard your JWT on the client.");
    }
}
