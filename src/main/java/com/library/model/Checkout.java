package com.library.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "checkouts")
public class Checkout extends PanacheEntityBase {


    @Id
    @GeneratedValue(generator = "UUID")
    @org.hibernate.annotations.UuidGenerator
    @Column(updatable = false, nullable = false)
    public UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "loan_date", nullable = false)
    @NotNull(message = "Loan date is required")
    private LocalDate loanDate;

    @Column(name = "due_date", nullable = false)
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @Column(name = "returned", nullable = false)
    private boolean returned = false;

    // Default constructor
    public Checkout() {}

    // Getters and Setters
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}