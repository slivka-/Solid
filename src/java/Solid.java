import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
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
    /**
     * Calculate surface area of convex hull
     * @param data list of points
     * @return area of convex hull
     */
    @Override
    public double CalculateConvexHullSurface(ArrayList<Point.Double> data)
    {
        double output = 0.0;
        //get convex hull points
        ArrayList<Point.Double> convexHull = CalculateConvexHull(data);
        Point.Double fp = convexHull.get(convexHull.size()-1);
        Point.Double f1, f2;
        //divide polygon created from points into triangles
        //and using shoelace formula to calculate surface
        for (int i = 1;i<convexHull.size()-1;i++)
        {
            f1 = convexHull.get(i-1);
            f2 = convexHull.get(i);
            output += Math.abs(((fp.x-f2.x)*(f1.y-fp.y))-((fp.x-f1.x)*(f2.y-fp.y)))/2; 
        }
        return output;
    }
    
    /**
     * Returns list of points creating convex hull of given set
     * @param data given list of points
     * @return list of points creating convex hull
     */
    private ArrayList<Point.Double> CalculateConvexHull(ArrayList<Point.Double> data)
    {
        //initialize stack
        PointStack stack = new PointStack();
        //sort points data
        ArrayList<Point.Double> sortedData = SortPoints(data);
        
        //put first 3 points on stack
        stack.push(sortedData.get(0));
        stack.push(sortedData.get(1));
        stack.push(sortedData.get(2));
        
        for (int i=3;i<sortedData.size();i++)
        {
            //while points are on the left side of section, pop from stack
            while (CalculateDet(stack.peekNext(), stack.peek(), sortedData.get(i)) < 0)
            {
                stack.pop();
            }
            //points is on right side, push it to stack
            stack.push(sortedData.get(i));
        }
        ArrayList<Point.Double> convexHull = new ArrayList<>();
        //write stack contents to output list
        while (!stack.isEmpty())
        {
            convexHull.add(stack.pop());
        }
        return convexHull;
    }
    
    /**
     * Calculates determinant of matrix created from given points
     * @param p1 first Point.Double
     * @param p2 second Point.Double
     * @param p3 third Point.Double
     * @return value of determinant
     */
    private double CalculateDet(Point.Double p1, Point.Double p2, Point.Double p3)
    {
        return p1.x*p2.y + p2.x*p3.y + p3.x*p1.y - p3.x*p2.y - p1.x*p3.y - p2.x*p1.y;  
    }
        
    /**
     * Sorts list of points by angle from OX axis
     * @param data list of points
     * @return sorted list of points
     */
    private ArrayList<Point.Double> SortPoints(ArrayList<Point.Double> data)
    {
        //output list
        ArrayList<Point.Double> output = new ArrayList<>();
        //temporary list
        ArrayList<PointWithAngle> pointAngles = new ArrayList<>();
        for (Point.Double p: data)
        {
            //calculate auxilliary variable for point
            double d = Math.abs(p.x) + Math.abs(p.y);
            double angle;
            //calculate angle depending on the quarter of coordnate system
            if (p.x >= 0)
            {
                if (p.y >=0)
                    angle = p.y/d;
                else
                    angle = 4-(Math.abs(p.y)/d);
            }
            else
            {
                if (p.y >= 0)
                    angle = 2-(p.y/d);
                else
                    angle = 2+(Math.abs(p.y)/d);
            }
            //add point with angle to temporary list
            pointAngles.add(new PointWithAngle(p, angle));
        }
        //sort temporary list by angle value, ascending
        Collections.sort(pointAngles, new PointComparator());
        //transfer sorted values to output list
        for (PointWithAngle pwa : pointAngles)
            output.add(pwa.point);
        return output;
    }
    
    /**
     * Represents point with its angle from OX axis
     */
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
    
    /**
     * Comparator for PointWithAngle class
     */
    private class PointComparator implements Comparator<PointWithAngle>
    {
        /**
         * 
         * @param o1 first PointWithAngle 
         * @param o2 second PointWithAngle
         * @return difference between points angles
         */
        @Override
        public int compare(PointWithAngle o1, PointWithAngle o2) {
            return (int)(o1.angle - o2.angle); 
        }
    }
    
    /**
     * Extended stack of Point.Double class
     */
    private class PointStack extends Stack<Point.Double>
    {
        //Serialization UID
        private static final long serialVersionUID = 5674345757569182633L;
        
        /**
         * Peeks second item from top
         * @return value of second item from top
         */
        public Point.Double peekNext()
        {
            return this.get(this.size()-2);
        }
    }
}
