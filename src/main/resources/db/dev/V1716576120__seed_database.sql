-- Seed Members
INSERT INTO members (id, first_name, last_name, email)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Alice', 'Smith', 'alice@example.com'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Bob', 'Jones', 'bob@example.com'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Carol', 'Williams', 'carol@example.com');

-- Seed Books
INSERT INTO books (id, title, author, isbn, publication_year, total_copies)
VALUES
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'Dune', 'Frank Herbert', '978-0441172719', 1965, 3),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'Neuromancer', 'William Gibson', '978-0441569595', 1984, 2),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b13', 'The Left Hand of Darkness', 'Ursula K. Le Guin', '978-0441478125', 1969, 1);