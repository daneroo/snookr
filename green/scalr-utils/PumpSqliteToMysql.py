
import sys
import os
import sqlite
import string
import time
import MySQLdb

def cnvTime(tedTimeString):
	secs = ( string.atol(tedTimeString) / 10000 - 62135582400000 ) / 1000
	return time.strftime("%Y-%m-%d %H:%M:%S",time.localtime(secs))


#print "We have  ",len(sys.argv) ," arguments"
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

countRows=0
connsqlite = sqlite.connect(filename)
curssqlite = connsqlite.cursor()
curssqlite.execute('select * from rdu_second_data')
for row in curssqlite:
        stamp = cnvTime(row[0])
        watt = row[1]*1000
        countRows+=1
        if (countRows%20000 == 0):
                sys.stderr.write("rows: %d stamp: %s\n" % (countRows,stamp));
                
        # print cnvTime(row[0]), row[1]*1000
        # could use REPLACE INTO, instead, or ON DUPLICATE update count=count+1.
        print ("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d');" % (stamp,watt))

curssqlite.close()
connsqlite.close()


#connmysql = MySQLdb.connect (host = "127.0.0.1",
#                             user = "aviso",
#                             passwd = "",
#                             db = "ted")
#cursmysql = connmysql.cursor()
#cursmysql.execute("INSERT IGNORE INTO watt (stamp, watt) VALUES ('%s', '%d')" % (stamp,watt))
#cursmysql.close()
#connmysql.close()
