package xyz.hyffer.onemessage_server.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Superclass of storage tests
 * <p>
 * Load {@link PopulateData PopulateData}, which initiate database with test data
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PopulateData.class)
public class RepositoryTest {

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    ContactInstanceRepository instanceRepository;
    @Autowired
    MessageRepository messageRepository;

    /**
     * `data.contacts` and `data.messages` are contacts and messages stored in database
     */
    @Autowired
    PopulateData data;
}
