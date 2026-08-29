package com.trackmyexpense.dto;

import com.trackmyexpense.entity.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * One row of a category-wise breakdown, e.g. { "category": "FOOD", "amount": 5200.00 }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySummary {
    private ExpenseCategory category;
    private BigDecimal amount;
}
