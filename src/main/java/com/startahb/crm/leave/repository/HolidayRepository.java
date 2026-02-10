package com.startahb.crm.leave.repository;

import com.startahb.crm.leave.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long>  {
    boolean existsByDate(LocalDate date);
}
