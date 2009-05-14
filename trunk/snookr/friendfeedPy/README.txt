This is an example of notification through friendfeed:

listen.py
  Start a live update loop:
    with timeouts every n seconds (60)
    when updates are received,
      The update titles are searched for the subscription prefix (Ping::42)
      The matched update entry is then deleted.
  Rinse Repeat

ping.py
  Produces an entry with the appropriate prefix: (Ping::42)