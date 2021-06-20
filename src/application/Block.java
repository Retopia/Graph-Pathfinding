package application;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author Will Kung
 **/

public class Block extends Group {
	private boolean wall, goal, start;
	private Rectangle i;
	private double x, y;
	private boolean stroke, visited = false, path = false, fringe = false;

	private ArrayList<Adjacency> links = new ArrayList<>();
	public Block parent;
	private double h = 0, f = 0, g = 0, total = 0, size;

	public Block(double size, double xBlock, double yBlock, boolean w, boolean stro) {
		parent = null;

		x = xBlock;
		y = yBlock;
		wall = w;
		stroke = stro;

		i = new Rectangle();
		i.setWidth(size);
		i.setHeight(size);
		i.setX(x);
		i.setY(y);

		this.size = size;

		goal = false;
		start = false;

		if (stroke) {
			i.setStrokeType(StrokeType.INSIDE);
			i.setStrokeWidth(0.23);
			i.setStroke(Color.DARKGREY);
		} else {
			i.setStrokeType(null);
			i.setStroke(null);
		}

		if (wall) {
			i.setFill(Color.BLACK);
		} else {
			i.setFill(Color.WHITE);
		}

		super.getChildren().add(i);

	}

	public void setXY(double change) {
		i.setX(x + change);
		i.setY(y + change);
		y += change;
		x += change;
	}

	public void setSize(double size) {
		this.size = size;
		i.setWidth(size);
		i.setHeight(size);
	}

	public double getSize() {
		return size;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
		i.setX(x);
	}

	public void setY(double y) {
		this.y = y;
		i.setY(y);
	}

	public boolean returnWall() {
		return wall;
	}

	public boolean getStart() {
		return start;
	}

	public boolean getGoal() {
		return goal;
	}

	// Changes color from white to black or black to white
	public void changeColor() {
		// 1 is Black and 0 is White
		if (wall) {
			i.setFill(Color.WHITE);
			wall = false;
		} else {
			i.setFill(Color.BLACK);
			wall = true;
		}
	}

	public void changeStart() {
		goal = false;
		i.setFill(Color.WHITE);
		wall = false;
		if (start) {
			i.setFill(Color.WHITE);
			start = false;
		} else {
			i.setFill(Color.GREEN);
			start = true;
		}
	}

	public void changeGoal() {
		start = false;
		i.setFill(Color.WHITE);
		wall = false;
		if (goal) {
			i.setFill(Color.WHITE);
			goal = false;
		} else {
			i.setFill(Color.RED);
			goal = true;
		}
	}

	public void changeStroke() {
		// Removes the Stroke or keeps it
		if (stroke) {
			i.setStrokeType(null);
			i.setStroke(null);
			stroke = false;
		} else {
			i.setStrokeType(StrokeType.INSIDE);
			i.setStroke(Color.GREY);
			stroke = true;
		}
	}

	public void setParent(Block i) {
		if (!start) {
			parent = i;
		}
	}

	public Block returnParent() {
		return parent;
	}

	public void addConnection(Block node, double c) {
		links.add(new Adjacency(node, c));
	}
	
	public void removeConnections()
	{
		links.remove(links);
	}

	public ArrayList<Adjacency> getConnections() {
		return links;
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
		total = g;
	}

	public double getH() {
		return h;
	}

	public void H() {
		total = h;
	}

	public void setH(double h) {
		this.h = h;
		total = h;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
		total = f;
	}

	public double getTotal() {
		return total;
	}

	public boolean getVisited() {
		return visited;
	}
	
	public void setZ(double z)
	{
		i.setTranslateZ(i.getTranslateZ()+z);
	}

	public boolean getPath() {
		return path;
	}

	public boolean getFringe() {
		return fringe;
	}
	
	public void resetStatus()
	{
		visited = false;
		path = false;
		fringe = false;
		i.setFill(Color.WHITE);
	}
	
	public void changeVisited() {
		visited = true;
		if (!start && !goal) {
			i.setFill(Color.VIOLET);
		}
	}

	public void changePath() {
		path = true;
		if (!start && !goal) {
			i.setFill(Color.YELLOW);
		}
	}

	public void changeFringe() {
		fringe = true;
		if (!visited)
			i.setFill(Color.LIGHTBLUE);
	}

	public String toString() {
		return "G: " + g + ", F: " + f + ", H: " + h;
	}
}
