package org.openspaces.admin.space;

/**
 * @author kimchy
 */
public interface SpaceInstanceStatistics {

    boolean isNA();

    long getTimestamp();

    long getPreviousTimestamp();

    SpaceInstanceStatistics getPrevious();

    long getWriteCount();

    double getWritePerSecond();

    long getReadCount();

    double getReadPerSecond();

    long getTakeCount();

    double getTakePerSecond();

    long getNotifyRegistrationCount();

    double getNotifyRegistrationPerSecond();

    long getCleanCount();

    double getCleanPerSecond();

    long getUpdateCount();

    double getUpdatePerSecond();

    long getNotifyTriggerCount();

    double getNotifyTriggerPerSecond();

    long getNotifyAckCount();

    double getNotifyAckPerSecond();

    long getExecuteCount();

    double getExecutePerSecond();

    /**
     * Remove happens when an entry is removed due to lease expiration or lease cancel.
     */
    long getRemoveCount();

    double getRemovePerSecond();
}
