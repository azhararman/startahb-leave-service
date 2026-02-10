package com.startahb.crm.leave.dto.response;
import com.startahb.crm.leave.dto.request.CalendarDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarResponse   {
    private int year;
    private int month;
    private List<CalendarDay> calendar;
}
