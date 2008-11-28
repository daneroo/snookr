import MySQLdb
import sys
import time
import math
from scalr import logInfo,logWarn,logError


# args --drop --start,... --mysqldb --update (select >current (+ offset))
##     So Here is the flow
##       reference table is watt
##       find max(stamp) from watt --> that will be our 'latestSecs'
##         our earliestSecs, will be either max(stamp) from watttensec,...
##         or a passed param --start, --hours 2, --days 2

##        Now for each day (possibly partial) in [earliest,latest)
##           perform averageing for each scope in order
##              tensec, minute, (tenminute), hour, day, (week, month)
##           Where only stop is adjusted for each scope (inside fillTableXXX)
##             start is not adjusted to allow a value for non dayStarting data Table
##                        fillTable(tensec,start,stop)    
##                        fillTable(minutes,start,stop)    
##                        fillTable(hours,start,stop)    
##                        filltable(day,start,stop)
## As in:
##  Summarizing [ 2008-10-29 23:28:37 EDT ,  2008-11-03 22:28:37 EST ) (~5.0 days)
##  partial  [ 2008-11-03 00:00:00 EST ,  2008-11-03 22:28:37 EST )
##  partial  [ 2008-11-02 00:00:00 EDT ,  2008-11-03 00:00:00 EST )
##  partial  [ 2008-11-01 00:00:00 EDT ,  2008-11-02 00:00:00 EDT )
##  partial  [ 2008-10-31 00:00:00 EDT ,  2008-11-01 00:00:00 EDT )
##  partial  [ 2008-10-30 00:00:00 EDT ,  2008-10-31 00:00:00 EDT )
##  partial  [ 2008-10-29 23:28:37 EDT ,  2008-10-30 00:00:00 EDT )

def tableExists(tablename):
    exists = getScalar("show tables like '%s'" % tablename) is not None
    return exists
    
def checkOrCreateTable(tablename,forceDrop):
    exists = tableExists(tablename)
    if exists and not forceDrop:
        logInfo(" Table %s is OK" % (tablename))
        return

    if exists and forceDrop:
        logInfo(" Dropping %s table" % (tablename))
        cursor.execute ("DROP TABLE IF EXISTS %s" % tablename)

    ddl = """CREATE TABLE %s (
stamp datetime NOT NULL default '1970-01-01 00:00:00',
watt int(11) NOT NULL default '0',
PRIMARY KEY %sByStamp (stamp)
);
""" % (tablename,tablename)
    cursor.execute(ddl)
    logInfo(" Created %s table" % (tablename))

def checkOrCreateTables(forceDrop):
    suffixes = ["tensec","minute","hour","day"]
    for suffix in suffixes:
        tablename="watt_%s" % suffix
        checkOrCreateTable(tablename,forceDrop);

## This method is only meant to be called with single full day
##  we will therefore find the bounadries ourselves
##     -instead of passing start,stop, we pass in secs
##     -and calculate start,stop, as day around this time
def fillDayTable(secs,fromTableSuffix):
    start = startOfDay(secs,0)
    stop = startOfDay(secs,1)

    timerstart = time.time()


    if fromTableSuffix is None:
        fromTable = "watt"
    else:
        fromTable="watt_%s" % fromTableSuffix

    suffix="day"
    intoTableName="watt_%s" % suffix
    startGMT = time.strftime("%Y-%m-%d %H:%M:%S",time.gmtime(start))
    stopGMT  = time.strftime("%Y-%m-%d %H:%M:%S",time.gmtime(stop))

    selectSql =  "select avg(watt) from %s where stamp>='%s' and stamp<'%s'" % (fromTable,startGMT,stopGMT)
    #print selectSql
    avgForDay = getScalar(selectSql)
    if avgForDay is None:
        #print "got avg %s, NULL" % startGMT
        return
    
    #print "got avg %s, %s" %(startGMT,avgForDay)
    replaceSql = "replace into %s (stamp,watt) values ('%s',%.0f)" % (intoTableName,startGMT,avgForDay)
    #print replaceSql
    cursor.execute(replaceSql)

    #### CHECK that rowcount is exactly 1  (how about updating
    #print " -- %s inserted %d entries (%6.2fs.)" % (intoTableName,cursor.rowcount,time.time()-timerstart)
    
    
