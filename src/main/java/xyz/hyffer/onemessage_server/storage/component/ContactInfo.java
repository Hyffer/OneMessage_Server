package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

@Data
public class ContactInfo {
    int _SID;
    long id;
    String name;

    public ContactInfo() {}

    public ContactInfo(long id, String name) {
        this.id = id;
        this.name = name;
    }
}
