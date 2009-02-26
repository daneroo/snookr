TODO:
  NOTE:zip file may NOT contain direcoty entries.
      created with -D option for example
     so "a" as a pth name should always be a directory
     so should all zip Directory entries: e.g. fr/
     but so should all partial paths from entries:
         somedir/subdir/file.html -> somedir/ and somedir/subdir/ directories
  - directories: like fr, without the trailing /: fr/
  - Cache directives...
Running the app server:

dev_appserver.py --admin_console_server= --port=8084 ./

stderr>stdin and grep
dev_appserver.py --admin_console_server= --port=8084 ./ 2>&1 | grep "^ERROR"
