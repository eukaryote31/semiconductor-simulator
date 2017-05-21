package game.circuitsimulator.simulator;

import java.awt.Point;
import java.util.Set;

import javax.annotation.Nullable;

import game.circuitsimulator.design.Layer;
import game.circuitsimulator.design.SiliconType;
import lombok.Data;

/**
 * POJO for a compiled Layer
 * 
 * @author eukaryote
 *
 */
@Data
public class Trace {
	
	public Trace(Layer l, Set<TraceNode> allNodes) {
		this.allNodes = allNodes;
	}
	
	Set<TraceNode> allNodes;

	Set<TraceNode> connectedNodes;

	Set<String> pads;

	@Data
	public static class TraceNode {
		public TraceNode(Point location, SiliconType type, Set<Point> switchesNPN, Set<Point> switchesPNP) {
			
		}
		
		Point location;

		/**
		 * Indicated the type of the node. 
		 */
		@Nullable
		SiliconType type;
		
		Set<Point> switchesNPN;
		Set<Point> switchesPNP;
	}
}
