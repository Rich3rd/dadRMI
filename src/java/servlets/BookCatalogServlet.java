package servlets;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import database.*;
import cart.*;
import exception.*;

public class BookCatalogServlet extends HttpServlet {
    private BookDBAO bookDB;
    @Override
    public void init() throws ServletException {
        bookDB = (BookDBAO) getServletContext().getAttribute("bookDB");
        if (bookDB == null) throw new UnavailableException("Couldn't get database.");
    }
    @Override
    public void destroy() {bookDB = null;}
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
        response.setContentType("text/html");
        response.setBufferSize(8192);
        String contextPath = request.getContextPath();
        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Book Catalog</title></head><body>");
        getServletContext().getRequestDispatcher("/Banner").include(request, response);
        String bookId = request.getParameter("Id");
        if (bookId != null) {
            try {
                BookDetails book = bookDB.getBookDetails(bookId);
                cart.add(bookId, book);
                out.println("<p><h3><font color='red'>You added <i>" + book.getTitle() +
                    "</i> to your shopping cart.</font></h3>");
            } catch (BookNotFoundException ex) {
                response.reset();
                throw ex;
            }
        }
        //Give the option of checking cart or checking out if cart not empty
        if (cart.getNumberOfItems() > 0) {
            out.println("<p><strong><a href='" +
                response.encodeURL(contextPath+ "/BookShowCart") +
                "'>Check Shopping Cart</a>&nbsp;&nbsp;&nbsp;<a href='" +
                response.encodeURL(contextPath+ "/BookCashier")+"'>Buy Your Books</a></p></strong>");
        }
        // Always prompt the user to buy more -- get and show the catalog
        out.println("<h3>Please choose from our selections:</h3><center><table border='1' summary='layout'>");
        try {
            Collection coll = bookDB.getBooks();
            Iterator i = coll.iterator();
            while (i.hasNext()) {
                BookDetails book = (BookDetails) i.next();
                bookId = book.getId();
                //Print out info on each book in its own two rows
                out.println("<tr><td bgcolor='#ffffaa'><a href='" +
                    response.encodeURL(contextPath+"/BookDetails?Id=" + bookId) + 
                    "'> <strong>" +book.getTitle()+"&nbsp;</strong></a></td>" +
                    "<td bgcolor='#ffffaa' rowspan='2'>$&nbsp;" + book.getPrice() +
                    "&nbsp; </td><td bgcolor='#ffffaa' rowspan='2'><a href='" +
                    response.encodeURL(contextPath+"/BookCatalog?Id=" + bookId) + 
                    "'> &nbsp;Add to Cart&nbsp;</a></td></tr>" +
                    "<tr><td bgcolor='white'>&nbsp; &nbsp;by&nbsp;<em>" + book.getAuthor()+"</em></td></tr>");
            }
        } catch (BooksNotFoundException ex) {
            response.reset();
            throw ex;
        }
        out.println("</table></center></body></html>");
        out.close();
    }
    @Override
    public String getServletInfo() {return "Adds books to the user's shopping cart and prints the catalog.";}
}
