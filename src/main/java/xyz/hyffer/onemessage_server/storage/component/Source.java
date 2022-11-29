package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

@Data
public class Source {
    int _SID;
    String name;

    public Source() {}

    public Source(String name) {
        this.name = name;
    }
}
