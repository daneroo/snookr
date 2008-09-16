import MySQLdb
import sys

print "Hello MySQL"

conn = MySQLdb.connect (host = "127.0.0.1",
                        user = "aviso",
                        passwd = "",
                        db = "ted")
cursor = conn.cursor ()

def dropAndCreateTable(name):
    print "making %s" % (name)
    tablename="watt%s" % name
    ddl = """
CREATE TABLE %s (
stamp datetime NOT NULL default '1970-01-01 00:00:00',
watt int(11) NOT NULL default '0',
PRIMARY KEY wattByStamp (stamp)
);
""" % (tablename)
    cursor.execute ("DROP TABLE IF EXISTS %s" % tablename)
    cursor.execute(ddl)
    print "ddl executed for %s " % (tablename)

def dropAndCreateTables():
    dropAndCreateTable("tensec");
    dropAndCreateTable("minute");
    dropAndCreateTable("hour");
    dropAndCreateTable("day");

def fillTable(name,groupingWidth):
    # example for watttensec !
    # replace into watttensec select concat(left(stamp,18),'0') as g,avg(watt) from watt where stamp>'2008-09-15 11:00:00' group by g;
    
    # example : cursor.execute("replace into wattminute select left(stamp,16) as g,avg(watt) from watt group by g")
    tablename="watt%s" % name
    cursor.execute("replace into %s select left(stamp,%d) as g,avg(watt) from watt group by g" % (tablename,groupingWidth))
    print "Table %s now has %d entries" % (tablename,getScalar("select count(*) from %s" % tablename))

    
def fillTables():
    fillTable("minute",16)
    fillTable("hour",13)
    fillTable("day",10)

def getScalar(sql):
    cursor.execute(sql)
    row = cursor.fetchone()
    return row[0]

dropAndCreateTables()
fillTables()

cursor.close ()
conn.close ()



