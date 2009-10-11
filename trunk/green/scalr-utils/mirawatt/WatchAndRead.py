# This is example code to perpetually read
# a directory with changing logs
#   logdir: prefix*.log
# One of theese files is growing
# as input to this script: use
# while true; do echo `date +%Y-%m-%dT%H:%M:%S%z` "Content in the file" >> PFX`date +%Y%m%dT%H%M00%z`.log; sleep 2; done
#
#  Two parts, 
#  -find and order candidate files
#  -read a file (until some timeout event..)
# The loop is
#  find all files, for each 
#      read each line until EOF
#      check for new files
#      if not more files, keep reading this one
#  
# so to do that can we iterate over a changing vector ?

import time

import fileinput
#import hashlib
import iso8601
import os
import pickle
import re
import string
from xml.dom import minidom

def findPrefixedLogs(path, prefix='CC', includeCompressed=False):
    """
    Finds files (recursively) in the directory tree starting at 'path'
    who's basenames start with prefix
    and who's siffix is .log (or.og.bz2 if includeCOmpressed is True)

    The returnd filepaths are sorted by basename

    Returns a sequence of paths for files found.
    """
    matcherList = [(lambda s: os.path.basename(s).startswith(prefix))]
    if includeCompressed:
        matcherList.append((lambda s: s.endswith('.log') or s.endswith('.log.bz2') or s.endswith('.log.gz')))
    else:
        matcherList.append((lambda s: s.endswith('.log')))

    fileList = ffind(path, namefs=matcherList, relative=False)
    fileList.sort(key=(lambda s: os.path.basename(s)))
    return fileList

# From: http://muharem.wordpress.com/2007/05/18/python-find-files-and-search-inside-them-find-grep/
class ScriptError(Exception): pass
def ffind(path, namefs=None, relative=True):
    """
    Finds files in the directory tree starting at 'path' (filtered by the
    functions in the optional 'namefs' sequence); if the 'relative'
    flag is not set, the result sequence will contain absolute paths.

    Returns a sequence of paths for files found.
    """
    if not os.access(path, os.R_OK):
        raise ScriptError("cannot access path: '%s'" % path)

    fileList = [] # result list
    try:
        for dir, subdirs, files in os.walk(path):
            fileList.extend('%s%s%s' % (dir, os.sep, f) for f in files)
        if not relative: fileList = map(os.path.abspath, fileList)
        if namefs: 
            for ff in namefs: fileList = filter(ff, fileList)
    except Exception, e: raise ScriptError(str(e))
    return(fileList)

# return a copy of logFileList, which need processing according to progress
def removeUnchanged(logFileList, progress): # Remove items which are unchanged in progress.
    activeFileList = [];
    for filename in  logFileList:
        newPI = ProgressItem(filename)
        localtimeModStr = time.strftime('%Y-%m-%dT%H:%M:%S', time.localtime(newPI.lastMod))
        ago = time.time()-newPI.lastMod
        #print " checking: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName),newPI.fileSize,ago,localtimeModStr,newPI.md5)
        oldPI = None
        if (filename in progress):
            oldPI = progress[filename]
        if (not oldPI): # new file: seed progress, calc md5, appent to active file list
            newPI.calcmd5()
            progress[filename] = newPI
            activeFileList.append(filename)
            print " NEW: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName), newPI.fileSize, ago, localtimeModStr, newPI.md5)
        elif (newPI.fileSize != oldPI.fileSize or newPI.lastMod != oldPI.lastMod):
            # current file is in progress, but size/date has changed
            # update info in progress, add to active List (calc md5)
            # make sure we preserve le lastLineRead (high water mark)
            newPI.calcmd5()
            newPI.lastLineRead = oldPI.lastLineRead
            # replace with new progress item
            progress[filename] = newPI
            activeFileList.append(filename)
            print " MOD: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName), newPI.fileSize, ago, localtimeModStr, newPI.md5)
        else:
            #print " SKP: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(oldPI.fileName),oldPI.fileSize,ago,localtimeModStr,oldPI.md5)
            pass

    return activeFileList

def md5OfFileContent(fileName): # if bz2 or gz md5 of uncompressed content
    mode = 'r'
    #fp = open(fileName,mode) #fileinput.hook_compressed(fileName, mode)
    fp = fileinput.hook_compressed(fileName, mode)

    #md5 = hashlib.md5()
    md5 = None
    try:
        import hashlib
        md5 = hashlib.md5()
    except:
        import md5
        md5 = md5.md5()

    try:
        while 1:
            data = fp.read(8096) #buffer at a time
            if not data:
                break
            md5.update(data)
    finally:
        fp.close()
    return md5.hexdigest()

class ProgressItem:
    def __init__(self, fileName):
        self.fileName = fileName
        stats = os.stat(fileName)
        self.fileSize = stats[6]
        self.lastMod = stats[8]
        self.md5 = 'UNSET'
        self.lastLineRead = 0

    def calcmd5(self):
        self.md5 = md5OfFileContent(self.fileName)

