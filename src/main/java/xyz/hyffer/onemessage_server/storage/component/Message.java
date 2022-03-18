package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Message {
    @Id // used for mongodb query, mapping primary key '_id' to attribute '_MID'
    int _MID;
    @Transient  // make fields transparent to mongodb
    Timestamp time;
//    int messageId;
//    int internalId;
    @Transient
    String direction;
//    int quoteId;
    @Transient
    String type;
    @Transient
    long senderId;
    @Transient
    String senderName;
    ArrayList<MessageSegment> segments;

    public Message() {}

    public Message(Timestamp time, String direction) {
        this.time = time;
        this.direction = direction;
    }

    public Message(Timestamp time, String direction, String type, long senderId, String senderName) {
        this.time = time;
        this.direction = direction;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
    }
}
