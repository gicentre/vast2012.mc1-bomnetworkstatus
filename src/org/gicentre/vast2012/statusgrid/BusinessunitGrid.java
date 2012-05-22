package org.gicentre.vast2012.statusgrid;

import java.util.HashMap;

public class BusinessunitGrid {

	public static int LAYOUT_GEO = 1;
	public static int LAYOUT_SEQ = 2;

	public static int padding = 0;
	
	protected int colCount = 0;
	protected int rowCount = 0;

	protected HashMap<String, Businessunit> businessunits;
	protected String[][] grid;
	
	public float x;
	public float y;

	public BusinessunitGrid(HashMap<String, Businessunit> businessunits, int layout) {

		this.businessunits = businessunits;

		setLayout(layout);
	}

	public void setLayout(int layout) {
		if (layout == LAYOUT_SEQ) {
			
			colCount = 5;
			rowCount = 20;
			grid = new String[colCount][rowCount];

			int row = 0;
			int col = 0;

			// Adding 10 large regions (5*2)
			for (int id = 1; id <= 10; id++) {
				addRegionToGrid(col, row, "region-" + id, 201);
				col++;
				if (col >= 5) {
					col = 0;
					row += 4;
				}
			}

			// Adding 40 small regions (5*8)
			for (int id = 11; id <= 50; id++) {
				addRegionToGrid(col, row, "region-" + id, 51);
				col++;
				if (col >= 5) {
					col = 0;
					row++;
				}
			}
		} else {
			throw new RuntimeException("Layout not implemented");
		}
	}

	public HashMap<String, Businessunit> getBusinessunits() {
		return businessunits;
	}

	private void addRegionToGrid(int col, int row, String name, int facilityCount) {
		grid[col][row] = name;
	}

	public int getRowHeight() {
		return 51;
	}

	public int getColWidth() {
		return 192;
	}

	public int getCol(String businessunitName) {
		for (int i = 0; i < getColCount(); i++)
			for (int j = 0; j < getRowCount(); j++)
				if (grid[i][j] == businessunitName)
					return i;
		throw new IllegalArgumentException("There is no businessunit with name = "+businessunitName);
	}

	public int getRow(String businessunitName) {
		for (int i = 0; i < getColCount(); i++)
			for (int j = 0; j < getRowCount(); j++)
				if (grid[i][j] == businessunitName)
					return j;
		throw new IllegalArgumentException("There is no businessunit with name = "+businessunitName);
	}

	public float getBuX(int col) {
		return col * (192 + padding) + padding;
	}

	public int getBuY(int row) {
		return row * (51 + padding) + padding;
	}
	
	public float getBuX(String businessunitName) {
		return getBuX(getCol(businessunitName));
	}

	public float getBuY(String businessunitName) {
		return getBuY(getRow(businessunitName));
	}

	public String getBuName(int col, int row) {
		return grid[col][row];
	}
	
	public Businessunit getBu(int col, int row) {
		if (grid[col][row] == null)
			return null;
		return businessunits.get(grid[col][row]);
	}

	public int getColCount() {
		return colCount;
	}

	public int getRowCount() {
		return rowCount;
	}

	public float getWidth() {
		return getBuX(rowCount) + padding;
	}

	public float getHeight() {
		return getBuY(colCount) + padding;
	}
}
