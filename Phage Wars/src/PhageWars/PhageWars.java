package PhageWars;


public class PhageWars {
	
	public Stage stage;
	public Cell selected; // Only one can be selected at a time
	
	
	public PhageWars() {
		this.stage = new Stage("testLevel.xml");
		this.selected = null;
	}
	
	public boolean step(double updateDelta) {
		stage.step(updateDelta);
		return false;
	}
}
