#!/usr/bin/python

import sys
import smtplib

# TO enable svn keyword substitution, use:
#  svn propset svn:keywords "Id Date Revision" this_file.py
# and read it back with:
#  svn propget svn:keywords this_file.py
# or
# svn propget -R svn:keywords
svnVersion='$Id$'

smtpHost='smtp.gmail.com'
smtpPort=587 #port 465 or 587
smtpUsername='watchdog@mirawatt.com'
smtpPassword='' # try md5 or something
smtpFrom = smtpUsername
smtpTo = 'alerts@mirawatt.com'

print "GMail SMTP Test Version: %s" % svnVersion
if (not smtpPassword):
    print "SMTP password required: set and rerun"
    sys.exit()

msg = ("From: %s\r\nTo: %s\r\nSubject:Gateway Test\r\n\r\nThis is a test of the gmail. SMTP Gateway") % (smtpFrom,smtpTo)

#server = smtplib.SMTP(smtpHost,smtpPort)
server = smtplib.SMTP()
server.connect(smtpHost,smtpPort)
server.ehlo()
server.starttls()
server.ehlo()
server.login(smtpUsername,smtpPassword)
server.sendmail(smtpFrom,smtpTo,msg)
server.close()
