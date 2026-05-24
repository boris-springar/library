package com.library.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Year;
import java.util.UUID;

@Entity
@Table(name = "books")
public class Book extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.UuidGenerator
    @Column(updatable = false, nullable = false)
    public UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Author is required")
    private String author;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotNull(message = "Publication year is required")
    @Column(name = "publication_year")
    private Integer publicationYear;

    @NotNull(message = "Total copies count is required")
    @Column(name = "total_copies")
    private Integer totalCopies;

    // Default constructor required by JPA
    public Book() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public Integer getTotalCopies() { return totalCopies; }
    public void setTotalCopies(Integer totalCopies) { this.totalCopies = totalCopies; }
}