package xyz.hyffer.onemessage_server.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import xyz.hyffer.onemessage_server.storage.component.Contact;

import java.util.List;

@Mapper
public interface ContactMapper {

    List<Contact> getContacts(int limit);

    List<Contact> searchContacts(String key, int limit);

    /**
     * Get the total number of messages of a contact
     * @param _CID id of the contact
     * @return the total number, or null indicates an error
     */
    Integer getContactTotal(int _CID);

}
