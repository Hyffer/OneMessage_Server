package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

@Data
public class ContactInfo {
    int _SID;
    String id;
    String name;

    public ContactInfo() {}

    public ContactInfo(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
