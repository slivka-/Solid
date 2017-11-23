import java.awt.Point;
import java.util.ArrayList;
import javax.ejb.Remote;

/**
 * Interface for calculating surface
 * @author Michał Śliwa
 */
@Remote
public interface ISolidRemote
{
    public double CalculateConvexHullSurface(ArrayList<Point.Double> data);
}
