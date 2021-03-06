/*******************************************************************************
 * 
 * Copyright (c) 2012 GigaSpaces Technologies Ltd. All rights reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 ******************************************************************************/
package org.openspaces.admin.internal.pu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openspaces.admin.internal.admin.InternalAdmin;
import org.openspaces.admin.internal.pu.events.DefaultOrphanProcessingUnitInstanceLifecycleEventManager;
import org.openspaces.admin.internal.pu.events.DefaultProcessingUnitInstanceAddedEventManager;
import org.openspaces.admin.internal.pu.events.DefaultProcessingUnitInstanceRemovedEventManager;
import org.openspaces.admin.internal.pu.events.InternalOrphanProcessingUnitInstanceLifecycleEventListener;
import org.openspaces.admin.internal.pu.events.InternalOrphanProcessingUnitInstanceLifecycleEventManager;
import org.openspaces.admin.internal.pu.events.InternalProcessingUnitInstanceAddedEventManager;
import org.openspaces.admin.internal.pu.events.InternalProcessingUnitInstanceRemovedEventManager;
import org.openspaces.admin.pu.ProcessingUnitInstance;
import org.openspaces.admin.pu.events.ProcessingUnitInstanceAddedEventManager;
import org.openspaces.admin.pu.events.ProcessingUnitInstanceLifecycleEventListener;
import org.openspaces.admin.pu.events.ProcessingUnitInstanceRemovedEventManager;

/**
 * @author kimchy
 */
public class DefaultProcessingUnitInstances implements InternalProcessingUnitInstances {

    private static final Log logger = LogFactory.getLog(DefaultProcessingUnitInstances.class);

    private final InternalAdmin admin;

    private final Map<String, ProcessingUnitInstance> orphanedProcessingUnitInstances = new ConcurrentHashMap<String, ProcessingUnitInstance>();

    private final Map<String, ProcessingUnitInstance> processingUnitInstances = new ConcurrentHashMap<String, ProcessingUnitInstance>();

    private final InternalProcessingUnitInstanceAddedEventManager processingUnitInstanceAddedEventManager;

    private final InternalProcessingUnitInstanceRemovedEventManager processingUnitInstanceRemovedEventManager;
    
    private final InternalOrphanProcessingUnitInstanceLifecycleEventManager orphanedProcessingUnitInstanceLifecycleEventManager;

    public DefaultProcessingUnitInstances(InternalAdmin admin) {
        this.admin = admin;
        this.processingUnitInstanceAddedEventManager = new DefaultProcessingUnitInstanceAddedEventManager(this, admin);
        this.processingUnitInstanceRemovedEventManager = new DefaultProcessingUnitInstanceRemovedEventManager(admin);
        this.orphanedProcessingUnitInstanceLifecycleEventManager = new DefaultOrphanProcessingUnitInstanceLifecycleEventManager(this, admin);
    }

    public boolean contains(ProcessingUnitInstance processingUnitInstance) {
        for (ProcessingUnitInstance it : processingUnitInstances.values()) {
            if (it.getUid().equals(processingUnitInstance.getUid())) {
                return true;
            }
        }
        return false;
    }

