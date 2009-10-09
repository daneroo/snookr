#!/usr/bin/env python
#
# this is a test module for parsing/formatting iso8601 datetimes
#
# We will build a timezone aware datetime object with FixedOffset
# and provide functions for casting to local and UTC timezones
import datetime
import time # strptime not in datetime before 2.5
import calendar
import re
import os
import math

TESTDATE=    '2009-10-08T01:04:53Z'
TESTDATE=    '2009-10-08T01:04:53,456-0500'
TESTDATEFRAC='2009-10-08T01:04:53.456Z'
ISO_DATE_FORMAT_Z =   '%Y-%m-%dT%H:%M:%SZ'
ISO_DATE_FORMAT_NOZ = '%Y-%m-%dT%H:%M:%S'

class iso8601:
    """
    A class for manipulating iso 8601 datetimes
    utc: naive datetime object
    -everything happens in the constructor or on demand
    instance variables:
        - utc: datetime.datetime tuple for UTC date
        - timestamp: unix timestamp, seconds since epoch in UTC
        - localSecondsOffset: local timezone offset in seconds
        - local: datetime.datetime tuple for local timezone
    """
    def __init__(self,stampStr):    
        self.utc = self.parse(stampStr)
        #self.setTimestamp()
        #self.setLocalAndSecondsOffset()
        #self.lcl = "ZZ"#self.getLocalStr()

    def __str__(self):
        return "%s" % (self.utc)
        return "%s : %f : %s" % (self.utc,self.timestamp,self.lcl)
    
    # use raw strings r"..": which do not escape \'s
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

    isopattern = re.compile(DATE_RE+TIME_RE+TZ_RE)

    def parse(self,stampStr):
        '''
        Parse an iso 8601 string and return a naive datetime object
        representing the UTC tuple for the passed datetime representation
        TODO: parse seperators,timezone and manipulate datetime tuple
        '''
        match = self.isopattern.match(stampStr)
        if (match):
            g = match.groupdict()
            #print g
            # seconds rounded to int even thoug RE allows fractional seconds
            # modf return frac+integer parts as floats
            float_secs = float(g['second'].replace(',','.') ) or 0
            (frac,secs) = math.modf( float_secs )
            microseconds = 1000000 * frac
            tz = getFixedOffset(int(g['tzhour'] or 0),int(g['tzmin'] or 0))
            #tz = FixedOffset(int(g['tzhour'] or 0),int(g['tzmin'] or 0))

            tuple_date = datetime.datetime(int(g['year']), int(g['month']), int(g['day']), int(g['hour']), int(g['minute']), int(secs),int(microseconds),tz)
            # TODO add offset, use timedelta...
            #tuple_date = tuple_date.replace(tzinfo=tz)
            #print "%s : %s" % (tuple_date,tuple_date.tzinfo)
            return tuple_date
        else:
            return None

    def setTimestamp(self):
        self.timestamp = calendar.timegm(self.utc.timetuple())

    def setLocalAndSecondsOffset(self):
        self.local_tm = time.localtime(self.timestamp)
        # Don't reference time.altzone unless time.daylight is set.
        # localSecondsOffset's sign is reversed w.r.t timezon/altzone
        self.localSecondsOffset = -time.timezone
        if (time.daylight and self.local_tm.tm_isdst):
            self.localSecondsOffset = -time.altzone
        self.localSecondsOffset = 16200+3600

    def getLocalStr(self):
        NOZStr = time.strftime(ISO_DATE_FORMAT_NOZ, self.local_tm)
        # modf's return argument both carry sign of parameter
        (hours_frac_float,hours_float) = math.modf(self.localSecondsOffset/3600.0)
        hourOffset = int(hours_float)
        minuteOffset = abs(int(hours_frac_float*60))
        return "%s%+03d%02d" % (NOZStr,hourOffset,minuteOffset)

#####################################################################################
'''
This module provides some datetime.tzinfo implementations.
'''
from datetime import timedelta, tzinfo
import time

