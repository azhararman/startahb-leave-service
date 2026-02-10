package com.startahb.crm.leave.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveValidationRequest {
    private Long employeeId;
    private Long leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
}