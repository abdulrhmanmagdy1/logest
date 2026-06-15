package com.edham.logistics.controller;

import com.edham.logistics.model.Survey;
import com.edham.logistics.repository.SurveyRepository;
import com.edham.logistics.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/surveys")
@CrossOrigin(origins = "*")
public class SurveyController {

    @Autowired
    private SurveyRepository surveyRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPERVISOR')")
    public ResponseEntity<ApiResponse<List<Survey>>> getAllSurveys() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Surveys retrieved", surveyRepository.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<ApiResponse<Survey>> submitSurvey(@RequestBody Survey survey) {
        survey.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(new ApiResponse<>(true, "Survey submitted", surveyRepository.save(survey)));
    }
}
