CREATE TABLE user_table (
    user_id SERIAL PRIMARY KEY,
    user_name varchar(255),
    email varchar(255),
    phone_number varchar(20),
    password varchar(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_login (
    user_login_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES user_table(user_id),
    token varchar(255),
    token_expire_time varchar(255)
);

CREATE TABLE budget_table (
    budget_id SERIAL PRIMARY KEY,
    budget_name varchar(255),
    commentary varchar(255),
    create_by INTEGER REFERENCES user_table(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_budget_table (
    user_budget_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES user_table(user_id),
    budget_id INTEGER REFERENCES budget_table(budget_id)
);

alter table user_budget_table 
add constraint uq_user_budget unique (user_id, budget_id);

CREATE TABLE expends_table (
    expends_id SERIAL PRIMARY KEY,
    expends_name VARCHAR(255),
    budget_id INTEGER REFERENCES budget_table(budget_id),
    paid_by INTEGER REFERENCES user_table(user_id),
    amount double precision,
    commentary VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_expends_table (
    user_expends_id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES user_table(user_id),
    expends_id INTEGER REFERENCES expends_table(expends_id)
);

alter table user_expends_table 
add constraint uq_user_expends unique (user_id, expends_id);

ALTER TABLE user_table  
ADD CONSTRAINT uq_username UNIQUE (user_name);

ALTER TABLE user_table  
ADD CONSTRAINT uq_user_phone UNIQUE (phone_number);

INSERT INTO user_table (user_name , email , password)
VALUES ('test', 'test@gmail.com', 'testPassword');

-- Create role and grant privileges
CREATE ROLE root LOGIN PASSWORD 'password';

REVOKE CONNECT ON DATABASE users FROM PUBLIC;
GRANT CONNECT ON DATABASE users TO new_user;

GRANT USAGE ON SCHEMA public TO new_user;

GRANT ALL PRIVILEGES ON DATABASE users TO new_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO new_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO new_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO new_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO new_user;