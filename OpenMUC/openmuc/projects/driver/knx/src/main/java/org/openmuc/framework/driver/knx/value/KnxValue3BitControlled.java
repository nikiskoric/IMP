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
package org.openmuc.framework.driver.knx.value;

import org.openmuc.framework.data.ByteValue;
import org.openmuc.framework.data.Value;

import tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled;
import tuwien.auto.calimero.exception.KNXFormatException;

public class KnxValue3BitControlled extends KnxValue {

    public KnxValue3BitControlled(String dptID) throws KNXFormatException {
        dptXlator = new DPTXlator3BitControlled(dptID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openmuc.framework.driver.knx.value.KnxValue#setOpenMucValue(org.openmuc.framework.data.Value)
     */
    @Override
    public void setOpenMucValue(Value value) throws KNXFormatException {
        ((DPTXlator3BitControlled) dptXlator).setValue(value.asByte());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openmuc.framework.driver.knx.value.KnxValue#getOpenMucValue()
     */
    @Override
    public Value getOpenMucValue() {
        return new ByteValue(((DPTXlator3BitControlled) dptXlator).getData()[0]);
    }

}
