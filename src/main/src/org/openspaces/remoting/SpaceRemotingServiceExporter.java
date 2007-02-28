package org.openspaces.remoting;

import org.openspaces.core.GigaSpace;
import org.openspaces.events.EventTemplateProvider;
import org.openspaces.events.SpaceDataEventListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author kimchy
 */
public class SpaceRemotingServiceExporter implements SpaceDataEventListener, InitializingBean, EventTemplateProvider {

    private List services;


    private Map interfaceToService = new HashMap();

    public void setServices(List services) {
        this.services = services;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(services, "services property is required");
        // go over the services and create the inteface to service lookup
        for (Iterator it = services.iterator(); it.hasNext();) {
            Object service = it.next();
            Class[] interfaces = ClassUtils.getAllInterfaces(service);
            for (int j = 0; j < interfaces.length; j++) {
                interfaceToService.put(interfaces[j].getName(), service);
            }
        }
    }

    public Object getTemplate() {
        return new SpaceRemoteInvocation();
    }

    public void onEvent(Object data, GigaSpace gigaSpace, Object source) {
        SpaceRemoteInvocation remoteInvocation = (SpaceRemoteInvocation) data;

        Object service = interfaceToService.get(remoteInvocation.getLookupName());
        if (service == null) {
            gigaSpace.write(new SpaceRemoteResult(remoteInvocation, new ServiceNotFoundSpaceRemotingException(remoteInvocation.getLookupName())));
            return;
        }

        // TODO could so some caching of the method invoker for better performance
        Object[] arguments = remoteInvocation.getArguments();
        if (arguments == null) {
            arguments = new Object[0];
        }
        Class[] argumentTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentTypes[i] = (arguments[i] != null ? arguments[i].getClass() : Object.class);
        }

        Method method;
        try {
            method = service.getClass().getMethod(remoteInvocation.getMethodName(), argumentTypes);
        } catch (Exception e) {
            gigaSpace.write(new SpaceRemoteResult(remoteInvocation, new ServiceMethodNotFoundSpaceRemotingException(remoteInvocation.getMethodName(), e)));
            return;
        }
        try {
            Object retVal = method.invoke(service, arguments);
            gigaSpace.write(new SpaceRemoteResult(remoteInvocation, retVal));
        } catch (InvocationTargetException e) {
            gigaSpace.write(new SpaceRemoteResult(remoteInvocation, e.getTargetException()));
        } catch (IllegalAccessException e) {
            gigaSpace.write(new SpaceRemoteResult(remoteInvocation, new SpaceRemotingException("Failed to access method", e)));
        }
    }
}
