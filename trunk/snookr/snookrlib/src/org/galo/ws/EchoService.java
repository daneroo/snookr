package org.galo.ws;
import java.util.List;

public interface EchoService {

  public String echo(String in);
  public List echoList(List l);
  public Object echoObject(Object o);

}
	
