#!/usr/bin/env python
#

import wsgiref.handlers
import logging
import datetime
import base64
import urllib
import urllib2

from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.api import memcache

logging.getLogger().setLevel(logging.DEBUG)


class Feeds(db.Model):
  owner   = db.StringProperty()
  content = db.TextProperty()
  stamp   = db.DateTimeProperty()


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
    stamp = datetime.datetime.now()

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
    now = datetime.datetime.now()
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
      accessedSecondsAgo = (datetime.datetime.now()-previousLastRead).seconds
      
    lastRead = datetime.datetime.now()
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


class LivenessPage (webapp.RequestHandler):
  """ Comet style poller reports lastRead
"""
  def get(self):
    owner = self.request.get("owner")
    lastRead = memcache.get("lastRead:%s" % owner)
    now = datetime.datetime.now()
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
  ('/q', LivenessPage),
],debug=True)

def main():
  wsgiref.handlers.CGIHandler().run(application)


if __name__ == '__main__':
  main()
