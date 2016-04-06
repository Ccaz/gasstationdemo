import net.bigpoint.assessment.gasstation.GasType;

/**
 * @author TTkocz
 * 	class for the customer gas requests, requests will be added to a list so the station can have more requests.
 *	requests could also be generated and added by a Thread with random parameters.
 * 	@param type
*            The type of gas the customer wants to buy
* 	@param amountInLiters
*            The amount of gas the customer wants to buy. Nothing less than this amount is acceptable!
* 	@param maxPricePerLiter
*            The maximum price the customer is willing to pay per liter
 *
 */


public class GasRequest {
	
	final GasType type;
	private final double amountInLiters;
	private final double maxPricePerLiter;
	
	
	public GasRequest(final GasType type, final double amountInLiters, final double maxPricePerLiter)
	{
		this.type = type;
		this.amountInLiters = amountInLiters;
		this.maxPricePerLiter = maxPricePerLiter;
	}


	public GasType getType() {
		return type;
	}


	public double getAmountInLiters() {
		return amountInLiters;
	}


	public double getMaxPricePerLiter() {
		return maxPricePerLiter;
	}
	
	
}
