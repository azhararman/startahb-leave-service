package com.startahb.crm.leave.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class HolidayBulkResponse{
    private List<HolidayDto> saved;
    private List<String> skipped;
}
