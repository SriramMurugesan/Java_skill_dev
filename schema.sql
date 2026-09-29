-- Database Schema for Campus Student Management

CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    age INT NOT NULL
);

-- Optional sample data:
-- INSERT INTO students (name, department, age) VALUES ('Alice Smith', 'Computer Science', 21);
-- INSERT INTO students (name, department, age) VALUES ('Bob Jones', 'Electrical Engineering', 22);