def fillTable(suffix,groupingWidth,rightPad,start,stop,fromTableSuffix):
    distantPast = time.mktime((1970,01,01,0,0,0,0,0,-1))
    distantFuture = time.mktime((2035,01,01,0,0,0,0,0,-1))
    if start is None: start=distantPast
    if stop  is None: stop=distantFuture

    timerstart = time.time()

    if fromTableSuffix is None:
        fromTable = "watt"
    else:
        fromTable="watt_%s" % fromTableSuffix
        
    intoTableName="watt_%s" % suffix
    startGMT = time.strftime("%Y-%m-%d %H:%M:%S",time.gmtime(start))
    stopGMT  = time.strftime("%Y-%m-%d %H:%M:%S",time.gmtime(stop))
    
    selectSql =  "select concat(left(stamp,%d),'%s') as g,avg(watt) from %s where stamp>='%s' and stamp<'%s' group by g" % (groupingWidth,rightPad,fromTable,startGMT,stopGMT)

    replaceSql = "replace into %s %s" % (intoTableName,selectSql)
    #print replaceSql
    cursor.execute(replaceSql)
    #print " -- %s inserted %d entries (%6.2fs.)" % (intoTableName,cursor.rowcount,time.time()-timerstart)

    
def fillTables(start,stop):
    fillTable("tensec",18, '0'        ,start, stop,None)
    fillTable("minute",16, ':00'      ,start, stop,None)
    fillTable("hour",  13, ':00:00'   ,start, stop,None)
    #replaced day
    ##fillTable("day",   10, ' 00:00:00',start, stop)
    fillDayTable(start,None)

def getScalar(sql):
    cursor.execute(sql)
    row = cursor.fetchone()
    if row is None: return None
    return row[0]

def startOfTenSec(secs):
    # must keep the dst flag in converting
    secsTuple = time.localtime(secs)
    roundToTen = int(10*math.floor(secsTuple[5]/10))
    startOfPeriodTuple = (secsTuple[0],secsTuple[1],secsTuple[2],secsTuple[3],secsTuple[4],roundToTen,0,0,secsTuple[8])
    startOfPeriodSecs  = time.mktime(startOfPeriodTuple)
    return startOfPeriodSecs

def startOfMinute(secs):
    # must keep the dst flag in converting
    secsTuple = time.localtime(secs)
    startOfPeriodTuple = (secsTuple[0],secsTuple[1],secsTuple[2],secsTuple[3],secsTuple[4],0,0,0,secsTuple[8])
    startOfPeriodSecs  = time.mktime(startOfPeriodTuple)
    return startOfPeriodSecs
def startOfHour(secs):
    # must keep the dst flag in converting
    secsTuple = time.localtime(secs)
    startOfPeriodTuple = (secsTuple[0],secsTuple[1],secsTuple[2],secsTuple[3],0,0,0,0,secsTuple[8])
    startOfPeriodSecs  = time.mktime(startOfPeriodTuple)
    return startOfPeriodSecs
    
def startOfDay(secs,offsetInDays):
    # don't keep DST flag in converting with offset..(unlike hour,minute)
    secsTuple = time.localtime(secs)
    startOfDayWithOffsetTuple = (secsTuple[0],secsTuple[1],secsTuple[2]+offsetInDays,0,0,0,0,0,-1)
    startOfDayWithOffsetSecs  = time.mktime(startOfDayWithOffsetTuple)
    return startOfDayWithOffsetSecs
    
# This is a GENERATOR not a function
# generates a sequence of (startSecs,stopSecs) tuples representing the start/end of Day in local time
# and walks backwards in time
def walkBackDaysGenerator(earlierSecs,laterSecs): # see generators docs
    # Initial Values
    startOfDaySecs = startOfDay(laterSecs,0)
    endOfDaySecs = startOfDay(startOfDaySecs,1)
    # Termination Boundary
    startOfEarliestDay = startOfDay(earliestSecs,0)

    while True:
        if (startOfDaySecs<startOfEarliestDay): # < or <= ???????
            return  # termination of generator
        yield (startOfDaySecs,endOfDaySecs)
        #currentSecs = startOfDay(currentSecs,-1)
        endOfDaySecs = startOfDaySecs
        startOfDaySecs = startOfDay(startOfDaySecs,-1)
        
    
