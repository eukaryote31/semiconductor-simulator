package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import game.circuitsimulator.design.SiliconType;
import lombok.Setter;

public class Trace extends LayerComponent {
	@Setter
	transient CompiledLayer layer;

	public Trace(CompiledLayer layer) {
		this.layer = layer;
	}
	
	public void addSiliconTrace(Collection<Point> s) {
		allSiliconNodes.addAll(s);
	}
	
	public void addMetalTrace(Collection<Point> s) {
		allMetalNodes.addAll(s);
		
		final int index = layer.traces.indexOf(this);
		s.forEach((p) -> {
			layer.index.put(p, index);
		});
	}

	List<Point> allMetalNodes;
	List<Point> allSiliconNodes;

	List<Point> junctionsConnectedN;
	List<Point> junctionsConnectedP;

	@Override
	public void update() {
		updateConnected(junctionsConnectedN, SiliconType.JUNC_PNP);
		updateConnected(junctionsConnectedP, SiliconType.JUNC_NPN);
	}

	private void updateConnected(List<Point> gates, SiliconType baseType) {
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
