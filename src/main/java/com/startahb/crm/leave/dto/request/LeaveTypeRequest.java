package com.startahb.crm.leave.dto.request;
import com.startahb.crm.leave.enums.LeaveType;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


@Data
public class LeaveTypeRequest {

    @NotNull(message = "Leave type is required")
    private LeaveType name;

    private String description;

    @NotNull
    @Min(0)
    private Integer yearlyQuota;

    @NotNull
    private Boolean carryForward;

    @Min(0)
    private Integer maxPerMonth;
}
