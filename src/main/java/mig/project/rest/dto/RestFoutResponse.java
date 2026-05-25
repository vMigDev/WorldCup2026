package mig.project.rest.dto;

import java.time.LocalDateTime;

public record RestFoutResponse(int status, String message, LocalDateTime timestamp) {
}