package xyz.hyffer.onemessage_server.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.hyffer.onemessage_server.storage.component.Message;

import java.util.List;

@Mapper
public interface MessageMapper {

    /**
     * Get messages of a contact from first_MID to last_MID
     * both the first and last are included
     *
     * @param _CID id of the contact
     * @param first_MID id of the first message
     * @param last_MID id of the last message
     * @return List of messages
     */
    List<Message> getMessages(int _CID, int first_MID, int last_MID);

    void createUserTable(int _CID);

    void createGroupTable(int _CID);

    /**
     * Add a message in message record of given contact
     * @param _CID id of the contact
     * @param message message to be added
     *                auto increment key `_MID` will be assigned
     */
    void addMessageRecord(int _CID, Message message);

}
