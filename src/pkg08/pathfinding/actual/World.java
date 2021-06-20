package pkg08.pathfinding.actual;

import java.util.ArrayList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 *
 * @author Preston Tang
 */
public class World extends Group {

    private Box[][] blocks;
    private Beacon[] beacons;

    private Cuboid cuboid;

    private static World instance;

    public final int size = 20;

    private World() {
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }
        return instance;
    }

    public void init(FastNoise fn) {
        blocks = new Box[75][125];
        beacons = new Beacon[15];

        ArrayList<Beacon> tempB = new ArrayList<>();

        int num = 0;

        for (int r = 0; r < blocks.length; r++) {
            for (int c = 0; c < blocks[r].length; c++) {
                Box rec = new Box();
                rec.setWidth(size);
                rec.setHeight(size);
                rec.setTranslateX(size * c);
                rec.setTranslateY(size * r);
                rec.setTranslateZ(-size * Math.abs(Math.floor(fn.GetSimplexFractal(r, c) * 20)));

                PhongMaterial pM = new PhongMaterial();

                if (Math.abs(rec.getTranslateZ() / 20) < 1) {
                    pM.setDiffuseColor(Color.DARKSLATEBLUE);
                } else if (Math.abs(rec.getTranslateZ() / 20) < 3) {
                    pM.setDiffuseColor(Color.CORNFLOWERBLUE);
                } else if (Math.abs(rec.getTranslateZ() / 20) < 4) {
                    pM.setDiffuseColor(Color.LEMONCHIFFON);
                } else if (Math.abs(rec.getTranslateZ() / 20) < 9) {
                    pM.setDiffuseColor(Color.MEDIUMSEAGREEN);
                } else {
                    pM.setDiffuseColor(Color.WHITESMOKE);
                }

                rec.setMaterial(pM);

                rec.setDepth(size);
                rec.setCullFace(CullFace.BACK);
                rec.setDrawMode(DrawMode.FILL);

                rec.setOnMousePressed(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        Object obj = event.getSource();

                        if (obj instanceof Box) {
                            Box b = (Box) obj;

                            Box newB = new Box();
                            newB.setTranslateX(b.getTranslateX());
                            newB.setTranslateY(b.getTranslateY());
                            newB.setDepth(20);
                            newB.setTranslateZ(b.getTranslateZ() - 20);
                            newB.setHeight(20);
                            newB.setWidth(20);

                            PhongMaterial pm1 = new PhongMaterial();
                            pm1.setDiffuseMap(new Image(getClass().getResourceAsStream("cobble.png")));
                            newB.setMaterial(pm1);

                            super.getChildren().add(newB);
                        }
                    }

                    if (event.getButton() == MouseButton.SECONDARY) {
                        Object obj = event.getSource();

                        if (obj instanceof Box) {
                            Box b = (Box) obj;
                            if (b.getWidth() == 20.0) {
                                super.getChildren().remove(b);
                            }

                        }
                    }
                });

                super.getChildren().add(rec);
                blocks[r][c] = rec;
            }
        }

        beacons[0] = new Beacon(80, 80, -200, "Beacon 0");
        beacons[1] = new Beacon(420, 60, -200, "Beacon 1");
        beacons[2] = new Beacon(200, 420, -200, "Beacon 2");
        beacons[3] = new Beacon(620, 480, -200, "Beacon 3");
        beacons[4] = new Beacon(520, 860, -200, "Beacon 4");
        beacons[5] = new Beacon(980, 620, -200, "Beacon 5");
        beacons[6] = new Beacon(1260, 1180, -200, "Beacon 6");
        beacons[7] = new Beacon(1520, 920, -200, "Beacon 7");
        beacons[8] = new Beacon(2100, 260, -200, "Beacon 8");
        beacons[9] = new Beacon(2180, 880, -200, "Beacon 9");
        beacons[10] = new Beacon(1280, 280, -200, "Beacon 10");
        beacons[11] = new Beacon(1680, 380, -200, "Beacon 11");
        beacons[12] = new Beacon(1360, 480, -200, "Beacon 12");
        beacons[13] = new Beacon(1980, 1140, -200, "Beacon 13");
        beacons[14] = new Beacon(820, 1140, -200, "Beacon 14");

        cc(beacons[0], beacons[1]);
        cc(beacons[0], beacons[2]);
        cc(beacons[2], beacons[3]);
        cc(beacons[1], beacons[3]);
        cc(beacons[3], beacons[5]);
        cc(beacons[3], beacons[10]);
        cc(beacons[3], beacons[14]);

        cc(beacons[2], beacons[4]);
        cc(beacons[4], beacons[14]);

        cc(beacons[10], beacons[12]);
        cc(beacons[5], beacons[12]);
        cc(beacons[5], beacons[7]);
        cc(beacons[5], beacons[6]);
        cc(beacons[14], beacons[6]);
        cc(beacons[6], beacons[13]);
        cc(beacons[7], beacons[13]);
        cc(beacons[13], beacons[9]);
        cc(beacons[9], beacons[8]);
        cc(beacons[7], beacons[11]);
        cc(beacons[11], beacons[8]);
        cc(beacons[1], beacons[10]);
        cc(beacons[12], beacons[13]);

        cuboid = new Cuboid(beacons[0].getTranslateX(), beacons[0].getTranslateY(),
                beacons[0].getTranslateZ());

        super.getChildren().add(cuboid);

        for (Beacon temp : beacons) {
            super.getChildren().add(temp);
        }
    }

    public void cc(Beacon o, Beacon t) {
        o.addConnection(t);
        t.addConnection(o);

        Point3D origin = new Point3D(o.getTranslateX(), o.getTranslateY(), o.getTranslateZ() - 50);
        Point3D target = new Point3D(t.getTranslateX(), t.getTranslateY(), t.getTranslateZ() - 50);

        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(1, height);

        line.setRadius(5.0);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        PhongMaterial pM = new PhongMaterial();
        pM.setDiffuseColor(Color.DARKGRAY);

        line.setMaterial(pM);

        super.getChildren().add(line);
    }

    public Box[][] getBlocks() {
        return this.blocks;
    }

    public Beacon[] getBeacons() {
        return this.beacons;
    }

    public Cuboid getCuboid() {
        return this.cuboid;
    }

    public void changeColor(Box b, Color col) {
        Box temp = new Box();
        for (int r = 0; r < blocks.length; r++) {
            for (int c = 0; c < blocks[r].length; c++) {
                if (b.getTranslateX() == blocks[r][c].getTranslateX()
                        && b.getTranslateY() == blocks[r][c].getTranslateY()) {
                    temp = b;
                }
            }
        }

        PhongMaterial pM = new PhongMaterial();
        pM.setDiffuseColor(col);

        temp.setMaterial(pM);
    }

    public void changeColor(Box b, Material col) {
        Box temp = new Box();
        for (int r = 0; r < blocks.length; r++) {
            for (int c = 0; c < blocks[r].length; c++) {
                if (b.getTranslateX() == blocks[r][c].getTranslateX()
                        && b.getTranslateY() == blocks[r][c].getTranslateY()) {
                    temp = b;
                }
            }
        }

        temp.setMaterial(col);
    }

    public double getDistance(Beacon a, Beacon b) {
        return Math.sqrt((b.getTranslateY() - a.getTranslateY())
                * (b.getTranslateY() - a.getTranslateY())
                + (b.getTranslateX() - a.getTranslateX())
                * (b.getTranslateX() - a.getTranslateX()));
    }

}
