package com.trackmyexpense.service;

import com.trackmyexpense.dto.*;
import com.trackmyexpense.entity.Expense;
import com.trackmyexpense.entity.ExpenseCategory;
import com.trackmyexpense.exception.ExpenseNotFoundException;
import com.trackmyexpense.exception.InvalidMonthFormatException;
import com.trackmyexpense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ExpenseRepository expenseRepository;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .category(request.getCategory())
                .expenseDate(request.getExpenseDate()) // null is fine - @PrePersist defaults it to now
                .description(request.getDescription())
                .build();

        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = findExpenseOrThrow(id);
        return toResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpenses(String month, ExpenseCategory category, String search) {
        List<Expense> expenses;

        if (month != null && !month.isBlank()) {
            YearMonth yearMonth = parseYearMonth(month);
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

            if (search != null && !search.isBlank()) {
                expenses = expenseRepository
                        .findByExpenseDateBetweenAndDescriptionContainingIgnoreCaseOrderByExpenseDateDesc(
                                start, end, search);
            } else if (category != null) {
                expenses = expenseRepository
                        .findByExpenseDateBetweenAndCategoryOrderByExpenseDateDesc(start, end, category);
            } else {
                expenses = expenseRepository.findByExpenseDateBetweenOrderByExpenseDateDesc(start, end);
            }
        } else if (category != null) {
            expenses = expenseRepository.findByCategoryOrderByExpenseDateDesc(category);
        } else {
            expenses = expenseRepository.findAllByOrderByExpenseDateDesc();
        }

        return expenses.stream().map(this::toResponse).toList();
    }

    @Override
    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
        Expense existing = findExpenseOrThrow(id);

        existing.setAmount(request.getAmount());
        existing.setCategory(request.getCategory());
        if (request.getExpenseDate() != null) {
            existing.setExpenseDate(request.getExpenseDate());
        }
        existing.setDescription(request.getDescription());
        // updatedAt is refreshed automatically via @PreUpdate

        Expense saved = expenseRepository.save(existing);
        return toResponse(saved);
    }

    @Override
    public void deleteExpense(Long id) {
        Expense existing = findExpenseOrThrow(id);
        expenseRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryResponse getMonthlySummary(String month) {
        YearMonth yearMonth = parseYearMonth(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        BigDecimal total = expenseRepository.sumAmountBetween(start, end);

        List<ExpenseRepository.CategoryTotalProjection> rows =
                expenseRepository.sumAmountByCategoryBetween(start, end);

        Map<ExpenseCategory, BigDecimal> totalsByCategory = rows.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ExpenseRepository.CategoryTotalProjection::getCategory,
                        ExpenseRepository.CategoryTotalProjection::getTotal));

        // Ensure every category appears in the response, even with zero spend,
        // so the frontend dashboard doesn't have to special-case missing categories.
        List<CategorySummary> categorySummaries = java.util.Arrays.stream(ExpenseCategory.values())
                .map(cat -> CategorySummary.builder()
                        .category(cat)
                        .amount(totalsByCategory.getOrDefault(cat, BigDecimal.ZERO))
                        .build())
                .toList();

        return ExpenseSummaryResponse.builder()
                .month(month)
                .total(total)
                .categories(categorySummaries)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TotalResponse getTotalToDate() {
        BigDecimal total = expenseRepository.sumAmountUpTo(LocalDateTime.now());
        return TotalResponse.builder().total(total).build();
    }

    // ---------- helpers ----------

    private Expense findExpenseOrThrow(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private YearMonth parseYearMonth(String month) {
        try {
            return YearMonth.parse(month, MONTH_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new InvalidMonthFormatException(month);
        }
    }

    private ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .category(expense.getCategory())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
