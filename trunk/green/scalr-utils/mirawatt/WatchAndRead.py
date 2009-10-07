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

import os
import fileinput
import time
import hashlib
import pickle

def findPrefixedLogs(path,prefix='CC',includeCompressed=False):
    """
    Finds files (recursively) in the directory tree starting at 'path'
    who's basenames start with prefix
    and who's siffix is .log (or.og.bz2 if includeCOmpressed is True)

    The returnd filepaths are sorted by basename

    Returns a sequence of paths for files found.
    """
    matcherList = [ (lambda s: os.path.basename(s).startswith(prefix) ) ]
    if includeCompressed:
        matcherList.append( (lambda s: s.endswith('.log') or s.endswith('.log.bz2') or s.endswith('.log.gz')) )
    else:
        matcherList.append( (lambda s: s.endswith('.log')) )

    fileList =  ffind(path,namefs=matcherList,relative=False)
    fileList.sort(key=(lambda s: os.path.basename(s) ))
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
def removeUnchanged(logFileList,progress): # Remove items which are unchanged in progress.
    activeFileList=[];
    for filename in  logFileList:
        newPI = ProgressItem(filename)
        localtimeModStr =  time.strftime('%Y-%m-%dT%H:%M:%S',time.localtime(newPI.lastMod))
        ago = time.time()-newPI.lastMod
        #print " checking: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName),newPI.fileSize,ago,localtimeModStr,newPI.md5)
        oldPI=None
        if (filename in progress):
            oldPI=progress[filename]
        if (not oldPI): # new file: seed progress, calc md5, appent to active file list
            newPI.calcmd5()
            progress[filename] = newPI
            activeFileList.append(filename)
            print " NEW: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName),newPI.fileSize,ago,localtimeModStr,newPI.md5)
        elif (newPI.fileSize!=oldPI.fileSize or newPI.lastMod!=oldPI.lastMod):
            # current file is in progress, but size/date has changed
            # update info in progress, add to active List (calc md5)
            # make sure we preserve le lastLineRead (high water mark)
            newPI.calcmd5()
            newPI.lastLineRead = oldPI.lastLineRead
            # replace with new progress item
            progress[filename] = newPI
            activeFileList.append(filename)
            print " MOD: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(newPI.fileName),newPI.fileSize,ago,localtimeModStr,newPI.md5)
        else:
            #print " SKP: %s  len:%08d ago:%ds. mod:%s md5:%s" % (os.path.basename(oldPI.fileName),oldPI.fileSize,ago,localtimeModStr,oldPI.md5)
            pass

    return activeFileList

def md5OfFileContent(fileName): # if bz2 or gz md5 of uncompressed content
    mode='r'
    fp = fileinput.hook_compressed(fileName,mode)
    md5 = hashlib.md5()
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
    def __init__(self,fileName):
        self.fileName = fileName
        stats = os.stat(fileName)
        self.fileSize = stats[6]
        self.lastMod = stats[8]
        self.md5 = 'UNSET'
        self.lastLineRead = 0

    def calcmd5(self):
        self.md5 = md5OfFileContent(self.fileName)

progressPickleFileName='progress.pkl'
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
    logFileList = findPrefixedLogs(os.curdir,prefix='CC2',includeCompressed=True)
    activeFileList = removeUnchanged(logFileList,progress)
    if (len(activeFileList)<=0):
        print "No files to process"
        return

    for line in fileinput.input(activeFileList,openhook=fileinput.hook_compressed):
        if (fileinput.filelineno()<=progress[fileinput.filename()].lastLineRead):
            #print "skip  %06d from:%s" % (fileinput.filelineno(),fileinput.filename())
            continue

        # indicate progress (before or after actual processing ?
        progress[fileinput.filename()].lastLineRead=fileinput.filelineno()

        # actually process the line
        #process(line)

        if (fileinput.filelineno()%1000==0):
            pass #print "file:%s:%06d  -mod:%s" % (os.path.basename(fileinput.filename()),fileinput.filelineno(),lastModDate)
        #print "file:%s:%06d" % (os.path.basename(fileinput.filename()),fileinput.filelineno())

    saveProgressToPickle(progress)
    print "Processing summary:"
    for fileName in sorted(progress.keys(),key=(lambda s: os.path.basename(s)) ):
        print " %7d lines from %s in %s" % (progress[fileName].lastLineRead,os.path.basename(fileName),os.path.dirname(fileName))

if __name__ == "__main__":
    print "Prefixed File Logs (can be compressed)"

    progress = loadProgressFromPickle() # map of file -> ProgressItem
    for n in range(1,400):
        print "Processing loop: %d" % n
        onepass(progress)
        time.sleep(5.0)

