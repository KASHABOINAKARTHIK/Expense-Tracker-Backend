package com.trackmyexpense.service;

import com.trackmyexpense.dto.ExpenseRequest;
import com.trackmyexpense.dto.ExpenseResponse;
import com.trackmyexpense.dto.ExpenseSummaryResponse;
import com.trackmyexpense.dto.TotalResponse;
import com.trackmyexpense.entity.ExpenseCategory;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse createExpense(ExpenseRequest request);

    ExpenseResponse getExpenseById(Long id);

    /**
     * Returns expenses, optionally filtered.
     * @param month    optional, format "yyyy-MM" (e.g. "2026-08")
     * @param category optional exact category match
     * @param search   optional case-insensitive description keyword search
     */
    List<ExpenseResponse> getExpenses(String month, ExpenseCategory category, String search);

    ExpenseResponse updateExpense(Long id, ExpenseRequest request);

    void deleteExpense(Long id);

    ExpenseSummaryResponse getMonthlySummary(String month);

    TotalResponse getTotalToDate();
}
