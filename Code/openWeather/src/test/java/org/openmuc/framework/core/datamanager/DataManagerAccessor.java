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
package org.openmuc.framework.core.datamanager;

import org.openmuc.framework.config.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Allows writing unit tests that are based on {@link DataManager} or in other words, that emulate a running OpenMUC instance.
 */
public class DataManagerAccessor {

    private static final Logger log = LoggerFactory.getLogger(DataManagerAccessor.class);

    private DataManagerAccessor() {
        // hide constructor, only static methods here
    }

    public static void activateWithConfig(DataManager dm, String configInTestResources)
            throws IOException, ParserConfigurationException, TransformerException, ParseException {
        File configFile = null;
        try {
            ClassLoader classLoader = DataManagerAccessor.class.getClassLoader();
            configFile = new File(classLoader.getResource(configInTestResources).toURI());
            log.info("Using file {}", configFile.getAbsolutePath());
        } catch (URISyntaxException e) {
            throw new IOException("file not found" + configInTestResources);
        }
        activateWithConfig(dm, configFile);
    }

    public static void activateWithConfig(DataManager dm, File configFile)
            throws IOException, ParserConfigurationException, TransformerException, ParseException {
        dm.activateWithConfig(configFile);
    }
}
