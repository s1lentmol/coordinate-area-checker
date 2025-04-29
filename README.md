# Coordinate Area Checker

An adaptive web application that allows authenticated users to interactively verify whether a given point on a 2D plane falls within a predefined area. All user interactions and results are persisted in a PostgreSQL database.

## Key Features

- **User Authentication**
  Secure login and registration using JSON Web Tokens (JWT) managed by Spring Security.

- **Interactive Canvas**
  Clickable 2D plot where each click sends the point coordinates to the server and displays the result in real time:

  - **Green dot**: point lies within the target area
  - **Red dot**: point lies outside the target area

- **Responsive Design**
  Front-end layout adapts seamlessly across desktop and mobile devices.

- **Result Persistence**
  Every point check (coordinates, timestamp, and result) is stored in PostgreSQL for audit and analysis.

## Architecture Overview

1. **Client (Vue.js + Axios)**

   - **Login Page**: collects username/password and obtains a JWT on success.
   - **Main Page**: renders an HTML5 `<canvas>` element; user clicks are captured, converted to coordinate data, and sent via Axios to the back end.

2. **Server (Spring Boot)**
   - **Authentication Module**:
     - Spring Security with JWT filters
   - **Business Logic**:
     - Receives point coordinates, applies area-inclusion algorithm, and returns success/error.
   - **Data Access**:
     - Spring Data JPA repositories for `User` and `CheckResult` entities.
   - **Database**:
     - PostgreSQL holds user credentials and history of all point checks.

## Technology Stack

| Layer     | Technology                                                   |
| --------- | ------------------------------------------------------------ |
| Back-end  | Java • Spring Boot • Spring Security (JWT) • Spring Data JPA |
| Database  | PostgreSQL                                                   |
| Front-end | Vue.js • Axios • HTML5 Canvas • CSS                          |

## How It Works

1. **Authentication**

   - User submits credentials on the login page.
   - Server validates and issues a JWT token.

2. **Point Checking**

   - On the main page, user clicks on the canvas.
   - Front-end converts click position to real-world coordinates and sends a POST request with JWT in headers.
   - Back-end endpoint validates the token, computes whether the point lies inside the predefined geometric area, and persists the check result.
   - Server responds with a boolean flag; the front-end plots a green or red dot accordingly.

3. **Result Storage**
   - Each check record includes: user ID, X/Y coordinates, timestamp, and inclusion result.
   - Users can review their past checks (if such a feature is enabled or extended).
