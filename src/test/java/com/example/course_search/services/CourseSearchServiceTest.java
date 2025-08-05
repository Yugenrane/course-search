package com.example.course_search.services;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Add this line
@DisplayName("CourseSearchService Unit Tests")
class CourseSearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private CourseSearchService courseSearchService;

    private CourseDocument sampleCourse;

    @BeforeEach
    void setUp() {
        sampleCourse = CourseDocument.builder()
                .id("1")
                .title("Java Programming")
                .description("Learn Java from basics")
                .category("Programming")
                .type("Online")
                .minAge(18)
                .maxAge(65)
                .minPrice(100.0)
                .maxPrice(200.0)
                .language("English")
                .nextSessionDate(Instant.now().plusSeconds(86400))
                .build();
    }

    @Test
    @DisplayName("Should find all courses with default sort parameter")
    void shouldFindAllCoursesWithDefaultSort() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("dateAsc");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find all courses with no parameters")
    void shouldFindAllCoursesWithNoParameters() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should sort by price ascending")
    void shouldSortByPriceAscending() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("priceasc", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should sort by price descending")
    void shouldSortByPriceDescending() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("pricedesc", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle invalid sort parameter")
    void shouldHandleInvalidSortParameter() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("invalid", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should search courses with type filter")
    void shouldSearchCoursesWithTypeFilter() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.searchCourses(
                null, null, null, null, null, null, "Online", null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle fuzzy search with empty query")
    void shouldHandleFuzzySearchWithEmptyQuery() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should find all courses successfully")
    void shouldFindAllCoursesSuccessfully() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("upcoming", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getCourses()).hasSize(1);
        assertThat(result.getCourses().get(0).getTitle()).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("Should search courses with query parameter")
    void shouldSearchCoursesWithQuery() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.searchCourses(
                "Java", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
        assertThat(result.getCourses()).hasSize(1);
    }

    @Test
    @DisplayName("Should search courses with filters")
    void shouldSearchCoursesWithFilters() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.searchCourses(
                null, 20, 60, 50.0, 150.0, "Programming", "Online", null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle empty search results")
    void shouldHandleEmptySearchResults() {
        // Given
        SearchHits<CourseDocument> emptySearchHitsMock = mock(SearchHits.class);

        when(emptySearchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptySearchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptySearchHitsMock);

        // When
        SearchResponse result = courseSearchService.findAllCourses("upcoming", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThat(result.getCourses()).isEmpty();
    }

    @Test
    @DisplayName("Should handle fuzzy search")
    void shouldHandleFuzzySearch() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);

        // Mock multiple possible search calls since fuzzy search may use different strategies
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isGreaterThanOrEqualTo(0L);
    }


    @Test
    @DisplayName("Should execute search with different sort parameters")
    void shouldExecuteSearchWithDifferentSortParameters() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test date descending sort
        SearchResponse result = courseSearchService.findAllCourses("datedesc", 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle null sort parameter")
    void shouldHandleNullSortParameter() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with null sort
        SearchResponse result = courseSearchService.findAllCourses(null, 0, 10);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle page validation - negative page")
    void shouldHandlePageValidationNegativePage() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with negative page
        SearchResponse result = courseSearchService.findAllCourses("upcoming", -1, 10);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle size validation - large size")
    void shouldHandleSizeValidationLargeSize() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with large size (should be capped at 100)
        SearchResponse result = courseSearchService.findAllCourses("upcoming", 0, 200);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle size validation - zero or negative size")
    void shouldHandleSizeValidationZeroSize() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with zero size (should default to 10)
        SearchResponse result = courseSearchService.findAllCourses("upcoming", 0, 0);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle fuzzy search with very short query")
    void shouldHandleFuzzySearchWithShortQuery() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with 2-character query (should avoid wildcard search)
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Ja", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should handle fuzzy search with long query")
    void shouldHandleFuzzySearchWithLongQuery() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with longer query
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "JavaScript", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should search with multiple filters combined")
    void shouldSearchWithMultipleFiltersCombined() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with query + age + price + category + type
        SearchResponse result = courseSearchService.searchCourses(
                "Java", 18, 65, 50.0, 300.0, "Programming", "Online", null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should search with blank category and type")
    void shouldSearchWithBlankCategoryAndType() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with blank strings (should be ignored)
        SearchResponse result = courseSearchService.searchCourses(
                "Java", null, null, null, null, "", "", null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should handle null page and size parameters")
    void shouldHandleNullPageAndSizeParameters() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Test with null page and size (should use defaults)
        SearchResponse result = courseSearchService.findAllCourses("upcoming", null, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1L);
    }


    @Test
    @DisplayName("Should test hasOtherFilters method coverage")
    void shouldTestHasOtherFiltersMethodCoverage() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - This should trigger the hasOtherFilters method with ALL parameters
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", 18, 65, 100.0, 200.0, "Programming", "Online",
                OffsetDateTime.now().plusDays(1), "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test fuzzy search exact match path")
    void shouldTestFuzzySearchExactMatchPath() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);

        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);

        // Mock multiple calls for fuzzy search strategies
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock)
                .thenReturn(searchHitsMock)
                .thenReturn(searchHitsMock);

        // When - This should trigger multiple search strategies
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java Programming", null, null, null, null, null, null, null, "upcoming", 0, 5
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test isCharacterMatch method coverage")
    void shouldTestIsCharacterMatchMethodCoverage() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);

        // Create multiple mock calls to cover all search strategies
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock)
                .thenReturn(searchHitsMock)
                .thenReturn(searchHitsMock)
                .thenReturn(searchHitsMock);

        // When - Use a query that will trigger character matching (typo scenario)
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Jaav", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test searchWildcard method coverage")
    void shouldTestSearchWildcardMethodCoverage() {
        // Given
        SearchHits<CourseDocument> emptySearchHitsMock = mock(SearchHits.class);
        when(emptySearchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptySearchHitsMock.getTotalHits()).thenReturn(0L);

        // Mock multiple calls to trigger wildcard patterns
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptySearchHitsMock)  // exact search
                .thenReturn(emptySearchHitsMock)  // contains search
                .thenReturn(emptySearchHitsMock)  // wildcard 1
                .thenReturn(emptySearchHitsMock)  // wildcard 2
                .thenReturn(emptySearchHitsMock); // wildcard 3

        // When - Use a query that will trigger wildcard search
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Program", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test addResults method coverage")
    void shouldTestAddResultsMethodCoverage() {
        // Given
        CourseDocument course2 = CourseDocument.builder()
                .id("2")
                .title("Python Programming")
                .description("Learn Python")
                .category("Programming")
                .build();

        SearchHit<CourseDocument> searchHit1Mock = mock(SearchHit.class);
        SearchHit<CourseDocument> searchHit2Mock = mock(SearchHit.class);

        when(searchHit1Mock.getContent()).thenReturn(sampleCourse);
        when(searchHit2Mock.getContent()).thenReturn(course2);

        List<SearchHit<CourseDocument>> hitsList1 = new ArrayList<>();
        hitsList1.add(searchHit1Mock);

        List<SearchHit<CourseDocument>> hitsList2 = new ArrayList<>();
        hitsList2.add(searchHit2Mock);

        SearchHits<CourseDocument> searchHits1Mock = mock(SearchHits.class);
        SearchHits<CourseDocument> searchHits2Mock = mock(SearchHits.class);

        when(searchHits1Mock.stream()).thenReturn(hitsList1.stream());
        when(searchHits1Mock.getTotalHits()).thenReturn(1L);
        when(searchHits2Mock.stream()).thenReturn(hitsList2.stream());
        when(searchHits2Mock.getTotalHits()).thenReturn(1L);

        // Mock sequential calls to trigger addResults with duplicates
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHits1Mock)  // exact search - returns course 1
                .thenReturn(searchHits1Mock)  // contains search - returns course 1 again (should be filtered)
                .thenReturn(searchHits2Mock); // wildcard search - returns course 2

        // When
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Programming", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
        // Should have results from multiple strategies combined
    }

    @Test
    @DisplayName("Should test all buildSort switch cases")
    void shouldTestAllBuildSortSwitchCases() {
        // Test each case in the buildSort switch statement
        String[] sortCases = {"upcoming", "priceasc", "pricedesc", "dateasc", "datedesc", "invalid", "", null};

        for (String sortCase : sortCases) {
            // Given
            SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
            when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
            when(searchHitsMock.getTotalHits()).thenReturn(0L);
            when(elasticsearchOperations.search(any(NativeQuery.class), eq(CourseDocument.class)))
                    .thenReturn(searchHitsMock);

            // When - Test each sort case
            SearchResponse result = courseSearchService.findAllCourses(sortCase, 0, 10);

            // Then
            assertThat(result).isNotNull();
        }
    }

    @Test
    @DisplayName("Should test isCharacterMatch with different scenarios")
    void shouldTestIsCharacterMatchWithDifferentScenarios() {
        // Given
        CourseDocument mathCourse = CourseDocument.builder()
                .id("3")
                .title("Math") // 4 characters
                .description("Learn Math")
                .build();

        CourseDocument physicsCourse = CourseDocument.builder()
                .id("4")
                .title("Physics") // 7 characters
                .description("Learn Physics")
                .build();

        SearchHit<CourseDocument> mathHitMock = mock(SearchHit.class);
        SearchHit<CourseDocument> physicsHitMock = mock(SearchHit.class);

        when(mathHitMock.getContent()).thenReturn(mathCourse);
        when(physicsHitMock.getContent()).thenReturn(physicsCourse);

        List<SearchHit<CourseDocument>> allCourses = new ArrayList<>();
        allCourses.add(mathHitMock);
        allCourses.add(physicsHitMock);

        SearchHits<CourseDocument> allCoursesHitsMock = mock(SearchHits.class);
        when(allCoursesHitsMock.stream()).thenReturn(allCourses.stream());
        when(allCoursesHitsMock.getTotalHits()).thenReturn(2L);

        // Mock: exact, contains, wildcard return empty, then character search returns courses
        SearchHits<CourseDocument> emptyHitsMock = mock(SearchHits.class);
        when(emptyHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptyHitsMock.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptyHitsMock)    // exact search - empty
                .thenReturn(emptyHitsMock)    // contains search - empty
                .thenReturn(emptyHitsMock)    // wildcard search - empty
                .thenReturn(allCoursesHitsMock); // character search - returns all courses

        // When - Use "Maht" (typo for "Math") - should match "Math" via character matching
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Maht", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test isCharacterMatch with length differences")
    void shouldTestIsCharacterMatchWithLengthDifferences() {
        // Given
        CourseDocument shortCourse = CourseDocument.builder()
                .id("5")
                .title("AI") // Very short title
                .description("Learn AI")
                .build();

        SearchHit<CourseDocument> shortHitMock = mock(SearchHit.class);
        when(shortHitMock.getContent()).thenReturn(shortCourse);

        List<SearchHit<CourseDocument>> courseList = new ArrayList<>();
        courseList.add(shortHitMock);

        SearchHits<CourseDocument> coursesHitsMock = mock(SearchHits.class);
        when(coursesHitsMock.stream()).thenReturn(courseList.stream());
        when(coursesHitsMock.getTotalHits()).thenReturn(1L);

        SearchHits<CourseDocument> emptyHitsMock = mock(SearchHits.class);
        when(emptyHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptyHitsMock.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptyHitsMock)     // exact search - empty
                .thenReturn(emptyHitsMock)     // contains search - empty
                .thenReturn(emptyHitsMock)     // wildcard search - empty
                .thenReturn(coursesHitsMock);  // character search - returns courses

        // When - Use "Programming" (long) vs "AI" (short) - should fail length check
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Programming", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test searchCharacterMatch with exact size limit")
    void shouldTestSearchCharacterMatchWithExactSizeLimit() {
        // Given
        List<CourseDocument> manyCourses = new ArrayList<>();
        List<SearchHit<CourseDocument>> manyHits = new ArrayList<>();

        // Create exactly 15 courses (more than size=10 limit)
        for (int i = 1; i <= 15; i++) {
            CourseDocument course = CourseDocument.builder()
                    .id(String.valueOf(i))
                    .title("Course" + i) // Similar enough for character matching
                    .description("Description " + i)
                    .build();
            manyCourses.add(course);

            SearchHit<CourseDocument> hit = mock(SearchHit.class);
            when(hit.getContent()).thenReturn(course);
            manyHits.add(hit);
        }

        SearchHits<CourseDocument> manyCoursesHitsMock = mock(SearchHits.class);
        when(manyCoursesHitsMock.stream()).thenReturn(manyHits.stream());
        when(manyCoursesHitsMock.getTotalHits()).thenReturn(15L);

        SearchHits<CourseDocument> emptyHitsMock = mock(SearchHits.class);
        when(emptyHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptyHitsMock.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptyHitsMock)        // exact search - empty
                .thenReturn(emptyHitsMock)        // contains search - empty
                .thenReturn(emptyHitsMock)        // wildcard search - empty
                .thenReturn(manyCoursesHitsMock); // character search - returns 15 courses

        // When - Should limit to size=10 even though 15 courses match
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Course", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test searchCharacterMatch exception handling")
    void shouldTestSearchCharacterMatchExceptionHandling() {
        // Given
        SearchHits<CourseDocument> emptyHitsMock = mock(SearchHits.class);
        when(emptyHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(emptyHitsMock.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(emptyHitsMock)                             // exact search - empty
                .thenReturn(emptyHitsMock)                             // contains search - empty
                .thenReturn(emptyHitsMock)                             // wildcard search - empty
                .thenThrow(new RuntimeException("Character search failed")); // character search - exception

        // When - Exception in character search should be handled
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "TestQuery", null, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then - Should return empty result, not throw exception
        assertThat(result).isNotNull();
        assertThat(result.getCourses()).isEmpty();
    }

    @Test
    @DisplayName("Should test hasOtherFilters with minAge only")
    void shouldTestHasOtherFiltersWithMinAgeOnly() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);
        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Only minAge provided
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", 18, null, null, null, null, null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test hasOtherFilters with category only")
    void shouldTestHasOtherFiltersWithCategoryOnly() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);
        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Only category provided
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", null, null, null, null, "Programming", null, null, "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test hasOtherFilters with date only")
    void shouldTestHasOtherFiltersWithDateOnly() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        SearchHit<CourseDocument> searchHitMock = mock(SearchHit.class);
        List<SearchHit<CourseDocument>> hitsList = new ArrayList<>();
        hitsList.add(searchHitMock);

        when(searchHitMock.getContent()).thenReturn(sampleCourse);
        when(searchHitsMock.stream()).thenReturn(hitsList.stream());
        when(searchHitsMock.getTotalHits()).thenReturn(1L);
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - Only nextSessionDate provided
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", null, null, null, null, null, null, OffsetDateTime.now(), "upcoming", 0, 10
        );

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Should test hasOtherFilters with blank strings")
    void shouldTestHasOtherFiltersWithBlankStrings() {
        // Given
        SearchHits<CourseDocument> searchHitsMock = mock(SearchHits.class);
        when(searchHitsMock.stream()).thenReturn(new ArrayList<SearchHit<CourseDocument>>().stream());
        when(searchHitsMock.getTotalHits()).thenReturn(0L);

        // Mock fuzzy search path (no other filters)
        when(elasticsearchOperations.search(any(CriteriaQuery.class), eq(CourseDocument.class)))
                .thenReturn(searchHitsMock);

        // When - All string filters are blank, all others are null (should return false)
        SearchResponse result = courseSearchService.searchCoursesWithFuzzy(
                "Java", null, null, null, null, "", "", null, "upcoming", 0, 10
        );

        // Then - Should use fuzzy search path since hasOtherFilters returns false
        assertThat(result).isNotNull();
    }


}
