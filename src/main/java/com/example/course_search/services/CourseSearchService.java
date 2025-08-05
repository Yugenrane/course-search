package com.example.course_search.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public SearchResponse searchCourses(String q,
                                        Integer minAge,
                                        Integer maxAge,
                                        Double minPrice,
                                        Double maxPrice,
                                        String category,
                                        String type,
                                        OffsetDateTime nextSessionDate,
                                        String sort,
                                        int page,
                                        int size) {

        Criteria criteria = new Criteria();
        boolean hasCriteria = false;

        // Keyword search in title and description
        if (q != null && !q.isBlank()) {
            Criteria titleCriteria = new Criteria("title").matches(q);
            Criteria descCriteria = new Criteria("description").matches(q);
            criteria = titleCriteria.or(descCriteria);
            hasCriteria = true;
        }

        // Age filtering
        if (minAge != null) {
            Criteria ageCriteria = new Criteria("maxAge").greaterThanEqual(minAge);
            criteria = hasCriteria ? criteria.and(ageCriteria) : ageCriteria;
            hasCriteria = true;
        }
        if (maxAge != null) {
            Criteria ageCriteria = new Criteria("minAge").lessThanEqual(maxAge);
            criteria = hasCriteria ? criteria.and(ageCriteria) : ageCriteria;
            hasCriteria = true;
        }

        // Price filtering
        if (minPrice != null) {
            Criteria priceCriteria = new Criteria("minPrice").greaterThanEqual(minPrice);
            criteria = hasCriteria ? criteria.and(priceCriteria) : priceCriteria;
            hasCriteria = true;
        }
        if (maxPrice != null) {
            Criteria priceCriteria = new Criteria("maxPrice").lessThanEqual(maxPrice);
            criteria = hasCriteria ? criteria.and(priceCriteria) : priceCriteria;
            hasCriteria = true;
        }

        // Category filtering
        if (category != null && !category.isBlank()) {
            Criteria categoryCriteria = new Criteria("category").is(category);
            criteria = hasCriteria ? criteria.and(categoryCriteria) : categoryCriteria;
            hasCriteria = true;
        }

        // Type filtering
        if (type != null && !type.isBlank()) {
            Criteria typeCriteria = new Criteria("type").is(type);
            criteria = hasCriteria ? criteria.and(typeCriteria) : typeCriteria;
            hasCriteria = true;
        }

        // Date filtering
        if (nextSessionDate != null) {
            Criteria dateCriteria = new Criteria("nextSessionDate")
                    .greaterThanEqual(nextSessionDate.toInstant());
            criteria = hasCriteria ? criteria.and(dateCriteria) : dateCriteria;
            hasCriteria = true;
        }

        // If no criteria specified, return all courses
        if (!hasCriteria) {
            return findAllCourses(sort, page, size);
        }

        // For Pagination
        Pageable pageable=createPageable(page, size, sort);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageable);
        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        long total = searchHits.getTotalHits();

        return SearchResponse.builder()
                .total(total)
                .courses(courses)
                .build();
    }

    public SearchResponse findAllCourses(String sort, Integer page, Integer size) {
        Query matchAllQuery = Query.of(q -> q.matchAll(m -> m));

        Pageable pageable=createPageable(page, size, sort);

        NativeQuery query = NativeQuery.builder()
                .withQuery(matchAllQuery)
                .withPageable(pageable)
                .build();

        SearchHits<CourseDocument> searchHits =
                elasticsearchOperations.search(query, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();
        long total = searchHits.getTotalHits();

        return SearchResponse.builder()
                .total(total)
                .courses(courses)
                .build();

    }

        public SearchResponse findAllCourses() {
            return findAllCourses("dateAsc", 0 , 10);
    }

    public SearchResponse findAllCourses(String sort) {
        return findAllCourses(sort, 0, 10);
    }

    private Sort buildSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            sortParam = "upcoming"; // Default
        }

        return switch (sortParam.toLowerCase()) {
            case "upcoming" -> Sort.by(Sort.Direction.ASC, "nextSessionDate");
            case "priceasc" -> Sort.by(Sort.Direction.ASC, "minPrice")
                    .and(Sort.by(Sort.Direction.ASC, "nextSessionDate")); // Secondary sort
            case "pricedesc" -> Sort.by(Sort.Direction.DESC, "maxPrice")
                    .and(Sort.by(Sort.Direction.ASC, "nextSessionDate")); // Secondary sort
            case "dateasc" -> Sort.by(Sort.Direction.ASC, "nextSessionDate");
            case "datedesc" -> Sort.by(Sort.Direction.DESC, "nextSessionDate");
            default -> Sort.by(Sort.Direction.ASC, "nextSessionDate"); // Default fallback
        };
    }

    private Pageable createPageable(Integer page, Integer size, String sort) {
        int validPage = (page != null && page >= 0) ? page : 0;
        int validSize = (size != null && size > 0 && size <= 100) ? size : 10; // Max 100 per page

        Sort sortObj = buildSort(sort);
        return PageRequest.of(validPage, validSize, sortObj);
    }

    // From this Fuzzy Search logic started
    private boolean hasOtherFilters(Integer minAge, Integer maxAge, Double minPrice, Double maxPrice,
                                    String category, String type, OffsetDateTime nextSessionDate) {
        return minAge != null || maxAge != null || minPrice != null || maxPrice != null ||
                (category != null && !category.isBlank()) || (type != null && !type.isBlank()) ||
                nextSessionDate != null;
    }

    private void addResults(List<CourseDocument> allResults, Set<String> seenIds, List<CourseDocument> newResults) {
        for (CourseDocument course : newResults) {
            if (!seenIds.contains(course.getId())) {
                allResults.add(course);
                seenIds.add(course.getId());
            }
        }
    }

    private List<CourseDocument> searchExact(String q, String sort, int page, int size) {
        try {
            Criteria criteria = new Criteria("title").is(q)
                    .or(new Criteria("description").is(q));
            return executeSearch(criteria, sort, page, size);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<CourseDocument> searchContains(String q, String sort, int page, int size) {
        try {
            Criteria criteria = new Criteria("title").contains(q.toLowerCase())
                    .or(new Criteria("description").contains(q.toLowerCase()));
            return executeSearch(criteria, sort, page, size);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<CourseDocument> searchWildcard(String q, String sort, int page, int size) {
        try {
            List<CourseDocument> results = new ArrayList<>();

            String[] patterns = {
                    "*" + q.toLowerCase() + "*",
                    q.toLowerCase() + "*",
                    "*" + q.toLowerCase()
            };

            for (String pattern : patterns) {
                try {
                    Criteria criteria = new Criteria("title").expression(pattern);
                    results.addAll(executeSearch(criteria, sort, 0, size));
                    if (results.size() >= size) break;
                } catch (Exception ignored) {}
            }

            return results;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<CourseDocument> searchCharacterMatch(String q, String sort, int page, int size) {
        try {
            List<CourseDocument> allCourses = executeSearch(new Criteria(), sort, 0, 100);
            List<CourseDocument> matches = new ArrayList<>();

            String queryLower = q.toLowerCase();

            for (CourseDocument course : allCourses) {
                String titleLower = course.getTitle().toLowerCase();

                if (isCharacterMatch(queryLower, titleLower)) {
                    matches.add(course);
                    if (matches.size() >= size) break;
                }
            }
            return matches;
        } catch (Exception e) {
            return List.of();
        }
    }

    private boolean isCharacterMatch(String query, String title) {
        if (Math.abs(query.length() - title.length()) > 2) return false;

        int matches = 0;
        int minLength = Math.min(query.length(), title.length());

        for (int i = 0; i < minLength; i++) {
            if (i < query.length() && query.charAt(i) == title.charAt(i)) {
                matches++;
            }
        }
        return (double) matches / minLength >= 0.6;
    }

    private List<CourseDocument> executeSearch(Criteria criteria, String sort, int page, int size) {
        try {
            Pageable pageable = createPageable(page, size, sort);
            CriteriaQuery query = new CriteriaQuery(criteria);
            query.setPageable(pageable);

            SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(query, CourseDocument.class);
            return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    public SearchResponse searchCoursesWithFuzzy(String q,
                                                 Integer minAge,
                                                 Integer maxAge,
                                                 Double minPrice,
                                                 Double maxPrice,
                                                 String category,
                                                 String type,
                                                 OffsetDateTime nextSessionDate,
                                                 String sort,
                                                 int page,
                                                 int size) {

        if (q == null || q.isBlank() || hasOtherFilters(minAge, maxAge, minPrice, maxPrice, category, type, nextSessionDate)) {
            return searchCourses(q, minAge, maxAge, minPrice, maxPrice, category, type, nextSessionDate, sort, page, size);
        }

        List<CourseDocument> allResults = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        addResults(allResults, seenIds, searchExact(q, sort, page, size));

        if (allResults.size() < size) {
            addResults(allResults, seenIds, searchContains(q, sort, page, size));
        }

        if (allResults.size() < size && q.length() >= 3) {
            addResults(allResults, seenIds, searchWildcard(q, sort, page, size));
        }

        if (allResults.size() < size && q.length() >= 3) {
            addResults(allResults, seenIds, searchCharacterMatch(q, sort, page, size));
        }

        List<CourseDocument> finalResults = allResults.stream()
                .limit(size)
                .collect(Collectors.toList());

        return SearchResponse.builder()
                .total(allResults.size())
                .courses(finalResults)
                .build();
    }
}
