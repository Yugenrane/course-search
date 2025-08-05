package com.example.course_search.services;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SuggestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SuggestionResponse getSuggestions(String query, int size) {
        if (query == null || query.trim().isEmpty()) {
            return SuggestionResponse.builder()
                    .suggestions(List.of())
                    .build();
        }

        try {
            Criteria criteria = new Criteria("title").startsWith(query.toLowerCase());
            CriteriaQuery searchQuery = new CriteriaQuery(criteria);

            Pageable pageable = PageRequest.of(0, size);
            searchQuery.setPageable(pageable);

            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

            List<String> suggestions = searchHits.stream()
                    .map(hit -> hit.getContent().getTitle())
                    .filter(title -> title.toLowerCase().startsWith(query.toLowerCase())) // Extra filter for case sensitivity
                    .distinct()
                    .limit(size)
                    .collect(Collectors.toList());

            return SuggestionResponse.builder()
                    .suggestions(suggestions)
                    .build();

        } catch (Exception e) {
            System.err.println("Error in suggestion search: " + e.getMessage());
            e.printStackTrace(); // For debugging purpose only I used

            return SuggestionResponse.builder()
                    .suggestions(List.of())
                    .build();
        }
    }
}
