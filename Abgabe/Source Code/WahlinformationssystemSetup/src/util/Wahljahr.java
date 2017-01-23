package util;

public enum Wahljahr {
	Y2009(2009), Y2013(2013);

	private int wahljahr;

	private Wahljahr(int wahljahr) {
		this.wahljahr = wahljahr;
	}

	public int toInt() {
		return wahljahr;
	}

	public static Wahljahr parse(String year) {
		return valueOf(Integer.valueOf(year));
	}

	public static Wahljahr valueOf(int year) {
		if (year == 2009) {
			return Y2009;
		} else if (year == 2013) {
			return Y2013;
		} else
			return null;
	}
}
