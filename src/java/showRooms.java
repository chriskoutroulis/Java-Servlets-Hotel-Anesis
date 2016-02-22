/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0nfr0ntier
 */
@WebServlet(urlPatterns = {"/showRooms"})
public class showRooms extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/anesis";
    private static final String DB_USER = "anesisdba";
    private static final String DB_PASS = "12345";
    private Connection database_conn;
    private Statement  dbStatement;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            out.println("<!doctype html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset=\"utf-8\">");
            out.println("<title>Ξενοδοχείο Άνεσις</title>");
            out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"stylesheet.css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<header>");
            out.println("<img src=\"reception900.jpg\" width=\"900\" height=\"233\">");
            out.println("</header>");
            out.println("<div id=\"surroundingImageTable\">");
            out.println("<div id=\"content\">");
            out.println("<h1>Hotel Anesis</h1>");
            out.println("<table id=\"resultTable\">");
            out.println("<tr>");
            out.println("<th class=\"resultData\">Α/Α</th>");
            out.println("<th class=\"resultData\">Δωμάτιο</th>");
            out.println("<th class=\"resultData\">Άφιξη</th>");
            out.println("<th class=\"resultData\">Αναχώρηση</th>");
            out.println("<th class=\"resultData\">Πλήθος</th>");
            out.println("<th class=\"resultData\">Όνομα Επικ.</th>");
            out.println("<th class=\"resultData\">Τηλέφωνο</th>");
            out.println("</tr>");

            //try {
            ResultSet results = getTable();
            if (results != null) {
                int i = 1;
                try {
                    while (results.next()) {

                        out.println("<tr class=\"resultRow\">");
                        out.println("<td class=\"resultData\">" + i++ + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("room") + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("arrival") + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("departure") + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("quantity") + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("contact_name") + "</td>");
                        out.println("<td class=\"resultData\">" + results.getString("phone") + "</td>");

                        out.println("</tr>");
                    } //End of While
                    
                    dbStatement.close();        // Ελευθερώνουμε τους πόρους από το Statement και ταυτόχρονα κλείνει και το ResultSet που προήρθε από αυτό το Statement.
                    
                    out.println("</table>");
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            } else {                            //Αν κατά το query δημιουργηθεί SQLException ή δεν γίνει καθόλου σύνδεση με τη βάση δεδομένων
                out.println("</table>");
                out.println("<h2>Σφάλμα ανάγνωσης από τη βάση δεδομένων</h2>");
            }
            
            out.println("<br>");
            out.println("<p><input type=\"button\" onClick=\"location.href='index.html'\" value=\"Επιστροφή\"></p>");

            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        }
    }

    private boolean connectDB() {
        boolean flag = true;
        try {
            Class.forName(DATABASE_DRIVER);
            database_conn = DriverManager.getConnection(
                    DATABASE_URL, DB_USER, DB_PASS);
        } catch (Exception ex) {
            database_conn = null;
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    private ResultSet getTable() {

        if (connectDB()) {
            try {
                dbStatement = database_conn.createStatement();
                String query = "SELECT * FROM rooms";
                ResultSet contents = dbStatement.executeQuery(query);
                return contents;

            } catch (SQLException ex) {
                return null;
            }
        } else {
            return null;
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
            throws ServletException, IOException {
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
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
