package util;

public class Pair<T, S> {

	public T first;
	public S second;
	
	public Pair(T first, S second) {
		this.first = first;
		this.second = second;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean equals(Object o) {
		return o instanceof Pair
				&& ((Pair<?, ?>) o).first.getClass().equals(first.getClass())
				&& ((Pair<?, ?>) o).second.getClass().equals(second.getClass())
				&& ((T) (((Pair) o).first)).equals(first)
				&& ((S) (((Pair) o).second)).equals(second);
	}
}
