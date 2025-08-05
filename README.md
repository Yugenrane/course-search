# Course Search API (Spring Boot 3 + Elasticsearch 8)

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
