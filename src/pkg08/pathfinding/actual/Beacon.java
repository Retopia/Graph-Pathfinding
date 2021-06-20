package pkg08.pathfinding.actual;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author Preston Tang
 */
public class Beacon extends Box implements Comparable<Beacon> {

    public List<Beacon> shortestPath = new LinkedList<>();;
    private ArrayList<Beacon> connections;
    private double distance = 0.0;
    private String name;
    
    public Beacon parent;
    public double gCost;
    public double hCost;
    public double fCost = gCost + hCost;

    public Beacon(double x, double y, double z, String name) {
        this.name = name;
        connections = new ArrayList<>();

        this.setTranslateX(x);
        this.setTranslateY(y);
        this.setTranslateZ(z);

        this.setWidth(10);
        this.setHeight(10);
        this.setDepth(500);

        PhongMaterial pM = new PhongMaterial();
        pM.setDiffuseColor(Color.GOLD);

        this.setMaterial(pM);
    }

    public void addConnection(Beacon b) {
        connections.add(b);
    }

    public ArrayList<Beacon> getConnections() {
        return this.connections;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int compareTo(Beacon b) {
        if (this.getDistance() < b.getDistance()) {
            return -1;
        } else if (b.getDistance() < this.getDistance()) {
            return 1;
        }
        return 0;
    }

}
