CREATE TABLE members (
     id UUID DEFAULT random_uuid() PRIMARY KEY,
     first_name VARCHAR(100) NOT NULL,
     last_name VARCHAR(100) NOT NULL,
     email VARCHAR(255) NOT NULL UNIQUE
);
