package com.trackmyexpense.dto;

import com.trackmyexpense.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response payload returned to clients. Entities are never returned directly
 * over REST - this keeps the API contract stable even if the entity changes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDateTime expenseDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
