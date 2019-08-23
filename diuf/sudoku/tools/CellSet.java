/**
 * 
 */
package diuf.sudoku.tools;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import diuf.sudoku.Cell;
import diuf.sudoku.Grid;

/**
 * @author Mladen Dobrichev
 *
 */
public class CellSet implements Set<Cell> {

	private final BitSet bits = new BitSet();
	
	public CellSet(CellSet c) {
		//bits.clear();
		bits.or(c.bits);
	}

	public CellSet(Collection<Cell> c) {
		for(Cell cell : c) {
			bits.set(cell.getIndex());
		}
	}
	
	public CellSet(int[] c) {
		for(int i : c) {
			bits.set(i);
		}
	}
	
	@Override
	public boolean add(Cell cell) {
		int i = cell.getIndex();
//		boolean ret = bits.get(i);
//		bits.set(i);
//		return ret;
		bits.set(i);
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Cell> c) {
		if(c instanceof CellSet) {
			bits.or(((CellSet) c).bits);
		}
		else {
			for(Cell cell : c) {
				bits.set(cell.getIndex());
			}
		}
		return false;
	}

	@Override
	public void clear() {
		bits.clear();
	}

	@Override
	public boolean contains(Object o) {
		if(o instanceof Cell) {
			return bits.get(((Cell)o).getIndex());
		}
		else if(o instanceof CellSet) {
			BitSet cl = (BitSet)(((CellSet) o).bits.clone());
			cl.andNot(bits);
			return cl.isEmpty();
		}
		throw new ClassCastException();
		//return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		if(c instanceof CellSet) {
			BitSet cl = (BitSet)(((CellSet) c).bits.clone());
			cl.andNot(bits);
			return cl.isEmpty();
		}
		for(Cell cell : (Collection<Cell>)c) {
			if(!bits.get(cell.getIndex())) return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return bits.isEmpty();
	}

	@Override
	public Iterator<Cell> iterator() {
		return new CellIterator();
	}

	@Override
	public boolean remove(Object o) {
		if(o instanceof Cell) {
			bits.clear(((Cell)o).getIndex());
			return false;
		}
		else if(o instanceof CellSet) {
			bits.andNot((BitSet)(((CellSet) o).bits));
			return false;
		}
		throw new ClassCastException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(c instanceof CellSet) {
			bits.andNot((BitSet)(((CellSet) c).bits));
			return false;
		}
		for(Cell cell : (Collection<Cell>)c) {
			bits.clear(cell.getIndex());
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(c instanceof CellSet) {
			bits.and((BitSet)(((CellSet) c).bits));
			return false;
		}
		CellSet other = new CellSet((Collection<Cell>)c);
		bits.and(other.bits);
		return false;
	}

	@Override
	public int size() {
		return bits.size();
	}

	@Override
	public Object[] toArray() {
		Cell[] ret = new Cell[bits.cardinality()];
		int i = 0;
		for(Cell c : this) {
			ret[i] = c;
		}
		return ret;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}
	public class CellIterator implements Iterator<Cell> {
		private int previous = -1;
		@Override
		public boolean hasNext() {
			return bits.nextSetBit(previous + 1) != -1;
		}

		@Override
		public Cell next() {
			previous = bits.nextSetBit(previous + 1);
			return Grid.getCell(previous);
		}
	}
}
