package com.startahb.crm.leave.dto.response;

import com.startahb.crm.leave.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LeaveTypeResponse {
    private Long id;
    private LeaveType name;
    private String description;
    private Integer yearlyQuota;
    private Boolean carryForward;
    private Integer maxPerMonth;
    private Boolean active;
}
