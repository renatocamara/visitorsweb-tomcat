package com.oracle.tomcat.example.visitorsweb;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.omg.CORBA.SystemException;

@SuppressWarnings("serial")
public class VisitorServlet extends HttpServlet {
	private static String JTA_PU_NAME = "statCreateTablesJTA";
    private EntityManagerFactory emf_;
    private static String OP;
    private static String fixedPart;
    private static String endPart;
    
    static {
        VisitorServlet.OP = "operation";
        VisitorServlet.fixedPart = "<html>\n<head>\n<title>Visitor</title>\n<link rel='stylesheet' type='text/css' href='css/visitors.css'>\n</head>\n<body>\n<div id=\"content\">\n<h1 id=\"welcome\">Welcome to Oracle Java Cloud Service</h1>\n<p>Please sign our visitor log</p>\n<div id=\"manage\">\n\n<form id=\"add\" method=\"post\">\n<div class=\"ctrlrow\">\n<label for=\"create_name\">Name: </label><input type=\"text\" name=\"create_name\" value=\"\">\n</div>\n<div class=\"ctrlrow\">\n<label for=\"create_comment\">Comment: </label><input type=\"text\" name=\"create_comment\" value=\"\">\n</div>\n<input type=\"hidden\" name=\"operation\" value=\"create\">\n<input type=\"submit\" value=\"Add\">\n</form>\n\n<form id=\"update\" method=\"post\">\n<p>Update Visitor:</p>\n<div class=\"ctrlrow\">\n<label for=\"id_to_update\">Id: </label><input type=\"text\" name=\"id_to_update\" value=\"\">\n</div>\n<div class=\"ctrlrow\">\n<label for=\"new_name\">Name: </label><input type=\"text\" name=\"new_name\" value=\"\">\n</div>\n<div class=\"ctrlrow\">\n<label for=\"new_comment\">Comment: </label><input type=\"text\" name=\"new_comment\" value=\"\">\n</div>\n<input type=\"hidden\" name=\"operation\" value=\"update\">\n<input type=\"submit\" value=\"Update\">\n</form>\n\n<form id=\"delete\" method=\"post\">\n<p>Delete Visitor:</p>\n<div class=\"ctrlrow\">\n<label for=\"id_to_delete\">Id: </label><input type=\"text\" name=\"id_to_delete\" value=\"\">\n</div>\n<input type=\"hidden\" name=\"operation\" value=\"delete\">\n<input type=\"submit\" value=\"Delete\">\n</form>\n\n</div>\n";
        VisitorServlet.endPart = "</div>\n</body>\n</html>";
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final PrintWriter out = response.getWriter();
        try {
            out.println(VisitorServlet.fixedPart);
            try {
                final String op = request.getParameter(VisitorServlet.OP);
                if (op != null && op.length() != 0) {
                    if (!op.equals("read")) {
                        if (op.equals("create")) {
                            this.create(request.getParameter("create_name"), request.getParameter("create_comment"), null);
                        }
                        else if (op.equals("update")) {
                            this.update(request.getParameter("id_to_update"), request.getParameter("new_name"), request.getParameter("new_comment"), null);
                        }
                        else if (op.equals("delete")) {
                            this.delete(request.getParameter("id_to_delete"), null);
                        }
                    }
                }
                this.read(out);
            }
            catch (Throwable t) {
                out.println("Exception: " + t.getLocalizedMessage());
                t.printStackTrace(out);
            }
            finally {
                out.println(VisitorServlet.endPart);
            }
        }
        finally {
            out.close();
        }
    }
    
    public int create(final String name, final String comment, final PrintWriter out) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory(JTA_PU_NAME);
    	EntityManager em = emf.createEntityManager();
    	
