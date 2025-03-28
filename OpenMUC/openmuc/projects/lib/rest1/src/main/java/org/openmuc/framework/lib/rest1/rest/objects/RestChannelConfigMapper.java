/*
 * Copyright 2011-2024 Fraunhofer ISE
 *
 * This file is part of OpenMUC.
 * For more information visit http://www.openmuc.org
 *
 * OpenMUC is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenMUC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenMUC. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openmuc.framework.lib.rest1.rest.objects;

import java.util.List;

import org.openmuc.framework.config.ChannelConfig;
import org.openmuc.framework.config.IdCollisionException;
import org.openmuc.framework.config.ServerMapping;
import org.openmuc.framework.lib.rest1.exceptions.RestConfigIsNotCorrectException;

public class RestChannelConfigMapper {

    public static RestChannelConfig getRestChannelConfig(ChannelConfig cc) {

        RestChannelConfig rcc = new RestChannelConfig();
        rcc.setChannelAddress(cc.getChannelAddress());
        rcc.setDescription(cc.getDescription());
        rcc.setDisabled(cc.isDisabled());
        rcc.setId(cc.getId());
        rcc.setListening(cc.isListening());
        rcc.setLoggingInterval(cc.getLoggingInterval());
        rcc.setLoggingTimeOffset(cc.getLoggingTimeOffset());
        rcc.setLoggingSettings(cc.getLoggingSettings());
        rcc.setSamplingGroup(cc.getSamplingGroup());
        rcc.setSamplingInterval(cc.getSamplingInterval());
        rcc.setSamplingTimeOffset(cc.getSamplingTimeOffset());
        rcc.setScalingFactor(cc.getScalingFactor());
        rcc.setServerMappings(cc.getServerMappings());
        rcc.setSettings(cc.getSettings());
        rcc.setUnit(cc.getUnit());
        rcc.setValueOffset(cc.getValueOffset());
        rcc.setValueType(cc.getValueType());
        rcc.setValueTypeLength(cc.getValueTypeLength());
        rcc.setLoggingEvent(cc.isLoggingEvent());
        return rcc;
    }

    public static void setChannelConfig(ChannelConfig cc, RestChannelConfig rcc, String idFromUrl)
            throws IdCollisionException, RestConfigIsNotCorrectException {
        if (cc == null) {
            throw new RestConfigIsNotCorrectException("ChannelConfig is null!");
        }

        if (rcc == null) {
            throw new RestConfigIsNotCorrectException();
        }

        if (rcc.getId() != null && !rcc.getId().isEmpty() && !idFromUrl.equals(rcc.getId())) {
            cc.setId(rcc.getId());
        }
        cc.setChannelAddress(rcc.getChannelAddress());
        cc.setDescription(rcc.getDescription());
        cc.setDisabled(rcc.isDisabled());
        cc.setListening(rcc.isListening());
        cc.setLoggingInterval(rcc.getLoggingInterval());
        cc.setLoggingTimeOffset(rcc.getLoggingTimeOffset());
        cc.setLoggingEvent(rcc.isLoggingEvent());
        cc.setLoggingSettings(rcc.getLoggingSettings());
        cc.setSamplingGroup(rcc.getSamplingGroup());
        cc.setSamplingInterval(rcc.getSamplingInterval());
        cc.setSamplingTimeOffset(rcc.getSamplingTimeOffset());
        cc.setScalingFactor(rcc.getScalingFactor());
        List<ServerMapping> serverMappings = rcc.getServerMappings();
        if (serverMappings != null) {
            for (ServerMapping serverMapping : cc.getServerMappings()) {
                cc.deleteServerMappings(serverMapping.getId());
            }
            for (ServerMapping restServerMapping : serverMappings) {
                cc.addServerMapping(restServerMapping);
            }
        }
        cc.setSettings(rcc.getSettings());
        cc.setUnit(rcc.getUnit());
        cc.setValueOffset(rcc.getValueOffset());
        cc.setValueType(rcc.getValueType());
        cc.setValueTypeLength(rcc.getValueTypeLength());
    }

}
