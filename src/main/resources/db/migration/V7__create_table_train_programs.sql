CREATE TABLE IF NOT EXISTS train_programs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    workout TEXT NOT NULL,
    account_cycle_enrollment_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (account_cycle_enrollment_id) REFERENCES account_cycle_enrollments(id) ON DELETE CASCADE
);
