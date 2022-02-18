package xyz.hyffer.onemessage_server.storage.component;

import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;

@Data
public class Message {
    int _MID;
    Timestamp time;
//    int messageId;
//    int internalId;
    int direction;
//    int quoteId;
    String type;
    long senderId;
    String senderName;
    ArrayList<MessageSegment> segments;
}
