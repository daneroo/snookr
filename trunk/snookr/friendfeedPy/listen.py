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
    #feed = session.fetch_home_feed()
    #showFeed(feed)

    updateStart = session.fetch_update_start()
    print updateStart
    token = updateStart["update"]["token"]
    print "token: %s" % token

    timeoutSeconds=60
    subscriptionPrefix="Ping::42"
    while True:
        shortToken = token[:5]+"..."+token[-5:]
        print "About to fetch update (blocking for %ds.) token: %s" % (timeoutSeconds,shortToken)
        update = session.fetch_update_home(token,timeoutSeconds)
        token = update["update"]["token"]
        #print update
        #print update["entries"]
        print "Got %d updated entries" % len(update["entries"])
        for e in update["entries"]:
            if e["title"].startswith(subscriptionPrefix):
                print "Found %s :: %s" % (e["published"],e["title"])
                print "  Deleting %s " % (e["id"])
                # Try catch...
                delete = session.delete_entry(e["id"])
                if (delete["success"]==True):
                    print "  Delete successful "

            else:
                print "Skip  %s :: %s" % (e["published"],e["title"])
        print""


    # Post a message on this user's feed
    #entry = session.publish_message("Power Interest 2009-05-08")
    #print "Posted new message at http://friendfeed.com/e/%s" % entry["id"]

    #comment_entry_id = session.add_comment(entry["id"],"Let Me see: 2009-05-08 12:34:56")
    #print "Posted new comment at NOT http://friendfeed.com/e/%s" % comment_entry_id

else:
    print "Authentication Failed ?"

