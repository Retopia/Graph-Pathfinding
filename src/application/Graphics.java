package application;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Graphics extends Application {

	private ArrayList<Block> visited = new ArrayList<>();
	private ArrayList<Adjacency> connections = new ArrayList<>();
	private ArrayList<Block> changeBlock = new ArrayList<>();
	private ArrayList<Double> xBlock = new ArrayList<>();
	private ArrayList<Double> yBlock = new ArrayList<>();
	private ArrayList<Double> blockSizes = new ArrayList<>();
	private Queue<Block> clickedOn = new LinkedList<>();
	private ArrayList<Block> open = new ArrayList<>();

	private Pane root = new Pane(), UI = new Pane(), graphics = new Pane();
	private Scene scene;
	private double screenSize = 600, gridX, gridY;

	private Block grid[][];
	private int numBlock = 30;
	private double blockSize;
	private boolean stroke = true, stop = true, clickable = true;
	private Block start, goal, current;
	private double x, y, change = 2, changePercent = 0.05;

	private AnimationTimer astarTimer, GreedyTimer, UniformCostTimer, timer;

	private int blockType = 0, searchType = 0;
	private boolean removeWall;
	private ArrayList<Block> drag = new ArrayList<>();

	private double currentG, f, diagonal, straight;

	private ChoiceBox<Object> choose = new ChoiceBox<>();
	private ChoiceBox<Object> chooseSearchType = new ChoiceBox<>();
	private Button startButton = new Button("Start");

	public void start(Stage primaryStage) {

		/*******************************/
		// Create the Grid
		grid = new Block[numBlock][numBlock];

		blockSize = screenSize / numBlock;

		for (int i = 0; i < numBlock; i++) {
			for (int j = 0; j < numBlock; j++) {
				grid[i][j] = new Block(blockSize, gridX, gridY, false, stroke);
				graphics.getChildren().add(grid[i][j]);
				gridX += blockSize;
			}
			gridY += blockSize;
			gridX = 0;
		}

		straight = blockSize;
		diagonal = Math.sqrt(Math.pow(blockSize, 2) + Math.pow(blockSize, 2));

		try {
			/**********************************************************************************/
			// Setting up Canvas
			UI.setPrefHeight(50);
			UI.setPrefWidth(screenSize);

			UI.setTranslateY(screenSize);
			graphics.setPrefHeight(screenSize);
			graphics.setPrefWidth(screenSize);

			root.getChildren().addAll(graphics, UI);

			scene = new Scene(root, screenSize, screenSize + 50);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			root.setStyle("-fx-background-color: white");
			primaryStage.setTitle("Grid Pathfinding");
			primaryStage.setResizable(false);

			primaryStage.show();
			root.setCache(true);
			root.setCacheHint(CacheHint.SPEED);
		} catch (Exception e) {
			e.printStackTrace();
		}

		initMouseControl(primaryStage);
		AStar();
		UniformCost();
		Greedy();

		timer = new AnimationTimer() {
			private long lastUpdate = 0;
			private Block block;

			public void start() {
				lastUpdate = System.nanoTime();
				super.start();
			}

			public void handle(long now) {
				// Slow down timer
				if (now - lastUpdate >= 5_000_000) {
					lastUpdate = now;
					if (!changeBlock.isEmpty()) {
						for (int i = 0; i < changeBlock.size(); i++) {
							block = changeBlock.get(i);
							if (block.getX() < xBlock.get(i) || block.getY() < yBlock.get(i) || block.getSize() > blockSize) {
								block.setXY(change / 2);
								block.setSize(block.getSize() * (1 - changePercent));
							} else {
								block.setX(xBlock.get(i));
								block.setY(yBlock.get(i));
								block.setSize(blockSize);
								xBlock.remove(xBlock.get(i));
								yBlock.remove(yBlock.get(i));
								changeBlock.remove(block);
								i--;
							}
						}
					}
				}
			}
		};
		timer.start();
	}

	public void UniformCost() {
		// Animation Timer
		UniformCostTimer = new AnimationTimer() {
			private long lastUpdate = 0;

			public void start() {
				lastUpdate = System.nanoTime();
				super.start();
			}

			public void handle(long now) {
				// Slow down timer
				if (now - lastUpdate >= 16_000_000) {
					lastUpdate = now;
					if (!open.isEmpty()) {
						current = getLeastCost();
						visited.add(current);
						current.changeVisited();

						if (current == goal) {
							showPath();
							UniformCostTimer.stop();
						}

						connections = current.getConnections();
						for (Adjacency i : connections) {
							Block successor = i.getConnection();
							if (!successor.returnWall()) {
								currentG = i.getG() + current.getG();
								if (checkClosed(successor))
									continue;
								if (!checkOpen(successor) || successor.getG() > currentG) {
									successor.setG(currentG);
									successor.setParent(current);
									if (!checkOpen(successor))
										open.add(successor);
									else {
										open.remove(successor);
										open.add(successor);
									}

									if (!(successor.getStart() || successor.getGoal()))
										successor.changeFringe();
								}
							}
						}
					}
				}
			}
		};
	}

	public void Greedy() {
		// Animation Timer
		GreedyTimer = new AnimationTimer() {
			private long lastUpdate = 0;

			public void start() {
				lastUpdate = System.nanoTime();
				super.start();
			}

			public void handle(long now) {
				// Slow down timer
				if (now - lastUpdate >= 50_000_000) {
					lastUpdate = now;
					if (!open.isEmpty()) {
						current = getLeastCost();
						current.changeVisited();
						visited.add(current);

						if (current == goal) {
							showPath();
							GreedyTimer.stop();
						}

						connections = current.getConnections();
						for (Adjacency i : connections) {
							Block successor = i.getConnection();
							if (!successor.returnWall()) {
								if (!(checkOpen(successor) || checkClosed(successor))) {
									successor.setParent(current);
									successor.H();
									open.add(successor);
								}
								if (!(successor.getStart() || successor.getGoal()))
									successor.changeFringe();
							}
						}
					}
				}
			}
		};
	}

	public void AStar() {
		// Animation Timer
		astarTimer = new AnimationTimer() {
			private long lastUpdate = 0;

			public void start() {
				lastUpdate = System.nanoTime();
				super.start();
			}

			public void handle(long now) {
				// Slow down timer
				if (now - lastUpdate >= 20_000_000) {
					lastUpdate = now;
					if (!open.isEmpty()) {
						current = getLeastCost();
						visited.add(current);
						current.changeVisited();
						if (current == goal) {
							showPath();
							astarTimer.stop();
						}

						connections = current.getConnections();
						for (Adjacency i : connections) {
							Block successor = i.getConnection();
							if (!successor.returnWall()) {
								currentG = i.getG() + current.getG();
								f = currentG + successor.getH();
								if (checkClosed(successor))
									continue;
								if (!checkOpen(successor) || successor.getF() > f) {
									successor.setG(currentG);
									successor.setParent(current);
									successor.setF(f);
									if (!checkOpen(successor))
										open.add(successor);
									else {
										open.remove(successor);
										open.add(successor);
									}

									if (!(successor.getStart() || successor.getGoal()))
										successor.changeFringe();
								}
							}
						}
					}
				}
			}
		};

	}

	public Block getLeastCost() {
		int index = 0;
		double min = Integer.MAX_VALUE;
		Block chosen;
		for (int i = 0; i < open.size(); i++) {
			if (min > open.get(i).getTotal()) {
				min = open.get(i).getTotal();
				index = i;
			}
		}
		chosen = open.get(index);
		open.remove(chosen);
		return chosen;
	}

	public void showPath() {
		findParent(goal);
	}

	// Traverses through the parents
	public void findParent(Block node) {
		Block parent = node.returnParent();
		if (parent != null) {
			parent.changePath();
			findParent(parent);
		}
	}

	// Check whether open list have the PNode
	public boolean checkOpen(Block i) {
		for (Block open : open) {
			if (open == i) {
				return true;
			}
		}
		return false;
	}

	// Check whether closed list have the PNode
	public boolean checkClosed(Block i) {
		for (Block closed : visited) {
			if (closed == i) {
				return true;
			}
		}
		return false;
	}

	public void initMouseControl(Stage primaryStage) {
		initDrag();
		initInterface();

		// Set the Stroke
		scene.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.W) {
				for (Block[] g : grid) {
					for (Block i : g) {
						i.changeStroke();
					}
				}
			}
			if (stroke)
				stroke = false;
			else
				stroke = true;
		});

		startButton.setOnAction(value -> {
			if (checkStart()) {
				if (stop) {
					clearProcess();
					calcH();
					calcG();
					open.add(start);
					switch (searchType) {
					case 0:
						astarTimer.start();
						break;
					case 1:
						GreedyTimer.start();
						break;
					case 2:
						UniformCostTimer.start();
						break;
					}
					startButton.setText("Clear");
					stop = false;
					clickable = false;
				} else {
					clickable = true;
					switch (searchType) {
					case 0:
						astarTimer.stop();
						break;
					case 1:
						GreedyTimer.stop();
						break;
					case 2:
						UniformCostTimer.stop();
						break;
					}
					clearProcess();
					startButton.setText("Start");
					stop = true;
				}
			}
		});

		// Choose Patterns and Clear the grid
		choose.getSelectionModel().selectedIndexProperty()
				.addListener((ChangeListener<Number>) (ov, value, new_value) -> {
					int choice = new_value.intValue();
					switch (choice) {
					case 2:
						clearGraphics();
						break;
					case 4:
						blockType = 1;
						break;
					case 5:
						blockType = 2;
						break;
					case 6:
						blockType = 0;
						break;
					}
				});

		// Choose Patterns and Clear the grid
		chooseSearchType.getSelectionModel().selectedIndexProperty()
				.addListener((ChangeListener<Number>) (ov, value, new_value) -> {
					int choice = new_value.intValue();
					switch (choice) {
					case 2:
						searchType = 0;
						break;
					case 3:
						searchType = 1;
						break;
					case 4:
						searchType = 2;
						break;
					}
				});
	}

	public void initInterface() {

		choose.setTranslateX(400);
		choose.setTranslateY(9);
		choose.setPrefWidth(170);
		choose.setItems(FXCollections.observableArrayList("Clear & Choose Block", new Separator(), "Clear",
				new Separator(), "Start", "Goal", "Wall"));
		choose.setValue("Clear & Choose Block");

		chooseSearchType.setTranslateX(220);
		chooseSearchType.setTranslateY(9);
		chooseSearchType.setPrefWidth(170);
		chooseSearchType.setItems(FXCollections.observableArrayList("Choose Search Type", new Separator(), "A*Star",
				"Greedy", "Uniform Cost"));
		chooseSearchType.setValue("Choose Search Type");

		startButton.setTranslateX(7);
		startButton.setTranslateY(8);
		startButton.setPrefWidth(85);

		UI.getChildren().addAll(choose, startButton, chooseSearchType);
	}

	public void initDrag() {
		// Mouse Event for clicking and changing block colors
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				double xMouse = event.getX();
				double yMouse = event.getY();
				for (Block[] g : grid) {
					for (Block i : g) {
						if (xMouse < i.getX() + blockSize && xMouse > i.getX() && yMouse < i.getY() + blockSize
								&& yMouse > i.getY()) {
							if (clickable) {
								switch (blockType) {
								case 0:
									if (!(i.getStart() || i.getGoal())) {
										if (!i.returnWall())
											removeWall = false;
										else
											removeWall = true;
										drag.add(i);
										i.changeColor();
									}
									break;
								case 1:
									if (!i.getStart()) {
										for (Block[] r : grid)
											for (Block b : r)
												if (b.getStart())
													b.changeStart();
										i.changeStart();
									}
									break;
								case 2:
									if (!i.getGoal()) {
										for (Block[] r : grid)
											for (Block b : r)
												if (b.getGoal())
													b.changeGoal();
										i.changeGoal();
									}
									break;
								}
								if (i.getVisited()) {
									i.changeVisited();
								} else if (i.getPath()) {
									i.changePath();
								} else if (i.getFringe()) {
									i.changeFringe();
								}
								setBlock(i);
								changeBlock.add(i);
							}
						}
					}
				}
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				double xMouse = event.getX();
				double yMouse = event.getY();
				for (Block[] g : grid) {
					for (Block i : g) {
						if (checkDrag(i) && xMouse < i.getX() + blockSize && xMouse > i.getX()
								&& yMouse < i.getY() + blockSize && yMouse > i.getY()) {
							if (clickable) {
								if (blockType == 0) {
									if (!(i.getStart() || i.getGoal())) {
										if (!removeWall) {
											if (!i.returnWall()) {
												i.changeColor();
												drag.add(i);
											}
										} else {
											if (i.returnWall()) {
												i.changeColor();
												drag.add(i);
											}
										}
										if (i.getVisited()) {
											i.changeVisited();
										} else if (i.getPath()) {
											i.changePath();
										} else if (i.getFringe()) {
											i.changeFringe();
										}
									}
									setBlock(i);
									changeBlock.add(i);
								}
							}
						}
					}
				}
			}
		});
		scene.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				drag.removeAll(drag);
			}
		});
	}
	
	public void setBlock(Block block)
	{
		xBlock.add(block.getX());
		yBlock.add(block.getY());

		graphics.getChildren().remove(block);
		block.setSize(blockSize * 1.26);
		graphics.getChildren().add(block);

		block.setXY(-blockSize * 0.13);
		change = blockSize * changePercent;
	}

	public void clearGraphics() {
		for (Block[] g : grid)
			for (Block i : g)
				if (i.returnWall())
					i.changeColor();
	}

	public void clearProcess() {
		for (Block[] g : grid) {
			for (Block i : g) {
				if (!(i.getStart() || i.getGoal())) {
					if (i.getVisited()) {
						i.resetStatus();
					} else if (i.getPath()) {
						i.resetStatus();
					} else if (i.getFringe()) {
						i.resetStatus();
					}
				}
			}
		}
		open.removeAll(open);
		visited.removeAll(visited);
	}

	public void calcH() {
		for (Block[] i : grid) {
			for (Block j : i) {
				double cost = Math.sqrt(Math.pow(goal.getX() - j.getX(), 2) + Math.pow(goal.getY() - j.getY(), 2));
				j.setH(cost);
			}
		}
	}

	public void calcG() {
		int successRow, successCol;

		for (int r = 0; r < numBlock; r++)
			for (int c = 0; c < numBlock; c++)
				for (int i = -1; i < 2; i++)
					for (int j = -1; j < 2; j++)
						if (!(i == 0 && j == 0)) {
							successRow = r + i;
							successCol = c + j;
							if (successRow >= 0 && successRow < numBlock && successCol >= 0 && successCol < numBlock) {
								Block successor = grid[successRow][successCol];
								if (i == 0 || j == 0)
									grid[r][c].addConnection(successor, straight);
								else
									grid[r][c].addConnection(successor, diagonal);

							}
						}
	}

	public boolean checkDrag(Block i) {
		for (Block j : drag)
			if (j == i)
				return false;
		return true;
	}

	public boolean checkStart() {
		boolean s = false, e = false;
		for (Block[] b : grid) {
			for (Block i : b) {
				if (i.getStart()) {
					start = i;
					s = true;
				} else if (i.getGoal()) {
					e = true;
					goal = i;
				}
			}
		}
		if (s && e)
			return true;
		else
			return false;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
