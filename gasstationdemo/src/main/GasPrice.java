package main;
import net.bigpoint.assessment.gasstation.GasType;

/**
 * @author TTkocz
 *  class for the gas prices, each gas type has it own price and gas prices will be stored in a list so you can set or get the prices with the station methods
 *  -void setPrice(GasType type, double price)
 *  -double getPrice(GasType type)
 *  each station has their own gas prices.
 *  @param type
*            The type of gas
*   @param price
*            The price of the specific gas
 *
 */

public class GasPrice
{

    private GasType type;
    private double price;

    public GasPrice( final GasType type, final double price )
    {
        this.setType(type);
        this.setPrice(price);
    }

    public GasType getType()
    {
        return type;
    }

    public void setType( final GasType type )
    {
        this.type = type;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice( final double price )
    {
        this.price = price;
    }

}
