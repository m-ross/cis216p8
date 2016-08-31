package lab08;

public class Parts {
	private String idNum, desc, loc;
	private double cost, price, weight;
	private int qty;

	public Parts(String i, String d, String l, double c, double p, double w, int q) {
		idNum = i;
		desc = d;
		loc = l;
		cost = c;
		price = p;
		weight = w;
		qty = q;
	}

	public void setInfo(String i, String d, String l, double c, double p, double w, int q) {
		idNum = i;
		desc = d;
		loc = l;
		cost = c;
		price = p;
		weight = w;
		qty = q;
	}

	public void modQty(int q) {
		qty += q;
	}

	public void setLoc(String l) {
		loc = l;
	}

	public void setCost(double c) {
		cost = c;
	}

	public void setPrice(double p) {
		price = p;
	}

	public String getId() {
		return idNum;
	}

	public String getDesc() {
		return desc;
	}

	public String getLoc() {
		return loc;
	}

	public double getCost() {
		return cost;
	}

	public double getPrice() {
		return price;
	}

	public double getWeight() {
		return weight;
	}

	public int getQty() {
		return qty;
	}
}