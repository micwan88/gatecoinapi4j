package mic.trade.bean;

import java.util.Comparator;

public class UnitComparator implements Comparator<Unit> {
	private boolean isAsc = true;
	
	/**
	 * @param isAsc
	 */
	public UnitComparator(boolean isAsc) {
		this.isAsc = isAsc;
	}

	@Override
	public int compare(Unit unit1, Unit unit2) {
		int result = unit1.getPrice().compareTo(unit2.getPrice());
		return isAsc?result:-result;
	}

}
