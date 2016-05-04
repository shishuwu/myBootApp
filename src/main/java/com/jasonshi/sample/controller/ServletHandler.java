package com.jasonshi.sample.controller;

/*
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * CopyrightVersion 1.1, 1.2_beta
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The default RMI socket factory contains several "fallback"
 * mechanisms which enable an RMI client to communicate with a remote
 * server.  When an RMI client initiates contact with a remote server,
 * it attempts to establish a connection using each of the following
 * protocols in turn, until one succeeds:
 *
 * 1. Direct TCP connection.
 * 2. Direct HTTP connection.
 * 3. Proxy connection (SOCKS or HTTP).
 * 4. Connection on port 80 over HTTP to a CGI script.
 * 5. Proxy HTTP connection to CGI script on port 80.
 *
 * The RMI ServletHandler can be used as replacement for the
 * java-rmi.cgi script that comes with the Java Development Kit (and
 * is invoked in protocols 4 and 5 above).  The java-rmi.cgi script
 * and the ServletHandler both function as proxy applications that
 * forward remote calls embedded in HTTP to local RMI servers which
 * service these calls.  The RMI ServletHandler enables RMI to tunnel
 * remote method calls over HTTP more efficiently than the existing
 * java-rmi.cgi script.  The ServletHandler is only loaded once from
 * the servlet administration utility.  The script, java-rmi.cgi, is
 * executed once every remote call.
 *
 * The ServletHandler class contains methods for executing as a Java
 * servlet extension.  Because the RMI protocol only makes use of the
 * HTTP post command, the ServletHandler only supports the
 * <code>doPost</code> <code>HttpServlet</code> method.  The
 * <code>doPost</code> method of this class interprets a servlet
 * request's query string as a command of the form
 * "<command>=<parameters>".  These commands are represented by the
 * abstract interface, <code>RMICommandHandler</code>.  Once the
 * <code>doPost</code> method has parsed the requested command, it
 * calls the execute method on one of several command handlers in the
 * <code>commands</code> array.
 *
 * The command that actually proxies remote calls is the
 * <code>ServletForwardCommand</code>.  When the execute method is
 * invoked on the ServletForwardCommand, the command will open a
 * connection on a local port specified by its <code>param</code>
 * parameter and will proceed to write the body of the relevant post
 * request into this connection.  It is assumed that an RMI server
 * (e.g. SampleRMIServer) is listening on the local port, "param."
 * The "forward" command will then read the RMI server's response and
 * send this information back to the RMI client as the body of the
 * response to the HTTP post method.
 *
 * Because the ServletHandler uses a local socket to proxy remote
 * calls, the servlet has the ability to forward remote calls to local
 * RMI objects that reside in the ServletVM or outside of it.
 *
 * Servlet documentation may be found at the following location:
 *
 * http://jserv.javasoft.com/products/java-server/documentation/
 *        webserver1.0.2/apidoc/Package-javax.servlet.http.html
 */
public class ServletHandler extends HttpServlet
{
	private static final long serialVersionUID = 6327652016112089892L;

	/**
     * RMICommandHandler is the abstraction for an object that handles
     * a particular supported command (for example the "forward"
     * command "forwards" call information to a remote server on the
     * local machine).
     *
     * The command handler is only used by the ServletHandler so the
     * interface is protected.
     */
    protected interface RMICommandHandler {

	/**
	 * Return the string form of the command to be recognized in the
	 * query string.
	 */
	public String getName();

	/**
	 * Execute the command with the given string as parameter.
	 */
	public void execute(HttpServletRequest req, HttpServletResponse res,
			    String param)
	    throws ServletClientException, ServletServerException, IOException;
    }

    /**
     * List of handlers for supported commands. A new command will be
     * created for every service request
     */
    private static RMICommandHandler commands[] =
	new RMICommandHandler [] {
            new ServletForwardCommand(),
	    new ServletGethostnameCommand(),
	    new ServletPingCommand(),
	    new ServletTryHostnameCommand()
	};

    /* construct table mapping command strings to handlers */
    private static Hashtable commandLookup;
    static {
	commandLookup = new Hashtable();
	for (int i = 0; i < commands.length; ++ i)
	    commandLookup.put(commands[i].getName(), commands[i]);
    }

