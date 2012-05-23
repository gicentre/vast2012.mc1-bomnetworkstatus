package org.gicentre.vast2012.bomnetworkstatus;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CompactTimestamp {

	protected static long compactEpochStart = 1328170500; //2012-02-02 08:15:00
	protected static long compactEpochDiff = 60*15; // 15 mins between times
	
	protected static SimpleDateFormat datetimeFormatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

	
	public static short FullTimestampToCompact(long ts) {
		return (short)((ts/1000 - compactEpochStart)/compactEpochDiff);
	}

	public static short FullTimestampToCompact(Timestamp ts) {
		return FullTimestampToCompact(ts.getTime());
	}
	
	public static short FullTimestampToCompact(String ts) {
		try {
		return FullTimestampToCompact(datetimeFormatter.parse(ts).getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static boolean isWithin48HrsWindow(short compactTimestamp) {
		return compactTimestamp >= 0 && compactTimestamp <= 191;
	}
}
