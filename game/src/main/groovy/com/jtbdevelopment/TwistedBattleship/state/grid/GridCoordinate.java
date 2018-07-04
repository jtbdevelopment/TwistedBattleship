package com.jtbdevelopment.TwistedBattleship.state.grid;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;

import java.io.Serializable;

/**
 * Date: 4/2/15
 * Time: 6:33 PM
 */
@SuppressWarnings("unused")
public class GridCoordinate implements Comparable<GridCoordinate>, Serializable {
    protected GridCoordinate() {
    }

    private int row;
    private int column;

    public GridCoordinate(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

    public GridCoordinate add(final int row, final int column) {
        return new GridCoordinate(this.row + row, this.column + column);
    }

    public GridCoordinate add(final GridCoordinate other) {
        return new GridCoordinate(this.row + other.getRow(), this.column + other.getColumn());
    }

    public boolean isValidCoordinate(final TBGame game) {
        int max = game.getGridSize() - 1;
        return !(row < 0 || column < 0 || row > max || column > max);
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof GridCoordinate)) return false;

        final GridCoordinate that = (GridCoordinate) o;

        if (row != that.getRow()) return false;
        return column == that.getColumn();
    }

    public int hashCode() {
        int result;
        result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public int compareTo(final GridCoordinate o) {
        if (this.row == o.getRow()) {
            if (this.column == o.getColumn()) {
                return 0;
            }

            return (this.column - o.getColumn());
        }

        return (this.row - o.getRow());
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
