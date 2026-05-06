---
name: api-design
description: REST API design guide for this project
---

# REST API Design Guide

## URL Design

- RESTful principles: `/v1/auth/token`, `/v1/students`
- Use plural nouns: `/students`, `/users`
- Hierarchy: `/students/{id}/records`

## Query Parameters

- Filtering: `?status=active`
- Pagination: `?page=0&size=20`
- Sorting: `?sort=createdAt,desc`

## OpenAPI Documentation

```java
@Operation(summary = "학생 정보 조회", description = "학생 ID로 정보를 조회합니다.")
@ApiResponse(responseCode = "200", description = "조회 성공")
@GetMapping("/{id}")
public StudentResDto findStudent(@PathVariable Long id) {
    return studentService.findById(id);
}
```

## Response Format

- Success: Handled automatically by the-sdk (`CommonApiResponse` wrapper)
- Error: Throw `ExpectedException(message, HttpStatus)` → SDK GlobalExceptionHandler processes it
