package com.example.course_search.dto;

import com.example.course_search.document.CourseDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse {

    @JsonProperty("total")
    private long total;

    @JsonProperty("courses")
    private List<CourseDocument> courses;
}
