package job_selection;

public class Pick {
	private Item item;
	private int count;

	public Pick(Item i, int c) {
		this.item = i;
		this.count = c;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


}
