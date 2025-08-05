package com.example.course_search.controllers;

import com.example.course_search.dto.SearchResponse;
import com.example.course_search.dto.SuggestionResponse;
import com.example.course_search.services.CourseSearchService;
import com.example.course_search.services.SuggestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseSearchController {
    private final CourseSearchService courseSearchService;
    private final SuggestionService suggestionService;

    @GetMapping("/search")
    public SearchResponse searchCourses(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime nextSessionDate,
            @RequestParam(required = false, defaultValue = "upcoming") String sort,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        return courseSearchService.searchCoursesWithFuzzy(q, minAge, maxAge,
                                                    minPrice, maxPrice, category,
                                                        type, nextSessionDate, sort,
                                                            page, size);
    }
    @GetMapping("/allCourses")
    public SearchResponse getAllCourses(@RequestParam(required = false, defaultValue = "upcoming") String sort,
                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                              @RequestParam(required = false, defaultValue = "10") Integer size) {
        return courseSearchService.findAllCourses(sort, page, size);
    }

    @GetMapping("/search/suggest")
    public SuggestionResponse getSuggestions(
            @RequestParam String q,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        return suggestionService.getSuggestions(q, size);
    }
}
