package com.startahb.crm.leave.service.impl;
import com.startahb.crm.leave.dto.response.HolidayBulkResponse;
import com.startahb.crm.leave.dto.response.HolidayDto;
import com.startahb.crm.leave.entity.Holiday;
import com.startahb.crm.leave.repository.HolidayRepository;
import com.startahb.crm.leave.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.ArrayList;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    public List<HolidayDto> getAllHolidays()  {
        return holidayRepository.findAll().stream()
                .map(h -> new HolidayDto(h.getDate(), h.getName(), h.getType()))
                .toList();
    }

//    @Override
//    public HolidayDto addHoliday(HolidayDto dto) {
//
//        // Check if   holiday already exists for that date
//        if (holidayRepository.existsByDate(dto.getDate())) {
//            throw new RuntimeException("Holiday already exists for this date!");
//        }
//
//        Holiday holiday = Holiday.builder()
//                .date(dto.getDate())
//                .name(dto.getName())
//                .type(dto.getType())
//                .build();
//
//        holidayRepository.save(holiday);
//
//        return new HolidayDto(holiday.getDate(), holiday.getName(), holiday.getType());
//    }

    @Override
    public HolidayBulkResponse addMultipleHolidays(List<HolidayDto> dtos) {

        List<HolidayDto> saved = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (HolidayDto dto : dtos) {

            boolean exists = holidayRepository.existsByDate(dto.getDate());
            if (exists) {
                skipped.add(dto.getName() + " already exists");
                continue;
            }

            Holiday holiday = Holiday.builder()
                    .date(dto.getDate())
                    .name(dto.getName())
                    .type(dto.getType())
                    .build();

            holidayRepository.save(holiday);
            saved.add(dto);
        }

        return new HolidayBulkResponse(saved, skipped);
    }


}