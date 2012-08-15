package org.gicentre.vast2012.bomnetworkstatus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Compact time stamp is introduced to save memory.
 * It is equal to 0 on 2012-02-02 at 08:15:00 BMT
 *                1 on 2012-02-02 at 08:30:00 BMT
 *                2 on 2012-02-02 at 08:45:00 BMT
 * and so on (+1 every 15 minutes)
 * The class is static (no constructor) 
 *
 * @author Alexander Kachkaev <alexander.kachkaev.1@city.ac.uk>
 */

/* 
 * This file is part of BoM Network Status Application, VAST 2012 Mini Challenge 1 entry
 * awarded for "Efficient Use of Visualization". It is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * BoM Network Status is distributed WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see 
 * http://www.gnu.org/licenses/.
 * 
 * For report on challenge, video and summary paper see http://gicentre.org/vast2012/
 */

public class CompactTimestamp {

	protected static long compactTimestampEpochStart = 1328170500; // 2012-02-02 08:15:00
	protected static long compactTimestampEpochDiff = 60 * 15; // 15 mins between time

	protected static SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected static SimpleDateFormat datetimeFormatterNoSec = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * Converts "classic" time stamp (long number, with milliseconds) to CompactTimestamp format (number, short)
	 */
	public static short fullTimestampToCompact(long ts) {
		return (short) ((ts / 1000 - compactTimestampEpochStart) / compactTimestampEpochDiff);
	}

	/**
	 * Converts "classic" time stamp (java.sql.Timestamp) to CompactTimestamp format (number, short)
	 */
	public static short fullTimestampToCompact(Timestamp ts) {
		return fullTimestampToCompact(ts.getTime());
	}

	/**
	 * Converts "classic" time stamp (string, e.g. 2012-01-01 00:00:00) to CompactTimestamp format (number, short)
	 */
	public static short fullTimestampToCompact(String ts) {
		try {
			return fullTimestampToCompact(datetimeFormatter.parse(ts).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Converts "classic" time stamp (string, e.g. 2012-01-01 00:00:00) to CompactTimestamp format (number, short)
	 */
	public static Timestamp compactTimestimpToFull(short cts) {
		return new Timestamp(1000 * (cts * compactTimestampEpochDiff + compactTimestampEpochStart));
	}
	/**
	 * Converts compact timestamp to a human-readable format
	 */
	public static String toHRString(short compactTimestamp) {
		return toHRString(compactTimestamp, 0);
	}

	/**
	 * Converts compact timestamp to a human-readable format
	 */
	public static String toHRString(short compactTimestamp, int timezoneOffset) {
		String result = datetimeFormatterNoSec.format(new Date(1000 * (compactTimestamp * compactTimestampEpochDiff + compactTimestampEpochStart + timezoneOffset*60*60)));
		
		if (timezoneOffset == 0)
			result += " BMT";
		else
			result += " LOC (BMT" + (timezoneOffset >= 0 ? "+" : "-") + (Math.abs(timezoneOffset) < 10 ? "0" : "") + Math.abs(timezoneOffset) + ")";
			
		return result;
	}

	/**
	 * Checks if a given CompactTimeStamp is within 48 hours from the beginning of the CompactTimestamp epoch start
	 * (needed in some parts of the grid visualization)
	 */
	public static boolean isWithin48HrsWindow(short compactTimestamp) {
		return compactTimestamp >= 0 && compactTimestamp <= 191;
	}
}
