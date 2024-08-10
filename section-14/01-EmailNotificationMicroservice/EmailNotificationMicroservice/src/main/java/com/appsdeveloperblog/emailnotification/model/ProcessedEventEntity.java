package com.appsdeveloperblog.emailnotification.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter @Setter
@Table(name = "processed_events")
public class ProcessedEventEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 7511328629940413505L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String messageId;

    @Column(nullable = false)
    private String productId;

    public ProcessedEventEntity(String messageId, String productId) {
        this.messageId = messageId;
        this.productId = productId;
    }

    public ProcessedEventEntity() {

    }
}
