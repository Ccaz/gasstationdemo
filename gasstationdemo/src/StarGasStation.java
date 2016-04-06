import java.util.ArrayList;
import java.util.Collection;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasNotAvailableException;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;


/**
 * @author TTkocz
 * implementation of the GasStation interface
 *
 */
public class StarGasStation extends Thread implements GasStation
{
    Collection<GasPump> gasPumpsList;
    Collection<GasRequest> gasRequestList;
    double stationRevenue;
    int numberOfSales;
    int numberNoGas;
    int numberGasTooExpensive;
    String stationName;

    public StarGasStation(String stationName)
    {
    	this.stationName = stationName;
        this.gasPumpsList = new ArrayList<GasPump>();
        this.gasRequestList = new ArrayList<GasRequest>();
        this.numberGasTooExpensive = 0;
        this.stationRevenue = 0;
        this.numberOfSales = 0;
        this.numberNoGas = 0;
    }

    @Override
    public void addGasPump( final GasPump pump )
    {
        this.gasPumpsList.add(pump);
    }

    @Override
    public Collection<GasPump> getGasPumps()
    {
        return gasPumpsList;
    }

    @Override
    public synchronized double buyGas( final GasType type, final double amountInLiters, final double maxPricePerLiter ) throws NotEnoughGasException, GasTooExpensiveException, GasNotAvailableException
       
    {
        double amount = 0;
        
        for ( final GasPump gasPump : getGasPumps() )
        {	//check the gas type
            final boolean isRequestedGasType = gasPump.getGasType().equals(type);
            if ( isRequestedGasType )
            {
            	//check if the gas price is right
                final double gasPrice = gasPump.getGasPrice();
                if ( gasPrice < maxPricePerLiter )
                {	
                	//check if the requested amount is available
                    final double remainingAmount = gasPump.getRemainingAmount();
                    if ( remainingAmount >= amountInLiters )
                    {
                    	//if everything is correct, start to pump gas, return the amount, add the revenue and number of sales
                        gasPump.pumpGas(amountInLiters);
                        amount = amountInLiters;
                        this.stationRevenue += (gasPump.getGasPrice() * amountInLiters);
                        this.numberOfSales++;
                        
                    }
                    else
                    {
                    	//if the requested amount of gas is too high
                        numberNoGas++;
                        throw new NotEnoughGasException();
                    }
                }
                else
                {
                	//if the requested gas is too expensive
                    numberGasTooExpensive++;
                    throw new GasTooExpensiveException();
                }
            }
            else
            {	//if the requested gas in not available at this station
            	 throw new GasNotAvailableException();
            }
        }

        return amount;
    }
    
    @Override
    public double getRevenue()
    {
        return stationRevenue;
    }

    @Override
    public int getNumberOfSales()
    {
        return numberOfSales;
    }

    @Override
    public int getNumberOfCancellationsNoGas()
    {
        return numberNoGas;
    }

    @Override
    public int getNumberOfCancellationsTooExpensive()
    {
        return numberGasTooExpensive;
    }

    @Override
    public double getPrice( final GasType type )
    {
        double price = 0;

        for ( final GasPump gasPump : getGasPumps() )
        {
            final boolean isRequestedGasType = gasPump.getGasType().equals(type);
            if ( isRequestedGasType )
            {
                price = gasPump.getGasPrice();
            }
        }
        return price;
    }

    @Override
    public void setPrice( final GasType type, final double price )
    {
        for ( final GasPump gasPump : getGasPumps() )
        {
            final boolean isRequestedGasType = gasPump.getGasType().equals(type);
            if ( isRequestedGasType )
            {
                gasPump.setGasPrice(price);
            }
        }
    }
    
    public void addGasRequest( final GasRequest request )
    {
        this.gasRequestList.add(request);
    }
    
    
    public Collection<GasRequest> getGasRequestList()
    {
    	return this.gasRequestList;
    }
    
    
    @Override
    public void run() {
    	for (GasRequest gasRequest : gasRequestList) 
    	{
    		try {
    		
    			//process all customer requests
    			System.out.println("Starting to pump " + gasRequest.amountInLiters + "L of " + gasRequest.type + " at "+ stationName);
    			double amountGas = buyGas(gasRequest.type, gasRequest.amountInLiters, gasRequest.maxPricePerLiter);
    			System.out.println(gasRequest.type + " request finished, bought "+ amountGas + "L at " + stationName);
    			System.out.println();
    		
    		
    		} catch (NotEnoughGasException e) {
    			System.out.println("Not enough gas! please try another station!");
    			System.out.println();
    		} catch (GasTooExpensiveException e) {
    			System.out.println(gasRequest.type +  " gas is too expensive! please try another station! ");
    			System.out.println();
    		} catch (GasNotAvailableException e) {
    			System.out.println(gasRequest.type +  " is not available! please try another station! ");
			}
    	}
    	
    	//print me the results at the end of the day for each gas station
    	System.out.println();
    	System.out.println(this.stationName + ":");
    	System.out.println("NumberOfCancellationsNoGas " + getNumberOfCancellationsNoGas());
    	System.out.println("NumberOfCancellationsTooExpensive " + getNumberOfCancellationsTooExpensive());
    	System.out.println("NumberOfSales " + getNumberOfSales());
    	System.out.println("Revenue " + getRevenue());
    	
    }
    
    

}
