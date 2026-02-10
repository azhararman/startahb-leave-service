package com.startahb.crm.leave.service;

import com.startahb.crm.leave.dto.request.LeaveBalanceDTO;

import java.util.List;

public interface LeaveBalanceService {

    LeaveBalanceDTO getLeaveBalance(Long userId);

    String generateYearlyBalance();

    String updateBalance(Long userId, LeaveBalanceDTO dto);

    List<LeaveBalanceDTO> getReport();
}
