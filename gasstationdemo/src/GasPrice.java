import net.bigpoint.assessment.gasstation.GasType;


public class GasPrice {
	
	private GasType type;
	private double price;
	
	
	public GasPrice(GasType type, double price)
	{
		this.setType(type);
		this.setPrice(price);
	}


	public GasType getType() {
		return type;
	}


	public void setType(GasType type) {
		this.type = type;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}
	

}
