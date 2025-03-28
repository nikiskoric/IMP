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
package org.openmuc.framework.driver.iec60870;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.driver.iec60870.settings.ChannelAddress;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.ie.InformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Iec60870ReadListener implements ConnectionEventListener {

    private List<ChannelRecordContainer> containers;
    private final HashMap<String, ChannelAddress> channelAddressMap = new HashMap<>();
    private final HashMap<String, Record> recordMap = new HashMap<>();

    private long timeout;

    private IOException ioException = null;
    private boolean isReadyReading = false;

    private static final Logger logger = LoggerFactory.getLogger(Iec60870ReadListener.class);

    public Iec60870ReadListener(Connection clientConnection) {
    }

    synchronized void setContainer(List<ChannelRecordContainer> containers) {

        this.containers = containers;

        for (ChannelRecordContainer channelRecordContainer : containers) {
            try {
                ChannelAddress channelAddress = new ChannelAddress(channelRecordContainer.getChannelAddress());
                channelAddressMap.put(channelRecordContainer.getChannel().getId(), channelAddress);
            } catch (ArgumentSyntaxException e) {
                logger.error(
                        "ChannelId: " + channelRecordContainer.getChannel().getId() + "; Message: " + e.getMessage());
            }
        }
    }

    void setReadTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public synchronized void newASdu(org.openmuc.j60870.Connection jConnectionASdu, ASdu aSdu) {
        logger.debug("Got new ASdu");
        if (logger.isTraceEnabled()) {
            logger.trace(aSdu.toString());
        }
        long timestamp = System.currentTimeMillis();

        if (!aSdu.isTestFrame()) {

            Set<String> keySet = channelAddressMap.keySet();

            for (String channelId : keySet) {
                ChannelAddress channelAddress = channelAddressMap.get(channelId);

                if (aSdu.getCommonAddress() == channelAddress.commonAddress()
                        && aSdu.getTypeIdentification().getId() == channelAddress.typeId()) {
                    processRecords(aSdu, timestamp, channelId, channelAddress);
                }
            }
            isReadyReading = true;
        }
    }

    @Override
    public void connectionClosed(org.openmuc.j60870.Connection jConnectionASdu, IOException e) {
        logger.info("Connection was closed by server.");
        ioException = e;
    }

    @Override
    public void dataTransferStateChanged(org.openmuc.j60870.Connection jConnectionASdu, boolean stopped) {
        logger.info("Data transfer state was changed to {}.", stopped);
    }

    void read() throws IOException {
        long sleepTime = 100;
        long time = 0;

        while (ioException == null && time < timeout && !isReadyReading) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                logger.error("", e);
            }
            time += sleepTime;
        }

        if (ioException != null) {
            throw ioException;
        }

        for (ChannelRecordContainer channelRecordContainer : containers) {
            String channelId = channelRecordContainer.getChannel().getId();
            Record record = recordMap.get(channelId);
            if (record == null || record.getFlag() != Flag.VALID) {
                channelRecordContainer.setRecord(new Record(Flag.DRIVER_ERROR_TIMEOUT));
            }
            else {
                channelRecordContainer.setRecord(record);
            }
        }
        isReadyReading = false;
    }

    private void processRecords(ASdu aSdu, long timestamp, String channelId, ChannelAddress channelAddress) {
        for (InformationObject informationObject : aSdu.getInformationObjects()) {
            if (informationObject.getInformationObjectAddress() == channelAddress.ioa()) {
                Record record = Iec60870DataHandling.handleInformationObject(aSdu, timestamp, channelAddress,
                        informationObject);
                recordMap.put(channelId, record);
            }
        }
    }

}
