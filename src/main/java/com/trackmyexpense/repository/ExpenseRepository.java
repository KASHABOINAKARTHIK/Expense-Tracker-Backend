package com.trackmyexpense.repository;

import com.trackmyexpense.entity.Expense;
import com.trackmyexpense.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Fetch expenses within a date range (used for month filtering), newest first
    List<Expense> findByExpenseDateBetweenOrderByExpenseDateDesc(
            LocalDateTime start, LocalDateTime end);

    // Fetch expenses within a date range AND matching category
    List<Expense> findByExpenseDateBetweenAndCategoryOrderByExpenseDateDesc(
            LocalDateTime start, LocalDateTime end, ExpenseCategory category);

    // Fetch all expenses matching category only (no date range)
    List<Expense> findByCategoryOrderByExpenseDateDesc(ExpenseCategory category);

    // Fetch all expenses, newest first (default listing)
    List<Expense> findAllByOrderByExpenseDateDesc();

    // Case-insensitive description search within a date range
    List<Expense> findByExpenseDateBetweenAndDescriptionContainingIgnoreCaseOrderByExpenseDateDesc(
            LocalDateTime start, LocalDateTime end, String keyword);

    // Sum of all expense amounts with expenseDate <= given timestamp ("total to date")
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e")
    BigDecimal sumAmount();

    // Sum of amounts within a date range (used for monthly total)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate >= :start AND e.expenseDate < :end")
    BigDecimal sumAmountBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Category-wise sums within a date range - used for the monthly summary/dashboard breakdown
    @Query("SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total " +
           "FROM Expense e WHERE e.expenseDate >= :start AND e.expenseDate < :end " +
           "GROUP BY e.category")
    List<CategoryTotalProjection> sumAmountByCategoryBetween(
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Projection interface for the category-wise GROUP BY query above.
     * Spring Data JPA maps the "category" and "total" aliases to these getters automatically.
     */
    interface CategoryTotalProjection {
        ExpenseCategory getCategory();
        BigDecimal getTotal();
    }
}
