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

  RECIPES:
    #pull logs from sheeva
    rsync  -avz --progress root@miraplug001.dnsalias.net:/mirawatt/logs/CC2\*log .
    rsync  -avz --progress root@miraplug002.dnsalias.net:/mirawatt/logs/CC3\*log .
    rsync  -avz --progress root@192.168.5.247:/mirawatt/logs/CC\*log .
    ssh root@192.168.5.247 "tail -f /var/log/messages /mirawatt/wdog/wdog.log /mirawatt/wdog/capture.log /mirawatt/wdog/heartbeat.log"
    ssh root@192.168.5.247 "tail -f /mirawatt/logs/CC*log"

  TODO:
   + Remove all tabs from python sources: EVIL.
     NOTE: you can run python with -t (or -tt for fatal) detection of bad space/tab mixes
       wrote a script: fixtabs.sh: detects all tabs, makes .notab, ans .showtab files
       the .notab file has substituted tabs for 8-spaces
       the show tabs has subtitued tabs for '~~TABS~~' string
   +make randomly dying client, to test the watchdog functionality
   +test smtp notification (watchdog@mirawatt.com)
   +make summarize check arithmetic, copy to test directory
   +deploy sheeva:/mirawatt/wdog
   +find /dev/ -maxdepth 1 -name ttyUSB\* -type c
   -test watchAndRead
   -make summarize functional
   -summarize -> publish?
   +refactor iso8601 and datetime manips
   -refactor config and paths +(deployment)

   DEPLOYMENT:
   /mirawatt/wdog: scripts
   /mirawatt/logs: raw data logs
   crontab -l:
   * * * * *  /mirawatt/wdog/wdog.py -guard >/dev/null 2>&1
   /mirawatt/wdog/wdog.py heartbeat start
   /mirawatt/wdog/wdog.py capture start

Watchdog Upstream Source:
  use python script: http://www.mi-ange.net/blog/msg.php?id=67&lng=en
  The copy in this directory was obtained by:
   svn export http://svn.arluison.com/wdog/trunk/ wdog
This is the one I got:
svn log http://svn.arluison.com/wdog/trunk/|more
------------------------------------------------------------------------
r19 | guillaume | 2009-05-08 10:45:24 -0400 (Fri, 08 May 2009) | 2 lines
------------

    UDEV: persistent naming for /dev/ttyUSB???
Can I add custom rule to /etc/udev/rules.d/, like those in the system wide: /lib/udev/rules.d
root@debian:/lib/udev/rules.d# udevadm info -q path -n /dev/ttyUSB0
/devices/platform/orion-ehci.0/usb1/1-1/1-1:1.0/ttyUSB0/tty/ttyUSB0
root@debian:/lib/udev/rules.d# udevadm info -a -p /devices/platform/orion-ehci.0/usb1/1-1/1-1:1.0/ttyUSB0/tty/ttyUSB0
...

