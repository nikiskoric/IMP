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
package org.openmuc.framework.driver.spi;

import org.openmuc.framework.config.DeviceScanInfo;

public interface DriverDeviceScanListener {

    /**
     * Can optionally be used by driver to let the Data Manager know about the device scan progress in percent.
     * Applications can access this value through the ConfigService.
     * 
     * @param progress
     *            the progress in percent.
     */
    void scanProgressUpdate(int progress);

    /**
     * Is used by the driver to notify the Data Manager of new devices found during a scan.
     * 
     * @param scanInfo
     *            the information obtained from the device.
     */
    void deviceFound(DeviceScanInfo scanInfo);

}
