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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmuc.framework.config.ArgumentSyntaxException;
import org.openmuc.framework.config.ChannelScanInfo;
import org.openmuc.framework.config.ScanException;
import org.openmuc.framework.data.Flag;
import org.openmuc.framework.data.Record;
import org.openmuc.framework.data.TypeConversionException;
import org.openmuc.framework.driver.iec60870.settings.ChannelAddress;
import org.openmuc.framework.driver.iec60870.settings.DeviceAddress;
import org.openmuc.framework.driver.iec60870.settings.DeviceSettings;
import org.openmuc.framework.driver.spi.ChannelRecordContainer;
import org.openmuc.framework.driver.spi.ChannelValueContainer;
import org.openmuc.framework.driver.spi.Connection;
import org.openmuc.framework.driver.spi.ConnectionException;
import org.openmuc.framework.driver.spi.RecordsReceivedListener;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ClientConnectionBuilder;
import org.openmuc.j60870.ie.IeQualifierOfInterrogation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Iec60870Connection implements Connection {

    private org.openmuc.j60870.Connection clientConnection;

    private static final Logger logger = LoggerFactory.getLogger(Iec60870Connection.class);

    private final DeviceAddress deviceAddress;
    private final DeviceSettings deviceSettings;
    private final String driverId;

    private final Iec60870ListenerList iec60870listener = new Iec60870ListenerList();
    private final Iec60870ReadListener readListener = new Iec60870ReadListener(clientConnection);

    public Iec60870Connection(DeviceAddress deviceAddress, DeviceSettings deviceSettings, String driverId)
            throws ConnectionException {
        this.deviceAddress = deviceAddress;
        this.deviceSettings = deviceSettings;
        this.driverId = driverId;

        ClientConnectionBuilder clientConnectionBuilder = new ClientConnectionBuilder(this.deviceAddress.hostAddress());

        int port = this.deviceAddress.port();
        System.out.println("Connection Port = " + port);
        String hostAddress = this.deviceAddress.hostAddress().getHostAddress();

        try {
            setupClientSap(clientConnectionBuilder, deviceSettings);
            connect(clientConnectionBuilder, port, hostAddress);
            startListenIec60870(deviceSettings, port, hostAddress);
        } catch (IOException e) {
            throw new ConnectionException(MessageFormat.format("Was not able to connect to {0}:{1}. {2}",
                    this.deviceAddress.hostAddress().getHostName(), port, e.getMessage()));
        }
    }

    private void startListenIec60870(DeviceSettings deviceSettings, int port, String hostAddress) throws IOException {
        clientConnection.startDataTransfer();
        iec60870listener.addListener(readListener);
        logger.debug("Driver-IEC60870: successful sent startDT act to {}{}{}{}", hostAddress, ":", port,
                "and got startDT con.");
    }

    private void connect(ClientConnectionBuilder clientConnectionBuilder, int port, String hostAddress)
            throws IOException {
        logger.debug("Try to connect to: {}{}{}", hostAddress, ':', port);
        clientConnection = clientConnectionBuilder.setConnectionEventListener(iec60870listener).build();
        logger.info("Driver-IEC60870: successful connected to {}{}{}", hostAddress, ":", port);
    }

    @Override
    public List<ChannelScanInfo> scanForChannels(String settings)
            throws UnsupportedOperationException, ArgumentSyntaxException, ScanException, ConnectionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object read(List<ChannelRecordContainer> containers, Object containerListHandle, String samplingGroup)
            throws UnsupportedOperationException, ConnectionException {
        // TODO: read specific values, not only general interrogation
        readListener.setContainer(containers);
        readListener.setReadTimeout(deviceSettings.readTimeout());
        try {
            clientConnection.interrogation(1, CauseOfTransmission.ACTIVATION, new IeQualifierOfInterrogation(20));
            readListener.read();
        } catch (IOException e) {
            throw new ConnectionException(e);
        } catch (UnsupportedOperationException e) {
            logger.error(e.getMessage());
            throw e;
        }
        return null;
    }

    @Override
    public synchronized void startListening(List<ChannelRecordContainer> containers, RecordsReceivedListener listener)
            throws ConnectionException {
        Iec60870Listener iec60870Listen = new Iec60870Listener();
        iec60870Listen.registerOpenMucListener(containers, listener, driverId, this);
        iec60870listener.addListener(iec60870Listen);
    }

    @Override
    public Object write(List<ChannelValueContainer> containers, Object containerListHandle) throws ConnectionException {
        if (!clientConnection.isStopped()) {
            for (ChannelValueContainer channelValueContainer : containers) {
                ChannelAddress channelAddress;
                try {
                    channelAddress = new ChannelAddress(channelValueContainer.getChannelAddress());
                    Record record = new Record(channelValueContainer.getValue(), System.currentTimeMillis(),
                            Flag.VALID);
                    Iec60870DataHandling.writeSingleCommand(record, channelAddress, clientConnection);
                    channelValueContainer.setFlag(Flag.VALID);
                } catch (ArgumentSyntaxException e) {
                    channelValueContainer.setFlag(Flag.DRIVER_ERROR_CHANNEL_ADDRESS_SYNTAX_INVALID);
                    logger.error(e.getMessage());
                    throw new UnsupportedOperationException(e);
                } catch (IOException e) {
                    channelValueContainer.setFlag(Flag.CONNECTION_EXCEPTION);
                    throw new ConnectionException(e);
                } catch (TypeConversionException e) {
                    channelValueContainer.setFlag(Flag.DRIVER_ERROR_CHANNEL_VALUE_TYPE_CONVERSION_EXCEPTION);
                    logger.error(e.getMessage());
                } catch (UnsupportedOperationException e) {
                    channelValueContainer.setFlag(Flag.DRIVER_ERROR_CHANNEL_ADDRESS_SYNTAX_INVALID);
                    logger.error(e.getMessage());
                    throw e;
                }
            }
        }
        else {
            logger.error("Client connection is in stopped state, sending messages are not allowed.");
            for (ChannelValueContainer channelValueContainer : containers) {
                channelValueContainer.setFlag(Flag.DRIVER_ERROR_CHANNEL_TEMPORARILY_NOT_ACCESSIBLE);
            }
        }
        return null;
    }

    @Override
    public void disconnect() {
        if (clientConnection != null) {
            clientConnection.close();
        }
        iec60870listener.removeAllListener();
        logger.info("Disconnected IEC 60870 driver.");
    }

    void setupClientSap(ClientConnectionBuilder clientSap, DeviceSettings deviceSettings) {
        clientSap.setPort(this.deviceAddress.port());

        if (deviceSettings.commonAddressFieldLength() > 0) {
            clientSap.setCommonAddressFieldLength(deviceSettings.commonAddressFieldLength());
        }
        else if (deviceSettings.cotFieldLength() > 0) {
            clientSap.setCotFieldLength(deviceSettings.cotFieldLength());
        }
        else if (deviceSettings.ioaFieldLength() > 0) {
            clientSap.setIoaFieldLength(deviceSettings.ioaFieldLength());
        }
        else if (deviceSettings.maxIdleTime() > 0) {
            clientSap.setMaxIdleTime(deviceSettings.maxIdleTime());
        }
        else if (deviceSettings.maxTimeNoAckReceived() > 0) {
            clientSap.setMaxTimeNoAckReceived(deviceSettings.maxTimeNoAckReceived());
        }
        else if (deviceSettings.maxTimeNoAckSent() > 0) {
            clientSap.setMaxTimeNoAckSent(deviceSettings.maxTimeNoAckSent());
        }
        else if (deviceSettings.maxUnconfirmedIPdusReceived() > 0) {
            clientSap.setMaxUnconfirmedIPdusReceived(deviceSettings.maxUnconfirmedIPdusReceived());
        }
        else if (deviceSettings.connectionTimeout() > 0) {
            clientSap.setConnectionTimeout(deviceSettings.connectionTimeout());
        }
        else if (deviceSettings.maxNumOfOutstandingIPdus() > 0) {
            clientSap.setMaxNumOfOutstandingIPdus(deviceSettings.maxNumOfOutstandingIPdus());
        }
        else if (!deviceSettings.allowedTypes().isEmpty()) {
            List<ASduType> allowedASduTypes = new ArrayList<>();

            String[] arrayIDsString = deviceSettings.allowedTypes().replaceAll("[^0-9,]", "").split(",");
            if (arrayIDsString != null && arrayIDsString.length > 0) {
                int[] ids = Arrays.stream(arrayIDsString).mapToInt(Integer::parseInt).toArray();

                for (int id : ids) {
                    ASduType aSduType = ASduType.typeFor(id);
                    if (aSduType == null) {
                        logger.error(
                                "Device settings contains not allowed value for \"at\" (allowed ASduType). Only int values between 1 and 255 are allowed.");
                    }
                    else {
                        allowedASduTypes.add(aSduType);
                    }
                }
                clientSap.setAllowedASduTypes(allowedASduTypes);
            }
        }
    }
}
