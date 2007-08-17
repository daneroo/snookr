package org.galo.ws;

import org.galo.model.Image;
import java.util.List;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteGaloService extends Remote {

    public void saveImage(Image ima) throws RemoteException;
    public void saveImageList(List li) throws RemoteException;

}
	