    	try {
    		em.getTransaction().begin();
    		em.persist((Object)new Visitor(name, comment, visitorDate(out)));
    		this.status("added visitor \"" + name + "\"", out);
    		em.getTransaction().commit();
    		em.flush();
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	} finally {
            if (em != null && em.isOpen()) {
                em.close();
            }            
            if (emf != null) {
                emf.close();
            }
        }
    	return 1;
    }
    
    public int createOld(final String name, final String comment, final PrintWriter out) {
        EntityManager em = null;
        em = this.emf_.createEntityManager();
        UserTransaction xact = null;
        try {
            xact = (UserTransaction)new InitialContext().lookup("javax.transaction.UserTransaction");
            xact.begin();
            em.persist((Object)new Visitor(name, comment, new Date()));
            this.status("added visitor \"" + name + "\"", out);
            xact.commit();
            this.status("Transaction committed successfully.", out);
        }
        catch (Throwable t) {
            this.status("Transaction failed: " + t.getLocalizedMessage(), out);
            if (xact != null) {
                try {
                    try {
						xact.rollback();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                catch (SystemException ex) {}
            }
            throw new RuntimeException(t);
        }
        finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return 1;
    }
    
    public int read(final PrintWriter out) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory(JTA_PU_NAME);
        EntityManager em = null;
        em = emf.createEntityManager();
        int count = 0;
        SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        try {
            final javax.persistence.Query queryVisitors = em.createQuery("SELECT OBJECT(v) FROM Visitor v");
            @SuppressWarnings("unchecked")
			final List<Visitor> visitors = (List<Visitor>)queryVisitors.getResultList();
            count = visitors.size();
            out.println("<div id=\"list\">\n");
            out.println("<h2>Recent Visitors</h2>\n");
            out.println("<table>\n");
            out.println("<thead>\n");
            out.println("<tr>\n");
            out.println("<th>ID</th>\n");
            out.println("<th>Name</th>\n");
            out.println("<th>Visit Date</th>\n");
            out.println("<th>Comment</th>\n");
            out.println("</tr>\n");
            out.println("</thead>\n");
            out.println("<tbody>\n");
            for (final Visitor visitor : visitors) {
                out.println("<tr>\n");
                out.println("<td>" + visitor.getId() + "</td>\n");
                out.println("<td>" + visitor.getName() + "</td>\n");
                if(null != visitor.getDateVisited()) {
                	out.println("<td>" + sdf1.format(visitor.getDateVisited()) + "</td>\n");
                } else {
                	out.println("<td>" + " " + "</td>\n");
                }              
                out.println("<td>" + visitor.getAllComments() + "</td>\n");
                out.println("</tr>\n");
            }
            out.println("</tbody>\n");
            out.println("</table>\n");
            out.println("<p>Number of Visitors <span class=\"amount\">" + count + "</span></p>\n");
            out.println("\n");
            out.println("<form method=\"post\">\n");
            out.println("<div class=\"ctrlrow\">\n");
            out.println("<input type=\"submit\" value=\"Refresh\">\n");
            out.println("</div>\n");
            out.println("<input type=\"hidden\" name=\"operation\" value=\"read\">\n");
            out.println("</form>\n");
            out.println("\n");
            out.println("</div>\n");
            this.status("Visitors read successfully. count: " + count, null);
        }
        catch (Throwable t) {
            this.status("Visitors read failed: " + t.getLocalizedMessage(), null);
            throw new RuntimeException(t);
        }
        finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
        return count;
    }
    
    public void update(final String id, final String newName, final String newComment, final PrintWriter out) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory(JTA_PU_NAME);
        EntityManager em = emf.createEntityManager();
        this.status("Starting update of Visitor with id = " + id + " to " + newName, out);
        try {
        	em.getTransaction().begin();
            final javax.persistence.Query q = em.createQuery("UPDATE Visitor v set v.name = :newName, v.allComments = :newComment WHERE v.id = :id");
            q.setParameter("newName", (Object)newName);
            q.setParameter("newComment", (Object)newComment);
            q.setParameter("id", (Object)Long.valueOf(id));
            final int updated = q.executeUpdate();
            em.getTransaction().commit();
    		em.flush();
            this.status("Updated " + updated + " Visitor with id = " + id + " to " + newName, out);
            this.status("Transaction committed successfully.", out);
        }
        catch(Exception ex){
    		ex.printStackTrace();
    	} finally {
            if (em != null && em.isOpen()) {
                em.close();
            }            
            if (emf != null) {
                emf.close();
            }
        }
    }
    
    public void updateOld(final String id, final String newName, final String newComment, final PrintWriter out) {
        EntityManager em = null;
        em = this.emf_.createEntityManager();
        this.status("Starting update of Visitor with id = " + id + " to " + newName, out);
        UserTransaction xact = null;
        try {
            xact = (UserTransaction)new InitialContext().lookup("javax.transaction.UserTransaction");
            xact.begin();
            final javax.persistence.Query q = em.createQuery("UPDATE Visitor v set v.name = :newName, v.allComments = :newComment WHERE v.id = :id");
            q.setParameter("newName", (Object)newName);
            q.setParameter("newComment", (Object)newComment);
            q.setParameter("id", (Object)Long.valueOf(id));
            final int updated = q.executeUpdate();
            xact.commit();
            this.status("Updated " + updated + " Visitor with id = " + id + " to " + newName, out);
            this.status("Transaction committed successfully.", out);
        }
        catch (Throwable t) {
            this.status("Transaction failed: " + t.getLocalizedMessage(), out);
            if (xact != null) {
                try {
                    try {
						xact.rollback();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                catch (SystemException ex) {}
            }
            throw new RuntimeException(t);
        }
        finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    public int delete(final String id, final PrintWriter out) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory(JTA_PU_NAME);
    	EntityManager em = emf.createEntityManager();
    	this.status("Starting delete of Visitor with id = " + id, out);
    	int deleted = 0;
    	
    	try { 
    		em.getTransaction().begin();
    		final javax.persistence.Query q = em.createQuery("DELETE FROM Visitor v WHERE v.id = :id");
    		q.setParameter("id", (Object)Long.valueOf(id));
            deleted = q.executeUpdate();
            em.getTransaction().commit();
            em.flush();
            this.status("Deleted " + deleted + " Visitors with id = " + id, out);
            this.status("Transaction committed successfully.", out);
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	} finally {
            if (em != null && em.isOpen()) {
                em.close();
            } 
            
            if (emf != null) {
                emf.close();
            }
        }    	
        return deleted;
    }
    
    public int deleteOld(final String id, final PrintWriter out) {
        EntityManager em = null;
        em = this.emf_.createEntityManager();
        this.status("Starting delete of Visitor with id = " + id, out);
        int deleted = 0;
        UserTransaction xact = null;
        try {
            xact = (UserTransaction)new InitialContext().lookup("javax.transaction.UserTransaction");
            xact.begin();
            final javax.persistence.Query q = em.createQuery("DELETE FROM Visitor v WHERE v.id = :id");
            q.setParameter("id", (Object)Long.valueOf(id));
            deleted = q.executeUpdate();
            xact.commit();
            this.status("Deleted " + deleted + " Visitors with id = " + id, out);
            this.status("Transaction committed successfully.", out);
        }
        catch (Throwable t) {
            this.status("Transaction failed: " + t.getLocalizedMessage(), out);
            if (xact != null) {
                try {
                    try {
						xact.rollback();
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
                catch (SystemException ex) {}
            }
            throw new RuntimeException(t);
        }
        finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return deleted;
    }
    
	public Date visitorDate(final PrintWriter out) {
    	EntityManagerFactory emf = Persistence.createEntityManagerFactory(JTA_PU_NAME);
        EntityManager em = null;
        em = emf.createEntityManager();
        Date d = null;
        try {
            final javax.persistence.Query queryVisitors = em.createNativeQuery("SELECT SYSDATE FROM DUAL");
            d = (Date)queryVisitors.getSingleResult();
        } catch(Exception ex) {
    		ex.printStackTrace();
    	} finally {
            if (em != null && em.isOpen()) {
                em.close();
            } 
            
            if (emf != null) {
                emf.close();
            }
        }    	
        return d;
    }
    
    public void status(final String msg, final PrintWriter out) {
        this.log(msg);
        if (out != null) {
            out.println("<h4>" + msg + "</h4>");
        }
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    public String getServletInfo() {
        return "Short description";
    }
    
}