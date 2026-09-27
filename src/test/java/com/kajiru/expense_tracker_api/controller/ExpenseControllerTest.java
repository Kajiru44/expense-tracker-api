package com.kajiru.expense_tracker_api.controller;
import tools.jackson.databind.ObjectMapper;


import com.kajiru.expense_tracker_api.exception.ExpenseNotFoundException;
import com.kajiru.expense_tracker_api.exception.GlobalExceptionHandler;
import com.kajiru.expense_tracker_api.model.Expense;
import com.kajiru.expense_tracker_api.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@Import(GlobalExceptionHandler.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseService expenseService;

    @Test
    void shouldCreateExpense() throws Exception {
        Expense expense = createExpense();

        when(expenseService.createExpense(any(Expense.class)))
                .thenReturn(expense);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Lebensmittel"))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.category").value("Food"));

        verify(expenseService).createExpense(any(Expense.class));
    }

    @Test
    void shouldGetAllExpenses() throws Exception {
        Expense expense = createExpense();

        when(expenseService.getAllExpenses())
                .thenReturn(List.of(expense));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Lebensmittel"))
                .andExpect(jsonPath("$[0].category").value("Food"));

        verify(expenseService).getAllExpenses();
    }

    @Test
    void shouldGetExpenseById() throws Exception {
        Expense expense = createExpense();

        when(expenseService.getExpenseById(1L))
                .thenReturn(expense);

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Lebensmittel"))
                .andExpect(jsonPath("$.amount").value(25.50));

        verify(expenseService).getExpenseById(1L);
    }

    @Test
    void shouldReturn404WhenExpenseDoesNotExist() throws Exception {
        when(expenseService.getExpenseById(999L))
                .thenThrow(new ExpenseNotFoundException(999L));

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Expense not found: 999"));

        verify(expenseService).getExpenseById(999L);
    }

    @Test
    void shouldUpdateExpense() throws Exception {
        Expense expense = createExpense();
        expense.setDescription("Updated");

        when(expenseService.updateExpense(eq(1L), any(Expense.class)))
                .thenReturn(expense);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"));

        verify(expenseService)
                .updateExpense(eq(1L), any(Expense.class));
    }

    @Test
    void shouldDeleteExpense() throws Exception {
        doNothing().when(expenseService).deleteExpense(1L);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(1L);
    }

    @Test
    void shouldRejectInvalidExpense() throws Exception {
        Expense invalidExpense = new Expense();
        invalidExpense.setDescription("");
        invalidExpense.setAmount(new BigDecimal("-5"));
        invalidExpense.setCategory("");
        invalidExpense.setDate(null);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidExpense)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description")
                        .value("Description must not be blank"))
                .andExpect(jsonPath("$.amount")
                        .value("Amount must be greater than 0"))
                .andExpect(jsonPath("$.category")
                        .value("Category must not be blank"))
                .andExpect(jsonPath("$.date")
                        .value("Date is required"));

        verifyNoInteractions(expenseService);
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