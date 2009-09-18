Mirawatt (Sheeva) Pump

  Nomencalature: repace Sheeva by aproved name of (Hub|BrokerServeur de comms local)

 Software Architecture
  -Watchdog  (on sheeva): Robust assembly of daemons, ensures each is running (once)
    -heartbeat  : captures state of hub: identity (MAC,ip's), clock, versions, resources(Disk space), daemons
                  and publishes it to Central Server.
    -capture    : Establish Device connectivity (one for now), device discovery, selection, config
                  It's main fnction is to capture the data in a raw state.
    -summarize  : Process raw data capture logs
    -publish :
    -archive :

  -Coordinator (Central Web Server)
   -heartbeat monitor, triggers, or aggregation

  -Clients : Connect to summarized data state on central server.

  TODO:
   +make randomly dying client, to test the watchdog functionality
   +test smtp notification (watchdog@mirawatt.com)

Watchdog Upstream Source:
  use python script: http://www.mi-ange.net/blog/msg.php?id=67&lng=en
  The copy in this directory was obtained by:
   svn export http://svn.arluison.com/wdog/trunk/ wdog
This is the one I got:
svn log http://svn.arluison.com/wdog/trunk/|more
------------------------------------------------------------------------
r19 | guillaume | 2009-05-08 10:45:24 -0400 (Fri, 08 May 2009) | 2 lines
------------
  

