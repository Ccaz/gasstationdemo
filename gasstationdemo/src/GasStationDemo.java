import net.bigpoint.assessment.gasstation.GasPump;
import net.bigpoint.assessment.gasstation.GasType;
import net.bigpoint.assessment.gasstation.exceptions.GasTooExpensiveException;
import net.bigpoint.assessment.gasstation.exceptions.NotEnoughGasException;


/**
 * @author TTkocz
 * Starter class for the gas station demo
 *
 */
public class GasStationDemo
{

  
    public static void main( final String[] args ) 
    {
    	//create new gas stations
        final StarGasStation marsGasStation = new StarGasStation("Mars StarKiller station");
        
        
        //Mars gas station adding gas pumps
        marsGasStation.addGasPump(new GasPump(GasType.REGULAR, 10000));
        marsGasStation.addGasPump(new GasPump(GasType.SUPER, 10000));
        marsGasStation.addGasPump(new GasPump(GasType.DIESEL, 10000));

        //adding customer gas requests
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 500, 1.22));
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 250, 0.77));
        marsGasStation.addGasRequest(new GasRequest(GasType.SUPER, 2500, 0.67));
        marsGasStation.addGasRequest(new GasRequest(GasType.SUPER, 2500, 1.67));
        marsGasStation.addGasRequest(new GasRequest(GasType.DIESEL, 1500, 1.77));
        marsGasStation.addGasRequest(new GasRequest(GasType.DIESEL, 1500, 1.77));

 
        //thread start
//        marsGasStation.processRequestList();
//        marsGasStation.getGasPumpsAmount();
        marsGasStation.startGasPumps();

    }

}
