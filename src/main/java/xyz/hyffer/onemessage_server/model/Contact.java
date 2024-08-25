package xyz.hyffer.onemessage_server.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Group different {@link ContactInstance}s of one real entity together
 */
@Data
@Slf4j
@Entity
@Table(indexes = {
        @Index(columnList = "changeOrder", unique = true),
})
@SecondaryTable(name = "contact_state", indexes = {
        @Index(columnList = "stateOrder", unique = true),
        @Index(columnList = "lastMsgRank")
})
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contact_seq")
    @SequenceGenerator(name = "contact_seq", allocationSize = 1)
    @Setter(AccessLevel.PACKAGE)
    int _CID;

    /**
     * `changeOrder` and `deleted` are used for synchronization purpose.
     * <p>
     * `changeOrder` is a global sequence, indicating the order of contact changes.
     * Everytime a Contact created or edited, `changeOrder` increases by one.
     * To sync delete operation, deletion is also recognized as an update,
     * with `deleted` field set.
     * <p>
     * The changes include `remark`, `pinned` and `instances`.
     * But message related properties like `unread` and `lastMsgRank` are not considered here.
     * See `stateOrder` for more details.
     */
    @Column(columnDefinition="serial")
    @Generated(event = {EventType.INSERT, EventType.UPDATE}, sql = "nextval('contact_change_order_seq')")
    @Setter(AccessLevel.PACKAGE)
    int changeOrder;
    @Setter(AccessLevel.PACKAGE)
    boolean deleted;

    @Column(unique = true, nullable = false)
    String remark;
    boolean pinned;

    @OneToMany(mappedBy = "attachedContact",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    // Do not access directly, using `addInstance`, `updateInstance` and `removeInstance` to maintain integrity
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PACKAGE)
    Set<ContactInstance> instances = new HashSet<>();
    // `_instanceEditTimes` has no practical usage.
    // the purpose is only to make contact record change when
    // editing associated contact instances.
    // otherwise hibernate will not perform database update,
    // thus `changeOrder` can no longer reflect changes in `instances`.
    int _instanceEditTimes;

    // message related properties
    /**
     * `stateOrder` is also used for synchronization purpose.
     * <p>
     * like `changeOrder`, `stateOrder` is also a global sequence, denoting the order of
     * message receiving, sending and reading. These three operations are the majority in daily use.
     * <p>
     * On message receiving, `lastMsgRank` and `unread` increase.
     * When reading, `unread` is reset. And when sending, `lastMsgRank` is updated.
     * So we could synchronize only those fields and ignoring others
     * to reduce synchronization cost.
     */
    @Column(columnDefinition="serial", table = "contact_state")
    @Generated(event = {EventType.INSERT, EventType.UPDATE}, sql = "nextval('contact_state_state_order_seq')")
    @Setter(AccessLevel.PACKAGE)
    int stateOrder;

    @Column(table = "contact_state")
    int unread;
    // These are redundant information, which can be deduced from message table.
    // But for better performance, some are stored with contact.
    @Column(table = "contact_state")
    int lastMsgRank;
    // The last message with this contact.
    @Transient
    Message lastMsg;

    public Contact() {
    }

    @Builder
    public Contact(String remark, boolean pinned, int unread) {
        this.remark = remark;
        this.pinned = pinned;
        this.unread = unread;
    }

    @Builder(builderClassName = "ContactRawBuilder",
            builderMethodName = "rawBuilder", access = AccessLevel.PACKAGE)
    public Contact(int _CID,
                   int changeOrder, boolean deleted, String remark, boolean pinned,
                   int stateOrder, int unread, int lastMsgRank) {
        this(remark, pinned, unread);
        this._CID = _CID;
        this.changeOrder = changeOrder;
        this.deleted = deleted;
        this.stateOrder = stateOrder;
        this.lastMsgRank = lastMsgRank;
    }

    public Set<Integer> getInstanceIds() {
        return instances.stream().map(ContactInstance::get_CiID)
                .collect(Collectors.toSet());
    }

    /**
     * Get the instance object in `instances` attribute that
     * refers to the same record with `example`.
     * @param example a contact instance with correct `_SID` and `id`
     * @return one of the objects in `instances`, or null if no one matches
     */
    ContactInstance getAssociatedInstanceByExample(ContactInstance example) {
        for (ContactInstance ci : instances) {
            if (ci.referEquals(example)) {
                return ci;
            }
        }
        return null;
    }

    public boolean addInstance(ContactInstance instance) {
        ContactInstance existing = getAssociatedInstanceByExample(instance);
        if (existing != null) {
            log.error("Trying to add " + instance
                    + " to " + this
                    + ". But the instance already exists.");
            return false;
        }

        ++_instanceEditTimes;
        instance.setAttachedContact(this);
        instances.add(instance);
        if (deleted) {
            deleted = false;
        }
        return true;
    }

    public boolean updateInstance(ContactInstance instance) {
        ContactInstance tobeUpdated = getAssociatedInstanceByExample(instance);
        if (tobeUpdated == null) {
            log.error("Trying to update " + instance
                    + " of " + this
                    + ". But none of instances matches.");
            return false;
        }

        ++_instanceEditTimes;
        instances.remove(tobeUpdated);
        tobeUpdated.setAttachedContact(null);
        instance.setAttachedContact(this);
        instances.add(instance);
        return true;
    }

    public boolean removeInstance(ContactInstance instance) {
        ContactInstance tobeRemoved = getAssociatedInstanceByExample(instance);
        if (tobeRemoved == null) {
            log.error("Trying to remove " + instance
                    + " from " + this
                    + ". But none of instances matches.");
            return false;
        }

        ++_instanceEditTimes;
        instances.remove(tobeRemoved);
        tobeRemoved.setAttachedContact(null);
        if (instances.isEmpty()) {
            deleted = true;
        }
        return true;
    }

}
