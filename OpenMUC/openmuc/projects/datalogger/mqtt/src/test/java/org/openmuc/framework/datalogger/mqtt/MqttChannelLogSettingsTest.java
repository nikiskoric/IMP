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

package org.openmuc.framework.datalogger.mqtt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.management.openmbean.InvalidKeyException;

import org.junit.jupiter.api.Test;
import org.openmuc.framework.datalogger.mqtt.util.MqttChannelLogSettings;

public class MqttChannelLogSettingsTest {

    @Test
    public void testCorrectTopic() {
        String logSettings = "amqplogger:queue=my/queue,setting=true,test=123;mqttlogger:topic=/my/topic/";
        String topic = MqttChannelLogSettings.getTopic(logSettings);
        assertTrue(topic.equals("/my/topic/"));
    }

    @Test
    public void testMissingMqttSettings() {
        assertThrows(InvalidKeyException.class, () -> {
            String logSettings = "amqplogger:queue=my/queue,setting=true,test=123";
            String topic = MqttChannelLogSettings.getTopic(logSettings);
        });
    }

    @Test
    public void testMissingMqttTopic() {
        assertThrows(InvalidKeyException.class, () -> {
            String logSettings = "mqttlogger";
            String topic = MqttChannelLogSettings.getTopic(logSettings);
        });
    }
}
