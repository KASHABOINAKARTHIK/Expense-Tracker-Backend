package com.trackmyexpense.service;

import com.trackmyexpense.dto.ExpenseRequest;
import com.trackmyexpense.dto.ExpenseResponse;
import com.trackmyexpense.dto.ExpenseSummaryResponse;
import com.trackmyexpense.dto.TotalResponse;
import com.trackmyexpense.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {

    ExpenseResponse createExpense(ExpenseRequest request);

    ExpenseResponse getExpenseById(Long id);

    Page<ExpenseResponse> getExpenses(String month, ExpenseCategory category, String search, Pageable pageable);

    ExpenseResponse updateExpense(Long id, ExpenseRequest request);

    void deleteExpense(Long id);

    ExpenseSummaryResponse getMonthlySummary(String month);

    TotalResponse getTotalToDate();
}