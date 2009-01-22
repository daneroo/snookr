# run as
# Talk to Aztech IHD through serial device /dev/ttyUSB1
#

import sys
import string
import math
import getopt
from scalr import logInfo,logWarn,logError
import datetime
import time
import MySQLdb
########################### START of ted.py Stuff

import serial
import binascii
import struct

# Special bytes

def oneIteration(device,doCommand):
    print " One Iteration %s" % (time.strftime("%Y-%m-%d %H:%M:%S %Z",time.localtime()))
    ser = serial.Serial(device,38400,timeout=0.1,rtscts=0,dsrdtr=0,xonxoff=0)
    #print "Used port: %s" % ser.portstr
    #print " Serial object: %s" % ser
    #print "CTS:%s DSR:%s RI:%s CD:%d" % (ser.getCTS(),ser.getDSR(),ser.getRI(),ser.getCD())
    #ser.write("DEBUGON\r\n")
    #ser.write("DEBUGOFF\r\n")
    if (doCommand):
        print "Executing Command"
        ser.write("RLOGDUMP\r\n")
    #else:
    #    print "NOT Executing Command"
    #ser.write("LOGDUMP\r\n")
    zeroAttempts=0
    while True:
        #resp = ser.read(4096)
        inWaiting = ser.inWaiting()
        while (False and inWaiting>50 and inWaiting<500):
            print "|W|=%d " % (inWaiting)
            time.sleep(.1)
            inWaiting = ser.inWaiting()

        resp = ser.readline()
        #print "last 2 |||%s|||" % (resp[-2:])
        resp = resp[:-1] # maybe 2?
        if (len(resp)==0):
            zeroAttempts+=1
        if (zeroAttempts>0):
            break
        print "|W|=%4d |Response|=%4d : %s" % (inWaiting,len(resp),resp)
        #time.sleep(0.2)
    #print "CTS:%s DSR:%s RI:%s CD:%d" % (ser.getCTS(),ser.getDSR(),ser.getRI(),ser.getCD())
    ser.close()
    print "Exited loop"
    #time.sleep(1)
    
if __name__ == "__main__":
        usage = 'python %s  ( --duration <secs> | --forever) [--device /dev/ttyXXXX]' % sys.argv[0]

        try:
                opts, args = getopt.getopt(sys.argv[1:], "", ["duration=", "forever", "device="])
        except getopt.error, msg:
                logError('error msg: %s' % msg)
                logError(usage)
                sys.exit(2)

        # default value (forever-> duration=-1
        duration=1
        #default value /dev/ttyUSB0
        device = "/dev/ttyUSB1"

        for o, a in opts:
                if o == "--duration":
                        duration = string.atol(a)
                elif o == "--forever":
                        duration = -1
                elif o == "--device":
                        device = a

        print "duration is %d" % duration
        start = time.time()


        oneIteration(device,True)
        while True:
            oneIteration(device,False)

        sys.exit(0)
        
        while True:
                datetimenow = datetime.datetime.now()
                now=time.time()
                if duration>0 and (now-start)>duration:
                        break

                oneIteration(device)

                now=time.time()
                if duration>0 and (now-start)>duration:
                        break

                # sleep to hit the second on the nose:
                (frac,dummy) = math.modf(now)
                desiredFractionalOffset = .1
                delay = 1-frac + desiredFractionalOffset
                time.sleep(delay)

print "Done; lasted %f" % (time.time()-start)


