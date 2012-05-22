import time

from mutagen.easyid3 import EasyID3
from mutagen.id3 import APIC
from mutagen.id3 import ID3
from mutagen.id3 import error
from mutagen.mp3 import MP3
import os
from xml.dom import minidom

def stampNOW():
    return time.strftime('%Y%m%d.%H%M%S')

def writeID3(mp3file, title, album, artist):
    audio = MP3(mp3file, ID3=EasyID3)
    try:
        audio.add_tags(ID3=EasyID3)
    except error:
        pass

    audio["title"] = unicode(title)
    audio["album"] = unicode(album)
    audio["artist"] = unicode(artist)
    audio["genre"] = u"Last.fm"
    audio.save()

# check if png|or jpg.. fix also from imageURI
def writeThumb(mp3file, thumbfile):
    audio = MP3(mp3file, ID3=ID3)
    try:
        audio.add_tags()
    except error:
        pass
    audio.tags.add(
    APIC(
        encoding=3, # 3 is for utf-8
        mime='image/png', # image/jpeg or image/png
        type=3, # 3 is for the cover image
        desc=u'Cover',
        data=open(thumbfile).read()
        )
    )
    audio.save()

# lastfm://artist/kruder/similarartists
# lastfm://user/daneroo/library
# lastfm://user/fromage67/library
# lastfm://user/daneroo/loved
# lastfm://user/fromage67/loved
station = 'lastfm://user/fromage67/library'
tuneCMD = 'perl lfmCMD.pl method=radio.tune station=%s sk=644253673e0f933b81d6722347a10f8e >/dev/null' % station
os.system(tuneCMD)

getListCMD = 'perl lfmCMD.pl method=radio.getPlaylist sk=644253673e0f933b81d6722347a10f8e >/dev/null'
os.system(getListCMD)

xmldoc = minidom.parse('last.fm.Response.xml')
title = xmldoc.getElementsByTagName('title')[0].childNodes[0].nodeValue
print "Playlist title is %s " % title

for trackNode in xmldoc.getElementsByTagName('track'):
    stamp = stampNOW()
    mp3file = "track-%s.mp3" % stamp
    thumbfile = "thumb-%s.png" % stamp
    location = trackNode.getElementsByTagName('location')[0].childNodes[0].nodeValue
    title = trackNode.getElementsByTagName('title')[0].childNodes[0].nodeValue
    album = trackNode.getElementsByTagName('album')[0].childNodes[0].nodeValue
    creator = trackNode.getElementsByTagName('creator')[0].childNodes[0].nodeValue
    imageURI = trackNode.getElementsByTagName('image')[0].childNodes[0].nodeValue
    print " -- %s : %s by %s \n\tstream: %s\n\timage:  %s\n\tfile:   %s" % (title, album, creator, location, imageURI, mp3file)

    # get The mp3 stream
    # -L is to follow redirects..
    # -s for silent
    # --limit-rate 16k : 128kbps
    os.system("curl -L --limit-rate 16k  \"%s\" >%s" % (location, mp3file))
    # get Thumbnail
    os.system("curl -s -L  \"%s\" >%s" % (imageURI, thumbfile))

    writeID3(mp3file, title, album, creator)
    writeThumb(mp3file, thumbfile)
    # clean up
    os.system("rm %s" % thumbfile)

