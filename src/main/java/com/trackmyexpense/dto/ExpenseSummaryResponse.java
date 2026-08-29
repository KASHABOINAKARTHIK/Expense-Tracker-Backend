package com.trackmyexpense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response for GET /api/expenses/summary?month=YYYY-MM
 * Powers the dashboard's monthly total + category breakdown.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryResponse {
    private String month;              // e.g. "2026-08"
    private BigDecimal total;
    private List<CategorySummary> categories;
}
