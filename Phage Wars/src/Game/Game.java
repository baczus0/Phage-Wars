package Game;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import PhageWars.Cell;
import PhageWars.PhageWars;

import Constants.Constants;

public class Game extends Canvas implements Runnable, MouseListener {
	private static final long serialVersionUID = 1L;

	private static final Font nonFullCell = new Font("Arial", Font.PLAIN, 14);
	private static final Font fullCell = new Font("Arial", Font.BOLD, 14);

	public static int width = 480;
	// public static int height = width * 9 / 16;
	public static int height = 640;
	public static int scale = 1;

	private Thread thread;
	private JFrame frame;
	private boolean running = false;

	// private Screen screen;

	// private BufferedImage image = new BufferedImage(width, height,
	// BufferedImage.TYPE_INT_RGB);
	// private int[] pixels = ((DataBufferInt)
	// image.getRaster().getDataBuffer()).getData();

	private PhageWars wars;

	public Game() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		frame = new JFrame();
		addMouseListener(this);

		this.wars = new PhageWars();
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nsPerUpdate = Constants.NANOS_PER_SEC / 25;
		final double nsPerUserInput = Constants.NANOS_PER_SEC / 12;
		double updateDelta = 0;
		double userInputDelta = 0;
		int frames = 0;
		int updates = 0;

		requestFocus();
		while (running) {
			long now = System.nanoTime();
			long sinceLastStep = now - lastTime;

			updateDelta += sinceLastStep / nsPerUpdate;
			userInputDelta += sinceLastStep / nsPerUserInput;

			lastTime = now;

			if (updateDelta >= 1) {
				// update();
				updates++;
				updateDelta = 0;
			}
			
			wars.step(sinceLastStep);

			if (userInputDelta >= 1) {
				userInputUpdate();
				userInputDelta = 0;
			}

			render();
			frames++;

			if (System.currentTimeMillis() - timer > 100) {
				timer += 1000;
				// System.out.println(updates + "UPS, " + frames + " FPS");
				frame.setTitle("Game | " + updates + " UPS, " + frames + " FPS");
				updates = 0;
				frames = 0;
			}
		}

		// stop();
	}

	public void update() {
		wars.step(0.0);
	}

	public void userInputUpdate() {

	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		// Fill Background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		// Draw Cells
		renderStage(g);

		// Draw Phages

		g.dispose();
		bs.show();
	}

	private void renderStage(Graphics g) {
		for (Cell c : wars.stage.cells) {
			// Draw cell
			if (c.owner == null) g.setColor(Color.GRAY);
			else g.setColor(c.owner.color);

			final int thickness = 3;
			g.fillOval(c.x, c.y, c.radius * 2, c.radius * 2);
			g.setColor(Color.WHITE);
			g.fillOval(c.x + thickness, c.y + thickness, (c.radius - thickness) * 2, (c.radius - thickness) * 2);

			// Draw number of phages in cell

			FontMetrics fm = null;
			if (c.isFull()) {
				fm = g.getFontMetrics(fullCell);
				g.setFont(fullCell);
			} else {
				fm = g.getFontMetrics(nonFullCell);
				g.setFont(nonFullCell);
			}

			String numPhages = Integer.toString(c.numPhages);
			int w = fm.stringWidth(numPhages);
			int h = fm.getAscent();

			g.setColor(Color.BLACK);
			g.drawString(numPhages, c.x + c.radius - (w / 2), c.y + c.radius + (h / 2));
		}

		// Draw selection circle
		if (wars.selected != null) {
			int offset = 15;
			g.setColor(Color.BLACK);
			g.drawOval(wars.selected.x - (offset / 2), wars.selected.y - (offset / 2), (wars.selected.radius * 2) + offset, (wars.selected.radius * 2) + offset);
		}
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.frame.setResizable(false);
		game.frame.setTitle("Title");
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);

		game.start();
	}

	private int distanceSquared(int x1, int y1, int x2, int y2) {
		int xDist = (x1 - x2);
		int yDist = (y1 - y2);
		int dist = xDist * xDist + yDist * yDist;

		// System.out.println(dist);
		return dist;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int mouseX = e.getX();
		int mouseY = e.getY();

		System.out.println("Mouse click at " + mouseX + ", " + mouseY);

		for (Cell c : wars.stage.cells) {
			int centerX = c.x + c.radius;
			int centerY = c.y + c.radius;

			if (distanceSquared(centerX, centerY, mouseX, mouseY) <= c.radius * c.radius) {
				System.out.println("Cell (" + centerX + ", " + centerY + ", " + c.radius + ") clicked at (" + mouseX + ", " + mouseY + ")");

				if (wars.selected == null) {
					// If there is already a cell selected, send phages to
					// clicked cell
					System.out.println("\tCell selected");
					c.isSelected = true;
					wars.selected = c;
				} else {
					// If there is NOT already a cell selected, select the
					// clicked cell

					// Send phages
					int attackers = wars.selected.sendPhages();
					c.receivePhages(wars.selected.owner, attackers);

					// Deselect
					c.isSelected = false;
					wars.selected = null;
					System.out.println("\tCell deselected");
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Do nothing
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

}
