#!/usr/bin/env python
#
# this is a test module for parsing/formatting iso8601 datetimes
#
# We will build a timezone aware datetime object with FixedOffset
# and provide functions for casting to local and UTC timezones
import datetime
import time
import re
import math

# TIMING
# all time in microseconds/call
#      noop: reference timing loop just f-call
#    regexp: time to parse iso8601 with Regexp: precompiled pattern
#     parse: time to parse and construct datetime object with timezone
# on cantor:      noop:0.40   regexp: 13  parse: 41.16
# on darwin:      noop:0.16   regexp:  6  parse: 17.95
# on miraplug001: noop:1.60   regexp:140  parse:595.59


######## ISO 8601 Regular Expression ########################
# Date seperator is '-', it is optional,
#   But must match: 20080901|2008-09-01 but not 2008-0901 and not 200809-01
DATE_RE = (r"(?P<year>[0-9]{4})(?P<DS>(-?))"
           r"(?P<month>[0-9]{2})(?P=DS)"
           r"(?P<day>[0-9]{2})")
# Date-Time seperator is T or <space>
# Time seperator is ':', it is optional,
#   But must match: 23:45:01|234501 but not 23:4501 and not 2345:01
TIME_RE = (r"(T| )"
           r"(?P<hour>[0-9]{2})(?P<TS>(:?))"
           r"(?P<minute>[0-9]{2})(?P=TS)"
           r"(?P<second>[0-9]{2}([,.][0-9]+)?)")
# TZ is required Z
TZ_RE = (r"(?P<tzname>(Z|"
         r"(?P<tzhour>[+-][0-9]{2})((:?)(?P<tzmin>[0-9]{2}))?))")

iso8601pattern = re.compile(DATE_RE+TIME_RE+TZ_RE)

def parse(stampStr):
    '''
    Parse an iso 8601 string and return a tzinfo aware datetime object
    '''
    match = iso8601pattern.match(stampStr)
    if (match):
        g = match.groupdict()
        # seconds may be fractional and may contain ',' instead of '.' as decimal separator
        float_secs = float(g['second'].replace(',','.') ) or 0
        (frac,secs) = math.modf( float_secs )
        microseconds = 1000000 * frac
        tz = getFixedOffset(int(g['tzhour'] or 0),int(g['tzmin'] or 0))

        tzAwareDateTime = datetime.datetime(int(g['year']), int(g['month']), int(g['day']), int(g['hour']), int(g['minute']), int(secs),int(microseconds),tz)
        return tzAwareDateTime
    else:
        return None


#####################################################################################
#  This section provides some datetime.tzinfo implementations.
# Constants defined at end of this file:
#   UTC = FixedOffset(0,0,"UTC")
#   LocalTZ = LocalTimezone()

# constant for zero time offset.
ZERO = datetime.timedelta(0)

fixedOffsetCache={}
def getFixedOffset(offset_hours, offset_minutes):
    key = "GMT%+03d%2d" % (offset_hours,offset_minutes)
    if (key in fixedOffsetCache):
        return fixedOffsetCache[key]
    newinstance = FixedOffset(offset_hours, offset_minutes)
    fixedOffsetCache[key] = newinstance
    return newinstance

class FixedOffset(datetime.tzinfo):
    '''
    A class building tzinfo objects for fixed-offset time zones.
    Note that FixedOffset(0, "UTC") is a different way to build a
    UTC tzinfo object.
    '''

    def __init__(self, offset_hours, offset_minutes, name=None):
        '''
        Initialise an instance with time offset and name.
        The time offset should be positive for time zones east of UTC
        and negate for time zones west of UTC.
        '''
        self.__offset = datetime.timedelta(hours=offset_hours, minutes=offset_minutes)
        if (not name):
            if (offset_minutes!=0):
                name = "GMT%+03d%2d" % (offset_hours,offset_minutes)
            else:
                name = "GMT%+03d" % (offset_hours)
        self.__name = name

    def utcoffset(self, dt):
        '''
        Return offset from UTC in minutes of UTC.
        '''
        return self.__offset

    def tzname(self, dt):
        '''
        Return the time zone name corresponding to the datetime object dt, as a
        string.
        '''
        return self.__name

    def dst(self, dt):
        '''
        Return the daylight saving time (DST) adjustment, in minutes east of
        UTC.
        '''
        return ZERO

    def __repr__(self):
        '''
        Return nicely formatted repr string.
        '''
        return "<FixedOffset %r>" % self.__name



