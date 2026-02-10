package com.startahb.crm.leave.entity;

import com.startahb.crm.leave.enums.LeaveStatus;
import com.startahb.crm.leave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "leave_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private LocalDate startDate;
    private LocalDate endDate;


    private String attachmentUrl;

    private Boolean isHalfDay = false;

    private String managerComment;
    private Long approvedBy;  // manager/admin id

}
