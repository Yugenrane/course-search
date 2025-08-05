# Course Search API (Spring Boot 3.5.4 + Elasticsearch 8.19.0)

Full-text search service to discover courses, workshops, and camps by topic, age, price, and date.  
Includes typo-tolerant **fuzzy search** and instant **autocomplete** suggestions.

---

## ✨ Features
- Full-text & fuzzy search on **title** and **description**
- Filters: age range, price range, category, type, next session date
- Autocomplete suggestions endpoint
- Pagination & multiple sort orders (upcoming, price asc/desc, date asc/desc)
- Sample dataset auto-indexed at startup

---

## 📋 Prerequisites
- Java 17+
- Maven 3.9+
- Docker 20.10+ (for Elasticsearch)

---

## 🚀 Quick Start

### 1  Launch Elasticsearch
A ready-to-use `docker-compose.yml` is in the project root.
docker compose up -d # starts Elasticsearch on http://localhost:9200


### 2  Build & Run the Spring Boot application
./mvnw clean package # build
java -jar target/course-search-0.0.1-SNAPSHOT.jar


### 3  Populate the index (automatic)
On startup the **DataIndexer** reads `src/main/resources/sample-courses.json`  
and bulk-indexes the records into the `courses` index—no manual action required.

Console log example:
Indexed 50 courses into Elasticsearch


---

## 🌐 REST API

Base URL: `http://localhost:8080/api`

| Method | Path | Purpose |
| ------ | ---- | ------- |
| GET | `/search` | Full-text + **fuzzy** search (supports filters) |
| GET | `/allCourses` | Paginated list of every course |
| GET | `/search/suggest` | Autocomplete suggestions |

### Common Query Parameters

| Name | Type | Notes |
| ---- | ---- | ----- |
| `q` | string | search keywords |
| `minAge` / `maxAge` | int | filter by age range |
| `minPrice` / `maxPrice` | double | filter by price |
| `category` | string | e.g. `Math`, `Science` |
| `type` | string | `COURSE` or `WORKSHOP` |
| `nextSessionDate` | ISO-8601 | courses starting **on/after** date |
| `sort` | enum | `upcoming` (default) \| `priceAsc` \| `priceDesc` \| `dateAsc` \| `dateDesc` |
| `page` | int | page index (0-based) |
| `size` | int | page size (1-100, default 10) |

---

## 🔗 Example Requests

### 1  Basic search
"http://localhost:8080/api/search?q=math&minAge=6&sort=priceAsc&page=0&size=5"

### 2  List all courses (page 2)
"http://localhost:8080/api/allCourses?page=2&size=10&sort=upcoming"

### 3  Autocomplete suggestions
"http://localhost:8080/api/search/suggest?q=jav&size=5"

_Response example:_
{
"suggestions": [
"Java Programming",
"JavaScript Basics"
]
}

### 4  Fuzzy search (typo tolerant)
Typo “Maht” should still match “Math for Beginners”
"http://localhost:8080/api/search?q=Maht&size=3"

_Response example (truncated):_
{
"total": 1,
"courses": 
```[
{
"id": "C001",
"title": "Math for Beginners",
"category": "Math",
"minPrice": 50.0,
"nextSessionDate": "2025-08-10T09:00:00Z"
}
]
}```

---

## 🧪 Development Commands
./mvnw test # run unit tests + JaCoCo coverage
docker compose down # stop Elasticsearch container

---

## 📄 License
MIT

