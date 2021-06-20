package application;

public class Adjacency {
	private double g;
	private Block connection;

	public Adjacency(Block connect, double g) {
		this.g = g;
		connection = connect;
	}

	//Return g cost
	public double getG() {
		return g;
	}
	
	//Return connection
	public Block getConnection() {
		return connection;
	}
}
