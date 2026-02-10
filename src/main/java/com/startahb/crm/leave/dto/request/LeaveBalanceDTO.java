package com.startahb.crm.leave.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class LeaveBalanceDTO {

    private Long employeeId;

    private List<LeaveTypeBalance> balances;

    @Data
    public static class LeaveTypeBalance {
        private String leaveType;
        private int total;
        private int used;
        private int remaining;
    }
}
