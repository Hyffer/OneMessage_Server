package xyz.hyffer.onemessage_server.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.transaction.TestTransaction;
import xyz.hyffer.onemessage_server.model.Contact;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@Import(TransactionWrapper.class)
@EnableRetry
public class ConcurrencyTest extends RepositoryTest {

    // use bean injection to make `@Transactional` annotation working
    @Autowired
    TransactionWrapper transactionWrapper;

    /**
     * Simulating a time-consuming task that performs query-update on record,
     * to test transaction conflict
     * <p>
     * Task:
     * 1) query the contact with _CID=1,
     * 2) append `String s` to its original remark,
     * 3) wait 500ms,
     * 4) save updated contact to database
     */
    Runnable updateContactTask(String s) {
        return () -> {
            try {
                Optional<Contact> result = contactRepository.findById(1);
                assert result.isPresent();
                Contact contact = result.get();
                contact.setRemark(contact.getRemark() + s);
                sleep(500);
                contactRepository.save(contact);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Running conflict tasks in two transactions simultaneously
     */
    @Test
    void transactionConflict() {
        Optional<Contact> result = contactRepository.findById(1);
        assert result.isPresent();
        String original_remark = result.get().getRemark();

        // Transaction runs in other thread will commit,
        // unlike that in test method will be rolled back automatically
        // https://github.com/spring-projects/spring-framework/issues/25439
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable t1 = () -> transactionWrapper.serializableTransaction(updateContactTask("t1"));
        Runnable t2 = () -> transactionWrapper.serializableTransaction(updateContactTask("t2"));
        Future<?> f1 = executor.submit(t1);
        Future<?> f2 = executor.submit(t2);

        // Assertion:
        // 1. conflict detected
        // expect CannotAcquireLockException (extended from PessimisticLockingFailureException)
        ExecutionException e = assertThrows(ExecutionException.class, () -> {
            f1.get();
            f2.get();
        });
        assertThat(e.getCause()).isInstanceOf(PessimisticLockingFailureException.class);

        // start new test transaction to see the result of asynchronous tasks above
        TestTransaction.end();
        TestTransaction.start();

        // 2. one transaction successfully commits
        // (how to check another is rolled back?)
        result = contactRepository.findById(1);
        assert result.isPresent();
        Contact contact = result.get();
        assertThat(contact.getRemark()).isIn(original_remark + "t1", original_remark + "t2");

        // restore original value
        contact.setRemark(original_remark);
        contactRepository.save(contact);
        TestTransaction.flagForCommit();
    }

    /**
     * Running conflict transactions simultaneously, but wrapped by retry
     */
    @Test
    void transactionSerialized() {
        Optional<Contact> result = contactRepository.findById(1);
        assert result.isPresent();
        String original_remark = result.get().getRemark();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Runnable t1 = () -> transactionWrapper.serializableTransaction_wrappedByRetry(updateContactTask("t1"));
        Runnable t2 = () -> transactionWrapper.serializableTransaction_wrappedByRetry(updateContactTask("t2"));
        Future<?> f1 = executor.submit(t1);
        Future<?> f2 = executor.submit(t2);
        try {
            f1.get();
            f2.get();
        } catch (ExecutionException | InterruptedException e) {
            fail();
        }

        TestTransaction.end();
        TestTransaction.start();

        // check transactions are serialized
        result = contactRepository.findById(1);
        assert result.isPresent();
        Contact contact = result.get();
        assertThat(contact.getRemark()).isIn(original_remark + "t1t2", original_remark + "t2t1");

        // restore
        contact.setRemark(original_remark);
        contactRepository.save(contact);
        TestTransaction.flagForCommit();
    }
}
