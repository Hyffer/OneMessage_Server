package xyz.hyffer.onemessage_server.client_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Contact_;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.model.Message_;
import xyz.hyffer.onemessage_server.storage.ContactRepository;
import xyz.hyffer.onemessage_server.storage.MessageRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Complex queries for client service.
 * See <a href="docs/API_CS.md">API_CS.md</a>
 */
@Service
public class ClientCustomQuery {

    final ContactRepository contactRepository;

    final MessageRepository messageRepository;

    @Autowired
    public ClientCustomQuery(ContactRepository contactRepository, MessageRepository messageRepository) {
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * fetch first {@code limit} elements with {@code sort} order
     *
     * @param limit number of elements, 0 for no limit. MUST be non-negative
     * @param sort  sort order
     * @return {@link Pageable} object
     */
    static Pageable headPageOf(int limit, Sort sort) {
        assert limit >= 0;
        if (limit > 0) {
            return PageRequest.of(0, limit, sort);
        } else {
            return Pageable.unpaged(sort);
        }
    }

    public List<Contact> catchupContacts(Integer _CID_l, Integer _CID_r, Integer pre_cOrd, Integer pre_sOrd, int limit) {
        LinkedList<Specification<Contact>> specs = new LinkedList<>();
        if (_CID_l != null) {
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Contact_._CID), _CID_l));
        }
        if (_CID_r != null) {
            specs.push((root, query, builder) -> builder.lessThanOrEqualTo(root.get(Contact_._CID), _CID_r));
        }

        Sort sort = Sort.by(Sort.Order.asc(Contact_._CID.getName()));
        if (pre_cOrd != null) {
            sort = Sort.by(Sort.Order.asc(Contact_.changeOrder.getName()));
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Contact_.changeOrder), pre_cOrd));
        }
        if (pre_sOrd != null) {
            sort = Sort.by(Sort.Order.asc(Contact_.stateOrder.getName()));
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Contact_.stateOrder), pre_sOrd));
        }
        if (pre_cOrd != null && pre_sOrd != null) {
            sort = Sort.unsorted();
            specs.push(specs.pop().or(specs.pop()));
        }

        Pageable pageable = headPageOf(limit, sort);

        Page<Contact> page = contactRepository.findAll(Specification.allOf(specs), pageable);
        return page.getContent();
    }

    List<Message> catchupMessages(Integer _MID_l, Integer _MID_r, Integer pre_rank, Integer pre_cOrd, int limit) {
        LinkedList<Specification<Message>> specs = new LinkedList<>();
        if (_MID_l != null) {
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Message_._MID), _MID_l));
        }
        if (_MID_r != null) {
            specs.push((root, query, builder) -> builder.lessThanOrEqualTo(root.get(Message_._MID), _MID_r));
        }

        Sort sort = Sort.by(Sort.Order.asc(Message_._MID.getName()));
        if (pre_rank != null) {
            sort = Sort.by(Sort.Order.asc(Message_.rank.getName()));
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Message_.rank), pre_rank));
        }
        if (pre_cOrd != null) {
            sort = Sort.by(Sort.Order.asc(Message_.contentOrder.getName()));
            specs.push((root, query, builder) -> builder.greaterThan(root.get(Message_.contentOrder), pre_cOrd));
        }
        if (pre_rank != null && pre_cOrd != null) {
            sort = Sort.unsorted();
            specs.push(specs.pop().or(specs.pop()));
        }

        Pageable pageable = headPageOf(limit, sort);

        Page<Message> page = messageRepository.findAll(Specification.allOf(specs), pageable);
        return page.getContent();
    }

    List<Contact> getContacts(boolean _pinned, Integer post_lMRank, int limit) {
        LinkedList<Specification<Contact>> specs = new LinkedList<>();
        specs.push((root, query, builder) -> builder.equal(root.get(Contact_.pinned), _pinned));
        if (post_lMRank != null) {
            specs.push((root, query, builder) -> builder.lessThan(root.get(Contact_.lastMsgRank), post_lMRank));
        }

        Sort sort = Sort.by(Sort.Order.desc(Contact_.lastMsgRank.getName()));
        Pageable pageable = headPageOf(limit, sort);

        Page<Contact> page = contactRepository.findAll(Specification.allOf(specs), pageable);
        return page.getContent();
    }

    List<Contact> searchContacts(String key, int limit) {
        Specification<Contact> spec =
                (root, query, builder) -> builder.like(root.get(Contact_.remark), "%" + key + "%");
        Pageable pageable = headPageOf(limit, Sort.unsorted());

        Page<Contact> page = contactRepository.findAll(spec, pageable);
        return page.getContent();
    }

    List<Message> getMessages(Collection<Integer> _CiIDs, Integer post_rank, int limit) {
        LinkedList<Specification<Message>> specs = new LinkedList<>();
        specs.push((root, query, builder) -> root.get(Message_._CiID).in(_CiIDs));
        if (post_rank != null) {
            specs.push((root, query, builder) -> builder.lessThan(root.get(Message_.rank), post_rank));
        }

        Sort sort = Sort.by(Sort.Order.desc(Message_.rank.getName()));
        Pageable pageable = headPageOf(limit, sort);

        Page<Message> page = messageRepository.findAll(Specification.allOf(specs), pageable);
        List<Message> messages = page.getContent();
        Collections.reverse(messages);
        return messages;
    }
}
