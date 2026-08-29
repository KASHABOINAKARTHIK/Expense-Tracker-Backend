package com.trackmyexpense.dto;

import com.trackmyexpense.entity.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Request payload for creating/updating an expense.
 * expenseDate is optional at the API level - the service defaults it to "now" if omitted.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private ExpenseCategory category;

    // Optional - if not provided, service layer defaults it to current date/time
    private LocalDateTime expenseDate;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}
