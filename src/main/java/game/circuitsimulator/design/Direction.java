package game.circuitsimulator.design;

import java.awt.Point;

import lombok.Getter;

public enum Direction {
	NORTH, SOUTH, EAST, WEST;
	
	@Getter(lazy = true)
	private static final Direction[] directions = Direction.class.getEnumConstants();
	

	public int offsetX(int x) {
		if (this == EAST)
			return x + 1;
		else if (this == WEST)
			return x - 1;
		else
			return x;
	}

	public int offsetY(int y) {
		if (this == NORTH)
			return y + 1;
		else if (this == SOUTH)
			return y - 1;
		else
			return y;
	}
	
	public Point offset(Point p) {
		return new Point(offsetX(p.x), offsetY(p.y));
	}

	public Direction opposite() {
		switch (this) {
		case NORTH:
			return SOUTH;
		case SOUTH:
			return NORTH;
		case EAST:
			return WEST;
		case WEST:
			return EAST;
		default:
			throw new AssertionError();
		}
	}
}
