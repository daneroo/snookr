import sys
from suds.client import Client

# /Users/daniel/Documents/NetBeansProjects/snookr/BTVClient
# grep -r lascala ./build/generated/wsimport/client/com/snapstream/webservice/*.java|grep base:
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVDispatcher.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVGuideUpdater.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLibrary.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLicenseManager.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVScheduler.asmx?WSDL");

def urlForService(svcName):
    return "http://lascala.dl.sologlobe.com:8129/wsdl/%s.asmx?WSDL" % svcName

# Show services:
btvServiceNames = ['BTVLicenseManager','BTVLibrary','BTVGuideUpdater']
for svcName in btvServiceNames:
    print " %s -> : %s" % (svcName,urlForService(svcName))

# Clients
licenseManagerClient = Client(urlForService('BTVLicenseManager'))
libraryClient = Client(urlForService('BTVLibrary'))

def getBTVVersion():
    return licenseManagerClient.service.GetVersionNumber()

def getLogonAuthTicket():
    result = licenseManagerClient.service.Logon('','','')
    for prop in result.Properties[0]:
        if (prop.Name=="AuthTicket"):
            return prop.Value
    return 'Unauthorized'

def meta(descr,thing):
    if (thing.__class__.__name__=='ArrayOfPVSPropertyBag'):
        print "%s: ArrayOfPVSPropertyBag[%d]" % (descr,len(thing[0]))
        firstThing = thing[0][0][0]
        meta( " %s[0][0][0]"%descr,firstThing)
    if (thing.__class__.__name__=='PVSPropertyBag'):
        print "%s: PVSPropertyBag[%d]" % (descr,len(thing.Properties))
        firstProp = thing.Properties[0]
        meta( " %s.Properties"%descr,firstProp)
    else:
        print "%s: ? %s" % (descr,thing.__class__.__name__)


btvVersion = getBTVVersion()
authTicket = getLogonAuthTicket()
print "BTV Version:%s  authTicket: %s" % (btvVersion,authTicket)


print "-=-=- B- Get Library Client API =-=-=-"
#print libraryClient

print "B-1 -=-=- FlatViewByDate =-=-=-"
fvbd = libraryClient.service.FlatViewByDate(authTicket)
print "-=-=- Get Result =-=-=-"
meta('fvbd',fvbd)
meta('fvbd[0]',fvbd[0])
meta('fvbd[0][0]',fvbd[0][0])
print fvbd[0][0]

