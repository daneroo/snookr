# all that was needed on dirac:
easy_install -U couchdb
# but I had looked at
# easy_install -U Couchdbkit

# created getJSON.php to export feeds from cantor:
#
time curl "http://cantor/iMetrical/getJSON.php" -o tensec.json
