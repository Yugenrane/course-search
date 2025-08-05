package com.example.course_search.controllers;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SearchResponse;
import com.example.course_search.dto.SuggestionResponse;
import com.example.course_search.services.CourseSearchService;
import com.example.course_search.services.SuggestionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;  //
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseSearchController.class)
@DisplayName("CourseSearchController Unit Tests")
class CourseSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean  //
    private CourseSearchService courseSearchService;

    @MockitoBean  //
    private SuggestionService suggestionService;

    @Test
    @DisplayName("Should search courses with query parameter")
    void shouldSearchCoursesWithQuery() throws Exception {
        // Given
        CourseDocument course = CourseDocument.builder()
                .id("1")
                .title("Java Programming")
                .description("Learn Java")
                .category("Programming")
                .build();

        SearchResponse searchResponse = SearchResponse.builder()
                .total(1L)
                .courses(List.of(course))
                .build();

        when(courseSearchService.searchCoursesWithFuzzy(
                anyString(), any(), any(), any(), any(),
                any(), any(), any(), anyString(), anyInt(), anyInt()
        )).thenReturn(searchResponse);

        // When & Then
        mockMvc.perform(get("/api/search")
                        .param("q", "Java")
                        .param("sort", "upcoming")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.courses", hasSize(1)))
                .andExpect(jsonPath("$.courses[0].title", is("Java Programming")));
    }

    @Test
    @DisplayName("Should get all courses")
    void shouldGetAllCourses() throws Exception {
        // Given
        SearchResponse searchResponse = SearchResponse.builder()
                .total(5L)
                .courses(List.of())
                .build();

        when(courseSearchService.findAllCourses(anyString(), anyInt(), anyInt()))
                .thenReturn(searchResponse);

        // When & Then
        mockMvc.perform(get("/api/allCourses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(5)));
    }

    @Test
    @DisplayName("Should get suggestions")
    void shouldGetSuggestions() throws Exception {
        // Given
        SuggestionResponse suggestionResponse = SuggestionResponse.builder()
                .suggestions(List.of("Java Programming", "JavaScript Basics"))
                .build();

        when(suggestionService.getSuggestions(anyString(), anyInt()))
                .thenReturn(suggestionResponse);

        // When & Then
        mockMvc.perform(get("/api/search/suggest")
                        .param("q", "Java")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.suggestions", hasSize(2)))
                .andExpect(jsonPath("$.suggestions[0]", is("Java Programming")));
    }

    @Test
    @DisplayName("Should handle search with all filters")
    void shouldHandleSearchWithAllFilters() throws Exception {
        // Given
        SearchResponse searchResponse = SearchResponse.builder()
                .total(1L)
                .courses(List.of())
                .build();

        when(courseSearchService.searchCoursesWithFuzzy(
                anyString(), any(), any(), any(), any(),
                any(), any(), any(), anyString(), anyInt(), anyInt()
        )).thenReturn(searchResponse);

        // When & Then
        mockMvc.perform(get("/api/search")
                        .param("q", "Java")
                        .param("minAge", "18")
                        .param("maxAge", "65")
                        .param("minPrice", "100.0")
                        .param("maxPrice", "200.0")
                        .param("category", "Programming")
                        .param("type", "Online"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total", is(1)));
    }

    @Test
    @DisplayName("Should handle empty suggestions")
    void shouldHandleEmptySuggestions() throws Exception {
        // Given
        SuggestionResponse emptySuggestionResponse = SuggestionResponse.builder()
                .suggestions(List.of())
                .build();

        when(suggestionService.getSuggestions(anyString(), anyInt()))
                .thenReturn(emptySuggestionResponse);

        // When & Then
        mockMvc.perform(get("/api/search/suggest")
                        .param("q", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.suggestions", hasSize(0)));
    }
}
