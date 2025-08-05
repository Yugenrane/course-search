package com.example.course_search.services;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.course_search.document.CourseDocument;
import com.example.course_search.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    /* ----------------------------------------------------------------
       PUBLIC API
       ---------------------------------------------------------------- */
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

        Criteria criteria = buildCriteria(q, minAge, maxAge,
                                          minPrice, maxPrice,
                                          category, type, nextSessionDate);

        // If no filters at all → delegate to findAllCourses
        if (criteria == null) {
            return findAllCourses(sort, page, size);
        }

        Pageable pageable = createPageable(page, size, sort);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.setPageable(pageable);

        SearchHits<CourseDocument> hits =
                elasticsearchOperations.search(query, CourseDocument.class);

        List<CourseDocument> courses = hits.stream()
                                           .map(SearchHit::getContent)
                                           .toList();

        return SearchResponse.builder()
                             .total(hits.getTotalHits())
                             .courses(courses)
                             .build();
    }

    public SearchResponse findAllCourses(String sort, Integer page, Integer size) {
        Query matchAll = Query.of(q -> q.matchAll(m -> m));
        Pageable pageable = createPageable(page, size, sort);

        NativeQuery query = NativeQuery.builder()
                                       .withQuery(matchAll)
                                       .withPageable(pageable)
                                       .build();

        SearchHits<CourseDocument> hits =
                elasticsearchOperations.search(query, CourseDocument.class);

        return SearchResponse.builder()
                             .total(hits.getTotalHits())
                             .courses(hits.stream().map(SearchHit::getContent).toList())
                             .build();
    }

    /* ----------------------------------------------------------------
       INTERNAL HELPERS
       ---------------------------------------------------------------- */
    private Criteria buildCriteria(String q,
                                   Integer minAge, Integer maxAge,
                                   Double minPrice, Double maxPrice,
                                   String category, String type,
                                   OffsetDateTime nextSession) {

        Criteria c = null;      // start with null, chain with and/or
        boolean has = false;

        if (q != null && !q.isBlank()) {
            c = new Criteria("title").matches(q)
                    .or(new Criteria("description").matches(q));
            has = true;
        }

        if (minAge != null) {
            Criteria age = new Criteria("maxAge").greaterThanEqual(minAge);
            c = has ? c.and(age) : age;   has = true;
        }
        if (maxAge != null) {
            Criteria age = new Criteria("minAge").lessThanEqual(maxAge);
            c = has ? c.and(age) : age;   has = true;
        }

        if (minPrice != null) {
            Criteria p = new Criteria("minPrice").greaterThanEqual(minPrice);
            c = has ? c.and(p) : p;       has = true;
        }
        if (maxPrice != null) {
            Criteria p = new Criteria("maxPrice").lessThanEqual(maxPrice);
            c = has ? c.and(p) : p;       has = true;
        }

        if (category != null && !category.isBlank()) {
            Criteria cat = new Criteria("category").is(category);
            c = has ? c.and(cat) : cat;   has = true;
        }
        if (type != null && !type.isBlank()) {
            Criteria t = new Criteria("type").is(type);
            c = has ? c.and(t) : t;       has = true;
        }

        if (nextSession != null) {
            Criteria d = new Criteria("nextSessionDate")
                             .greaterThanEqual(nextSession.toInstant());
            c = has ? c.and(d) : d;       has = true;
        }

        return has ? c : null;
    }

    private Pageable createPageable(Integer page, Integer size, String sort) {
        int p   = (page != null && page >= 0)           ? page : 0;
        int sz  = (size != null && size > 0 && size <= 100) ? size : 10;
        Sort s  = buildSort(sort);
        return PageRequest.of(p, sz, s);
    }

    private Sort buildSort(String sortParam) {
        if (sortParam == null) sortParam = "upcoming";

        return switch (sortParam.toLowerCase()) {
            case "priceasc"  -> Sort.by(Sort.Direction.ASC,  "minPrice")
                                    .and(Sort.by(Sort.Direction.ASC, "nextSessionDate"));
            case "pricedesc" -> Sort.by(Sort.Direction.DESC, "maxPrice")
                                    .and(Sort.by(Sort.Direction.ASC, "nextSessionDate"));
            case "dateasc"   -> Sort.by(Sort.Direction.ASC,  "nextSessionDate");
            case "datedesc"  -> Sort.by(Sort.Direction.DESC, "nextSessionDate");
            default          -> Sort.by(Sort.Direction.ASC,  "nextSessionDate"); // upcoming default
        };
    }
}
