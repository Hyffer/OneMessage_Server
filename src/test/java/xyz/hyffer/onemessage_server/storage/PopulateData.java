package xyz.hyffer.onemessage_server.storage;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.DataGenerator;
import xyz.hyffer.onemessage_server.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * initiate database with test data
 */
@Component
public class PopulateData {

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactInstanceRepository instanceRepository;
    @Autowired
    MessageRepository messageRepository;

    @Getter
    List<Contact> contacts = new ArrayList<>();
    @Getter
    List<Message> messages = new ArrayList<>();

    @PostConstruct
    void initData() {
        contacts = DataGenerator.getTestContacts_withoutGenValue();
        contactRepository.saveAllAndFlush(contacts);

        // check auto generated unique id
        assert contacts.stream().map(Contact::get_CID).distinct().count()
                == contacts.size();
        List<Integer> contactInstanceIds = contacts.stream()
                .map(Contact::getInstanceIds).flatMap(Set::stream).toList();
        assert contactInstanceIds.stream().distinct().count()
                == instanceRepository.count();

        messages = DataGenerator.getTestMessages_withoutGenValue();
        messageRepository.saveAllAndFlush(messages);

        assert messages.stream().map(Message::get_MID).distinct().count()
                == messages.size();
        // check auto generated non-pk column
        assert messages.stream().map(Message::getChangeOrder).distinct().count()
                == messages.size();
    }
}
