package com.expensetracker.controller;

import com.expensetracker.dto.ApiResponse;
import com.expensetracker.dto.IncomeRequest;
import com.expensetracker.dto.IncomeResponse;
import com.expensetracker.dto.PagedResponse;
import com.expensetracker.security.UserPrincipal;
import com.expensetracker.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<IncomeResponse>>> getIncomes(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "dateReceived") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        PagedResponse<IncomeResponse> response = incomeService.getIncomes(
                currentUser.getId(), query, category, startDate, endDate, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Incomes retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> getIncomeById(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        IncomeResponse income = incomeService.getIncomeById(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Income retrieved successfully", income));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> createIncome(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @Valid @RequestBody IncomeRequest request) {
        IncomeResponse income = incomeService.createIncome(currentUser.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Income created successfully", income), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> updateIncome(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {
        IncomeResponse income = incomeService.updateIncome(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Income updated successfully", income));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncome(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable Long id) {
        incomeService.deleteIncome(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Income deleted successfully"));
    }
}
