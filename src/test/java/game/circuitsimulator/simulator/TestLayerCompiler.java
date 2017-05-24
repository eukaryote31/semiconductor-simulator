package game.circuitsimulator.simulator;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import game.circuitsimulator.design.Direction;
import game.circuitsimulator.design.Layer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class TestLayerCompiler {
	// @Test
	public void testMetalTraceSpiral() {
		Layer l = new Layer(10, 10);

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				l.setMetal(x, y);
			}
		}

		// make spiral
		for (int i = 0; i < 5; i++)
			for (int j = i; j < 10 - i; j++)
				l.connectMetal(j, i, Direction.EAST);

		for (int i = 0; i < 5; i++)
			for (int j = i; j < 9 - i; j++)
				l.connectMetal(j, 9 - i, Direction.EAST);

		for (int i = 0; i < 5; i++)
			for (int j = i; j < 9 - i; j++)
				l.connectMetal(i, j + 1, Direction.SOUTH);

		for (int i = 0; i < 5; i++)
			for (int j = i; j < 8 - i; j++)
				l.connectMetal(9 - i, j + 2, Direction.SOUTH);

		System.out.println(l);

		LayerCompiler sim = new LayerCompiler(l);

		// make sure the spiral was joined correctly

		assertThat(sim.metalTraces.size(), is(equalTo(1)));

		// now break the spiral somewhere
		l.removeMetal(2, 4);

		sim = new LayerCompiler(l);

		assertThat(sim.metalTraces.size(), is(equalTo(2)));

		// now put a non connected bit in

		l.setMetal(2, 4);

		sim = new LayerCompiler(l);

		assertThat(sim.metalTraces.size(), is(equalTo(3)));

	}

	@Test
	public void testSiliconTrace() {
		Layer l = new Layer(10, 10);

		l.setNSilicon(3, 2);
		l.setNSilicon(3, 3);
		l.setNSilicon(3, 4);
		l.setNSilicon(3, 5);
		l.setPSilicon(4, 4);

		l.connectSilicon(3, 2, Direction.NORTH);
		l.connectSilicon(3, 3, Direction.NORTH);
		l.connectSilicon(3, 4, Direction.NORTH);

		l.connectSilicon(4, 4, Direction.WEST);

		LayerCompiler sim = new LayerCompiler(l);

		assertThat(sim.siliconTraces.size(), is(equalTo(4)));

		System.out.println(l);
	}

	@Test
	public void testViaMap() {
		LayerCompiler lc = new LayerCompiler(new Layer(10, 10));

		List<Set<Point>> metalTraces = new LinkedList<>();
		metalTraces.add(makeSet(new Point(0, 0), new Point(0, 1), new Point(0, 2)));
		metalTraces.add(makeSet(new Point(0, 4)));
		
		List<Set<Point>> siliconTraces = new LinkedList<>();
		siliconTraces.add(makeSet(new Point(0, 0), new Point(1, 0), new Point(2, 0)));

		Multimap<Point, Point> metalToSilicon = HashMultimap.create();
		Multimap<Point, Point> siliconToMetal = HashMultimap.create();

		Set<Point> via = makeSet(new Point(0, 0));
		
		lc.viaMap((p) -> {
			return via.contains(p);
		}, metalToSilicon, siliconToMetal, metalTraces, siliconTraces);
		
		System.out.println(metalToSilicon);
		System.out.println(siliconToMetal);
	}

	private static <T> Set<T> makeSet(T... elements) {
		Set<T> ret = new HashSet<>();

		for (T t : elements)
			ret.add(t);

		return ret;
	}
}
