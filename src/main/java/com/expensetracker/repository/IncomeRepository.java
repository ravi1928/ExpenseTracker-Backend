package com.expensetracker.repository;

import com.expensetracker.entity.Income;
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
public interface IncomeRepository extends JpaRepository<Income, Long> {
    Page<Income> findByUserId(Long userId, Pageable pageable);
    Optional<Income> findByIdAndUserId(Long id, Long userId);
    List<Income> findByUserIdAndDateReceivedBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Income> findByUserIdOrderByDateReceivedDesc(Long userId);

    @Query("SELECT i FROM Income i WHERE i.user.id = :userId AND " +
           "(:query IS NULL OR LOWER(i.source) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:startDate IS NULL OR i.dateReceived >= :startDate) AND " +
           "(:endDate IS NULL OR i.dateReceived <= :endDate)")
    Page<Income> findWithFilters(@Param("userId") Long userId,
                                 @Param("query") String query,
                                 @Param("category") String category,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 Pageable pageable);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId")
    BigDecimal sumTotalByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(i.amount) FROM Income i WHERE i.user.id = :userId AND i.dateReceived BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserIdAndDateBetween(@Param("userId") Long userId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
