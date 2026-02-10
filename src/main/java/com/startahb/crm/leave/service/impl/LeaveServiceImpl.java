package com.startahb.crm.leave.service.impl;


import com.startahb.crm.leave.dto.request.CalendarDay;
import com.startahb.crm.leave.dto.response.CalendarResponse;
import com.startahb.crm.leave.dto.response.EmployeeCalendarDto;
import com.startahb.crm.leave.dto.response.HolidayDto;
import com.startahb.crm.leave.entity.LeaveRequest;
import com.startahb.crm.leave.enums.LeaveStatus;
import com.startahb.crm.leave.enums.LeaveType;
import com.startahb.crm.leave.exception.ResourceNotFoundException;
import com.startahb.crm.leave.exception.UnauthorizedActionException;
import com.startahb.crm.leave.repository.LeaveRepository;
import com.startahb.crm.leave.service.HolidayService;
import com.startahb.crm.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
//import startup.backend.entity.Manager;

import org.springframework.stereotype.Service;

//import startup.backend.repository.ManagerRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final HolidayService holidayService;  // ⭐ ADD    THIS HER

    // private final ManagerRepository managerRepository;


    @Override
    public LeaveRequest applyLeave(LeaveRequest leaveRequest, Long userId) {

        LocalDate today = LocalDate.now();

        // 1️⃣ Date validation
        if (leaveRequest.getStartDate().isBefore(today) ||
                leaveRequest.getEndDate().isBefore(today)) {
            throw new IllegalArgumentException("Leave dates cannot be in the past.");
        }

        if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        // 2️⃣ Leave type required
        if (leaveRequest.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type is required.");
        }

        LeaveType type = leaveRequest.getLeaveType();


        // 3️⃣ Check if applying leave on OPTIONAL_HOLIDAY
        boolean holidayOverlap = leaveRepository.existsHolidayOverlap(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        if (holidayOverlap) {
            throw new IllegalArgumentException("Leave cannot be applied during an Optional Holiday.");
        }


        // 4️⃣ Overlap check for user's own leaves
        boolean overlap = leaveRepository.existsOverlapForUser(
                userId,
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        );

        if (overlap) {
            throw new IllegalArgumentException("This leave overlaps with an existing leave.");
        }


        // 5️⃣ Calculate total leave days
        long totalDays = ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        ) + 1;


        // 6️⃣SICK LEAVE RULE (More Than 2 Days)
        if (type == LeaveType.SICK) {

            if (totalDays > 2) {

                if (leaveRequest.getAttachmentUrl() == null ||
                        leaveRequest.getAttachmentUrl().trim().isEmpty()) {

                    throw new IllegalArgumentException(
                            "Doctor certificate is required for sick leave exceeding 2 days."
                    );
                }
            }
        }


        // 7️⃣ CRM-specific leave validations
        switch (type) {

            case WFH:
                if (leaveRequest.getReason() == null || leaveRequest.getReason().trim().isEmpty()) {
                    throw new IllegalArgumentException("WFH requires a valid reason.");
                }
                break;

            case OPTIONAL_HOLIDAY:
                if (!leaveRequest.getStartDate().equals(leaveRequest.getEndDate())) {
                    throw new IllegalArgumentException("Optional Holiday must be only for one day.");
                }
                break;

            case COMP_OFF:
                validateCompOff(leaveRequest);
                break;

            case MATERNITY:
            case PATERNITY:
                if (leaveRequest.getReason() == null || leaveRequest.getReason().trim().isEmpty()) {
                    throw new IllegalArgumentException("Maternity/Paternity leave requires a reason.");
                }
                break;

            default:
                break;
        }

        leaveRequest.setUserId(userId);
        leaveRequest.setStatus(LeaveStatus.PENDING);


        return leaveRepository.save(leaveRequest);
    }

    @Override
    public LeaveRequest updateLeaveStatus(Long id, LeaveStatus status) {
        LeaveRequest leave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        leave.setStatus(status);
        return leaveRepository.save(leave);
    }

    @Override
    public List<LeaveRequest> getAllLeaves() {
        return leaveRepository.findAll();
    }

    @Override
    public List<LeaveRequest> getLeavesByUserId(Long userId) {
        return leaveRepository.findByUserId(userId);
    }

    @Override
    public LeaveRequest updateUserLeave(Long id, Long userId, LeaveRequest updatedLeave) {
        LeaveRequest existingLeave = leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + id));

        if (!existingLeave.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only update your own leave request.");
        }

        if (existingLeave.getStatus() != LeaveStatus.PENDING) {
            throw new UnauthorizedActionException("Only pending leaves can be updated.");
        }

        if (updatedLeave.getLeaveType() == null) {
            throw new IllegalArgumentException("Leave type cannot be null when updating leave.");
        }

        existingLeave.setReason(updatedLeave.getReason());
        existingLeave.setStartDate(updatedLeave.getStartDate());
        existingLeave.setEndDate(updatedLeave.getEndDate());
        existingLeave.setLeaveType(updatedLeave.getLeaveType());

        return leaveRepository.save(existingLeave);
    }

    @Override
    public void deleteLeaveById(Long leaveId, Long userId) {
        LeaveRequest leaveRequest = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found."));

        if (!leaveRequest.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only delete your own leave request.");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new UnauthorizedActionException("Only pending leaves can be deleted.");
        }

        leaveRepository.delete(leaveRequest);
    }

    @Override
    public void cancelLeave(Long leaveId, Long userId) {

        LeaveRequest leaveRequest = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found."));

        if (!leaveRequest.getUserId().equals(userId)) {
            throw new UnauthorizedActionException("You can only cancel your own leave request.");
        }

        if (leaveRequest.getStatus() != LeaveStatus.PENDING) {
            throw new UnauthorizedActionException("Only pending leaves can be cancelled.");
        }

        leaveRequest.setStatus(LeaveStatus.CANCELLED);
        leaveRepository.save(leaveRequest);
    }


    @Override
    public EmployeeCalendarDto getEmployeeCalendar(Long employeeId) {

        // Load holidays from database
        List<HolidayDto> holidays = holidayService.getAllHolidays();

        // Load employee leaves
        List<LeaveRequest> leaves = leaveRepository.findByUserId(employeeId);

        // Map to store status for calendar visualization
        Map<String, String> statusMap = new HashMap<>();

        // Put leave entries into status map
        leaves.forEach(leave -> statusMap.put(
                leave.getStartDate().toString(),        // date
                leave.getLeaveType().name()             // type (CL, SL, etc.)
        ));

        // Put holidays into status map
        holidays.forEach(holiday -> statusMap.put(
                holiday.getDate().toString(),
                holiday.getName()
        ));

        // return the full response DTO
        return new EmployeeCalendarDto(holidays, leaves, statusMap);
    }

    @Override
    public CalendarResponse generateMonthlyCalendar(Long userId, int year, int month) {

        List<CalendarDay> days = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, 1);

        List<HolidayDto> holidays = holidayService.getAllHolidays();
        List<LeaveRequest> leaves = leaveRepository.findByUserId(userId);

        while (date.getMonthValue() == month) {

            LocalDate finalDate = date;

            CalendarDay day = new CalendarDay();
            day.setDate(date);
            day.setType("NORMAL");
            day.setName(null);

            // WEEKEND
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                day.setType("WEEKEND");
                day.setName(date.getDayOfWeek().toString().substring(0, 1) +
                        date.getDayOfWeek().toString().substring(1).toLowerCase());
            }

            // HOLIDAY (festival)
            holidays.stream()
                    .filter(h -> h.getDate().equals(finalDate))
                    .findFirst()
                    .ifPresent(holiday -> {
                        day.setType("HOLIDAY");
                        day.setName(holiday.getName());
                    });

            // LEAVE
            leaves.stream()
                    .filter(l -> l.getStartDate().equals(finalDate))
                    .findFirst()
                    .ifPresent(l -> day.setType(l.getLeaveType().name()));

            days.add(day);
            date = date.plusDays(1);
        }

        return new CalendarResponse(year, month, days);
    }

    @Override
    public void approveLeave(Long leaveId, Long managerId, String comment) {

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() == LeaveStatus.APPROVED) {
            throw new IllegalArgumentException("Leave already approved.");
        }
        if (leave.getStatus() == LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Cannot approve a rejected leave.");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(managerId);
        leave.setManagerComment(comment);

        leaveRepository.save(leave);
    }

    @Override
    public void rejectLeave(Long leaveId, Long managerId, String comment) {

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));

        if (leave.getStatus() == LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Leave already rejected.");
        }
        if (leave.getStatus() == LeaveStatus.APPROVED) {
            throw new IllegalArgumentException("Cannot reject an approved leave.");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(managerId);
        leave.setManagerComment(comment);

        leaveRepository.save(leave);
    }

    @Override
    public List<LeaveRequest> getLeavesByStatus(LeaveStatus status) {
        return leaveRepository.findByStatus(status);
    }

    @Override
    public List<Map<String, Object>> getTeamCalendar() {

        List<LeaveRequest> approvedLeaves = leaveRepository.findByStatus(LeaveStatus.APPROVED);

        List<Map<String, Object>> calendar = new ArrayList<>();

        for (LeaveRequest leave : approvedLeaves) {

            LocalDate current = leave.getStartDate();

            while (!current.isAfter(leave.getEndDate())) {

                Map<String, Object> entry = new HashMap<>();
                entry.put("date", current.toString());
                entry.put("employeeId", leave.getUserId());
                entry.put("type", leave.getLeaveType().name());
                entry.put("leaveId", leave.getId());

                calendar.add(entry);

                current = current.plusDays(1);
            }
        }

        return calendar;
    }

    @Override
    public boolean validateLeave(Long leaveTypeId, Long employeeId, LocalDate startDate, LocalDate endDate) {

        if (endDate.isBefore(startDate)) return false;
        if (startDate.isBefore(LocalDate.now())) return false;

        boolean overlap = leaveRepository.existsOverlapForUser(employeeId, startDate, endDate);
        if (overlap) return false;

        boolean holidayOverlap = leaveRepository.existsHolidayOverlap(startDate, endDate);
        if (holidayOverlap) return false;

        return true;
    }
    public Map<String, Object> getLeaveDetails(Long leaveId) {

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("leaveId", leave.getId());
        map.put("employeeId", leave.getUserId());
        map.put("leaveType", leave.getLeaveType().name());
        map.put("startDate", leave.getStartDate());
        map.put("endDate", leave.getEndDate());
        map.put("status", leave.getStatus().name());
        map.put("reason", leave.getReason());
        map.put("managerComment", leave.getManagerComment());

        return map;
    }

    @Override
    public List<Map<String, Object>> getUpcomingLeaves(Long employeeId) {

        LocalDate today = LocalDate.now();

        List<LeaveRequest> upcoming = leaveRepository
                .findByUserIdAndStartDateAfter(employeeId, today);

        List<Map<String, Object>> result = new ArrayList<>();

        for (LeaveRequest leave : upcoming) {
            Map<String, Object> map = new HashMap<>();
            map.put("leaveId", leave.getId());
            map.put("from", leave.getStartDate());
            map.put("to", leave.getEndDate());
            result.add(map);
        }
        return result;
    }

    // ----------------------------------------------------
