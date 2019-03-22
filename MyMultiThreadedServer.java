import java.net.*;
import java.io.*;
import java.util.*;

/* MyClient - Demo of client / server network communication
	by: Michael Floeser
*/

public class MyMultiThreadedServer{
   private String clientMsg;
   private String oldMsg;
   private Vector<Thread> threads = new Vector<Thread>();
   private Vector<PrintWriter> writers = new Vector<PrintWriter>();

   public static void main(String [] args) {
      new MyMultiThreadedServer();
   }
	
   public MyMultiThreadedServer()
   {
      ServerSocket ss = null;
   
      try {
         System.out.println("getLocalHost: "+InetAddress.getLocalHost() );
         System.out.println("getByName:    "+InetAddress.getByName("localhost") );
      
         ss = new ServerSocket(16789);
         Socket cs = null;
         while(true){ // run forever once up
            cs = ss.accept(); // wait for connection
            ThreadServer ths = new ThreadServer( cs );
            threads.add(ths);
            ths.start();
            System.out.println("New client connecting... Current active connections: " + threads.size());
         }
      }
      catch( BindException be ) {
         System.out.println("Server already running on this computer, stopping.");
      }
      catch( IOException ioe ) {
         System.out.println("IO Error");
         ioe.printStackTrace();
      }
   
   } // end main
	
   class ThreadServer extends Thread {
      Socket cs;
      BufferedReader br;
      PrintWriter opw;
      String name;
      boolean keepGoing = true;
   
      public ThreadServer( Socket cs ) {
         this.cs = cs;
         name = "";
      }
   	
      public void run() {
         
         try {
            br = new BufferedReader(
               	new InputStreamReader( 
               		cs.getInputStream()));
            opw = new PrintWriter(
               	new OutputStreamWriter(
               		cs.getOutputStream()));
            writers.add(opw);
           
            opw.println("Please enter your name: ");
            opw.flush();
           
            try {
               while (name.equals("")) {
                  name = br.readLine();
               }
            } catch (SocketException se) {
               try {
                  threads.remove(this);
                  writers.remove(opw);
                  send(name + " disconnected");
                  br.close();
                  opw.close();
                  cs.close();
                  keepGoing = false;
               } catch (IOException e) {
               }
            }
            
            if (!name.equals("")) {
               System.out.println("Client with name '" + name + "' connected.");
            } else {
               send(name + " disconnected");
            }
           
            opw.println("Welcome to the server " + name + "!" + "\n Close chat program or type 'quit' to exit\n");
            opw.flush();
           
            while (keepGoing && !cs.isClosed()) {
               synchronized(this) {
                  try {		
                     clientMsg = br.readLine();					// from client
                     if (clientMsg == null) {
                        keepGoing = false;
                     } else {
                        oldMsg = clientMsg;
                        System.out.println(name + ": " + clientMsg + " (sent to " + threads.size() + " clients)");
                     
                        send(clientMsg);
                     }
                  } catch (SocketException se) {
                     System.out.println(name + " disconnected");
                     send(name + " disconnected");
                     try {
                        threads.remove(this);
                        writers.remove(opw);
                        br.close();
                        opw.close();
                        cs.close();
                        keepGoing = false;
                        return;
                     } catch (IOException e) {
                     }
                  }      
               }                        			
            }
         }
         catch( IOException e ) { 
            System.out.println(name + " Disconnected"); 
            send(name + " disconnected");
            e.printStackTrace();
            try {
               threads.remove(this);
               writers.remove(opw);
               br.close();
               opw.close();
               cs.close();
               keepGoing = false;
               return;
            } catch (IOException ioe) {
            }
         }
         try {
            System.out.println(name + " Disconnected"); 
            send(name + " disconnected");
            threads.remove(this);
            writers.remove(opw);
            br.close();
            opw.close();
            cs.close();
            keepGoing = false;
            return;
         } catch (IOException e) {
         }
      } // end run
      
      
      public void send(String _msg) {
         for (PrintWriter opw : writers) {
            if (_msg != null) {
               opw.println(name + ": " + clientMsg); // send message to every client
               opw.flush();
            }
         }
      }
   } // end class ThreadServer 
} // end MyMultiThreadedServer