package com.library.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Checkout extends PanacheEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "member_id", nullable = false)
    @NotNull(message = "Member ID is required")
    private Long memberId;

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

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }
}