#!/usr/bin/python

import sys
import smtplib

version='$Id$'
smtpHost='smtp.gmail.com'
smtpPort=587 #port 465 or 587
smtpUsername='watchdog@mirawatt.com'
smtpPassword='' # try md5 or something
smtpFrom = smtpUsername
smtpTo = 'alerts@mirawatt.com'

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
