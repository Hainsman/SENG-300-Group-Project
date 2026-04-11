# Login Page Abstraction – High-Level Design

## Purpose

This document defines the high-level architecture and abstraction for the **Login** UI.  
The Login Page is the first page shown for the program. It prompts the player for their credentials and redirects them to the Home Page after a successful authentication, while remaining independent of any game-specific logic or backend computation.

---

## Design Goals


- Prompt a player their Username and Password
- Redirect to Home Page after successful login
- Gives player feedback (error) if login is unsuccessful
- Offer the option to create a new Player
- Remain fully decoupled from game logic and rules
- Follow object-oriented design principles suitable for Java

---

## High-Level Concept

The Login Page is a **UI-level abstraction** focused purely on the inputting of credentials (username and password) and the feedback from the authentication attempt.  
It does **not** validate credentials or query the database directly. Instead, it collects inputs and responds to authentication either with a redirection to the Home Page or an error message.

---

## Core Abstraction: Login

The `Login` abstraction represents the login view before the player is authenticated.

### Responsibilities

- Initialize login UI on client load
- Display username and password input boxes
- Handle login submission and new user creation actions 
- Give user authentication success or error message
- Redirect player to Home Page if login is successful
- Send incremental UI updates to the client

---

## Conceptual Interface (Java-oriented)

```text
Login
 ├── initialize(LoginViewData data)
 ├── handleLoginAction(LoginAction action)
 ├── getLoginState() LoginState
 └── shutdown()
```

---

## Login State

The login page maintains lightweight state describing what is currently visible to the user.

### LoginState (Conceptual)

- Username input value
- Password input value
- Authentication success or error message
- Login result status (SUCCESS, FAILURE, PENDING)

---

## Login Actions

All user interactions with the login page are represented as `LoginAction` objects.

### LoginAction (Conceptual)

- Player ID (nothing until after authentication success)
- Action type (e.g., `SUBMIT`)
- Credential payload (username, password)

These actions represent **UI intent only** and do not directly do the authentication.

---

## UI Features

The Login Page UI exposes the following core features:

- **Submit**
    - Takes inputs for username and password and submits them for authentication
- **Feedback**
    - Show success or error message after authentication attempt
- **CreateNewPlayer**
    - Navigates to a registration UI to create a new player

---

## Login Data Abstraction

All data rendered by the Login Page is provided in a display-ready format.

### LoginViewData (Conceptual)

- Feedback message (nothing when first displayed)
- Login status 
- Enabled navigation options (CreateNewPlayer)

The Login Page does not transform or compute this data.

---

## Interaction Flow (High-Level)

1. User launches the App and it opens with the Login Page
2. Login page initializes with empty LoginViewData
3. Player enters their credentials (username and password)
4. Player submits credentials with SUBMIT action, and Login sends credentials to be authenticated
5. After successful authentication Login Page redirects player to HomePage
6. After failed authentication Login Page updates feedback message and remains active

---

## Non-Goals

- No authentication logic
- No matchmaking or session management
- No database querying
- No game specific logic

---

## Summary

By isolating the Login Page as a pure UI abstraction, the platform provides a secure entry point for players which remains separate from authentication.  
This design supports easy UI iteration and extension while maintaining strict separation from authentication and backend systems.
