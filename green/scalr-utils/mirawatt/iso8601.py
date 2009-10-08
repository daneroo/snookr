#!/usr/bin/env python
#
# this is a test module for parsing/formatting iso8601 datetimes
#
# let us start with speed:
# compare strptime vs RE
# on cantor:      noop:0.4usec strptime:62usec regexp:9usec
# on darwin:      noop:0.2usec strptime:39usec regexp:7usec
# on miraplug001: noop:1.6usec strptime:800usec regexp:117usec
#
# now lets look at timezone REQUIREMENTS
#  we will alway work with unambiguous representations
# i.e. Fixed Offset representations UTC or -0400 or +0500
# but we have the need for some localtime operations:
#  i.e. aggregation: startOfDay based on localtime
#
#  IDEA use only naive datetime objects, or FixedOffset
# but find normalized localtime representation
# we can use time.localtime vs time.gmtime to do this.

import datetime
import time # strptime not in datetime before 2.5./is
import calendar
import re
import os

TESTDATE='2009-10-08T01:04:53Z'
TESTDATEFRAC='2009-10-08T01:04:53.456Z'
ISO_DATE_FORMAT_Z = '%Y-%m-%dT%H:%M:%SZ'
ISO_DATE_FORMAT_NOZ = '%Y-%m-%dT%H:%M:%S'

def test_noop():
    pass

def test_strlen():
    length = len(TESTDATE)

def test_strptime():
    return time.strptime(TESTDATE,ISO_DATE_FORMAT_Z)
    pass

def test_tupletoiso8601():
    return tuple_to_iso8601local((2009, 10, 8, 1, 4, 53))

def tuple_to_iso8601local(tuple6):
    unix_stamp_gmt = calendar.timegm(tuple6)
    struct_tmL = time.localtime(unix_stamp_gmt)

    NOZStr = time.strftime(ISO_DATE_FORMAT_NOZ, struct_tmL)
    # this is actually slower than strftime !?
    #tt = struct_tmL
    #NOZStr = "%04d-%02d-%02dT%02d:%02d:%02d" % (tt.tm_year,tt.tm_mon,tt.tm_mday,tt.tm_hour,tt.tm_min,tt.tm_sec)

    #localSecondsOffset = (time.timezone,time.altzone)[struct_tmL.tm_isdst] # or [8]
    # don't reference time.altzone unless time.daylight is set.
    localSecondsOffset = time.timezone
    if (time.daylight and struct_tmL.tm_isdst):
        localSecondsOffset = time.altzone

    sign = "+" if localSecondsOffset<0 else "-"
    hourOffset = int(localSecondsOffset)/3600
    minuteOffset = (int(localSecondsOffset) % 3600)/60
    return "%s%s%02d%02d" % (NOZStr,sign,hourOffset,minuteOffset)

def test_localfromstrp():
    struct_tmZ = time.strptime(TESTDATE,ISO_DATE_FORMAT_Z)
    tuple6 = struct_tmZ[:6]
    return tuple_to_iso8601local(tuple6)
    pass

def to_int(x): return int(float(x))

def test_localre():
    g = test_regexp()
    #tuple6str = (g['year'],g['month'],g['day'],g['hour'],g['minute'],g['second'])
    #tuple6 = map(to_int,tuple6str)
    tuple6 = [ int(g['year']), int(g['month']), int(g['day']), int(g['hour']), int(g['minute']), int(float(g['second'])) ]
    #return tuple6
    return tuple_to_iso8601local(tuple6)
    pass

# use raw strings r"..": which do not escape \'s
pattern = re.compile(r"(?P<year>[0-9]{4})-"
                     r"(?P<month>[0-9]{2})-"
                     r"(?P<day>[0-9]{2})"
                     r"T?"
                     r"(?P<hour>[0-9]{2}):"
                     r"(?P<minute>[0-9]{2}):"
                     r"(?P<second>[0-9]{2}([,.][0-9]+)?)"
                     r"Z"
                     )
def test_regexp():
    match = pattern.match(TESTDATE)
    if (match):
        groups = match.groupdict()
        return groups
    else:
        return {'error':'No Match'}

if __name__ == "__main__":
    print "Testing iso8601 Datetimes"
    print "time module timezones:"
    print "time.tzname: %s" % ('|'.join(time.tzname))
    print "time.timezone: %s" % (time.timezone)
    print "time.altzone: %s" % (time.altzone)
    print "time.daylight: %s" % (time.daylight)
    # timemodule's C implementation is based on localtime syscall
    #print "os.environ['TZ']: %s" % (os.environ['TZ'])
    # os.environ['TZ'] = 'US/Eastern'
    # os.environ['TZ'] = 'EST+05EDT,M4.1.0,M10.5.0'
    # os.environ['TZ'] = 'AEST-10AEDT-11,M10.5.0,M3.5.0'

    
    print " strptime: %s" % test_strptime()
    print "   regexp: %s" % test_regexp()
    print "  tpl-iso: %s" % test_tupletoiso8601()
    print "  local-p: %s" % test_localfromstrp()
    print " local-re: %s" % test_localre()

    number=1000
    repeat=2
    from timeit import Timer
    for tname in ['noop','strlen','strptime','regexp','tupletoiso8601','localfromstrp','localre']:
        t = Timer("test_%s()"%tname, "from __main__ import test_%s"%tname)
        results = t.repeat(repeat=repeat,number=number)
        for r in results:
            print "%20s %.2f usec/pass" % (tname,(1000000 * r/number))
        
