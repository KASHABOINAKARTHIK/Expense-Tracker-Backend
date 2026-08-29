package com.trackmyexpense.entity;

/**
 * Fixed set of expense categories.
 * Stored in the database as a STRING (see @Enumerated(EnumType.STRING) on Expense.category)
 * so the DB column stays human-readable and safe from reordering issues.
 */
public enum ExpenseCategory {
    FOOD,
    TRANSPORT,
    CARE,
    ENTERTAINMENT,
    OTHER
}
