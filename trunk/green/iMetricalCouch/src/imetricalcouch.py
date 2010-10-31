# populate couch instance with imetrical data
#
#import simplejson as json
import json
import urllib2
import datetime
import couchdb

__author__="daniel"
__date__ ="$Oct 31, 2010 6:57:01 PM$"
JSONURL = "http://cantor/iMetrical/getJSON.php";
COUCHDBNAME = "imetricaltest"

couch = couchdb.Server()
#couch = couchdb.Server('http://example.com:5984/')

try:
   db = couch.create(COUCHDBNAME)
   print 'database (%s) created' % COUCHDBNAME
except:
   del couch[COUCHDBNAME]
   db = couch.create(COUCHDBNAME)
   print 'database (%s) deleted and created' % COUCHDBNAME


def pretty(any):
    print json.dumps(any, sort_keys=True, indent=4)

def loadJSON(url):
    result = json.load(urllib2.urlopen(url))
    #print result
    #pretty(result)
    return result;

if __name__ == "__main__":
    print "Relax, iMetrical Couch!"

    #pretty(['foo', {'bar': ('baz', None, 1.0, 2)}])

    observations = loadJSON(JSONURL)
    for obs in observations:
        #print "saving obs:"
        #pretty(obs)
        db.save(obs)
        #pretty(obs)

    print "Saved %d observations." % (len(observations))