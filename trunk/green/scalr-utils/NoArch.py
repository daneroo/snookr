import sys
import os
#import string
#import time

def logError(msg):
	sys.stderr.write(msg)
	sys.stderr.write("\n")
def logWarn(msg):
	logError(msg)
def logInfo(msg):
	logError(msg)
	
def SqliteConnectNoArch(filename): # return either a sqlite3/2/1 connection
	if not os.path.exists(filename):
		logError("Could not find SQLite3 db file: %s" % filename)
		sys.exit(0);
	try:
		from pysqlite2 import dbapi2 as sqlite
		logInfo('Using sqlite-2')
		return sqlite.connect(filename)
	except:
		pass
#		logWarn("from pysqlite2 import dbapi2 failed")
	try:
		import sqlite3 as sqlite;
		logInfo('Using sqlite-3')
		return sqlite.connect(filename)
	except:
		pass
#		logWarn("import sqlite3 failed")
	try:
		import sqlite
		logInfo('Using sqlite-1')
		return sqlite.connect(filename)
	except:
		pass
#		logWarn("import sqlite failed")
	return None
	
if __name__ == "__main__":
	
	import sys
	import os
	if len(sys.argv) < 2:
		print "usage : %s <SQLite3 db file>" % sys.argv[0]
		sys.exit(0);
	else:
		filename = sys.argv[1]

	conn = SqliteConnectNoArch(filename)
	c = conn.cursor()
	c.execute('select * from rdu_second_data limit 10')
	for row in c:
		print "| %s | %f | %f | %f |" % row
