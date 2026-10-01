# ITFreeSource Academy — Edge API Architecture & Contract

This document specifies the decoupled micro-API contract connecting the **ITFS Academy Native Android Client** to the **Cloudflare Edge Gateway**.

---

## 1. Architectural Principles

1. **Zero Hardcoded Question Assets**:
   Questions are never compiled into the APK as static assets. The client fetches quiz items dynamically per lesson node over HTTPS.
2. **Section Decoupling**:
   Every section and lesson node is individually addressable. Modifying questions or adding units in one module does not impact any other course module.
3. **SHA-256 DRM Answer Verification**:
   The API provides questions with SHA-256 digested answers (`correctAnswerHash`). Plaintext answers are never exposed in JSON responses, preventing API packet interception attacks.
4. **Resilient Offline Fallback**:
   If network access is disrupted, the client utilizes its cached, device-bound encrypted vault (`EncryptedDataStore`) to continue seamless gameplay without crashes.

---

## 2. API Endpoints

### 2.1 Get All Courses
- **Endpoint**: `GET /api/v1/courses`
- **Response**: `200 OK`
```json
[
  {
    "id": "agentic-engineering",
    "title": "Agentic Engineering & AI Agents",
    "category": "AI_AGENTIC",
    "iconEmoji": "🤖",
    "description": "Master autonomous AI agents, multi-agent swarms, tool calling, and LLM reasoning loops.",
    "levelsCount": 8,
    "xpReward": 200,
    "isSubscribed": true,
    "progressPercent": 0.35
  }
]
```

### 2.2 Get Course Curriculum (Quest Path)
- **Endpoint**: `GET /api/v1/courses/{courseId}/curriculum`
- **Response**: `200 OK`
```json
[
  {
    "sectionId": "agentic-engineering_sec_1",
    "courseId": "agentic-engineering",
    "title": "Unit 1: Foundations & Core Concepts",
    "description": "Understand low-level architecture and execution lifecycle.",
    "order": 1,
    "lessons": [
      {
        "id": "agentic-engineering_l1",
        "sectionId": "agentic-engineering_sec_1",
        "title": "Architecture Principles",
        "order": 1,
        "nodeType": "STANDARD_QUEST",
        "status": "COMPLETED",
        "stars": 3,
        "xpValue": 25,
        "estMinutes": 5
      }
    ]
  }
]
```

### 2.3 Get Lesson Questions
- **Endpoint**: `GET /api/v1/lessons/{lessonId}/questions`
- **Response**: `200 OK`
```json
[
  {
    "id": "q_py_1",
    "lessonId": "python_l1",
    "prompt": "Which standard data type in Python is strictly IMMUTABLE once instantiated?",
    "type": "MULTIPLE_CHOICE",
    "options": ["List", "Dictionary", "Tuple", "Set"],
    "correctAnswerHash": "fb2c3dcbe52aa287c413879a8d1b4564c5058391ae4d64f361b5b0caae9397ca",
    "explanationHint": "Tuples maintain fixed contiguous memory allocations and cannot be resized in-place."
  }
]
```

### 2.4 Verify Answer (Edge Scoring)
- **Endpoint**: `POST /api/v1/lessons/{lessonId}/verify`
- **Payload**:
```json
{
  "questionId": "q_py_1",
  "answer": "Tuple"
}
```
- **Response**: `200 OK`
```json
{
  "isCorrect": true,
  "answerDigest": "fb2c3dcbe52aa287c413879a8d1b4564c5058391ae4d64f361b5b0caae9397ca",
  "xpEarned": 15,
  "gemsEarned": 3
}
```
