package PhageWars;

import Constants.Constants;

public class Cell {
	
	private static final double BASE_NANOS_PER_REP = Constants.NANOS_PER_SEC / 2;

	public int x, y;
	public int radius;
	public Phage owner; // Phage?
	public int numPhages;
	public int maxPhages;
	
	public boolean isSelected;
	
	public double defense;
	public double reproduction;
	public double speed;
	public double strength;
	
	private double currNanos;
	private final double nanosPerReproduce;
	
	public Cell(int x, int y, int radius, double defense, double reproduction, double speed, double strength, Phage owner, int numPhages, int maxPhages) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		
		this.owner = owner;
		this.numPhages = numPhages;
		this.maxPhages = maxPhages;
		
		this.isSelected = false;
		
		this.defense = defense;
		this.reproduction = reproduction;
		this.speed = speed;
		this.strength = strength;
		
		this.currNanos = 0;
		this.nanosPerReproduce = BASE_NANOS_PER_REP / (1.0 + this.reproduction + this.owner.reproduction);
	}
	
	public Cell(int x, int y, int radius, Phage cellStats, Phage owner, int numPhages, int maxPhages) {
		this(x, y, radius, cellStats.defense, cellStats.reproduction, cellStats.speed, cellStats.strength, owner, numPhages, maxPhages);
	}
	
	public void reproduce(double updateDelta) {
		currNanos += updateDelta;
		//System.out.println(currNanos + " ns of " + nanosPerReproduce + " have passed.");
		
		if (isFull()) return;
		else {
			if (currNanos > nanosPerReproduce) {
				numPhages++;
				currNanos = 0;
			}
		}
	}
	
	public boolean isFull() {
		return numPhages >= maxPhages;
	}
	
	public int sendPhages() {
		int sentPhages = numPhages / 2;
		numPhages -= sentPhages;
		System.out.println("Sent " + sentPhages + " phages");
		return sentPhages;
	}
	
	public void receivePhages(Phage sender, int numSent) {
		if (this.owner == sender) {
			// If friendly phages are sent
			if (!isFull()) numPhages = Math.min(numPhages + numSent, maxPhages);
		} else {
			// If enemy phages are sent
			if (this.numPhages <= numSent) {
				this.owner = sender;
				this.numPhages = numSent - this.numPhages;
			} else {
				numPhages -= numSent;
				System.out.println(numSent + " phages lost");
			}
			
		}
	}
}
