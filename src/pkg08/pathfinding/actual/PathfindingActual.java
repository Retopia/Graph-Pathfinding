/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg08.pathfinding.actual;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

/**
 *
 * @author Preston Tang
 */
public class PathfindingActual extends Application {

    private PerspectiveCamera camera;
    private PointLight single;

    private ArrayList<Beacon> path = new ArrayList<>();

    int i = 0;

    private boolean camW, camA, camS, camD, camPgDn, camPgUp;

    @Override
    public void start(Stage primaryStage) {
        camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-900);
        camera.getTransforms().add(new Rotate(15, Rotate.X_AXIS));
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);

        World root = World.getInstance();

        single = new PointLight();
        single.setTranslateX(450);
        single.setTranslateY(2000);
        single.setTranslateZ(-4000);

        root.getChildren().add(single);

        root.getChildren().add(camera);

        FastNoise fn = new FastNoise();
        fn.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
        fn.SetSeed(123457);
        fn.SetFrequency(0.01f);
        fn.SetFractalOctaves(1);

        Text info = new Text();
        info.setX(2);
        info.setY(32);
        info.setText("No Beacon Selected");

        root.init(fn);

        for (Beacon b : root.getBeacons()) {
            b.setOnMouseEntered((MouseEvent t) -> {
                info.setText(b.getName() + ": " + b.getTranslateX() + " "
                        + b.getTranslateY() + " " + b.getTranslateZ());

                PhongMaterial pM = new PhongMaterial();
                pM.setDiffuseColor(Color.BLUE);
                b.setMaterial(pM);

                String temp = info.getText() + "\nChildren: ";

                for (Beacon b2 : b.getConnections()) {
                    PhongMaterial pM2 = new PhongMaterial();
                    pM2.setDiffuseColor(Color.GREEN);
                    b2.setMaterial(pM2);

                    temp += b2.getName() + " ";
                }

                info.setText(temp);

            });

            b.setOnMouseExited((MouseEvent t) -> {
                for (Beacon b2 : root.getBeacons()) {
                    PhongMaterial pM = new PhongMaterial();
                    pM.setDiffuseColor(Color.GOLD);
                    b2.setMaterial(pM);
                }
            });
        }

        Box blocks[][] = root.getBlocks();

        for (Box[] block : blocks) {
            for (Box block1 : block) {
                Material test = block1.getMaterial();

                block1.setOnMouseEntered((MouseEvent t) -> {
                    root.changeColor(block1, Color.BLACK);
                    info.setText("Location: " + block1.getTranslateX() + " "
                            + block1.getTranslateY() + " " + block1.getTranslateZ());
                });

                block1.setOnMouseExited((MouseEvent t) -> {
                    root.changeColor(block1, test);
                });

                root.setOnMousePressed(event -> {
                    for (Node n : root.getChildren()) {
                        if (n instanceof Box) {
                            Box b0 = (Box) n;

                            b0.setOnMousePressed(e -> {
                                if (e.getButton() == MouseButton.PRIMARY) {
                                    Object obj = e.getSource();
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

                                    root.getChildren().add(newB);

                                }

                                if (e.getButton() == MouseButton.SECONDARY) {
                                    Object obj = e.getSource();

                                    Box b = (Box) obj;

                                    if (b.getWidth() == 20.0) {
                                        root.getChildren().remove(b);
                                    }

                                }
                            });
                        }
                    }
                });
            }
        }

        camera.setTranslateX(root.getCuboid().getTranslateX());
        camera.setTranslateY(root.getCuboid().getTranslateY() + 200);

        SubScene subScene = new SubScene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);

        Pane p = new Pane();
        p.getChildren().add(subScene);

        Rectangle base = new Rectangle();
        base.setWidth(250);
        base.setHeight(100);

        CheckBox cb = new CheckBox("Free Roam");
        cb.setSelected(true);

        base.setX(0);
        base.setY(0);

        Button b = new Button("Set Target");
        b.setTranslateX(2);
        b.setTranslateY(56);

        b.setOnAction((ActionEvent e) -> {
            Beacon initial = root.getBeacons()[0];
            Beacon end = root.getBeacons()[0];

            TextInputDialog dialog = new TextInputDialog("Beacon 0");
            dialog.setTitle("Set initial beacon");
            dialog.setHeaderText(null);
            dialog.setContentText("Type in the initial beacon: ");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                for (Beacon b1 : root.getBeacons()) {
                    if (b1.getName().equals(result.get())) {
                        initial = b1;
                    }
                }
            }

            TextInputDialog dialog1 = new TextInputDialog("Beacon 15");
            dialog1.setTitle("Set end beacon");
            dialog1.setHeaderText(null);
            dialog1.setContentText("Type in the end beacon: ");

            Optional<String> result1 = dialog1.showAndWait();
            if (result1.isPresent()) {
                for (Beacon b2 : root.getBeacons()) {
                    if (b2.getName().equals(result1.get())) {
                        end = b2;
                    }
                }
            }

            List<String> choices = new ArrayList<>();
            choices.add("Uniform Cost Search");
            choices.add("Greedy Search");
            choices.add("A* Search");

            ChoiceDialog<String> dialog2 = new ChoiceDialog<>("Uniform Cost Search", choices);
            dialog2.setTitle("Select a Pathfinding Option");
            dialog2.setHeaderText(null);
            dialog2.setContentText("Select your pathfinding option: ");

            Optional<String> result2 = dialog2.showAndWait();

            long startTime = 0L;
            long stopTime = 0L;

            if (result2.isPresent()) {
                switch (result2.get()) {
                    case "Uniform Cost Search":
                        i = 0;
                        path.clear();
                        startTime = System.nanoTime();
                        path = root.getCuboid().UCS(initial, end);
                        stopTime = System.nanoTime();
                        break;

                    case "Greedy Search":
                        i = 0;
                        path.clear();
                        startTime = System.nanoTime();
                        path = root.getCuboid().Greedy(initial, end);
                        stopTime = System.nanoTime();
                        break;

                    case "A* Search":
                        i = 0;
                        path.clear();
                        startTime = System.nanoTime();
                        path = root.getCuboid().AStar(initial, end);
                        stopTime = System.nanoTime();
                        break;

                    default:
                        System.err.println("ERROR: Invalid Search Option."); //Should never happen
                        break;
                }
            }

            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Execution Time Display");
            a.setHeaderText("");
            a.setContentText("Pathfinding Execution time: " + (stopTime - startTime));
            a.showAndWait();
        });

        base.setFill(Color.GRAY);

        p.getChildren().addAll(base, cb, info, b);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                subScene.requestFocus();

                if (root.getCuboid().canMove()) {
                    b.setDisable(false);
                } else {
                    b.setDisable(true);
                }

                if (!path.isEmpty()) {
                    if (i < path.size()) {
                        root.getCuboid().moveToBeacon(path.get(i));
                    }
                    if (root.getCuboid().canMove()) {
                        i++;
                    }
                }

                if (!cb.isSelected()) {
                    camera.setTranslateX(root.getCuboid().getTranslateX());
                    camera.setTranslateY(root.getCuboid().getTranslateY() + 200);
                    camera.setTranslateZ(-900);
                }

                single.setTranslateX(camera.getTranslateX() + 1000);
                single.setTranslateY(camera.getTranslateY());
                //<editor-fold defaultstate="collapsed" desc="camera movement">
                if (camW) {
                    camera.setTranslateY(camera.getTranslateY() - 15);
                }
                if (camA) {
                    camera.setTranslateX(camera.getTranslateX() - 15);
                }
                if (camS) {
                    camera.setTranslateY(camera.getTranslateY() + 15);
                }
                if (camD) {
                    camera.setTranslateX(camera.getTranslateX() + 15);
                }
                if (camPgUp && camera.getTranslateZ() >= -3000) {
                    camera.setTranslateZ(camera.getTranslateZ() - 15);
                }
                if (camPgDn) {
                    camera.setTranslateZ(camera.getTranslateZ() + 15);
                }
