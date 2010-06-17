<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=320, initial-scale=1.0, maximum-scale=2.3, user-scalable=0;"/>

        <title>Ernergate Protoype</title>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
        <script type="text/javascript">
            var imIsIPhone=true;
            if(!((navigator.userAgent.match(/iPhone/i)) || (navigator.userAgent.match(/iPod/i)))) {
                imIsIPhone=false;
            }
            function delayer(){
                if (imIsIPhone){
                    window.location = "proxied.html";
                } else {
                    window.location = "framed.html";
                }
            }

            $(function(){
                $('#browserid').text(navigator.userAgent);
                if (imIsIPhone){
                    $('#status').html("You have an iPhone");
                } else {
                    $('#status').html("You are not on an iPhone");
                }
                setTimeout('delayer()', 5000);

            });
        </script>
        <style type="text/css" media="screen">
            body {
                font-family: 'trebuchet ms', verdana, arial;
                background: rgb(255, 255, 255); /* The Fallback */
                background: rgba(255, 255, 255, 1.0);
            }
            #logo {
                padding-top: 1em;
                padding-bottom: 1em;
            }
            #busyBox {
                margin-top: 2em;
                height: 20px;
            }
            #busySpinner {
            }
            #status {
                margin-top: 1em;
                color: black;
                font-size: 110%;
                text-align: center;
                font-style: italic;
            }
            #browserid {
                margin-top: 1em;
                color: gray;
                font-size: 70%;
                text-align: center;
            }
        </style>
    </head>

    <body>
        <div id="logo">
            <center><img src="images/logo.gif" /></center>
        </div>

        <center>
            <div>
                Energate Prototype Landing Page <br />
                Redirecting for appropriate browser <br />

            </div>
            <div>
                <div id="status">
                </div>
                <div>
                    <span id="browserid">--</span>
                </div>
            </div>

        </center>
        <div id="busyBox">
            <center><img id="busySpinner" src="images/busy20trans.gif" /></center>
        </div>

        <?php
        /*
             * Copyright 2010 Daniel Lauzon <daniel.lauzon@gmail.com>
        */
        ?>
    </body>
</html>
