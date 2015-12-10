package servlets;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import cart.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookCashierServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, RemoteException {
        HttpSession session = request.getSession();
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        response.setContentType("text/html");
        String contextPath = request.getContextPath();
        PrintWriter out = response.getWriter();
        String amount = "MYR " + Double.toString(cart.getTotal());
        if (request.getParameter("button1") != null) {
            try {
                amount = "USD " + Double.toString(cart.convertUSD(cart.getTotal()));
            } catch (NotBoundException ex) {
                Logger.getLogger(BookCashierServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (request.getParameter("button2") != null) {
            try {
                amount = "GBP " + Double.toString(cart.convertUK(cart.getTotal()));
            } catch (NotBoundException ex) {
                Logger.getLogger(BookCashierServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (request.getParameter("button3") != null) {
            try {
                amount = "MYR " + Double.toString(cart.convertMYR(cart.getTotal()));
            } catch (NotBoundException ex) {
                Logger.getLogger(BookCashierServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        out.println("<html><head><title>Cashier</title></head><body>");
        getServletContext().getRequestDispatcher("/Banner").include(request, response);
         // Go back to catalog
         out.println("<p> &nbsp; <p><strong><a href='" +response.encodeURL(
                 contextPath+"/BookCatalog") +"'>Back to Catalog</a>" );                

        // Print out the total and the form for the user
        out.println("<p>Your total purchase amount is:<strong>&nbsp;" +
        amount
        + "<form action=\"" + response.encodeURL(contextPath + "/BookCashier") + "\" method=\"get\">\n" +
        "<input type=\"submit\" name=\"button1\" value=\"Convert to USD\" />\n" +
        "<input type=\"submit\" name=\"button2\" value=\"Convert to UK\" />\n" +
        "<input type=\"submit\" name=\"button3\" value=\"Convert to MYR\" />\n" +
        "</form>" +
            "</strong><p>To purchase the items in your shopping cart, please provide us with the following information:<form action='" +
            response.encodeURL(contextPath+ "/BookReceipt") +
            "' method='post'><table summary='layout'><tr>" +
            "<td><strong>Name:</strong></td>" +
            "<td><input type='text' name='cardname'" +
            "value='Tong Sam Pah' size='19'></td></tr>" + 
            "<tr><td><strong>Credit Card Number:</strong></td>" +
            "<td><input type='text' name='cardnum' " +
            "value='xxxx xxxx xxxx xxxx' size='19'></td></tr>" +
            "<tr><td></td><td><input type='submit' value='Submit Information'></td></tr></table>" +
            "</form></body></html>");
        out.close();
    }
    @Override
    public String getServletInfo() {
        return "Takes the user's name and credit card number so that the user can buy the books.";
    }
}
