import net.bigpoint.assessment.gasstation.GasType;


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
