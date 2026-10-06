CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    age INT,
    CONSTRAINT fk_student_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);