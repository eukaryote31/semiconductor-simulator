package game.circuitsimulator.simulator;

import java.awt.Point;

import game.circuitsimulator.design.Layer;
import lombok.Getter;

public class Pad extends LayerComponent {
	transient boolean externalPower = false;

	transient CompiledLayer layer;

	@Getter
	Point location;

	@Getter
	String name;

	public Pad(CompiledLayer layer, String name, Point location) {
		this.name = name;
		this.location = location;
		this.layer = layer;
	}

	@Override
	public void update() {
		LayerComponent com = layer.getComponent(location);
		
		if(this.isPowered()) {
			com.power(this);
		} else {
			com.depower(this);
		}
	}

	public void powerOn() {
		externalPower = true;
	}

	public void powerOff() {
		externalPower = false;
	}

	@Override
	public boolean hasPowerOtherThan(LayerComponent from) {
		return externalPower || super.hasPowerOtherThan(from);
	}

	@Override
	public boolean isPowered() {
		return externalPower || super.isPowered();
	}

}
