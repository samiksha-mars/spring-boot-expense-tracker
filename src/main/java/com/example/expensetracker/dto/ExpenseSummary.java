package com.example.expensetracker.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExpenseSummary(
        BigDecimal total,
        Map<String, BigDecimal> byCategory
) {
}
