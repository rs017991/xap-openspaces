package org.openspaces.core.gateway;

import java.util.List;

import net.jini.core.discovery.LookupLocator;

import com.gigaspaces.internal.cluster.node.impl.gateway.lus.ReplicationLookupParameters;

/**
 * A gateway lookups factory bean for constructing {@link ReplicationLookupParameters} using
 * the {@link #asReplicationLookupParameters} method. 
 * 
 * @author Idan Moyal
 * @since 8.0.3
 *
 */
public class GatewayLookupsFactoryBean {

    private String lookupGroup;
    private List<GatewayLookup> gatewayLookups;

    public GatewayLookupsFactoryBean() {
    }
    public GatewayLookupsFactoryBean(String lookupGroup) {
        this.lookupGroup = lookupGroup;
    }

    /**
     * @return The gateway's lookup group.
     */
    public String getLookupGroup() {
        return lookupGroup;
    }
    
    /**
     * Sets the gateway's lookup group.
     * @param lookupGroup The lookup group.
     */
    public void setLookupGroup(String lookupGroup) {
        this.lookupGroup = lookupGroup;
    }
    
    /**
     * @return The gateway's associated lookups configuration as a {@link GatewayLookup}s array.
     */
    public List<GatewayLookup> getGatewayLookups() {
        return gatewayLookups;
    }
    
    /**
     * Sets the gateway's lookups configuration.
     * @param gatewayLookups The lookups configuration.
     */
    public void setGatewayLookups(List<GatewayLookup> gatewayLookups) {
        this.gatewayLookups = gatewayLookups;
    }
    
    /**
     * @return A new {@link ReplicationLookupParameters} instance constructed of the bean's properties. 
     */
    public ReplicationLookupParameters asReplicationLookupParameters() {
        ReplicationLookupParameters parameters = new ReplicationLookupParameters();
        parameters.setLookupGroups(new String[] { lookupGroup });
        if (gatewayLookups != null) {
            LookupLocator[] locators = new LookupLocator[gatewayLookups.size()];
            for (int i = 0; i < gatewayLookups.size(); i++) {
                GatewayLookup gatewayLookup = gatewayLookups.get(i);
                locators[i] = new LookupLocator(gatewayLookup.getHost(), Integer.valueOf(gatewayLookup.getDiscoveryPort()));
            }
            parameters.setLookupLocators(locators);
        }
        return parameters;
    }
    
}
