package com.example.course_search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionResponse {

    @JsonProperty("suggestions")
    private List<String> suggestions;
}
