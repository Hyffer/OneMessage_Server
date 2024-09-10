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
import xyz.hyffer.onemessage_server.storage.ContactRepository;

import java.util.LinkedList;
import java.util.List;

@Service
public class ClientCustomQuery {

    final ContactRepository contactRepository;

    @Autowired
    public ClientCustomQuery(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
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

        Pageable pageable;
        if (limit != 0) {
            pageable = PageRequest.of(0, limit, sort);
        } else {
            pageable = Pageable.unpaged(sort);
        }
        Page<Contact> page = contactRepository.findAll(Specification.allOf(specs), pageable);
        return page.getContent();
    }

    List<Message> catchupMessages(Integer _MID_l, Integer _MID_r, Integer pre_rank, Integer pre_cOrd, int limit) {
        return null;
    }

    List<Contact> getContacts(boolean pinned, Integer post_lMRank, int limit) {
        return null;
    }

    List<Contact> getContacts(String key, int limit) {
        return null;
    }

    List<Message> getMessages(int _CID, Integer post_rank, int limit) {
        return null;
    }

    void updateState() {

    }
    void postMessage() {

    }
//    void editContact() {
//
//    }

}
