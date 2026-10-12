package com.expensetracker.service;

import com.expensetracker.dto.IncomeRequest;
import com.expensetracker.dto.IncomeResponse;
import com.expensetracker.dto.PagedResponse;
import com.expensetracker.entity.Income;
import com.expensetracker.entity.User;
import com.expensetracker.exception.ResourceNotFoundException;
import com.expensetracker.repository.IncomeRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    public PagedResponse<IncomeResponse> getIncomes(Long userId, String query, String category,
                                                   LocalDate startDate, LocalDate endDate,
                                                   int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Income> incomes = incomeRepository.findWithFilters(userId, query, category, startDate, endDate, pageable);

        List<IncomeResponse> content = incomes.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                incomes.getNumber(),
                incomes.getSize(),
                incomes.getTotalElements(),
                incomes.getTotalPages(),
                incomes.isLast()
        );
    }

    public IncomeResponse getIncomeById(Long userId, Long incomeId) {
        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income", "id", incomeId));
        return mapToResponse(income);
    }

    @Transactional
    public IncomeResponse createIncome(Long userId, IncomeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Income income = new Income();
        income.setUser(user);
        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setCategory(request.getCategory());
        income.setDateReceived(request.getDateReceived());
        income.setDescription(request.getDescription());

        Income savedIncome = incomeRepository.save(income);
        return mapToResponse(savedIncome);
    }

    @Transactional
    public IncomeResponse updateIncome(Long userId, Long incomeId, IncomeRequest request) {
        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income", "id", incomeId));

        income.setSource(request.getSource());
        income.setAmount(request.getAmount());
        income.setCategory(request.getCategory());
        income.setDateReceived(request.getDateReceived());
        income.setDescription(request.getDescription());

        Income updatedIncome = incomeRepository.save(income);
        return mapToResponse(updatedIncome);
    }

    @Transactional
    public void deleteIncome(Long userId, Long incomeId) {
        Income income = incomeRepository.findByIdAndUserId(incomeId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Income", "id", incomeId));
        incomeRepository.delete(income);
    }

    private IncomeResponse mapToResponse(Income income) {
        IncomeResponse response = new IncomeResponse();
        response.setId(income.getId());
        response.setSource(income.getSource());
        response.setAmount(income.getAmount());
        response.setCategory(income.getCategory());
        response.setDateReceived(income.getDateReceived());
        response.setDescription(income.getDescription());
        response.setCreatedAt(income.getCreatedAt());
        response.setUpdatedAt(income.getUpdatedAt());
        return response;
    }
}
