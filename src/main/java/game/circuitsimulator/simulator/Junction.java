package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import game.circuitsimulator.design.SiliconType;
import lombok.Getter;
import lombok.Setter;

public class Junction extends LayerComponent {
	@Setter
	transient CompiledLayer layer;
	transient List<LayerComponent> switchingPoweredBy = new LinkedList<>();

	public Junction(SiliconType t, CompiledLayer layer) {
		this.type = t;
		if (!this.type.isJunction())
			throw new IllegalArgumentException("SiliconType on Junction must be JUNC_NPN or JUNC_PNP!");

		this.layer = layer;
	}

	@Getter
	private SiliconType type;

	Point[] connNLayer;
	Point[] connPLayer;

	public void powerSwitch(LayerComponent from) {
		switchingPoweredBy.add(from);
	}

	public void depowerSwitch(LayerComponent from) {
		switchingPoweredBy.remove(from);
	}

	public boolean isSwitchPowered() {
		return !switchingPoweredBy.isEmpty();
	}

	public boolean output() {
		// PNP is connected by default, NPN is disconnected by default
		boolean isConnected = isSwitchPowered() ^ (getType() == SiliconType.JUNC_PNP);

		// Output is only on if gate is receiving input power AND is connected
		return isConnected && isPowered();
	}

	public Point[] getOutputs() {
		return (getType() == SiliconType.JUNC_PNP) ? connPLayer : connNLayer;
	}

	@Override
	public void update() {
		for (Point p : getOutputs()) {
			Trace lc = layer.getTrace(p);

			// prevent cyclic powering; on depower cycles this will obviously
			// evaluate to true
			if (!this.isPoweredBy(lc)) {
				if (this.isPowered())
					lc.power(this);
				else
					lc.depower(this);
			}
		}
	}
}
