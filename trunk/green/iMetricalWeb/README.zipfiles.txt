Process for wrapping:
     Automator - Google Analytics
     zip iMetrical, iMetricalFR
Process for verifying integrity : Google analytics: UAZZZ-X

wrapping root files:
  # NOTE, changed the redirect in /index.html 
  #  from iMetrical/Home.html to iMetrical/ 
  mkdir -p root/en root/fr
  curl -o root/index.html http://www.imetrical.com/index.html
  curl -o root/en/index.html http://www.imetrical.com/en/index.html
  curl -o root/fr/index.html http://www.imetrical.com/fr/index.html
  #zip -r root.zip root
  cd root ; zip -r ../root.zip .
  cd .. ; unzip -l root.zip
