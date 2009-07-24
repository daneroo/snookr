import sys
import time
import math
from suds.client import Client
from xml.dom import minidom

# /Users/daniel/Documents/NetBeansProjects/snookr/BTVClient
# grep -r lascala ./build/generated/wsimport/client/com/snapstream/webservice/*.java|grep base:
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVDispatcher.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVGuideUpdater.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLibrary.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVLicenseManager.asmx?WSDL");
#   "http://lascala.dl.sologlobe.com:8129/wsdl/BTVScheduler.asmx?WSDL");
#
#    daniel@LASCALA /cygdrive/c/Program Files/SnapStream Media/Beyond TV/wwwroot/wsdl
#    $ ls
#    BTVBatchProcessor.asmx	BTVLibrary.asmx		BTVScheduler.asmx
#    BTVDispatcher.asmx	BTVLicenseManager.asmx	BTVSettings.asmx
#    BTVExpiration.asmx	BTVLiveTVManager.asmx	BTVWebServiceManager.asmx
#    BTVGuideData.asmx	BTVLog.asmx		InternalBTVScheduler.asmx
#    BTVGuideUpdater.asmx	BTVNotifier.asmx

def urlForService(svcName):
    return "http://lascala.dl.sologlobe.com:8129/wsdl/%s.asmx?WSDL" % svcName

def printAllAPI():
    apiNames = ['BTVBatchProcessor','BTVLibrary','BTVScheduler','BTVDispatcher',
        'BTVLicenseManager','BTVSettings','BTVExpiration',
        'BTVLiveTVManager','BTVWebServiceManager','BTVGuideData','BTVLog',
        'InternalBTVScheduler','BTVGuideUpdater','BTVNotifier']
    for apiName in apiNames:
        print "";
        print "-=-=-=-=-=   API for %s" % apiName;
        print Client(urlForService(apiName))
        print "";

# Clients
licenseManagerClient = Client(urlForService('BTVLicenseManager'))
libraryClient        = Client(urlForService('BTVLibrary'))
guideUpdaterClient   = Client(urlForService('BTVGuideUpdater'))
guideDataClient   = Client(urlForService('BTVGuideData'))

def getBTVVersion():
    return licenseManagerClient.service.GetVersionNumber()

def getLogonAuthTicket():
    result = licenseManagerClient.service.Logon('','','')
    for prop in result.Properties[0]:
        if (prop.Name=="AuthTicket"):
            return prop.Value
    return 'Unauthorized'

def className(descr,thing):
    print "%s: class %s" % (descr,thing.__class__.__name__)

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

# To convert One Show
def propListToDict(list):
    dico = dict()
    for prop in list:
        dico[prop.Name] = prop.Value
        #print "  %s ::= %s" % (prop.Name,prop.Value)
    return dico


# To convert Array Of Shows, or Jobs, (so far)
def getListOfDict(result): # as in GetJobs, or FlatViewByDate
    list = []
    for bag in result.PVSPropertyBag:
        list.append(propListToDict(bag.Properties.PVSProperty))
    return list

def formatBTVDuration(show):
    if (not 'Duration' in show):
        if ('InProgress' in show and show['InProgress']=='True'):
            return 'REC'
        return "Unknown"

    durationStr = show['Duration']
    duration = long(durationStr)
    secs = duration/10000000.0
    if (secs<60):
        return "%.2f s" % (secs)
    minutes = secs/60.0
    if (minutes<60):
        return "%.0f m %.0f s" % (math.floor(minutes),secs % 60.0)
    hours = minutes/60.0
    return "%.0f h %.0f m" % (math.floor(hours),minutes % 60.0)

def formatBTVSize(sizeStr):
    size = long(sizeStr)
    if (size>(1024*1024*1024)):
        return "%.2fG" % (size/1024/1024/1024)
    if (size>(1024*1024)):
        return "%.2fM" % (size/1024/1024)
    if (size>(1024)):
        return "%.2fK" % (size/1024)
    return "%.2fB" % size

#   From Java BTVCLient
#    Date convertBTVToDate(long btvStamp) {
#        /*
#         *  so if we parsse in GMT :
#         *     long offsetMS = df.parse("1601-01-01 00:00:00").getTime();
#         *   but if we parse in EST:
#         *     long offsetMS = df.parse("1600-12-31 19:00:00").getTime();
#         */
#        final long epochBTVMS = -11644473600000l;
#        return new Date(btvStamp / 10000 + epochBTVMS);
#    }
def formatBTVDate(btvStampStr):
    # from Java: 1601-01-01 00:00:00 GMT or 1600-12-31 19:00:00 EST python is secs. java is milis
    epochBTVMS = -11644473600.000
    #formatStr = "%Y-%m-%d %H:%M:%S %Z"
    formatStr = "%Y-%m-%d %H:%M"
    btvStamp = long(btvStampStr) / 10000000.0 + epochBTVMS
    fmt = time.strftime(formatStr,time.localtime(btvStamp))
    #print "Converting %s" % btvStampStr
    #print "  Epoch %s" % epochBTVMS
    #print "  -> stamp %f" % btvStamp
    #print "  -> fmt %s" % fmt
    return fmt;

