package org.gicentre.vast2012.bomnetworkstatus.ui;

import java.awt.Rectangle;
import java.util.HashMap;

import org.gicentre.vast2012.bomnetworkstatus.Businessunit;

public class BusinessunitGrid {

	public static final int LAYOUT_GEO = 1;
	public static final int LAYOUT_SEQ = 2;

	public static int padding = 2;
	
	protected int colCount = 0;
	protected int rowCount = 0;

	protected HashMap<String, Businessunit> businessunits;
	protected String[][] grid;
	
	protected float x;
	protected float y;

	public BusinessunitGrid(HashMap<String, Businessunit> businessunits, int layout) {

		this.businessunits = businessunits;

		setLayout(layout);
	}

	public void setLayout(int layout) {
		if (layout == LAYOUT_SEQ) {
			
			colCount = 5;
			rowCount = 17;
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
				if (businessunitName.equals(grid[i][j]))
					return i;
		throw new IllegalArgumentException("There is no businessunit with name = "+businessunitName);
	}

	public int getRow(String businessunitName) {
		for (int i = 0; i < getColCount(); i++)
			for (int j = 0; j < getRowCount(); j++)
				if (businessunitName.equals(grid[i][j]))
					return j;
		throw new IllegalArgumentException("There is no businessunit with name = "+businessunitName);
	}

	public float getBuX(int col) {
		return col * (192 + padding) + padding + x;
	}

	public float getBuY(int row) {
		return row * (51 + padding) + padding + y;
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

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return getBuX(colCount) - x + padding;
	}

	public float getHeight() {
		return getBuY(rowCount) - y + padding;
	}

	public Rectangle getRectangle() {
		return new Rectangle((int)x, (int)y, (int)getWidth(), (int)getHeight());
	}
}
