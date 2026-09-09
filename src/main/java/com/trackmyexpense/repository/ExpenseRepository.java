package com.trackmyexpense.repository;

import com.trackmyexpense.entity.Expense;
import com.trackmyexpense.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Every query is scoped to userId so one user can never see/modify another's data.

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    List<Expense> findByUserIdAndExpenseDateBetweenAndCategoryOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end, ExpenseCategory category);

    List<Expense> findByUserIdAndCategoryOrderByExpenseDateDesc(Long userId, ExpenseCategory category);

    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    List<Expense> findByUserIdAndExpenseDateBetweenAndDescriptionContainingIgnoreCaseOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end, String keyword);

    // Sum of all of this user's expenses ("total to date")
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal sumAmount(@Param("userId") Long userId);

    // Sum of this user's amounts within a date range (used for monthly total)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.expenseDate >= :start AND e.expenseDate < :end")
    BigDecimal sumAmountBetween(
            @Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Category-wise sums within a date range, scoped to this user - powers the monthly dashboard breakdown
    @Query("SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total " +
           "FROM Expense e WHERE e.user.id = :userId AND e.expenseDate >= :start AND e.expenseDate < :end " +
           "GROUP BY e.category")
    List<CategoryTotalProjection> sumAmountByCategoryBetween(
            @Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Projection interface for the category-wise GROUP BY query above.
     * Spring Data JPA maps the "category" and "total" aliases to these getters automatically.
     */
    interface CategoryTotalProjection {
        ExpenseCategory getCategory();
        BigDecimal getTotal();
    }
}
