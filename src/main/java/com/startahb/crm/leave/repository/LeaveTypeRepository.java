package com.startahb.crm.leave.repository;

import com.startahb.crm.leave.entity.LeaveTypes;
import com.startahb.crm.leave.enums.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveTypes, Long> {
    boolean existsByName(LeaveType name);

    List<LeaveTypes> findByActiveTrue();
}