class LocalTimezone(datetime.tzinfo):
    # A class capturing the platform's idea of local time.
    # locale time zone offset
    STDOFFSET = datetime.timedelta(seconds = -time.timezone)

    # calculate local daylight saving offset if any.
    DSTOFFSET = STDOFFSET
    if time.daylight:
        DSTOFFSET = datetime.timedelta(seconds = -time.altzone)

    DSTDIFF = DSTOFFSET - STDOFFSET
    # difference between local time zone and local DST time zone

    def utcoffset(self, dt):
        #Return offset from UTC in minutes of UTC.
        if self._isdst(dt):
            return self.DSTOFFSET
        else:
            return self.STDOFFSET

    def dst(self, dt):
        #Return daylight saving offset.
        if self._isdst(dt):
            return self.DSTDIFF
        else:
            return ZERO

    def tzname(self, dt):
        # Return the time zone name corresponding to the datetime object dt, as a string.
        return time.tzname[self._isdst(dt)]

    def _isdst(self, dt):
        # Returns true if DST is active for given datetime object dt.
        tt = (dt.year, dt.month, dt.day,
              dt.hour, dt.minute, dt.second,
              dt.weekday(), 0, -1)
        stamp = time.mktime(tt)
        tt = time.localtime(stamp)
        return tt.tm_isdst > 0

# the default instance for UTC.
UTC = FixedOffset(0,0,"UTC")
# the default instance for local time zone.
LocalTZ = LocalTimezone()

##################################################################################

TESTDATE=    '2009-10-08T01:04:53Z'
TESTDATE=    '2009-12-08T01:04:53,456-0500'
TESTDATEFRAC='2009-10-08T01:04:53.456Z'
ISO_DATE_FORMAT_Z =   '%Y-%m-%dT%H:%M:%SZ'
ISO_DATE_FORMAT_NOZ = '%Y-%m-%dT%H:%M:%S'

def test_construct():
    return parse(TESTDATE)

def test_noop():
    pass

def test_regexp():
    match = iso8601pattern.match(TESTDATE)
    if (match):
        groups = match.groupdict()
        return groups
    else:
        return {'error':'No Match'}

if __name__ == "__main__":
    if (False):
        parseTest('20091008 01:04:53Z')
        # DS:Date Separator must be  consitent
        #parseTest('200910-08 01:04:53Z')
        parseTest('2009-10-08 01:04:53Z')
        parseTest('2009-10-08T01:04:53Z')
        parseTest('2009-10-08T01:04:53+0400')
        parseTest('2009-10-08T01:04:53+0400')
        #parseTest('2009-10-08T01:04:53')
        parseTest('2009-10-08T010453Z')
        parseTest('2009-10-08T010453-0400')
        parseTest('2009-10-08T010453-04')

    if (False):
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

    
    print "  constr: %s" % test_construct()

    number=1000
    repeat=1
    from timeit import Timer
    for tname in ['noop','regexp','construct']:
        t = Timer("test_%s()"%tname, "from __main__ import test_%s"%tname)
        results = t.repeat(repeat=repeat,number=number)
        for r in results:
            print "%20s %.2f usec/pass" % (tname,(1000000 * r/number))
        

    dt = parse(TESTDATE)
    print "date %s -> %s" %(TESTDATE,dt);
    local = dt.astimezone(LocalTZ)
    print "  local -> %s" %(local);
    gmt = dt.astimezone(UTC)
    print "    gmt -> %s" %(gmt);

    # try Formatting with strftime
    ISO8601BASIC    = '%Y%m%dT%H%M%S%z'
    ISO8601EXTENDED = '%Y-%m-%dT%H:%M:%S%z'
    print "reformated Basic    %s -> %s" % (local,local.strftime(ISO8601BASIC))
    print "reformated Extended %s -> %s" % (local,local.strftime(ISO8601EXTENDED))
