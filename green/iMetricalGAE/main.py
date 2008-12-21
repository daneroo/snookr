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

from google.appengine.ext import db
from google.appengine.ext import webapp

logging.getLogger().setLevel(logging.DEBUG)


class Feeds(db.Model):
  owner   = db.StringProperty()
  content     = db.BlobProperty()
  date    = db.DateTimeProperty(auto_now_add=True)


class MainPage(webapp.RequestHandler):
  def get(self):
    self.response.out.write('Hello iMetrical World!')

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
    feeds = Feeds(key_name=owner)
    feeds.owner = owner
    feeds.content = db.Blob(self.request.get("content"))
    feeds.put()
    self.response.out.write("""
""")

class SubscribePage (webapp.RequestHandler):
  def get(self):
    feeds = Feeds.get_by_key_name(self.request.get("owner"))
    if feeds.content:
      self.response.headers['Content-Type'] = "text/xml"
      self.response.out.write(feeds.content)
    else:
      self.response.out.write("<feeds/>")

application = webapp.WSGIApplication([
  ('/', MainPage),
  ('/post', PublishPage),
  ('/feeds', SubscribePage),
],debug=True)

def main():
  wsgiref.handlers.CGIHandler().run(application)


if __name__ == '__main__':
  main()
