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
package org.apache.kerby.kerberos.kerb.type.ticket;

import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.kdc.EncKdcRepPart;

public class KrbTicket {
    private Ticket ticket;
    private EncKdcRepPart encKdcRepPart;

    public KrbTicket(Ticket ticket, EncKdcRepPart encKdcRepPart) {
        this.ticket = ticket;
        this.encKdcRepPart = encKdcRepPart;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public EncKdcRepPart getEncKdcRepPart() {
        return encKdcRepPart;
    }

    public EncryptionKey getSessionKey() {
        return encKdcRepPart.getKey();
    }

    public String getRealm() {
        return ticket.getRealm();
    }
}
