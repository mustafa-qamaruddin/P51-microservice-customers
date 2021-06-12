package com.mqubits.customers.models;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
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
    @Column(unique = true)
    private String email;

    private String timeline;

    public Customer() {
    }

    public Customer(String email, String timeline) {
        this.email = email;
        this.timeline = timeline;
    }

    public String getId() {
        return id;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }
}
