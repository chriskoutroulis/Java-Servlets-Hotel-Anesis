/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author c0nfr0ntier
 */
@WebServlet(urlPatterns = {"/addRooms"})
public class addRooms extends HttpServlet {

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

    private Calendar todayCal = Calendar.getInstance();
    private Calendar arrivalCal;
    private Calendar departureCal;

    private Connection database_conn;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] expectedFields = {"room", "arrivalDate", "departureDate", "numOfPeople", "contactName", "phoneNumber"};
        int numOfFields = request.getParameterMap().keySet().size();
        String[] receivedFields = request.getParameterMap().keySet().toArray(new String[numOfFields]);

        if (Arrays.equals(receivedFields, expectedFields)) { //Αν υπάρχουν όλα τα πεδία της φόρμας αυτά που περιμέναμε με βάση το Name τους.

            boolean roomOkay = false;

            String[] rooms = {"K1", "K2", "K3", "K4", "K5", "A1", "A2", "A3", "A4", "A5"};
            for (String oneRoom : rooms) {                            // Αν το δωμάτιο έχει αναμενόμενη τιμή.
                if (request.getParameter("room").equalsIgnoreCase(oneRoom)) {
                    roomOkay = true;
                } //End if
            } // End for 

            if (!roomOkay) {
                response.sendRedirect("error.html");
                return;
            }

            String[] arrival = request.getParameter("arrivalDate").split("-");
            arrivalCal = Calendar.getInstance();
            try {
                arrivalCal.set(Integer.parseInt(arrival[0]), (Integer.parseInt(arrival[1]) - 1), Integer.parseInt(arrival[2])); // Οι μήνες στο Calendar ξεκινούν από το το 0, γι'αυτό αφαιρούμε 1 από το μήνα που δίνει ο χρήστης.

                arrivalCal.setLenient(false); // Ορισμός αυστηρής αναπαράστασης για την ημερομηνία. Όταν κληθεί η μέθοδος get θα ελέγξει αν τα νούμερα που μπήκαν 
                //στην ημερομηνία είναι έγκυρα. Ρίχνει exception αν δεν είναι.
                arrivalCal.get(Calendar.YEAR); // Ο έλεγχος για σωστές τιμές στην ημερομηνία πραγματοποιείται με την κλήση της μεθόδου get.
                //΄Έτσι δεν επιτρέπονται ημερομηνίες που δεν υπάρχουν, όπως π.χ. 31/06 (αφού ο Ιούνιος φτάνει μέχρι 30).  

                if (todayCal.get(Calendar.YEAR) <= Integer.parseInt(arrival[0]) && Integer.parseInt(arrival[0]) > todayCal.get(Calendar.YEAR) + 1) {  //Να επιτρέπονται μόνο κρατήσεις σε σε βάθος χρόνου ενός έτους.
                    throw new RuntimeException();
                } //End if
            } catch (RuntimeException ex) {   //Σε περίπτωση που  η ημερομηνία δεν αποτελείται από ακεραίους αριθμούς ή δεν περιέχει κατάλληλες τιμές για χρονολογία, μήνα ή μέρα.
                response.sendRedirect("error.html");
                return;
            }

            if (todayCal.compareTo(arrivalCal) > 0) { // Αν η ημερομηνία άφιξης είναι πριν από την τωρινή ημερομηνία (παρελθοντική).
                response.sendRedirect("error.html");
                return;

            } else {
                String[] departure = request.getParameter("departureDate").split("-");
                departureCal = Calendar.getInstance();
                try {
                    departureCal.set(Integer.parseInt(departure[0]), (Integer.parseInt(departure[1]) - 1), Integer.parseInt(departure[2])); // Οι μήνες στο Calendar ξεκινούν από το το 0, γι'αυτό αφαιρούμε 1 από το μήνα που δίνει ο χρήστης.
                    departureCal.setLenient(false); // Ορισμός αυστηρής αναπαράστασης για την ημερομηνία. Όταν κληθεί η μέθοδος get θα ελέγξει αν τα νούμερα που μπήκαν στην ημερομηνία είναι έγκυρα. Ρίχνει exception αν δεν είναι.
                    departureCal.get(Calendar.YEAR); // Ο έλεγχος για σωστές τιμές στην ημερομηνία πραγματοποιείται με την κλήση της μεθόδου get.

                    if (todayCal.compareTo(departureCal) > 0 || departureCal.compareTo(arrivalCal) < 0) { //Αν η ημερομηνία αναχώρησης είναι πριν από την τωρινή ημερομηνία ή αν η ημερομηνία αναχώρησης είναι πριν από την ημερομηνία άφιξης. 
                        //Επιτρέπεται η αναχώρηση να είναι την ίδια μέρα με την άφιξη.
                        throw new RuntimeException();
                    }//End if

                    if (departureCal.get(Calendar.YEAR) > arrivalCal.get(Calendar.YEAR) + 10) { //Αν η χρονολογία αναχώρησης είναι μεγαλύτερη κατά 10 χρόνια από την ημερομηνία άφιξης, τότε να δίνει σφάλμα.
                        throw new RuntimeException();
                    } //End if

                } catch (RuntimeException ex) { //Αν η ημερομηνία δεν αποτελείται από 3 πεδία που χωρίζονται με παύλα 
                                                //και για οποιαδήποτε απο τις παραπάνω περιπτώσεις που ρίχνουμε RuntTime Exception.
                    response.sendRedirect("error.html");
                    return;
                }
            }// End if

            int people = 1;
            try {
                people = Integer.parseInt(request.getParameter("numOfPeople"));   // Αν ο αριθμός ατόμων δεν είναι αριθμός, θα δημιουργηθεί NumberFormatException.
                if (people < 1 || people >= 16) {  //Αν ο αριθμός ατόμων δεν είναι μεταξύ 1 και 15 τότε θα βγάλει τη σελίδα σφάλματος.
                    throw new NumberFormatException();
                }//End if
            } catch (NumberFormatException ex) {
                response.sendRedirect("error.html");
                return;
            }

            if (request.getParameter("contactName").length() < 3) { //Αν το όνομα επικοινωνία είναι μικρότερο από 3 χαρακτήρες να πάει στη σελίδα σφάλματος.
                response.sendRedirect("error.html");
                return;
            } //End if

            try {
                Long phoneLong = Long.parseLong(request.getParameter("phoneNumber")); //Αν ο τηλεφωνικός αριθμός περιέχει χαρακτήρες, τότε θα δημιουργηθεί NumberFormatException.
                if (request.getParameter("phoneNumber").length() != 10 || phoneLong<=0 ) { //Αν ο τηλεφωνικός αριθμός δεν είναι ακριβώς 10 ψηφία ή
                                                                                            //  ο αριθμός είναι αρνητικός ή 0  τότε να πάει στη σελίδα σφάλματος.
                    throw new NumberFormatException();
                } //End if
            } catch (NumberFormatException ex) {
                response.sendRedirect("error.html");
                return;
            }
     
            boolean recordStatus = createRecord(request.getParameter("room").toUpperCase(), request.getParameter("arrivalDate"), request.getParameter("departureDate"),
                    request.getParameter("numOfPeople"), request.getParameter("contactName"), request.getParameter("phoneNumber"));

            if (!recordStatus) {                    //Αν ΔΕΝ γίνει η εγγραφή στη βάση δεδομένων.
                response.sendRedirect("error.html");
                return;
            }

            response.sendRedirect("success.html"); //Όλα τα πεδία της φόρμας πέρασαν όλους τους ελέγχους και η εισαγωγή στη βάση δεδομένων έγινε με επιτυχία.

        } else {                                   //Αν λείπει κάποιο από τα πεδία ή δεν έχουν ακριβώς τα Name που περιμένουμε.
            response.sendRedirect("error.html");

        } //End if
    } //End processRequest()

//      
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

    private boolean createRecord(String room, String arrivalDate, String departureDate,
            String people, String name, String phone) {

        if (connectDB()) {                                  //Αν γίνει σύνδεση με τη βάση δεδομένων.
            try (Statement dbStatement = database_conn.createStatement()) {
                String sql = "INSERT INTO rooms (room, arrival, departure, quantity, contact_name, phone) "
                        + "VALUES ('" + room + "', '" + arrivalDate + "', '" + departureDate + "', "
                        + people + ", '" + name + "', '" + phone + "')";
                dbStatement.executeUpdate(sql);
                return true;
            } catch (SQLException ex) { //Αν η εκτέλεση της SQL εντολής αποτύχει
                return false;
            }
        } else {                       // Αν δεν γίνει σύνδεση με τη βάση δεδομένων
            return false;
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
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        processRequest(request, response);
//    }

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
