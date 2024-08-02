package xyz.hyffer.onemessage_server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.hyffer.onemessage_server.model.Source;

public interface SourceRepository extends JpaRepository<Source, Integer> {
}
