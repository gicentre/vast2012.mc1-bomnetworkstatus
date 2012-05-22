package org.gicentre.vast2012.statusgrid;

import java.sql.Timestamp;

public class CompactTimestamp {

	protected static long compactEpochStart = 1325376000; //Sun, 01 Jan 2012 00:00:00 GMT
	protected static long compactEpochDiff = 60*15; // 15 mins between times
	
	public static short FullTimestampToCompact(Timestamp ts) {
		return (short)((ts.getTime()/1000 - compactEpochStart)/compactEpochDiff);
	}
}
