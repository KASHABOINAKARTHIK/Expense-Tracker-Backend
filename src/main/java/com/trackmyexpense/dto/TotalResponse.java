package com.trackmyexpense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response for GET /api/expenses/total
 * Running total of all expenses with an expenseDate up to "now".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalResponse {
    private BigDecimal total;
}
