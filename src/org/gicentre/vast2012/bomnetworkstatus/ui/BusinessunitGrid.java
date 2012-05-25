package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.util.HashMap;
import org.gicentre.vast2012.bomnetworkstatus.Businessunit;
import org.gicentre.vast2012.bomnetworkstatus.Facility;

/**
 * The grid is a collection of business units attached to the cells
 */
public class BusinessunitGrid {

	public static final int COL_WIDTH = 192;
	public static final int ROW_HEIGHT = 15;

	public static final int LAYOUT_GEO = 1;
	public static final int LAYOUT_SEQ = 2;

	public static final int PADDING = 3;
	public static final int CELLPADDING_H = 4;
	public static final int CELLPADDING_V = 4;

	protected int colCount = 0;
	protected int rowCount = 0;

	protected HashMap<String, Businessunit> businessunits;
	protected String[][] grid;

	public BusinessunitGrid(HashMap<String, Businessunit> businessunits, int layout) {

		this.businessunits = businessunits;

		setLayout(layout);
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

		} else {
			throw new RuntimeException("Layout not implemented");
		}
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
				return 200 + 2 + 11;
			else
				return 50 + 2 + 3;
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
