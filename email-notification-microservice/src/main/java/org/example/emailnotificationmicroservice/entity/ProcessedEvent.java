package org.example.emailnotificationmicroservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@Table(name = "processed_events")
public class ProcessedEvent implements Serializable {

    private static final long serialVersionUID = 21312432543245324L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String messageId;

    @Column(nullable = false)
    String productId;

    public ProcessedEvent(String messageId, String productId) {
        this.messageId = messageId;
        this.productId = productId;
    }
}
