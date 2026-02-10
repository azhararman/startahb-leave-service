package com.startahb.crm.leave.service;


import com.startahb.crm.leave.dto.response.HolidayBulkResponse;
import com.startahb.crm.leave.dto.response.HolidayDto;

import java.util.List;

public interface HolidayService {
    List<HolidayDto> getAllHolidays();
    //HolidayDto addHoliday(HolidayDto dto);
    HolidayBulkResponse addMultipleHolidays(List<HolidayDto> dtos);

}
