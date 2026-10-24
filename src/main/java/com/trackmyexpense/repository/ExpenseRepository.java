package com.trackmyexpense.repository;

import com.trackmyexpense.entity.Expense;
import com.trackmyexpense.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // --- Paginated versions (recommended) ---
    Page<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Expense> findByUserIdAndExpenseDateBetweenAndCategoryOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end, ExpenseCategory category, Pageable pageable);

    Page<Expense> findByUserIdAndCategoryOrderByExpenseDateDesc(
            Long userId, ExpenseCategory category, Pageable pageable);

    Page<Expense> findByUserIdOrderByExpenseDateDesc(Long userId, Pageable pageable);

    Page<Expense> findByUserIdAndExpenseDateBetweenAndDescriptionContainingIgnoreCaseOrderByExpenseDateDesc(
            Long userId, LocalDateTime start, LocalDateTime end, String keyword, Pageable pageable);

    // Keep the old non-paginated methods if you still need them temporarily (optional)
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

    // Category-wise sums within a date range
    @Query("SELECT e.category AS category, COALESCE(SUM(e.amount), 0) AS total " +
           "FROM Expense e WHERE e.user.id = :userId AND e.expenseDate >= :start AND e.expenseDate < :end " +
           "GROUP BY e.category")
    List<CategoryTotalProjection> sumAmountByCategoryBetween(
            @Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    interface CategoryTotalProjection {
        ExpenseCategory getCategory();
        BigDecimal getTotal();
    }
}