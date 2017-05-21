package game.circuitsimulator.design;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class Layer {
	@Getter
	int width;

	@Getter
	int height;

	@Getter
	Map<String, Point> pads;

	@Getter
	Metal[] metalLayer;
	@Getter
	Silicon[] siliconLayer;

	public Layer(int width, int height) {
		this(new Metal[width * height], new Silicon[width * height], width);
	}

	public Layer(Layer l) {
		// clone is probably the best solution here
		this.metalLayer = l.getMetalLayer().clone();
		this.siliconLayer = l.getSiliconLayer().clone();

		this.width = l.width;
		this.height = l.height;
		this.pads = l.pads;
	}

	public Layer(@NonNull Metal[] metalLayer, @NonNull Silicon[] siliconLayer, int width) {
		this.metalLayer = metalLayer;
		this.siliconLayer = siliconLayer;

		// TODO: sanitize input better
		this.width = width;
		this.height = metalLayer.length / width;

		this.pads = new HashMap<>();
	}

	// SILICON //

	public boolean connectSilicon(int x, int y, @NonNull Direction direction) {
		Silicon top = getSiliconAt(x, y);
		Silicon base = getSiliconAt(direction.offsetX(x), direction.offsetY(y));

		if (top == null || base == null)
			return false;

		if (top.getType() != base.getType()) {
			// NPN
			if (top.getType() == SiliconType.P && base.getType() == SiliconType.N) {
				base.setType(SiliconType.JUNC_NPN);
			}

			// PNP
			if (top.getType() == SiliconType.N && base.getType() == SiliconType.P) {
				base.setType(SiliconType.JUNC_PNP);
			}

			// otherwise one or both is a junction and we can safely let those
			// be connected
		}

		top.setConnected(true, direction);
		base.setConnected(true, direction.opposite());

		return true;
	}

	public Silicon getSiliconAt(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return siliconLayer[x + width * y];
	}

	public void removeSilicon(int x, int y) {
		setSiliconAt(x, y, null);

		for (Direction d : Direction.getDirections()) {
			Silicon tgt = getSiliconAt(d.offsetX(x), d.offsetY(y));

			// diconnect
			if (tgt != null && tgt.isConnected(d.opposite()))
				tgt.setConnected(false, d.opposite());
		}
	}

	public void setNSilicon(int x, int y) {
		setSiliconAt(x, y, new Silicon(SiliconType.N));
	}

	public void setPSilicon(int x, int y) {
		setSiliconAt(x, y, new Silicon(SiliconType.P));
	}

	public void setSiliconAt(int x, int y, @Nullable Silicon s) {
		siliconLayer[x + width * y] = s;
	}

	// METAL //

	public void setMetal(int x, int y) {
		setMetalAt(x, y, new Metal());
	}

	public void setMetalAt(int x, int y, @Nullable Metal m) {
		metalLayer[x + width * y] = m;
	}

	public Metal getMetalAt(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		return metalLayer[x + width * y];
	}

	public boolean connectMetal(int x, int y, @NonNull Direction direction) {
		Metal a = getMetalAt(x, y);
		Metal b = getMetalAt(direction.offsetX(x), direction.offsetY(y));

		if (a == null || b == null)
			return false;

		a.setConnected(true, direction);
		b.setConnected(true, direction.opposite());

		return true;
	}

	public void removeMetal(int x, int y) {
		setMetalAt(x, y, null);

		for (Direction d : Direction.getDirections()) {
			Metal tgt = getMetalAt(d.offsetX(x), d.offsetY(y));

			// diconnect
			if (tgt != null && tgt.isConnected(d.opposite()))
				tgt.setConnected(false, d.opposite());
		}
	}

	// PADS

	public boolean addPad(@NonNull String name, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return false;

		// pads have to be on metal
		if (getMetalAt(x, y) == null)
			return false;

		Point p = new Point(x, y);

		return p.equals(pads.putIfAbsent(name, p));
	}

	public Point getPadLocation(@NonNull String name) {
		return pads.get(name);
	}

	public boolean removePad(@NonNull String name) {
		return pads.remove(name) != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Metal Layer: \n");
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				Metal m = this.getMetalAt(x, y);
				sb.append(m == null ? " " : m.toString());
			}

			sb.append("\n");
		}

		sb.append("\n\nSilicon layer (N): \n");
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				Silicon m = this.getSiliconAt(x, y);
				sb.append(m == null || m.getType() != SiliconType.N ? " " : m.toString());
			}

			sb.append("\n");
		}

		sb.append("\n\nSilicon layer (P): \n");
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				Silicon m = this.getSiliconAt(x, y);
				sb.append(m == null || m.getType() != SiliconType.P ? " " : m.toString());
			}

			sb.append("\n");
		}

		sb.append("\n\nSilicon layer (NPN junctions): \n");
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				Silicon m = this.getSiliconAt(x, y);
				sb.append(m == null || m.getType() != SiliconType.JUNC_NPN ? " " : m.toString());
			}

			sb.append("\n");
		}

		sb.append("\n\nSilicon layer (PNP junctions): \n");
		for (int y = getHeight() - 1; y >= 0; y--) {
			for (int x = 0; x < getWidth(); x++) {
				Silicon m = this.getSiliconAt(x, y);
				sb.append(m == null || m.getType() != SiliconType.JUNC_PNP ? " " : m.toString());
			}

			sb.append("\n");
		}

		return sb.toString();
	}
}
