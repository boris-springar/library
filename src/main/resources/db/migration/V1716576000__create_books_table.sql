CREATE TABLE books (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    publication_year INTEGER NOT NULL,
    total_copies INTEGER NOT NULL
);