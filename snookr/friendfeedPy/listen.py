import friendfeed

# This script connect to live feed and listens
#

def showEntry(e):
    #print type(e),e
    #print "id: %s :: %s" % (e["id"],e["title"])
    # other date: e["updated"]
    print "%s :: %s" % (e["published"],e["title"])

def showFeed(feed):
    print type(feed["entries"])
    for e in feed["entries"]:
        showEntry(e)

session = friendfeed.FriendFeed(auth_nickname="daneroo", auth_key="black439sower")
if session.auth_nickname and session.auth_key:
    # The feed that the authenticated user would see on their home page
    updateStart = session.fetch_update_start()
    print updateStart
    token = updateStart["update"]["token"]
    print "token: %s" % token
    print "About to fetch update (blocking)"
    update = session.fetch_update_home(token,60)
    print update

    #feed = session.fetch_home_feed()
    #showFeed(feed)

    # Post a message on this user's feed
    #entry = session.publish_message("Power Interest 2009-05-08")
    #print "Posted new message at http://friendfeed.com/e/%s" % entry["id"]

    #comment_entry_id = session.add_comment(entry["id"],"Let Me see: 2009-05-08 12:34:56")
    #print "Posted new comment at NOT http://friendfeed.com/e/%s" % comment_entry_id

else:
    print "Authentication Failed ?"

