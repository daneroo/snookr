
import sys
import os
import string
import time
import getopt
import scalr
# for shortcuts, or even from scalr import *
from scalr import logInfo,logWarn,logError
from scalr import cnvTime,invTime,testTime
from scalr import getScalar

#def main():
usage = 'python %s --db <dbfile> (--all | --secs <secs> --minutes <minutes> --hours <hours> --days <days>)' % sys.argv[0]

# parse command line options
try:
	opts, args = getopt.getopt(sys.argv[1:], "", ["db=", "all", "secs=","minutes=","hours=","days="])
except getopt.error, msg:
	logError('error msg: %s' % msg)
	logError(usage)
	sys.exit(2)

dbfilename = ''
daysAgo=0
hoursAgo=0
minutesAgo=0
secondsAgo=0
allTime = False

for o, a in opts:
	if o == "--db":
		dbfilename = a
	elif o == "--secs":
		secondsAgo = string.atol(a)
	elif o == "--all":
		allTime = True
	elif o == "--minutes":
		minutesAgo = string.atol(a)
	elif o == "--hours":
		hoursAgo = string.atol(a)
	elif o == "--days":
		daysAgo = string.atol(a)

totalSecondsAgo = ((daysAgo*24+hoursAgo)*60+minutesAgo)*60+secondsAgo

if dbfilename == '':
	logError('specify --db dbfile:')
	logError(usage)
	sys.exit(2)

if not os.path.exists(dbfilename):
	logError("Could not find SQLite3 db file: %s" % dbfilename)
	sys.exit(2);

if not allTime and totalSecondsAgo == 0:
	logError(usage)
	logError('Use --all or at least one of --secs --minutes --hours --days')
	sys.exit(2)


logInfo("Incremental Pump started")

fullSql = 'select tick,kw from rdu_second_data'
sql = fullSql
if not allTime:
	referenceTimeAsTed = invTime(time.time()-totalSecondsAgo)
	logInfo("referenceTime: %s - %s" % (referenceTimeAsTed, cnvTime(referenceTimeAsTed)) )
	# include cnvTime in select:
	incrementalSql = "%s where tick>='%s'" % (fullSql,referenceTimeAsTed)
	#convertedIncrementalSql = "select datetime((tick/10000-62135582400000)/1000,\"unixepoch\",\"localtime\"), kw from rdu_second_data where tick>='%s'" % referenceTimeAsTed
	#logInfo("sql: %s" % incrementalSql)
	#logInfo("sql: %s" % convertedIncrementalSql)
	sql = incrementalSql
else:
	logInfo("Full Dump (--all) : %d" % allTime)
	#logInfo("sql: %s" % fullSql)

logInfo("using sql: %s" % sql)

sys.exit(0)
connsqlite = scalr.SqliteConnectNoArch(dbfilename)


#logInfo("count: %s" % getScalar('select count(*) from rdu_second_data'))
#logInfo("max: %s" % getScalar('select max(tick) from rdu_second_data'))

curssqlite = connsqlite.cursor()
curssqlite.execute(incrementalSql)

logInfo("SQL executed");
countRows=0
for row in curssqlite:
        stamp = cnvTime(row[0])
        watt = row[1]*1000
        countRows+=1
        if (countRows%10000 == 1):
                logInfo("rows: %8d stamp: %s [%s]" % (countRows,stamp,row[0]));
                
        # print cnvTime(row[0]), row[1]*1000
        # could use REPLACE INTO, instead, or ON DUPLICATE update count=count+1.
        #print ("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))
        print ("REPLACE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))

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