   /**
    * Once loaded, Java Servlets continue to run until they are
    * unloaded or the webserver is stopped.  This example takes
    * advantage of the extended Servlet life-cycle and runs a remote
    * object in the Servlet VM.
    *
    * To initialize this remote object the Servlet Administrator
    * should specify a set of parameters which will be used to
    * download and install an initial remote server (see readme.txt).
    *
    * If configuration parameters are valid (not blank), the
    * servlet will attempt to load and start a remote object and a
    * registry in which the object may be bound.
    *
    * @param config Standard configuration object for an http servlet.
    *
    * @exception ServletException Calling
    *           <code>super.init(config)</code> may cause a servlet
    *           exception to be thrown.
    */
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	try {

	    /* RMI requires that a local security manager be
             * responsible for the method invocations from remote
             * clients - we need to make sure a security manager is
             * installed.
	     */
	    //if (System.getSecurityManager() == null) {
            //    System.setSecurityManager(new RMISecurityManager());
	    //}

	    System.out.println("RMI Servlet Handler loaded sucessfully.");

	} catch (Exception e) {
	    System.err.println("Exception thrown in RMI ServletHandler: " +
			       e.getMessage());
	    e.printStackTrace();
	}
    }

    /* NOTE: If you are using JDK1.2Beta4 or later, it is recommended
     * that you provide your servlet with a destroy method that will
     * unexport any remote objects that your servlet ever exports.  As
     * mentioned in the readme file for this example, it is not
     * possible to unexport remote objects in JDK1.1.x; there is no
     * method in the RMI 1.1 public API that will perform this task.
     * To restart remote objects in the servlet VM, you will have to
     * restart your webserver.  In JDK1.2x, the methods to unexport a
     * remote object are as follows:
     *
     * java.rmi.activation.Activatable.
     *                 unexportObject(Remote obj, boolean force)
     * java.rmi.server.UnicastRemoteObject.
     *                 unexportObject(Remote obj, boolean force)
     */

    /**
     * Execute the command given in the servlet request query string.
     * The string before the first '=' in the queryString is
     * interpreted as the command name, and the string after the first
     * '=' is the parameters to the command.
     *
     * @param req  HTTP servlet request, contains incoming command and
     *             arguments
     * @param res  HTTP servlet response
     * @exception  ServletException and IOException when invoking
     *             methods of <code>req<code> or <code>res<code>.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	try {

	    // Command and parameter for this POST request.
	    String queryString = req.getQueryString();
	    String command, param;
	    int delim = queryString.indexOf("=");
	    if (delim == -1) {
		command = queryString;
		param = "";
	    } else {
		command = queryString.substring(0, delim);
		param = queryString.substring(delim + 1);
	    }

	    System.out.println("command: " + command);
	    System.out.println("param: " + param);

	    // lookup command to execute on the client's behalf
	    RMICommandHandler handler =
		(RMICommandHandler) commandLookup.get(command);

	    // execute the command
	    if (handler != null)
		try {
		    handler.execute(req, res, param);
		} catch (ServletClientException e) {
		    returnClientError(res, "client error: " +
				      e.getMessage());
		    e.printStackTrace();
		} catch (ServletServerException e) {
		    returnServerError(res, "internal server error: " +
				      e.getMessage());
		    e.printStackTrace();
		}
	    else
		returnClientError(res, "invalid command: " +
				  command);
	} catch (Exception e) {
	    returnServerError(res, "internal error: " +
			      e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * Provide more intelligible errors for methods that are likely to
     * be called.  Let unsupported HTTP "do*" methods result in an
     * error generated by the super class.
     *
     * @param req  http Servlet request, contains incoming command and
     *             arguments
     *
     * @param res  http Servlet response
     *
     * @exception  ServletException and IOException when invoking
     *             methods of <code>req<code> or <code>res<code>.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	returnClientError(res,
			  "GET Operation not supported: " +
			  "Can only forward POST requests.");
    }

    public void doPut(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {

	returnClientError(res,
			  "PUT Operation not supported: " +
			  "Can only forward POST requests.");
    }

    public String getServletInfo() {
	return "RMI Call Forwarding Servlet Servlet.<br>\n";
    }

    /**
     * Return an HTML error message indicating there was error in
     * the client's request.
     *
     * @param res Servlet response object through which <code>message</code>
     *            will be written to the client which invoked one of
     *            this servlet's methods.
     * @param message Error message to be written to client.
     */
    private static void returnClientError(HttpServletResponse res,
					  String message)
	throws IOException {

    	System.out.println(HttpServletResponse.SC_BAD_REQUEST +
    		      "Java RMI Client Error" +
    		      message);
    	
        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /**
     * Return an HTML error message indicating an internal error
     * occurred here on the server.
     *
     * @param res Servlet response object through which <code>message</code>
     *            will be written to the servlet client.
     * @param message Error message to be written to servlet client.
     */
    private static void returnServerError(HttpServletResponse res,
					  String message)
	throws IOException {

    	System.out.println(HttpServletResponse.SC_INTERNAL_SERVER_ERROR +
  		      "Java RMI Server Error: " +
  		      message);
    	
        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

    /*
     * The ServletHandler class is the only object that needs to access the
     * CommandHandler subclasses, so we write the commands internal to the
     * servlet handler.
     */

    /**
     * Class that has an execute command to forward request body to
     * local port on the server and send server reponse back to client.
     */
    protected static class ServletForwardCommand implements RMICommandHandler {

	public String getName() {
	    return "forward";
	}

	/**
	 * Execute the forward command.  Forwards data from incoming servlet
	 * request to a port on the local machine.  Presumably, an RMI server
	 * will be reading the data that this method sends.
	 *
	 * @param req   The servlet request.
	 * @param res   The servlet response.
	 * @param param Port to which data will be sent.
	 */
	public void execute(HttpServletRequest req, HttpServletResponse res,
			    String param)
	    throws ServletClientException, ServletServerException, IOException {

	    int port;
	    try {
		port = Integer.parseInt(param);
	    } catch (NumberFormatException e) {
		throw new ServletClientException("invalid port number: " +
						 param);
	    }
	    if (port <= 0 || port > 0xFFFF)
		throw new ServletClientException("invalid port: " + port);
	    if (port < 1024)
		throw new ServletClientException("permission denied for port: "
						 + port);

	    byte buffer[];
	    Socket socket;
	    try {
		socket = new Socket(InetAddress.getLocalHost(), port);
	    } catch (IOException e) {
		throw new ServletServerException("could not connect to " +
						 "local port");
	    }

	    // read client's request body
	    DataInputStream clientIn =
		new DataInputStream(req.getInputStream());
	    buffer = new byte[req.getContentLength()];
	    try {
		clientIn.readFully(buffer);
	    } catch (EOFException e) {
		throw new ServletClientException("unexpected EOF " +
						 "reading request body");
	    } catch (IOException e) {
		throw new ServletClientException("error reading request" +
						 " body");
	    }

	    DataOutputStream socketOut = null;
	    // send to local server in HTTP
	    try {
		socketOut =
		    new DataOutputStream(socket.getOutputStream());
		socketOut.writeBytes("POST / HTTP/1.0\r\n");
		socketOut.writeBytes("Content-length: " +
				     req.getContentLength() + "\r\n\r\n");
		socketOut.write(buffer);
		socketOut.flush();
	    } catch (IOException e) {
		throw new ServletServerException("error writing to server");
	    }

	    // read response from local server
	    DataInputStream socketIn;
	    try {
		socketIn = new DataInputStream(socket.getInputStream());
	    } catch (IOException e) {
		throw new ServletServerException("error reading from " +
						 "server");
	    }
	    String key = "Content-length:".toLowerCase();
	    boolean contentLengthFound = false;
	    String line;
	    int responseContentLength = -1;
	    do {
		try {
		    line = socketIn.readLine();
		} catch (IOException e) {
		    throw new ServletServerException("error reading from server");
		}
		if (line == null)
		    throw new ServletServerException(
		        "unexpected EOF reading server response");

		if (line.toLowerCase().startsWith(key)) {
		    if (contentLengthFound)
			; // what would we want to do in this case??
		    responseContentLength =
			Integer.parseInt(line.substring(key.length()).trim());
		    contentLengthFound = true;
		}
	    } while ((line.length() != 0) &&
		     (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

	    if (!contentLengthFound || responseContentLength < 0)
		throw new ServletServerException(
		    "missing or invalid content length in server response");
	    buffer = new byte[responseContentLength];
	    try {
		socketIn.readFully(buffer);
	    } catch (EOFException e) {
		throw new ServletServerException(
		    "unexpected EOF reading server response");
	    } catch (IOException e) {
		throw new ServletServerException("error reading from server");
	    }

	    System.out.println("response: write");
	    
	    // send local server response back to servlet client
	    res.setStatus(HttpServletResponse.SC_OK);
	    res.setContentType("application/octet-stream");
	    res.setContentLength(buffer.length);

	    try {
		OutputStream out = res.getOutputStream();
		out.write(buffer);
		out.flush();
	    } catch (IOException e) {
		throw new ServletServerException("error writing response");
	    } finally {
		socketOut.close();
		socketIn.close();
	    }
	    
	    System.out.println("response: closed");
	}
    }

    /**
     * Class that has an execute method to return the host name of the
     * server as the response body.
     */
    protected static class ServletGethostnameCommand
	implements RMICommandHandler
    {

	public String getName() {
	    return "gethostname";
	}

	public void execute(HttpServletRequest req, HttpServletResponse res,
			    String param)
	    throws ServletClientException, ServletServerException, IOException {

	    byte[] getHostStringBytes = req.getServerName().getBytes();

	    res.setStatus(HttpServletResponse.SC_OK);
	    res.setContentType("application/octet-stream");
	    res.setContentLength(getHostStringBytes.length);

	    OutputStream out = res.getOutputStream();
	    out.write(getHostStringBytes);
	    out.flush();
	}
    }

    /**
     * Class that has an execute method to return an OK status to
     * indicate that connection was successful.
     */
    protected static class ServletPingCommand implements RMICommandHandler {

	public String getName() {
	    return "ping";
	}

	public void execute(HttpServletRequest req, HttpServletResponse res,
			    String param)
	    throws ServletClientException, ServletServerException, IOException {

	    res.setStatus(HttpServletResponse.SC_OK);
	    res.setContentType("application/octet-stream");
	    res.setContentLength(0);
	}
    }

    /**
     * Class that has an execute method to return a human readable
     * message describing which host name is available to local Java
     * VMs.
     */
    protected static class ServletTryHostnameCommand
	implements RMICommandHandler
    {

	public String getName() {
	    return "hostname";
	}


	public void execute(HttpServletRequest req, HttpServletResponse res,
			    String param)
	    throws ServletClientException, ServletServerException, IOException {

	    PrintWriter pw = res.getWriter();

	    pw.println("");
	    pw.println("<HTML>" +
		       "<HEAD><TITLE>Java RMI Server Hostname Info" +
		       "</TITLE></HEAD>" +
		       "<BODY>");
	    pw.println("<H1>Java RMI Server Hostname Info</H1>");
	    pw.println("<H2>Local host name available to Java VM:</H2>");
	    pw.print("<P>InetAddress.getLocalHost().getHostName()");
	    try {
		String localHostName = InetAddress.getLocalHost().getHostName();

		pw.println(" = " + localHostName);
	    } catch (UnknownHostException e) {
		pw.println(" threw java.net.UnknownHostException");
	    }

	    pw.println("<H2>Server host information obtained through Servlet " +
		       "interface from HTTP server:</H2>");
	    pw.println("<P>SERVER_NAME = " + req.getServerName());
	    pw.println("<P>SERVER_PORT = " + req.getServerPort());
	    pw.println("</BODY></HTML>");
	}
    }

    /**
     * ServletClientException is thrown when an error is detected
     * in a client's request.
     */
    protected static class ServletClientException extends Exception {

	public ServletClientException(String s) {
	    super(s);
	}
    }

    /**
     * ServletServerException is thrown when an error occurs here on the server.
     */
    protected static class ServletServerException extends Exception {

	public ServletServerException(String s) {
	    super(s);
	}
    }
}