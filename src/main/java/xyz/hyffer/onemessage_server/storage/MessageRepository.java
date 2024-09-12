package xyz.hyffer.onemessage_server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.Collection;
import java.util.List;

/**
 * JPA Repository of {@link Message} entity
 */
public interface MessageRepository extends JpaRepository<Message, Integer>, JpaSpecificationExecutor<Message> {
    List<Message> findBy_CiID(int _CiID);

    List<Message> findBy_CiIDIn(Collection<Integer> _CiIDs);

    List<Message> findBy_MIDIn(Collection<Integer> _MIDs);

    int countBy_CiIDIn(Collection<Integer> _CiIDs);
}
