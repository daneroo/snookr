import MySQLdb
import sys
import time
from scalr import logInfo,logWarn,logError


# add main, usage
# create if necessary
# args --drop --start,... --mysqldb --update (select >current (+ offset))
# walk up the dates for day-month, or find some clever mysql dst expressions

def dropAndCreateTable(name):
    tablename="watt_%s" % name
    logInfo("Making table for %s (%s)" % (name,tablename))
    ddl = """CREATE TABLE %s (
stamp datetime NOT NULL default '1970-01-01 00:00:00',
watt int(11) NOT NULL default '0',
PRIMARY KEY %sByStamp (stamp)
);
""" % (tablename,tablename)
    cursor.execute ("DROP TABLE IF EXISTS %s" % tablename)
    cursor.execute(ddl)
    logInfo(" ddl executed for %s " % (tablename))
    #print ddl

def dropAndCreateTables():
    suffixes = ["tensec","minute","hour","day","month"]
    for suffix in suffixes:
        dropAndCreateTable(suffix);
    logInfo("Done creating Tables")

def fillTable(suffix,groupingWidth,rightPad):
    # example for watttensec - why we have rightPad
    # replace into watttensec select concat(left(stamp,18),'0') as g,avg(watt) from watt where stamp>'2008-09-15 11:00:00' group by g;
    
    # example : cursor.execute("replace into wattminute select left(stamp,16) as g,avg(watt) from watt group by g")
    tablename="watt_%s" % suffix
    now = time.time()
    cursor.execute("replace into %s select concat(left(stamp,%d),'%s') as g,avg(watt) from watt group by g" % (tablename,groupingWidth,rightPad))
    print "Table %s now has %d entries (%6.2fs.)" % (tablename,getScalar("select count(*) from %s" % tablename),time.time()-now)

    
def fillTables():
    fillTable("tensec",18,'0')
    fillTable("minute",16,':00')
    fillTable("hour",13,':00:00')
    fillTable("day",10,' 00:00:00')
    fillTable("month",7,'-01 00:00:00')

def getScalar(sql):
    cursor.execute(sql)
    row = cursor.fetchone()
    return row[0]

def startOfDay(secs,offsetInDays):
    secsTuple = time.localtime(secs)
    startOfDayWithOffsetTuple = (secsTuple[0],secsTuple[1],secsTuple[2]+offsetInDays,0,0,0,0,0,-1)
    startOfDayWithOffsetSecs  = time.mktime(startOfDayWithOffsetTuple)
    return startOfDayWithOffsetSecs
    
# This is a GENERATOR not a function
def walkBackDaysGenerator(startSecs,stopSecs): # see generators docs
    # boundary Conditions ?
    # both directions ?
    currentSecs = startOfDay(startSecs,0)
    while True:
        if (currentSecs<=stopSecs): # < or <= ???????
            return  # termination of generator
        yield currentSecs
        currentSecs = startOfDay(currentSecs,-1)
    
        
if __name__ == "__main__":

## shall I get rid of Month ?
##     So Here is the flow
##       reference table is watt
##       find max(stamp) from watt --> that will be our startTime (going backwards)
##                     --bad name use stop for end of watt
##     for all days, including (current partial day)
##     for each scope: tensec,minute,hour,day
##         filltable(..,start,stop) will do appropriate rounding
##          TO BE CONTINUED..
    

    #for daySecs in walkBackDaysGenerator(startOfDay(time.time(),0),startOfDay(time.time(),-2)):
    for daySecs in walkBackDaysGenerator(time.time(),time.time()-(86400*2)):
        tomorrow = startOfDay(daySecs,1)
        print "  GMT:[%s GMT] Local:[%s]   lasts %.1f hours" % (
            time.strftime("%Y-%m-%d %H:%M:%S",time.gmtime(daySecs)),
            time.strftime("%Y-%m-%d %H:%M:%S %Z day:%j",time.localtime(daySecs)),
            (tomorrow-daySecs)/3600
            )

    print "Done"
    sys.exit(0)

    conn = MySQLdb.connect (host="127.0.0.1",user="aviso",passwd="",db="ted")
    cursor = conn.cursor ()

    #dropAndCreateTables()
    fillTables()

    cursor.close ()
    conn.close ()



