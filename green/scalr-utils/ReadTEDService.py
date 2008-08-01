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

import datetime
import urllib 
from xml.dom import minidom  

TED_DASHBOARDDATA_URL = 'http://aria.dl.sologlobe.com:9090/DashboardData'
ISO_DATE_FORMAT = '%Y-%m-%d %H:%M:%S'

usock = urllib.urlopen(TED_DASHBOARDDATA_URL)
xmldoc = minidom.parse(usock)                              
usock.close()                                              
# print xmldoc.toxml() 
# print ""
# print "Extracting KWNow and VrmsNowDsp"

isodatestr = datetime.datetime.now().strftime(ISO_DATE_FORMAT) 
kWstr = xmldoc.getElementsByTagName('KWNow')[0].childNodes[0].nodeValue
vstr = xmldoc.getElementsByTagName('VrmsNowDsp')[0].childNodes[0].nodeValue
print "%s\t%s\t%s" % (isodatestr, kWstr, vstr) 