//</editor-fold>

            }
        };
        timer.start();

        Scene scene = new Scene(p, 800, 600, true);

        //<editor-fold defaultstate="collapsed" desc="camera input checking">
        scene.setOnKeyPressed(
                (KeyEvent event) -> {
                    switch (event.getCode()) {
                        case UP:
                            camW = true;
                            break;
                        case DOWN:
                            camS = true;
                            break;
                        case LEFT:
                            camA = true;
                            break;
                        case RIGHT:
                            camD = true;
                            break;
                        case PAGE_UP:
                            camPgUp = true;
                            break;
                        case PAGE_DOWN:
                            camPgDn = true;
                        default:
                            break;
                    }
                }
        );

        scene.setOnKeyReleased(
                (KeyEvent event) -> {
                    switch (event.getCode()) {
                        case UP:
                            camW = false;
                            break;
                        case DOWN:
                            camS = false;
                            break;
                        case LEFT:
                            camA = false;
                            break;
                        case RIGHT:
                            camD = false;
                            break;
                        case PAGE_UP:
                            camPgUp = false;
                            break;
                        case PAGE_DOWN:
                            camPgDn = false;
                        default:
                            break;
                    }
                }
        //</editor-fold>

        );

        primaryStage.setTitle(
                "08-Pathfinding - By Preston Tang");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
