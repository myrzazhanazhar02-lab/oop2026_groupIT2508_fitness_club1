# Fitness Club (Assignment 3 Milestone)

A minimal console project for the topic ‘Fitness Club Membership & Class Booking’.
The following requirements are covered: JDBC via the Database interface, Supabase/Postgres implementation, 4 entities, database tables, repositories (interfaces + JDBC implementations), services (MembershipService, BookingService, NotificationService), basic use cases, and exceptions (membership expired, class full, booking already exists, invalid input / not found)
## Tech stack
- Java 17
- Maven
- PostgreSQL (Supabase, sslmode=require)

## Database connection
By default, test credentials from Supabase are hardcoded:
```
URL: jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require
USER: postgres.wwlkpayichwwogvjsaqb
PASSWORD: Myrzazhan_12
```
They can be overridden via the environment variables DB_URL, DB_USER, and DB_PASSWORD.
## Run
```bash
cd /Users/kaspi/Documents/Azhar
mvn clean package -DskipTests
java -jar target/fitness-club-1.0-SNAPSHOT-jar-with-dependencies.jar
```
On startup, tables and default data (membership_types, classes) are created automatically.
## Main CLI scenarios
- List membership types
- Create member
- List members
- Buy membership / Extend membership
- List classes
- Book class
- View member bookings (attendance history)

Exceptions are displayed in the console: membership expired, class full, booking already exists, invalid input, not found.

## Structure
-`com.fitnessclub.db` — `Database` interface, `SupabaseDatabase`, singleton `GymConfig`, `SchemaInitializer`.

-`model` — `Member`, `MembershipType`, `FitnessClass`, `ClassBooking`.

-`repository` — interfaces + JDBC implementations (`repository.jdbc.*`).

-`service` — `MembershipService`, `BookingService`, `NotificationService` (+ `ConsoleNotificationService`).

-`Main` — simple menu to demonstrate user flows.


## SQL for mock data (via terminal)
Install `psql` and execute:

```bash
psql "postgresql://postgres.wwlkpayichwwogvjsaqb:Myrzazhan_12@aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require" <<'SQL'
INSERT INTO membership_types(name, duration_days, price, visit_limit) VALUES
  ('Student-Month', 30, 70.00, NULL),
  ('PT-5-Pack', 45, 200.00, 5)
ON CONFLICT (name) DO NOTHING;

INSERT INTO classes(name, capacity, start_time) VALUES
  ('Pilates', 10, NOW() + INTERVAL '3 days'),
  ('Boxing', 12, NOW() + INTERVAL '4 days')
ON CONFLICT DO NOTHING;
SQL
```

## What to show during the presentation

* How the `Database` interface is substituted with the `SupabaseDatabase` implementation.
* Where tables are created (`SchemaInitializer`) and how entities/repositories are structured.
* Exception handling in services (expired/full/already exists/invalid input).
* SOLID principles: services use repositories via interfaces; `NotificationService` is separated.

## Project done by:
## Myrzazhan Azhar Bekmuratkyzy
## Abdukhairov Tair
