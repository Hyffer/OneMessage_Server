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
    void findBy_Rank_not_exist() {
        int invalid_rank = -1;
        Optional<Message> result = messageRepository.findByRank(invalid_rank);
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void update_rank_increment() {
        int e_MID = data.messages.get(0).get_MID();
        Optional<Message> result = messageRepository.findById(e_MID);
        assert result.isPresent();
        Message message = result.get();
        int old_rank = message.getRank();
        int old_contentOrder = message.getContentOrder();

        // `rank` increment
        message.setTime(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(message);
        messageRepository.flush();  // this is necessary to get database generated value
        int new_rank = message.getRank();
        assertThat(new_rank).isGreaterThan(old_rank);

        // meanwhile `contentOrder` remains the same
        int new_contentOrder = message.getContentOrder();
        assertThat(new_contentOrder).isEqualTo(old_contentOrder);
    }

    @Test
    void update_contentOrder_increment() {
        int e_MID = data.messages.get(0).get_MID();
        Optional<Message> result = messageRepository.findById(e_MID);
        assert result.isPresent();
        Message message = result.get();
        int old_rank = message.getRank();
        int old_contentOrder = message.getContentOrder();

        // `contentOrder` increment
        message.setModifiedTime(new Timestamp(System.currentTimeMillis()));
        messageRepository.save(message);
        messageRepository.flush();  // this is necessary to get database generated value
        int new_contentOrder = message.getContentOrder();
        assertThat(new_contentOrder).isGreaterThan(old_contentOrder);

        // meanwhile `rank` remains the same
        int new_rank = message.getRank();
        assertThat(new_rank).isEqualTo(old_rank);
    }
}
