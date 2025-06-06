INSERT IGNORE INTO
    roles (name)
VALUES ('ADMIN'),
    ('CLIENT'),
    ('TRAINER');

INSERT IGNORE INTO
    accounts (
        email,
        password,
        email_confirmed,
        blocked,
        created_at,
        updated_at
    )
VALUES
    ('admin@example.com', '$2a$12$2yEeMA5fr9WSwJDyoZOteOn0Y7wdkVdOinjkJwRdnqmqJVsFHO4LS', TRUE, FALSE, NOW(), NOW()),
    ('client@example.com', '$2a$12$2yEeMA5fr9WSwJDyoZOteOn0Y7wdkVdOinjkJwRdnqmqJVsFHO4LS', TRUE, FALSE, NOW(), NOW()),
    ('trainer@example.com', '$2a$12$2yEeMA5fr9WSwJDyoZOteOn0Y7wdkVdOinjkJwRdnqmqJVsFHO4LS', TRUE, FALSE, NOW(), NOW());

INSERT IGNORE INTO
    accounts_roles (account_id, role_id)
VALUES
    ((SELECT id FROM accounts WHERE email = 'admin@example.com'), (SELECT id FROM roles WHERE name = 'ADMIN')),
    ((SELECT id FROM accounts WHERE email = 'client@example.com'), (SELECT id FROM roles WHERE name = 'CLIENT')),
    ((SELECT id FROM accounts WHERE email = 'trainer@example.com'), (SELECT id FROM roles WHERE name = 'TRAINER'));

INSERT IGNORE INTO wallets (account_id, balance, created_at, updated_at)
VALUES
    ((SELECT id FROM accounts WHERE email = 'client@example.com'), 100.00, NOW(), NOW());

INSERT IGNORE INTO
    cycles (name, description, duration_in_days, published, price, created_at, updated_at)
VALUES
    ('Basic Cycle', 'A basic training cycle for beginners.', 30, TRUE, 49.99, NOW(), NOW()),
    ('Advanced Cycle', 'An advanced training cycle for experienced athletes.', 60, TRUE, 99.99, NOW(), NOW()),
    ('Premium Cycle', 'A premium training cycle with personalized coaching.', 90, TRUE, 149.99, NOW(), NOW()),
    ('Custom Cycle', 'A custom training cycle tailored to individual needs.', 45, FALSE, 79.99, NOW(), NOW()),
    ('Seasonal Cycle', 'A seasonal training cycle focusing on specific sports.', 120, TRUE, 199.99, NOW(), NOW()),
    ('Recovery Cycle', 'A recovery cycle for athletes post-competition.', 30, TRUE, 59.99, NOW(), NOW()),
    ('Strength Cycle', 'A strength-focused training cycle.', 75, TRUE, 89.99, NOW(), NOW()),
    ('Endurance Cycle', 'An endurance-focused training cycle.', 90, TRUE, 109.99, NOW(), NOW()),
    ('Flexibility Cycle', 'A flexibility and mobility training cycle.', 60, TRUE, 69.99, NOW(), NOW()),
    ('Nutrition Cycle', 'A nutrition-focused training cycle with meal plans.', 30, TRUE, 79.99, NOW(), NOW());

