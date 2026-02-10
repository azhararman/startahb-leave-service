package com.startahb.crm.leave.controller;



import com.startahb.crm.leave.dto.request.LeaveTypeRequest;
import com.startahb.crm.leave.dto.response.LeaveTypeResponse;
import com.startahb.crm.leave.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService service;

    @GetMapping
    public List<LeaveTypeResponse> getAll() {
        return service.getAll();
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LeaveTypeRequest request) {
        LeaveTypeResponse created = service.create(request);
        return ResponseEntity.ok(Map.of(
                "message", "Leave type created",
                "id", created.getId()
        ));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody LeaveTypeRequest request) {
        service.update(id, request);
        return ResponseEntity.ok(Map.of("message", "Leave type updated"));
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Leave type deleted"));
    }
}

