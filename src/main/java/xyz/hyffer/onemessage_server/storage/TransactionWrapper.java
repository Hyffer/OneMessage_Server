package xyz.hyffer.onemessage_server.storage;

import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionWrapper {
    /**
     * Use {@link Isolation} to do concurrency control.
     * (Or adding {@link org.springframework.data.jpa.repository.Lock @Lock} on JPA query method
     * to generate "select for update" SQL is also a viable solution)
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void serializableTransaction(Runnable runnable) {
        runnable.run();
    }

    /**
     * Wrap {@link Transactional} with {@link Retryable}
     * <p>
     * serializable isolation could throw {@link org.springframework.dao.CannotAcquireLockException CannotAcquireLockException}
     * (extended from {@link PessimisticLockingFailureException})
     */
    @Retryable(retryFor = PessimisticLockingFailureException.class)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void serializableTransaction_wrappedByRetry(Runnable runnable) {
        serializableTransaction(runnable);
    }
}
