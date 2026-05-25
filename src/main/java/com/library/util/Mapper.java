package com.library.util;

import com.library.dto.BookCreateDto;
import com.library.dto.BookResponseDto;
import com.library.model.Book;

public class Mapper {

    public static Book toEntity(BookCreateDto dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setIsbn(dto.isbn());
        book.setPublicationYear(dto.publicationYear());
        book.setTotalCopies(dto.totalCopies());
        return book;
    }

    public static BookResponseDto toDto(Book book) {
        return BookResponseDto.fromEntity(book);
    }
}