package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.List;
import java.util.Set;

import game.circuitsimulator.design.SiliconType;

public class Trace extends LayerComponent {
	transient CompiledLayer layer;

	public Trace(CompiledLayer layer) {
		this.layer = layer;
	}
	
	public Trace(Trace t) {
		
	}

	Point[] allMetalNodes;
	Point[] allSiliconNodes;

	Point[] junctionsConnectedN;
	Point[] junctionsConnectedP;

	@Override
	public void update() {
		updateConnected(junctionsConnectedN, SiliconType.JUNC_PNP);
		updateConnected(junctionsConnectedP, SiliconType.JUNC_NPN);
	}

	private void updateConnected(Point[] gates, SiliconType baseType) {
		for (Point juncP : gates) {
			Junction junc = layer.getJunction(juncP);

			if (junc.getType() == baseType) {
				// connected to base of gate

				junc.powerSwitch(this);
			} else {
				// connected to input of gate

				junc.power(this);
			}
		}
	}
}
