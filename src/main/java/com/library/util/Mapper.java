package com.library.util;

import com.library.dto.BookDto;
import com.library.model.Book;

public class Mapper {

    public static Book toEntity(BookDto dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.id = dto.id();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        book.setPublicationYear(dto.publicationYear());
        book.setTotalCopies(dto.totalCopies());
        return book;
    }

    public static BookDto toDto(Book book) {
        return BookDto.fromEntity(book);
    }
}