package game.circuitsimulator.simulator;

import org.junit.Test;

import com.google.gson.Gson;

import game.circuitsimulator.design.Direction;
import game.circuitsimulator.design.Layer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Set;

public class TestLayerSimulator {
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

		LayerSimulator sim = new LayerSimulator(l);

		// make sure the spiral was joined correctly

		assertThat(sim.metalTraces.size(), is(equalTo(1)));

		// now break the spiral somewhere
		l.removeMetal(2, 4);

		sim = new LayerSimulator(l);

		assertThat(sim.metalTraces.size(), is(equalTo(2)));

		// now put a non connected bit in

		l.setMetal(2, 4);

		sim = new LayerSimulator(l);

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

		LayerSimulator sim = new LayerSimulator(l);

		assertThat(sim.siliconTraces.size(), is(equalTo(4)));

		System.out.println(l);
	}

	@Test
	public void test() {
		Layer l = new Layer(10, 10);

		l.setNSilicon(3, 2);
		l.setNSilicon(3, 3);
		l.setNSilicon(3, 4);
		l.setNSilicon(3, 5);
		l.setNSilicon(3, 6);
		l.setPSilicon(4, 4);

		l.setMetal(3, 2);
		l.setMetal(3, 6);

		l.getSiliconAt(3, 2).setVia(true);
		l.getSiliconAt(3, 6).setVia(true);

		l.connectSilicon(3, 2, Direction.NORTH);
		l.connectSilicon(3, 3, Direction.NORTH);
		l.connectSilicon(3, 4, Direction.NORTH);
		l.connectSilicon(3, 5, Direction.NORTH);

		l.connectSilicon(4, 4, Direction.WEST);

		LayerSimulator sim = new LayerSimulator(l);
		
		Set<Trace> traces = sim.reduceTrace(sim.metalTraces, sim.siliconTraces, l);
		
		Gson gson = new Gson();
		
		System.out.println(gson.toJson(traces));
	}
}
