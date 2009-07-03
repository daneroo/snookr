# Extract from http://aria.dl.sologlobe.com:9090/DashboardData
# <DashboardData>
#   ...
#   <VrmsNowDsp>124.7</VrmsNowDsp>
#   ...
#   <KWNow>0.560</KWNow>
#   ...
# </DashboardData>
# run as 
#   while true; do  python ReadTEDService.py ; sleep 1; done
# until this is turned into a timing loop.

import sys
import string
import math
import getopt
import datetime
import time
import urllib 
from xml.dom import minidom  

def parseLocaltimeToSecs(stampStrNoTZ):
	# date format: 2009-07-02T19:08:12
	ISO_DATE_FORMAT = '%Y-%m-%dT%H:%M:%S'
	stampSecs = time.mktime(time.strptime(stampStrNoTZ,ISO_DATE_FORMAT))
	return stampSecs
def formatGMTForMysql(stampSecs):
	ISO_DATE_FORMAT_MYSQL = '%Y-%m-%d %H:%M:%S'
	gmtStr =  time.strftime(ISO_DATE_FORMAT_MYSQL,time.gmtime(stampSecs))
	return gmtStr

def parseFragment(stampStrNoTZ,ccfragment):
	ccdom = minidom.parseString(ccfragment)
	ccTimeStr = ccdom.getElementsByTagName('time')[0].childNodes[0].nodeValue
	ccStampStr = stampStrNoTZ[:-8]+ccTimeStr
	stampSecs = parseLocaltimeToSecs(stampStrNoTZ)
	ccTimeSecs = parseLocaltimeToSecs(ccStampStr)
	drift = ccTimeSecs - stampSecs

	histNode = ccdom.getElementsByTagName('hist')
	if (histNode):
		#print "Detected History Drift=%f" % drift
		return

	sensor = string.atol(ccdom.getElementsByTagName('sensor')[0].childNodes[0].nodeValue)
	sensorID = string.atol(ccdom.getElementsByTagName('id')[0].childNodes[0].nodeValue)
	#print "D=%f T=%s S=%d frag=%s" % (drift,ccTimeStr,sensor,ccfragment)
	sumwatts=0
	wattarray = []
	for wattnode in ccdom.getElementsByTagName('watts'):
		watt = string.atol(wattnode.childNodes[0].nodeValue)
		#print watt
		wattarray.append(watt)
		sumwatts += watt
	# print "T=%s S=%d watts=%d walen:%d" % (ccTimeStr,sensor,sumwatts,len(wattarray))
	gmtStr=formatGMTForMysql(stampSecs)
	sql = "INSERT IGNORE INTO cc_native (stamp, watt, sensorid, ch1watt, ch2watt, drift) VALUES ('%s','%d','%s', '%d','%d','%d');" % (gmtStr,sumwatts,sensorID,wattarray[0],wattarray[1],drift)
	print sql
	csv = ','.join([gmtStr,str(sumwatts),str(sensorID),str(wattarray[0]),str(wattarray[1]),str(drift)])
	print csv

def handleLine(line):
	stampStr = line[:24]
	# date format: 2009-07-02T19:08:12-0400
	# remove the utcoffset in string
	stampStrNoTZ = stampStr[:-5]
	stampSecs = parseLocaltimeToSecs(stampStrNoTZ)

	#ISO_DATE_FORMAT = '%Y-%m-%dT%H:%M:%S'
	#gmtStr =  time.strftime(ISO_DATE_FORMAT,time.gmtime(stampSecs))+'Z'
	#gmtStr =  time.strftime(ISO_DATE_FORMAT,time.gmtime(stampSecs))+'+0000'
	#localtimeStr =   time.strftime(ISO_DATE_FORMAT,time.localtime(stampSecs)) 
	#print "line %d --- %s %s" % (totallines,stampStr,localtimeStr) 

	CCStr = line[25:-1] # to remove the newline ?
	if (len(CCStr)>0):
		parseFragment(stampStrNoTZ,CCStr)

if __name__ == "__main__":
        usage = 'python %s' % sys.argv[0]
	totallines=0;
	# read line by line
        while True:
		line = sys.stdin.readline()         # read a one-line string
		if not line:                        # or an empty string at EOF
			break
		totallines+=1
		handleLine(line)
		if (totallines>1000000):
			break
                # (stamp, watts,volts) = getGMTTimeWattsAndVoltsFromTedService()


print "Done; counted %d lines" % (totallines)


