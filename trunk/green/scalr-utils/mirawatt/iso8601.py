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

def parse_iso8601(stampStr):
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


#-------- Formatting -----
# The point here is to combine
#   -specify seperators, Date,Time,TZ,
#   -include/omit 'T'
#   -special Case 'Z' for UTC (-0) offset
ISO8601BASICFMT     = '%Y%m%dT%H%M%S%z'
ISO8601EXTENDEDFMT  = '%Y-%m-%dT%H:%M:%S%z'

def fmtBasic(dt): # basicFormating YYYYMMDDTHHMMSS+zzzz
    return dt.strftime(ISO8601BASICFMT)
def fmtExtended(dt): # extended Formating YYYY-MM-DD-THH:MM:SS+zzzz
    return dt.strftime(ISO8601EXTENDEDFMT)
def fmtBasicZ(dt): # basicFormating YYYYMMDDTHHMMSS(Z|+zzzz)
    if (dt.tzinfo.utcoffset(dt)==ZERO):
        ISO8601BASICFMTZ     = '%Y%m%dT%H%M%SZ' # only for UTC | +0000
        return dt.strftime(ISO8601BASICFMTZ)
    return dt.strftime(ISO8601BASICFMT)
def fmtExtendedZ(dt): # extended Formating YYYY-MM-DD-THH:MM:SS(Z|+zzzz)
    if (dt.tzinfo.utcoffset(dt)==ZERO):
        ISO8601EXTENDEDFMTZ = '%Y-%m-%dT%H:%M:%SZ' # only for UTC | +0000
        return dt.strftime(ISO8601EXTENDEDFMTZ)
    return dt.strftime(ISO8601EXTENDEDFMT)

#-------- TZ Conversion shortcuts
def toLocalTZ(dt):
    return dt.astimezone(LocalTZ)
def toUTC(dt):
    return dt.astimezone(UTC)

#####################################################################################
#  This section provides some datetime.tzinfo implementations.

# constant for zero time offset.
ZERO = datetime.timedelta(0)

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

    def __init__(self):
        self.STDOFFSET = datetime.timedelta(seconds = -time.timezone)
        # calculate local daylight saving offset if any.
        self.DSTOFFSET = self.STDOFFSET
        if time.daylight:
            self.DSTOFFSET = datetime.timedelta(seconds = -time.altzone)
        # difference between local time zone and local DST time zone
        self.DSTDIFF = self.DSTOFFSET - self.STDOFFSET

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
fixedOffsetCache={}
def getFixedOffset(offset_hours, offset_minutes=0):
    key = "GMT%+03d%2d" % (offset_hours,offset_minutes)
    if (key in fixedOffsetCache):
        return fixedOffsetCache[key]
    newinstance = FixedOffset(offset_hours, offset_minutes)
    fixedOffsetCache[key] = newinstance
    return newinstance

# the default instance for local time zone.
LocalTZ = LocalTimezone()

##################################################################################

TESTDATE=    '2009-10-08T01:04:53Z'
TESTDATE=    '2009-12-08T01:04:53,456-0500'
TESTDATEFRAC='2009-10-08T01:04:53.456Z'
ISO_DATE_FORMAT_Z =   '%Y-%m-%dT%H:%M:%SZ'
ISO_DATE_FORMAT_NOZ = '%Y-%m-%dT%H:%M:%S'

