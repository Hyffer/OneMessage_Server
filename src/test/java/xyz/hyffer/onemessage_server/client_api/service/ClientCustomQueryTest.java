package xyz.hyffer.onemessage_server.client_api.service;

import io.hypersistence.utils.hibernate.query.SQLExtractor;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.ComparisonOperator;
import org.hibernate.query.sqm.tree.domain.SqmBasicValuedSimplePath;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.query.sqm.tree.expression.ValueBindJpaCriteriaParameter;
import org.hibernate.query.sqm.tree.predicate.SqmComparisonPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmInListPredicate;
import org.hibernate.query.sqm.tree.predicate.SqmJunctionPredicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logicng.formulas.CType;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.StringUtils;
import xyz.hyffer.onemessage_server.model.Contact;
import xyz.hyffer.onemessage_server.model.Contact_;
import xyz.hyffer.onemessage_server.model.Message;
import xyz.hyffer.onemessage_server.model.Message_;
import xyz.hyffer.onemessage_server.storage.ContactRepository;
import xyz.hyffer.onemessage_server.storage.MessageRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link ClientCustomQuery}
 * <p>
 * This is a unit test checking whether {@link ClientCustomQuery} can build query correctly.
 * However, to get JPA Metamodel generator working, and to get `entityManager` for testing,
 * it is launched with `@DataJpaTest` annotation.
 */
