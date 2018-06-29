
import java.rmi.*;
import java.io.*;

  public interface rmiClassLoader extends Remote {
    public byte[] loadRemoteClass(String name) throws RemoteException;
}