# Constants defined at end of this file:
#   UTC = FixedOffset(0,0,"UTC")
#   LocalTZ = LocalTimezone()

# constant for zero time offset.
ZERO = timedelta(0)

fixedOffsetCache={}
def getFixedOffset(offset_hours, offset_minutes):
    key = "GMT%+03d%2d" % (offset_hours,offset_minutes)
    if (key in fixedOffsetCache):
        return fixedOffsetCache[key]
    newinstance = FixedOffset(offset_hours, offset_minutes)
    fixedOffsetCache[key] = newinstance
    return newinstance

class FixedOffset(tzinfo):
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
        self.__offset = timedelta(hours=offset_hours, minutes=offset_minutes)
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



class LocalTimezone(tzinfo):
    # A class capturing the platform's idea of local time.
    # locale time zone offset
    STDOFFSET = timedelta(seconds = -time.timezone)

    # calculate local daylight saving offset if any.
    if time.daylight:
        DSTOFFSET = timedelta(seconds = -time.altzone)
    else:
        DSTOFFSET = STDOFFSET

    DSTDIFF = DSTOFFSET - STDOFFSET
    # difference between local time zone and local DST time zone

    def utcoffset(self, dt):
        #Return offset from UTC in minutes of UTC.
        if self._isdst(dt):
            return DSTOFFSET
        else:
            return STDOFFSET

    def dst(self, dt):
        #Return daylight saving offset.
        if self._isdst(dt):
            return DSTDIFF
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

def test_construct():
    return iso8601(TESTDATE)

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

    # Don't reference time.altzone unless time.daylight is set.
    localSecondsOffset = time.timezone
    if (time.daylight and struct_tmL.tm_isdst):
        localSecondsOffset = time.altzone

    # localSecondsOffset's sign is reversed..
    sign = "+" 
    if (localSecondsOffset>0): sign = "-"
    hourOffset = int(localSecondsOffset)/3600
    minuteOffset = (int(localSecondsOffset) % 3600)/60
    return "%s%s%02d%02d" % (NOZStr,sign,hourOffset,minuteOffset)

def test_localfromstrp():
    struct_tmZ = time.strptime(TESTDATE,ISO_DATE_FORMAT_Z)
    tuple6 = struct_tmZ[:6]
    return tuple_to_iso8601local(tuple6)
    pass

def test_localre():
    g = test_regexp()
    # mapping by hand is faster than map(to_int,tuple6Str)
    tuple6 = [ int(g['year']), int(g['month']), int(g['day']), int(g['hour']), int(g['minute']), int(float(g['second'].replace(',','.'))) ]
    return tuple_to_iso8601local(tuple6)
    pass

def test_regexp():
    match = iso8601.isopattern.match(TESTDATE)
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

    
    #print " strptime: %s" % test_strptime()
    #print "   regexp: %s" % test_regexp()
    #print "  tpl-iso: %s" % test_tupletoiso8601()
    #print "  local-p: %s" % test_localfromstrp()
    #print " local-re: %s" % test_localre()
    print "  constr: %s" % test_construct()

    number=1000
    repeat=1
    from timeit import Timer
    #for tname in ['noop','strlen','strptime','regexp','tupletoiso8601','localfromstrp','localre']:
    for tname in ['noop','regexp','tupletoiso8601','localre','construct']:
        t = Timer("test_%s()"%tname, "from __main__ import test_%s"%tname)
        results = t.repeat(repeat=repeat,number=number)
        for r in results:
            print "%20s %.2f usec/pass" % (tname,(1000000 * r/number))
        

    dt = iso8601(TESTDATE).utc
    print "date %s -> %s" %(TESTDATE,dt);
    local = dt.astimezone(LocalTZ)
    print "  local -> %s" %(local);
    gmt = dt.astimezone(UTC)
    print "    gmt -> %s" %(gmt);