def formatShow(show,format='short'):
    # EpisodeTitle==DisplayText
    if (format=='short'):
        print "%s @ %s [%s, %s]" % (show['DisplayText'],formatBTVDate(show['TargetStart']),formatBTVDuration(show),formatBTVSize(show['Length']))
    elif (format=='long'):
        print "%s @ %s [%s, %s]" % (show['DisplayText'],formatBTVDate(show['TargetStart']),formatBTVDuration(show),formatBTVSize(show['Length']))
        print "  %s" % (show['EpisodeDescription'])
        if (show['Actors']):
            print "  %s" % (show['Actors'])
    else: # format=='all'
        srt = show.keys()
        srt.sort()
        for k in srt:
            print " %s: %s"% (k, show[k])


def testLibraryClient():
    print "-=-=- B- Get Library Client API =-=-=-"
    print "B-1 -=-=- FlatViewByDate =-=-=-"
    fvbd = libraryClient.service.FlatViewByDate(authTicket)
    # just get two:
    #fvbd = libraryClient.service.FlatViewByDate2(authTicket,6,1)

    debugFlatView=False
    if (debugFlatView):
        print "-=-=- Get Result =-=-=-"
        #meta('fvbd',fvbd)
        #meta('fvbd[0]',fvbd[0])
        #meta('fvbd[0][0]',fvbd[0][0])
        #print fvbd

        className('fvbd',fvbd)
        # -=-= fvbd.PVSPropertyBag == fvbd[0]
        className('fvbd.PVSPropertyBag',fvbd.PVSPropertyBag)
        className('fvbd[0]',fvbd[0])
        # length of list == number of shows
        print('len(fvbd.PVSPropertyBag) = %d' % len(fvbd.PVSPropertyBag))
        # first element:
        className('fvbd.PVSPropertyBag[0]',fvbd.PVSPropertyBag[0])
        # Equivalent
        className('fvbd.PVSPropertyBag[0].Properties',fvbd.PVSPropertyBag[0].Properties)
        className('fvbd.PVSPropertyBag[0][0]',fvbd.PVSPropertyBag[0][0])
        # Equivalent
        className('fvbd.PVSPropertyBag[0].Properties.PVSProperty',fvbd.PVSPropertyBag[0].Properties.PVSProperty)
        className('fvbd.PVSPropertyBag[0].Properties[0]',fvbd.PVSPropertyBag[0].Properties[0])
        # length of list = number of attributes
        print('len(fvbd.PVSPropertyBag[0].Properties.PVSProperty) = %d' % len(fvbd.PVSPropertyBag[0].Properties.PVSProperty))
        # first Property:
        className('fvbd.PVSPropertyBag[0].Properties.PVSProperty[0]',fvbd.PVSPropertyBag[0].Properties.PVSProperty[0])
        # Name, Attr
        print('fvbd.PVSPropertyBag[0].Properties.PVSProperty[0].Name: %s' % fvbd.PVSPropertyBag[0].Properties.PVSProperty[0].Name)
        print('fvbd.PVSPropertyBag[0].Properties.PVSProperty[0].Value %s' % fvbd.PVSPropertyBag[0].Properties.PVSProperty[0].Value)
         # so what we want is a simple list of shows:
         # each show will have Named Attributes: show['attribute'] ::= value
        print "Convert one show:"
        firstShow = propListToDict(fvbd.PVSPropertyBag[0].Properties.PVSProperty)
        print ""
        print "Show as dict: %s" % firstShow

    allShows = getListOfDict(fvbd)

    #formatShow(allShows[8],'all')

    print "Found %d library shows" % len(allShows)
    for show in allShows:
        formatShow(show,'short')

    #formatShow(allShows[8],'long')
    #formatShow(allShows[0],'all')
########### End of testLibraryClient

def testGuideUpdaterClient():
    print "-=-=- C- Guide Updater Client API =-=-=-"
    #print guideUpdaterClient
    print "C-1 -=-=- Updater status =-=-=-"
    lau = guideUpdaterClient.service.GetLastAttemptedUpdate(authTicket)
    lsu = guideUpdaterClient.service.GetLastSuccessfulUpdate(authTicket)
    nau = guideUpdaterClient.service.GetNextAttemptedUpdate(authTicket)
    print "Last Attempted  Update: %s" % formatBTVDate(lau)
    print "Last Successful Update: %s" % formatBTVDate(lsu)
    print "Next Attempted  Update: %s" % formatBTVDate(nau)
