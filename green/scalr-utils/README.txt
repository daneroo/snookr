2008-11-14 Inventory and new plan.

-=-= Objective (short):
  -Python daemon
   -reads ted service every second and inserts into new database: wattrical
   -uses 'now" as time stamp, wattrical db sores stamps in GMT

  -PHP converts wattrical.watt db into plist for wattrical iPhone App.

  -Architecture neutral sqlite code: consolidate pumpsqlite.

-=-= Objective (mid):
  -Python daemon summarizes 'second' data into:
     tensec,minute,tenminute,halfhour,hour,day,month summary tables
     -possibly have accumulation table for mainting averaging state
   compare live tedservice feed to ted sqlite archive

  -Alternative feed xml format: GData
  -GAppEngine repository (mirrors local daemon data (through synch/push))
  -Groovy repository (ibid)
  -GoogleDocs Spreadsheet repository

  -Objective-C binding for GData on iPhone
    
-=-= Inventory:

 Incremental.py:
     sqlite.teddb > mysql text out.
          add params --days X, --hours Y, etc
     only works on linux: make Architexture neutral

Python GDATA:
  DON'T FORGET 
  export PYTHONPATH=~/python/lib/python/;
  showGDATA.py: List SpreadSheet, WorkSheet, Data
    python showGDATA.py --user=daniel.lauzon --pw=BLABLA
   or
    python showGDATA.py --user=daniel.lauzon --pw=BLABLA --key pEzZl0NxQ-0gFzvkp3oJsew --sheet od6

  append

Java
 WinTime.java:
    Time Experiments to convert TED sqlite time format to java



















