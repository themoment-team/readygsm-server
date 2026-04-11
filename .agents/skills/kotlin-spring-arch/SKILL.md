---
name: kotlin-spring-arch
description: Java + Spring Boot 4.0 architecture detailed guide for this project
---

# Java + Spring Boot Architecture Guide

## Layer Structure

### Controller
- Role: Request validation, DTO conversion, HTTP response
- Annotations: `@RestController`, `@RequestMapping`
- Validation: `@Valid`, `@Validated`
- Response: Return DTO directly (SDK wrapper auto-wraps with `CommonApiResponse`)

### Service
- Role: Business logic, transaction management
- Transaction:
  - Read: `@Transactional(readOnly = true)`
  - Write: `@Transactional`
- Dependencies: Inject Repository via constructor injection

### Repository
- Role: Data access
- JPA: Extend `JpaRepository`
- Avoid N+1: Fetch Join, `@EntityGraph`

## Transaction Strategy

### Read-only Optimization
```java
@Transactional(readOnly = true)
public List<StudentResDto> findStudents() {
    return repository.findAll().stream()
            .map(StudentResDto::from)
            .toList();
}
```

### N+1 Problem Resolution
```java
// ❌ N+1 occurs
repository.findAll(); // 1 query
entity.getRelatedEntity(); // N queries

// ✅ Fetch Join
@Query("SELECT e FROM Entity e JOIN FETCH e.relatedEntity")
List<Entity> findAllWithRelated();
```

## Exception Handling

### Custom Exception
Do NOT create subclasses of `ExpectedException`. Instantiate it directly:
```java
studentRepository.findById(id).orElseThrow(() ->
    new ExpectedException("Student not found. studentId: " + id, HttpStatus.NOT_FOUND)
);
```

### Global Handler
See `src/main/java/team/themoment/readygsmserver/global/` — the SDK's built-in `GlobalExceptionHandler` handles `ExpectedException` automatically

## DTO Conversion Pattern

```java
// Entity → ResDto (static factory)
public static StudentResDto from(Student student) {
    return new StudentResDto(student.getId(), student.getName());
}

// ReqDto → Entity
public Student toEntity() {
    return new Student(this.name);
}
```