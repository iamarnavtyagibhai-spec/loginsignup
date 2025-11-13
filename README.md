# Musify Backend API
## Music Streaming Platform with ML-Powered Recommendations

---

## 📋 Table of Contents

1. [Overview](#overview)
2. [Technology Stack](#technology-stack)
3. [Architecture](#architecture)
4. [Core Features](#core-features)
5. [API Endpoints](#api-endpoints)
6. [Database Schema](#database-schema)
7. [Security Implementation](#security-implementation)
8. [ML Integration](#ml-integration)
9. [File Storage](#file-storage)
10. [Error Handling](#error-handling)

---

## 🎯 Overview

*Musify* is a modern music streaming backend application built with Spring Boot that provides:
- User authentication and authorization
- Song management and streaming
- ML-powered music recommendations
- Play history tracking
- Wishlist functionality
- Cloud-based file storage

*Version:* 1.0.0  
*Java Version:* 17  
*Framework:* Spring Boot 3.5.7

---

## 🛠 Technology Stack

### Backend Framework
- *Spring Boot 3.5.7* - Main framework
- *Spring Security* - Authentication & Authorization
- *Spring Data MongoDB* - Database operations
- *Spring Web* - RESTful API

### Database
- *MongoDB* - NoSQL document database
- *MongoDB Atlas* - Cloud-hosted database

### Authentication
- *JWT (JSON Web Tokens)* - Token-based authentication
- *OAuth2 Client* - Google OAuth2 integration
- *Password Encryption* - BCrypt password hashing

### External Services
- *Cloudinary* - Cloud-based media storage (audio & images)
- *ML Recommendation Service* - External ML service for song recommendations

### Additional Libraries
- *Lombok* - Code generation
- *Validation* - Input validation
- *RestTemplate* - HTTP client for external APIs

---

## 🏗 Architecture

### Project Structure


com.example.Musify/
├── config/          # Configuration classes
│   ├── SecurityConfigv2.java
│   ├── CloudinaryConfig.java
│   ├── JwtProperties.java
│   └── WebConfig.java
├── controller/      # REST API endpoints
│   ├── AuthController.java
│   ├── SongController.java
│   ├── RecommendController.java
│   ├── PlayHistoryController.java
│   ├── WishlistController.java
│   └── UserController.java
├── service/         # Business logic
│   ├── MLRecommendService.java
│   ├── PlayHistoryService.java
│   ├── SongService.java
│   ├── WishlistService.java
│   └── UserService.java
├── repository/      # Data access layer
│   ├── UserRepository.java
│   ├── SongRepository.java
│   └── PlayHistoryRepository.java
├── model/           # Data models
│   ├── User.java
│   ├── Song.java
│   └── PlayHistory.java
├── security/        # Security components
│   └── JwtAuthenticationFilter.java
├── exception/       # Error handling
│   ├── RestExceptionHandler.java
│   └── GlobalErrorController.java
└── util/            # Utilities
    └── JwtUtil.java


### Request Flow


Client Request
    ↓
Security Filter (JWT/OAuth2)
    ↓
Controller Layer
    ↓
Service Layer
    ↓
Repository Layer
    ↓
MongoDB Database


---

## ✨ Core Features

### 1. *User Authentication*
- JWT-based authentication
- Google OAuth2 login
- Manual signup/login
- Token blacklisting for logout
- Password encryption

### 2. *Song Management*
- Upload songs with audio and images
- Cloudinary integration for media storage
- Song search functionality
- Category-based organization
- Get all songs with pagination

### 3. *Play History*
- Automatic play tracking
- Aggregated play counts per song
- Recent play history
- History clearing functionality
- Integration with recommendation system

### 4. *ML-Powered Recommendations*
- Next song recommendation
- Home page recommendations
- User event tracking
- Play history integration
- External ML service integration

### 5. *Wishlist*
- Add/remove songs from wishlist
- Get user's wishlist
- Persistent storage

### 6. *User Management*
- User profile management
- User information retrieval
- Email/username-based lookup

---

## 🔌 API Endpoints

### Authentication (/api/auth)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/login | User login | No |
| POST | /api/auth/logout | User logout | Yes |
| GET | /api/auth/me | Get current user | Yes |

*Login Request:*
json
{
  "email": "user@example.com",
  "password": "password123"
}


*Login Response:*
json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}


---

### Songs (/api/songs)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/songs/upload | Upload song | Yes |
| GET | /api/songs | Get all songs | No |
| GET | /api/songs?search=query | Search songs | No |
| POST | /api/songs/{songId}/play | Track play | Optional |

*Upload Song:*

POST /api/songs/upload
Content-Type: multipart/form-data

file: [audio file]
image: [image file]
title: "Song Title"
artist: "Artist Name"
category: "Hindi"


*Get Songs Response:*
json
[
  {
    "id": "691072e436d44df4c6303459",
    "title": "Song Title",
    "artist": "Artist Name",
    "audioUrl": "https://res.cloudinary.com/...",
    "imagePath": "https://res.cloudinary.com/...",
    "category": "Hindi"
  }
]


---

### Recommendations (/api/recommend)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/recommend/event | Track listening event | Optional |
| POST | /api/recommend/next | Get next song | Optional |
| POST | /api/recommend/home | Get home recommendations | Optional |

*Next Song Request:*
json
{
  "user_id": "USER123",
  "id": "691072e436d44df4c6303459",
  "history": [
    {"song_id": "691072e436d44df4c6303459", "plays": 8},
    {"song_id": "6910790036d44df4c630345e", "plays": 5}
  ]
}


*Next Song Response:*
json
{
  "next_song": {
    "id": "691388a04cd9556c7e810681",
    "title": "zara sa",
    "artist": "kk",
    "audioUrl": "https://res.cloudinary.com/...",
    "imagePath": "https://res.cloudinary.com/...",
    "category": "Hindi"
  }
}


*Home Recommendations Response:*
json
{
  "recommendations": [
    {
      "id": "...",
      "title": "...",
      "artist": "...",
      ...
    }
  ]
}


---

### Play History (/api/history)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/history/play/{songId} | Record play | Yes |
| GET | /api/history | Get detailed history | Yes |
| GET | /api/history/recent?limit=10 | Get recent plays | Yes |
| GET | /api/history/aggregated | Get aggregated history | Yes |
| DELETE | /api/history | Clear history | Yes |

*Aggregated History Response:*
json
{
  "history": [
    {"song_id": "691072e436d44df4c6303459", "plays": 8},
    {"song_id": "6910790036d44df4c630345e", "plays": 5},
    {"song_id": "69106302e0576f8ae71d506e", "plays": 3}
  ]
}


---

### Wishlist (/api/wishlist)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/wishlist | Get wishlist | Yes |
| POST | /api/wishlist/add/{songId} | Add to wishlist | Yes |
| DELETE | /api/wishlist/remove/{songId} | Remove from wishlist | Yes |

---

## 🗄 Database Schema

### User Collection

javascript
{
  "_id": "ObjectId",
  "name": "String",
  "email": "String (unique, indexed)",
  "username": "String (unique, indexed)",
  "password": "String (hashed)",
  "provider": "String (google/local)",
  "providerId": "String",
  "wishlist": ["songId1", "songId2", ...]
}


### Song Collection

javascript
{
  "_id": "ObjectId",
  "title": "String",
  "artist": "String",
  "audioUrl": "String (Cloudinary URL)",
  "imagePath": "String (Cloudinary URL)",
  "category": "String"
}


### Play History Collection

javascript
{
  "_id": "ObjectId",
  "userId": "String (indexed)",
  "songId": "String (indexed)",
  "playedAt": "LocalDateTime"
}


---

## 🔒 Security Implementation

### JWT Authentication

1. *Token Generation:*
   - User logs in with email/username and password
   - System generates JWT token with user email as subject
   - Token expires after 24 hours (86400000 ms)

2. *Token Validation:*
   - JwtAuthenticationFilter intercepts requests
   - Extracts token from Authorization: Bearer <token> header
   - Validates token signature and expiration
   - Sets authentication in security context

3. *Token Blacklisting:*
   - Logout endpoint blacklists tokens
   - Blacklisted tokens are rejected on subsequent requests
   - In-memory blacklist service

### OAuth2 Integration

- Google OAuth2 client configuration
- Automatic user creation on OAuth login
- JWT token generation after OAuth success

### Password Security

- BCrypt password hashing
- Password never returned in API responses
- Secure password validation

### CORS Configuration

- Configurable allowed origins
- Configurable HTTP methods
- Configurable headers

---

## 🤖 ML Integration

### ML Service Endpoints

The backend integrates with an external ML service at:
- Base URL: https://group-task.onrender.com

### Integration Points

1. *User Event Tracking:*
   - Endpoint: /ml/user_event
   - Tracks when users play songs
   - Also saves to local play history database

2. *Next Song Recommendation:*
   - Endpoint: /recommend/next
   - Sends: user_id, id (current song), history array
   - Returns: Next recommended song

3. *Home Recommendations:*
   - Endpoint: /recommend/home
   - Sends: user_id, history array
   - Returns: List of recommended songs

### Play History Integration

- Automatically fetches play history from database
- Merges with history from request body
- Sends aggregated history to ML service
- Format: [{song_id: "...", plays: 8}, ...]

### Error Handling

- Graceful fallback if ML service is unavailable
- Returns error message instead of crashing
- Logs errors for debugging

---

## ☁ File Storage

### Cloudinary Integration

- *Audio Files:* Stored as video resource type
- *Images:* Stored as image resource type
- *Automatic URL Generation:* Secure HTTPS URLs
- *Configuration:* Environment-based credentials

### File Upload Limits

- Max file size: 20MB
- Max request size: 50MB
- Multipart form data support

### Upload Flow


Client Upload
    ↓
Spring Multipart Handler
    ↓
Cloudinary Upload Service
    ↓
Store URLs in MongoDB
    ↓
Return Song Object


---

## ⚠ Error Handling

### Exception Handler

Global exception handler (RestExceptionHandler) handles:

1. *BadCredentialsException* → 401 Unauthorized
2. *RuntimeException* → 400 Bad Request
3. *IOException* → 400 Bad Request (with Cloudinary-specific messages)
4. *General Exception* → 500 Internal Server Error

### Error Response Format

json
{
  "error": "Error message description"
}


### Global Error Controller

Handles unhandled errors and returns JSON responses with:
- HTTP status code
- Error message
- Error details

---

## 📊 Key Features Summary

✅ *Authentication & Authorization*
- JWT token-based authentication
- Google OAuth2 integration
- Token blacklisting
- Secure password hashing

✅ *Song Management*
- Upload songs with media files
- Cloud-based storage (Cloudinary)
- Search functionality
- Category organization

✅ *Play History*
- Automatic tracking
- Aggregated statistics
- Recent plays
- Database persistence

✅ *ML Recommendations*
- Next song prediction
- Home page recommendations
- Play history integration
- External ML service integration

✅ *Wishlist*
- Add/remove songs
- User-specific lists
- Persistent storage

✅ *Security*
- JWT authentication
- OAuth2 support
- Password encryption
- CORS configuration
- Token blacklisting

---

## 🚀 Deployment

### Configuration

- *Port:* 8081 (configurable via PORT environment variable)
- *Database:* MongoDB Atlas (cloud-hosted)
- *File Storage:* Cloudinary (cloud-hosted)
- *ML Service:* External service integration

### Environment Variables

- PORT - Server port
- MongoDB connection string
- Cloudinary credentials
- JWT secret key
- OAuth2 client credentials

---

## 📝 API Usage Examples

### 1. User Login

bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'


### 2. Get All Songs

bash
curl -X GET http://localhost:8081/api/songs


### 3. Get Next Recommendation

bash
curl -X POST http://localhost:8081/api/recommend/next \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "user_id": "USER123",
    "id": "691072e436d44df4c6303459",
    "history": [
      {"song_id": "691072e436d44df4c6303459", "plays": 8}
    ]
  }'


### 4. Track Play

bash
curl -X POST http://localhost:8081/api/history/play/691072e436d44df4c6303459 \
  -H "Authorization: Bearer <token>"


---

## 🎯 Future Enhancements

- [ ] Playlist management
- [ ] User following system
- [ ] Song ratings and reviews
- [ ] Advanced search filters
- [ ] Analytics dashboard
- [ ] Real-time notifications
- [ ] Caching layer (Redis)
- [ ] Rate limiting
- [ ] API versioning

---

## 📞 Contact & Support

*Project:* Musify Backend API  
*Version:* 1.0.0  
*Framework:* Spring Boot 3.5.7  
*Java Version:* 17

---

Generated for Musify Backend API Documentation
