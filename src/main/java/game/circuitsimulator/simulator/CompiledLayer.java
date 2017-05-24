package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

public class CompiledLayer {
	transient List<LayerComponent> toUpdate;

	LayerComponent[] traces;

	Point[] npnJunctions;
	Point[] pnpJunctions;

	Map<String, Pad> pads;

	Map<Point, Integer> index;

	public Trace getTrace(Point p) {
		return (Trace) traces[index.get(p)];
	}

	public Junction getJunction(Point p) {
		return (Junction) traces[index.get(p)];
	}

	public LayerComponent getComponent(Point p) {
		return traces[index.get(p)];
	}

	public void tick() {
		new ArrayList<>(toUpdate).parallelStream().forEach((component) -> {
			component.update();
		});
	}

	public void setPadOn(String pad) {
		Pad p = pads.get(pad);
		p.powerOn();
		Trace c = getTrace(p.getLocation());
		toUpdate.add(p);
	}

	public String toJSON(Gson gson) {
		return gson.toJson(this);
	}
}
