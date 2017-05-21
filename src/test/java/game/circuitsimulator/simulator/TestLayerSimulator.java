package game.circuitsimulator.simulator;

import org.junit.Test;

import game.circuitsimulator.design.Direction;
import game.circuitsimulator.design.Layer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestLayerSimulator {
	@Test
	public void testMetalTraceSpiral() {
		Layer l = new Layer(10, 10);

		for (int x = 0; x < 10; x++) {
			for(int y = 0; y < 10; y++) {
				l.setMetal(x, y);
			}
		}
		
		// make spiral
		for(int i = 0; i < 5; i++)
			for(int j = i; j < 10 - i; j++)
				l.connectMetal(j, i, Direction.EAST);
		
		for(int i = 0; i < 5; i++)
			for(int j = i; j < 9 - i; j++)
				l.connectMetal(j, 9 - i, Direction.EAST);

		for(int i = 0; i < 5; i++)
			for(int j = i; j < 9 - i; j++)
				l.connectMetal(i, j + 1, Direction.SOUTH);
		
		for(int i = 0; i < 5; i++)
			for(int j = i; j < 8 - i; j++)
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
}
