package org.openspaces.admin.internal.support;

import org.openspaces.admin.GridComponent;
import org.openspaces.admin.internal.machine.InternalMachineAware;
import org.openspaces.admin.internal.os.InternalOperatingSystemInfoProvider;
import org.openspaces.admin.internal.transport.InternalTransportInfoProvider;
import org.openspaces.admin.internal.vm.InternalVirtualMachineInfoProvider;

/**
 * @author kimchy
 */
public interface InternalGridComponent extends GridComponent, InternalMachineAware,
        InternalTransportInfoProvider, InternalOperatingSystemInfoProvider, InternalVirtualMachineInfoProvider {
}
