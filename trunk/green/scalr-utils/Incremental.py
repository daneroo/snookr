
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
usage = '''
   python %s --db <dbfile> --all ' % sys.argv[0]
or python %s --db <dbfile> [--secs <secs> --minutes <minutes> --hours <hours> --days <days>]' % sys.argv[0]
or python %s --db <dbfile> [--start \'YYYY-MM-DD HH:mm:ss\' [--stop \'YYYY-MM-DD HH:mm:ss\']]' % sys.argv[0]
'''

# parse command line options
try:
	opts, args = getopt.getopt(sys.argv[1:], "", ["db=", "all", "secs=","minutes=","hours=","days=","start=","stop="])
except getopt.error, msg:
	logError('error msg: %s' % msg)
	logError(usage)
	sys.exit(2)

dbfilename = ''
daysAgo=0
hoursAgo=0
minutesAgo=0
secondsAgo=0
startSecs=0
stopSecs=0
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
	elif o == "--start":
		try:
			startSecs = time.mktime(time.strptime(a,"%Y-%m-%d %H:%M:%S"))
		except ValueError:
			print " Start Date (%s) does not match format:  \'YYYY-MM-DD HH:mm:ss\'" % a
	elif o == "--stop":
		try:
			stopSecs = time.mktime(time.strptime(a,"%Y-%m-%d %H:%M:%S"))
		except ValueError:
			print " Stop Date (%s) does not match format:  \'YYYY-MM-DD HH:mm:ss\'" % a

totalSecondsAgo = ((daysAgo*24+hoursAgo)*60+minutesAgo)*60+secondsAgo

if dbfilename == '':
	logError('specify --db dbfile:')
	logError(usage)
	sys.exit(2)

if not os.path.exists(dbfilename):
	logError("Could not find SQLite3 db file: %s" % dbfilename)
	sys.exit(2);

if not (allTime or totalSecondsAgo or startSecs):
	logError(usage)
	logError('Use --all, --start YYYY-... or at least one of --secs --minutes --hours --days')
	sys.exit(2)

if (totalSecondsAgo):
	startSecs = time.time()-totalSecondsAgo

logInfo("Incremental Pump started")

fullSql = 'select tick,kw from rdu_second_data'
sql = fullSql
if not allTime:
	startTimeAsTed = invTime(startSecs)
	if (stopSecs):
		stopTimeAsTed = invTime(stopSecs)
		logInfo(" interval: %s - %s" % (cnvTime(startTimeAsTed),cnvTime(stopTimeAsTed)) )
		sql = "%s where tick>='%s' and tick<'%s'" % (fullSql,startTimeAsTed,stopTimeAsTed)
	else:
		logInfo(" interval: %s - " % cnvTime(startTimeAsTed) )
		sql = "%s where tick>='%s'" % (fullSql,startTimeAsTed)
else:
	logInfo("Full Dump (--all) : %d" % allTime)

logInfo("Using sql: %s" % sql)

connsqlite = scalr.SqliteConnectNoArch(dbfilename)
curssqlite = connsqlite.cursor()
curssqlite.execute(sql)

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
