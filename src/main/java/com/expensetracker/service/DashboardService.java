package com.expensetracker.service;

import com.expensetracker.dto.*;
import com.expensetracker.entity.Expense;
import com.expensetracker.entity.Income;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private BudgetService budgetService;

    public DashboardSummaryResponse getDashboardSummary(Long userId, String range, LocalDate customStart, LocalDate customEnd) {
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = now;

        if ("this_week".equalsIgnoreCase(range)) {
            startDate = now.minusDays(now.getDayOfWeek().getValue() - 1);
        } else if ("last_month".equalsIgnoreCase(range)) {
            YearMonth lastM = YearMonth.now().minusMonths(1);
            startDate = lastM.atDay(1);
            endDate = lastM.atEndOfMonth();
        } else if ("this_year".equalsIgnoreCase(range)) {
            startDate = LocalDate.of(now.getYear(), 1, 1);
        } else if ("custom".equalsIgnoreCase(range) && customStart != null && customEnd != null) {
            startDate = customStart;
            endDate = customEnd;
        } else { // default "this_month"
            startDate = LocalDate.of(now.getYear(), now.getMonthValue(), 1);
        }

        // Totals
        BigDecimal totalIncome = incomeRepository.sumByUserIdAndDateBetween(userId, startDate, endDate);
        if (totalIncome == null) totalIncome = BigDecimal.ZERO;

        BigDecimal totalExpenses = expenseRepository.sumByUserIdAndDateBetween(userId, startDate, endDate);
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        BigDecimal totalLifetimeIncome = incomeRepository.sumTotalByUserId(userId);
        if (totalLifetimeIncome == null) totalLifetimeIncome = BigDecimal.ZERO;

        BigDecimal totalLifetimeExpenses = expenseRepository.sumTotalByUserId(userId);
        if (totalLifetimeExpenses == null) totalLifetimeExpenses = BigDecimal.ZERO;

        BigDecimal totalBalance = totalLifetimeIncome.subtract(totalLifetimeExpenses);
        BigDecimal monthlySavings = totalIncome.subtract(totalExpenses);

        double savingsRate = 0.0;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = monthlySavings.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }

        // Category Breakdown
        List<Object[]> categoryResults = expenseRepository.findCategorySpendingByUserIdAndDateBetween(userId, startDate, endDate);
        List<CategorySpendingResponse> categoryBreakdown = new ArrayList<>();
        String highestCategory = "N/A";
        BigDecimal highestCategoryAmount = BigDecimal.ZERO;

        for (Object[] result : categoryResults) {
            String cat = (String) result[0];
            BigDecimal amt = (BigDecimal) result[1];

            double pct = 0.0;
            if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
                pct = amt.divide(totalExpenses, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            }

            categoryBreakdown.add(new CategorySpendingResponse(cat, amt, pct));

            if (amt.compareTo(highestCategoryAmount) > 0) {
                highestCategoryAmount = amt;
                highestCategory = cat;
            }
        }

        // Monthly Trends (Last 6 Months)
        List<MonthlyTrendResponse> monthlyTrends = new ArrayList<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate mStart = ym.atDay(1);
            LocalDate mEnd = ym.atEndOfMonth();

            BigDecimal mInc = incomeRepository.sumByUserIdAndDateBetween(userId, mStart, mEnd);
            if (mInc == null) mInc = BigDecimal.ZERO;

            BigDecimal mExp = expenseRepository.sumByUserIdAndDateBetween(userId, mStart, mEnd);
            if (mExp == null) mExp = BigDecimal.ZERO;

            BigDecimal mSav = mInc.subtract(mExp);

            monthlyTrends.add(new MonthlyTrendResponse(ym.format(monthFormatter), mInc, mExp, mSav));
        }

        // Budget Progress
        List<BudgetProgressResponse> budgetProgress = budgetService.getBudgetsProgress(userId, now.getMonthValue(), now.getYear());

        // Recent Unified Transactions (Top 10)
        List<Expense> expensesList = expenseRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        List<Income> incomesList = incomeRepository.findByUserIdAndDateReceivedBetween(userId, startDate, endDate);

        List<TransactionItemResponse> recentTransactions = new ArrayList<>();
        for (Expense e : expensesList) {
            TransactionItemResponse t = new TransactionItemResponse();
            t.setId(e.getId());
            t.setType("EXPENSE");
            t.setTitle(e.getTitle());
            t.setAmount(e.getAmount());
            t.setCategory(e.getCategory());
            t.setDate(e.getTransactionDate());
            t.setPaymentMethod(e.getPaymentMethod());
            t.setDescription(e.getDescription());
            recentTransactions.add(t);
        }
        for (Income inc : incomesList) {
            TransactionItemResponse t = new TransactionItemResponse();
            t.setId(inc.getId());
            t.setType("INCOME");
            t.setTitle(inc.getSource());
            t.setAmount(inc.getAmount());
            t.setCategory(inc.getCategory());
            t.setDate(inc.getDateReceived());
            t.setPaymentMethod("Direct Transfer");
            t.setDescription(inc.getDescription());
            recentTransactions.add(t);
        }

        recentTransactions.sort((a, b) -> b.getDate().compareTo(a.getDate()));
        if (recentTransactions.size() > 10) {
            recentTransactions = recentTransactions.subList(0, 10);
        }

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        response.setTotalBalance(totalBalance);
        response.setTotalIncome(totalIncome);
        response.setTotalExpenses(totalExpenses);
        response.setMonthlySavings(monthlySavings);
        response.setSavingsRate(savingsRate);
        response.setHighestSpendingCategory(highestCategory);
        response.setHighestSpendingCategoryAmount(highestCategoryAmount);
        response.setCategoryBreakdown(categoryBreakdown);
        response.setMonthlyTrends(monthlyTrends);
        response.setBudgetProgressList(budgetProgress);
        response.setRecentTransactions(recentTransactions);

        return response;
    }
}