def GMTTimeWithTZ(secs):
    return time.strftime("%Y-%m-%d %H:%M:%S GMT",time.gmtime(secs))

def localTimeWithTZ(secs):
    #return time.strftime("%Y-%m-%d %H:%M:%S %Z (day:%j)",time.localtime(secs))
    return time.strftime("%Y-%m-%d %H:%M:%S %Z",time.localtime(secs))

def testStartOfPeriods():

    for now in [time.time(),
                time.mktime((2008,11,02,1,00,00,0,0,0)), # maps to 01:00:00 EDT
                time.mktime((2008,11,02,1,59,59,0,0,1)),
                time.mktime((2008,11,02,2,00,00,0,0,1)), # maps to 01:00:00 EST
                ]:
        print "Rounding time    : %s" % localTimeWithTZ(now)
        print "  Start of tenSec: %s" % localTimeWithTZ(startOfTenSec(now))
        print "  Start of minute: %s" % localTimeWithTZ(startOfMinute(now))
        print "  Start of hour  : %s" % localTimeWithTZ(startOfHour(now))
        print "  Start of day   : %s" % localTimeWithTZ(startOfDay(now,0))

    
if __name__ == "__main__":


    #testStartOfPeriods()

    conn = MySQLdb.connect (host="127.0.0.1",user="aviso",passwd="",db="ted")
    cursor = conn.cursor ()

    forceDrop=False
    checkOrCreateTables(forceDrop)

    latestSecs = time.time()   +(1*86400)
    earliestSecs =  latestSecs -(86400*365)

    import calendar
    latestSecs = calendar.timegm(getScalar("select max(stamp) from watt").timetuple())
    earliestSecs =  startOfDay(latestSecs,-1)
    earliestSecs =  startOfDay(latestSecs,0)

    print " Summarizing [ %s ,  %s ) (~%.1f days)" % (localTimeWithTZ(earliestSecs),localTimeWithTZ(latestSecs),(latestSecs-earliestSecs)/86400)

    # examle iteration through days
    for (startOfDaySecs,endOfDaySecs) in walkBackDaysGenerator(earliestSecs,latestSecs):
        dayLengthInHours = (endOfDaySecs-startOfDaySecs)/3600
        #print "day [ %s ,  %s ) (%.0fh)" % (localTimeWithTZ(startOfDaySecs),localTimeWithTZ(endOfDaySecs),dayLengthInHours)
        # intersection of dayIteration and original interval 
        (start,stop) = ( max(earliestSecs,startOfDaySecs) ,  min(latestSecs,endOfDaySecs) )
        #print " partial  [ %s ,  %s ) " % (localTimeWithTZ(start),localTimeWithTZ(stop))


    for i in range(1, 2):
        timerstart = time.time()
    
        # this took 69s for 117d, 14s for 28d
        for (startOfDaySecs,endOfDaySecs) in walkBackDaysGenerator(earliestSecs,latestSecs):
            (start,stop) = ( max(earliestSecs,startOfDaySecs) ,  min(latestSecs,endOfDaySecs) )
            (start,stop) = ( startOfDaySecs , endOfDaySecs )
            
            print " partial  [ %s ,  %s ) " % (localTimeWithTZ(start),localTimeWithTZ(stop))
            fillTable("tensec",18, '0'        ,start, stop,None)
            fillTable("minute",16, ':00'      ,start, stop,'tensec')
            fillTable("hour",  13, ':00:00'   ,start, stop,'minute')
            (start,stop) = ( startOfDaySecs , endOfDaySecs )
            fillDayTable(start,'hour')
        
        print " Handled %d days (%6.2fs.)" % (getScalar("select count(*) from watt_day"),time.time()-timerstart)
        
    cursor.close ()
    conn.close ()


# conclusion tensec is everything, what is it's optimal size
