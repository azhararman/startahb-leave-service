package com.startahb.crm.leave.repository;

import com.startahb.crm.leave.entity.LeaveBalance;
import com.startahb.crm.leave.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByUserId(Long userId);

    Optional<LeaveBalance> findByUserIdAndLeaveType(Long userId, LeaveType leaveType);

    boolean existsByUserIdAndLeaveType(Long userId, LeaveType leaveType);
}