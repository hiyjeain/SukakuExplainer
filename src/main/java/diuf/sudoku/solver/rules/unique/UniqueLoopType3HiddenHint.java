/*
 * Project: Sudoku Explainer
 * Copyright (C) 2006-2007 Nicolas Juillerat
 * Available under the terms of the Lesser General Public License (LGPL)
 */
package diuf.sudoku.solver.rules.unique;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import diuf.sudoku.Cell;
import diuf.sudoku.Grid;
import diuf.sudoku.tools.*;
import diuf.sudoku.Settings;

public class UniqueLoopType3HiddenHint extends UniqueLoopHint {

    private final Cell c1;
    private final Cell c2;
    private final int[] otherValues;
    private final Grid.Region region;
    private final BitSet hiddenValues;
    private final int[] hiddenIndexes; // indexes of the hidden set


    public UniqueLoopType3HiddenHint(UniqueLoops rule, List<Cell> loop, int v1, int v2,
            Map<Cell, BitSet> removablePotentials, Cell c1, Cell c2, int[] otherValues, BitSet hiddenValues,
            Grid.Region region, int[] indexes) {
        super(rule, loop, v1, v2, removablePotentials);
        this.c1 = c1;
        this.c2 = c2;
        this.otherValues = otherValues;
        this.hiddenValues = hiddenValues;
        this.region = region;
        this.hiddenIndexes = indexes;
    }

    @Override
    public double getDifficulty() {
        double toAdd = hiddenIndexes.length;
		if (Settings.getInstance().revisedRating() == 1)
			toAdd = toAdd * 0.1;//Pair=0.1, Quad=0.3
		else
			toAdd = (toAdd - 1) * 0.1;//Original rating Pair=0.0, Quad=0.2
        return super.getDifficulty() + toAdd;
    }

    private Map<Cell, BitSet> appendOrangePotentials(Map<Cell, BitSet> potentials) {
        for (int i = 0; i < hiddenIndexes.length; i++) {
            int index = hiddenIndexes[i];
            Cell cell = region.getCell(index);
            BitSet values = potentials.get(cell);
            if (values == null) {
                values = new BitSet(10);
                potentials.put(cell, values);
            }
            values.or(hiddenValues);
        }
        // Add the two cells of the loop
        BitSet values = potentials.get(c1);
        if (values == null)
            potentials.put(c1, hiddenValues);
        else
            values.or(hiddenValues);
        values = potentials.get(c2);
        if (values == null)
            potentials.put(c2, hiddenValues);
        else
            values.or(hiddenValues);
        return potentials;
    }

    @Override
    public Map<Cell, BitSet> getGreenPotentials(Grid grid, int viewNum) {
        return appendOrangePotentials(super.getGreenPotentials(grid, viewNum));
    }

    @Override
    public Map<Cell, BitSet> getRedPotentials(Grid grid, int viewNum) {
        Map<Cell, BitSet> removables = super.getRemovablePotentials();
        Map<Cell, BitSet> result = new HashMap<Cell, BitSet>();
        for (Map.Entry<Cell, BitSet> entry : removables.entrySet()) {
        	result.put(entry.getKey(), (BitSet)entry.getValue().clone());
        }
        return appendOrangePotentials(result);
    }

    @Override
    public Grid.Region[] getRegions() {
        return new Grid.Region[] {region};
    }

    @Override
    public int getType() {
        return 3;
    }

	private String sharedRegions(){
		if (Settings.getInstance().isVanilla())
			return "row, column or block";
		else {
			String res[] = new String[10];
			int i = 0;
			String finalRes = "row";
			if (Settings.getInstance().isVLatin())
				return "row or column";
			else
				res[i++]= "column";
			if (Settings.getInstance().isBlocks())
				res[i++]= "block";
			if (Settings.getInstance().isDG())
				res[i++]= "disjoint group";
			if (Settings.getInstance().isWindows())
				res[i++]= "window group";
			if (Settings.getInstance().isX())
				res[i++]= "diagonal";
			if (Settings.getInstance().isGirandola())
				res[i++]= "girandola group";
			if (Settings.getInstance().isAsterisk())
				res[i++]= "asterisk group";
			if (Settings.getInstance().isCD())
				res[i++]= "center dot group";
			i--;
			for (int j = 0; j < i; j++)
				finalRes += ", " + res[j];
			finalRes += " or " + res[i];
			return finalRes;
		}
	}

    @Override
    public String toHtml(Grid grid) {
        String result = HtmlLoader.loadHtml(this, "UniqueLoopType3Hidden.html");
        String type = getTypeName();
        Cell[] loopCells = new Cell[loop.size()];
        loop.toArray(loopCells);
        String allCells = Cell.toString(loopCells);
        String cell1 = c1.toString();
        String cell2 = c2.toString();
        String valuesOrName = ValuesFormatter.formatValues(otherValues, " or ");
        final String[] setNames = new String[] {"Pair", "Triplet", "Quad", "Quintuplet",
                "Sextuplet", "Septuplet"};
        String setName = setNames[hiddenValues.cardinality() - 2];
        Cell[] cells = new Cell[hiddenIndexes.length];
        for (int i = 0; i < cells.length; i++)
            cells[i] = region.getCell(hiddenIndexes[i]);
        String otherCells = ValuesFormatter.formatCells(cells, " and ");
        String valuesAndName = ValuesFormatter.formatValues(hiddenValues, " and ");
        String regionName = region.toString();
        result = HtmlLoader.format(result, type, v1, v2, allCells, cell1,
                cell2, valuesOrName, setName, otherCells, valuesAndName, regionName, sharedRegions());
        return result;
    }

    /**
     * Overriden to differentiate hints with different naked sets.
     * <p>
     * Because we only make different objects that are equal according
     * to <tt>super.equals()</tt>, <tt>hashCode()</tt> does not need
     * to be overriden.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UniqueLoopType3HiddenHint))
            return false;
        if (!super.equals(o))
            return false;
        UniqueLoopType3HiddenHint other = (UniqueLoopType3HiddenHint)o;
        if (!this.region.equals(other.region))
            return false;
        if (!this.hiddenValues.equals(other.hiddenValues))
            return false;
        if (this.hiddenIndexes.length != other.hiddenIndexes.length)
            return false;
        for (int i = 0; i < hiddenIndexes.length; i++) {
            if (this.hiddenIndexes[i] != other.hiddenIndexes[i])
                return false;
        }
        return true;
    }

}
