# Chapter 1 - Introduction to Microservices

> Goal: Understand the evolution from monolithic to microservices architecture.

## 1. What is Software Architecture?

Software architecture is the high-level structural design that defines:
- How components communicate
- How data flows through the system
- Deployment strategy
- Long-term evolution and scale

A good architecture exhibits:
- **Scalability**: handles growth gracefully
- **Maintainability**: clean, understandable code structure
- **Security**: defends against threats
- **Testability**: code is verifiable
- **Fault tolerance**: survives failures

Architecture decisions have lasting impact and should be made thoughtfully.

---

## 2. What is a Monolithic Application?

A monolith packages all business modules into a single deployable application.

Example e-commerce monolith:
- Product Management
- Order Management
- Inventory
- Payment
- Notification

All modules share codebase, deployment process, and typically one database.

### Advantages

- Simple development for small teams
- Single deployment pipeline
- Easy debugging (all in one place)
- ACID transactions across modules

### Disadvantages

- Full application redeploy for any change
- Cannot scale individual modules
- One module's failure can crash the entire system
- Large codebase becomes hard to maintain
- Teams block each other during development

**Interview Q:** Why do companies initially build monoliths?

**Answer:** They're simpler to develop, deploy, and manage during early product stages when simplicity beats scaling concerns.

---

## 3. Why Microservices?

As businesses grow, different modules have different requirements:

**E-commerce example:**
- Product searches: 10,000/minute
- Orders placed: 500/minute

Scaling the entire monolith wastes resources—only Product module needs more infrastructure.

**Solution:** Break into independent services, each scalable independently.

---

## 4. What is a Microservice?

A microservice is a small, independently deployable application owning a single business capability.

Example for this project:

**Product Service:**
- Product CRUD operations
- Product pricing
- Inventory management

**Order Service:**
- Order creation and cancellation
- Order history and status

Each service owns:
- Business logic
- Public APIs
- Its own database

---

## 5. Core Principles

### Single Responsibility
Each service focuses on one business capability.

### Database per Service
Every service owns its own database.
- Never access another service's database directly
- Ensures loose coupling
- Enables independent deployment
- Improves security

### API Communication
Services communicate using APIs (REST, gRPC) or events (Kafka) rather than database joins.

---

## 6. Advantages of Microservices

- Independent deployment (no full system redeploy)
- Independent scaling (scale what you need)
- Technology flexibility (each service can use different tech)
- Better fault isolation (one service failure doesn't cascade)
- Faster release cycles (smaller teams move faster)
- Smaller codebases (easier to understand)
- Team autonomy (teams own their services)

---

## 7. Disadvantages

- Distributed transactions (hard to maintain consistency)
- Network latency (remote calls are slower than in-process)
- Service discovery (which service runs where?)
- Monitoring complexity (more services to observe)
- Debugging across services (harder to trace issues)
- More infrastructure (more services to run and maintain)

**Key insight:** Microservices solve business scaling problems but introduce distributed-system challenges.

---

## 8. When NOT to Use Microservices

Avoid microservices if:
- Team is small
- Application is small/simple
- Building an MVP
- Business domain is simple

**Better choice:** Start with a modular monolith, split into services as business needs demand.

---

## 9. Applying These Concepts to Our Project

We've created:

**Product Service:**
- Owns products data
- Owns stock and inventory
- Source of truth for product information

**Order Service:**
- Owns orders
- Calls Product Service via Feign (REST)
- Stores order snapshot (copy of product data at purchase time)

This follows:
- Single Responsibility Principle
- Database-per-Service Principle

---

## 10. Interview Questions

**Q1: Why did microservices evolve?**

A: Monoliths become difficult to scale, deploy, and maintain as systems grow. Microservices allow independent deployment, scaling, and team ownership of business capabilities.

**Q2: What is the biggest advantage of microservices?**

A: Independent deployment and scaling while allowing teams to work autonomously.

**Q3: What is the biggest disadvantage?**

A: Distributed system complexity (network failures, eventual consistency, distributed transactions, observability).

**Q4: Why database per service?**

A: Enforces service ownership, avoids tight coupling through shared schema, enables independent evolution.

**Q5: Can two microservices share the same database?**

A: Considered an anti-pattern—it breaks service boundaries and creates tight coupling.

---

## Summary

- Start with a monolith unless there's a clear business reason for microservices
- Every microservice owns one business capability
- Every microservice owns its own database
- Services communicate through APIs or events
- Product Service owns product data
- Order Service owns order data
- Never bypass service APIs by accessing another service's database

---

## Cheat Sheet

**Monolith:**
- Single deployment
- Shared database  
- Easy to start
- Hard to scale independently

**Microservices:**
- Independent deployment
- Database per service
- API communication
- Better scalability
- More operational complexity
