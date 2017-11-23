import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import javax.ejb.Stateless;

/**
 * EJB Bean for calculating convex hull surface
 * @author Michał Śliwa
 */
@Stateless
public class Solid implements ISolidRemote
{
    @Override
    public double CalculateConvexHullSurface(ArrayList<Point.Double> data)
    {
        double output = 0.0;
        ArrayList<Point.Double> convexHull = CalculateConvexHull(data);

        return output;
    }
    
    private ArrayList<Point.Double> CalculateConvexHull(ArrayList<Point.Double> data)
    {
        PointStack stack = new PointStack();
        ArrayList<Point.Double> sortedData = SortPoints(data);
        
        stack.push(sortedData.get(0));
        stack.push(sortedData.get(1));
        stack.push(sortedData.get(2));
        
        for(int i=3;i<sortedData.size();i++)
        {
            while (CalculateDet(stack.peekNext(), stack.peek(), sortedData.get(i)) < 0)
            {
                stack.pop();
            }
            stack.push(sortedData.get(i));
        }
        ArrayList<Point.Double> convexHull = new ArrayList<>();
        while(!stack.isEmpty())
        {
            convexHull.add(stack.pop());
        }
        return convexHull;
    }
    
    private double CalculateDet(Point.Double p1, Point.Double p2, Point.Double p3)
    {
        return p1.x*p2.y + p2.x*p3.y + p3.x*p1.y - p3.x*p2.y - p1.x*p3.y - p2.x*p1.y;  
    }
    
    private ArrayList<Point.Double> SortPoints(ArrayList<Point.Double> data)
    {
        ArrayList<Point.Double> output = new ArrayList<>();
        ArrayList<PointWithAngle> pointAngles = new ArrayList<>();
        for(Point.Double p: data)
        {
            double d = Math.abs(p.x) + Math.abs(p.y);
            double angle;
            if(p.x >= 0)
            {
                if(p.y >=0)
                    angle = p.y/d;
                else
                    angle = 4-(Math.abs(p.y)/d);
            }
            else
            {
                if(p.y >= 0)
                    angle = 2-(p.y/d);
                else
                    angle = 2+(Math.abs(p.y)/d);
            }
            pointAngles.add(new PointWithAngle(p, angle));
        }
        pointAngles.sort(new PointComparator());
        for(PointWithAngle pwa : pointAngles)
            output.add(pwa.point);
        return output;
    }
    
    private class PointWithAngle
    {
        public Point.Double point;
        public double angle;
        
        public PointWithAngle(Point.Double p, double a)
        {
            this.point = p;
            this.angle = a;
        }
    }
      
    private class PointComparator implements Comparator<PointWithAngle>
    {
        @Override
        public int compare(PointWithAngle o1, PointWithAngle o2) {
            return (int)(o1.angle - o2.angle); 
        }
    }
    
    private class PointStack extends Stack<Point.Double>
    {
        public Point.Double peekNext()
        {
            return this.get(this.size()-2);
        }
    }
}
