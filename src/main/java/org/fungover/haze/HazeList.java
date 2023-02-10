package org.fungover.haze;

import java.util.ArrayList;

public class HazeList<T> {

	private ArrayList<T> list;

	public HazeList() {
		this.list = new ArrayList<>();
	}

	public void LPUSH(T value) {
		this.list.add(0, value);
	}

	public void RPUSH(T value) {
		this.list.add(value);
	}

	public T RPOP() {
		return this.list.remove(0);
	}

	public T LPOP() {
		int lastSpot = list.size()-1;
		return this.list.remove(lastSpot);
	}

	public int LLEN() {
		return this.list.size();

	}
	public T LMOVE(HazeList<T> source, HazeList<T> destination, String whereFrom, String whereTo) {
		T value = null;
		if (whereFrom.equals("LEFT") && whereTo.equals("LEFT")) {
			value = source.LPOP();
			destination.LPUSH(value);
		}
		else if (whereFrom.equals("LEFT") && whereTo.equals("RIGHT")) {
			value = source.LPOP();
			destination.RPUSH(value);
		}
		else if (whereFrom.equals("RIGHT") && whereTo.equals("LEFT")) {
			value = source.RPOP();
			destination.LPUSH(value);
		}
		else if (whereFrom.equals("RIGHT") && whereTo.equals("RIGHT")) {
			value = source.RPOP();
			destination.RPUSH(value);
		}
		return value;
	}


	public void LTRIM(int start, int stop) {
		try {
			list = new ArrayList<>(list.subList(start, stop+1));
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("The inputs are outside the range of the list.");
		}
	}
}

