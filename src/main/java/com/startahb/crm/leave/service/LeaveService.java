package com.startahb.crm.leave.service;



import com.startahb.crm.leave.dto.response.CalendarResponse;
import com.startahb.crm.leave.dto.response.EmployeeCalendarDto;
import com.startahb.crm.leave.entity.LeaveRequest;
import com.startahb.crm.leave.enums.LeaveStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LeaveService  {
    LeaveRequest applyLeave(LeaveRequest leaveRequest, Long userId);
    LeaveRequest updateLeaveStatus(Long id, LeaveStatus status);
    List<LeaveRequest> getAllLeaves();
    List<LeaveRequest> getLeavesByUserId(Long userId);
    LeaveRequest updateUserLeave(Long id, Long userId, LeaveRequest updatedLeave);
    void deleteLeaveById(Long leaveId, Long userId);
    void cancelLeave(Long leaveId, Long userId);
    EmployeeCalendarDto getEmployeeCalendar(Long employeeId);
    CalendarResponse generateMonthlyCalendar(Long userId, int year, int month);
    //List<TeamCalendarDto> getManagerTeamCalendar(Long managerId, int year, int month);  // <-- Update this
    List<LeaveRequest> getLeavesByStatus(LeaveStatus status);

    void approveLeave(Long leaveId, Long managerId, String comment);

    void rejectLeave(Long leaveId, Long managerId, String comment);

    List<Map<String, Object>> getTeamCalendar();
    boolean validateLeave(Long leaveTypeId, Long employeeId, LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> getUpcomingLeaves(Long employeeId);
    Map<String, Object> getLeaveDetails(Long leaveId);

}