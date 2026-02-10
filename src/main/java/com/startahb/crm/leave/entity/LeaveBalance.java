package com.startahb.crm.leave.entity;


import com.startahb.crm.leave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(
        name = "leave_balance",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "leave_type"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_type")
    private LeaveType leaveType;

    private Integer total;       // yearly quota
    private Integer used;        // how many approved
    private Integer remaining;   // total - used
}
