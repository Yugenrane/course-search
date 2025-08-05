package com.example.course_search.services;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SuggestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SuggestionService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SuggestionService Unit Tests")
class SuggestionServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private SuggestionService suggestionService;

    private CourseDocument courseJava;
    private CourseDocument courseJs;

    @BeforeEach
    void setUp() {
        courseJava = CourseDocument.builder()
                .id("1")
                .title("Java Programming")
                .description("Learn Java")
                .build();

        courseJs = CourseDocument.builder()
                .id("2")
                .title("JavaScript Basics")
                .description("Learn JavaScript")
                .build();
    }

    @Nested
    @DisplayName("Positive path")
    class PositiveCases {

        @Test
        @DisplayName("Returns suggestions for a valid query")
        void shouldReturnSuggestionsForValidQuery() {
            // mock SearchHit list
            SearchHit<CourseDocument> hitJava = mock(SearchHit.class);
            SearchHit<CourseDocument> hitJs   = mock(SearchHit.class);
            when(hitJava.getContent()).thenReturn(courseJava);
            when(hitJs.getContent()).thenReturn(courseJs);

            List<SearchHit<CourseDocument>> hitList = new ArrayList<>();
            hitList.add(hitJava);
            hitList.add(hitJs);

            // mock SearchHits stream & return
            SearchHits<CourseDocument> hits = mock(SearchHits.class);
            when(hits.stream()).thenReturn(hitList.stream());

            when(elasticsearchOperations.search(any(CriteriaQuery.class),
                    eq(CourseDocument.class)))
                    .thenReturn(hits);

            // WHEN
            SuggestionResponse response = suggestionService.getSuggestions("Java", 10);

            // THEN
            verify(elasticsearchOperations, times(1))
                    .search(any(CriteriaQuery.class), eq(CourseDocument.class));

            assertThat(response).isNotNull();
            assertThat(response.getSuggestions())
                    .containsExactlyInAnyOrder("Java Programming", "JavaScript Basics");
        }
    }

    @Nested
    @DisplayName("Edge cases and error handling")
    class EdgeCases {

        @Test
        @DisplayName("Returns empty list for null query")
        void shouldReturnEmptyListForNullQuery() {
            SuggestionResponse response = suggestionService.getSuggestions(null, 10);
            assertThat(response.getSuggestions()).isEmpty();
            verifyNoInteractions(elasticsearchOperations);
        }

        @Test
        @DisplayName("Returns empty list for blank query")
        void shouldReturnEmptyListForBlankQuery() {
            SuggestionResponse response = suggestionService.getSuggestions("   ", 10);
            assertThat(response.getSuggestions()).isEmpty();
            verifyNoInteractions(elasticsearchOperations);
        }

        @Test
        @DisplayName("Handles Elasticsearch exceptions gracefully")
        void shouldHandleElasticsearchExceptionGracefully() {
            when(elasticsearchOperations.search(any(CriteriaQuery.class),
                    eq(CourseDocument.class)))
                    .thenThrow(new RuntimeException("Elasticsearch down"));

            SuggestionResponse response = suggestionService.getSuggestions("Java", 10);

            assertThat(response.getSuggestions()).isEmpty();
            verify(elasticsearchOperations, times(1))
                    .search(any(CriteriaQuery.class), eq(CourseDocument.class));
        }
    }
}
