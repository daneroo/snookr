
import sys
import os
import string
import time
import getopt
import scalr
# for shortcuts, or even from scalr import *
from scalr import logInfo,logWarn,logError
#deprecated
from scalr import cnvTime,invTime,testTime
# new methods
from scalr import secsToTed,tedToSecs,tedToGMT,tedToLocal,localTimeToTed
from scalr import getScalar
from time import time,strftime,strptime,gmtime,localtime,mktime

############################################
### Documentation 
############################################
#  This testing program does 3 things
#  -show the use of ted's time representation subtleties
#   and the use of our conversion functions
#  -displays ted's month,day (hor,minute) tables
#  -analyzes ted's sequences of timestamps to discover stability issues:
#    probable duplicates, system time changes, unavailable data.
#

usage = 'python %s --db <dbfile> [--all | [--showTedTimeUsage --showMonthTable --showSequentialEvents]]' % sys.argv[0]

# parse command line options
try:
	opts, args = getopt.getopt(sys.argv[1:], "", ["db=","showTedTimeUsage","showMonthTable","showSequentialEvents"])
except getopt.error, msg:
	logError('error msg: %s' % msg)
	logError(usage)
	sys.exit(2)

dbfilename = ''
showTedTimeUsage = False
showMonthTable = False
showSequentialEvents = False

for o, a in opts:
	if o == "--db":
		dbfilename = a
	elif o == "--all":
		showTedTimeUsage = True
		showMonthTable = True
		showSequentialEvents = True
	elif o == "--showTedTimeUsage":
		showTedTimeUsage = True
	elif o == "--showMonthTable":
		showMonthTable = True
	elif o == "--showSequentialEvents":
		showSequentialEvents = True

if dbfilename == '':
	logError('specify --db dbfile:')
	logError(usage)
	sys.exit(2)

if not os.path.exists(dbfilename):
	logError("Could not find SQLite3 db file: %s" % dbfilename)
	sys.exit(2);

if not (showTedTimeUsage or showMonthTable or showSequentialEvents):
	logError('specify --all or at least one of --showTedTimeUsage --showMonthTable --showSequentialEvents')
	logError(usage)
	sys.exit(2)


connsqlite = scalr.SqliteConnectNoArch(dbfilename)
curssqlite = connsqlite.cursor()


