package com.kajiru.expense_tracker_api.exception;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found: " + id);
    }
}