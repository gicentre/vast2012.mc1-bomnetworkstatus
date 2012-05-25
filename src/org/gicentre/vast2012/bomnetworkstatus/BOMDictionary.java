package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Converts values to a human-readable format
 * @author alexander
 *
 */
public class BOMDictionary {

	public static String machineGroupToHR(int mg) {
		String result = "";

		switch (mg) {
		case 0:
			result += "All machines";
			break;
		case 1:
			result += "ATMs";
			break;
		case 2:
			result += "Servers";
			break;
		case 3:
			result += "Workstations";
			break;
		}
		return result;
	}

	public static String activityFlagToHR(int activityFlag) {
		String result = String.valueOf(activityFlag);
		switch (activityFlag) {
		case 0:
			result += " (n/a)";
			break;
		case 1:
			result += " (normal)";
			break;
		case 2:
			result += " (maintenance)";
			break;
		case 3:
			result += " (>5 inv. logins)";
			break;
		case 4:
			result += " (CPU 100%)";
			break;
		case 5:
			result += " (device added)";
			break;
		}
		return result;
	}

	public static String policyStatusToHR(int policyStatus) {
		String result = String.valueOf(policyStatus);
		switch (policyStatus) {
		case 0:
			result += " (n/a)";
			break;
		case 1:
			result += " (healthy)";
			break;
		case 2:
			result += " (moderate policy deviation)";
			break;
		case 3:
			result += " (serious policy deviations)";
			break;
		case 4:
			result += " (critical policy deviations)";
			break;
		case 5:
			result += " (possible virus infection)";
			break;
		}
		return result;
	}

	public static String connectionsToHR(int connectionParameter) {
		String result = "";
		switch (connectionParameter) {
		case 0:
			result += "count";
			break;
		case 1:
			result += "min";
			break;
		case 2:
			result += "max";
			break;
		case 3:
			result += "avg";
			break;
		case 4:
			result += "sd";
			break;
		}
		return result;
	}

	public static String machineFunctionToHR(int machineFunction) {
		switch (machineFunction) {
		case 1:
			return "compute";
		case 2:
			return "email";
		case 3:
			return "file server";
		case 4:
			return "multiple";
		case 5:
			return "loan";
		case 6:
			return "office";
		case 7:
			return "teller";
		case 8:
			return "web";
		}
		return "unknown";
	}

	public static byte machineFunctionFromHR(String machineFunction) {
		if (machineFunction.equals("compute"))
			return 1;
		if (machineFunction.equals("email"))
			return 2;
		if (machineFunction.equals("file server"))
			return 3;
		if (machineFunction.equals("multiple"))
			return 4;
		if (machineFunction.equals("loan"))
			return 5;
		if (machineFunction.equals("office"))
			return 6;
		if (machineFunction.equals("teller"))
			return 7;
		if (machineFunction.equals("web"))
			return 8;

		System.out.println(machineFunction);
		return 0;
	}

	public static String longToIp(long i) {

		return ((i >> 24) & 0xFF) + "." +

		((i >> 16) & 0xFF) + "." +

		((i >> 8) & 0xFF) + "." +

		(i & 0xFF);

	}
}
