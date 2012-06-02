package org.gicentre.vast2012.bomnetworkstatus;

/**
 * Converts values to a human-readable format
 *
 */
public class BOMDictionary {

	public static String machineGroupToHR(int mg) {
		switch (mg) {
		case 0:
			return "all machines";
		case 1:
			return "ATMs";
		case 2:
			return "servers";
		case 3:
			return "workstations";
		case 3 + 1:
			return "servers / compute";
		case 3 + 2:
			return "servers / email";
		case 3 + 3:
			return "servers / file server";
		case 3 + 4:
			return "servers / multiple";
		case 3 + 5:
			return "servers / web";
		case 3 + 6:
			return "workstations / loan";
		case 3 + 7:
			return "workstations / office";
		case 3 + 8:
			return "workstations / teller";
		}
		return null;
	}

	public static byte machineClassFromHR(String machineGroup) {
		if (machineGroup.equals("atm"))
			return 1;
		if (machineGroup.equals("server"))
			return 2;
		if (machineGroup.equals("workstation"))
			return 3;
		return 0;
	}

	public static String machineClassToHR(int machineClass) {
		switch (machineClass) {
		case 1:
			return "ATM";
		case 2:
			return "server";
		case 3:
			return "workstation";
		}
		return null;
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
			return "web";
		case 6:
			return "loan";
		case 7:
			return "office";
		case 8:
			return "teller";
		}
		return "undefined";
	}

	public static String machineFunctionToHR2(int machineFunction) {
		switch (machineFunction) {
		case 1:
			return "srv - compute";
		case 2:
			return "srv - email";
		case 3:
			return "srv - file srv";
		case 4:
			return "srv - multiple";
		case 5:
			return "srv - web";
		case 6:
			return "ws - loan";
		case 7:
			return "ws - office";
		case 8:
			return "ws - teller";
		}
		return "atm";
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
		if (machineFunction.equals("web"))
			return 5;
		if (machineFunction.equals("loan"))
			return 6;
		if (machineFunction.equals("office"))
			return 7;
		if (machineFunction.equals("teller"))
			return 8;

		return 0;
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
		return policyStatusToHR(policyStatus, false);
	}

		public static String policyStatusToHR(int policyStatus, boolean isShort) {
		String result = String.valueOf(policyStatus);
		switch (policyStatus) {
		case 0:
			result += " (n/a)";
			break;
		case 1:
			result += " (healthy)";
			break;
		case 2:
			result += isShort ? " (moderate p. d.)" : " (moderate policy deviation)";
			break;
		case 3:
			result += isShort ? " (serious p. d.)" : " (serious policy deviations)";
			break;
		case 4:
			result += isShort ? " (critical p. d.)" : " (critical policy deviations)";
			break;
		case 5:
			result += isShort ? " (possible virus)" : " (possible virus infection)";
			break;
		}
		return result;
	}

	public static String connectionsToHR(int connectionParameter) {
		switch (connectionParameter) {
		case 0:
			return "avg";
		case 1:
			return "sd";
		case 2:
			return "min";
		case 3:
			return "max";
		}
		return "unknown";
	}

	public static String DetailsSortOrderToHR(int sortMode) {
		switch (sortMode) {
		case MachineDetailsComparator.SM_IP:
			return "IP address";
		case MachineDetailsComparator.SM_ACTIVITY_FLAG:
			return "activity flag";
		case MachineDetailsComparator.SM_POLICY_STATUS:
			return "policy status";
		case MachineDetailsComparator.SM_CONNECTIONS:
			return "number of connections";
		}
		return null;
	}

}
