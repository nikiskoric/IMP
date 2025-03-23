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
package org.openmuc.framework.datalogger.ascii.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Const {

    public static final Charset CHAR_SET = StandardCharsets.US_ASCII;

    public static final String DEFAULT_DIRECTORY = System.getProperty("user.dir") + "/data/ascii/";
    public static final String FILEINFO = "generated by AsciiLoger of OpenMUC";
    public static final double ISEFORMATVERSION = 1.00;

    public static final String EXTENSION = ".dat";
    public static final String EXTENSION_OLD = ".old";

    public static final String HEADER_SIGN = "##";
    public static final String COMMENT_SIGN = "#";
    public static final String COMMENT_NAME = "comment";
    public static final String TIMESTAMP_STRING = "unixtimestamp";
    public static final short INVALID_INDEX = -1;
    public static final String DATE_FORMAT = "%1$tY%1$tm%1$td"; // Date in YYYYMMDD
    public static final String TIME_FORMAT = "%1$tH%1$tM%1$tS"; // Time in HHMMSS
    public static final String SEPARATOR = ";\t";
    public static final char LINESEPARATOR = '\n';
    public static final String LINESEPARATOR_STRING = "\n";
    public static final String ERROR = "err";
    public static final String COL_NUM = "col_no";
    public static final String DATATYPE_NAME = "value_type";
    public static final String VALUETYPE_ENDSIGN = ". ";
    public static final String VALUETYPE_SIZE_SEPARATOR = ",";
    public static final String HEXADECIMAL = "0x";

    public static final char MINUS_SIGN = '-';
    public static final char PLUS_SIGN = '+';
    public static final char DECIMAL_SEPARATOR = '.';
    public static final char TIME_SEPERATOR = '_';
    public static final String TIME_SEPERATOR_STRING = "_";

    public static final int SIZE_LEADING_SIGN = 1;
    public static final int VALUE_SIZE_DOUBLE = 8 + SIZE_LEADING_SIGN;
    public static final int VALUE_SIZE_INTEGER = 11 + SIZE_LEADING_SIGN;
    public static final int VALUE_SIZE_LONG = 20 + SIZE_LEADING_SIGN;
    public static final int VALUE_SIZE_SHORT = 6 + SIZE_LEADING_SIGN;
    public static final int VALUE_SIZE_MINIMAL = 5 + SIZE_LEADING_SIGN;

    public static final int NUM_OF_TIME_TYPES_IN_HEADER = 3;

    private Const() {
    }
}
