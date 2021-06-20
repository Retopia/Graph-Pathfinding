package pkg08.pathfinding.actual;

import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

/**
 *
 * @author Preston Tang
 */
public class Cuboid extends Box {

    private boolean canMove = true;

    public Cuboid(double x, double y, double z) {
        super.setTranslateX(x);
        super.setTranslateY(y);
        super.setTranslateZ(z - 15);

        super.setHeight(15);
        super.setWidth(15);
        super.setDepth(15);

        PhongMaterial pM = new PhongMaterial();
        pM.setDiffuseColor(Color.RED);

        super.setMaterial(pM);

    }

    public void moveToBeacon(Beacon b) {
        canMove = false;

        Box[][] blocks = World.getInstance().getBlocks();

        if (getDistance(b) < 3.0) {
            World.getInstance().getCuboid().setTranslateX(b.getTranslateX());
            World.getInstance().getCuboid().setTranslateY(b.getTranslateY());

            canMove = true;
        } else {
            for (int r = 0; r < blocks.length; r++) {
                for (int c = 0; c < blocks[r].length; c++) {
                    if (blocks[r][c].getTranslateX()
                            == (World.getInstance().getCuboid().getTranslateX() + 15) - ((World.getInstance().getCuboid().getTranslateX() + 15) % 20)
                            && blocks[r][c].getTranslateY()
                            == (World.getInstance().getCuboid().getTranslateY() + 15) - ((World.getInstance().getCuboid().getTranslateY() + 15) % 20)) {
                        World.getInstance().getCuboid().setTranslateZ(blocks[r][c].getTranslateZ() - 20);
                    }
                }
            }
            World.getInstance().getCuboid().setTranslateX(World.getInstance().getCuboid().getTranslateX() + (2 * Math.cos(getAngle(b))));
            World.getInstance().getCuboid().setTranslateY(World.getInstance().getCuboid().getTranslateY() + (2 * Math.sin(getAngle(b))));
        }

    }

    public ArrayList<Beacon> UCS(Beacon start, Beacon end) {
        super.setTranslateX(start.getTranslateX());
        super.setTranslateY(start.getTranslateY());
        super.setTranslateZ(start.getTranslateZ() - 15);

        ArrayList<Beacon> open = new ArrayList<>();
        ArrayList<Beacon> closed = new ArrayList<>();

        ArrayList<Beacon> path = new ArrayList<>();

        Beacon current = null;

        open.add(start);

        while (!open.isEmpty()) {
            Collections.sort(open);
            current = open.get(0);

            open.remove(current);
            closed.add(current);

            if (current == end) {
                path = retracePath(start, end);

                System.out.println("NEW PATH");

                for (Beacon b : path) {
                    System.out.println(b.getName());
                }
                return path;
            }

            for (Beacon b : current.getConnections()) {
                if (!closed.contains(b)) {

                    double newCost = b.gCost + getDistance(current, b);
                    if (newCost < b.gCost || !open.contains(b)) {
                        b.gCost = newCost;
                        b.parent = current;

                        if (!open.contains(b)) {
                            open.add(b);
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Beacon> Greedy(Beacon start, Beacon end) {
        super.setTranslateX(start.getTranslateX());
        super.setTranslateY(start.getTranslateY());
        super.setTranslateZ(start.getTranslateZ() - 15);

        ArrayList<Beacon> open = new ArrayList<>();
        ArrayList<Beacon> closed = new ArrayList<>();

        ArrayList<Beacon> path = new ArrayList<>();

        Beacon current = null;

        open.add(start);

        while (!open.isEmpty()) {
            Collections.sort(open);
            current = open.get(0);

            for (int i = 0; i < open.size(); i++) {
                if (open.get(i).hCost < current.hCost || open.get(i).hCost == current.hCost) {
                    if (open.get(i).hCost < current.hCost) {
                        current = open.get(i);
                    }
                }
            }

            open.remove(current);
            closed.add(current);

            if (current == end) {
                path = retracePath(start, end);

                System.out.println("NEW PATH");

                for (Beacon b : path) {
                    System.out.println(b.getName());
                }

                return path;

            }

            for (Beacon b : current.getConnections()) {
                if (!closed.contains(b)) {

                    double newCost = b.gCost + getDistance(current, b);
                    if (newCost < b.gCost || !open.contains(b)) {
                        b.gCost = newCost;
                        b.hCost = getDistance(b, end);
                        b.parent = current;

                        if (!open.contains(b)) {
                            open.add(b);
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Beacon> AStar(Beacon start, Beacon end) {
        super.setTranslateX(start.getTranslateX());
        super.setTranslateY(start.getTranslateY());
        super.setTranslateZ(start.getTranslateZ() - 15);

        ArrayList<Beacon> open = new ArrayList<>();
        ArrayList<Beacon> closed = new ArrayList<>();

        ArrayList<Beacon> path = new ArrayList<>();

        Beacon current = null;

        open.add(start);

        while (!open.isEmpty()) {
            Collections.sort(open);
            current = open.get(0);

            for (int i = 0; i < open.size(); i++) {
                if (open.get(i).fCost < current.fCost || open.get(i).fCost == current.fCost) {
                    if (open.get(i).hCost < current.hCost) {
                        current = open.get(i);
                    }
                }
            }

            open.remove(current);
            closed.add(current);

            if (current == end) {
                path = retracePath(start, end);

                System.out.println("NEW PATH");

                for (Beacon b : path) {
                    System.out.println(b.getName());
                }

                return path;
            }

            for (Beacon b : current.getConnections()) {
                if (!closed.contains(b)) {

                    double newCost = b.gCost + getDistance(current, b);
                    if (newCost < b.gCost || !open.contains(b)) {
                        b.gCost = newCost;
                        b.hCost = getDistance(b, end);
                        b.parent = current;

                        if (!open.contains(b)) {
                            open.add(b);
                        }
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<Beacon> retracePath(Beacon start, Beacon end) {
        ArrayList<Beacon> path = new ArrayList<>();
        Beacon currentBeacon = end;

        while (currentBeacon != start) {
            path.add(currentBeacon);
            currentBeacon = currentBeacon.parent;
        }

        Collections.reverse(path);

        return path;
    }

    public double getDistance(Beacon b) {
        return Math.sqrt((b.getTranslateY() - super.getTranslateY())
                * (b.getTranslateY() - super.getTranslateY())
                + (b.getTranslateX() - super.getTranslateX())
                * (b.getTranslateX() - super.getTranslateX()));
    }

    public double getDistance(Beacon b1, Beacon b2) {
        return Math.sqrt((b1.getTranslateY() - b2.getTranslateY())
                * (b1.getTranslateY() - b2.getTranslateY())
                + (b1.getTranslateX() - b2.getTranslateX())
                * (b1.getTranslateX() - b2.getTranslateX()));
    }

    public double getAngle(Beacon b) {
        return Math.atan2((b.getTranslateY() - super.getTranslateY()),
                (b.getTranslateX() - super.getTranslateX()));
    }

    public boolean canMove() {
        return this.canMove;
    }

}
