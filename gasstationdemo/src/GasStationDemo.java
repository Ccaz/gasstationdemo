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
        final StarGasStation plutoGasStation = new StarGasStation("Pluto DeathStar station");
        
        
        //Mars gas station adding gas pumps
        marsGasStation.addGasPump(new GasPump(GasType.REGULAR, 2000, 1.21));
        marsGasStation.addGasPump(new GasPump(GasType.SUPER, 4000, 1.45));
        marsGasStation.addGasPump(new GasPump(GasType.DIESEL, 1000, 0.98));

        //adding customer gas requests
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 500, 1.22));
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 250, 0.77));
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 2500, 1.77));
        marsGasStation.addGasRequest(new GasRequest(GasType.SUPER, 2500, 0.67));
        marsGasStation.addGasRequest(new GasRequest(GasType.SUPER, 2500, 1.67));
        marsGasStation.addGasRequest(new GasRequest(GasType.REGULAR, 1500, 1.77));

        
        //Pluto gas station adding gas pumps
        plutoGasStation.addGasPump(new GasPump(GasType.SUPER, 3000, 1.25));
        plutoGasStation.addGasPump(new GasPump(GasType.DIESEL, 5000, 0.77));
        
        //adding customer gas requests
        plutoGasStation.addGasRequest(new GasRequest(GasType.SUPER, 200, 1.70));
        plutoGasStation.addGasRequest(new GasRequest(GasType.SUPER, 600, 1.50));
        plutoGasStation.addGasRequest(new GasRequest(GasType.DIESEL, 10000, 1.25));
        plutoGasStation.addGasRequest(new GasRequest(GasType.DIESEL, 2500, 0.90));
 
        //thread start
//        marsGasStation.start();
        plutoGasStation.start();

    }

}