if (showTedTimeUsage):
##########################################################
        print
        print "TED stamps usage examples"
        ted = "0633529550800006250"
        print "Usage example"
        print "  ted time string: |%s|" % ted
        print "    --> GMT:       |%s|" % tedToGMT(ted)
        print "    --> Local:     |%s|" % tedToLocal(ted)
        print " and back again (roungind to second)"
        localTimeStr = "2008-07-29 19:04:40";
        print "  local time string: |%s|" % localTimeStr
        print "    --> TED  :       |%s|" % scalr. localTimeToTed(localTimeStr)
        
        print "How about DST boundary"
        print " 2008-03-09 01:59:59 EST +:01 -> 2008-11-02 03:00:00 EST skip ahead an hour"
        print " 2008-11-02 01:59:59 EDT +:01 -> 2008-11-02 01:00:00 EST skip back an hour"
        print " 2008-03-09 02:00:00 to 2008-03-09 02:59:59 don't exist"
        print " 2008-11-02 01:00:00 to 2008-03-09 01:59:59 exist twice"


        # 2008-03-09 02:00:00 and 2008-11-02 02:00:00
        dstSecs = [1205046000 , 1225605600]
        for atBoundarySecs in dstSecs:
                print "DST Boundary  : %s " % strftime("%Y-%m-%d %H:%M:%S GMT",gmtime(atBoundarySecs))
                print "  just before : %s" % strftime("%Y-%m-%d %H:%M:%S %Z",localtime(atBoundarySecs-1))
                print "  just after  : %s" % strftime("%Y-%m-%d %H:%M:%S %Z",localtime(atBoundarySecs))
                
                print ""
                dstTimeStr = ["2008-03-09 02:00:00","2008-11-02 01:00:00"] 
                for boundaryTimeStr in dstTimeStr:
                        print "The problem with parsing local Time: %s" % boundaryTimeStr 
                        print " parsing local time without explicit timezone %Z is ambigous:"
                        print "HOWEVER secs is never ambiguous!"
                        withEST = "%s EST" % boundaryTimeStr
                        withESTsecs = mktime(strptime(withEST,"%Y-%m-%d %H:%M:%S %Z"))
                        withEDT = "%s EDT" % boundaryTimeStr
                        withEDTsecs = mktime(strptime(withEDT,"%Y-%m-%d %H:%M:%S %Z"))
                        withoutsecs = mktime(strptime(boundaryTimeStr,"%Y-%m-%d %H:%M:%S"))
                        print " with EST: %-23s -> secs: %f -> %s" % (withEST, withESTsecs, strftime("%Y-%m-%d %H:%M:%S GMT",gmtime(withESTsecs)))
                        print " with EDT: %-23s -> secs: %f -> %s" % (withEDT, withEDTsecs,strftime("%Y-%m-%d %H:%M:%S GMT",gmtime(withEDTsecs)))
                        print " without : %-23s -> secs: %f -> %s" % (boundaryTimeStr, withoutsecs,strftime("%Y-%m-%d %H:%M:%S GMT",gmtime(withoutsecs)))
                        
        print ""
        print " with Ted - this is how we roundtrip secs->ted->secs->ted"
        secsOfInterest = [dstSecs[0]-1,dstSecs[0],dstSecs[1]-3600,dstSecs[1]-1,dstSecs[1]]
        for pr in [0,1]:
                print ""
                for secs in secsOfInterest:
                        print "  %s Roundtrip" % strftime("%Y-%m-%d %H:%M:%S %Z",localtime(secs))
                        
                        ted = secsToTed(secs)
                        secsBack = tedToSecs(ted)
                        reTed = secsToTed(secsBack)
                        gmt = strftime("%Y-%m-%d %H:%M:%S",gmtime(secs))
                        gmtBack = strftime("%Y-%m-%d %H:%M:%S",gmtime(secsBack))
                        if (pr==0):
                                print "  %d : (%s) -> %d -> (%s)" % (secs,ted,secsBack,reTed)
                        if (pr==1):
                                print "  %s GMT : (%s) -> %s GMT -> (%s)" % (gmt,ted,gmtBack,reTed)
# end of if showTedTimeUsage

if (showMonthTable):
##########################################################
        print
        print "TED stamps in other time scope tables month,day.hour,minute"
        timeScopes = ["minute","hour", "day", "month"] # minute
        # timeScopes = ["day", "month"] # minute
        # timeScopes = ["month"] # minute
        for t in timeScopes:
                print "-=-=-= Stamp Information from '%s' table (12 entries only) =-=-=-" % t
                sql = 'select tick,kw from rdu_%s_data limit 12' % t
                logInfo(" --using sql: %s" % sql)
                
                curssqlite.execute(sql)
                countRows=0
                for row in curssqlite:
                        tedLong  =  string.atol(row[0])
                        stampLocal = tedToLocal(row[0])
                        stampGMT = tedToGMT(row[0])
                        watt = row[1]*1000
                        print "%7s %019ld %19s %19s GMT %5d" % (t,tedLong,stampLocal,stampGMT,watt)
# end of if (showMonthTable):

