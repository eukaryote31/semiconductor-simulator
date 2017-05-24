package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import game.circuitsimulator.design.Direction;
import game.circuitsimulator.design.Layer;
import game.circuitsimulator.design.Metal;
import game.circuitsimulator.design.Silicon;
import game.circuitsimulator.design.SiliconType;

public class LayerCompiler {
	Layer layer;

	List<Set<Point>> metalTraces = new LinkedList<>();
	List<Set<Point>> siliconTraces = new LinkedList<>();

	public LayerCompiler(Layer l) {
		this.layer = l;

		metalTraces = this.getMetalTraces(layer);
		siliconTraces = this.getSiliconTraces(layer);
	}

	protected List<Set<Point>> getMetalTraces(Layer layer) {
		List<Set<Point>> ret = new LinkedList<>();

		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				Metal m = layer.getMetalAt(x, y);

				if (m == null)
					continue;

				Point p = new Point(x, y);

				// We don't care about the other two directions because we're
				// starting in the top left corner, so only the up and the left
				// matter (connections go both ways, so if one way is connected,
				// you can assume that the opposite way on the adjacent cell is
				// connected)

				boolean connectedSouth = m.isConnected(Direction.SOUTH);
				boolean connectedWest = m.isConnected(Direction.WEST);

				if (connectedSouth && connectedWest) {
					// if NORTH and WEST are two different traces, delete both
					// and then push a new trace with the members of both AND
					// the new position

					Point ps = Direction.SOUTH.offset(p);
					Set<Point> traceNorth = partOfTrace(ret, ps);

					Point pw = Direction.WEST.offset(p);
					Set<Point> traceWest = partOfTrace(ret, pw);

					Set<Point> both = new HashSet<>();
					both.addAll(traceNorth);
					both.addAll(traceWest);
					both.add(p);

					ret.remove(traceNorth);
					ret.remove(traceWest);
					ret.add(both);

				} else if (connectedSouth) {
					// if connected to NORTH, then add position to NORTH trace

					Point q = Direction.SOUTH.offset(p);
					Set<Point> trace = partOfTrace(ret, q);

					if (trace != null)
						trace.add(p);
					else
						throw new IllegalStateException(
								"Invalid layer: Metal at " + p + " connects to empty cell SOUTH");
				} else if (connectedWest) {
					// if connected to WEST, then add position to WEST trace
					Point q = Direction.WEST.offset(p);
					Set<Point> trace = partOfTrace(ret, q);

					if (trace != null) {
						trace.add(p);
					} else
						throw new IllegalStateException(
								"Invalid layer: Metal at " + p + " connects to empty cell WEST " + ret);
				} else {
					// not connected

					Set<Point> newSet = new HashSet<>();
					newSet.add(p);

					ret.add(newSet);
				}
			}
		}

		return ret;
	}

	protected List<Set<Point>> getSiliconTraces(Layer layer) {
		List<Set<Point>> ret = new LinkedList<>();

		for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				Silicon s = layer.getSiliconAt(x, y);

				if (s == null)
					continue;

				Point p = new Point(x, y);

				// We don't care about the other two directions because we're
				// starting in the top left corner, so only the up and the left
				// matter (connections go both ways, so if one way is connected,
				// you can assume that the opposite way on the adjacent cell is
				// connected)

				boolean connectedSouth = s.isConnected(Direction.SOUTH) && layer
						.getSiliconAt(Direction.SOUTH.offsetX(x), Direction.SOUTH.offsetY(y)).getType() == s.getType();
				boolean connectedWest = s.isConnected(Direction.WEST) && layer
						.getSiliconAt(Direction.WEST.offsetX(x), Direction.WEST.offsetY(y)).getType() == s.getType();

				if (connectedSouth && connectedWest) {
					// if NORTH and WEST are two different traces, delete both
					// and then push a new trace with the members of both AND
					// the new position

					Point ps = Direction.SOUTH.offset(p);
					Set<Point> traceNorth = partOfTrace(ret, ps);

					Point pw = Direction.WEST.offset(p);
					Set<Point> traceWest = partOfTrace(ret, pw);

					Set<Point> both = new HashSet<>();
					both.addAll(traceNorth);
					both.addAll(traceWest);
					both.add(p);

					ret.remove(traceNorth);
					ret.remove(traceWest);
					ret.add(both);

				} else if (connectedSouth) {
					// if connected to NORTH, then add position to NORTH trace

					Point q = Direction.SOUTH.offset(p);
					Set<Point> trace = partOfTrace(ret, q);

					if (trace != null)
						trace.add(p);
					else
						throw new IllegalStateException(
								"Invalid layer: Silicon at " + p + " connects to empty cell SOUTH");
				} else if (connectedWest) {
					// if connected to WEST, then add position to WEST trace
					Point q = Direction.WEST.offset(p);
					Set<Point> trace = partOfTrace(ret, q);

					if (trace != null) {
						trace.add(p);
					} else
						throw new IllegalStateException(
								"Invalid layer: Silcion at " + p + " connects to empty cell WEST " + ret);
				} else {
					// not connected

					Set<Point> newSet = new HashSet<>();
					newSet.add(p);

					ret.add(newSet);
				}
			}
		}

		return ret;
	}

	private Set<Point> partOfTrace(List<Set<Point>> partTraces, Point p) {

		for (Set<Point> s : partTraces) {

			if (s.contains(p))
				return s;
		}

		return null;
	}

	public void runTick() {

	}
}
