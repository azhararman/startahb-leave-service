package com.startahb.crm.leave.service.impl;



import com.startahb.crm.leave.dto.request.LeaveBalanceDTO;
import com.startahb.crm.leave.entity.LeaveBalance;
import com.startahb.crm.leave.entity.LeaveTypes;
import com.startahb.crm.leave.enums.LeaveStatus;
import com.startahb.crm.leave.enums.LeaveType;
import com.startahb.crm.leave.repository.LeaveBalanceRepository;
import com.startahb.crm.leave.repository.LeaveRepository;
import com.startahb.crm.leave.repository.LeaveTypeRepository;
import com.startahb.crm.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository balanceRepo;
    private final LeaveRepository leaveRepo;
    private final LeaveTypeRepository leaveTypeRepo;

    // 1️⃣ GET BALANCE FOR ONE EMPLOYEE
    @Override
    public LeaveBalanceDTO getLeaveBalance(Long userId) {

        List<LeaveBalance> balances = balanceRepo.findByUserId(userId);

        if (balances.isEmpty())
            throw new RuntimeException("Leave balance not found");

        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setEmployeeId(userId);

        List<LeaveBalanceDTO.LeaveTypeBalance> list = new ArrayList<>();

        for (LeaveBalance bal : balances) {

            int used = leaveRepo.countByUserIdAndLeaveTypeAndStatus(
                    userId,
                    bal.getLeaveType(),
                    LeaveStatus.APPROVED
            );

            LeaveBalanceDTO.LeaveTypeBalance b = new LeaveBalanceDTO.LeaveTypeBalance();
            b.setLeaveType(bal.getLeaveType().name());
            b.setTotal(bal.getTotal());
            b.setUsed(used);
            b.setRemaining(bal.getTotal() - used);

            list.add(b);
        }

        dto.setBalances(list);
        return dto;
    }

    // 2️⃣ GENERATE YEARLY BALANCES
    @Override
    public String generateYearlyBalance() {

        // USERS WHO HAVE APPLIED LEAVES BEFORE (YOUR CURRENT LOGIC)
        List<Long> employeeIds = leaveRepo.findAll()
                .stream()
                .map(l -> l.getUserId())
                .distinct()
                .toList();

        List<LeaveTypes> types = leaveTypeRepo.findByActiveTrue();

        for (Long userId : employeeIds) {

            for (LeaveTypes lt : types) {

                LeaveType type = lt.getName();

                // SKIP SPECIAL TYPES
                if (type == LeaveType.OPTIONAL_HOLIDAY || type == LeaveType.COMP_OFF)
                    continue;

                int total = lt.getYearlyQuota();

                LeaveBalance balance =
                        balanceRepo.findByUserIdAndLeaveType(userId, type)
                                .orElse(
                                        new LeaveBalance(null, userId, type, total, 0, total)
                                );

                balance.setTotal(total);
                balance.setRemaining(total - balance.getUsed());

                balanceRepo.save(balance);
            }
        }

        return "Yearly leave balance generated.";
    }

    // 3️⃣ ADMIN MANUAL UPDATE
    @Override
    public String updateBalance(Long userId, LeaveBalanceDTO dto) {

        for (LeaveBalanceDTO.LeaveTypeBalance b : dto.getBalances()) {

            LeaveType type = LeaveType.valueOf(b.getLeaveType());

            LeaveBalance bal = balanceRepo
                    .findByUserIdAndLeaveType(userId, type)
                    .orElse(
                            new LeaveBalance(null, userId, type,
                                    b.getTotal(), b.getUsed(), b.getRemaining())
                    );

            bal.setTotal(b.getTotal());
            bal.setUsed(b.getUsed());
            bal.setRemaining(b.getRemaining());

            balanceRepo.save(bal);
        }

        return "Leave balance updated successfully.";
    }

    // 4️⃣ REPORT (ADMIN)
    @Override
    public List<LeaveBalanceDTO> getReport() {

        List<Long> users = balanceRepo.findAll()
                .stream()
                .map(LeaveBalance::getUserId)
                .distinct()
                .toList();

        List<LeaveBalanceDTO> report = new ArrayList<>();

        for (Long uid : users) {
            report.add(getLeaveBalance(uid));
        }

        return report;
    }
}
