
import java.rmi.*;
import java.io.*;

  public interface rmiFile extends Remote {
    public String getRoot() throws RemoteException;
    public String[] getDirectory(String s) throws RemoteException;
    public String[] getListing(String s) throws RemoteException;
}