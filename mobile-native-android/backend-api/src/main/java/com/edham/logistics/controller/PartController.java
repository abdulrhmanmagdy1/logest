package com.edham.logistics.controller;

import com.edham.logistics.model.Part;
import com.edham.logistics.repository.PartRepository;
import com.edham.logistics.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@CrossOrigin(origins = "*")
public class PartController {

    @Autowired
    private PartRepository partRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR', 'WORKSHOP')")
    public ResponseEntity<ApiResponse<List<Part>>> getAllParts() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Parts retrieved", partRepository.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR', 'WORKSHOP')")
    public ResponseEntity<ApiResponse<Part>> addPart(@RequestBody Part part) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Part added", partRepository.save(part)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR', 'WORKSHOP')")
    public ResponseEntity<ApiResponse<Part>> updatePart(@PathVariable Long id, @RequestBody Part partDetails) {
        Part part = partRepository.findById(id).orElseThrow(() -> new RuntimeException("Part not found"));
        part.setName(partDetails.getName());
        part.setQuantity(partDetails.getQuantity());
        part.setMinQuantity(partDetails.getMinQuantity());
        part.setLocation(partDetails.getLocation());
        part.setStatus(partDetails.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(true, "Part updated", partRepository.save(part)));
    }
}
