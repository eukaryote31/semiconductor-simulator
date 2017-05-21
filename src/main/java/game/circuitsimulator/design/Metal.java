package game.circuitsimulator.design;

import lombok.Data;

@Data
public class Metal implements Cloneable, Connectable {
	boolean connectedN = false;
	boolean connectedS = false;
	boolean connectedE = false;
	boolean connectedW = false;

	@Override
	public Metal clone() {
		Metal m = new Metal();

		m.setConnectedN(this.connectedN);
		m.setConnectedS(this.connectedS);
		m.setConnectedE(this.connectedE);
		m.setConnectedW(this.connectedW);

		return m;
	}

	@Override
	public String toString() {
		final String none = "\u00b7";
		final String north = "\u2579";
		final String south = "\u257b";
		final String east = "\u257a";
		final String west = "\u2578";
		final String northwest = "\u251b";
		final String northsouth = "\u2503";
		final String northeast = "\u2517";
		final String southwest = "\u2513";
		final String southeast = "\u250f";
		final String eastwest = "\u2501";
		final String northsoutheast = "\u2523";
		final String northsouthwest = "\u252b";
		final String northeastwest = "\u253b";
		final String southeastwest = "\u2533";
		final String all = "\u254b";

		// bit order: W,E,S,N
		String[] map = { none, north, south, northsouth, east, northeast, southeast, northsoutheast, west, northwest,
				southwest, northsouthwest, eastwest, northeastwest, southeastwest, all };

		int n = isConnectedN() ? 1 : 0;
		int s = isConnectedS() ? 1 : 0;
		int e = isConnectedE() ? 1 : 0;
		int w = isConnectedW() ? 1 : 0;

		return map[w * 8 + e * 4 + s * 2 + n];
	}
}
