  import java.rmi.*;
  import java.rmi.server.*;
  import java.io.*; 

   public class rmiFileClient {

		rmiFile rmiLink = null;
		
		public rmiFileClient(String remotehost) {
		         try {
            System.setSecurityManager(new RMISecurityManager());
            String localhost = "localhost";            
            String server = "rmiFileServer";
            // change localhost to remote host in next line
            String name = "rmi://" + localhost + "/" + server;
            rmiLink = (rmiFile)Naming.lookup(name);
         }
         catch (Throwable e) {
            System.err.println("exception: " + e);
            System.exit(1);
         }         
		}
		
				
    public static void main(String args[])
      {
				String remotehost = "localhost";
				rmiFileClient hc = new rmiFileClient(remotehost);     
				System.out.println("rmiFileClient in main");
      }
         
   }