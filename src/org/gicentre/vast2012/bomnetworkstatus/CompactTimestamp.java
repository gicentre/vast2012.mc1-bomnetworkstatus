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
 */
public class CompactTimestamp {

	protected static long compactTimestampEpochStart = 1328170500; // 2012-02-02 08:15:00
	protected static long compactTimestampEpochDiff = 60 * 15; // 15 mins between times

	protected static SimpleDateFormat datetimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
	 * Converts compact timestamp to a human-readable format
	 */
	public static String toHRString(short compactTimestamp) {
		return toHRString(compactTimestamp, 0);
	}

	/**
	 * Converts compact timestamp to a human-readable format
	 */
	public static String toHRString(short compactTimestamp, int timezoneOffset) {
		String result = datetimeFormatter.format(new Date(1000 * (compactTimestamp * compactTimestampEpochDiff + compactTimestampEpochStart + timezoneOffset*60*60)));
		
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
