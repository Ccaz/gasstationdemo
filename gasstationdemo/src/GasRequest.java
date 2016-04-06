import net.bigpoint.assessment.gasstation.GasType;

/**
 * @author TTkocz
 * 	class for the customer gas requests, requests will be added to a list so the station can have more requests.
 *	requests could also be generated and added by a Thread with random parameters.
 * 
 *
 */


public class GasRequest {
	
	final GasType type;
	final double amountInLiters;
	final double maxPricePerLiter;
	
	
	public GasRequest(final GasType type, final double amountInLiters, final double maxPricePerLiter)
	{
		this.type = type;
		this.amountInLiters = amountInLiters;
		this.maxPricePerLiter = maxPricePerLiter;
	}
	
	
}
