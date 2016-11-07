package org.fenixedu.bennu.core.groups;

import java.util.Collections;
import java.util.Set;

import org.fenixedu.bennu.core.annotation.GroupOperator;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.PersistentGroup;
import org.fenixedu.bennu.core.domain.groups.PersistentGroupStrategy;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link GroupStrategy} is a stateless (pure-logic) {@link CustomGroup}, which is identified solely by its class.
 * 
 * Implementation note: GroupStrategy fails gracefully if the concrete strategy is removed: it issues a warning and returns a
 * strategy that behaves the same as {@link NobodyGroup}.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
public abstract class GroupStrategy extends CustomGroup {

    private static final Logger logger = LoggerFactory.getLogger(GroupStrategy.class);

    private static final long serialVersionUID = -6389394974941030980L;

    /**
     * Retrieves a {@link GroupStrategy} by the name of its type.
     * 
     * If the given type is no longer available (or the typeName is invalid), it returns a strategy that behaves the same as
     * {@link NobodyGroup}.
     */
    public static GroupStrategy strategyForType(String typeName) {
        try {
            return (GroupStrategy) Class.forName(typeName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e) {
            logger.error("Exception de-serializing GroupStrategy '" + typeName + "'. Returning NobodyGroup.", e);
            return new NobodyGroupStrategy();
        }
    }

    @GroupOperator("nobody-strategy")
    private static final class NobodyGroupStrategy extends GroupStrategy {
        private static final long serialVersionUID = 584606595093061522L;

        @Override
        public String getPresentationName() {
            return BundleUtil.getString("resources.BennuResources", "label.bennu.group.nobody");
        }

        @Override
        public Set<User> getMembers() {
            return Collections.emptySet();
        }

        @Override
        public Set<User> getMembers(DateTime when) {
            return Collections.emptySet();
        }

        @Override
        public boolean isMember(User user) {
            return false;
        }

        @Override
        public boolean isMember(User user, DateTime when) {
            return false;
        }
    }

    @Override
    public final PersistentGroup toPersistentGroup() {
        return PersistentGroupStrategy.getInstance(this);
    }

    /*
     * Ensure that equality is based on class identity, not on state.
     */
    @Override
    public final boolean equals(Object other) {
        return other == null ? false : getClass().equals(other.getClass());
    }

    @Override
    public final int hashCode() {
        return this.getClass().hashCode();
    }

    /*
     * Return the className as the externalization. This ensures that no state is ever kept on the ValueType.
     */
    public final String externalize() {
        return getClass().getName();
    }

}
