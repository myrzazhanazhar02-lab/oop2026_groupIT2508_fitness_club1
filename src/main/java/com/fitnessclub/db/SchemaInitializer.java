package com.fitnessclub.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {
    private final Database database;

    public SchemaInitializer(Database database) {
        this.database = database;
    }

    public void ensureSchema() {
        try (Connection connection = database.getConnection(); Statement st = connection.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS membership_types (
                        id SERIAL PRIMARY KEY,
                        name TEXT UNIQUE NOT NULL,
                        duration_days INTEGER NOT NULL,
                        price NUMERIC(10,2) NOT NULL,
                        visit_limit INTEGER
                    );
                    """);
            // In case table existed without the column, ensure it is present.
            st.execute("ALTER TABLE membership_types ADD COLUMN IF NOT EXISTS visit_limit INTEGER;");
            st.execute("ALTER TABLE membership_types ADD COLUMN IF NOT EXISTS duration_days INTEGER;");
            st.execute("ALTER TABLE membership_types ADD COLUMN IF NOT EXISTS price NUMERIC(10,2);");
            st.execute("ALTER TABLE membership_types ALTER COLUMN price TYPE NUMERIC(10,2) USING price::NUMERIC;");
            st.execute("ALTER TABLE membership_types ALTER COLUMN duration_days TYPE INTEGER USING duration_days::INTEGER;");
            st.execute("ALTER TABLE membership_types ALTER COLUMN visit_limit TYPE INTEGER USING visit_limit::INTEGER;");

            st.execute("""
                    CREATE TABLE IF NOT EXISTS members (
                        id SERIAL PRIMARY KEY,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        phone TEXT,
                        membership_type_id INTEGER REFERENCES membership_types(id),
                        membership_end_date DATE,
                        created_at TIMESTAMPTZ DEFAULT NOW()
                    );
                    """);
            st.execute("ALTER TABLE members ADD COLUMN IF NOT EXISTS phone TEXT;");
            st.execute("ALTER TABLE members ADD COLUMN IF NOT EXISTS membership_type_id INTEGER;");
            st.execute("ALTER TABLE members ADD COLUMN IF NOT EXISTS membership_end_date DATE;");
            st.execute("ALTER TABLE members ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;");
            st.execute("ALTER TABLE members ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at::TIMESTAMPTZ;");
            st.execute("ALTER TABLE members ALTER COLUMN created_at SET DEFAULT NOW();");
            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_members_email ON members(email);");

            st.execute("""
                    CREATE TABLE IF NOT EXISTS classes (
                        id SERIAL PRIMARY KEY,
                        name TEXT NOT NULL,
                        capacity INTEGER NOT NULL,
                        start_time TIMESTAMPTZ NOT NULL
                    );
                    """);
            st.execute("ALTER TABLE classes ADD COLUMN IF NOT EXISTS capacity INTEGER;");
            st.execute("ALTER TABLE classes ADD COLUMN IF NOT EXISTS start_time TIMESTAMPTZ;");
            st.execute("ALTER TABLE classes ALTER COLUMN start_time TYPE TIMESTAMPTZ USING start_time::TIMESTAMPTZ;");

            st.execute("""
                    CREATE TABLE IF NOT EXISTS class_bookings (
                        id SERIAL PRIMARY KEY,
                        member_id INTEGER NOT NULL REFERENCES members(id) ON DELETE CASCADE,
                        class_id INTEGER NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
                        booked_at TIMESTAMPTZ DEFAULT NOW(),
                        UNIQUE(member_id, class_id)
                    );
                    """);
            st.execute("ALTER TABLE class_bookings ADD COLUMN IF NOT EXISTS booked_at TIMESTAMPTZ;");
            st.execute("ALTER TABLE class_bookings ALTER COLUMN booked_at TYPE TIMESTAMPTZ USING booked_at::TIMESTAMPTZ;");
            st.execute("ALTER TABLE class_bookings ALTER COLUMN booked_at SET DEFAULT NOW();");
            st.execute("CREATE UNIQUE INDEX IF NOT EXISTS uq_booking_member_class ON class_bookings(member_id, class_id);");
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to initialize schema", e);
        }
    }

    public void seedDefaults() {
        try (Connection connection = database.getConnection(); Statement st = connection.createStatement()) {
            st.execute("""
                    INSERT INTO membership_types(name, duration_days, price, visit_limit)
                    VALUES 
                        ('Monthly', 30, 100.00, NULL),
                        ('Yearly', 365, 900.00, NULL),
                        ('10-Visits', 60, 120.00, 10)
                    ON CONFLICT (name) DO NOTHING;
                    """);

            st.execute("""
                    INSERT INTO classes(name, capacity, start_time)
                    VALUES
                        ('Morning Yoga', 15, NOW() + INTERVAL '1 day'),
                        ('Evening HIIT', 12, NOW() + INTERVAL '2 days')
                    ON CONFLICT DO NOTHING;
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to seed default data", e);
        }
    }
}
