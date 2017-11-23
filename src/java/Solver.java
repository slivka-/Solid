import java.awt.Point;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.ejb.EJB;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pl.jrj.dsm.IDSManagerRemote;


/**
 * @author Michał Śliwa
 */
@WebServlet(urlPatterns = {"/Solver"})
public class Solver extends HttpServlet
{
    private static final long serialVersionUID = 5674342757569182632L;
    //deployment descriptor form data source provider
    private final String dsDeploymentDescriptor = "java:global/ejb-project/DSManager!pl.jrj.dsm.IDSManagerRemote";
    //data source query
    private final String dbQuery = "SELECT * FROM %s";
    
    //EJB bean providing calculation functions
    @EJB
    private ISolidRemote solidBean;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        String dbTabName = "";
        //check url parameters for tableName
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements())
        {
            dbTabName = request.getParameter(paramNames.nextElement());
            break;
        }
        try (PrintWriter out = response.getWriter())
        {
            //check if table name exist
            if (!dbTabName.equalsIgnoreCase(""))
            {
                try
                {
                    //declare data table
                    ArrayList<Point.Double> data = new ArrayList<>();
                    //lookup DSManager EJB
                    Context ctx = new InitialContext();
                    IDSManagerRemote dsManager;
                    dsManager = (IDSManagerRemote)ctx.lookup(dsDeploymentDescriptor);
                    //get data source using info provided by DSManager EJB
                    DataSource source = (DataSource)ctx.lookup(dsManager.getDS());
                    //connect to data source
                    try (Connection c = source.getConnection())
                    {
                        //create sql statement
                        try (Statement s = c.createStatement())
                        {
                            //execute query
                            ResultSet result = s.executeQuery(String.format(dbQuery, dbTabName));
                            while (result.next())
                            {
                                //write data from result set to data table
                                data.add(new Point.Double(result.getFloat(2),result.getFloat(4)));
                            }
                        }
                    }
                    //if data table is not null, pass it to bean and print result
                    if (data.size()>0)
                    {
                        double surface = solidBean.CalculateConvexHullSurface(data);
                        out.println(String.format("%f", surface));
                    }
                    else
                        out.println("No data");
                }
                catch (NamingException | SQLException ex)
                {
                    //print errors
                    out.println(ex.toString());
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
