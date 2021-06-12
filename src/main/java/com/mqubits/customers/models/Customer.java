package com.mqubits.customers.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
public class Customer {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(
            name = "system-uuid",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @Column(name = "`created_at`")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "`updated_at`")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Email
    @NotNull
    @UniqueElements
    private String email;

    public Customer(String email) {
        this.email = email;
    }
}
