import sys
import os
# This also works!
#try:
#	from pysqlite2 import dbapi2 as sqlite
#except:
#	print "import failed trying sqlite3"
#	import sqlite3 as sqlite

import string
import time

def SqliteConnectNoArch(filename): # return either a sqlite3/2/1 connection
	if not os.path.exists(filename):
		print "Could not find SQLite3 db file: %s" % filename
		sys.exit(0);
	try:
		from pysqlite2 import dbapi2 as sqlite
		print 'Using sqlite-2'
		return sqlite.connect(filename)
	except:
		pass
#		print "from pysqlite2 import dbapi2 failed"
	try:
		import sqlite3 as sqlite;
		print 'Using sqlite-3'
		return sqlite.connect(filename)
	except:
		pass
#		print "import sqlite3 failed"
	try:
		import sqlite
		print 'Using sqlite-1'
		return sqlite.connect(filename)
	except:
		pass
#		print "import sqlite failed"
	return None
	

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