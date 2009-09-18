#!/usr/bin/python

import smtplib

smtpHost='smtp.gmail.com'
smtpPort=587 #port 465 or 587
smtpUsername='watchdog@mirawatt.com'
smtpPassword='' # try md5 or something
smtpFrom = smtpUsername
smtpTo = 'alerts@mirawatt.com'

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
