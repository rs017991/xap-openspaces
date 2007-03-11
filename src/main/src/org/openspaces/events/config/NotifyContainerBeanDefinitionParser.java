package org.openspaces.events.config;

import org.openspaces.events.notify.SimpleNotifyEventListenerContainer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author kimchy
 */
public class NotifyContainerBeanDefinitionParser extends AbstarctTxEventContainerBeanDefinitionParser {

    private static final String TEMPLATE = "template";

    private static final String SQL_QUERY = "sql-query";

    private static final String COM_TYPE = "com-type";

    private static final String FIFO = "fifo";

    private static final String TRIGGER_NOTIFY_TEMPLATE = "trigger-notify-template";

    private static final String REPLICATE_NOTIFY_TEMPLATE = "replicate-notify-template";

    private static final String PERFORM_TAKE_ON_NOTIFY = "perform-take-on-notify";

    private static final String BATCH = "batch";

    private static final String BATCH_TIME = "time";

    private static final String BATCH_SIZE = "size";

    private static final String LEASE = "lease";

    private static final String LEASE_AUTO_RENEW = "auto-renew";

    private static final String LEASE_TIMEOUT = "timeout";

    private static final String NOTIFY = "notify";

    protected Class getBeanClass(Element element) {
        return SimpleNotifyEventListenerContainer.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        super.doParse(element, parserContext, builder);

        Element templateEle = DomUtils.getChildElementByTagName(element, TEMPLATE);
        if (templateEle != null) {
            Object template = parserContext.getDelegate().parsePropertyValue(templateEle, builder.getRawBeanDefinition(), "template");
            builder.addPropertyValue("template", template);
        }
        Element sqlQueryEle = DomUtils.getChildElementByTagName(element, SQL_QUERY);
        if (sqlQueryEle != null) {
            builder.addPropertyValue("template", parserContext.getDelegate().parsePropertySubElement(sqlQueryEle, builder.getRawBeanDefinition(), null));
        }

        Element notifyEle = DomUtils.getChildElementByTagName(element, NOTIFY);
        if (notifyEle != null) {
            String write = notifyEle.getAttribute("write");
            if (StringUtils.hasLength(write)) {
                builder.addPropertyValue("notifyWrite", write);
            }
            String update = notifyEle.getAttribute("update");
            if (StringUtils.hasLength(update)) {
                builder.addPropertyValue("notifyUpdate", update);
            }
            String take = notifyEle.getAttribute("take");
            if (StringUtils.hasLength(take)) {
                builder.addPropertyValue("notifyTake", take);
            }
            String leaseExpire = notifyEle.getAttribute("lease-expire");
            if (StringUtils.hasLength(leaseExpire)) {
                builder.addPropertyValue("notifyLeaseExpire", leaseExpire);
            }
            String all = notifyEle.getAttribute("all");
            if (StringUtils.hasLength(all)) {
                builder.addPropertyValue("notifyAll", all);
            }
        }

        Element batchEle = DomUtils.getChildElementByTagName(element, BATCH);
        if (batchEle != null) {
            builder.addPropertyValue("batchTime", batchEle.getAttribute(BATCH_TIME));
            builder.addPropertyValue("batchSize", batchEle.getAttribute(BATCH_SIZE));
        }

        Element leaseEle = DomUtils.getChildElementByTagName(element, LEASE);
        if (leaseEle != null) {
            String autoRenew = leaseEle.getAttribute(LEASE_AUTO_RENEW);
            if (StringUtils.hasLength(autoRenew)) {
                builder.addPropertyValue("autoRenew", autoRenew);
            }
            String timeout = leaseEle.getAttribute(LEASE_TIMEOUT);
            if (StringUtils.hasLength(timeout)) {
                builder.addPropertyValue("listenerLease", timeout);
            }
        }

        String comType = element.getAttribute(COM_TYPE);
        if (StringUtils.hasLength(comType)) {
            builder.addPropertyValue("comTypeName", comType);
        }

        String fifo = element.getAttribute(FIFO);
        if (StringUtils.hasLength(fifo)) {
            builder.addPropertyValue("fifo", fifo);
        }

        String triggerNotifyTemplate = element.getAttribute(TRIGGER_NOTIFY_TEMPLATE);
        if (StringUtils.hasLength(triggerNotifyTemplate)) {
            builder.addPropertyValue("triggerNotifyTemplate", triggerNotifyTemplate);
        }

        String replicateNotifyTemplate = element.getAttribute(REPLICATE_NOTIFY_TEMPLATE);
        if (StringUtils.hasLength(replicateNotifyTemplate)) {
            builder.addPropertyValue("triggerNotifyTemplate", replicateNotifyTemplate);
        }

        String performTakeOnNotify = element.getAttribute(PERFORM_TAKE_ON_NOTIFY);
        if (StringUtils.hasLength(performTakeOnNotify)) {
            builder.addPropertyValue("performTakeOnNotify", performTakeOnNotify);
        }
    }
}