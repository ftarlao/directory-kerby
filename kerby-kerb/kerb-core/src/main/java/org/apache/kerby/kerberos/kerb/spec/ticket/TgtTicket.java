/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.kerby.kerberos.kerb.spec.ticket;

import org.apache.kerby.kerberos.kerb.spec.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.spec.kdc.EncAsRepPart;

public class TgtTicket extends KrbTicket {
    private PrincipalName clientPrincipal;

    public TgtTicket(Ticket ticket, EncAsRepPart encKdcRepPart, PrincipalName clientPrincipal) {
        super(ticket, encKdcRepPart);
        this.clientPrincipal = clientPrincipal;
    }

    public PrincipalName getClientPrincipal() {
        return clientPrincipal;
    }
}
