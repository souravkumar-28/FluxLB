# ⚡ FluxLB — Custom Load Balancer

FluxLB is a custom production-style load balancer built with **Java and Spring Boot**, with a **React + Vite monitoring dashboard**.

It acts as a reverse proxy between clients and multiple backend servers and dynamically distributes incoming requests using different load-balancing algorithms.

---

## 🚀 Features

### Load Balancing
- Round Robin
- Weighted Round Robin
- Least Connections
- Random
- IP Hash

### Backend Management
- Dynamic backend server registration
- Remove backend servers at runtime
- Runtime server weight updates
- Automatic backend health checks
- Automatic unhealthy server detection
- Healthy-server-only request routing

### Monitoring
- Active connection tracking
- Total request metrics
- Per-backend request tracking
- Backend UP/DOWN status
- Server weight monitoring
- Automatic dashboard refresh

### Dashboard
- React-based monitoring dashboard
- Runtime algorithm switching
- Add backend server from UI
- Remove backend server from UI
- Update backend weight from UI
- Live backend health status

---

## 🏗️ Architecture

```text
                         ┌──────────────────┐
                         │      Client      │
                         └────────┬─────────┘
                                  │
                                  │ HTTP Request
                                  ▼
                    ┌──────────────────────────┐
                    │          FluxLB          │
                    │     Spring Boot :8080    │
                    │                          │
                    │  ┌────────────────────┐  │
                    │  │ Load Balancer      │  │
                    │  │ Selection Engine   │  │
                    │  └────────────────────┘  │
                    └────────────┬─────────────┘
                                 │
                  ┌──────────────┼──────────────┐
                  │              │              │
                  ▼              ▼              ▼
          ┌────────────┐ ┌────────────┐ ┌────────────┐
          │ Backend 1  │ │ Backend 2  │ │ Backend 3  │
          │   :9001    │ │   :9002    │ │   :9003    │
          └────────────┘ └────────────┘ └────────────┘
                  │              │              │
                  └──────────────┼──────────────┘
                                 │
                         Health Monitoring
                                 │
                                 ▼
                    ┌──────────────────────────┐
                    │      Health Checker      │
                    │      Every 5 seconds     │
                    └──────────────────────────┘


                    ┌──────────────────────────┐
                    │     React Dashboard      │
                    │        :5173             │
                    └────────────┬─────────────┘
                                 │
                                 │ REST API
                                 ▼
                    ┌──────────────────────────┐
                    │          FluxLB           │
                    │         :8080             │
                    └──────────────────────────┘
🔄 Request Flow
Client Request
      │
      ▼
    FluxLB
      │
      ▼
Check Current Algorithm
      │
      ▼
Find Healthy Backend
      │
      ▼
Increment Active Connections
      │
      ▼
Forward Request
      │
      ▼
Backend Response
      │
      ▼
Decrement Active Connections
      │
      ▼
Return Response to Client
⚖️ Load Balancing Algorithms
1. Round Robin

Requests are distributed sequentially across healthy backend servers.

Request 1 → Backend 1
Request 2 → Backend 2
Request 3 → Backend 3
Request 4 → Backend 1
Request 5 → Backend 2
2. Weighted Round Robin

Backend servers receive traffic according to their configured weights.

Example:

Backend 1 → Weight 3
Backend 2 → Weight 2
Backend 3 → Weight 1

Approximate distribution:

Backend 1 → 50%
Backend 2 → 33%
Backend 3 → 17%

FluxLB uses smooth weighted round-robin scheduling to distribute traffic according to server capacity.

3. Least Connections

The backend with the fewest active connections is selected.

Backend 1 → 3 connections
Backend 2 → 1 connection
Backend 3 → 2 connections

Next Request → Backend 2

This is useful when requests have different processing times.

4. Random

A healthy backend server is selected randomly for every request.

Request → Random Healthy Server
5. IP Hash

The client's IP address is hashed to determine the backend server.

Client IP
    │
    ▼
Hash Function
    │
    ▼
Backend Index
    │
    ▼
Selected Backend

Requests from the same client IP will normally map to the same backend as long as the healthy-server set remains unchanged.

❤️ Health Checks

FluxLB continuously checks backend server availability.

The health checker runs every 5 seconds.

FluxLB
   │
   ├── Backend 1 → 🟢 UP
   ├── Backend 2 → 🟢 UP
   └── Backend 3 → 🔴 DOWN

If a backend becomes unavailable:

Backend 3 → DOWN
       │
       ▼
Removed from server selection
       │
       ▼
Traffic routed to healthy servers

When the backend becomes available again, the health checker can mark it as healthy again.

📊 Monitoring

FluxLB maintains runtime metrics including:

Total Requests
Active Connections
Backend Health
Backend Request Count
Server Weight

Example:

Total Requests: 150

Backend 1
Requests: 75
Active Connections: 0
Status: UP

Backend 2
Requests: 50
Active Connections: 0
Status: UP

Backend 3
Requests: 25
Active Connections: 0
Status: UP
🖥️ React Dashboard

The project includes a React dashboard for monitoring and controlling FluxLB.

Dashboard capabilities
View backend servers
View backend health
View active connections
View total requests
Change load-balancing algorithm
Add backend servers
Remove backend servers
Update server weights
Automatically refresh server information

Example:

┌─────────────────────────────────────────────────────┐
│ ⚡ FluxLB                              ● System UP  │
│ Custom Load Balancer Dashboard                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Backend Servers │ Healthy Servers │ Active │ Requests│
│       3         │       3         │   0    │   150   │
│                                                     │
├─────────────────────────────────────────────────────┤
│ Current Algorithm                                  │
│                                                     │
│ [ Weighted Round Robin ▼ ]      [ Change ]         │
│                                                     │
├─────────────────────────────────────────────────────┤
│ Backend Servers                                    │
│                                                     │
│ localhost:9001                    🟢 UP             │
│ Weight: 3                      Active: 0            │
│                                                     │
│ localhost:9002                    🟢 UP             │
│ Weight: 2                      Active: 0            │
│                                                     │
│ localhost:9003                    🟢 UP             │
│ Weight: 1                      Active: 0            │
└─────────────────────────────────────────────────────┘
🛠️ Tech Stack
Backend
Java 17
Spring Boot
Spring Web MVC
Maven
REST APIs
RestTemplate
Java Concurrency Utilities
Frontend
React
Vite
Tailwind CSS
JavaScript
Fetch API
Development Tools
IntelliJ IDEA
Git
GitHub
PowerShell
📁 Project Structure
Backend
fluxlb/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── sourav/
│       │           └── fluxlb/
│       │               │
│       │               ├── config/
│       │               │   ├── CorsConfig.java
│       │               │   └── RestTemplateConfig.java
│       │               │
│       │               ├── controller/
│       │               │   ├── AlgorithmController.java
│       │               │   ├── ProxyController.java
│       │               │   ├── ServerController.java
│       │               │   └── TestController.java
│       │               │
│       │               ├── model/
│       │               │   ├── BackendServer.java
│       │               │   ├── LoadBalancerAlgorithm.java
│       │               │   └── LoadBalancerMetrics.java
│       │               │
│       │               ├── service/
│       │               │   ├── AlgorithmService.java
│       │               │   ├── HealthCheckService.java
│       │               │   └── LoadBalancerService.java
│       │               │
│       │               └── FluxLbApplication.java
│       │
│       └── resources/
│           └── application.properties
│
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
Frontend
fluxlb-dashboard/
│
├── src/
│   ├── App.jsx
│   ├── main.jsx
│   └── index.css
│
├── public/
├── package.json
├── vite.config.js
└── README.md
🔌 API Documentation
Proxy APIs
Forward Request
GET /api/hello

Example:

curl http://localhost:8080/api/hello

Response:

Hello from Backend Server 1
Slow Request
GET /api/slow

Used for testing active connections and Least Connections.

curl http://localhost:8080/api/slow
Backend Server APIs
Get All Servers
GET /api/servers

Example:

curl http://localhost:8080/api/servers
Add Server
POST /api/servers?host=localhost&port=9004

Example:

curl -X POST "http://localhost:8080/api/servers?host=localhost&port=9004"
Remove Server
DELETE /api/servers/{port}

Example:

curl -X DELETE "http://localhost:8080/api/servers/9004"
Update Server Weight
PUT /api/servers/{port}/weight?weight={value}

Example:

curl -X PUT "http://localhost:8080/api/servers/9001/weight?weight=5"
Get Metrics
GET /api/servers/metrics

Example:

curl http://localhost:8080/api/servers/metrics

Response:

{
  "totalRequests": 25
}
⚙️ Algorithm APIs
Get Current Algorithm
GET /api/config/algorithm

Example:

curl http://localhost:8080/api/config/algorithm
Change Algorithm
POST /api/config/algorithm?algorithm={ALGORITHM}

Available algorithms:

ROUND_ROBIN
WEIGHTED_ROUND_ROBIN
LEAST_CONNECTIONS
RANDOM
IP_HASH

Example:

curl -X POST "http://localhost:8080/api/config/algorithm?algorithm=LEAST_CONNECTIONS"
▶️ Getting Started
Prerequisites

Make sure you have:

Java 17+
Maven 3.9+
Node.js 20+
npm
Git
🚀 Running FluxLB
1. Clone Repository
git clone https://github.com/YOUR_USERNAME/FluxLB.git
cd FluxLB
2. Start FluxLB
mvn spring-boot:run

FluxLB will start on:

http://localhost:8080
🖥️ Start Backend Servers

FluxLB requires backend servers to forward requests to.

Run backend applications on:

Backend 1 → http://localhost:9001
Backend 2 → http://localhost:9002
Backend 3 → http://localhost:9003

You can add additional backend servers dynamically through the API or dashboard.

🎨 Start React Dashboard

Open another terminal:

cd fluxlb-dashboard
npm install
npm run dev

Dashboard:

http://localhost:5173
🧪 Testing
Test Round Robin

Set:

curl -X POST "http://localhost:8080/api/config/algorithm?algorithm=ROUND_ROBIN"

Then send multiple requests:

curl http://localhost:8080/api/hello
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/hello

Requests should be distributed sequentially across healthy servers.

Test Weighted Round Robin

Set:

curl -X POST "http://localhost:8080/api/config/algorithm?algorithm=WEIGHTED_ROUND_ROBIN"

Example weights:

Backend 1 → 3
Backend 2 → 2
Backend 3 → 1

Send multiple requests and observe the distribution.

Test Least Connections

Set:

curl -X POST "http://localhost:8080/api/config/algorithm?algorithm=LEAST_CONNECTIONS"

Use the slow endpoint:

curl http://localhost:8080/api/slow

This can be used to create concurrent requests and observe active connection tracking.

Test IP Hash

Set:

curl -X POST "http://localhost:8080/api/config/algorithm?algorithm=IP_HASH"

Then:

curl http://localhost:8080/api/hello
curl http://localhost:8080/api/hello
curl http://localhost:8080/api/hello

Requests from the same client IP should normally map to the same backend while the healthy-server set remains unchanged.

🩺 Test Health Checks
Start all backend servers.
Confirm all servers show UP.
Stop one backend server.
Wait for the health-check interval.
Check:
curl http://localhost:8080/api/servers

The stopped backend should be marked:

healthy: false

Requests should then be routed only to healthy backends.

📈 Example Configuration
FluxLB
Port: 8080

Backend Servers:

localhost:9001
Weight: 5
Status: UP

localhost:9002
Weight: 2
Status: UP

localhost:9003
Weight: 1
Status: UP

Current algorithm:

WEIGHTED_ROUND_ROBIN
🔐 Current Limitations

This project currently maintains backend configuration and runtime state in memory.

Therefore:

Dynamic server changes are lost after application restart.
Metrics reset after restart.
Server configuration is not persisted.
Authentication is not currently implemented.
Distributed state is not currently supported.

These are intentional areas for future development.

🔮 Future Improvements

Planned improvements include:

Infrastructure
Docker containerization
Docker Compose
Kubernetes deployment
Distributed Systems
Redis-based shared state
Distributed rate limiting
Persistent backend configuration
Multi-instance FluxLB support
Observability
Prometheus metrics
Grafana dashboards
Request latency tracking
Error-rate monitoring
Backend response-time monitoring
Performance
Connection pooling
Non-blocking request forwarding
Configurable health-check intervals
Request timeout handling
Circuit breaker support
Testing
JUnit
Mockito
Integration testing
Load testing with k6
Benchmarking
Security
API authentication
Role-based access control
CORS configuration
Request validation
Rate limiting
🎯 Learning Objectives

This project was built to understand practical concepts related to:

Load balancing
Reverse proxies
Distributed systems
Backend architecture
HTTP request forwarding
Server health monitoring
Concurrency
Runtime configuration
REST API design
System observability
React dashboard development
👨‍💻 Author

Sourav Kumar

B.Tech — Information Technology

⭐ Project Status
Backend        ████████████████████  Complete
Load Balancing ████████████████████  Complete
Health Checks  ████████████████████  Complete
Dynamic Servers████████████████████  Complete
Dashboard      ████████████████████  Complete
Monitoring     ████████████░░░░░░░░  In Progress
Production     ████████░░░░░░░░░░░░  Future
📜 License

This project is intended for educational and portfolio purposes.
