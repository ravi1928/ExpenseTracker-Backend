package com.expensetracker.service;

import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Income;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    public ByteArrayInputStream exportTransactionsCsv(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) startDate = LocalDate.of(2000, 1, 1);
        if (endDate == null) endDate = LocalDate.of(2100, 12, 31);

        List<Expense> expenses = expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        List<Income> incomes = incomeRepository.findByUserIdAndDateReceivedBetween(userId, startDate, endDate);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("ID,Type,Title/Source,Category,Date,Payment Method,Amount,Description");

            for (Income inc : incomes) {
                writer.printf("%d,INCOME,\"%s\",\"%s\",%s,\"%s\",%.2f,\"%s\"\n",
                        inc.getId(),
                        escape(inc.getSource()),
                        escape(inc.getCategory()),
                        inc.getDateReceived(),
                        "Direct",
                        inc.getAmount(),
                        escape(inc.getDescription()));
            }

            for (Expense exp : expenses) {
                writer.printf("%d,EXPENSE,\"%s\",\"%s\",%s,\"%s\",%.2f,\"%s\"\n",
                        exp.getId(),
                        escape(exp.getTitle()),
                        escape(exp.getCategory()),
                        exp.getTransactionDate(),
                        escape(exp.getPaymentMethod()),
                        exp.getAmount(),
                        escape(exp.getDescription()));
            }
            writer.flush();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private String escape(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }
}