def test_construct():
    return parse_iso8601(TESTDATE)

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
    '''Test cases for the isodatetime module.'''
    import unittest

    PARSE_TEST_CASES = [
                ('19660516T010203Z',          datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), "basic date and time tz=Z" ),
                ('19660516T010203+0000',      datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), "basic date and time tz=0" ),
                ('1966-05-16T01:02:03Z',      datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), 'extended date and time tz=Z' ),
                ('1966-05-16T01:02:03-0400',  datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=getFixedOffset(-4)), 'extended date and time tz=-4' ),
                ('1966-05-16T01:02:03-04:00', datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=getFixedOffset(-4)), 'extended date and time tz=-4' ),
                # repeat with <space> Date-Time sep
                ('19660516 010203Z',          datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), "basic date and time tz=Z noT" ),
                ('19660516 010203+000',       datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), "basic date and time tz=0 noT" ),
                ('1966-05-16 01:02:03Z',      datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), 'extended date and time tz=Z noT' ),
                ('1966-05-16 01:02:03-0400',  datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=getFixedOffset(-4)), 'extended date and time tz=-4 noT' ),
                ('1966-05-16 01:02:03-04:00', datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=getFixedOffset(-4)), 'extended date and time tz=-4 noT' ),
                # how about some failing tests
                ('NOT DATE AT ALL',None,'Not date at all!'),
                ('2001-01-02',None,'Just Date should fail!'),
                ('2001-01-02T23:45:01',None, 'Missing TZ should fail!'),
                ('2001-0102T23:45:01', None, 'Unmatched DS:Date Seperator should fail!'),
                ('200101-02T23:45:01', None, 'Unmatched DS:Date Seperator should fail!'),
                ('2001-01-02T2345:01', None, 'Unmatched TS:Time Seperator should fail!'),
                ('2001-01-02T23:4501', None, 'Unmatched TS:Time Seperator should fail!'),
                # fractional seconds
                ('1966-05-16T01:02:03.123Z',      datetime.datetime(1966, 5, 16,  1,  2, 3,123000, tzinfo=UTC               ), 'fractional seconds tz=Z' ),
                ('1966-05-16T01:02:03.123-04:00', datetime.datetime(1966, 5, 16,  1,  2, 3,123000, tzinfo=getFixedOffset(-4)), 'fractional seconds tz=-4' ),
                ('1966-05-16T01:02:03,123Z',      datetime.datetime(1966, 5, 16,  1,  2, 3,123000, tzinfo=UTC               ), 'fractional seconds w -,- tz=Z' ),
                ('1966-05-16T01:02:03,123-04:00', datetime.datetime(1966, 5, 16,  1,  2, 3,123000, tzinfo=getFixedOffset(-4)), 'fractional seconds w -,- tz=-4' ),
                ('1966-05-16T01:02:03.0Z',        datetime.datetime(1966, 5, 16,  1,  2, 3,0, tzinfo=UTC               ), 'fractional seconds range-0' ),
                ('1966-05-16T01:02:03.1Z',        datetime.datetime(1966, 5, 16,  1,  2, 3,100000, tzinfo=UTC), 'fractional seconds precision-0.1' ),
                # these two fail with 1 usec offset? 999!=1000, and 9999!=10000
                #('1966-05-16T01:02:03.01Z',       datetime.datetime(1966, 5, 16,  1,  2, 3, 10000, tzinfo=UTC), 'fractional seconds precision-0.01' ),
                #('1966-05-16T01:02:03.001Z',      datetime.datetime(1966, 5, 16,  1,  2, 3,  1000, tzinfo=UTC), 'fractional seconds precision-0.001' ),
                ('1966-05-16T01:02:03.0001Z',     datetime.datetime(1966, 5, 16,  1,  2, 3,   100, tzinfo=UTC), 'fractional seconds precision-0.0001'),
                ]
    def create_parse_testcase(datetimestring, expectation,msg):
        '''TestCase Factory - test case template to validate iso8601 parsing'''
        class TestDateTime(unittest.TestCase):
            def test_parse(self):
                result = parse_iso8601(datetimestring)
                self.assertEqual(result, expectation,msg)
        return unittest.TestLoader().loadTestsFromTestCase(TestDateTime)

    TZCONV_TEST_CASES = [ # test conversion to UTC and LocalTime (assume on America/Montreal Machine!
                ('19660516T010203Z',          datetime.datetime(1966, 5, 16,  1,  2, 3, tzinfo=UTC), "basic date and time tz=Z" ),
                ]

    def create_tzconvert_testcase(datetimestring, expectation,msg):
        '''TestCase Factory - test case template to validate iso8601 parsing'''
        class TestDateTime(unittest.TestCase):
            def test_parse(self):
                result = parse_iso8601(datetimestring)
                self.assertEqual(result, expectation,msg)
        return unittest.TestLoader().loadTestsFromTestCase(TestDateTime)

    def test_suite():
        '''Construct a TestSuite instance for all test cases.'''
        suite = unittest.TestSuite()
        for datetimestring, expectation, msg in PARSE_TEST_CASES:
            suite.addTest(create_parse_testcase(datetimestring, expectation, None))
        return suite

    #unittest.main(defaultTest='test_suite')

    
    print "  constr: %s" % test_construct()

    number=1000
    repeat=1
    from timeit import Timer
    for tname in ['noop','regexp','construct']:
        t = Timer("test_%s()"%tname, "from __main__ import test_%s"%tname)
        results = t.repeat(repeat=repeat,number=number)
        for r in results:
            print "%20s %.2f usec/pass" % (tname,(1000000 * r/number))
        

    dt = parse_iso8601(TESTDATE)
    print "date %s -> %s" %(TESTDATE,dt);
    local = toLocalTZ(dt) #== dt.astimezone(LocalTZ)
    print "  local -> %s" %(local)
    gmt = toUTC(dt) # == dt.astimezone(UTC)
    print "    gmt -> %s" %(gmt);

    # try Formatting with strftime
    print "reformated Basic    %s -> %s" % (local,fmtBasic(local))
    print "reformated Extended %s -> %s" % (local,fmtExtended(local))
    print "reformated Basic    %s -> %s" % (gmt,fmtBasic(gmt))
    print "reformated Extended %s -> %s" % (gmt,fmtExtended(gmt))
    print "reformated BasicZ    %s -> %s" % (local,fmtBasicZ(local))
    print "reformated ExtendedZ %s -> %s" % (local,fmtExtendedZ(local))
    print "reformated BasicZ    %s -> %s" % (gmt,fmtBasicZ(gmt))
    print "reformated ExtendedZ %s -> %s" % (gmt,fmtExtendedZ(gmt))


    if (True):
        import os
        for tzStr in ['US/Eastern','Canada/Newfoundland','Asia/Hong_Kong','Egypt',None]:
            if (tzStr):
                os.environ['TZ'] = tzStr
            else:
                del os.environ['TZ']
            time.tzset()
            # Reset our global variable!
            newZone = LocalTimezone()
            for dtStr in ['2009-01-01T12:34:56Z','2009-06-01T12:34:56Z']:
                dt = toUTC(parse_iso8601(dtStr))
                loc = dt.astimezone(newZone)
                utcBack = toUTC(loc)
                print "%20s %16s : %s -> %s -> %s" % (os.getenv('TZ'),time.tzname,dt,loc,utcBack)
