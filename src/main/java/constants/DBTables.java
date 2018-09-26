package constants;

public enum DBTables {
    USERS("users", "CREATE TABLE users (id SERIAL PRIMARY KEY, email VARCHAR(150) UNIQUE, registration_date bigint, password VARCHAR(50));"),
    SESSIONS("sessions", "CREATE TABLE sessions (id SERIAL PRIMARY KEY, session_id VARCHAR(100) , user_id INTEGER);");

    private String tableName;
    private String createTableSQL;

    DBTables(String tableName, String createTableSQL){
        this.tableName= tableName;
        this.createTableSQL = createTableSQL;
    }

    public String getTableName(){
        return this.tableName;
    }

    public String getCreateTableSQL(){
        return this.createTableSQL;
    }

    @Override
    public String toString(){
        return this.tableName;
    }
}
