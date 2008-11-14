
import sys
import os
import string
import time
import scalr
# for shortcuts, or even from scalr import *
from scalr import logInfo,logWarn,logError
from scalr import cnvTime,invTime,testTime
from scalr import getScalar


if len(sys.argv) < 2:
	print "usage : %s <SQLite3 db file>" % sys.argv[0]
	sys.exit(0);
else:
	filename = sys.argv[1]

if not os.path.exists(filename):
	print "Could not find SQLite3 db file: %s" % filename
	sys.exit(0);

logInfo("Pump started")
connsqlite = scalr.SqliteConnectNoArch(filename)

#logInfo("count: %s" % getScalar(connsqlite,'select count(*) from rdu_second_data'))
#logInfo("max: %s" % getScalar(connsqlite,'select max(tick) from rdu_second_data'))

curssqlite = connsqlite.cursor()
curssqlite.execute('select tick,kw from rdu_second_data')

#curssqlite.execute('select tick,kw from rdu_second_data order by tick desc limit 100000')
#curssqlite.execute('select tick,kw from rdu_second_data where tick>"0633540625260005000"')

#hourAgo = invTime(time.time()-10*60)
#hourAgo = invTime(time.time()+100)
#curssqlite.execute('select tick,kw from rdu_second_data where tick>"%s"' % hourAgo)

logInfo("SQL executed");
countRows=0
for row in curssqlite:
        stamp = cnvTime(row[0])
        watt = row[1]*1000
        countRows+=1
        if (countRows%20000 == 0):
                logInfo("rows: %8d stamp: %s [%s]" % (countRows,stamp,row[0]));
                
        # print cnvTime(row[0]), row[1]*1000
        # could use REPLACE INTO, instead, or ON DUPLICATE update count=count+1.
        #print ("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))
        print ("REPLACE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))

curssqlite.close()
connsqlite.close()

logInfo("Done");


# import MySQLdb
#connmysql = MySQLdb.connect (host = "127.0.0.1",
#                             user = "aviso",
#                             passwd = "",
#                             db = "ted")
#cursmysql = connmysql.cursor()
#cursmysql.execute("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d')" % (stamp,watt))
#cursmysql.close()
#connmysql.close()
