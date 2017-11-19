import javax.ejb.Remote;

/**
 * Interface for calculating surface
 * @author Michał Śliwa
 */
@Remote
public interface ISolidRemote
{
    public String Hello();
}
