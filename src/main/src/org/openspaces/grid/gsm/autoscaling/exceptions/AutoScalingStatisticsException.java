/*******************************************************************************
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
 *******************************************************************************/
package org.openspaces.grid.gsm.autoscaling.exceptions;

import java.util.Map;

import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.admin.pu.statistics.ProcessingUnitStatisticsId;

/**
 * @author itaif
 * @since 9.0.0
 */
public class AutoScalingStatisticsException extends AutoScalingSlaEnforcementInProgressException {

    private static final long serialVersionUID = 1L;
    
    protected AutoScalingStatisticsException(ProcessingUnit pu, String message) {
        super(pu, message);
    }
    
    protected AutoScalingStatisticsException(ProcessingUnit pu, String message, Throwable reason) {
        super(pu, message, reason);
    }
    
    public AutoScalingStatisticsException(ProcessingUnit pu, ProcessingUnitStatisticsId statisticsId) {
        this(pu,message(statisticsId,pu));
    }
    
    public AutoScalingStatisticsException(ProcessingUnit pu, ProcessingUnitStatisticsId statisticsId, Map<ProcessingUnitStatisticsId, Object> statistics) {
        this(pu,message(statisticsId,pu, statistics));
    }
    

    /**
     * Generates the default message
     * @param pu 
     */
    private static String message(ProcessingUnitStatisticsId statisticsId, ProcessingUnit pu) {
        return "No " + pu.getName() + " statistics for " + statisticsId;
    }
    
    private static String message(ProcessingUnitStatisticsId statisticsId, ProcessingUnit pu, Map<ProcessingUnitStatisticsId, Object> statistics) {
        return "No " + pu.getName() + " statistics for " + statisticsId + ". current processing unit statistics are : " + statistics ;
    }

}
