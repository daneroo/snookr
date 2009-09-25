#!/bin/bash                                                                                             
# This pushes the summrized feed into the cloud

USERNAME="jean"
FEEDFILE="/mirawatt/feeds.xml"

while true; do  
    curl -m 30 -F "owner=${USERNAME}" -F "content=@${FEEDFILE};type=text/xml"  http://imetrical.appspot.com/post; 
    sleep 60;
done
