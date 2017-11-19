import javax.ejb.Stateless;

/**
 * EJB Bean for calculating surface
 * @author Michał Śliwa
 */
@Stateless
public class Solid implements ISolidRemote
{

    @Override
    public String Hello()
    {
        return "Hello World!";
    }
    
}
