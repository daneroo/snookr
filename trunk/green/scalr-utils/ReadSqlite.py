
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
# time formatting:
# 1 second between readings
#  print 633530465840008874 - 633530465830000554
# --> 10008320
# 90000 seconds
# print (633530465840008874 - 633529550800006250 ) / 10000000

conn = sqlite3.connect(filename)
c = conn.cursor()
c.execute('select * from rdu_second_data')
for row in c:
    print row
