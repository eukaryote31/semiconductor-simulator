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
}
