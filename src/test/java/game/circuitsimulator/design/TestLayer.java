package game.circuitsimulator.design;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestLayer {
	@Test
	public void testConnectSilicon() {
		Layer l = new Layer(10, 10);
		l.setNSilicon(0, 0);
		l.setNSilicon(0, 1);
		
		Silicon bottom = l.getSiliconAt(0, 0);
		Silicon top = l.getSiliconAt(0, 1);
		
		l.connectSilicon(0, 0, Direction.NORTH);

		assertThat(top.isConnected(Direction.SOUTH), is(true));
		assertThat(top.isConnected(Direction.NORTH), is(not(true)));
		assertThat(bottom.isConnected(Direction.NORTH), is(true));
		assertThat(bottom.isConnected(Direction.SOUTH), is(not(true)));
		
	}
	
	@Test
	public void removeSilicon() {
		Layer l = new Layer(10, 10);
		l.setNSilicon(1, 1);
		l.setNSilicon(0, 1);
		l.setNSilicon(1, 0);
		
		Silicon middle = l.getSiliconAt(1, 1);
		Silicon left = l.getSiliconAt(0, 1);
		Silicon bottom = l.getSiliconAt(1, 0);

		// now all are connected
		l.connectSilicon(1, 1, Direction.SOUTH);
		l.connectSilicon(1, 1, Direction.WEST);

		assertThat(middle.isConnected(Direction.WEST), is(true));
		assertThat(left.isConnected(Direction.EAST), is(true));
		assertThat(middle.isConnected(Direction.SOUTH), is(true));
		assertThat(bottom.isConnected(Direction.NORTH), is(true));
		
		l.removeSilicon(1, 1);
		
		middle = l.getSiliconAt(1, 1);
		left = l.getSiliconAt(0, 1);
		bottom = l.getSiliconAt(1, 0);
		
		assertThat(left.isConnected(Direction.EAST), is(not(true)));
		assertThat(bottom.isConnected(Direction.NORTH), is(not(true)));
		assertThat(middle, is(equalTo(null)));
		
	}
}
