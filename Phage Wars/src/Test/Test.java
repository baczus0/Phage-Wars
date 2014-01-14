package Test;

import PhageWars.Cell;
import PhageWars.Stage;

public class Test {
	public static void main (String[] args) {
		Stage s = new Stage("testLevel.xml");
		
		int i = 1;
		for (Cell c1 : s.cells) {
			int j = 1;
			for (Cell c2 : s.cells) {
				if (c1 != c2) {
					System.out.println("cell " + i + " == cell " + j + ": " + (c1.owner == c2.owner));
				}
				
				
				j++;
			}
			i++;
			
		}
	}
}
