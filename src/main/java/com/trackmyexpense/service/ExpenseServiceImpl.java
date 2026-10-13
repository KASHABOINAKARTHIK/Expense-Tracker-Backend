package com.trackmyexpense.service;

import com.trackmyexpense.dto.*;
import com.trackmyexpense.entity.Expense;
import com.trackmyexpense.entity.ExpenseCategory;
import com.trackmyexpense.entity.User;
import com.trackmyexpense.exception.ExpenseNotFoundException;
import com.trackmyexpense.exception.InvalidMonthFormatException;
import com.trackmyexpense.exception.UserNotFoundException;
import com.trackmyexpense.repository.ExpenseRepository;
import com.trackmyexpense.repository.UserRepository;
import com.trackmyexpense.security.CurrentUserProvider;
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

/**
 * Every method here operates only on the currently authenticated user's data.
 * The user id comes from CurrentUserProvider (reads it off the JWT-backed
 * SecurityContext) - never from a client-supplied parameter - so there is no
 * way for one user to read or modify another user's expenses.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = getCurrentUser();

        Expense expense = Expense.builder()
                .user(user)
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
        Long userId = currentUserProvider.getCurrentUserId();
        List<Expense> expenses;

        if (month != null && !month.isBlank()) {
            YearMonth yearMonth = parseYearMonth(month);
            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

            if (search != null && !search.isBlank()) {
                expenses = expenseRepository
                        .findByUserIdAndExpenseDateBetweenAndDescriptionContainingIgnoreCaseOrderByExpenseDateDesc(
                                userId, start, end, search);
            } else if (category != null) {
                expenses = expenseRepository
                        .findByUserIdAndExpenseDateBetweenAndCategoryOrderByExpenseDateDesc(
                                userId, start, end, category);
            } else {
                expenses = expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
                        userId, start, end);
            }
        } else if (category != null) {
            expenses = expenseRepository.findByUserIdAndCategoryOrderByExpenseDateDesc(userId, category);
        } else {
            expenses = expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
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
        Long userId = currentUserProvider.getCurrentUserId();
        YearMonth yearMonth = parseYearMonth(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        BigDecimal total = expenseRepository.sumAmountBetween(userId, start, end);

        List<ExpenseRepository.CategoryTotalProjection> rows =
                expenseRepository.sumAmountByCategoryBetween(userId, start, end);

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
        Long userId = currentUserProvider.getCurrentUserId();
        BigDecimal total = expenseRepository.sumAmount(userId);

        return TotalResponse.builder()
                .total(total)
                .build();
    }

    // ---------- helpers ----------

    private User getCurrentUser() {
        Long userId = currentUserProvider.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // Looks up an expense AND verifies it belongs to the current user in one query -
    // an expense that exists but belongs to someone else is treated as 404, not 403,
    // so we don't leak whether a given id exists at all.
    private Expense findExpenseOrThrow(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        return expenseRepository.findByIdAndUserId(id, userId)
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
