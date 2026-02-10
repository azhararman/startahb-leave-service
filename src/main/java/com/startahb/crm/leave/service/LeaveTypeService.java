package com.startahb.crm.leave.service;

import com.startahb.crm.leave.dto.request.LeaveTypeRequest;
import com.startahb.crm.leave.dto.response.LeaveTypeResponse;
import com.startahb.crm.leave.entity.LeaveTypes;
import com.startahb.crm.leave.exception.ResourceNotFoundException;
import com.startahb.crm.leave.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository repository;

    public List<LeaveTypeResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(type -> new LeaveTypeResponse(
                        type.getId(),
                        type.getName(),
                        type.getDescription(),
                        type.getYearlyQuota(),
                        type.getCarryForward(),
                        type.getMaxPerMonth(),
                        type.getActive()
                ))
                .collect(Collectors.toList());
    }

    public LeaveTypeResponse create(LeaveTypeRequest req) {

        if (repository.existsByName(req.getName())) {
            throw new IllegalArgumentException("Leave type already exists");
        }

        LeaveTypes type = new LeaveTypes();
        type.setName(req.getName());    // ENUM
        type.setDescription(req.getDescription());
        type.setYearlyQuota(req.getYearlyQuota());
        type.setCarryForward(req.getCarryForward());
        type.setMaxPerMonth(req.getMaxPerMonth());
        type.setActive(true);

        LeaveTypes saved = repository.save(type);

        return new LeaveTypeResponse(
                saved.getId(),
                saved.getName(),   // ENUM
                saved.getDescription(),
                saved.getYearlyQuota(),
                saved.getCarryForward(),
                saved.getMaxPerMonth(),
                saved.getActive()
        );
    }


    public void update(Long id, LeaveTypeRequest req) {

        LeaveTypes type = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));

        if (req.getDescription() != null)
            type.setDescription(req.getDescription());

        if (req.getYearlyQuota() != null)
            type.setYearlyQuota(req.getYearlyQuota());

        if (req.getCarryForward() != null)
            type.setCarryForward(req.getCarryForward());

        if (req.getMaxPerMonth() != null)
            type.setMaxPerMonth(req.getMaxPerMonth());

        repository.save(type);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Leave type not found");
        }
        repository.deleteById(id);
    }
}
