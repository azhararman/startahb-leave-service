package com.startahb.crm.leave.dto.response;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CompOffDTO {
    private Long id;
    private Long userId;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private String status;
}
