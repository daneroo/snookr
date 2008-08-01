
import sys
import os
import sqlite3

#print "We have  ",len(sys.argv) ," arguments"
if len(sys.argv) < 2:
	print "usage : %s <SQLite3 db file>" % sys.argv[0]
	sys.exit(0);
else:
	filename = sys.argv[1]

if not os.path.exists(filename):
	print "Could not find SQLite3 db file: %s" % filename
	sys.exit(0);

conn = sqlite3.connect(sys.argv[1])
c = conn.cursor()
c.execute('select * from rdu_second_data')
for row in c:
    print row
