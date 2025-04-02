package com.evawova.preview.domain.interview.service;

import com.evawova.preview.domain.interview.dto.InterviewCategoryDto;
import com.evawova.preview.domain.interview.repository.InterviewCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterviewCategoryService {

    private final InterviewCategoryRepository interviewCategoryRepository;

    public List<InterviewCategoryDto> getAllCategories() {
        return interviewCategoryRepository.findAll().stream()
                .map(InterviewCategoryDto::from)
                .collect(Collectors.toList());
    }

    // TODO: Add methods for creating, updating, deleting categories if needed.
}