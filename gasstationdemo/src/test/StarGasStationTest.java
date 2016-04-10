package test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import main.GasRequest;
import main.StarGasStation;
import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author TTkocz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StarGasStationTest
{
	 final double DELTA = 1e-15;
	 ReentrantLock lock = new ReentrantLock();
	 final StarGasStation testStation = new StarGasStation("testStation");
	 Collection<GasRequest> gasRequestList = new ArrayList<GasRequest>();
	 
	 
	 //ExecutorService for Thread testing
	 public void startPumpThreads()
	    {
		 
		 testStation.setPrice(GasType.REGULAR, 1.21);
	     testStation.setPrice(GasType.SUPER, 2.22);
	     testStation.setPrice(GasType.DIESEL, 0.87);
		 
	     //test data
		 gasRequestList.add(new GasRequest(GasType.REGULAR, 50, 1.22));
		 gasRequestList.add(new GasRequest(GasType.REGULAR, 25, 0.77));
		 gasRequestList.add(new GasRequest(GasType.SUPER, 250, 0.67));
		 gasRequestList.add(new GasRequest(GasType.SUPER, 250, 3.67));
		 gasRequestList.add(new GasRequest(GasType.DIESEL, 150, 1.77));
		 gasRequestList.add(new GasRequest(GasType.DIESEL, 150, 1.77));
		    	
		 
		 ExecutorService executor = Executors.newFixedThreadPool(3);
		 //Starting 3 Threads, each one for a specific gas type
	    		 executor.submit(() -> {
	    			 pumpThread(executor,GasType.REGULAR);
	    		 });
	    		 executor.submit(() -> {
	    			 pumpThread(executor,GasType.SUPER);
	    		 });
	    		 executor.submit(() -> {
	    			 pumpThread(executor,GasType.DIESEL);
	    		 });
			  
	    		 stop(executor);
	    }
	 
	 public void pumpThread(ExecutorService executor, GasType gasType) {
	    	String threadName = Thread.currentThread().getName();
	    	System.out.println("Hello " + threadName);
	    	
	    	//each Threads iterates through the request list, if the Thread can deliver the requested gas type, lock the process and call buyGas()
	    	for ( final GasRequest gasRequest : this.gasRequestList )
	    	{
	    		if(gasRequest.getType().equals(gasType))
	    		{
	    			try
	    			{
	    				lock.lock();
	    				System.out.println(threadName + " Starting to pump " + gasRequest.getAmountInLiters() + "L of " + gasRequest.getType());
	    				final double amountGas = testStation.buyGas(gasRequest.getType(), gasRequest.getAmountInLiters(), gasRequest.getMaxPricePerLiter());
	    				System.out.println(threadName + " " + gasRequest.getType() + " request finished, bought " + amountGas + "L");
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
	 	
	 
	 	
		private void stop(ExecutorService executor) {
	    	try {
	    	    System.out.println("attempt to shutdown executor");
	    	    executor.shutdown();
	    	    executor.awaitTermination(10, TimeUnit.MINUTES);
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
	 
	 //the first 4 test cases are to brittle, if the request list changes, the test cases will fail.

	@Test
	public void test2CheckRevenue() {
		startPumpThreads();
		final double actualRevenue = this.testStation.getRevenue();
        final double expectedRevenue = (50 * 1.21) + (250 * 2.22) + (300 * 0.87);
        assertEquals(expectedRevenue, actualRevenue, DELTA);
	}
	@Test
	public void test3checkNumberOfSales() {
		startPumpThreads();
		final int actualNumberOfSales = testStation.getNumberOfSales();
        final int expectedNumberOfSales = 4;
        assertEquals(expectedNumberOfSales, actualNumberOfSales);
	}
	@Test
	public void test4checkNumberOfCancellationsTooExpensive() {
		startPumpThreads();
		final int actualNumberOfCancellationsTooExpensive = testStation.getNumberOfCancellationsTooExpensive();
        final int expectedNumberOfCancellationsTooExpensive = 2;
        assertEquals(expectedNumberOfCancellationsTooExpensive, actualNumberOfCancellationsTooExpensive, DELTA);
	}
	@Test
	public void test5checkNumberOfCancellationsNoGas() {
		startPumpThreads();
		final int actualNumberOfCancellationsNoGas = testStation.getNumberOfCancellationsNoGas();
        final int expectedNumberOfCancellationsNoGas = 0;
        assertEquals(expectedNumberOfCancellationsNoGas, actualNumberOfCancellationsNoGas, DELTA);
	}
	@Test
	public void test6checkDieselGasPrice() {
		final double actualDieselPrice = testStation.getPrice(GasType.DIESEL);
        final double expectedDieselPrice = 0.87;
        assertEquals(expectedDieselPrice, actualDieselPrice, DELTA);
	}
	@Test
	public void test7checkSuperGasPrice() {
		final double actualSuperPrice = testStation.getPrice(GasType.SUPER);
        final double expectedSuperPrice = 2.22;
        assertEquals(expectedSuperPrice, actualSuperPrice, DELTA);
	}
	
	@Test
	public void test8RegularGasPrice() {
		final double actualRegularPrice = testStation.getPrice(GasType.REGULAR);
	    final double expectedRegularPrice = 1.21;
	    assertEquals(expectedRegularPrice, actualRegularPrice, DELTA);
		}
	
	
}
