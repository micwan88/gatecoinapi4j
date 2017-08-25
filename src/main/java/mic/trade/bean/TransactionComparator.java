package mic.trade.bean;

import java.util.Comparator;

public class TransactionComparator implements Comparator<Transaction> {
	private boolean isAsc = true;
	private int sortIndex = 0;
	
	public TransactionComparator(int sortIndex, boolean isAsc) {
		this.isAsc = isAsc;
		this.sortIndex = sortIndex;
	}

	@Override
	public int compare(Transaction t1, Transaction t2) {
		int result;
		if (sortIndex == 0)
			result = t1.gettID()>t2.gettID()?1:-1;
		else
			result = t1.gettTime().compareTo(t2.gettTime());
		return isAsc?result:-result;
	}
}
