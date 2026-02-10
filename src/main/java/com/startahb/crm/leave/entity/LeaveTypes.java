package com.startahb.crm.leave.entity;
import com.startahb.crm.leave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "leave_types")
@Data
public class LeaveTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private LeaveType name;

    private String description;
    private Integer yearlyQuota;
    private Boolean carryForward;
    private Integer maxPerMonth;

    private Boolean active = true;
}
