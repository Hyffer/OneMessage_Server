package xyz.hyffer.onemessage_server.storage;

import org.junit.jupiter.api.Test;
import xyz.hyffer.onemessage_server.model.Message;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageRepositoryTest extends RepositoryTest {
    @Test
    void findBy_CiID() {
        int e_CiID = data.contacts.get(0).getInstanceIds().stream().toList().get(0);
        List<Message> standard_messages = data.messages.stream()
                .filter(message -> message.get_CiID() == e_CiID).toList();

        List<Message> messages = messageRepository.findBy_CiID(e_CiID);
        assertThat(messages).usingRecursiveComparison().ignoringCollectionOrder()
                .isEqualTo(standard_messages);
    }

    @Test
    void findBy_CiID_empty() {
        List<Message> no_messages = messageRepository.findBy_CiID(-1);
        assertThat(no_messages.size()).isEqualTo(0);
    }

    @Test
    void update_changeOrder_increment() {
        Message original_message = data.messages.get(0);
        int old_eventSeq = original_message.getChangeOrder();

        Optional<Message> result = messageRepository.findById(original_message.get_MID());
        assert result.isPresent();
        Message modified_message = result.get();
        modified_message.setModifiedTime(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(modified_message);
        messageRepository.flush();  // this is necessary to get database generated value

        int new_eventSeq = modified_message.getChangeOrder();
        assertThat(new_eventSeq).isGreaterThan(old_eventSeq);
    }
}
