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

import cgi
import facebook

from google.appengine.ext import db
from google.appengine.ext import webapp

class Greeting(db.Model):
    author = db.StringProperty() # change the author to a string
    content = db.StringProperty(multiline=True)
    date = db.DateTimeProperty(auto_now_add=True)

class MainPage(webapp.RequestHandler):

    # get is for testing at imetrical-fb.appspot.com directly
    def get(self):
        self.response.out.write('iMetrical Facebook!<br>')

        self.response.out.write("""
<form action="./localsign" method="post">
<div><input type="text" name="content" size="30" maxlength="60"  />
<input type="submit" value="Add Message"></div>
</form>
""")
        greetings = db.GqlQuery("SELECT * FROM Greeting ORDER BY date DESC LIMIT 4")

        for greeting in greetings:
            if greeting.author:
                # change the greeting.author.nickname() to just greeting.author
                # since we change the author from a user property to a string property
                self.response.out.write('<p><b>%s</b> wrote: ' % greeting.author)
            else:
                self.response.out.write('An anonymous person wrote: ')

            self.response.out.write('%s</p>' % cgi.escape(greeting.content))

    # this is the post for facebook fbml integration
    def post(self):
        # Both these keys are provided to you when you create a Facebook Application.
        api_key = 'bd100b49340effa90332cdfbe6e85659'
        secret_key = 'b150ebbad9cdff51269248dbe4b9683d'

        # Initialize the Facebook Object.
        appName='imetrical'
        self.facebookapi = facebook.Facebook(api_key, secret_key,None,appName)

        # Checks to make sure that the user is logged into Facebook.
        if self.facebookapi.check_session(self.request):
            pass
        else:
            # If not redirect them to your application add page.
            url = self.facebookapi.get_add_url()
            self.response.out.write('<fb:redirect url="' + url + '" />')
            return

        # Checks to make sure the user has added your application.
        if self.facebookapi.added:
            pass
        else:
            # If not redirect them to your application add page.
            url = self.facebookapi.get_add_url()
            self.response.out.write('<fb:redirect url="' + url + '" />')
            return

        # Get the information about the user.
        user = self.facebookapi.users.getInfo( [self.facebookapi.uid], ['uid', 'name', 'birthday', 'relationship_status'])[0]

        # Display a welcome message to the user along with all the greetings.
        self.response.out.write("""
<p>Hello %s,</p>
<p>Welcome to the facebook edition of iMetrical Power Monitoring application. </p>
<p>Here you can see your current power consumption levels for different time scales.</p>
""" % user['name'])

        # create profiles using fbml refhandle
        stamp = datetime.datetime.now()
        fbml = 'Last Updated at %s' % stamp
        self.facebookapi.fbml.setRefHandle('myhandle',fbml)
        fbmlprofile = 'Box <fb:ref handle="myhandle" />'
        fbmlprofilemain = 'Wall <fb:ref handle="myhandle" />'
        self.facebookapi.profile.setFBML('', self.facebookapi.uid, fbmlprofile, '', '', fbmlprofilemain)

        # create Info section
        #jsonArray=' {"label": "Day"}','{"link": "" }'
        #jsonArray='{{"label": "Day", "description": "37.45 kWh power consumption for 2009-01-05 (+31%)", "image":"", "link": "" }}'
        #{"label": "Week", "description": "34.23 kWh power consumption for week of 2009-01-04 (+9%)" , "image":"", "link": ""},
        #{"label": "Month", "description": "36.00 kWh power consumption for 2008-02 (+25%)" , "image":"", "link": ""},
        #self.facebookapi.profile.setInfo('iMetrical Info', 5,'',self.facebookapi.uid)
        #self.facebookapi.profile.setInfoOptions("Time Scales",jsonArray)
        # {u'info_fields': {}, u'type': 5, u'title': u'iMetrical Info'}
        #gotinfo = self.facebookapi.profile.getInfo(self.facebookapi.uid)
        #self.response.out.write('<br>info:<pre>%s</pre><br>'%gotinfo);

        # add section buttons
        self.response.out.write('<fb:add-section-button section="profile" />');
        self.response.out.write('<fb:add-section-button section="info" />');


        # this just confirmed the previous setFBML worked
        # temp code to confirm fbml...
        #self.response.out.write('<p>profile fbml</p>');
        #self.response.out.write('<p>profile (1): <pre>%s</pre></p>' % (self.facebookapi.profile.getFBML(self.facebookapi.uid,1)))
        #self.response.out.write('<p>profile_main (2):<pre>%s</pre></p>' % (self.facebookapi.profile.getFBML(self.facebookapi.uid,2)))

        self.response.out.write('<fb:iframe  src="http://imetrical.appspot.com/iG/googleviz-fb.html" width="400" height="400" frameborder="0"/>');
        #self.response.out.write('<fb:iframe  src="http://imetrical.appspot.com/iG/boot-fb.html" frameborder="0"/>');
        #self.response.out.write('<br><br><fb:iframe src="http://imetrical.appspot.com/iG/jq.html" frameborder="0"/>');

        # requires appName to be set in Facebook object above: use / relative path
        signurl = self.facebookapi.get_app_url('sign')
        self.response.out.write("""
<br><br>
<form action="%s" method="post">
<div><input type="text" name="content" size="30" maxlength="60"  />
<input type="submit" value="Add Message"></div>
</form>
""" % signurl)

        greetings = db.GqlQuery("SELECT * FROM Greeting ORDER BY date DESC LIMIT 4")
        for greeting in greetings:
            if greeting.author:
                # change the greeting.author.nickname() to just greeting.author
                # since we change the author from a user property to a string property
                self.response.out.write('<p><b>%s</b> wrote: ' % greeting.author)
            else:
                self.response.out.write('<p>An anonymous person wrote: ')

            self.response.out.write('%s</p>' % cgi.escape(greeting.content))