// COMP-OFF VALIDATION (PASTE THIS INSIDE YOUR CLASS)
// ----------------------------------------------------
    private void validateCompOff(LeaveRequest leaveRequest) {

        LocalDate start = leaveRequest.getStartDate();
        LocalDate end = leaveRequest.getEndDate();

        // 1) Reason required
        if (leaveRequest.getReason() == null || leaveRequest.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Comp-Off requires a valid reason.");
        }

        // 2) Start and end must be weekdays (Mon–Fri)
        if (isWeekend(start) || isWeekend(end)) {
            throw new IllegalArgumentException("Comp-Off can only be taken on weekdays (Mon–Fri).");
        }

        // 3) Every date must be weekday (supports multiple days)
        LocalDate date = start;
        while (!date.isAfter(end)) {
            if (isWeekend(date)) {
                throw new IllegalArgumentException("Comp-Off leave cannot include weekends.");
            }
            date = date.plusDays(1);
        }
    }

    private boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() == 6 ||   // Saturday
                date.getDayOfWeek().getValue() == 7;    // Sunday
    }

//    @Override
//    public List<TeamCalendarDto> getManagerTeamCalendar(Long managerId, int year, int month) {
//
//        List<Manager> teamMembers = managerRepository.findByManagerId(managerId);
//        List<HolidayDto> holidays = holidayService.getAllHolidays();
//        List<TeamCalendarDto> teamCalendar = new ArrayList<>();
//
//        for (Manager emp : teamMembers) {
//
//            List<LeaveRequest> leaves = leaveRepository.findByUserId(emp.getId());
//
//            Map<String, String> statusMap = new HashMap<>();
//            leaves.forEach(l -> statusMap.put(l.getStartDate().toString(), l.getLeaveType().name()));
//
//            teamCalendar.add(
//                    new TeamCalendarDto(
//                            emp.getId(),
//                            emp.getFullName(),      // <-- use fullName
//                            holidays,
//                            leaves,
//                            statusMap
//                    )
//            );
//        }
//        return teamCalendar;
//    }

}
