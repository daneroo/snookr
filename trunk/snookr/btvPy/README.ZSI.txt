Using Suds Library instead

Failed attempt at using ZSI soap Implementation
Could not solve this dependancy adequately:

    from xml.dom.ext.reader import PyExpat
ImportError: No module named ext.reader

-- ZSI-2.0-py2.5.egg
Unzip the egg: eggs not in path ?
 unzip ZSI-2.0-py2.5.egg

Generate the stubs:
 python btvgen.py

Us it with btvtest:
 python btvtest.py

----- btvgen.py
#from ZSI.ServiceProxy import ServiceProxy
#from ZSI.generate import wsdl2python
import sys
from ZSI.generate import commands

# /Users/daniel/Documents/NetBeansProjects/snookr/BTVClient
# grep -r lascala ./build/generated/wsimport/client/com/snapstream/webservice/*.java|grep base:
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVDispatcher.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVGuideUpdater.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLibrary.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLicenseManager.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVScheduler.asmx?WSDL");

btvServiceNames = ['BTVLicenseManager','BTVLibrary','BTVGuideUpdater']

for svcName in btvServiceNames:
    print "Generating Stubs for %s" % svcName
    url = "http://lascala.dl.sologlobe.com:8129/wsdl/%s.asmx?WSDL" % svcName
    print "  at url: %s" % url
    commands.wsdl2py(['--url',url])

----- btvtest.py

import BTVLicenseManager_services

licMan = BTVLicenseManager_services.BTVLicenseManagerLocator().getBTVLicenseManagerSoap()

logonArgs = BTVLicenseManager_services.LogonSoapIn()
licMan.Logon(logonArgs)








