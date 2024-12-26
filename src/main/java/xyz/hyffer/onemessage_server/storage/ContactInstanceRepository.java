package xyz.hyffer.onemessage_server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.hyffer.onemessage_server.model.ContactInstance;

import java.util.Optional;

/**
 * JPA Repository of {@link ContactInstance} entity
 */
public interface ContactInstanceRepository extends JpaRepository<ContactInstance, Integer> {
    Optional<ContactInstance> findBy_SIDAndId(int _SID, String id);
}
