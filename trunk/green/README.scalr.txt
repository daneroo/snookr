
-Data feed operations: scalr-utils
  Python
    ReadSqlite.py  :    read TED.db sqlite3 -> data
    ReadTEDService.py : aria.dl.sologlobe.com:9090/DashboardData -> data

  store data -> MySQL
  push data -> GoogleAppengine/scalr

- ChartApp : jfreechart to show moving data
     Mysql (live) -> java chart

-GoogleAppEngine/scalr
   record scalar streams : kw/TED kw/Polling