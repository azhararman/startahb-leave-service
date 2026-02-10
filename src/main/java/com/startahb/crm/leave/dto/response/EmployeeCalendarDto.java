package com.startahb.crm.leave.dto.response;


import com.startahb.crm.leave.entity.LeaveRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCalendarDto{
    // <-- Added

    private List<HolidayDto> holidays;
    private List<LeaveRequest> leaves;
    private Map<String, String> statusMap;
}
