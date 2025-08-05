package com.example.course_search.services;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.repositories.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataIndexer implements ApplicationRunner {
    private final ObjectMapper objectMapper;
    private final CourseRepository courseRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        courseRepository.deleteAll();

        InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
        List<CourseDocument> courseDocuments = objectMapper.readValue(inputStream, new TypeReference<>() {});

        courseDocuments.forEach(CourseDocument::setSuggestFromTitle);

        courseRepository.saveAll(courseDocuments);
        System.out.println("Indexed " + courseDocuments.size() + " courses into Elasticsearch with autocomplete suggestions");
    }
}
