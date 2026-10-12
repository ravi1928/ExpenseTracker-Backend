package com.expensetracker.controller;

import com.expensetracker.dto.ApiResponse;
import com.expensetracker.dto.ExpenseRequest;
import com.expensetracker.dto.ExpenseResponse;
import com.expensetracker.dto.PagedResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseResponse>>> getExpenses(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "transactionDate") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        PagedResponse<ExpenseResponse> response = expenseService.getExpenses(
                currentUser.getId(), query, category, startDate, endDate, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        ExpenseResponse expense = expenseService.getExpenseById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense retrieved successfully", expense));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse expense = expenseService.createExpense(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Expense created successfully", expense), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse expense = expenseService.updateExpense(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        expenseService.deleteExpense(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully"));
    }
}
