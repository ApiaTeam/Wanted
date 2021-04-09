package me.bertek41.wanted.misc;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class RayTrace {
	
	private Vector origin, direction;
	
	public RayTrace(Vector origin, Vector direction) {
		this.origin = origin.clone();
		this.direction = direction.clone();
	}
	
	public Vector getOrigin() {
		return origin;
	}
	
	public Vector getDirection() {
		return direction;
	}
	
	public double origin(int i) {
		switch(i) {
		case 0:
			return origin.getX();
		case 1:
			return origin.getY();
		case 2:
			return origin.getZ();
		default:
			return 0;
		}
	}
	
	public double direction(int i) {
		switch(i) {
		case 0:
			return direction.getX();
		case 1:
			return direction.getY();
		case 2:
			return direction.getZ();
		default:
			return 0;
		}
	}
	
	public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
		ArrayList<Vector> positions = traverse(blocksAway, accuracy);
		for(Vector position : positions) {
			if(intersects(position, min, max)) {
				return position;
			}
		}
		return null;
	}
	
	public ArrayList<Vector> traverse(double blocksAway, double accuracy) {
		ArrayList<Vector> positions = new ArrayList<>();
		for(double d = 0; d <= blocksAway; d += accuracy) {
			positions.add(getPosition(d));
		}
		return positions;
	}
	
	public ArrayList<Vector> traverse(double blocksAway, double accuracy, World world) {
		ArrayList<Vector> positions = new ArrayList<>();
		for(double d = 0; d <= blocksAway; d += accuracy) {
			Vector vector = getPosition(d);
			if(vector.toLocation(world).getBlock().getType() != Material.AIR)
				break;
			positions.add(vector);
		}
		return positions;
	}
	
	public Vector getPosition(double blocksAway) {
		return origin.clone().add(direction.clone().multiply(blocksAway));
	}
	
	public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy, ArrayList<Vector> positions) {
		for(Vector position : positions) {
			if(intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
				return position;
			}
		}
		return null;
	}
	
	public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
		ArrayList<Vector> positions = traverse(blocksAway, accuracy);
		for(Vector position : positions) {
			if(intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean intersects(Vector position, Vector min, Vector max) {
		return ((position.getX() < min.getX() || position.getX() > max.getX()) || (position.getY() < min.getY() || position.getY() > max.getY())
				|| (position.getZ() < min.getZ() || position.getZ() > max.getZ())) ? false : true;
	}
	
}