progressPickleFileName = 'progress.pkl'
def loadProgressFromPickle():
    progress = {} # map of file -> ProgressItem
    if (os.path.exists(progressPickleFileName)):
        print "Reading Pickled Summary: %s" % progressPickleFileName
        pckfp = open(progressPickleFileName, 'rb')
        progress = pickle.load(pckfp)
        pckfp.close()
    return progress;

def saveProgressToPickle(progress):
    print "Writting Pickled Summary: %s" % progressPickleFileName
    pckfp = open(progressPickleFileName, 'wb')
    pickle.dump(progress, pckfp)
    pckfp.close()

def onepass(progress):
    # renew the list
    logFileList = findPrefixedLogs(os.curdir, prefix='CC2', includeCompressed=True)
    activeFileList = removeUnchanged(logFileList, progress)
    if (len(activeFileList) <= 0):
        print "No files to process"
        return

    #for line in fileinput.input(activeFileList):
    for line in fileinput.input(activeFileList, openhook=fileinput.hook_compressed):
        if (fileinput.filelineno() <= progress[fileinput.filename()].lastLineRead):
            #print "skip  %06d from:%s" % (fileinput.filelineno(),fileinput.filename())
            continue

        # indicate progress (before or after actual processing ?
        progress[fileinput.filename()].lastLineRead = fileinput.filelineno()

        # actually process the line
        handleLine(line)

        if (fileinput.filelineno() % 1000 == 0):
            pass #print "file:%s:%06d  -mod:%s" % (os.path.basename(fileinput.filename()),fileinput.filelineno(),lastModDate)
        #print "file:%s:%06d" % (os.path.basename(fileinput.filename()),fileinput.filelineno())

    saveProgressToPickle(progress)
    print "Processing summary:"
    for fileName in sorted(progress.keys(), key=(lambda s: os.path.basename(s))):
        print " %7d lines from %s in %s" % (progress[fileName].lastLineRead, os.path.basename(fileName), os.path.dirname(fileName))

MARKStamp = None #'2000-01-01T00:00:00'
MARKCheckLen = 13      # 13 for hour,16 for minute
def handleLine(line):
    if (line[:4] == "<!--"):
        return
    stampStr = line[:24]
    CCStr = line[25:-1] # to remove the newline ?
    # omit empty lines

    # Mark the logs as the stampStr advances.
    global MARKStamp
    if (stampStr[:MARKCheckLen] != MARKStamp):
        print "# MARK -- %s" % stampStr
    MARKStamp = stampStr[:MARKCheckLen]

    if (len(CCStr) > 0):
        parseFragment(stampStr, CCStr)

def warnDrift(stamp, ccfragment):
    matchTime = re.search("<time>(?P<hour>[0-9]{2}):(?P<minute>[0-9]{2}):(?P<second>[0-9]{2})</time>", ccfragment)
    if (not matchTime): return
    ccstamp = stamp.replace(hour=int(matchTime.group('hour')),
        minute=int(matchTime.group('minute')),
        second=int(matchTime.group('second')))
    delta = ccstamp - stamp
    drift = delta.days * 86400 + delta.seconds #+delta.microseconds/1000000.0
    if (drift > 43200):
        drift = -86400 + drift
    elif (drift < -43200):
        drift = 86400 + drift
    if (abs(drift) > 600):
        print "WARNING clock drift: %f seconds @ %s" % (drift, stamp)

# could validate entire DTD, hist version,sample version
msgintegrity = re.compile('<msg>.*</msg>')
chwattpattern = re.compile('<(?P<ch>ch[0-9])><watts>(?P<watt>[0-9]+)</watts></(?P=ch)>')
def extractWattsRE(stampStr, ccfragment):  # return sum of watt channels - could be 1,2,3 channels
    ok  = msgintegrity.search(ccfragment)
    if (not ok):
        print "XML Error: %s : %s" % (stampStr, 'RE: Incomplete fragment')
        return None
    
    pairs = chwattpattern.findall(ccfragment)
    # e.g.: [('ch1', '00769'), ('ch2', '00400')]
    if (pairs):
        sumwatts = 0
        for ch, watt in pairs:
            sumwatts += int(watt)
        #print "RE  %s W = sum(%s)" % (sumwatts,pairs)
        return sumwatts
    else:
        return None

def extractWattsXML(stampStr, ccfragment):  # return sum of watt channels - could be 1,2,3 channels
    try:
        ccdom = minidom.parseString(ccfragment)
        sumwatts = 0
        wattarray = []
        for wattnode in ccdom.getElementsByTagName('watts'):
            watt = string.atol(wattnode.childNodes[0].nodeValue)
            wattarray.append(watt)
            sumwatts += watt
        #print "XML %s W = sum(%s)" % (sumwatts,wattarray)
        return sumwatts
    except Exception, e:
        print "XML Error: %s : %s" % (stampStr, e)

    return None

