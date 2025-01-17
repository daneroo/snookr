#!/usr/bin/env python
#
# Copyright 2008 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the"License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "ASIS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import memcache_zipserve
import wsgiref.handlers
import logging
from google.appengine.ext import webapp
from google.appengine.api import memcache

class CachePage(webapp.RequestHandler):
  def get(self):
    self.response.out.write("<html><body>")
    stats = memcache.get_stats()
    if stats:
      self.response.out.write("<b>Memcache stats</b><br>")
      self.response.out.write("<b>Cache Hits:  %s</b><br>\n" % stats['hits'])
      self.response.out.write("<b>Cache Misses:%s</b><br>\n" % stats['misses'])
      self.response.out.write("<b>Items:       %s</b><br>\n" % stats['items'])
      self.response.out.write("<b>Oldest(s):   %s</b><br>\n" % stats['oldest_item_age'])
    else:
      self.response.out.write("<b>No Memcache stats</b><br>\n")

    isFlush = self.request.get('flush')
    if isFlush:
      self.response.out.write("<b>Flush::%s</b><br><br>\n" % isFlush)
      memcache.flush_all()

    self.response.out.write('<a href="?">Don''t Flush, Refresh</a><br><br>\n')
    self.response.out.write('<a href="?flush=1">BE CAREFULL: Flush</a><br>\n')
    self.response.out.write("""
        </body>
      </html>""")

def main():
  logging.getLogger().setLevel(logging.WARNING)
  application = webapp.WSGIApplication(
      [('/cache', CachePage),
#       ('/(.*)',
#        memcache_zipserve.create_handler([['0.zip', 'index.html'],
#                                          ['1.zip',
#                                           'content_dir/sub_dir1.html'],
#                                          ['Sulbalcon.zip',
#                                           'Sulbalcon/index.html'],
#                                          ])),
       ('/(.*)',memcache_zipserve.create_handler(
            [['Sulbalcon.zip','Sulbalcon/index.html']],1
        )),
       ],
      debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()
