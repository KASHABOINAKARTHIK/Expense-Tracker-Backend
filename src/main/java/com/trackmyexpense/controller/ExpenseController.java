package com.trackmyexpense.controller;

import com.trackmyexpense.dto.ExpenseRequest;
import com.trackmyexpense.dto.ExpenseResponse;
import com.trackmyexpense.dto.ExpenseSummaryResponse;
import com.trackmyexpense.dto.TotalResponse;
import com.trackmyexpense.entity.ExpenseCategory;
import com.trackmyexpense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // POST /api/expenses
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/expenses/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // GET /api/expenses?month=2026-08&category=FOOD&search=lunch
    // All query params are optional and can be combined.
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) ExpenseCategory category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(expenseService.getExpenses(month, category, search));
    }

    // PUT /api/expenses/{id}
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(id, request));
    }

    // DELETE /api/expenses/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/expenses/summary?month=2026-08
    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponse> getMonthlySummary(@RequestParam String month) {
        return ResponseEntity.ok(expenseService.getMonthlySummary(month));
    }

    // GET /api/expenses/total  -> running total of all expenses up to now
    @GetMapping("/total")
    public ResponseEntity<TotalResponse> getTotalToDate() {
        return ResponseEntity.ok(expenseService.getTotalToDate());
    }
}
