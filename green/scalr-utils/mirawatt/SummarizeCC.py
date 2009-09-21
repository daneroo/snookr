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

summaryHours = {'2001-01-01T01:00:00-0400': 1000}

def parseLocaltimeToSecs(stampStrNoTZ):
	# date format: 2009-07-02T19:08:12
	ISO_DATE_FORMAT = '%Y-%m-%dT%H:%M:%S'
	stampSecs = time.mktime(time.strptime(stampStrNoTZ,ISO_DATE_FORMAT))
	return stampSecs
def formatGMTForMysql(stampSecs):
	ISO_DATE_FORMAT_MYSQL = '%Y-%m-%d %H:%M:%S'
	gmtStr =  time.strftime(ISO_DATE_FORMAT_MYSQL,time.gmtime(stampSecs))
	return gmtStr

def doHistNode(stampStr,histNode):
	# msg/hist/data/sensor(0)/../[h|d]???
	# for all data nodes
	# find first sensor child, only handle sensor0
	#  handle all it's (h|d|m)XXX siblings
	for dataNode in histNode.getElementsByTagName('data'):
		sensor = string.atol(dataNode.getElementsByTagName('sensor')[0].childNodes[0].nodeValue)
		#print "Found sensor: %d" % sensor
		if (sensor==0):
			#print "Handling sensor: %d" % sensor
			for hdm in dataNode.childNodes:
				#print "tag: %s" % hdm.tagName
				if ("sensor"==hdm.tagName):
					continue
				if ("h"==hdm.tagName[:1]):
					print "%s Hour  %05d %10.5f" % ( stampStr, string.atoi(hdm.tagName[1:]),string.atof(hdm.childNodes[0].nodeValue ) )
				if ("d"==hdm.tagName[:1]):
					print "%s Day   %05d %10.5f" % ( stampStr, string.atoi(hdm.tagName[1:]),string.atof(hdm.childNodes[0].nodeValue ) )
				if ("m"==hdm.tagName[:1]):
					print "%s Month %05d %10.5f" % ( stampStr, string.atoi(hdm.tagName[1:]),string.atof(hdm.childNodes[0].nodeValue ) )
		#else:
		#	pass
		#	print "Ignoring sensor: %d" % sensor

# stampStr has the format: 
def parseFragment(stampStr,ccfragment):
	# date format: 2009-07-02T19:08:12-0400
	# remove the utcoffset in string
	stampStrNoTZ = stampStr[:-5]
	# reformat utcofset from -0400 to -04:00
	UTCOffset = "%s:%s" % (stampStr[-5:-2],stampStr[-2:])
	# gmtStampStrExpr = "CONVERT_TZ('%s','America/Montreal','GMT')"%(stampStrNoTZ)
	gmtStampStrExpr = "CONVERT_TZ('%s','%s','GMT') " % ( stampStrNoTZ,UTCOffset)
	# we also need actual value for csv.
	stampSecs = parseLocaltimeToSecs(stampStrNoTZ)
	gmtStr=formatGMTForMysql(stampSecs)

	try:
		ccdom = minidom.parseString(ccfragment)
	except Exception, e:
	#except:
		print "XML Error: %s : %s" % (stampStr,e)
		return

	#calculate drift
	ccTimeStr = ccdom.getElementsByTagName('time')[0].childNodes[0].nodeValue
	ccStampStr = stampStrNoTZ[:-8]+ccTimeStr
	ccTimeSecs = parseLocaltimeToSecs(ccStampStr)
	drift = ccTimeSecs - stampSecs

	histNodeList = ccdom.getElementsByTagName('hist')
	if (histNodeList):
		# confirm only one history Node ?
		print "Detected History T: %s Drift: %f" % (stampStr,drift)
		doHistNode(stampStr,histNodeList[0])
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
	#print "T=%s S=%d watts=%d walen:%d" % (ccTimeStr,sensor,sumwatts,len(wattarray))
	#sql = "INSERT IGNORE INTO cc_native (stamp, watt, sensorid, ch1watt, ch2watt, drift) VALUES (%s,'%d','%s', '%d','%d','%d');" % (gmtStampStrExpr,sumwatts,sensorID,wattarray[0],wattarray[1],drift)
	#print sql
	csv = ','.join([gmtStr,str(sumwatts),str(sensorID),str(wattarray[0]),str(wattarray[1]),str(drift)])
	#print csv

def handleLine(line):
	if (line[:4]=="<!--"):
		return
	stampStr = line[:24]
	CCStr = line[25:-1] # to remove the newline ?
	# omit empty lines
	if (len(CCStr)>0):
		parseFragment(stampStr,CCStr)

if __name__ == "__main__":
        usage = 'python %s' % sys.argv[0]
	totallines=0;
	# read line by line
        while True:
		line = sys.stdin.readline()         # read a one-line string
		if not line:                        # or an empty string at EOF
			break
		totallines+=1

		if ((totallines % 10000) == 0):
			sys.stderr.write("line # %d \n" % (totallines))
		
		handleLine(line)
		#if (totallines>100):
		#	break
                # (stamp, watts,volts) = getGMTTimeWattsAndVoltsFromTedService()

sys.stderr.write("Done; counted %d lines\n" % (totallines))

