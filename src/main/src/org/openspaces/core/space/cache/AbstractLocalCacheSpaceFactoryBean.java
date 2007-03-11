package org.openspaces.core.space.cache;

import com.j_spaces.core.IJSpace;
import com.j_spaces.core.client.FinderException;
import com.j_spaces.core.client.SpaceFinder;
import com.j_spaces.core.client.SpaceURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.core.space.CannotCreateSpaceException;
import org.openspaces.core.util.SpaceUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Properties;

/**
 * <p>Base class for different Local cache space proxies that work with a master
 * {@link com.j_spaces.core.IJSpace IJSpace}. The master is set using {@link #setSpace(com.j_spaces.core.IJSpace)}.
 * This factory represents an {@link com.j_spaces.core.IJSpace IJSpace} that is the
 * local cache proxy on top of the master space.
 *
 * <p>A clustered flag allows to control if this GigaSpace instance will work against
 * a clustered view of the space or directly against a clustered memeber.  By default
 * if this flag is not set it will be set automatically by this factory. It will be set
 * to <code>true</code> if the space is an embedded one. It will be set to <code>false</code>
 * otherwise (i.e. the space is not an embedded space).
 *
 * @author kimchy
 */
public abstract class AbstractLocalCacheSpaceFactoryBean implements InitializingBean, FactoryBean, BeanNameAware {

    protected Log logger = LogFactory.getLog(this.getClass());

    private IJSpace space;

    private Boolean clustered;

    private Properties properties;

    
    private String beanName;

    private IJSpace localCacheSpace;

    /**
     * Sets the master space that a local cache will be built on top.
     */
    public void setSpace(IJSpace space) {
        this.space = space;
    }

    /**
     * <p>A clustered flag allows to control if this GigaSpace instance will work against
     * a clustered view of the space or directly against a clustered memeber. By default
     * if this flag is not set it will be set automatically by this factory. It will be
     * set to <code>true</code> if the space is an embedded one. It will be set to
     * <code>false</code> otherwise (i.e. the space is not an embedded space).
     */
    public void setClustered(boolean clustered) {
        this.clustered = clustered;
    }

    /**
     * Sets additional properties for the local cache.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Spring callback that sets the bean name.
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * <p>Constructs a new local cache {@link com.j_spaces.core.IJSpace IJSpace} based
     * on the master local cache set using {@link #setSpace(com.j_spaces.core.IJSpace)}
     * and a set of properties driving the actual local cache type based on
     * {@link #createCacheProeprties()}. Additional properties are applied based on
     * {@link #setProperties(java.util.Properties)}.
     *
     * <p>A clustered flag allows to control if this GigaSpace instance will work against
     * a clustered view of the space or directly against a clustered memeber. By default
     * if this flag is not set it will be set automatically by this factory. It will be
     * set to <code>true</code> if the space is an embedded one. It will be set to
     * <code>false</code> otherwise (i.e. the space is not an embedded space).
     *
     * @see com.j_spaces.core.client.SpaceFinder#find(com.j_spaces.core.client.SpaceURL,com.j_spaces.core.IJSpace,com.sun.jini.start.LifeCycle)
     */
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(space, "space property must be set");
        IJSpace actualSpace = space;
        if (clustered == null) {
            clustered = !space.isEmbedded();
            if (logger.isDebugEnabled()) {
                logger.debug("Clustered flag automatically set to [" + clustered + "] for bean [" + beanName + "]");
            }
        }
        if (!clustered) {
            actualSpace = SpaceUtils.getClusterMemberSpace(actualSpace, true);
        }
        Properties props = createCacheProeprties();
        props.put(SpaceURL.USE_LOCAL_CACHE, "true");
        if (properties != null) {
            props.putAll(properties);
        }

        SpaceURL spaceUrl = (SpaceURL) space.getURL().clone();
        spaceUrl.putAll(props);
        spaceUrl.getCustomProperties().putAll(props);
        try {
            localCacheSpace = (IJSpace) SpaceFinder.find(spaceUrl, actualSpace, null);
        } catch (FinderException e) {
            throw new CannotCreateSpaceException("Failed to create local cache space for space [" + space + "]", e);
        }
    }

    /**
     * Subclasses should implement this mehtod to return the properties relevant for the local
     * concrete local cache implementation.
     */
    protected abstract Properties createCacheProeprties();

    /**
     * Returns an {@link com.j_spaces.core.IJSpace IJSpace} that is the local cache
     * wrapping the master proxy set using {@link #setSpace(com.j_spaces.core.IJSpace)}.
     */
    public Object getObject() throws Exception {
        return this.localCacheSpace;
    }

    /**
     * Returns the type of the factory object.
     */
    public Class getObjectType() {
        return (localCacheSpace == null ? IJSpace.class : localCacheSpace.getClass());
    }

    /**
     * Returns <code>true</code> since this bean is a singleton.
     */
    public boolean isSingleton() {
        return true;
    }
}
