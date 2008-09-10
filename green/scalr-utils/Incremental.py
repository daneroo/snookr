
import sys
import os
import sqlite
import string
import time
import MySQLdb

def cnvTime(tedTimeString):
	secs = ( string.atol(tedTimeString) / 10000 - 62135582400000 ) / 1000
	return time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(secs))

def invTime(secs):
        tedTimeLong = long( ((secs * 1000) +  62135582400000)*10000 ) 
        return "%019ld" % tedTimeLong

def testTime():
        secs = time.time()
        log("Testing time from secs:%d" % secs)
        stamp = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(secs))
        ted = invTime(secs)
        log("stamp=%s invTime=%s" % ( stamp, ted ) )
        log("ted=  %s cnvTime=%s" % ( ted, cnvTime(ted) ) )
        sys.exit(0)
        
def log(msg):
	stamp = time.strftime("%Y-%m-%d %H:%M:%S",time.localtime())        
        sys.stderr.write("%s %s\n" % (stamp,msg));

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


log("Incremental Pump started")
daysAgo=0
hoursAgo=1
minutesAgo=0
secondsAgo=0

hourAgoAsTed = invTime(time.time()-((daysAgo*24+hoursAgo)*60+minutesAgo)*60+secondsAgo)
log("1 hour Ago: %s - %s" % (hourAgoAsTed, cnvTime(hourAgoAsTed)) )

# include cnvTime in select:
fullSql = 'select tick,kw from rdu_second_data'
incrementalSql = "select tick, kw from rdu_second_data where tick>='%s'" % hourAgoAsTed

convertedIncrementalSql = "select datetime((tick/10000-62135582400000)/1000,\"unixepoch\",\"localtime\"), kw from rdu_second_data where tick>='%s'" % hourAgoAsTed

#log("sql: %s" % fullSql)
#log("sql: %s" % incrementalSql)
#log("sql: %s" % convertedIncrementalSql)

countRows=0
connsqlite = sqlite.connect(filename)

#log("count: %s" % getScalar('select count(*) from rdu_second_data'))
#log("max: %s" % getScalar('select max(tick) from rdu_second_data'))

curssqlite = connsqlite.cursor()
curssqlite.execute(incrementalSql)

log("SQL executed");
for row in curssqlite:
        stamp = cnvTime(row[0])
        watt = row[1]*1000
        countRows+=1
        if (countRows%10000 == 1):
                log("rows: %8d stamp: %s [%s]" % (countRows,stamp,row[0]));
                
        # print cnvTime(row[0]), row[1]*1000
        # could use REPLACE INTO, instead, or ON DUPLICATE update count=count+1.
        #print ("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))
        print ("REPLACE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))

curssqlite.close()
connsqlite.close()

log("Done (%d total rows)" % countRows);

