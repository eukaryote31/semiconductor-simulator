package game.circuitsimulator.design;

public enum SiliconType {
	N, P, JUNC_NPN, JUNC_PNP;
	
	public boolean isJunction() {
		return this == JUNC_NPN || this == JUNC_PNP;
	}
}