########### End of testGuideUpdaterClient

def testGuideDataClient():
    print "-=-=- C- Guide Data Client API =-=-=-"
    #print guideDataClient
    print "C-1 -=-=- Guide Data =-=-=-"
    lastUpdate =  guideDataClient.service.GetLastUpdateTime(authTicket)
    print "Last Guide Update: %s" % formatBTVDate(lastUpdate)
    # need chanel id, or seried ids.
    # GetEpisodesByRange2(xs:string authTicket, xs:string uniqueChannelIDStart, xs:string uniqueChannelIDEnd, xs:unsignedLong timeStart, xs:unsignedLong timeEnd, )
    firstChannel='000000000000002000000010097'
    lastChannel ='000000000000071000000010104'

    epochBTVMS = -11644473600.000
    nowSecs = time.time()
    timeStart = long(nowSecs - epochBTVMS) * 10000000
    timeEnd = timeStart #long((nowSecs+ZZZ) - epochBTVMS) * 10000000
    # this return a string[][]...
    print guideDataClient.service.GetEpisodesByRange2(authTicket,firstChannel,lastChannel,timeStart,timeEnd)
########### End of testGuideUpdaterClient

def testSettings():
    settingsClient = Client(urlForService('BTVSettings'))
    # this returns xml in whih we find  chanels: BaseUniqueChannelID
    lineupXML = settingsClient.service.GetBaseLineups(authTicket)
    # 2 probs unescaped &, and bad unicode char
    #print lineupXML[300:380] # bad unicode char
    #print lineupXML[28310:28320] # bad &: arts & enternn
    #return
    lineupXML = lineupXML.replace('&','&amp;')
    lineupDOM = minidom.parseString(lineupXML.encode( "utf-8" ))
    #kWattStr = xmldoc.getElementsByTagName('KWNow')[0].childNodes[0].nodeValue
    channels = lineupDOM.getElementsByTagName('Channel')
    for c in channels:
        cname=''
        cid=''
        props =  c.getElementsByTagName('Property')
        #print "has %d props" % len(props)
        for p in props:
            pname = p.getElementsByTagName('Name')[0].firstChild.nodeValue
            pvalue = p.getElementsByTagName('Value')[0].firstChild.nodeValue
            #print "Name= %s" % pname
            #print "Value= %s" % pvalue
            if (pname=='StationName'):
                cname = pvalue
            if (pname=='UniqueChannelID'):
                cid = pvalue
        print "Channel: %s : UID %s" % (cname,cid)
########### End of testSettings

def formatRecording(recording,format='all'):
    if (format=='short'):
        if (not 'Duration' in recording):
            recording['Duration']=recording['TargetDuration']
        #print "%s @ %s [%s]" % (recording['DisplayTitle'],formatBTVDate(recording['TargetStart']),formatBTVDuration(recording))
        print "%s [%10s] : %s" % (formatBTVDate(recording['TargetStart']),formatBTVDuration(recording),recording['DisplayTitle'],)
    else: # format=='all'
        srt = recording.keys()
        srt.sort()
        for k in srt:
            print " %s: %s"% (k, recording[k])
        print "---------"

def testScheduler():
    # GetNextRecording, GetLastRecording GetUpcomingRecordings
    # GetRecordings is for specific GUID

    schedulerClient = Client(urlForService('BTVScheduler'))

    upcomingRecs = schedulerClient.service.GetUpcomingRecordings(authTicket)
    upcomingRecsList = getListOfDict(upcomingRecs)
    print "-=-=-= Found %d upcoming recordings" % len(upcomingRecsList)
    for rec in upcomingRecsList:
        #formatRecording(rec,'all')
        formatRecording(rec,'short')

    # NOT WORKING YET wrong structure
    #nextRecording = schedulerClient.service.GetNextRecording(authTicket)
    #nextRecList = getListOfDict(nextRecording)
    #print "-=-=-= Found %d next recordings" % len(nextRecList)
    #for rec in nextRecList:
    #    #formatRecording(rec,'all')
    #    formatRecording(rec,'short')

    #print nextRecXML
    # last is 'last scheduled'
    #lastRecXML = schedulerClient.service.GetLastRecording(authTicket)
    #print lastRecXML


########### End of testScheduler


# Execution Start
btvVersion = getBTVVersion()
authTicket = getLogonAuthTicket()
print "BTV Version:%s  authTicket: %s" % (btvVersion,authTicket)

#printAllAPI()
testLibraryClient()
#testGuideUpdaterClient()
#testGuideDataClient()
#testSettings()
testScheduler()
