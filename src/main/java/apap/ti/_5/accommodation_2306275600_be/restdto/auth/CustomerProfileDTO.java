package apap.ti._5.accommodation_2306275600_be.restdto.auth;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerProfileDTO(
    UUID userId,
    String username,
    String name,
    String email,
    String gender,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isDeleted,
    long saldo
) {}
