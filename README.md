# Virality Engine API

A robust, high-performance Spring Boot microservice that acts as a central API gateway and guardrail system with Redis-based virality scoring and atomic locks.

## 🚀 Features

### Core API
- **User Management**: Create and manage users with premium status
- **Bot Management**: Create AI bots with persona descriptions
- **Post Management**: Create posts with author tracking
- **Comment System**: Hierarchical comments with depth levels
- **Like System**: Track user interactions with posts

### Redis Virality Engine
- **Real-time Scoring**: 
  - Bot Reply = +1 Point
  - Human Like = +20 Points
  - Human Comment = +50 Points
- **Atomic Guardrails**:
  - Horizontal Cap: Maximum 100 bot replies per post
  - Vertical Cap: Maximum 20 comment thread depth levels
  - Cooldown Cap: 10-minute bot-human interaction cooldown

### Smart Notification System
- **Throttling**: 15-minute notification cooldown
- **Batching**: Queues pending notifications
- **CRON Sweeper**: Processes notifications every 5 minutes (testing) / 15 minutes (production)

## 🛠 Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **PostgreSQL** (Primary Database)
- **Redis** (Virality Engine & Guardrails)
- **Spring Data JPA/Hibernate**
- **Spring Data Redis**
- **Lombok** (Code Generation)
- **Maven** (Build Tool)

## 📋 Prerequisites

- Docker + Docker Compose (or Docker Desktop)
- Java 17+ (project target is Java 17; newer JDKs also work)
- Maven (optional if you use the Maven Wrapper `mvnw`/`mvnw.cmd`)

## 🚀 Quick Start

### 1. Start Infrastructure

```bash
docker compose up -d
```

This will start:
- PostgreSQL on `localhost:5433` (container port `5432` mapped to host `5433`)
- Redis on `localhost:6379`

### 2. Run Application

```bash
./mvnw -DskipTests spring-boot:run
```

The API will be available at `http://localhost:8080`

#### Windows (PowerShell)

```powershell
docker compose up -d
.\mvnw.cmd -DskipTests spring-boot:run
```

### 3. Test with Postman

Import the provided `ViralityEngineAPI.postman_collection.json` into Postman to test all endpoints.

## 📡 API Endpoints

### Users
- `POST /api/user` - Create user
- `GET /api/user?id={id}` - Get user

### Bots
- `POST /api/bot` - Create bot

### Posts
- `POST /api/post` - Create post
- `GET /api/post?postId={id}` - Get post
- `POST /api/post/{postId}/like` - Like post

### Comments
- `POST /api/comment` - Add comment
- `GET /api/comment?postId={id}` - Get comments for post

## 🔒 Thread Safety & Atomic Operations

### Redis Guardrails Implementation

The system guarantees thread safety through Redis atomic operations:

#### Horizontal Cap (Bot Reply Limit)
```java
// Atomic increment with rollback
Long currentCount = redisTemplate.opsForValue().increment(key);
if (currentCount > MAX_BOT_REPLIES_PER_POST) {
    redisTemplate.opsForValue().increment(key, -1); // Rollback
    return false;
}
```

#### Cooldown System
```java
// Atomic set with TTL (Time-To-Live)
Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "active", 
    Duration.ofMinutes(10));
```

#### Virality Score Updates
```java
// Atomic increment for scoring
redisTemplate.opsForValue().increment("post:{id}:virality_score", points);
```

### Statelessness Guarantee

- **No in-memory state**: All counters and cooldowns stored in Redis
- **Atomic operations**: Using Redis INCR, SETIFABSENT for concurrency
- **Automatic cleanup**: TTL-based expiration for cooldowns
- **Database consistency**: Redis acts as gatekeeper before DB transactions

## 🧪 Testing Scenarios

### Race Condition Test
To test the horizontal cap under concurrent load:

```bash
# Send 200 concurrent requests to bot comment endpoint
# Expected: Exactly 100 comments succeed, 100 fail with 429 error
```

### Guardrail Tests
1. **Bot Reply Limit**: Send 101 bot comments to same post (should fail at 101st)
2. **Comment Depth**: Try depth level 21 (should fail with 400 error)
3. **Cooldown**: Same bot interacting with same human within 10 minutes (should fail)

## 📊 Redis Key Structure

```
post:{id}:virality_score     # Virality score counter
post:{id}:bot_count          # Bot reply counter
cooldown:bot_{id}:human_{id} # Bot-human cooldown (TTL: 10min)
notif_cooldown:user_{id}     # Notification cooldown (TTL: 15min)
user:{id}:pending_notifs     # Pending notifications list
```

## 🔧 Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/virality_engine
spring.datasource.username=virality_user
spring.datasource.password=virality_password
```

### Redis Configuration
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
```

## 🧰 Troubleshooting

### Port 8080 already in use
Either stop the process using port `8080` or run the app on a different port:

```bash
./mvnw -DskipTests spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Docker services not reachable
- Ensure Docker containers are running:

```bash
docker compose ps
```

- Ensure ports match the config:
  - Postgres: `localhost:5433`
  - Redis: `localhost:6379`

## 📈 Performance Considerations

- **Redis Operations**: All guardrail checks are O(1) atomic operations
- **Connection Pooling**: Redis template uses connection pooling
- **Batch Processing**: Notifications batched to prevent spam
- **TTL Management**: Automatic cleanup prevents memory leaks

## 🚨 Error Handling

The API returns appropriate HTTP status codes:
- `200` - Success
- `400` - Bad Request (e.g., comment depth exceeded)
- `429` - Too Many Requests (e.g., bot limits exceeded)
- `404` - Not Found (e.g., post not found)
- `500` - Internal Server Error

## 🔄 CRON Jobs

### Notification Sweeper
```java
@Scheduled(fixedRate = 300000) // 5 minutes for testing
public void processPendingNotifications() {
    // Process queued notifications
}
```

In production, change to `@Scheduled(fixedRate = 900000)` for 15-minute intervals.

## 📝 Architecture Decisions

1. **Redis for Guardrails**: Chosen for atomic operations and TTL support
2. **Stateless Design**: No in-memory state for horizontal scalability
3. **Database as Source of Truth**: PostgreSQL stores actual content
4. **Smart Batching**: Prevents notification spam while maintaining engagement

## 🧩 Future Enhancements

- Rate limiting per user
- Advanced notification preferences
- Real-time WebSocket updates
- Analytics dashboard
- Machine learning for virality prediction

## 📞 Support

For issues and questions, please refer to the test scenarios in the Postman collection or check the application logs for detailed error information.
