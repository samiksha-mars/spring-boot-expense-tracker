package com.example.expensetracker.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank(message = "description must not be blank")
        @Size(max = 200, message = "description must be at most 200 characters")
        String description,
        @NotNull(message = "amount is required")
        @Positive(message = "amount must be positive")
        @Digits(integer = 10, fraction = 2, message = "amount must have at most 2 decimal places")
        BigDecimal amount,
        @Size(max = 50, message = "category must be at most 50 characters")
        String category,
        @NotNull(message = "date is required")
        LocalDate date
) {
}