if (showSequentialEvents):
        # #########################################################
        logInfo("Sequential Time Analysis started")
        sql = 'select tick,kw from rdu_second_data'
        logInfo("using sql: %s" % sql)

        def showDiff(symbol,previousTedLong,tedLong,rowNum):
                if (previousTedLong==0):
                        logWarn("No previous TED date")
                        return
                tedStr  =  "%019ld" % tedLong
                previousTedStr  =  "%019ld" % previousTedLong

                tedDiff = tedLong-previousTedLong
                tedDiffInSecs = tedDiff/10000/1000;
                print "----------row %10d ----------" % rowNum
                print " %6s %10d %019ld %19s" % (symbol,tedDiff,previousTedLong,tedToLocal(previousTedStr))
                print " %6s %10.4f %019ld %19s" % ("",tedDiffInSecs,tedLong,tedToLocal(tedStr))

        curssqlite.execute(sql)
        logInfo("SQL executed")
        countRows=0
        previousTedLong=0
        histogram={"==OK==":0} # etc
        for row in curssqlite:
                tedLong  =  string.atol(row[0])
                stampGMT = tedToGMT(row[0])
                watt = row[1]*1000

                expectedTedDiff = 10000000  # == 1 second
                tedJitter = 10000           #  1 millisecond: A LOT of  stamp is duplicated with only <10000 diff
                tedDiff = tedLong-previousTedLong

                # classify tedDiffs into bins
                histoDescription = {
                        "<<<<<<" : "diff < -jitter  -- Significant Reverse Step",
                        "~~~~~~" : "|diff|< jitter  -- Probable duplicate",
                        "======" : "|diff|== 0      -- Exact duplicate; subcase of ~~~~~",
                        ">>02>>" : "diff > 2-secs   -- Skip ahead: 2 secs",
                        ">>10>>" : "diff > 10-secs  -- Skip ahead: 10 secs",
                        ">>1m>>" : "diff > 60-secs  -- Skip ahead: 60 secs",
                        ">>2m>>" : "diff > 120-secs -- Skip ahead: 120 secs",
                        "==OK==" : "diff  < 2 secs  -- Normal"
                        }

                symbol="??????"# unknown 
                if (tedDiff<-tedJitter):
                        symbol="<<<<<<"
                elif (tedDiff == 0):
                        symbol="======"
                elif (abs(tedDiff) < tedJitter):
                        symbol="~~~~~~"
                elif (tedDiff>=120*expectedTedDiff):
                        symbol=">>2m>>"
                elif (tedDiff>=60*expectedTedDiff):
                        symbol=">>1m>>"
                elif (tedDiff>10*expectedTedDiff):
                        symbol=">>10>>"
                elif (tedDiff>2*expectedTedDiff):
                        symbol=">>02>>"
                else:
                        symbol="==OK=="
                        
                if (symbol in histogram):
                        histogram[symbol]=histogram[symbol]+1;
                else:
                        histogram[symbol]=1;
		
                # only print significant events! <-jitter, and > 60 seconds
                if ( (tedDiff < -tedJitter) or ( tedDiff >= 120*expectedTedDiff) ):
                        showDiff(symbol,previousTedLong,tedLong, countRows)

                        previousTedLong = tedLong;
                        countRows+=1
                        if (countRows%100000 == 1):
                                # logInfo("row: %8d stamp: %s GMT [%s]" % (countRows,stampGMT,row[0]));
                                logInfo("row: %8d stamp: %s GMT %s [%s]" % (countRows,stampGMT,tedToLocal(row[0]),row[0]));

        print "Histogram of Classified Sequential Time Differences"
        # for symbol, count in histogram.iteritems():
        #     print "%6s : %8d" % (symbol,count)
        total=0;
        myorderedkeys = ["<<<<<<","======","~~~~~~",">>02>>",">>10>>",">>1m>>",">>2m>>","==OK=="]
        for symbol in myorderedkeys:
                count = 0;
                if (symbol in histogram):
                        count = histogram[symbol]
                        total += count
                        print "%6s : %8d : %s" % (symbol,count,histoDescription[symbol])
                        print "%6s : %8d" % ("total",total)

        curssqlite.close()
        connsqlite.close()

logInfo("Done (%d total rows)" % countRows);


# import MySQLdb
#connmysql = MySQLdb.connect (host = "127.0.0.1",
#                             user = "aviso",
#                             passwd = "",
#                             db = "ted")
#cursmysql = connmysql.cursor()
#cursmysql.execute("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d')" % (stamp,watt))
#cursmysql.close()
#connmysql.close()

#if __name__ == '__main__':
#  main()
