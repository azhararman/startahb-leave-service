package com.startahb.crm.leave.controller;



import com.startahb.crm.leave.dto.response.HolidayBulkResponse;
import com.startahb.crm.leave.dto.response.HolidayDto;
import com.startahb.crm.leave.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController  {

    private final HolidayService holidayService;

    @GetMapping
    public ResponseEntity<List<HolidayDto>> getAllHolidays() {
        return ResponseEntity.ok(holidayService.getAllHolidays());
    }

    //    @PostMapping
//    @PreAuthorize("hasRole('MGR')")   // 🛑 Only Manager can add holidays
//    public ResponseEntity<?> addHoliday(@RequestBody HolidayDto dto) {
//        HolidayDto saved =  holidayService.addHoliday(dto);
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "message", "Holiday added successfully",
//                        "data", saved
//                )
//        );
//    }
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('MGR')")
    public ResponseEntity<?> addMultipleHolidays(@RequestBody List<HolidayDto> dtos) {
        HolidayBulkResponse response = holidayService.addMultipleHolidays(dtos);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Holiday processing completed",
                        "saved", response.getSaved(),
                        "skipped", response.getSkipped()
                )
        );
    }
}