class LocalSignPage(webapp.RequestHandler):

    def post(self):
        greeting = Greeting()
        greeting.author = 'local anon'
        greeting.content = self.request.get('content')
        greeting.put()
        self.redirect('/')

class SignPage(webapp.RequestHandler):

    def post(self):
        # Both these keys are provided to you when you create a Facebook Application.
        api_key = 'bd100b49340effa90332cdfbe6e85659'
        secret_key = 'b150ebbad9cdff51269248dbe4b9683d'

        # Initialize the Facebook Object.
        appName='imetrical'
        self.facebookapi = facebook.Facebook(api_key, secret_key,None,appName)

        # Checks to make sure that the user is logged into Facebook.
        if self.facebookapi.check_session(self.request):
            pass
        else:
            # If not redirect them to your application add page.
            url = self.facebookapi.get_add_url()
            self.response.out.write('<fb:redirect url="' + url + '" />')
            return

        # Checks to make sure the user has added your application.
        if self.facebookapi.added:
            pass
        else:
            # If not redirect them to your application add page.
            url = self.facebookapi.get_add_url()
            self.response.out.write('<fb:redirect url="' + url + '" />')
            return

        # Get the information about the user.
        user = self.facebookapi.users.getInfo( [self.facebookapi.uid], ['uid', 'name', 'birthday', 'relationship_status'])[0]

        greeting = Greeting()
        greeting.author = user['name']
        greeting.content = self.request.get('content')
        greeting.put()
        url = self.facebookapi.get_app_url()
        logging.info("Signed paged author: %s  redirect: %s" % (user['name'],url))
        self.response.out.write('<fb:redirect url="' + url + '" />')
        return


application = webapp.WSGIApplication([
  ('/', MainPage),
  ('/sign', SignPage),
  ('/localsign', LocalSignPage),
],debug=True)

def main():
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()
