
import sys
import os
import string
import time
import scalr
# for shortcuts, or even from scalr import *
from scalr import logInfo,logWarn,logError

def cnvTime(tedTimeString):
	secs = ( string.atol(tedTimeString) / 10000 - 62135582400000 ) / 1000
	return time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(secs))

def invTime(secs):
        tedTimeLong = long( ((secs * 1000) +  62135582400000)*10000 ) 
        return "%019ld" % tedTimeLong

def testTime():
        secs = time.time()
        logInfo("Testing time from secs:%d" % secs)
        stamp = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(secs))
        ted = invTime(secs)
        logInfo("stamp=%s invTime=%s" % ( stamp, ted ) )
        logInfo("ted=  %s cnvTime=%s" % ( ted, cnvTime(ted) ) )
        sys.exit(0)

def getScalar(sql):
        curs = connsqlite.cursor()
        curs.execute(sql)
        row = curs.fetchone()
        curs.close()
        return row[0]

if len(sys.argv) < 2:
	print "usage : %s <SQLite3 db file>" % sys.argv[0]
	sys.exit(0);
else:
	filename = sys.argv[1]

if not os.path.exists(filename):
	print "Could not find SQLite3 db file: %s" % filename
	sys.exit(0);
# time formatting:
# 1 second between readings
#  print 633530465840008874 - 633530465830000554
# --> 10008320
# 90000 seconds
# print (633530465840008874 - 633529550800006250 ) / 10000000


logInfo("Pump started")
countRows=0
connsqlite = scalr.SqliteConnectNoArch(filename)

#logInfo("count: %s" % getScalar('select count(*) from rdu_second_data'))
#logInfo("max: %s" % getScalar('select max(tick) from rdu_second_data'))

curssqlite = connsqlite.cursor()
curssqlite.execute('select tick,kw from rdu_second_data')

#curssqlite.execute('select tick,kw from rdu_second_data order by tick desc limit 100000')
#curssqlite.execute('select tick,kw from rdu_second_data where tick>"0633540625260005000"')

#hourAgo = invTime(time.time()-10*60)
#hourAgo = invTime(time.time()+100)
#curssqlite.execute('select tick,kw from rdu_second_data where tick>"%s"' % hourAgo)

logInfo("SQL executed");
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
