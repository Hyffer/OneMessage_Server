package xyz.hyffer.onemessage_server.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(indexes = {
        @Index(columnList = "rank", unique = true),
        @Index(name = "message_retrieve_key", columnList = "_CiID, rank", unique = true)
})
@SecondaryTable(name = "message_content", indexes = {
        @Index(columnList = "contentOrder", unique = true)
})
public class Message {
    // TODO: table partition by _CiID,
    //  and use (_CiID, _CiMID) as index?
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    @SequenceGenerator(name = "message_seq", allocationSize = 1)
    @Setter(AccessLevel.PACKAGE)
    int _MID;

    int _CiID;

    // these properties only used in group message
    String type;
    String senderId;
    String senderName;

    /**
     * `rank` indicates the temporal sequence of messages, which is a total order relationship.
     * Meanwhile, it represents message change order, used for synchronization purpose.
     * <p>
     * Because message sending is asynchronous,
     * when message status switches from `SENDING` to `OUT` or `ERROR`,
     * `time` and metadata properties will get changed.
     */
    @Column(columnDefinition="serial")
    @Generated(event = {EventType.INSERT, EventType.UPDATE}, sql = "nextval('message_rank_seq')")
    @Setter(AccessLevel.PACKAGE)
    int rank;

    // message send/receive time
    // one with later `time` has larger `rank`, but `time` might not be unique.
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Timestamp time;

    public enum Status {
        ERROR, IN, OUT, SENDING
    }
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    Status status;

    // TODO: metadata to refer to a specific message
    //  for reply and withdraw.
    //  these diverge among different agents.
//    int messageId;
//    int internalId;
//    int quoteId;

    /**
     * `contentOrder` is used for message content synchronization.
     */
    @Column(columnDefinition="serial", table = "message_content")
    @Generated(event = {EventType.INSERT, EventType.UPDATE}, sql = "nextval('message_content_content_order_seq')")
    @Setter(AccessLevel.PACKAGE)
    int contentOrder;

    // message modify/withdraw time
    @Column(table = "message_content")
    Timestamp modifiedTime;
    @Column(table = "message_content")
    boolean deleted;

    @Column(table = "message_content")
    @JdbcTypeCode(SqlTypes.JSON)
    List<MessageSegment> segments;

    public Message() {
    }

    @Builder(builderClassName = "MessageRawBuilder",
            builderMethodName = "rawBuilder", access = AccessLevel.PACKAGE)
    public Message(int _MID, int _CiID,
                   String type, String senderId, String senderName,
                   Timestamp time, Status status,
                   Timestamp modifiedTime, boolean deleted,
                   @Singular List<MessageSegment> segments) {
        this._MID = _MID;
        this._CiID = _CiID;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.time = time;
        this.status = status;
        this.modifiedTime = modifiedTime;
        this.deleted = deleted;
        this.segments = segments;
    }
}
