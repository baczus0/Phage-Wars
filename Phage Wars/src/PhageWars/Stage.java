package PhageWars;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Stage {
	
	private static final String LEVEL_DIR = "res/levels/";
	private static final String PLAYER_FILE = "res/player.xml";

	public Collection<Cell> cells;
	public Map<String, Phage> owners;
	
	private String xmlFileName;
	
	public Stage(String xmlFileName) {
		this.xmlFileName = xmlFileName;
		this.cells = new ArrayList<Cell>();
		this.owners = new TreeMap<String, Phage>();
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(LEVEL_DIR + xmlFileName));
			doc.getDocumentElement().normalize();
			
			// Create Owners
			NodeList nList = doc.getElementsByTagName("players").item(0).getChildNodes();
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					owners.put(nNode.getNodeName(), nodeToPhage(nList.item(temp)));
				}
			}
			
			Document player = dBuilder.parse(new File(PLAYER_FILE));
			player.getDocumentElement().normalize();
			
			nList = player.getElementsByTagName("player");
			owners.put("player", nodeToPhage(nList.item(0)));
			
			
			// Create Cells
			nList = doc.getElementsByTagName("cell");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
		 
					int x = getIntAttribute(eElement, "x");
					int y = getIntAttribute(eElement, "y");
					int radius = getIntAttribute(eElement, "radius");
					double defense = getDoubleAttribute(eElement, "defense");
					double reproduction = getDoubleAttribute(eElement, "reproduction");
					double speed = getDoubleAttribute(eElement, "speed");
					double strength = getDoubleAttribute(eElement, "strength");
					int numPhages = getIntAttribute(eElement, "numPhages");
					int maxPhages = getIntAttribute(eElement, "maxPhages");
					
					String o = eElement.getAttribute("owner");
					Phage owner = null;
					
					if (o.equals("default")) {
						owner = Phage.DEFAULT_PHAGE;
					} else {
						owner = owners.get(o);
					}
					
					Cell c = new Cell(x, y, radius, defense, reproduction, speed, strength, owner, numPhages, maxPhages);
					cells.add(c);
		 		}
			}
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		printFile();
	}
	
	private int getIntAttribute(Element e, String attr) {
		return Integer.parseInt(e.getAttribute(attr));
	}
	
	private double getDoubleAttribute(Element e, String attr) {
		return Double.parseDouble(e.getAttribute(attr));
	}
	
	private Phage nodeToPhage(Node nNode) {
		Element eElement = (Element) nNode;
		 
		Color color = null;
		try {
			color = (Color) Color.class.getField(eElement.getAttribute("color")).get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		double defense = getDoubleAttribute(eElement, "defense");
		double reproduction = getDoubleAttribute(eElement, "reproduction");
		double speed = getDoubleAttribute(eElement, "speed");
		double strength = getDoubleAttribute(eElement, "strength");
		
		return new Phage(color, defense, reproduction, speed, strength);
	}
	
	public boolean step(double updateDelta) {
		// Cells reproduce
		for (Cell c : cells) {
			c.reproduce(updateDelta);
		}
		
		// Phages move
		
		
		// Return false if game is not over
		return false;
	}
	
	private void printFile() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(LEVEL_DIR + xmlFileName));
			while (r.ready()) {
				System.out.println(r.readLine());
			}
			System.out.println();
			r.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
