---
description: REST API design guide
---

# REST API Design Guide

## URL Design

- RESTful principles: `/v1/auth/login`
- Use plural: `/students`, `/users`
- Hierarchy: `/students/{id}/applications`

## Query Parameters

- Filtering: `?status=active`
- Pagination: `?page=0&size=20`
- Sorting: `?sort=createdAt,desc`

## OpenAPI Documentation

```java
@Operation(summary = "학생 정보 조회", description = "...")
@ApiResponse(responseCode = "200", description = "Success")
@GetMapping("/students/{id}")
public CommonApiResponse<StudentResDto> getStudent(@PathVariable Long id)
```

## Response Format

- Success: `CommonApiResponse(data = ...)`
- Error: `ExpectedException` → Global Handler
