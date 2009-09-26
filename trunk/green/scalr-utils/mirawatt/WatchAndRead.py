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


def hook_compressed_seek(filename, mode):
    ext = os.path.splitext(filename)[1]
    if ext == '.gz':
        import gzip
        return gzip.open(filename, mode)
    elif ext == '.bz2':
        import bz2
        return bz2.BZ2File(filename, mode)
    else:
        #return open(filename, mode)
        f =  open(filename, mode)
        f.seek(2669103-5000)
        return f;

if __name__ == "__main__":
    print "Prefixed File Logs (can be compressed)"
    logFileList = findPrefixedLogs(os.curdir,prefix='CC2',includeCompressed=True)
    for fileName in  logFileList:
        print " Will Process: %s" % fileName
    print

    progress = {} # map of absfileName,lastLineRead(not max)  later (,md5sum)
    
    def onepass():
        # we can maybe seek woth os.lseek(fd, pos, how) applied to fileinput.fileno
        # fileinput.input([files[, inplace[, backup[, mode[, openhook]]]]])
        for line in fileinput.input(logFileList,openhook=fileinput.hook_compressed):
        #for line in fileinput.input(logFileList,openhook=hook_compressed_seek):
            #process(line)
            progress[fileinput.filename()]=fileinput.filelineno()

            if (fileinput.isfirstline()):
                print "%06d Reading file:%s:%06d" % (fileinput.lineno(),os.path.basename(fileinput.filename()),fileinput.filelineno())
            if (fileinput.filelineno()%1000==0):
                lastModDate='Must Be a pipe!'
                try:
                    stats = os.fstat(fileinput.fileno())
                    lastModDate = gmtStr =  time.strftime('%Y-%m-%dT%H:%M:%S',time.localtime(stats[8]))
                    print fileinput._file
                except AttributeError:
                    pass
                print "%06d                 %06d mod:%s" % (fileinput.lineno(),fileinput.filelineno(),lastModDate)

        print "Processing summary:"
        for fileName in sorted(progress.keys(),key=(lambda s: os.path.basename(s)) ):
            print " %7d lines from %s in %s" % (progress[fileName],os.path.basename(fileName),os.path.dirname(fileName))

    onepass()
#import os, itertools
#os.stat(filename).st_mtime
#f=open('file_to_tail.txt','rb')
#for line in tail(f, 20):
#    print line
#
#while 1:
#    where = file.tell()
#    line = file.readline()
#    if not line:
#        time.sleep(1)
#        file.seek(where)
#    else:
#        print line, # already has newline

