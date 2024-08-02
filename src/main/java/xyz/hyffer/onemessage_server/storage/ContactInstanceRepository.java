package xyz.hyffer.onemessage_server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.hyffer.onemessage_server.model.ContactInstance;

/**
 * JPA Repository of {@link ContactInstance} entity
 */
public interface ContactInstanceRepository extends JpaRepository<ContactInstance, Integer> {
}
