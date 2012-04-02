package org.openspaces.admin.pu.statistics;

import java.util.HashMap;
import java.util.Map;

import org.openspaces.admin.internal.pu.statistics.StatisticsObjectList;
import org.openspaces.admin.internal.pu.statistics.StatisticsObjectListFunction;

/**
 * Calculates the throughput of all cluster instances values, 
 * by taking total request count and dividing by time
 * 
 * @since 9.0.0
 * @author itaif, gal
 * 
 */
public class ThroughputInstancesStatisticsConfig 
    extends AbstractTimeWindowStatisticsConfig 
    implements StatisticsObjectListFunction, InstancesStatisticsConfig {

    public ThroughputInstancesStatisticsConfig() {
        this(new HashMap<String, String>());
    }

    public ThroughputInstancesStatisticsConfig(Map<String, String> properties) {
        super(properties);
    }

    @Override
    public void validate() throws IllegalStateException {
        // ok
    }

    @Override
    public Object calc(StatisticsObjectList values) {
        return values.getDeltaValuePerSecond();
    }
}
