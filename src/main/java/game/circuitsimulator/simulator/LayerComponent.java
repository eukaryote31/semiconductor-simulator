package game.circuitsimulator.simulator;

import java.util.LinkedList;
import java.util.List;

public abstract class LayerComponent {
	transient List<LayerComponent> poweredBy = new LinkedList<>();

	public boolean isPowered() {
		return !poweredBy.isEmpty();
	}
	
	public void power(LayerComponent from) {
		poweredBy.add(from);
	}
	
	public void depower(LayerComponent from) {
		poweredBy.remove(from);
	}
	
	public boolean hasPowerOtherThan(LayerComponent from) {
		return poweredBy.size() > 1 || !poweredBy.contains(from);
	}
	
	public boolean isPoweredBy(LayerComponent from) {
		return poweredBy.contains(from);
	}
	
	public abstract void update();
}
