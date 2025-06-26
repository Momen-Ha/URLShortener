# 🚀 URL Shortener Service

A scalable and distributed URL shortening service built with **Spring Boot**.

---

### 📌 Tech Stack

- ☕ **Java 17**
- 🌱 **Spring Boot 3**
- 📦 **MongoDB** – store urls
- ⚡ **Redis** – cache urls
- 🦉 **ZooKeeper** – Ensures distributed coordination by assigning ID ranges to nodes using znodes
- 🐋 **Docker**

---

## 🧠 Key Features

✅ **Distributed ID Generation** using Apache ZooKeeper (Curator)  
✅ **Base62 Short Code Encoding** for compact, unique URLs  
✅ **Persistent Storage** in MongoDB  
✅ **Caching Layer** with Redis for performance  
✅ **IP-based Rate Limiting** to control abuse  
✅ **Global Error Handling** for clean API responses  

---

## 📁 Project Structure

```
.
├── config/
├── constants/
├── controller/
├── DTO/
├── exceptions/
├── model/
├── repository/
├── service/
├── utils/
└──  UrlShortenerApplication.java

```

Key components include:
- `ZookeeperService.java` – Coordinates ID ranges  
- `Base62Encoder.java` – Encodes numeric ID to short codes  
- `RateLimitFilter.java` – Handles request throttling  
- `RedisService.java` – Manages caching layer  

---

## 🗃️ Data Model

```java
@Document(collection = "urls")
public class Url {
    @Id
    private String id;

    @NotBlank
    private String url;

    @NotBlank
    private String shortCode;

    @NotNull
    private Instant createdAt;
    private Instant updatedAt;

}
```

---

## 🔗 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/urls` | Create short URL with `{ "url": "..." }` |
| `GET`  | `/{shortCode}` | Redirect to original URL |
| `GET`  | `/api/v1/urls/{shortCode}/stats` | View stats for a given short URL |

---

## 🧩 ZooKeeper ID Coordination

Each application instance registers with ZooKeeper under a znode like:  
```
/urlshortener/nodes/{nodeId}
```

ZooKeeper assigns a unique numeric range (e.g., node 0 → `0–100000`). This ensures:

- No ID collisions
- Horizontal scalability  

---

## 🏃 Getting Started

```bash
# Requirements:
# Java 17+, Maven, Docker (optional)

# 1. Build the app
mvn clean package

# 2. Run the app
java -jar target/urlshortener-0.0.1-SNAPSHOT.jar
```

Ensure MongoDB, Redis, and ZooKeeper services are running!

---

## ⚙️ Configuration (`application.yaml`)

```yaml
spring:
  application:
    name: URLShortener
  data:
    mongodb:
      uri: mongodb://localhost:27017/urls
    redis:
      host: localhost
      port: 6379
      jedis:
        max-active: 10
        max-idle: 5
        min-idle: 1

  cache:
    type: redis

logging:
  level:
    org.springframework.cache: TRACE
server:
  port: 8080

zookeeper:
  host: localhost:2181
  timeout: 4000
  node-path: "/counter"
  range-length: 100000
  max-retry: 3
  session-timeout: 12000

```

---

## 🧪 URL Shortening Flow

1. **Node Initialization**  
   `ZookeeperService` registers and obtains a `[start, end]` ID range.

2. **ID Generation**  
   `UrlService` uses an atomic counter + offset to generate unique numeric ID.

3. **Base62 Encoding**  
   Converts numeric ID to `shortCode` using `Base62Encoder`.

4. **Persistence**  
   Stores URL mapping in MongoDB, and caches in Redis.

---

## 🐳 Docker Compose

```yaml
services:
  mongodb:
    image: mongo:latest
    restart: unless-stopped
    container_name: urls-mongodb
    ports:
      - "27018:27017"
    volumes:
      - db_data:/data/db
    networks:
      - spring-boot-app

  redis:
    image: redislabs/rebloom:latest
    container_name: redis
    restart: unless-stopped
    ports:
      - "6380:6379"
    volumes:
      - redis_data:/data
    networks:
      - spring-boot-app

  url-shortener:
    container_name: url-shortener-app
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8081:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/urls
      - ZOOKEEPER_HOST=zookeeper:2181
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      - mongodb
      - zookeeper
      - redis
    networks:
      - spring-boot-app

  url-shortener2:
    container_name: url-shortener2-app
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8082:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/urls
      - ZOOKEEPER_HOST=zookeeper:2181
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      - mongodb
      - zookeeper
      - redis
    networks:
      - spring-boot-app

  nginx:
    image: nginx:latest
    restart: unless-stopped
    container_name: nginx
    ports:
      - "81:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - url-shortener
      - url-shortener2
    networks:
      - spring-boot-app

  zookeeper:
    image: zookeeper:latest
    container_name: zookeeper
    restart: unless-stopped
    ports:
      - "2182:2181"
    environment:
      ZOO_MY_ID: 1
      ZOO_SERVERS: server.1=zookeeper1:2888:3888;2181
    volumes:
      - zookeeper_data:/data
      - zookeeper_datalog:/datalog
    networks:
      - spring-boot-app

volumes:
  redis_data:
  db_data:
  zookeeper_data:
  zookeeper_datalog:

networks:
  spring-boot-app:

```


