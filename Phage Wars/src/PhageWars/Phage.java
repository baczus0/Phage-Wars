package PhageWars;

import java.awt.Color;

public class Phage {
	
	public static final Phage DEFAULT_PHAGE = new Phage(Color.GRAY, 0.0, 0.0, 0.0, 0.0); 

	public Color color;
	public double defense;
	public double reproduction;
	public double speed;
	public double strength;
	
	public Phage(Color color, double defense, double reproduction, double speed, double strength) {
		this.color = color;
		this.defense = defense;
		this.reproduction = reproduction;
		this.speed = speed;
		this.strength = strength;
	}
}
