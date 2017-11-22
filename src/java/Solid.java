import javax.ejb.Stateless;

/**
 * EJB Bean for calculating surface
 * @author Michał Śliwa
 */
@Stateless
public class Solid implements ISolidRemote
{
    @Override
    public double CalculateConvexHull(double[][] data)
    {
        double output = 0.0;
        for (double[] d : data)
        {
            output += d[0] + d[1];
        }
        return output;
    }
    
}
