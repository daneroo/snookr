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
from scalr import logInfo,logWarn,logError
import datetime
import time
import urllib 
from xml.dom import minidom  

#def getkWAndVFromTedService():
def getTimeWattsAndVoltsFromTedService():
	TED_DASHBOARDDATA_URL = 'http://aria.dl.sologlobe.com:9090/DashboardData'
	ISO_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'
	usock = urllib.urlopen(TED_DASHBOARDDATA_URL)
	xmldoc = minidom.parse(usock)                              
	usock.close()                                              
	# print xmldoc.toxml() 
	# print ""
	# print "Extracting KWNow and VrmsNowDsp"

	isodatestr = datetime.datetime.now().strftime(ISO_DATE_FORMAT) 
	kWattStr = xmldoc.getElementsByTagName('KWNow')[0].childNodes[0].nodeValue
	voltStr = xmldoc.getElementsByTagName('VrmsNowDsp')[0].childNodes[0].nodeValue
	#print "%s\t%s\t%s" % (isodatestr, kWattStr, voltStr) 
	watts = string.atof(kWattStr)*1000.0
	volts  = string.atof(voltStr)
	return (isodatestr , watts,volts)

usage = 'python %s  ( --duration <secs> | --forever)' % sys.argv[0]

# parse command line options
try:
	opts, args = getopt.getopt(sys.argv[1:], "", ["duration=", "forever"])
except getopt.error, msg:
	logError('error msg: %s' % msg)
	logError(usage)
	sys.exit(2)

duration=1

for o, a in opts:
	if o == "--duration":
		duration = string.atol(a)
	elif o == "--forever":
		duration = -1

print "duration is %d" % duration
start = time.time()
while True:
	datetimenow = datetime.datetime.now()
	now=time.time()
	if duration>0 and (now-start)>duration:
		break
	(isodatestr, watts,volts) = getTimeWattsAndVoltsFromTedService()
	print "%s --- %s\t%.0f\t%.1f" % (datetimenow,isodatestr, watts, volts) 

	now=time.time()
	if duration>0 and (now-start)>duration:
		break
	# sleep to hit the second on the nose:
	(frac,dummy) = math.modf(now)
	desiredFractionalOffset = .1
	delay = 1-frac + desiredFractionalOffset
	# delay after first iteration should be 1-service time
	#print "delay: %s  %s" % (delay,now)
	time.sleep(delay)

print "Done; lasted %f" % (time.time()-start)


