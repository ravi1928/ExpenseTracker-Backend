package com.expensetracker.controller;

import com.expensetracker.dto.ApiResponse;
import com.expensetracker.dto.BudgetProgressResponse;
import com.expensetracker.dto.BudgetRequest;
import com.expensetracker.dto.BudgetResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetProgressResponse>>> getBudgets(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {
        List<BudgetProgressResponse> budgets = budgetService.getBudgetsProgress(currentUser.getId(), month, year);
        return ResponseEntity.ok(ApiResponse.success("Budgets retrieved successfully", budgets));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> saveBudget(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody BudgetRequest request) {
        BudgetResponse response = budgetService.createOrUpdateBudget(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Budget saved successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        budgetService.deleteBudget(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Budget deleted successfully"));
    }
}
