#!/usr/bin/env python
#
# Copyright 2007 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#




import wsgiref.handlers
import logging
import datetime

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
    count = db.GqlQuery("SELECT * FROM Feeds").count()
    self.response.out.write("""Hello iMetrical World!
I have retreived %d records
""" % count)

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
    accessedSecondsAgo=-1;
    if (lastRead is not None):
        accessedSecondsAgo = stamp-lastRead

    logging.info("Updated Feed entity: %s %s" % (feeds.key().name(),feeds.stamp))

    self.response.out.write("""Published for %s @ %s (%s) |content|=%d
""" % (feeds.owner,feeds.stamp,accessedSecondsAgo,len(feeds.content)))



class SubscribePage (webapp.RequestHandler):
  """ http://imetrical.appspot.com/feeds?owner=daniel
"""
  def get(self):
    owner = self.request.get("owner")
    lastRead = datetime.datetime.now()
    memcache.set("lastRead:%s" % owner, lastRead)

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

application = webapp.WSGIApplication([
  ('/', MainPage),
  ('/post', PublishPage),
  ('/feeds', SubscribePage),
],debug=True)

def main():
  wsgiref.handlers.CGIHandler().run(application)


if __name__ == '__main__':
  main()
