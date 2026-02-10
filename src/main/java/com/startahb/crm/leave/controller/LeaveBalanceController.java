package com.startahb.crm.leave.controller;



import com.startahb.crm.leave.dto.request.LeaveBalanceDTO;
import com.startahb.crm.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/leaves/balance")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalance(userId));
    }

    @PostMapping("/generate-yearly")
    public ResponseEntity<?> generateYearly() {
        return ResponseEntity.ok(leaveBalanceService.generateYearlyBalance());
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<?> updateBalance(
            @PathVariable Long employeeId,
            @RequestBody LeaveBalanceDTO dto) {
        return ResponseEntity.ok(leaveBalanceService.updateBalance(employeeId, dto));
    }

    @GetMapping("/report")
    public ResponseEntity<?> getReport() {
        return ResponseEntity.ok(leaveBalanceService.getReport());
    }
}