# moving averages for each scope
averages = {
    'month'  : {},
    'day'    : {},
    'hour'   : {},
    'minute' : {},
    'tensec' : {},
}
def movingAverage(stamp, value):
    '''Accumulate averages for different scopes'''
    # - convert stamp to LocalTime (it probably already is
    localStamp = iso8601.toLocalTZ(stamp)
    scopeStamps = {
        'month'  : iso8601.startOfMonth(localStamp),
        'day'    : iso8601.startOfDay(localStamp),
        'hour'   : iso8601.startOfHour(localStamp),
        'minute' : iso8601.startOfMinute(localStamp),
        'tensec' : iso8601.startOfTensec(localStamp),
    }
    if (False): print "stamp:%s  local:%s Mo:%s DD:%s HH:%s MM:%s TS:%s" % (
        stamp,localStamp,
        scopeStamps['month'],
        scopeStamps['day'],
        scopeStamps['hour'],
        scopeStamps['minute'],
        scopeStamps['tensec'],
        )
    for scope in ['month','day','hour','minute','tensec']:
        scopeStamp = scopeStamps[scope]
        if (scopeStamp in averages[scope]):
            averages[scope][scopeStamp][0] = averages[scope][scopeStamp][0] + value
            averages[scope][scopeStamp][1] = averages[scope][scopeStamp][1] + 1
        else:
            averages[scope][scopeStamp] = [value, 1];

def writeXML():
    if (False): # combine Archived Hours/days
        combinedHours = {}
        for k, v in summaryHours.items():
            combinedHours[k] = [v, 1]
        for k, v in averageHours.items():
            combinedHours[k] = v
        combinedDays = {}
        for k, v in summaryDays.items():
            combinedDays[k] = [v, 1]
        for k, v in averageDays.items():
            combinedDays[k] = v

    scopes = [
        {'id':0, 'name':'Live', 'averages':averages['tensec'], 'howMany':30},
        {'id':1, 'name':'Hour', 'averages':averages['minute'], 'howMany':60},
        {'id':2, 'name':'Day',  'averages':averages['hour'],   'howMany':24}, #combinedHours
        {'id':3, 'name':'Week', 'averages':averages['day'],    'howMany':7},  #combinedDays
        {'id':4, 'name':'Month','averages':averages['day'],    'howMany':30}, #combinedDays
    ]
    f = open('feeds.xml', 'w')
    print >> f, '<?xml version="1.0"?>'
    print >> f, '<!DOCTYPE plist PUBLIC "-//iMetrical//DTD OBSFEEDS 1.0//EN" "http://www.imetrical.com/DTDs/ObservationFeeds-1.0.dtd">'
    print >> f, '<feeds>'
    for scope in scopes:
        avgArray = scope['averages']
        sortedKeys = avgArray.keys()
        sortedKeys.sort()
        sortedKeys.reverse()
        scopeLastValue = avgArray[sortedKeys[0]][0] / avgArray[sortedKeys[0]][1]
        scopeAverageAverage = [0.0, 0]
        for d in sortedKeys[:scope['howMany']]:
            scopeAverageAverage[0] = scopeAverageAverage[0] + avgArray[d][0]
            scopeAverageAverage[1] = scopeAverageAverage[1] + avgArray[d][1]
        scopeAverageValue = scopeAverageAverage[0] / scopeAverageAverage[1]
        scopeStamp = sortedKeys[0]
        scopeValue = scopeAverageValue
        if (scope['id'] == 0): scopeValue = scopeLastValue

        scopeStamp = iso8601.fmtExtendedZ(scopeStamp)
        print >> f, '  <feed scopeId="%s" name="%s" stamp="%s" value="%.1f">' % (scope['id'], scope['name'], scopeStamp, scopeValue)
        for d in sortedKeys[:scope['howMany']]:
            obsStamp = iso8601.fmtExtendedZ(d)
            print >> f, '    <observation stamp="%s" value="%.1f"/>' % (obsStamp, avgArray[d][0] / avgArray[d][1])
        print >> f, '  </feed>'
    print >> f, '</feeds>'
    f.close()


def parseFragment(stampStr, ccfragment):
    # date format: 2009-07-02T19:08:12-0400
    stamp = iso8601.parse_iso8601(stampStr)

    useDOM=False # or use RE...
    if (useDOM):
        sumwatts = extractWattsXML(stampStr,ccfragment)
    else:
        sumwatts = extractWattsRE(stampStr, ccfragment)

    if (sumwatts):
        movingAverage(stamp, sumwatts)

    warnDrift(stamp, ccfragment)

    
if __name__ == "__main__":
    print "Prefixed File Logs (can be compressed)"

    progress = {} # loadProgressFromPickle() # map of file -> ProgressItem
    for n in range(1, 400):
        print "Processing loop: %d" % n
        onepass(progress)
        writeXML()
        break
        time.sleep(5.0)

