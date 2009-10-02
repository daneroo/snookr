#!/usr/bin/env python
#

import wsgiref.handlers
import logging
import base64
import urllib
import urllib2
import math

from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.api import memcache

from xml.dom import minidom, Node
from datetime import tzinfo, timedelta, datetime

logging.getLogger().setLevel(logging.DEBUG)


class Feeds(db.Model):
  owner   = db.StringProperty()
  content = db.TextProperty()
  stamp   = db.DateTimeProperty()

ZERO = timedelta(0)
HOUR = timedelta(hours=1)

class UTC(tzinfo):
    """UTC"""

    def utcoffset(self, dt):
        return ZERO

    def tzname(self, dt):
        return "UTC"

    def dst(self, dt):
        return ZERO

UTC_TZINFO = UTC()

def first_sunday_on_or_after(dt):
    days_to_go = 6 - dt.weekday()
    if days_to_go:
        dt += timedelta(days_to_go)
    return dt

# In the US, DST starts at 2am (standard time) on the first Sunday in April.
DSTSTART = datetime(1, 4, 1, 2)
# and ends at 2am (DST time; 1am standard time) on the last Sunday of Oct.
# which is the first Sunday on or after Oct 25.
DSTEND = datetime(1, 10, 25, 1)

class USTimeZone(tzinfo):

    def __init__(self, hours, reprname, stdname, dstname):
        self.stdoffset = timedelta(hours=hours)
        self.reprname = reprname
        self.stdname = stdname
        self.dstname = dstname

    def __repr__(self):
        return self.reprname

    def tzname(self, dt):
        if self.dst(dt):
            return self.dstname
        else:
            return self.stdname

    def utcoffset(self, dt):
        return self.stdoffset + self.dst(dt)

    def dst(self, dt):
        if dt is None or dt.tzinfo is None:
            # An exception may be sensible here, in one or both cases.
            # It depends on how you want to treat them.  The default
            # fromutc() implementation (called by the default astimezone()
            # implementation) passes a datetime with dt.tzinfo is self.
            return ZERO
        assert dt.tzinfo is self

        # Find first Sunday in April & the last in October.
        start = first_sunday_on_or_after(DSTSTART.replace(year=dt.year))
        end = first_sunday_on_or_after(DSTEND.replace(year=dt.year))

        # Can't compare naive to aware objects, so strip the timezone from
        # dt first.
        if start <= dt.replace(tzinfo=None) < end:
            return HOUR
        else:
            return ZERO

Eastern  = USTimeZone(-5, "Eastern",  "EST", "EDT")
Central  = USTimeZone(-6, "Central",  "CST", "CDT")
Mountain = USTimeZone(-7, "Mountain", "MST", "MDT")
Pacific  = USTimeZone(-8, "Pacific",  "PST", "PDT")


class MainPage(webapp.RequestHandler):
  def get(self):
    #self.redirect("/iG/googleviz-fb.html");
    self.redirect("/s/p/www-6bc.html");
    #count = db.GqlQuery("SELECT * FROM Feeds").count()
    #self.response.out.write("""Hello iMetrical World!
    #  I have retreived %d records
    # """ % count)

class PublishPage(webapp.RequestHandler):
  """  To publish content:
   alternat url http://localhost:8082/post
  while true; do \
    curl -o tmp.xml http://192.168.5.2/iMetrical/feeds.php; \
    curl -F "owner=daniel" -F "content=@tmp.xml;type=text/xml" \
       http://imetrical.appspot.com/post; \
    sleep 1; \
  done
"""
  def post(self):
    owner = self.request.get("owner")
    keyName = owner
    content = db.Text(self.request.get("content"))
    stamp = datetime.now()

    # no need for this expense
    #testfeeds = Feeds.get_by_key_name(keyName)
    #if testfeeds is None: # new
    #    logging.info("New Feed entity for key/owner: %s", keyName)

    # get or create feed:
    #   get_or_insert will not update the fields if the entity exists
    #  feeds = Feeds.get_or_insert(keyName,owner=owner,content=content,stamp=stamp)
    feeds = Feeds.get_or_insert(keyName,owner=owner)
    feeds.content = content
    feeds.stamp = stamp
    feeds.put()

    lastRead = memcache.get("lastRead:%s" % owner)
    now = datetime.now()
    accessedSecondsAgo=-1;
    if (lastRead is not None):
        accessedSecondsAgo = (now-lastRead).seconds

    logging.info("Updated Feed entity: %s %s" % (feeds.key().name(),feeds.stamp))

    self.response.out.write("""Published for %s @ %s (%s) |content|=%d
""" % (feeds.owner,feeds.stamp,accessedSecondsAgo,len(feeds.content)))



