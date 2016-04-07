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
public class StarGasStation implements GasStation
{
    Collection<GasPump> gasPumpsList;
    Collection<GasRequest> gasRequestList;
    Collection<GasPrice> gasPriceList;
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
        initGasPrice();
    }

    private void initGasPrice() {
    	this.gasPriceList = new ArrayList<GasPrice>();
    	for (GasType type : GasType.values()) {
			gasPriceList.add(new GasPrice(type, 1.21));
		}
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
    public double buyGas( final GasType type, final double amountInLiters, final double maxPricePerLiter ) throws NotEnoughGasException, GasTooExpensiveException
       
    {
        double amount = 0;

        for ( final GasPump gasPump : getGasPumps() )
        {	//check the gas type
            final boolean isRequestedGasType = gasPump.getGasType().equals(type);
            if ( isRequestedGasType )
            {
            	//check if the gas price is right
                final double gasPrice = getPrice(type);
                if ( gasPrice < maxPricePerLiter )
                {	
                	//check if the requested amount is available
                    final double remainingAmount = gasPump.getRemainingAmount();
                    if ( remainingAmount >= amountInLiters )
                    {
                    	//if everything is correct, start to pump gas, return the amount, add the revenue and number of sales
                        gasPump.pumpGas(amountInLiters);
                        amount = amountInLiters;
                        this.stationRevenue += (gasPrice * amountInLiters);
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
        
        for (GasPrice gasPrice : gasPriceList) {
			boolean isType = gasPrice.getType().equals(type);
			if(isType)
			{
				return gasPrice.getPrice();
			}
		}
        
        return price;
        
    }

    @Override
    public void setPrice( final GasType type, final double price )
    {
        
        for (GasPrice gasPrice : gasPriceList) {
        	boolean isType = gasPrice.getType().equals(type);
        	if(isType)
        	{
        		gasPrice.setPrice(price);
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
    
    public void startGasPumps(){
    	//start a new thread for each gas pump. 
    	//TODO: Solution for one pump per type at the same time is missing.
    	
    	for (GasPump gasPump : getGasPumps()) {
    		GasType gasType = gasPump.getGasType();
    		PumpThread pumpThread = new PumpThread(stationName, gasType);
    		pumpThread.start();
		}
    }
    
    
    class PumpThread extends Thread
    {
    	GasType type;
    	PumpThread (String name, GasType type)
    	   {
    	      super (name); 
    	      this.type = type;
    	   }
    	
    	
    	   public void run ()
    	   {
    		   
    	      System.out.println ("Gas Type: " + getType());
    	      
    	      for (GasRequest gasRequest : getGasRequestList()) {
				
    	    	  GasType gasRequestType = gasRequest.getType();
    	    	  if(gasRequestType == this.type)
    	    	  {
    	    		  try {
    	    			  System.out.println("Starting to pump " + gasRequest.getAmountInLiters() + "L of " + gasRequest.getType() + " at "+ stationName);
    	    			  
    	    			  double amountGas = buyGas(gasRequest.getType(), gasRequest.getAmountInLiters(), gasRequest.getMaxPricePerLiter());
						
    	    			  System.out.println(gasRequest.getType() + " request finished, bought "+ amountGas + "L at " + stationName);
						
    	    		  } catch (NotEnoughGasException e) {
              			System.out.println("Not enough " + gasRequest.getType() + " gas! please try another station!");
              			
              		} catch (GasTooExpensiveException e) {
              			System.out.println(gasRequest.getType() +  " gas is too expensive! please try another station! ");
              		} 
    	    	  }
			}
    	      
    	   }
    	   
    	   public GasType getType()
    	   {
    		   return this.type;
    	   }
    	   
    }
    
    

}
