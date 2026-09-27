package com.kajiru.expense_tracker_api.service;

import com.kajiru.expense_tracker_api.exception.ExpenseNotFoundException;
import com.kajiru.expense_tracker_api.model.Expense;
import com.kajiru.expense_tracker_api.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository);
    }

    @Test
    void shouldCreateExpense() {
        Expense expense = createExpense();

        when(expenseRepository.save(expense)).thenReturn(expense);

        Expense result = expenseService.createExpense(expense);

        assertEquals(expense, result);
        verify(expenseRepository).save(expense);
    }

    @Test
    void shouldGetAllExpenses() {
        Expense expense = createExpense();

        when(expenseRepository.findAll())
                .thenReturn(List.of(expense));

        List<Expense> result = expenseService.getAllExpenses();

        assertEquals(1, result.size());
        assertEquals(expense, result.get(0));

        verify(expenseRepository).findAll();
    }

    @Test
    void shouldGetExpenseById() {
        Expense expense = createExpense();

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseById(1L);

        assertEquals(expense, result);

        verify(expenseRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenExpenseDoesNotExist() {
        when(expenseRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ExpenseNotFoundException.class,
                () -> expenseService.getExpenseById(999L)
        );

        verify(expenseRepository).findById(999L);
    }

    @Test
    void shouldUpdateExpense() {
        Expense existingExpense = createExpense();

        Expense updatedExpense = new Expense();
        updatedExpense.setDescription("Updated");
        updatedExpense.setAmount(new BigDecimal("50.00"));
        updatedExpense.setCategory("Transport");
        updatedExpense.setDate(LocalDate.of(2026, 9, 27));

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(existingExpense))
                .thenReturn(existingExpense);

        Expense result =
                expenseService.updateExpense(1L, updatedExpense);

        assertEquals("Updated", result.getDescription());
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals("Transport", result.getCategory());
        assertEquals(
                LocalDate.of(2026, 9, 27),
                result.getDate()
        );

        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(existingExpense);
    }

    @Test
    void shouldDeleteExpense() {
        Expense expense = createExpense();

        when(expenseRepository.findById(1L))
                .thenReturn(Optional.of(expense));

        expenseService.deleteExpense(1L);

        verify(expenseRepository).findById(1L);
        verify(expenseRepository).delete(expense);
    }

    private Expense createExpense() {
        Expense expense = new Expense();

        expense.setDescription("Lebensmittel");
        expense.setAmount(new BigDecimal("25.50"));
        expense.setCategory("Food");
        expense.setDate(LocalDate.of(2026, 9, 27));

        return expense;
    }
}