# BiniyogBuddy

## Software Requirements Specification

*Version 1.0 — Free MVP*

**Status:** Draft — **Target Launch:** 2–3 Months  
**Market:** Bangladesh (DSE / CSE) — **Target User:** Beginner Investors

| **Document Title**           | Software Requirements Specification |
|------------------------------|-------------------------------------|
| **Project**                  | BiniyogBuddy                        |
| **Version**                  | 1.0 — MVP                           |
| **Date**                     | March 2026                          |
| **Author**                   | BiniyogBuddy Team                   |
| **Classification**           | Confidential — Internal Use Only    |

*"Invest in learning before money."* — বিনিয়োগবাডি

---

## Table of Contents

1. [Introduction & Purpose](#1-introduction--purpose)
2. [Overall Description](#2-overall-description)
3. [Users & Accounts](#3-users--accounts)
4. [Stock Journal](#4-stock-journal)
5. [Trade Logs](#5-trade-logs)
6. [Portfolio View](#6-portfolio-view)
7. [Trade Notes](#7-trade-notes)
8. [UI & UX Requirements](#8-ui--ux-requirements)
9. [Non-Functional Requirements](#9-non-functional-requirements)
10. [Out of Scope — MVP v1](#10-out-of-scope--mvp-v1)
11. [Assumptions & Dependencies](#11-assumptions--dependencies)
12. [Appendix — Database Schema](#12-appendix--database-schema)
13. [API Endpoints](#13-api-endpoints)

---

## 1. Introduction & Purpose

### 1.1 Purpose of This Document
This Software Requirements Specification (SRS) defines the functional and non-functional requirements for BiniyogBuddy version 1.0 — a free, web-based stock learning journal for beginner investors in Bangladesh. It is intended for use by the development team, AI-assisted coding tools, and any stakeholders involved in the first release.

### 1.2 Project Goal
BiniyogBuddy gives beginner investors in Bangladesh a simple, low-pressure space to track and reflect on their stock trades — whether real or paper (simulation). The goal is to build investing discipline through journaling, not to replace a brokerage or provide financial advice.

### 1.3 Problem Statement
New investors in Bangladesh's DSE/CSE market frequently make impulsive trades without keeping records. They lose money and fail to learn from mistakes because there is no structured way to journal their thinking. BiniyogBuddy solves this with a minimal, focused tool.

### 1.4 Scope
This SRS covers the MVP (Minimum Viable Product) — version 1.0 only. All features listed here are targeted for the initial free release. Features outside this scope are listed in [Section 10](#10-out-of-scope--mvp-v1).

### 1.5 Definitions & Acronyms

| **Term**         | **Definition** |
|------------------|----------------|
| **SRS**          | Software Requirements Specification — this document |
| **MVP**          | Minimum Viable Product — the smallest working version to launch |
| **DSE / CSE**    | Dhaka Stock Exchange / Chittagong Stock Exchange — the two stock markets in Bangladesh |
| **Paper Trade**  | A simulated trade — no real money involved, for learning purposes only |
| **BDT / Tk**     | Bangladeshi Taka — the local currency used for all monetary fields |
| **JWT**          | JSON Web Token — used for authentication in the Spring Boot backend |
| **MUST**         | Required for MVP launch — no exceptions |
| **SHOULD**       | Highly desirable but can be deferred to a patch release if needed |
| **WON'T (MVP)**  | Intentionally excluded from version 1.0 |

---

## 2. Overall Description

### 2.1 Product Perspective
BiniyogBuddy is a standalone, browser-based web application. It does not integrate with any external financial data provider or brokerage in this version. All data is entered manually by the user.

### 2.2 Tech Stack

| **Layer**       | **Technology**               | **Role** |
|-----------------|------------------------------|----------|
| **Frontend**    | Next.js 14                   | Page routing, server components, SSR |
| **Styling**     | Tailwind CSS                 | Responsive utility-first CSS |
| **Backend**     | Spring Boot                  | REST API, business logic, authentication (Spring Security + JWT) |
| **Database**    | PostgreSQL                   | Stores all user, stock, trade, notes data (accessed via Spring Data JPA) |
| **Hosting**     | Vercel (frontend) + Backend on cloud platform (e.g., Railway, Heroku, AWS) | Auto-deploy from GitHub |
| **Container**   | Docker                       | Portable local + production builds |

### 2.3 What This Product Is NOT
The following are explicitly outside the intent of this product:
- A real-time stock price tracker or market data feed
- A brokerage, trading platform, or financial transaction system
- A financial advisor or investment recommendation engine
- A social platform, community tool, or content-sharing service
- A native mobile application (iOS / Android)

### 2.4 User Classes
For MVP, there is a single user type:

| **User Type**    | **Description** |
|------------------|-----------------|
| **Beginner Investor** | A registered, logged-in user who tracks their own stock trades. Experience level is fixed at 'Beginner' in v1.0. No admin role or multi-role system is included. |

---

## 3. Users & Accounts

### 3.1 Authentication Requirements

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| A-01 | User can sign up with email and password | **MUST**     | Password min 6 characters; email uniqueness enforced |
| A-02 | User can log in with email and password  | **MUST**     | Returns a JWT; show clear error on wrong credentials |
| A-03 | User can log out                         | **MUST**     | Frontend discards JWT; no server-side session |
| A-04 | Password reset via email link            | **SHOULD**   | Spring Boot sends reset email (e.g., via SendGrid) |
| A-05 | Protected routes redirect unauthenticated users to /login | **MUST** | Frontend checks JWT presence/validity; backend validates JWT on each API call |

### 3.2 User Profile

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| P-01 | User can set and update their display name | **MUST**   | Text field, no character limit enforced |
| P-02 | Experience level displayed as 'Beginner' | **MUST**     | Fixed value in v1 — not editable |
| P-03 | Profile photo upload                     | **WON'T**    | Skipped for MVP |

---

## 4. Stock Journal

The Stock Journal is the core list of stocks a user is watching or has traded. It acts as the user's personal stock database.

### 4.1 Add a Stock

| **Field**       | **Type**        | **Description**                        | **Required** |
|-----------------|-----------------|----------------------------------------|--------------|
| **Stock Name**  | Text            | Full name e.g. 'Square Pharma'         | Yes |
| **DSE / CSE Code** | Text        | Ticker code e.g. 'SQURPHARMA'          | Yes |
| **Sector**      | Dropdown/Text   | e.g. Pharma, Bank, Textile, IT         | Yes |
| **Purchase Price** | Number (BDT) | Manual entry — no live price feed      | Yes |
| **Quantity**    | Integer         | Number of shares                       | Yes |
| **Trade Type**  | Toggle          | 'Learning Only' (paper) or 'Real Trade'| Yes |

### 4.2 Stock List & Management

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| J-01 | Display all added stocks in a card or list view | **MUST** | Show name, code, sector, trade type badge |
| J-02 | User can edit an existing stock entry    | **MUST**     | All fields editable |
| J-03 | User can delete a stock entry            | **MUST**     | Warn user if trade logs exist |
| J-04 | Filter stocks by trade type (Learning vs Real) | **SHOULD** | Toggle filter on list view |
| J-05 | Search stocks by name or code            | **SHOULD**   | Client-side filter, no server call needed |

---

## 5. Trade Logs

For each stock in the journal, the user can log individual buy or sell transactions. This forms the historical trade record.

### 5.1 Log a Trade Entry

| **Field**       | **Type**     | **Description**                        | **Required** |
|-----------------|--------------|----------------------------------------|--------------|
| **Stock**       | Linked       | Select from stocks in the journal      | Yes |
| **Action**      | Toggle       | 'Buy' or 'Sell'                        | Yes |
| **Date**        | Date picker  | When the trade occurred                | Yes |
| **Price / Share**| Number (BDT)| Price per share at time of trade       | Yes |
| **Quantity**    | Integer      | Number of shares traded                | Yes |
| **Total Value** | Computed     | Price x Quantity — auto-calculated and displayed on form | Auto |

### 5.2 Trade List View

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| T-01 | Show all trade entries in reverse chronological order (newest first) | **MUST** | Default view on /trades page |
| T-02 | Each row shows: date, stock name, Buy/Sell badge, price, qty, total | **MUST** | |
| T-03 | User can delete a trade log entry        | **MUST**     | Also deletes linked notes row |
| T-04 | Filter trade list by stock name          | **SHOULD**   | Dropdown or text filter |
| T-05 | User can edit an existing trade log entry| **SHOULD**   | All fields editable |

---

## 6. Basic Portfolio View

The Portfolio dashboard shows a summary of the user's holdings — all calculated from manually entered trade logs. There is no live market data in v1.0.

### 6.1 Portfolio Summary Requirements

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| PF-01 | Display total invested amount (BDT) — sum of all BUY trades | **MUST** | Label in BDT with Tk / ৳ symbol |
| PF-02 | Display current holdings list — stocks with quantity > 0 | **MUST** | Each row: stock name, quantity held, avg buy price |
| PF-03 | Do NOT show profit/loss, % return or current market value | **WON'T** | Out of scope for MVP |
| PF-04 | Do NOT integrate a live DSE/CSE price feed | **WON'T** | All prices are manual |

### 6.2 Calculation Logic

| **Metric**             | **Formula** |
|------------------------|-------------|
| **Total Invested**     | SUM of (price x quantity) for all BUY entries across all stocks |
| **Current Quantity**   | Total BUY quantity minus total SELL quantity per individual stock |
| **Average Buy Price**  | Total BUY cost / Total BUY quantity, calculated per stock |
| **Active Holdings**    | Only stocks where Current Quantity is greater than 0 are shown |

---

## 7. Trade Notes (Reflection)

Trade Notes are the most important learning feature of BiniyogBuddy. After each trade, the user can record their reasoning and reflections. This is what separates BiniyogBuddy from a plain trade tracker.

### 7.1 Notes Requirements

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| N-01 | Each trade log entry has a 'Why did I buy/sell this?' open text field | **MUST** | Multi-line textarea |
| N-02 | Each trade log entry has a 'What did I learn from this?' open text field | **MUST** | Multi-line textarea |
| N-03 | Notes can be added at the time of logging the trade | **MUST** | Fields appear on the trade log form |
| N-04 | Notes can be edited at any time after the trade is logged | **MUST** | Editable from the trade detail view |
| N-05 | Notes are optional — trade can be saved without filling them in | **MUST** | No validation block on empty notes |

---

## 8. UI & UX Requirements

### 8.1 Design Principles

| #    | **Requirement**                         | **Priority** | **Notes** |
|------|------------------------------------------|--------------|-----------|
| UI-01 | Clean, minimal design — generous white space, no visual clutter | **MUST** | |
| UI-02 | Mobile-responsive — all pages work on 320px+ screen widths | **MUST** | Android Chrome is the priority browser |
| UI-03 | Fast loading — pages target under 3 seconds on mobile connections | **MUST** | Keep JS bundle light |
| UI-04 | Navigation has at most 4 items — no complex menus | **MUST** | Sidebar or bottom nav |
| UI-05 | Optional Bangla sub-labels or tooltips for key financial terms | **SHOULD** | UI language stays English; Bangla is supplementary |
| UI-06 | Full Bangla language toggle             | **WON'T**   | Deferred post-MVP |

### 8.2 Navigation Structure

| **Page**         | **Route**      | **Purpose** |
|------------------|----------------|-------------|
| Dashboard / Portfolio | /dashboard | Holdings summary — total invested, current stocks |
| Stock Journal    | /journal       | Add, view, edit, delete stocks |
| Trade Logs       | /trades        | Log and list trade entries with notes |
| Profile          | /profile       | Display name and experience level |
| Login            | /login         | Email + password login form |
| Sign Up          | /signup        | New account registration |

---

## 9. Non-Functional Requirements

| **Category**        | **Metric / Target** | **Detail** |
|---------------------|---------------------|------------|
| **Performance**     | Page load < 3 seconds | On standard 4G mobile connection. Measured on Android Chrome. |
| **Security**        | Passwords hashed (bcrypt) | Handled by Spring Security. JWTs are signed and validated. |
| **Reliability**     | 99% uptime target    | Achieved via managed Vercel (frontend) and backend hosting (e.g., Railway). |
| **Scalability**     | 100–500 concurrent users | No architecture changes needed at this scale. Backend can be scaled horizontally. |
| **Data Retention**  | Retained until deletion | User data is kept unless the user explicitly requests deletion. No automated purge. |
| **Browser Support** | Latest Chrome, Firefox, Safari | Android Chrome is the primary target. iOS Safari secondary. |
| **Accessibility**   | Basic WCAG AA compliance | Readable font sizes, sufficient colour contrast, form labels on all inputs. |
| **Localisation**    | English UI + Bangla tooltips | Full Bangla language is out of scope for MVP. |

---

## 10. Out of Scope — MVP v1

The following features are intentionally excluded from version 1.0. They may be considered for future releases.

| **Feature**                         | **Reason for Deferral** |
|-------------------------------------|--------------------------|
| Live stock price feed (DSE/CSE API) | Requires paid API and adds significant complexity |
| Profit / Loss calculations          | Needs live prices to be meaningful; deferred with live feed |
| Charts and analytics dashboards     | Post-MVP feature once data volume justifies it |
| Social features — sharing, leaderboards | Community features are a v2+ consideration |
| Push notifications and price alerts | Requires live data and notification service |
| Multiple portfolio support          | Single portfolio is sufficient for beginner users |
| Brokerage account sync              | Integration complexity far exceeds MVP scope |
| Full Bangla language toggle         | Tooltip-level Bangla is sufficient for MVP |
| Native mobile app (iOS / Android)   | Web app is sufficient; native adds build/review cost |
| Admin dashboard                     | No admin role in MVP; single user type only |
| Subscription or payment system      | MVP is fully free; monetisation is post-MVP |

---

## 11. Assumptions & Dependencies

### 11.1 Assumptions
- All stock price data is entered manually by the user — no automated price fetching.
- Users have access to a modern smartphone or desktop browser with internet.
- The primary user demographic has basic English literacy for navigating the UI.
- The backend (Spring Boot) and database (PostgreSQL) can be hosted on affordable cloud platforms suitable for MVP scale.
- Vercel free tier deployment is acceptable for the initial launch period.
- The project is built primarily using AI-assisted coding tools alongside the developer.

### 11.2 External Dependencies

| **Service**       | **Usage**          | **Risk if Unavailable** |
|-------------------|--------------------|--------------------------|
| **Backend Host** (e.g., Railway, Heroku) | Hosts Spring Boot API | App is non-functional — no API access |
| **Vercel**        | Hosting + CDN for frontend | App is unreachable — fallback to another host needed |
| **GitHub**        | Source control + CI/CD | Manual deployment required |
| **Node.js / npm** | Frontend runtime + package manager | Build process fails |

---

## 12. Appendix — Database Schema

All tables are created in PostgreSQL. Spring Security ensures that each user can only access their own data via user ID checks in service/controller layers.

### Table: `users`

| **Column**         | **Type**     | **Description** |
|--------------------|--------------|-----------------|
| **id**             | uuid (PK)    | Primary key, generated automatically |
| **email**          | text         | User email address — unique, used for login |
| **password_hash**  | text         | Bcrypt-hashed password |
| **name**           | text         | Display name shown on the profile page |
| **experience_level**| text        | Fixed value: 'Beginner' in MVP — not user-editable |
| **created_at**     | timestamp    | Account creation timestamp |

### Table: `stocks`

| **Column**         | **Type**     | **Description** |
|--------------------|--------------|-----------------|
| **id**             | uuid (PK)    | Primary key |
| **user_id**        | uuid (FK)    | References `users.id` |
| **name**           | text         | Full stock name e.g. 'Square Pharma' |
| **code**           | text         | DSE / CSE ticker code e.g. 'SQURPHARMA' |
| **sector**         | text         | Sector category e.g. Pharma, Bank, Textile |
| **purchase_price** | numeric      | Initial purchase price per share in BDT |
| **quantity**       | integer      | Number of shares added to the journal |
| **trade_type**     | text         | 'Learning Only' or 'Real Trade' |

### Table: `trade_logs`

| **Column**         | **Type**     | **Description** |
|--------------------|--------------|-----------------|
| **id**             | uuid (PK)    | Primary key |
| **user_id**        | uuid (FK)    | References `users.id` |
| **stock_id**       | uuid (FK)    | References `stocks.id` |
| **action**         | text         | 'Buy' or 'Sell' |
| **date**           | date         | The date the trade occurred |
| **price**          | numeric      | Price per share at the time of this trade |
| **quantity**       | integer      | Number of shares in this transaction |

### Table: `notes`

| **Column**         | **Type**     | **Description** |
|--------------------|--------------|-----------------|
| **id**             | uuid (PK)    | Primary key |
| **trade_log_id**   | uuid (FK)    | References `trade_logs.id` (one-to-one) |
| **why_bought**     | text         | User's answer to 'Why did I buy/sell this?' — can be empty |
| **what_learned**   | text         | User's answer to 'What did I learn?' — can be empty |
| **updated_at**     | timestamp    | Last time the notes were edited — updated on every save |

---

## 13. API Endpoints

BiniyogBuddy uses a Spring Boot backend that exposes a RESTful API. The frontend (Next.js) calls these endpoints server-side or client-side with the user's JWT in the `Authorization` header. All endpoints are prefixed with `/api`.

| Property | Value |
|----------|-------|
| **Base URL** | `https://api.biniyogbuddy.example.com` (or `/api` when proxied via Next.js) |
| **Auth Header** | `Authorization: Bearer <jwt_token>` |
| **Content-Type** | `application/json` |

---

### 13.0 HTTP Method Key

| Method | Meaning |
|--------|---------|
| `GET`  | Read / fetch data |
| `POST` | Create a new resource |
| `PUT`  | Replace an existing resource entirely |
| `PATCH`| Update specific fields on an existing resource |
| `DELETE` | Remove a resource |

---

### 13.1 Authentication

| #   | Method | Endpoint               | Request Body                          | Response                              |
|-----|--------|------------------------|---------------------------------------|---------------------------------------|
| AU-1| `POST` | `/api/auth/signup`     | `{ email, password, name }`           | `201 Created` with user object (no token; user must log in) |
| AU-2| `POST` | `/api/auth/login`      | `{ email, password }`                  | `200 OK` with `{ token, user }`       |
| AU-3| `POST` | `/api/auth/logout`     | (no body, token in header)            | `200 OK` (client discards token)      |
| AU-4| `POST` | `/api/auth/reset-password` | `{ email }`                        | `200 OK` if email exists (reset email sent) |

**Note:** All subsequent endpoints require the JWT token in the `Authorization` header.

---

### 13.2 User Profile

| #   | Method | Endpoint           | Request Body          | Response                              |
|-----|--------|--------------------|-----------------------|---------------------------------------|
| UP-1| `GET`  | `/api/users/me`    | (none)                | `200 OK` with user profile object     |
| UP-2| `PATCH`| `/api/users/me`    | `{ name }`            | `200 OK` with updated user object     |

---

### 13.3 Stock Journal

| #   | Method | Endpoint               | Request Body (if any)                              | Response                              |
|-----|--------|------------------------|----------------------------------------------------|---------------------------------------|
| ST-1| `GET`  | `/api/stocks`          | (none)                                             | `200 OK` with array of stocks         |
| ST-2| `GET`  | `/api/stocks/{id}`     | (none)                                             | `200 OK` with single stock object     |
| ST-3| `POST` | `/api/stocks`          | `{ name, code, sector, purchasePrice, quantity, tradeType }` | `201 Created` with created stock      |
| ST-4| `PATCH`| `/api/stocks/{id}`     | `{ any fields to update }`                          | `200 OK` with updated stock            |
| ST-5| `DELETE`| `/api/stocks/{id}`    | (none)                                             | `204 No Content` (cascades to related trade logs & notes) |

---

### 13.4 Trade Logs

| #   | Method | Endpoint                         | Request Body (if any)                              | Response                              |
|-----|--------|----------------------------------|----------------------------------------------------|---------------------------------------|
| TL-1| `GET`  | `/api/trades`                    | (none)                                             | `200 OK` with array of trades, newest first |
| TL-2| `GET`  | `/api/trades?stockId={id}`       | (none)                                             | `200 OK` with trades for that stock   |
| TL-3| `POST` | `/api/trades`                    | `{ stockId, action, date, price, quantity }`      | `201 Created` with created trade      |
| TL-4| `PATCH`| `/api/trades/{id}`               | `{ any fields to update }`                          | `200 OK` with updated trade            |
| TL-5| `DELETE`| `/api/trades/{id}`              | (none)                                             | `204 No Content` (cascades to notes)  |

---

### 13.5 Trade Notes

| #   | Method | Endpoint                         | Request Body (if any)                              | Response                              |
|-----|--------|----------------------------------|----------------------------------------------------|---------------------------------------|
| NT-1| `GET`  | `/api/notes?tradeId={id}`        | (none)                                             | `200 OK` with notes object (or empty) |
| NT-2| `POST` | `/api/notes`                     | `{ tradeId, whyBought, whatLearned }`              | `201 Created` with created/updated notes (upsert) |
| NT-3| `PUT`  | `/api/notes/{id}`                | `{ whyBought, whatLearned }`                        | `200 OK` with updated notes            |
| NT-4| `DELETE`| `/api/notes/{id}`               | (none)                                             | `204 No Content`                      |

---

### 13.6 Common HTTP Error Codes

| Code | Status            | Most Likely Cause |
|------|-------------------|-------------------|
| 400  | Bad Request       | Missing required field or wrong data type in request body |
| 401  | Unauthorized      | Missing or invalid JWT — user must log in again |
| 403  | Forbidden         | User tried to access another user's data (user ID mismatch) |
| 404  | Not Found         | Resource does not exist or was already deleted |
| 409  | Conflict          | Duplicate entry — e.g., email already registered |
| 422  | Unprocessable     | Validation failed — e.g., invalid email format, password too short |
| 500  | Server Error      | Backend exception — check logs |

---

*BiniyogBuddy SRS v1.0 — MVP — For internal planning use only*