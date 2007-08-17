package org.galo.ws;

import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteEchoService extends Remote {

  public String echo(String in)throws RemoteException;
  public List echoList(List l) throws RemoteException;
  public Object echoObject(Object o) throws RemoteException;

}
	
