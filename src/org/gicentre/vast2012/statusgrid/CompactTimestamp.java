package org.gicentre.vast2012.statusgrid;

import java.sql.Timestamp;

public class CompactTimestamp {

	protected static long compactEpochStart = 1328170500; //2012-02-02 08:15:00
	protected static long compactEpochDiff = 60*15; // 15 mins between times
	
	public static short FullTimestampToCompact(Timestamp ts) {
		return (short)((ts.getTime()/1000 - compactEpochStart)/compactEpochDiff);
	}
	
	public static boolean isWithin48HrsWindow(short compactTimestamp) {
		return compactTimestamp >= 0 && compactTimestamp <= 191;
	}
}
