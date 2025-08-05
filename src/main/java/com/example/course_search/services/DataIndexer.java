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
        // Delete old data to avoid duplicates during development
        courseRepository.deleteAll();

        InputStream input = new ClassPathResource("sample-courses.json").getInputStream();
        List<CourseDocument> courses =
                objectMapper.readValue(input, new TypeReference<>() {});

        courses.forEach(CourseDocument::setSuggestFromTitle);
        courseRepository.saveAll(courses);

        System.out.println("Indexed " + courses.size() + " courses into Elasticsearch");
    }
}
