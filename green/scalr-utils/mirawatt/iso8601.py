#!/usr/bin/env python
#
# this is a test module for parsing/formatting iso8601 datetimes
#
# let us start with speed:
# compare strptime vs RE
# on cantor: noop:0.4usec strptime:62usec regexp:9usec
# on darwin: noop:0.2usec strptime:39usec regexp:7usec

import datetime
import time # strptime not in datetime before 2.5
import re

TESTDATE='2009-10-08T01:04:53Z'
TESTDATE2='2009-10-08T01:04:53.456Z'
ISO_DATE_FORMAT_Z = '%Y-%m-%dT%H:%M:%SZ'

def test_noop():
    pass

def test_strlen():
    length = len(TESTDATE)

def test_strptime():
    #return datetime.datetime.strptime(TESTDATE,ISO_DATE_FORMAT_Z)
    return time.strptime(TESTDATE,ISO_DATE_FORMAT_Z)
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
    match = pattern.match(TESTDATE2)
    if (match):
        groups = match.groupdict()
        return groups
    else:
        return "No Match"

if __name__ == "__main__":
    print "Testing iso8601 Datetimes"
    print "strptime: %s" % test_strptime()
    print "  regexp: %s" % test_regexp()
    number=100000
    repeat=3
    from timeit import Timer
    for tname in ['noop','strlen','strptime','regexp']:
        t = Timer("test_%s()"%tname, "from __main__ import test_%s"%tname)
        results = t.repeat(repeat=repeat,number=number)
        for r in results:
            print "%10s %.2f usec/pass" % (tname,(1000000 * r/number))
        
