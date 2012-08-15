package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.util.HashMap;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.Facility;

/**
 * The grid is a collection of business units attached to the cells
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

public class BusinessunitGrid {

	public static final int COL_WIDTH = 192;
	public static final int ROW_HEIGHT = 15;

	public static final int LAYOUT_SEQ = 1;
	public static final int LAYOUT_GEO = 2;
	public static final int LAYOUT_GEO_EXCL_DC = 3;

	public static final int PADDING = 4;
	public static final int CELLPADDING_H = 4;
	public static final int CELLPADDING_V = 4;

	protected int colCount = 0;
	protected int rowCount = 0;

	protected HashMap<String, Businessunit> businessunits;
	protected String[][] grid;
	private int currentLayout;

	public BusinessunitGrid(HashMap<String, Businessunit> businessunits, int layout) {
		setBusinessUnits(businessunits);
		setLayout(layout);
	}

	public int getLayout() {
		// TODO Auto-generated method stub
		return currentLayout;
	}

	public void setBusinessUnits(HashMap<String, Businessunit> businessunits) {
		this.businessunits = businessunits;
	}
	
	/**
	 * Rearranges the business units, ordering them by name or as a spatial tree map
	 */
	public void setLayout(int layout) {
		if (layout == LAYOUT_SEQ) {

			colCount = 5;
			rowCount = 48;
			grid = new String[colCount][rowCount];

			int row = 0;
			int col = 0;

			// Adding 10 large regions (5*2)
			for (int id = 1; id <= 10; id++) {
				grid[col][row] = "region-" + id;
				col++;
				if (col >= 5) {
					col = 0;
					row += 11;
				}
			}

			// Adding 40 small regions (5*8)
			for (int id = 11; id <= 50; id++) {
				grid[col][row] = "region-" + id;
				col++;
				if (col >= 5) {
					col = 0;
					row += 3;
				}
			}

			// Adding data centres
			for (int id = 1; id <= 5; id++)
				grid[id-1][46] = "datacenter-" + id;
			grid[4][47] = "headquarters";

		} else if (layout == LAYOUT_GEO) {
			colCount = 5;
			rowCount = 48;
			grid = new String[colCount][rowCount];

			grid[0][0] = "region-11";
			grid[0][3] = "region-1";
			grid[0][14] = "region-36";
			grid[0][17] = "datacenter-2";
			grid[0][18] = "region-37";
			grid[0][21] = "region-2";
			grid[0][32] = "region-50";
			grid[0][35] = "region-41";
			grid[0][38] = "region-33";
			grid[0][41] = "region-34";
			grid[0][44] = "region-35";
			
			grid[1][0] = "region-12";
			grid[1][3] = "region-13";
			grid[1][6] = "region-39";
			grid[1][9] = "region-9";
			grid[1][20] = "region-46";
			grid[1][23] = "region-48";
			grid[1][26] = "region-47";
			grid[1][29] = "region-49";
			grid[1][32] = "region-32";
			grid[1][35] = "region-8";
			grid[1][46] = "datacenter-3";

			grid[2][0] = "datacenter-5";
			grid[2][1] = "region-10";
			grid[2][12] = "region-14";
			grid[2][15] = "region-45";
			grid[2][18] = "region-43";
			grid[2][21] = "region-40";
			grid[2][24] = "region-44";
			grid[2][27] = "region-42";
			grid[2][30] = "region-30";
			grid[2][33] = "region-3";
			grid[2][44] = "region-31";
			
			grid[3][0] = "region-20";
			grid[3][3] = "region-19";
			grid[3][6] = "region-18";
			grid[3][9] = "region-17";
			grid[3][12] = "datacenter-1";
			grid[3][13] = "region-15";
			grid[3][16] = "region-5";
			grid[3][27] = "region-16";
			grid[3][30] = "region-6";
			grid[3][41] = "region-38";
			grid[3][44] = "region-29";

			grid[4][0] = "region-21";
			grid[4][3] = "region-22";
			grid[4][6] = "region-23";
			grid[4][9] = "region-24";
			grid[4][12] = "region-25";
			grid[4][15] = "region-4";
			grid[4][26] = "region-26";
			grid[4][29] = "region-27";
			grid[4][32] = "region-28";
			grid[4][35] = "region-7";
			grid[4][46] = "datacenter-4";
			grid[4][47] = "headquarters";
			
		} else if (layout == LAYOUT_GEO_EXCL_DC) {
			colCount = 5;
			rowCount = 48;
			grid = new String[colCount][rowCount];

			grid[0][0] = "region-11";
			grid[0][3] = "region-1";
			grid[0][14] = "region-36";
			//grid[0][17] = "datacenter-2";
			grid[0][17] = "region-37";
			grid[0][20] = "region-2";
			grid[0][31] = "region-50";
			grid[0][34] = "region-41";
			grid[0][37] = "region-33";
			grid[0][40] = "region-34";
			grid[0][43] = "region-35";
			grid[0][46] = "datacenter-2";
			
			grid[1][0] = "region-12";
			grid[1][3] = "region-13";
			grid[1][6] = "region-39";
			grid[1][9] = "region-9";
			grid[1][20] = "region-46";
			grid[1][23] = "region-48";
			grid[1][26] = "region-47";
			grid[1][29] = "region-49";
			grid[1][32] = "region-32";
			grid[1][35] = "region-8";
			grid[1][46] = "datacenter-3";

			//grid[2][0] = "datacenter-5";
			grid[2][0] = "region-10";
			grid[2][11] = "region-14";
			grid[2][14] = "region-45";
			grid[2][17] = "region-43";
			grid[2][20] = "region-40";
			grid[2][23] = "region-44";
			grid[2][26] = "region-42";
			grid[2][29] = "region-30";
			grid[2][32] = "region-3";
			grid[2][43] = "region-31";
			grid[2][46] = "datacenter-5";
			
			grid[3][0] = "region-20";
			grid[3][3] = "region-19";
			grid[3][6] = "region-18";
			grid[3][9] = "region-17";
			//grid[3][12] = "datacenter-1";
			grid[3][12] = "region-15";
			grid[3][15] = "region-5";
			grid[3][26] = "region-16";
			grid[3][29] = "region-6";
			grid[3][40] = "region-38";
			grid[3][43] = "region-29";
			grid[3][46] = "datacenter-1";

			grid[4][0] = "region-21";
			grid[4][3] = "region-22";
			grid[4][6] = "region-23";
			grid[4][9] = "region-24";
			grid[4][12] = "region-25";
			grid[4][15] = "region-4";
			grid[4][26] = "region-26";
			grid[4][29] = "region-27";
			grid[4][32] = "region-28";
			grid[4][35] = "region-7";
			grid[4][46] = "datacenter-4";
			grid[4][47] = "headquarters";
			
		} else {
			throw new RuntimeException("Grod layout not implemented");
		}
		
		currentLayout = layout;
	}

	/**
	 * Returns the list of business units
	 */
	public HashMap<String, Businessunit> getBusinessunits() {
		return businessunits;
	}

	/**
	 * Finds column containing business unit with a given name
	 */
	public int getCol(String businessunitName) {
		for (int i = 0; i < getColCount(); i++)
			for (int j = 0; j < getRowCount(); j++)
				if (businessunitName.equals(grid[i][j]))
					return i;
		throw new IllegalArgumentException("There is no businessunit with name = " + businessunitName);
	}

	/**
	 * Finds row containing business unit with a given name
	 */
	public int getRow(String businessunitName) {
		for (int i = 0; i < getColCount(); i++)
			for (int j = 0; j < getRowCount(); j++)
				if (businessunitName.equals(grid[i][j]))
					return j;
		throw new IllegalArgumentException("There is no businessunit with name = " + businessunitName);
	}

	/**
	 * Returns x coordinate of a business unit having a given name
	 */
	public int getBusinessunitX(String businessunitName) {
		return getColX(getCol(businessunitName));
	}

	/**
	 * Returns y coordinate of a business unit having a given name
	 */
	public int getBusinessunitY(String businessunitName) {
		return getColY(getRow(businessunitName));
	}

	/**
	 * Returns name of a business unit located at given column and row (counts start at 0), or null if there the cell is empty
	 */
	public String getBusinessunitNameAt(int col, int row) {
		return grid[col][row];
	}

	/**
	 * Returns business unit located at given column and row (counts start at 0), or null if there the cell is empty
	 */
	public Businessunit getBusinessunitAt(int col, int row) {
		if (grid[col][row] == null)
			return null;
		return businessunits.get(grid[col][row]);
	}

	/**
	 * Returns businessunit height in pixels
	 */
	public int getBusinessunitHeight(Businessunit businessunit) {
		if (businessunit.name.charAt(0) == 'h' || businessunit.name.charAt(0) == 'd') {
			return ROW_HEIGHT;
		} else {
			if (Businessunit.extractIdFromName(businessunit.name) <= 10)
				return 200 + 2 + 4;
			else
				return 50 + 2 + 2;
		}
	}

	/**
	 * Returns facility height in pixels
	 * - branch (≈100 machines): 1
	 * - headqaurters
	 *     small regions (≈500 machines): 2
	 *     large regions (≈21K machines): 4
	 *     headquarters (≈ 15K machines): 20
	 * - datacenter (≈50K machines): 36
	 * 
	 */
	public int getFacilityHeight(Facility f) {
		switch (f.facilityName.charAt(0)) {
		case 'b':
			return 1;
		case 'h':
			int buId = Businessunit.extractIdFromName(f.businessunitName);
			if (buId == 0) {
				return ROW_HEIGHT;
			} else if (buId <= 10) {
				return 4;
			} else {
				return 2;
			}
		case 'd':
			return ROW_HEIGHT;
		}

		return -1;
	}

	/**
	 * Returns the size of the gap in pixels between two facilities
	 * Only headquarters are surrounded by a gap of 1 pixel.
	 */
	public int getGapBetweenFacilities(Facility f1, Facility f2) {
		if (f1 == null || f2 == null)
			return 0;

		char l1 = f1.facilityName.charAt(0);
		char l2 = f2.facilityName.charAt(0);

		if (l1 == 'h' || l2 == 'h')
			return 1;
		else
			return 0;
	}

	/**
	 * Returns number of the columns in the grid
	 */
	public int getColCount() {
		return colCount;
	}

	/**
	 * Returns number of the rows in the grid
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Returns x coordinate of a given column (count starts at 0)
	 */
	public int getColX(int col) {
		return col * (COL_WIDTH + CELLPADDING_H) + PADDING;
	}

	/**
	 * Returns y coordinate of a given row (count starts at 0)
	 */
	public int getColY(int row) {
		return row * (ROW_HEIGHT + CELLPADDING_V) + PADDING;
	}

	/**
	 * Returns width of the grid
	 */
	public int getWidth() {
		return getColX(colCount) - CELLPADDING_H + PADDING;
	}

	/**
	 * Returns height of the grid
	 */
	public int getHeight() {
		return getColY(rowCount) - CELLPADDING_V + PADDING;
	}

	public void sortFacilities() {
		for (Businessunit bu : businessunits.values()) {
			bu.sortFacilities();
		}
	}

}
