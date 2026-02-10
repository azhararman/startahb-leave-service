package com.startahb.crm.leave.repository;

import com.startahb.crm.leave.entity.LeaveRequest;
import com.startahb.crm.leave.enums.LeaveStatus;
import com.startahb.crm.leave.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long>  {

    List<LeaveRequest> findByUserId(Long userId);
    List<LeaveRequest> findByUserIdAndStartDateAfter(Long userId, LocalDate date);//upcoming
    /**
     * COUNT APPROVED LEAVES FOR BALANCE CALCULATION
     * ⭐⭐ ADDED THIS METHOD (DO NOT REMOVE OTHER CODE)
     */
    int countByUserIdAndLeaveTypeAndStatus(Long userId, LeaveType leaveType, LeaveStatus status);


    /**
     * Checks if the user already has ANY leave entry overlapping the given date range.
     * Includes:
     *  - Approved leaves
     *  - Pending leaves
     *  - Optional holidays (stored in same table)
     *
     * Excludes:
     *  - Rejected leaves
     */
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM LeaveRequest l
        WHERE l.userId = :userId
          AND l.status != 'REJECTED'
          AND (
                 (l.startDate <= :endDate AND l.endDate >= :startDate)
              )
    """)
    boolean existsOverlapForUser(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    /**
     * Checks if the given date range overlaps with ANY optional holiday.
     */
    @Query("""
        SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
        FROM LeaveRequest l
        WHERE l.leaveType = com.startahb.crm.leave.enums.LeaveType.OPTIONAL_HOLIDAY
          AND l.startDate <= :endDate
          AND l.endDate >= :startDate
    """)
    boolean existsHolidayOverlap(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<LeaveRequest> findByStatus(LeaveStatus status);


}