    public void addOrphaned(ProcessingUnitInstance orphanProcessingUnitInstance) {
        assertStateChangesPermitted();
        ProcessingUnitInstance existingPU = orphanedProcessingUnitInstances.put(orphanProcessingUnitInstance.getUid(), orphanProcessingUnitInstance);
        
        if (existingPU == null) {
        	orphanedProcessingUnitInstanceLifecycleEventManager.orphanProcessingUnitInstanceAdded(orphanProcessingUnitInstance);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("Orphan " + orphanProcessingUnitInstance.getProcessingUnitInstanceName() + " with id ["+ orphanProcessingUnitInstance.getUid() + "] has already been added. No event is called.");
            }
        }
    }

    public ProcessingUnitInstance removeOrphaned(String uid) {
        assertStateChangesPermitted();
        final ProcessingUnitInstance orphanProcessingUnitInstance = orphanedProcessingUnitInstances.remove(uid);
        if (orphanProcessingUnitInstance != null) {
        	orphanedProcessingUnitInstanceLifecycleEventManager.orphanProcessingUnitInstanceRemoved(orphanProcessingUnitInstance);
        }
        
        return orphanProcessingUnitInstance;
    }

    public void addInstance(ProcessingUnitInstance processingUnitInstance) {
        assertStateChangesPermitted();
        ProcessingUnitInstance existingPU = processingUnitInstances.put(processingUnitInstance.getUid(), processingUnitInstance);
        if (existingPU == null) {
            processingUnitInstanceAddedEventManager.processingUnitInstanceAdded(processingUnitInstance);
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug(processingUnitInstance.getProcessingUnitInstanceName() + " with id ["+ processingUnitInstance.getUid() + "] has already been added. No event is called.");
            }
        }
    }

    public ProcessingUnitInstance removeInstance(String uid) {
        assertStateChangesPermitted();
        ProcessingUnitInstance processingUnitInstance = processingUnitInstances.remove(uid);
        if (processingUnitInstance != null) {
            processingUnitInstanceRemovedEventManager.processingUnitInstanceRemoved(processingUnitInstance);
        }
        return processingUnitInstance;
    }

    public Iterator<ProcessingUnitInstance> getInstancesIt() {
        return Collections.unmodifiableCollection(processingUnitInstances.values()).iterator();
    }

    public ProcessingUnitInstance[] getProcessingUnitInstances() {
        return getInstances();
    }

    public ProcessingUnitInstance[] getInstances() {
        return processingUnitInstances.values().toArray(new ProcessingUnitInstance[0]);
    }

    public ProcessingUnitInstance[] getInstances(String processingUnitName) {
        ArrayList<ProcessingUnitInstance> retVal = new ArrayList<ProcessingUnitInstance>();
        for (ProcessingUnitInstance instance : processingUnitInstances.values()) {
            if (instance.getName().equals(processingUnitName)) {
                retVal.add(instance);
            }
        }
        return retVal.toArray(new ProcessingUnitInstance[retVal.size()]);
    }

    @Override
    public ProcessingUnitInstanceAddedEventManager getProcessingUnitInstanceAdded() {
        return this.processingUnitInstanceAddedEventManager;
    }

    @Override
    public ProcessingUnitInstanceRemovedEventManager getProcessingUnitInstanceRemoved() {
        return this.processingUnitInstanceRemovedEventManager;
    }
    
    @Override
    public void addProcessingUnitInstanceLifecycleEventListener(ProcessingUnitInstanceLifecycleEventListener eventListener) {
        processingUnitInstanceAddedEventManager.add(eventListener);
        processingUnitInstanceRemovedEventManager.add(eventListener);
    }

    @Override
    public void removeProcessingUnitInstanceLifecycleEventListener(ProcessingUnitInstanceLifecycleEventListener eventListener) {
        processingUnitInstanceAddedEventManager.remove(eventListener);
        processingUnitInstanceRemovedEventManager.remove(eventListener);
    }
    

    private void assertStateChangesPermitted() {
        admin.assertStateChangesPermitted();
    }

    public ProcessingUnitInstance getInstanceByUID(String processingUnitInstanceUid) {
        return processingUnitInstances.get(processingUnitInstanceUid);
    }

	@Override
	public ProcessingUnitInstance[] getOrphanProcessingUnitInstances() {
		return orphanedProcessingUnitInstances.values().toArray(new ProcessingUnitInstance[0]);
	}

	@Override
	public void addOrphanProcessingUnitInstanceLifecycleEventListener(
			InternalOrphanProcessingUnitInstanceLifecycleEventListener eventListener) {
		orphanedProcessingUnitInstanceLifecycleEventManager.add(eventListener);
	}

	@Override
	public void removeOrphanProcessingUnitInstanceLifecycleEventListener(
			InternalOrphanProcessingUnitInstanceLifecycleEventListener eventListener) {
		orphanedProcessingUnitInstanceLifecycleEventManager.remove(eventListener);
	}
}