@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClientCustomQueryTest {

    @Resource
    EntityManager entityManager;

    ClientCustomQuery clientCustomQuery;

    ContactRepository contactRepository;
    MessageRepository messageRepository;

    @BeforeEach
    void setupMock() {
        contactRepository = mock(ContactRepository.class);
        given(contactRepository.findAll(ArgumentMatchers.<Specification<Contact>>any(), any(Pageable.class)))
                .willReturn(Page.empty());

        messageRepository = mock(MessageRepository.class);
        given(messageRepository.findAll(ArgumentMatchers.<Specification<Message>>any(), any(Pageable.class)))
                .willReturn(Page.empty());

        clientCustomQuery = new ClientCustomQuery(contactRepository, messageRepository);
    }

    FormulaFactory ff = new FormulaFactory();
    PseudoBooleanParser pp = new PseudoBooleanParser(ff);

    /**
     * Construct a {@link Formula} from given {@link Predicate}
     *
     * @param predicate generated by {@link Specification#toPredicate(Root, CriteriaQuery, CriteriaBuilder)}
     * @return pseudo boolean formula
     */
    Formula constructFromPredicate(Predicate predicate) {
        if (predicate instanceof SqmJunctionPredicate p) {
            // compound predicate
            Formula lhs = constructFromPredicate(p.getPredicates().get(0));
            Formula rhs = constructFromPredicate(p.getPredicates().get(1));
            String op = p.getOperator().name().toLowerCase();   // "and" or "or"
            return ReflectionTestUtils.invokeMethod(ff, op, (Object) new Formula[]{lhs, rhs});

        } else if (predicate instanceof SqmComparisonPredicate p) {
            // simple predicate
            CType op;
            boolean negated = p.isNegated();
            switch (p.getSqmOperator()) {
                case EQUAL -> op = CType.EQ;
                case NOT_EQUAL -> {
                    op = CType.EQ;
                    negated = !negated;
                }
                case GREATER_THAN -> op = CType.GT;
                case GREATER_THAN_OR_EQUAL -> op = CType.GE;
                case LESS_THAN -> op = CType.LT;
                case LESS_THAN_OR_EQUAL -> op = CType.LE;
                default -> throw new RuntimeException("Not implement yet");
            }
            SqmBasicValuedSimplePath<?> lhs;
            ValueBindJpaCriteriaParameter<?> rhs;
            if (p.getLeftHandExpression() instanceof SqmBasicValuedSimplePath<?> l
                    && p.getRightHandExpression() instanceof ValueBindJpaCriteriaParameter<?> r) {
                lhs = l;
                rhs = r;
            } else if (p.getLeftHandExpression() instanceof ValueBindJpaCriteriaParameter<?> l
                    && p.getRightHandExpression() instanceof SqmBasicValuedSimplePath<?> r) {
                // swap lhs and rhs
                lhs = r;
                rhs = l;
                Map<CType, CType> reverse = Map.of(
                        CType.GT, CType.LT,
                        CType.GE, CType.LE,
                        CType.LT, CType.GT,
                        CType.LE, CType.GE
                );
                if (reverse.containsKey(op))
                    op = reverse.get(op);
            } else {
                throw new RuntimeException("Not implement yet");
            }

            Formula formula;
            // field name
            String variable = lhs.getNavigablePath().getLocalName();
            // handle different value types
            if (rhs.getValue() instanceof Integer value) {
                formula = ff.cc(op, value, ff.variable(variable));
            } else if (rhs.getValue() instanceof Boolean value) {
                assert op == CType.EQ;
                formula = ff.literal(variable, value);
            } else {
                throw new RuntimeException("Not implement yet");
            }
            if (negated)
                formula = ff.not(formula);
            return formula;

        } else if (predicate instanceof SqmInListPredicate<?> p) {
            // variable in (value1, value2, ...)
            SqmExpression<?> lhs = p.getTestExpression();
            List<? extends SqmExpression<?>> rhs_l = p.getListExpressions();

            // break down into disjunction of equations
            List<Formula> formulas = new LinkedList<>();
            for (SqmExpression<?> rhs : rhs_l) {
                formulas.add(constructFromPredicate(new SqmComparisonPredicate(
                        lhs,
                        ComparisonOperator.EQUAL,
                        rhs,
                        p.nodeBuilder()
                )));
            }
            return ff.or(formulas);

        } else if (predicate == null) {
            // empty predicate
            return ff.constant(true);

        } else {
            throw new RuntimeException("Not implement yet");
        }
    }

    /**
     * Check SQL string
     */
    interface SQLChecker {
        void check(String sql);
    }

    /**
     * Check whether correct parameters are passed to {@link JpaSpecificationExecutor#findAll(Specification, Pageable)}.
     *
     * @param repository repository to be checked
     * @param f          a formula string representing the expected predicate.
     *                   null means do not parse and check logic formula
     * @param checker    functor to check SQL generated by the specification
     * @param limit      expected page size, must be positive.
     *                   or null representing no paging
     * @param sort       expected sort order. {@link Sort#unsorted()} or null representing no sorting
     * @param <T>        entity type
     */
    <T> void checkParams(JpaSpecificationExecutor<T> repository,
                         String f, SQLChecker checker, Integer limit, Sort sort) {
        // get entity class
        @SuppressWarnings("unchecked")
        Class<T> Tclass = (Class<T>) GenericTypeResolver.resolveTypeArgument(repository.getClass(), JpaSpecificationExecutor.class);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Specification<T>> arg1 = ArgumentCaptor.forClass(Specification.class);
        ArgumentCaptor<Pageable> arg2 = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(arg1.capture(), arg2.capture());
        Specification<T> specification = arg1.getValue();
        Pageable pageable = arg2.getValue();

        // check specification
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(Tclass);
        Root<T> root = query.from(Tclass);
        Predicate predicate = specification.toPredicate(root, query, builder);

        // construct formula from specification, and compare with expected
        if (f != null) {
            Formula formula = constructFromPredicate(predicate);
            Formula formula_expected;
            try {
                formula_expected = pp.parse(f);
            } catch (ParserException e) {
                throw new RuntimeException(e);
            }
            assertThat(formula).isEqualTo(formula_expected);
        } else {
            log.warn("Skip checking logic formula.");
        }

        // check specification by generated SQL
        if (checker != null) {
            query.where(predicate);
            TypedQuery<T> q = entityManager.createQuery(query);
            String sql = SQLExtractor.from(q);
            checker.check(sql);
        }

        // check pageable
        assert limit == null || limit > 0;
        if (limit == null) {
            assertTrue(pageable.isUnpaged());
        } else {
            assertThat(pageable.getPageSize()).isEqualTo(limit);
            assertThat(pageable.getPageNumber()).isEqualTo(0);
        }

        if (sort == null) {
            assertTrue(pageable.getSort().isUnsorted());
        } else {
            assertThat(pageable.getSort()).isEqualTo(sort);
        }
    }

    @Test
    void catchupContacts_case1() {
        clientCustomQuery.catchupContacts(35, null, null, null, 15);
        checkParams(
                contactRepository,
                "_CID > 35",
                null,
                15,
                Sort.by(Sort.Direction.ASC, Contact_._CID.getName())
        );
    }

    @Test
    void catchupContacts_case2() {
        clientCustomQuery.catchupContacts(null, 70, 12, null, 10);
        checkParams(
                contactRepository,
                "_CID <= 70 & changeOrder > 12",
                null,
                10,
                Sort.by(Sort.Direction.ASC, Contact_.changeOrder.getName())
        );
    }

    @Test
    void catchupContacts_case3() {
        clientCustomQuery.catchupContacts(null, null, null, 1, 0);
        checkParams(
                contactRepository,
                "stateOrder > 1",
                null,
                null,
                Sort.by(Sort.Direction.ASC, Contact_.stateOrder.getName())
        );
    }

    @Test
    void catchupContacts_case4() {
        clientCustomQuery.catchupContacts(15, 20, 1, 1, 0);
        checkParams(
                contactRepository,
                "_CID > 15 & _CID <= 20 & (changeOrder > 1 | stateOrder > 1)",
                sql -> {
                    System.out.println(sql);
                    assertTrue(sql.contains("(") && sql.contains(")"));
                    String substring = sql.substring(sql.indexOf('(') + 1, sql.lastIndexOf(')'));
                    assertThat(StringUtils.countOccurrencesOf(substring, " or ")).isEqualTo(1);
                    assertThat(StringUtils.countOccurrencesOf(substring, " and ")).isEqualTo(0);
                },
                null,
                Sort.unsorted()
        );
    }

    @Test
    void catchupMessages_case1() {
        clientCustomQuery.catchupMessages(null, null, null, null, 10);
        checkParams(
                messageRepository,
                "",
                null,
                10,
                Sort.by(Sort.Direction.ASC, Message_._MID.getName())
        );
    }

    @Test
    void catchupMessages_case2() {
        clientCustomQuery.catchupMessages(null, null, 80, null, 1);
        checkParams(
                messageRepository,
                "rank > 80",
                null,
                1,
                Sort.by(Sort.Direction.ASC, Message_.rank.getName())
        );
    }

    @Test
    void catchupMessages_case3() {
        clientCustomQuery.catchupMessages(30, 39, null, 5, 99);
        checkParams(
                messageRepository,
                "_MID > 30 & _MID <= 39 & contentOrder > 5",
                null,
                99,
                Sort.by(Sort.Direction.ASC, Message_.contentOrder.getName())
        );
    }

    @Test
    void catchupMessages_case4() {
        clientCustomQuery.catchupMessages(30, 20, 5, 6, 0);
        checkParams(
                messageRepository,
                "_MID > 30 & _MID <= 20 & (rank > 5 | contentOrder > 6)",
                sql -> {
                    System.out.println(sql);
                    assertTrue(sql.contains("(") && sql.contains(")"));
                    String substring = sql.substring(sql.indexOf('(') + 1, sql.lastIndexOf(')'));
                    assertThat(StringUtils.countOccurrencesOf(substring, " or ")).isEqualTo(1);
                    assertThat(StringUtils.countOccurrencesOf(substring, " and ")).isEqualTo(0);
                },
                null,
                null
        );
    }

    @Test
    void getContacts_1() {
        clientCustomQuery.getContacts(true, 10, 5);
        checkParams(
                contactRepository,
                "pinned & lastMsgRank < 10",
                null,
                5,
                Sort.by(Sort.Direction.DESC, Contact_.lastMsgRank.getName())
        );
    }

    @Test
    void getContacts_2() {
        clientCustomQuery.getContacts(false, null, 0);
        checkParams(
                contactRepository,
                "~pinned",
                null,
                null,
                Sort.by(Sort.Direction.DESC, Contact_.lastMsgRank.getName())
        );
    }

    @Test
    void searchContacts_1() {
        clientCustomQuery.searchContacts("Bob", 5);
        checkParams(
                contactRepository,
                null,
                sql -> {
                    System.out.println(sql);
                    assertTrue(sql.contains("remark like"));
                },
                5,
                null
        );
    }

    @Test
    void getMessages_1() {
        clientCustomQuery.getMessages(Set.of(1, 2, 3), 10, 5);
        checkParams(
                messageRepository,
                "(_CiID = 1 | _CiID = 2 | _CiID = 3) & rank < 10",
                sql -> {
                    System.out.println(sql);
                    assertTrue(sql.contains("_ciid in"));
                },
                5,
                Sort.by(Sort.Direction.DESC, Message_.rank.getName())
        );
    }
}
