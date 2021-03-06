/*
 * Copyright (c) 2017-2018 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.podm.services.configuration.difference;

import com.intel.podm.business.entities.redfish.Endpoint;
import com.intel.podm.business.entities.redfish.embeddables.Identifier;
import org.apache.commons.collections4.Equator;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Set;

import static com.intel.podm.common.types.DurableNameFormat.NQN;
import static com.intel.podm.common.types.EntityRole.INITIATOR;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

public class EndpointEquator implements Equator<Endpoint> {
    private static final IpTransportDetailsEquator IP_TRANSPORT_DETAILS_EQUATOR = new IpTransportDetailsEquator();
    private static final ConnectedEntityEquator CONNECTED_ENTITY_EQUATOR = new ConnectedEntityEquator();
    private static final IdentifierEquator IDENTIFIER_EQUATOR = new IdentifierEquator();

    static String toEquatorString(Endpoint endpoint) {
        if (endpoint != null) {
            return new ToStringBuilder(endpoint)
                .append("IpTransportDetails", endpoint.getIpTransportsDetails().stream()
                    .map(IP_TRANSPORT_DETAILS_EQUATOR::asEquatorString)
                    .collect(joining(", "))
                )
                .append("ConnectedEntities", endpoint.getConnectedEntities().stream()
                    .map(CONNECTED_ENTITY_EQUATOR::asEquatorString)
                    .collect(joining(", "))
                )
                .append("Identifiers", endpoint.getIdentifiers().stream()
                    .map(IDENTIFIER_EQUATOR::asEquatorString)
                    .collect(joining(", ")))
                .toString();
        }
        return null;
    }

    @Override
    public boolean equate(Endpoint first, Endpoint second) {
        if (first.hasRole(INITIATOR) && second.hasRole(INITIATOR)) {
            return compareByConnectedEntitiesAndNqn(first, second);
        } else {
            return isEqualCollection(first.getIpTransportsDetails(), second.getIpTransportsDetails(), IP_TRANSPORT_DETAILS_EQUATOR)
                && compareByConnectedEntitiesAndNqn(first, second);
        }
    }

    private boolean compareByConnectedEntitiesAndNqn(Endpoint first, Endpoint second) {
        return isEqualCollection(first.getConnectedEntities(), second.getConnectedEntities(), CONNECTED_ENTITY_EQUATOR)
            && isEqualCollection(findNqnIdentifier(first.getIdentifiers()), findNqnIdentifier(second.getIdentifiers()), IDENTIFIER_EQUATOR);
    }

    @Override
    public int hash(Endpoint object) {
        HashCodeBuilder builder = new HashCodeBuilder()
            .append(IDENTIFIER_EQUATOR.hash(findNqnIdentifier(object.getIdentifiers())))
            .append(CONNECTED_ENTITY_EQUATOR.hash(object.getConnectedEntities()));

        if (!object.hasRole(INITIATOR)) {
            builder.append(IP_TRANSPORT_DETAILS_EQUATOR.hash(object.getIpTransportsDetails()));
        }

        return builder.toHashCode();
    }

    private Set<Identifier> findNqnIdentifier(Set<Identifier> identifiers) {
        return identifiers.stream()
            .filter(identifier -> NQN.equals(identifier.getDurableNameFormat()))
            .collect(toSet());
    }
}
