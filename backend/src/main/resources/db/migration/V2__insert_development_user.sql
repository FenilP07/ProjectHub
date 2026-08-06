INSERT INTO users (
    id,
    email,
    password_hash,
    first_name,
    last_name,
    role,
    status,
    created_at,
    updated_at
)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'fenil@example.com',
           'temporary-password-hash',
           'Fenil',
           'Patel',
           'USER',
           'ACTIVE',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );