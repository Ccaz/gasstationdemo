package main;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
    ReentrantLock lock = new ReentrantLock();
	BlockingQueue<GasRequest> queue;

    public StarGasStation( final String stationName )
    {
        this.stationName = stationName;
        this.gasPumpsList = new ArrayList<GasPump>();
        this.gasRequestList = new ArrayList<GasRequest>();
        this.numberGasTooExpensive = 0;
        this.stationRevenue = 0;
        this.numberOfSales = 0;
        this.numberNoGas = 0;
        initGasPumps();
        initGasPrice();
        initGasRequests();
        
//        startPumpThreads();
//        testBlocking();
    }

    private void initGasPumps() {
    	addGasPump(new GasPump(GasType.REGULAR, 10000));
        addGasPump(new GasPump(GasType.SUPER, 10000));
        addGasPump(new GasPump(GasType.DIESEL, 10000));
		
	}

	private void initGasRequests() {
        addGasRequest(new GasRequest(GasType.REGULAR, 50, 1.22));
        addGasRequest(new GasRequest(GasType.REGULAR, 25, 0.77));
        addGasRequest(new GasRequest(GasType.SUPER, 25, 0.67));
        addGasRequest(new GasRequest(GasType.SUPER, 25, 3.67));
        addGasRequest(new GasRequest(GasType.DIESEL, 15, 1.77));
        addGasRequest(new GasRequest(GasType.DIESEL, 15, 1.77));
		
	}

	private void initGasPrice()
    {
        this.gasPriceList = new ArrayList<GasPrice>();
        for ( final GasType type : GasType.values() )
        {
            gasPriceList.add(new GasPrice(type, 0.00));
        }
		
		setPrice(GasType.REGULAR, 1.21);
        setPrice(GasType.SUPER, 2.22);
        setPrice(GasType.DIESEL, 0.87);
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
        { // check the gas type
            final boolean isRequestedGasType = gasPump.getGasType().equals(type);
            if ( isRequestedGasType )
            {
                // check if the gas price is right
                final double gasPrice = getPrice(type);
                if ( gasPrice < maxPricePerLiter )
                {
                    // check if the requested amount is available
                    final double remainingAmount = gasPump.getRemainingAmount();
                    if ( remainingAmount >= amountInLiters )
                    {
                        // if everything is correct, start to pump gas, return the amount, add the revenue and number of sales
                        gasPump.pumpGas(amountInLiters);
                        amount = amountInLiters;
                        this.stationRevenue += (gasPrice * amountInLiters);
                        this.numberOfSales++;

                    }
                    else
                    {
                        // if the requested amount of gas is too high
                        numberNoGas++;
                        throw new NotEnoughGasException();
                    }
                }
                else
                {
                    // if the requested gas is too expensive
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
        return this.stationRevenue;
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

    // go through the gasPriceList and check if the requested gas type is in the list, if yes return the price of the specific gas.
    // you could extend this by adding another exception if the specific gas type is not served at this station.
    @Override
    public double getPrice( final GasType type )
    {
        final double price = 0;

        for ( final GasPrice gasPrice : gasPriceList )
        {
            final boolean isType = gasPrice.getType().equals(type);
            if ( isType )
            {
                return gasPrice.getPrice();
            }
        }

        return price;

    }

    // go through the gasPriceList and set the new price for the specific gas type.
    // you could extend this by adding another exception if the specific gas type is not served at this station.
    @Override
    public void setPrice( final GasType type, final double price )
    {

        for ( final GasPrice gasPrice : gasPriceList )
        {
            final boolean isType = gasPrice.getType().equals(type);
            if ( isType )
            {
                gasPrice.setPrice(price);
            }
        }

    }

    private void addGasRequest( final GasRequest request )
    {
        this.gasRequestList.add(request);
    }

    private Collection<GasRequest> getGasRequestList()
    {
        return this.gasRequestList;
    }

    
    
    private void startPumpThreads()
    {
    	ExecutorService executor = Executors.newFixedThreadPool(3);
    	 //Starting 3 Threads, each one for a specific gas type
    		 executor.submit(() -> {
    			 pump(executor,GasType.REGULAR);
    		 });
    		 executor.submit(() -> {
    			 pump(executor,GasType.SUPER);
    		 });
    		 executor.submit(() -> {
    			 pump(executor,GasType.DIESEL);
    			 stop(executor);
    		 });
		  
    }

    private void pump(ExecutorService executor, GasType gasType) {
    	String threadName = Thread.currentThread().getName();
    	System.out.println("Hello " + threadName);
    	//each Threads iterates through the request list, if the Thread can deliver the requested gas type, lock the process and call buyGas()
    	for ( final GasRequest gasRequest : getGasRequestList() )
    	{
    		if(gasRequest.getType().equals(gasType))
    		{
    			try
    			{
    				lock.lock();
    				System.out.println(threadName + " Starting to pump " + gasRequest.getAmountInLiters() + "L of " + gasRequest.getType() + " at " + stationName);
    				final double amountGas = buyGas(gasRequest.getType(), gasRequest.getAmountInLiters(), gasRequest.getMaxPricePerLiter());
    				System.out.println(threadName + " " + gasRequest.getType() + " request finished, bought " + amountGas + "L at " + stationName);
    				lock.unlock();
    				
    			}
    			catch ( final NotEnoughGasException e )
    			{
    				System.out.println("Not enough " + gasRequest.getType() + " gas! please try another station!");
    				lock.unlock();
    				
    			}
    			catch ( final GasTooExpensiveException e )
    			{
    				System.out.println(gasRequest.getType() + " gas is too expensive! please try another station! ");
    				lock.unlock();
    			}
    		}
    		else
    		{
    			continue;
    		}
    	}
    	
    	System.out.println(threadName + " Requests finished!");
    }
    
//    private void testBlocking()
//    {
//    	//experimenting with BlockingQueue
//    	ExecutorService executor = Executors.newFixedThreadPool(3);
//    	queue = new ArrayBlockingQueue<GasRequest>(12);
//    	for ( final GasRequest gasRequest : getGasRequestList() ){
//    		try {
//    			queue.put(gasRequest);
//    		} catch (Exception e) {
//    			e.printStackTrace();
//    		}
//    	}
//    	
//    	try {
//    		
//    		executor.submit(() -> {
//    			pumpWithBlockingQueue(executor, GasType.REGULAR,queue);
//    		});
//    		executor.submit(() -> {
//    			pumpWithBlockingQueue(executor, GasType.SUPER,queue);
//    		});
//    		executor.submit(() -> {
//    			pumpWithBlockingQueue(executor, GasType.DIESEL,queue);
//    			stop(executor);
//    		});
//    		
//    	} catch (Exception e) {
//    		// TODO Auto-generated catch block
//    		e.printStackTrace();
//    	}
//    }
//    
//    private void pumpWithBlockingQueue(ExecutorService executor, GasType gasType, BlockingQueue<GasRequest> queue) {
//    	String threadName = Thread.currentThread().getName();
//    	System.out.println("Hello " + threadName);
//    		
//    	
//    			try {
//    					GasRequest tmpGasRequest = queue.take().getGasRequest();
//    				
//    					if(tmpGasRequest.getType().equals(gasType))
//    					{
//    						try
//    						{
//    							lock.lock();
//    							System.out.println(threadName + " Starting to pump " + tmpGasRequest.getAmountInLiters() + "L of " + tmpGasRequest.getType() + " at " + stationName);
//    							final double amountGas = buyGas(tmpGasRequest.getType(), tmpGasRequest.getAmountInLiters(), tmpGasRequest.getMaxPricePerLiter());
//    							System.out.println(threadName + " " + tmpGasRequest.getType() + " request finished, bought " + amountGas + "L at " + stationName);
//    							lock.unlock();
//    							
//    						}
//    						catch ( final NotEnoughGasException e )
//    						{
//    							System.out.println("Not enough " + tmpGasRequest.getType() + " gas! please try another station!");
//    							lock.unlock();
//    							
//    						}
//    						catch ( final GasTooExpensiveException e )
//    						{
//    							System.out.println(tmpGasRequest.getType() + " gas is too expensive! please try another station! ");
//    							lock.unlock();
//    						}
//    					}
//    			} catch (InterruptedException e) {
//    				// TODO Auto-generated catch block
//    				e.printStackTrace();
//    			}
//    	
//    }

	private void stop(ExecutorService executor) {
    	try {
    	    System.out.println("attempt to shutdown executor");
    	    executor.shutdown();
    	    executor.awaitTermination(10, TimeUnit.SECONDS);
    	}
    	catch (InterruptedException e) {
    	    System.err.println("tasks interrupted");
    	}
    	finally {
    	    if (!executor.isTerminated()) {
    	        System.err.println("cancel non-finished tasks");
    	    }
    	    executor.shutdownNow();
    	    System.out.println("shutdown finished");
    	}
	}

}
