package xyz.hyffer.onemessage_server.source_api.service.message_handler;

import lombok.extern.slf4j.Slf4j;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.ContactInstance;
import xyz.hyffer.onemessage_server.source_api.controller_onebot.payload.Response;
import xyz.hyffer.onemessage_server.source_api.service.SourceHandlerContext;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ResponseHandler_get_instance_list extends ResponseHandler {

    public ResponseHandler_get_instance_list(int _SID, SourceHandlerContext ctx) {
        super(_SID, ctx);
        expectedResponseClass = Response.Response_get_instance_list.class;
    }

    @Override
    public List<ReqRespPair> onResponse(Response response) {
        if (response.getRetcode() == 0) {
            List<ContactInstance> instances = ((Response.Response_get_instance_list) response).getData();
            // instances deserialized from response do not have _SID
            instances.forEach(instance -> instance.set_SID(_SID));
            migrateInstances(instances);
        }
        return null;
    }

    // TODO: migrate or split when remark changes, and more cases
    // TODO: prevent contact db get corrupted due to wrong source mapping
    public void migrateInstances(List<ContactInstance> instances) {
        AtomicInteger count = new AtomicInteger(0);
        for (ContactInstance instance : instances) {
            ctx.transactionWrapper.serializableTransaction_wrappedByRetry(() -> {
                if (migrateOneInstance(instance))
                    count.getAndIncrement();
            });
        }
        log.info("Migrated {} instances", count.get());
    }

    /**
     * insert or update a contact instance
     *
     * @param instance the instance to be migrated
     * @return is there any change to database
     */
    private boolean migrateOneInstance(ContactInstance instance) {
        Optional<ContactInstance> existing =
                ctx.instanceRepository.findBy_SIDAndId(instance.get_SID(), instance.getId());

        if (existing.isPresent()) {
            // record exists, update if any change
            ContactInstance old = existing.get();
            if (!old.getName().equals(instance.getName()) || !old.getRemark().equals(instance.getRemark())) {
                old.setName(instance.getName());
                old.setRemark(instance.getRemark());
                old.getAttachedContact().updateInstance(old);   // increase changeOrder
                ctx.instanceRepository.save(old);
                return true;
            }

        } else {
            Optional<Contact> sameName = ctx.contactRepository.findByRemark(instance.getRemark());
            if (sameName.isPresent()) {
                // new instance of an existing contact
                Contact contact = sameName.get();
                contact.addInstance(instance);
                ctx.contactRepository.save(contact);
                return true;

            } else {
                // new contact
                Contact contact = Contact.builder()
                        .remark(instance.getRemark())
                        .build();
                contact.addInstance(instance);
                ctx.contactRepository.save(contact);
                return true;
            }
        }
        return false;
    }
}
