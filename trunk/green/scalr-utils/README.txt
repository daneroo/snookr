2008-11-14 Inventory and new plan.

-=-= Objective (short):
  -Python daemon
   Historical data (billing) into watt_day
   Flow:  when and who
     to overwrite, --> watt 
     to compare,     tednative,tedlive(,aztech) , watt
     to summarize

   Names: Incremental -> ReadTEDNative.py
          unchanged   -> ReadTEDService.py
          hierTed     -> Summarize.py

   DB tables: (dbname ted->wattrical)
         tedlive -> ted_service
                 -> ted_native ? NOT: duplicating sqlite dumps, but might help compare
                 -> aztech_service
         current_state (all feed scopes, with averages, of projections ??)
         watt -> (stamp,watt)  ? stampGMT: NO
           Decided not to use watt_0010,..watt_3600
         watt_tensec
         watt_minute
         watt_hour
         watt_day 
         watt_month

   + Fix Incremental for Better GMT timestamps
   -?    and Rename to PumpTedNative

   -hierTed.py: (rename ?) to

   -rename watt to wattEDT to keep old data.
      RENAME TABLE watt TO wattEDT

   -recreate watt and fill with new Incremental
   -somehow compare (perhaps as we refill tensec,minute,hour)
        wattEDT to watt (GMT)
        compare with tedLive

   -refill <=hour with gmt average

   -convert tedLive.php to use watt...(GMT)<=hour
   -automate filling watt(GMT) and hierarchical tables from tedlive and tedraw.

   -? Rename ted database wattrical

   + testTedDates: to validate tedStamps
        It seems that day/month tables are not populated exactly at midnight localtime: 
	Always 86400 seconds between timestamps
        in EST such as 2008-11-18 table is populated before 00:22 !

	Other oddities of rdu_second_data:
          duplicates <<<jitter
          many skips >> 2 minutes, even more >>1 minute
          most >> 2minutes are due to windows clock correction and restarts

   -Excpetion Proof: ReadTEDService: output to log
Traceback (most recent call last):
  File "ReadTEDService.py", line 88, in ?
    (stamp, watts,volts) = getTimeWattsAndVoltsFromTedService()
  File "ReadTEDService.py", line 28, in getTimeWattsAndVoltsFromTedService
    usock = urllib.urlopen(TED_DASHBOARDDATA_URL)
IOError: [Errno socket error] (-2, 'Name or service not known')


       
     
   _ wattrical database tables (GMT)
     -owner ? multiple accounts ? only on gae or morph
     tedservice  (<- ted.tedlive )
     tednative ( <- from pump  (remname Incremental)

     watt
     watt0010
     watt0060
     watt1800
     watt3600
     wattday
     wattmonth

   +reads ted service every second and inserts into new table: ted.tedlive


   -uses 'now" as time stamp, ted db sores stamps in GMT

  -PHP converts wattrical.watt db into plist for wattrical iPhone App.

  +Architecture neutral sqlite code: consolidate pumpsqlite.
  +module scalr for code re-use

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
Bash cron:
  doMirror:
    copy cifs mounted ted directory's TED.db sqlite db
      requires:  mount -t cifs //aria/ted /ted
    compress and archive as per usual.
    installed as cron for daniel:
00 * * * *    cd /archive/mirror/ted; ./doMirror >>tedmirror.log 2>&1

Python
 Incremental.py: will be renamed as Pump
     use --all or --secs,--minutes,--hours,--days
     sqlite.teddb > mysql text out.
          add params --days X, --hours Y, etc
     only works on linux: make Architexture neutral

PumpSqliteToMysql.py
   Connect to name sqlite db, 
   select all from ted. print as Mysql ready text to stdout.
       REPLACE INTO....

ReadSqlite.py
   Predates PumpXXX, and simply prints out data in column form (limit 1000)

ReadTEDService.py
   Reads and prints ted: watt, volt : and current client-time
   added --duration and --forever params
   added a precise 1 second timing loop

hierTed.py:
  calculates summary tables: wattday,watthour,wattminute,watttensec.


Python GDATA:
  DON'T FORGET 
  export PYTHONPATH=~/python/lib/python/;
  showGDATA.py: List SpreadSheet, WorkSheet, Data
    python showGDATA.py --user daniel.lauzon --pw BLABLA
   or (for GDATA-TED Spreadseet/Worksheet-1
    python showGDATA.py --user daniel.lauzon --pw BLABLA --key pEzZl0NxQ-0gFzvkp3oJsew --sheet od6

  appendToGDATA.py: insert watt data into GDATA-TED Spreadsheet
    python appendToGDATA.py --user=daniel.lauzon --pw=BLABLA --watt=125
    python appendToGDATA.py --user daniel.lauzon --pw BLABLA --watt 126 --stamp '2008-11-16 23:34:55'

Java
 WinTime.java:
    Time Experiments to convert TED sqlite time format to java



















