package game.circuitsimulator.design;

public interface Connectable {
	default public boolean isConnected(Direction d) {
		switch (d) {
		case NORTH:
			return isConnectedN();
		case SOUTH:
			return isConnectedS();
		case EAST:
			return isConnectedE();
		case WEST:
			return isConnectedW();
		default:
			throw new AssertionError(d);
		}
	}

	default public void setConnected(boolean connected, Direction d) {
		switch (d) {
		case NORTH:
			setConnectedN(connected);
			break;
		case SOUTH:
			setConnectedS(connected);
			break;
		case EAST:
			setConnectedE(connected);
			break;
		case WEST:
			setConnectedW(connected);
			break;
		default:
			throw new AssertionError(d);
		}
	}

	public boolean isConnectedN();

	public boolean isConnectedS();

	public boolean isConnectedE();

	public boolean isConnectedW();

	public void setConnectedN(boolean connected);

	public void setConnectedS(boolean connected);

	public void setConnectedE(boolean connected);

	public void setConnectedW(boolean connected);
}
