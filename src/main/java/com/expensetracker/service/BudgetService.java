package com.expensetracker.service;

import com.expensetracker.dto.BudgetProgressResponse;
import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.dto.BudgetResponse;
import com.expensetracker.entity.Budget;
import com.expensetracker.entity.User;
import com.expensetracker.exception.BadRequestException;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BudgetProgressResponse> getBudgetsProgress(Long userId, Integer month, Integer year) {
        LocalDate now = LocalDate.now();
        int targetMonth = (month != null) ? month : now.getMonthValue();
        int targetYear = (year != null) ? year : now.getYear();

        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, targetMonth, targetYear);
        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return budgets.stream().map(budget -> {
            BigDecimal spent = expenseRepository.sumByUserIdCategoryAndDateBetween(userId, budget.getCategory(), startDate, endDate);
            if (spent == null) {
                spent = BigDecimal.ZERO;
            }

            BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);
            double percentage = 0.0;
            if (budget.getMonthlyLimit().compareTo(BigDecimal.ZERO) > 0) {
                percentage = spent.divide(budget.getMonthlyLimit(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            BudgetProgressResponse progress = new BudgetProgressResponse();
            progress.setId(budget.getId());
            progress.setCategory(budget.getCategory());
            progress.setMonthlyLimit(budget.getMonthlyLimit());
            progress.setSpentAmount(spent);
            progress.setRemainingAmount(remaining);
            progress.setPercentageConsumed(Math.min(percentage, 999.9));
            progress.setExceeded(spent.compareTo(budget.getMonthlyLimit()) > 0);
            progress.setWarning(percentage >= 80.0);
            progress.setMonth(budget.getMonth());
            progress.setYear(budget.getYear());

            return progress;
        }).collect(Collectors.toList());
    }

    @Transactional
    public BudgetResponse createOrUpdateBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Optional<Budget> existingBudget = budgetRepository.findByUserIdAndCategoryAndMonthAndYear(
                userId, request.getCategory(), request.getMonth(), request.getYear());

        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
            budget.setMonthlyLimit(request.getMonthlyLimit());
        } else {
            budget = new Budget();
            budget.setUser(user);
            budget.setCategory(request.getCategory());
            budget.setMonthlyLimit(request.getMonthlyLimit());
            budget.setMonth(request.getMonth());
            budget.setYear(request.getYear());
        }

        Budget savedBudget = budgetRepository.save(budget);
        return mapToResponse(savedBudget);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget", "id", budgetId));
        budgetRepository.delete(budget);
    }

    private BudgetResponse mapToResponse(Budget budget) {
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setCategory(budget.getCategory());
        response.setMonthlyLimit(budget.getMonthlyLimit());
        response.setMonth(budget.getMonth());
        response.setYear(budget.getYear());
        return response;
    }
}
