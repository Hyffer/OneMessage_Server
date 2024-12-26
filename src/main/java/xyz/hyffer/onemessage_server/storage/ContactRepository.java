package xyz.hyffer.onemessage_server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.ContactInstance;

import java.util.Optional;

/**
 * JPA Repository of {@link Contact} entity,
 * and {@link ContactInstance ContactInstance} inside it
 */
public interface ContactRepository extends JpaRepository<Contact, Integer>, JpaSpecificationExecutor<Contact> {
    Optional<Contact> findByRemark(String remark);
}
