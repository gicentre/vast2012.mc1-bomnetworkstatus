package org.gicentre.vast2012.bomnetworkstatus;

public class IPConverter {
	public static int stringToInt(String ip) {
		
		if (ip.isEmpty())
			return 0;
		
		String[] ipArray = ip.split("\\.");

		int num = 0;

		for (int i = 0; i < ipArray.length; i++) {
			num += (Integer.parseInt(ipArray[i]) & 0xFF) << ((3-i)*8);
		}

		return num;
	}

	public static String intToStr(int ip) {
		if (ip == 0) return "";
		return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + (ip & 0xFF);
	}
}
