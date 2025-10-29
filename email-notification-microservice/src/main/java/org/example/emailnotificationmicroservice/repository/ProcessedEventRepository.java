package org.example.emailnotificationmicroservice.repository;

import org.example.emailnotificationmicroservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long>{

    ProcessedEvent findByMessageId(String messageId);

}
