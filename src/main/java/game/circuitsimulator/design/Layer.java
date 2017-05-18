package game.circuitsimulator.design;

import javax.annotation.Nullable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode
public class Layer {
	@Getter
	Metal[][] metalLayer;
	@Getter
	Silicon[][] siliconLayer;

	public Layer(int width, int height) {
		this(new Metal[width][height], new Silicon[width][height]);
	}

	public Layer(@NonNull Metal[][] metalLayer, @NonNull Silicon[][] siliconLayer) {
		this.metalLayer = metalLayer;
		this.siliconLayer = siliconLayer;
	}

	public Layer(Layer l) {
		// clone is probably the best solution here
		this.metalLayer = l.getMetalLayer().clone();
		this.siliconLayer = l.getSiliconLayer().clone();
	}

	public Silicon getSiliconAt(int x, int y) {
		return siliconLayer[x][y];
	}

	public void setSiliconAt(int x, int y, @Nullable Silicon s) {
		siliconLayer[x][y] = s;
	}

	public void setMetalAt(int x, int y, @Nullable Metal m) {
		metalLayer[x][y] = m;
	}

	public Metal getMetalAt(int x, int y) {
		return metalLayer[x][y];
	}

	public void setNSilicon(int x, int y) {
		setSiliconAt(x, y, new Silicon(SiliconType.N));
	}

	public void setPSilicon(int x, int y) {
		setSiliconAt(x, y, new Silicon(SiliconType.P));
	}

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

	public void removeSilicon(int x, int y) {
		setSiliconAt(x, y, null);

		for (Direction d : Direction.getDirections()) {
			Silicon tgt = getSiliconAt(d.offsetX(x), d.offsetY(y));

			// diconnect
			if (tgt != null && tgt.isConnected(d.opposite()))
				tgt.setConnected(false, d.opposite());
		}
	}

}
