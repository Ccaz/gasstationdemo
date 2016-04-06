import java.util.ArrayList;
import java.util.Collection;

import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasStation;
import net.bigpoint.assessment.gasstation.GasType;
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
	private Thread requestThread;

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
    public synchronized Collection<GasPump> getGasPumps()
    {
        return gasPumpsList;
    }

    @Override
    public synchronized double buyGas( final GasType type, final double amountInLiters, final double maxPricePerLiter ) throws NotEnoughGasException, GasTooExpensiveException
       
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
    
    

    
    public void processRequestList(){
        requestThread = new Thread(new Runnable() {           
            public void run() { 
                //do stuff here
            	for (GasRequest gasRequest : gasRequestList) 
            	{
            		try {
            		
            			//process all customer requests
            			System.out.println("Starting to pump " + gasRequest.getAmountInLiters() + "L of " + gasRequest.getType() + " at "+ stationName);
            			double amountGas = buyGas(gasRequest.getType(), gasRequest.getAmountInLiters(), gasRequest.getMaxPricePerLiter());
            			System.out.println(gasRequest.getType() + " request finished, bought "+ amountGas + "L at " + stationName);
            			System.out.println();
            		
            		
            		} catch (NotEnoughGasException e) {
            			System.out.println("Not enough " + gasRequest.getType() + " gas! please try another station!");
            			System.out.println();
            		} catch (GasTooExpensiveException e) {
            			System.out.println(gasRequest.getType() +  " gas is too expensive! please try another station! ");
            			System.out.println();
            		} 
            	}
            	
            	//print me the results at the end of the day for each gas station
            	System.out.println();
            	System.out.println(stationName + ":");
            	System.out.println("NumberOfCancellationsNoGas " + getNumberOfCancellationsNoGas());
            	System.out.println("NumberOfCancellationsTooExpensive " + getNumberOfCancellationsTooExpensive());
            	System.out.println("NumberOfSales " + getNumberOfSales());
            	System.out.println("Revenue " + getRevenue());
            } 
        });
        requestThread.start();
    }
    
    
    //show me the remaining amount of gas of the gas pumps
    public void getGasPumpsAmount(){
        Thread amountThread = new Thread(new Runnable() {           
            public void run() { 
            	while(requestThread.isAlive())
            	{
            		for (GasPump gasPump : getGasPumps())
            			{
            				System.out.println("Remaining Amount of "+ gasPump.getGasType() + " " + gasPump.getRemainingAmount());
            			}
            		try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
            	}
            } 
        });
        amountThread.start();
    }
    
    

}
