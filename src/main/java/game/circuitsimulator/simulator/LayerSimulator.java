package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;

import game.circuitsimulator.design.Direction;
import game.circuitsimulator.design.Layer;
import game.circuitsimulator.design.Metal;
import game.circuitsimulator.design.Silicon;
import game.circuitsimulator.design.SiliconType;
import game.circuitsimulator.simulator.Trace.TraceNode;

public class LayerSimulator {
	Layer layer;

	List<Set<Point>> metalTraces = new LinkedList<>();
	List<Set<Point>> siliconTraces = new LinkedList<>();

	public LayerSimulator(Layer l) {
		this.layer = l;

		metalTraces = this.getMetalTraces(layer);
		siliconTraces = this.getSiliconTraces(layer);
	}

	protected Set<Trace> reduceTrace(List<Set<Point>> metal, List<Set<Point>> silicon, Layer l) {
		Set<Trace> ret = new HashSet<>();

		for (Set<Point> trace : metal) {
			ret.add(getFullTrace(trace, l, silicon, metal));
		}

		return ret;
	}

	private Trace getFullTrace(Set<Point> metal, Layer l, List<Set<Point>> siliconTraces,
			List<Set<Point>> metalTraces) {
		Set<Set<Point>> viadSilicon;
		Set<Set<Point>> viadMetal = new HashSet<>();

		viadSilicon = listViadSilicon(layer, metal, siliconTraces);

		Set<Set<Point>> prevViadS = viadSilicon;
		Set<Set<Point>> prevViadM = viadMetal;

		while (true) {
			// get all metal connected to the silicon
			for (Set<Point> s : prevViadS) {
				prevViadM = listViadMetal(layer, s, metalTraces);

				viadMetal.addAll(prevViadM);
			}

			if (prevViadM.isEmpty())
				break;

			// get all silicon connected to the metal connected to the silicon
			for (Set<Point> s : prevViadM) {
				prevViadS = listViadSilicon(layer, s, siliconTraces);

				viadSilicon.addAll(prevViadS);
			}

			if (prevViadS.isEmpty())
				break;
		}

		Set<TraceNode> nodes = new HashSet<>();

		for (Set<Point> subSet : viadSilicon) {
			for (Point s : subSet) {
				// todo: connectN and connectP
				nodes.add(new TraceNode(s, l.getSiliconAt(s.x, s.y).getType(), getNPNJunctions(l, s),
						getPNPJunctions(l, s)));
			}
		}
		
		return new Trace(l, nodes);
	}

	private Set<Point> getPNPJunctions(Layer l, Point p) {
		Set<Point> ret = new HashSet<>();

		if (l.getSiliconAt(p.x, p.y).getType() == SiliconType.JUNC_PNP)
			ret.add(p);

		for (Direction d : Direction.getDirections()) {
			Silicon s = l.getSiliconAt(d.offsetX(p.x), d.offsetY(p.y));

			if (s.getType() == SiliconType.JUNC_PNP) {
				ret.add(d.offset(p));
			}
		}

		return ret;
	}

	private Set<Point> getNPNJunctions(Layer l, Point p) {
		Set<Point> ret = new HashSet<>();

		if (l.getSiliconAt(p.x, p.y).getType() == SiliconType.JUNC_NPN)
			ret.add(p);

		for (Direction d : Direction.getDirections()) {
			Silicon s = l.getSiliconAt(d.offsetX(p.x), d.offsetY(p.y));

			if (s.getType() == SiliconType.JUNC_NPN) {
				ret.add(d.offset(p));
			}
		}

		return ret;
	}

	private Set<String> listPads(Set<Point> metal, Layer l) {
		BiMap<Point, String> pads = l.getPads().inverse();
		Set<String> ret = new HashSet<>();

		for (Point p : metal) {
			String pad = pads.get(p);

			if (pad != null) {
				ret.add(pad);
			}
		}

		return ret;
	}

	private Set<Set<Point>> listViadSilicon(Layer layer, Set<Point> metal, List<Set<Point>> siliconTraces) {

		Set<Set<Point>> ret = new HashSet<>();

		for (Point m : metal) {
			Set<Point> siliconTrace = viadSilicon(layer, siliconTraces, m);

			if (siliconTrace != null)
				ret.add(siliconTrace);
		}

		return ret;
	}

	private Set<Set<Point>> listViadMetal(Layer layer, Set<Point> silicon, List<Set<Point>> metalTraces) {

		Set<Set<Point>> ret = new HashSet<>();

		for (Point m : silicon) {
			if (layer.getSiliconAt(m.x, m.y).isVia()) {
				for (Set<Point> s : metalTraces) {
					if (s.contains(m))
						ret.add(s);
				}
			}
		}

		return ret;
	}

	private Set<Point> viadSilicon(Layer layer, List<Set<Point>> siliconTraces, Point metal) {
		Silicon s = layer.getSiliconAt(metal.x, metal.y);

		if (s.isVia()) {
			for (Set<Point> trace : siliconTraces) {
				if (trace.contains(metal))
					return trace;
			}
		}

		return null;
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

				// junction
				if (s.getType() == SiliconType.JUNC_NPN || s.getType() == SiliconType.JUNC_PNP) {
					Set<Point> newSet = new HashSet<>();
					newSet.add(p);

					ret.add(newSet);
				}

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
}
