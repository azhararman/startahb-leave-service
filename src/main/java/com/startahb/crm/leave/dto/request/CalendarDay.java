package com.startahb.crm.leave.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CalendarDay    {
    private LocalDate date;
    private String type; // HOLID AY | WEEKEND | CASUAL | SICK ...
    private String name; // Only holiday name

}
