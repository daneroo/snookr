Setup for ted.mirawatt.com:

copied jqtouch and themes sub-directories by hand.
to update

cd /Users/daniel/Documents/NetBeansProjects/green/mirawatt/iphone
 - or equivalent -
rsync -av --progress --exclude .svn ./ mirawatt@axial.mirawatt.com:httpdocs/com/mirawatt/ted/
