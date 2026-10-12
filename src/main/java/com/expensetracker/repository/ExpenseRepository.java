package com.expensetracker.repository;

import com.expensetracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    Page<Expense> findByUserId(Long userId, Pageable pageable);
    Optional<Expense> findByIdAndUserId(Long id, Long userId);
    List<Expense> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Expense> findByUserIdOrderByTransactionDateDesc(Long userId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND " +
           "(:query IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:startDate IS NULL OR e.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.transactionDate <= :endDate)")
    Page<Expense> findWithFilters(@Param("userId") Long userId,
                                  @Param("query") String query,
                                  @Param("category") String category,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate,
                                  Pageable pageable);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal sumTotalByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndDateBetween(@Param("userId") Long userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId AND e.category = :category AND e.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdCategoryAndDateBetween(@Param("userId") Long userId,
                                                 @Param("category") String category,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category AS category, SUM(e.amount) AS totalAmount FROM Expense e WHERE e.user.id = :userId AND e.transactionDate BETWEEN :startDate AND :endDate GROUP BY e.category ORDER BY totalAmount DESC")
    List<Object[]> findCategorySpendingByUserIdAndDateBetween(@Param("userId") Long userId,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);
}
