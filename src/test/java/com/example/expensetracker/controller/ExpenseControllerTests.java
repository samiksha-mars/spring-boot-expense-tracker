package com.example.expensetracker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExpenseRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    private Expense save(String description, String amount, String category) {
        Expense expense = new Expense();
        expense.setDescription(description);
        expense.setAmount(new BigDecimal(amount));
        expense.setCategory(category);
        expense.setDate(LocalDate.of(2026, 11, 24));
        return repository.save(expense);
    }

    @Test
    void createReturns201WithTheSavedExpense() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "Groceries", "amount": 42.50, "category": "Food", "date": "2026-11-24"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(42.50))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.date").value("2026-11-24"));
    }

    @Test
    void getReturnsExpenseById() throws Exception {
        Expense expense = save("Coffee", "3.20", "Food");

        mockMvc.perform(get("/api/expenses/{id}", expense.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Coffee"));
    }

    @Test
    void listFiltersByCategoryIgnoringCase() throws Exception {
        save("Coffee", "3.20", "Food");
        save("Bus ticket", "2.00", "Transport");

        mockMvc.perform(get("/api/expenses"))
                .andExpect(jsonPath("$", hasSize(2)));
        mockMvc.perform(get("/api/expenses").param("category", "food"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Coffee"));
    }

    @Test
    void updateReplacesFields() throws Exception {
        Expense expense = save("Coffee", "3.20", "Food");

        mockMvc.perform(put("/api/expenses/{id}", expense.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "Lunch", "amount": 12.00, "category": "Food", "date": "2026-11-25"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Lunch"))
                .andExpect(jsonPath("$.date").value("2026-11-25"));
    }

    @Test
    void deleteReturns204() throws Exception {
        Expense expense = save("Coffee", "3.20", "Food");

        mockMvc.perform(delete("/api/expenses/{id}", expense.getId()))
                .andExpect(status().isNoContent());
        assertThat(repository.existsById(expense.getId())).isFalse();
    }

    @Test
    void unknownIdReturns404() throws Exception {
        mockMvc.perform(get("/api/expenses/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
        mockMvc.perform(delete("/api/expenses/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void summaryTotalsByCategory() throws Exception {
        save("Coffee", "3.20", "Food");
        save("Groceries", "42.50", "Food");
        save("Bus ticket", "2.00", "Transport");
        save("Gift", "10.00", null);

        mockMvc.perform(get("/api/expenses/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(57.70))
                .andExpect(jsonPath("$.byCategory.Food").value(45.70))
                .andExpect(jsonPath("$.byCategory.Transport").value(2.00))
                .andExpect(jsonPath("$.byCategory.uncategorized").value(10.00));
    }

    @Test
    void invalidExpenseIsRejected() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "Refund", "amount": -5, "date": "2026-11-24"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Request validation failed"))
                .andExpect(jsonPath("$.errors.amount").value("amount must be positive"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void unparseableDateReturnsProblemDetail() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "Coffee", "amount": 3.20, "date": "24/11/2026"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
