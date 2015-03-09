/*
 * Copyright 2006-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openspaces.pu.container.spi;

import org.openspaces.admin.quiesce.QuiesceStateChangedListener;
import org.openspaces.core.cluster.MemberAliveIndicator;
import org.openspaces.core.cluster.ProcessingUnitUndeployingListener;
import org.openspaces.pu.container.ProcessingUnitContainer;
import org.openspaces.pu.service.ServiceDetailsProvider;
import org.openspaces.pu.service.ServiceMetricProvider;
import org.openspaces.pu.service.ServiceMonitorsProvider;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Collections;

/**
 * A processing unit container that is based on Spring {@link ApplicationContext}.
 * 
 * @author kimchy
 */
public abstract class ApplicationContextProcessingUnitContainer extends ProcessingUnitContainer {

    public abstract ApplicationContext getApplicationContext();

    @Override
    public Collection<ServiceMetricProvider> getServiceMetricProviders() {
        return getBeansOfType(ServiceMetricProvider.class);
    }

    @Override
    public Collection<ServiceDetailsProvider> getServiceDetailsProviders() {
        return getBeansOfType(ServiceDetailsProvider.class);
    }

    @Override
    public Collection<ServiceMonitorsProvider> getServiceMonitorsProviders() {
        return getBeansOfType(ServiceMonitorsProvider.class);
    }

    @Override
    public Collection<QuiesceStateChangedListener> getQuiesceStateChangedListeners() {
        return getBeansOfType(QuiesceStateChangedListener.class);
    }

    @Override
    public Collection<ProcessingUnitUndeployingListener> getUndeployListeners() {
        return getBeansOfType(ProcessingUnitUndeployingListener.class);
    }

    @Override
    public Collection<MemberAliveIndicator> getMemberAliveIndicators() {
        return getBeansOfType(MemberAliveIndicator.class);
    }

    private <T> Collection<T> getBeansOfType(Class<T> type) {
        ApplicationContext applicationContext = getApplicationContext();
        return applicationContext != null
                ? applicationContext.getBeansOfType(type).values()
                : Collections.EMPTY_LIST;
    }
}
