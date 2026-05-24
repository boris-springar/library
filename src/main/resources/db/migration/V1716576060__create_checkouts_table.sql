CREATE TABLE checkouts (
    id UUID DEFAULT random_uuid() PRIMARY KEY,
    book_id UUID NOT NULL,
    member_id UUID NOT NULL,
    checkout_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_checkout_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT fk_checkouts_member FOREIGN KEY (member_id) REFERENCES members(id)
);