class SubscribePage (webapp.RequestHandler):
  """ http://imetrical.appspot.com/feeds?owner=daniel
"""
  def adviseFriendfeed(self,owner,lastRead):
    logging.info("Advise of activity for %s @ %s" % (owner,lastRead))
    auth_nickname="imetrical"
    auth_key="unite369tumid"
    post_args = {"title": "iMetrical::Read by %s @ %s"%(owner,lastRead) }
    uri="/api/share"
    url_args={"format":"json"}

    # this is mostly from my patched friendfeed.py::_fetch
    args = urllib.urlencode(url_args)
    host = "friendfeed.com"
    url = "http://" + host + uri + "?" + args
    if post_args is not None:
      request = urllib2.Request(url, urllib.urlencode(post_args))
    else:
      request = urllib2.Request(url)
    if auth_nickname and auth_key:
      pair = "%s:%s" % (auth_nickname, auth_key)
      token = base64.b64encode(pair)
      request.add_header("Authorization", "Basic %s" % token)
    stream = urllib2.urlopen(request)
    data = stream.read()
    stream.close()
    logging.info("POST Reply: %s" % (data))

  def advisePublisher(self,owner):
    accessThreshold=600
    accessedSecondsAgo=accessThreshold
    previousLastRead = memcache.get("lastRead:%s" % owner)
    if (previousLastRead is not None):
      accessedSecondsAgo = (datetime.now()-previousLastRead).seconds

    lastRead = datetime.now()
    expireSeconds = 8 * 3600
    memcache.set("lastRead:%s" % owner, lastRead,expireSeconds)

    # Do we need to publish notification
    if ( accessedSecondsAgo >= accessThreshold ):
      self.adviseFriendfeed(owner,lastRead)

  def get(self):
    owner = self.request.get("owner")
    self.advisePublisher(owner)

    feeds = Feeds.get_by_key_name(owner)
    self.response.headers['Content-Type'] = "text/xml"
    if feeds and feeds.content:
      self.response.out.write(feeds.content)
    else:
      self.response.out.write("""<?xml version="1.0"?>
<!DOCTYPE plist PUBLIC "-//iMetrical//DTD OBSFEEDS 1.0//EN" "http://www.imetrica
l.com/DTDs/ObservationFeeds-1.0.dtd">
<feeds>
  <!-- feeds for %s is empty -->
</feeds>
""" % owner)


