package com.fitnessclub.db;
public enum GymConfig {
    INSTANCE;

    private final String url = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:5432/postgres?sslmode=require"
    );
    private final String user = System.getenv().getOrDefault(
            "DB_USER",
            "postgres.wwlkpayichwwogvjsaqb"
    );
    private final String password = System.getenv().getOrDefault(
            "DB_PASSWORD",
            "Myrzazhan_12"
    );

    private final Database database = new SupabaseDatabase(url, user, password);

    public Database database() {
        return database;
    }

    public String url() {
        return url;
    }

    public String user() {
        return user;
    }
}
