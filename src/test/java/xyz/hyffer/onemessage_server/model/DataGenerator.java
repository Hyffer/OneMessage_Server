package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Generate test data
 */
public class DataGenerator {
    private static final Contact c1, c2;
    private static final ContactInstance c1ci1, c1ci2, c2ci3;
    private static final List<Contact> testContacts;

    private static final Message m1, m2;
    private static final List<Message> testMessages;

    static {
        // Contacts
        c1 = Contact.rawBuilder()
                ._CID(1)
                .remark("\uFEDC\uBA98\u7654\u3210")
                .build();

        c2 = Contact.rawBuilder()
                ._CID(2)
                .remark("testIdAutoGen")
                .build();

        c1ci1 = ContactInstance.rawBuilder()
                ._CiID(1)
                ._SID(1)
                .id("utf8mb4_test")
                .name("\u0123\u4567\u89AB\uCDEF")
                .build();

        c1ci2 = ContactInstance.rawBuilder()
                ._CiID(2)
                ._SID(1)
                .id("c1ci2")
                .name("c1ci2name")
                .build();

        c2ci3 = ContactInstance.rawBuilder()
                ._CiID(3)
                ._SID(1)
                .id("c2ci3")
                .name("c2ci3name")
                .build();

        c1.addInstance(c1ci1);
        c1.addInstance(c1ci2);
        c2.addInstance(c2ci3);

        testContacts = new ArrayList<>();
        testContacts.add(c1);
        testContacts.add(c2);

        // Messages
        m1 = Message.rawBuilder()
                ._MID(1)
                ._CiID(c1ci1.get_CiID())
                .segment(new MessageSegment(
                        new MessageSegmentContent.Plaintext("a text message")
                ))
                .build();

        m2 = Message.rawBuilder()
                ._MID(2)
                ._CiID(c1ci2.get_CiID())
                .segment(new MessageSegment(
                        new MessageSegmentContent.Image("img://url")
                ))
                .build();

        testMessages = new ArrayList<>();
        testMessages.add(m1);
        testMessages.add(m2);
    }

    /**
     * Get test contacts, with emulated database generated value.
     * <p>
     * i.e. Contact._CID and ContactInstance._CiID are meaningful values,
     * but they <b>MIGHT NOT</b> be the same as those generated by a real database
     * (e.g. it depends on actual insert order)
     * @return a list of {@link Contact}
     */
    public static List<Contact> getTestContacts() {
        // make a deep copy of `testContacts`
        // prevent modifying original objects
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        try {
            List<Contact> contacts = mapper.readValue(mapper.writeValueAsString(testContacts), new TypeReference<>() {});
            // maintain relation
            for (Contact c : contacts) {
                for (ContactInstance ci : c.getInstances()) {
                    ci.setAttachedContact(c);
                }
            }
            return contacts;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get test contacts, without emulated database generated value.
     * <p>
     * i.e. Contact._CID and ContactInstance._CiID are all 0
     * @return a list of {@link Contact}
     */
    public static List<Contact> getTestContacts_withoutGenValue() {
        // deep copy and erase generated value
        List<Contact> contacts = getTestContacts();
        for (Contact c : contacts) {
            c.set_CID(0);
            for (ContactInstance ci : c.getInstances()) {
                ci.set_CiID(0);
            }
        }
        return contacts;
    }

    /**
     * Get test messages, with emulated database generated value.
     * <p>
     * i.e. Message._MID are meaningful values,
     * but they <b>MIGHT NOT</b> be the same as those generated by a real database
     * (e.g. it depends on the actual insert order)
     * @return a list of {@link Message}
     */
    public static List<Message> getTestMessages() {
        // make a deep copy of `testMessages`
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(mapper.writeValueAsString(testMessages), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get test messages, without emulated database generated value.
     * <p>
     * i.e. Message._MID are all 0. <b>BUT</b> Message._CiID are still meaningful values.
     * @return a list of {@link Message}
     */
    public static List<Message> getTestMessages_withoutGenValue() {
        List<Message> messages = getTestMessages();
        for (Message m : messages) {
            m.set_MID(0);
        }
        return messages;
    }
}
