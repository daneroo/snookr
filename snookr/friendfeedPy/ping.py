import socket
import friendfeed

# This script connects and posts a message
# The message has a known prefix
#

subscriptionPrefix="Ping::42"
#hostIdentification = os.uname[1]
#hostIdentification = socket.gethostname()
hostIdentification = socket.getfqdn()

session = friendfeed.FriendFeed(auth_nickname="daneroo", auth_key="black439sower")
if session.auth_nickname and session.auth_key:


    # Post a message on this user's feed
    entry = session.publish_message(subscriptionPrefix+" from "+hostIdentification)
    #print entry
    print "Posted new message at http://friendfeed.com/e/%s" % entry["id"]

    #comment_entry_id = session.add_comment(entry["id"],"Let Me see: 2009-05-08 12:34:56")
    #print "Posted new comment at NOT http://friendfeed.com/e/%s" % comment_entry_id

else:
    print "Authentication Failed ?"