class GraphPage (webapp.RequestHandler):
  """ Produce a google Chart URL. (and post to friendfeed)
"""
  def publishChartToFriendfeed(self,title,chartURL):
    logging.info("Publish chart: %s" % (title))
    auth_nickname="imetrical"
    auth_key="unite369tumid"

    #post_args = {"title": title, "link":chartURL, "image0_url": chartURL }
    post_args = {"title": title, "image0_url": chartURL }
    uri="/api/share"
    url_args={"format":"json"}

    # this is mostly from my patched friendfeed.py::_fetch
    args = urllib.urlencode(url_args)
    host = "friendfeed.com"
    url = "http://" + host + uri + "?" + args
    if post_args is not None:
      request = urllib2.Request(url, urllib.urlencode(post_args))
    else:
      request = urllib2.Request(url)
    if auth_nickname and auth_key:
      pair = "%s:%s" % (auth_nickname, auth_key)
      token = base64.b64encode(pair)
      request.add_header("Authorization", "Basic %s" % token)
    stream = urllib2.urlopen(request)
    data = stream.read()
    stream.close()
    logging.info("POST Reply: %s" % (data))

  def eastern(self,gmtstring):
    # remove extraneous -400 in bd feeeds: 2009-09-10T23:34:56Z-400
    utc = datetime.strptime(gmtstring[:20], "%Y-%m-%dT%H:%M:%SZ").replace(tzinfo=UTC_TZINFO)
    eastern = utc.astimezone(Eastern)
    return eastern

  def get(self):
    owner = self.request.get("owner")


    if (owner):
        feeds = Feeds.get_by_key_name(owner)
        dom = minidom.parseString(feeds.content)
        feedRoot = dom.getElementsByTagName("feed")
        #for feed in feedRoot:
        #    name = feed.getAttribute("name")
        #    self.response.out.write("<br>name: %s" %( name ))

        # Hard Code Day feed
        day = feedRoot.item(2)

        decaWatts=[]
        HHs = []
        maxkW = 2
        for obs in day.getElementsByTagName("observation"):
            stamp = obs.getAttribute("stamp")
            eastern = self.eastern(stamp)
            if ((eastern.hour%6)==0):
                HHs.append("%02d" % eastern.hour)
            else:
                HHs.append("")

            decaWatt = int(obs.getAttribute("value"))/10;
            decaWattStr = "%d" % (decaWatt);
            maxkW = max(maxkW,int(math.ceil(decaWatt/100.0)))
            decaWatts.append(decaWattStr)
            #self.response.out.write("<br> %s = %s HH=%s-> %sx10W" %( stamp,eastern,eastern.hour,decaWatt ))

        # feed is inversed in time
        HHs.reverse()
        decaWatts.reverse()
        chartParams = {
            'cht': 'bvs',
            'chs': '320x200',
            'chbh' : 'a',
            'chd' : 't:%s' % ",".join(decaWatts),
            'chds' : '0,%d' %(maxkW*100), #these are decaWatts
            'chco' : 'c6d9fd,4d89f9',
            'chxt' : 'x,y',
            'chxl' : '0:|%s|1:|0|%d' % ( "|".join(HHs),maxkW),
            #'chdl' : 'Last 24 hours',
            #'chdlp' : 'b',
            'chtt' : 'Power Connsumption last 24 hours (kW)',
        }
        params = "&".join(["%s=%s" % (k, v) for k, v in chartParams.items()])
        chartURL = "http://chart.apis.google.com/chart?%s" % params
        chartURL = chartURL.replace(' ','%20')

        dayFeedStamp = self.eastern(day.getAttribute("stamp"))
        dayFeed_kWh = int(day.getAttribute("value"))*24.0/1000.0
        title = "Power Consumption for %s @ %s  - %.1f kWh" %(owner,dayFeedStamp,dayFeed_kWh)
        self.response.out.write("""<h3>%s</h3>
<img src="%s" >
""" % (title,chartURL))
        #self.response.out.write("<br><br> HHs=%s-> dWs=%s" %( ",".join(HHs),",".join(decaWatts) ))

        postfriend = self.request.get("postfriend")
        if (postfriend):
            self.publishChartToFriendfeed(title,chartURL)
            self.response.out.write("<br><br><br>Posted to FriendFeed: %s"%postfriend)


    else:
        self.response.out.write("""Graph: owner unspecified
""")

class LivenessPage (webapp.RequestHandler):
  """ Comet style poller reports lastRead
"""
  def get(self):
    owner = self.request.get("owner")
    lastRead = memcache.get("lastRead:%s" % owner)
    now = datetime.now()
    accessedSecondsAgo=-1;

    if (lastRead is not None):
        accessedSecondsAgo = (now-lastRead).seconds
        self.response.out.write("""Feed:%s Accessed %ss. ago
""" % (owner,accessedSecondsAgo))
    else:
        self.response.out.write("""Feed:%s Never Accessed
""" % owner)


application = webapp.WSGIApplication([
  ('/', MainPage),
  ('/post', PublishPage),
  ('/feeds', SubscribePage),
  ('/graph', GraphPage),
  ('/q', LivenessPage),
],debug=True)

def main():
  wsgiref.handlers.CGIHandler().run(application)


if __name__ == '__main__':
  main()
