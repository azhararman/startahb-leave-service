package com.startahb.crm.leave.controller;



import com.startahb.crm.leave.dto.request.LeaveActionDTO;
import com.startahb.crm.leave.dto.request.LeaveValidationRequest;
import com.startahb.crm.leave.dto.response.CompOffDTO;
import com.startahb.crm.leave.dto.response.EmployeeCalendarDto;
import com.startahb.crm.leave.entity.LeaveRequest;
import com.startahb.crm.leave.enums.LeaveStatus;
import com.startahb.crm.leave.enums.LeaveType;
import com.startahb.crm.leave.service.HolidayService;
import com.startahb.crm.leave.service.LeaveService;
import com.startahb.crm.leave.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController   {

    private final LeaveService leaveService;
    private final JwtTokenUtil jwtTokenUtil;
    private final HolidayService holidayService;

    // Helper method to extract data from SecurityContext
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();
        return jwtTokenUtil.getUserIdFromToken(token);
    }

    private Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities();
    }

    @PostMapping
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequest leaveRequest) {

        Long userId = getCurrentUserId();
        LeaveRequest savedLeave = leaveService.applyLeave(leaveRequest, userId);

        // --- COMP-OFF RESPONSE (clean DTO JSON like image 2) ---
        if (savedLeave.getLeaveType() != null &&
                savedLeave.getLeaveType().equals(LeaveType.COMP_OFF)) {

            CompOffDTO dto = convertToCompOffDTO(savedLeave);

            return ResponseEntity.ok(dto);  // send clean DTO response
        }

        // --- OTHER LEAVE TYPES ---
        return ResponseEntity.ok(savedLeave);
    }

    // ---------------- DTO MAPPER (paste here) ----------------
    private CompOffDTO convertToCompOffDTO(LeaveRequest leave) {
        CompOffDTO dto = new CompOffDTO();
        dto.setId(leave.getId());
        dto.setUserId(leave.getUserId());
        dto.setReason(leave.getReason());
        dto.setStartDate(leave.getStartDate());
        dto.setEndDate(leave.getEndDate());
        dto.setLeaveType(leave.getLeaveType().name());
        dto.setStatus(leave.getStatus().name());
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {

        Long userId = getCurrentUserId();
        Collection<? extends GrantedAuthority> authorities = getCurrentUserRoles();

        boolean isAdmin = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(leaveService.getAllLeaves());
        } else {
            return ResponseEntity.ok(leaveService.getLeavesByUserId(userId));
        }
    }
    @GetMapping("/my-leaves")
    public ResponseEntity<List<LeaveRequest>> getMyLeaves() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(leaveService.getLeavesByUserId(userId));
    }

    @GetMapping("/my-upcoming/{employeeId}")
    public ResponseEntity<?> getUpcomingLeaves(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getUpcomingLeaves(employeeId));
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<?> getLeaveById(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveService.getLeaveDetails(leaveId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateMyLeave(
            @PathVariable Long id,
            @RequestBody LeaveRequest updatedLeave) {

        Long userId = getCurrentUserId();
        LeaveRequest leave = leaveService.updateUserLeave(id, userId, updatedLeave);
        return ResponseEntity.ok(leave);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id)  {

        Long userId = getCurrentUserId();
        leaveService.deleteLeaveById(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "Leave deleted successfully.");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelLeave(@PathVariable Long id) {

        Long userId = getCurrentUserId();
        leaveService.cancelLeave(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "Leave cancelled successfully.");
        response.put("data", null);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/calendar/employee/{employeeId}")
    public ResponseEntity<EmployeeCalendarDto> getEmployeeCalendar(@PathVariable Long employeeId) {

        EmployeeCalendarDto response = leaveService.getEmployeeCalendar(employeeId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/calendar/month")
    public ResponseEntity<?> getMonthlyCalendar(
            @RequestParam int year,
            @RequestParam int month,
            @RequestHeader("Authorization") String token) {

        Long userId = getCurrentUserId();
        return ResponseEntity.ok(leaveService.generateMonthlyCalendar(userId, year, month));
    }

    @GetMapping("/team")
    public ResponseEntity<?> getTeamLeaves(@RequestParam(required = false) LeaveStatus status) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();

        if (!jwtTokenUtil.hasAnyRole(token, "ROLE_ADMIN", "ROLE_MGR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Admin/Manager can view team leaves");
        }

        if (status != null) {
            return ResponseEntity.ok(leaveService.getLeavesByStatus(status));
        }

        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @PostMapping("/approve/{leaveId}")
    public ResponseEntity<?> approveLeaveWithAction(
            @PathVariable Long leaveId,
            @RequestBody LeaveActionDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();

        if (!jwtTokenUtil.hasAnyRole(token, "ROLE_ADMIN", "ROLE_MGR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Admin/Manager can approve leaves");
        }

        Long managerId = jwtTokenUtil.getUserIdFromToken(token);

        leaveService.approveLeave(leaveId, managerId, dto.getComment());

        return ResponseEntity.ok(Map.of("status", "APPROVED"));
    }

    @PostMapping("/reject/{leaveId}")
    public ResponseEntity<?> rejectLeave(
            @PathVariable Long leaveId,
            @RequestBody LeaveActionDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();

        if (!jwtTokenUtil.hasAnyRole(token, "ROLE_ADMIN", "ROLE_MGR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Admin/Manager can reject leaves");
        }

        Long managerId = jwtTokenUtil.getUserIdFromToken(token);

        leaveService.rejectLeave(leaveId, managerId, dto.getComment());

        return ResponseEntity.ok(Map.of("status", "REJECTED"));
    }

    @GetMapping("/team-calendar")
    public ResponseEntity<?> getTeamCalendar() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();

        if (!jwtTokenUtil.hasAnyRole(token, "ROLE_ADMIN", "ROLE_MGR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only Admin/Manager can view team calendar");
        }

        return ResponseEntity.ok(Map.of("calendar", leaveService.getTeamCalendar()));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateLeave(@RequestBody LeaveValidationRequest request) {

        boolean isValid = leaveService.validateLeave(
                request.getLeaveTypeId(),
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity.ok(Map.of("valid", isValid));
    }










//    @GetMapping("/calendar/manager/{managerId}")
//    @PreAuthorize("hasRole('MGR')")
//    public ResponseEntity<?> getManagerTeamCalendar(
//            @PathVariable Long managerId,
//            @RequestParam int year,
//            @RequestParam int month) {
//
//        return ResponseEntity.ok(
//                Map.of(
//                        "success", true,
//                        "message", "Team Calendar Loaded",
//                        "data", leaveService.getManagerTeamCalendar(managerId, year, month)
//                )
//        );
    //  }


}

