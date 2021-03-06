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
package org.openspaces.grid.gsm.machines.exceptions;

import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.grid.gsm.sla.exceptions.SlaEnforcementInProgressException;

public class MachinesSlaEnforcementInProgressException extends SlaEnforcementInProgressException{

    private static final long serialVersionUID = 1L;
    
    public MachinesSlaEnforcementInProgressException(ProcessingUnit pu) {
        super(pu, "Machines SLA Enforcement is in progress");
    }
    
    public MachinesSlaEnforcementInProgressException(ProcessingUnit pu, String message) {
        super(pu, "Machines SLA Enforcement is in progress: " + message);
    }
    
    public MachinesSlaEnforcementInProgressException(ProcessingUnit pu, String message, Exception cause) {
        super(pu, "Machines SLA Enforcement is in progress: " + message, cause);
    }
}
