package com.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private BigDecimal totalBalance;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal monthlySavings;
    private double savingsRate;
    private String highestSpendingCategory;
    private BigDecimal highestSpendingCategoryAmount;

    private List<CategorySpendingResponse> categoryBreakdown;
    private List<MonthlyTrendResponse> monthlyTrends;
    private List<BudgetProgressResponse> budgetProgressList;
    private List<TransactionItemResponse> recentTransactions;

    public DashboardSummaryResponse() {}

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getMonthlySavings() {
        return monthlySavings;
    }

    public void setMonthlySavings(BigDecimal monthlySavings) {
        this.monthlySavings = monthlySavings;
    }

    public double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public String getHighestSpendingCategory() {
        return highestSpendingCategory;
    }

    public void setHighestSpendingCategory(String highestSpendingCategory) {
        this.highestSpendingCategory = highestSpendingCategory;
    }

    public BigDecimal getHighestSpendingCategoryAmount() {
        return highestSpendingCategoryAmount;
    }

    public void setHighestSpendingCategoryAmount(BigDecimal highestSpendingCategoryAmount) {
        this.highestSpendingCategoryAmount = highestSpendingCategoryAmount;
    }

    public List<CategorySpendingResponse> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategorySpendingResponse> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<MonthlyTrendResponse> getMonthlyTrends() {
        return monthlyTrends;
    }

    public void setMonthlyTrends(List<MonthlyTrendResponse> monthlyTrends) {
        this.monthlyTrends = monthlyTrends;
    }

    public List<BudgetProgressResponse> getBudgetProgressList() {
        return budgetProgressList;
    }

    public void setBudgetProgressList(List<BudgetProgressResponse> budgetProgressList) {
        this.budgetProgressList = budgetProgressList;
    }

    public List<TransactionItemResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionItemResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
