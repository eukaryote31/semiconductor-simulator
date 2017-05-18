package circuitgame.simulator.design;

import lombok.Data;
import lombok.NonNull;

@Data
public class Silicon implements Cloneable, Connectable {

	@NonNull
	SiliconType type;

	boolean via = false;

	boolean connectedN = false;
	boolean connectedS = false;
	boolean connectedE = false;
	boolean connectedW = false;

	@Override
	public Silicon clone() {
		Silicon m = new Silicon(this.getType());

		m.setConnectedN(this.connectedN);
		m.setConnectedS(this.connectedS);
		m.setConnectedE(this.connectedE);
		m.setConnectedW(this.connectedW);

		m.setVia(this.isVia());

		return m;
	}
}
