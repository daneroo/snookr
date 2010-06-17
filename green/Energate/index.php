<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title></title>
    </head>
    <body>
        <pre>
            <?php
            require_once 'service/Energate.php';
            print("Hello Energate!\n");

            $enrgate = new Energate();
            $username="scottdesk";
            $password="test123";
            $sessioncookie = $enrgate->login($username,$password);

            $dataout = $enrgate->getit($sessioncookie,$username);
            print("\n--Now the output\n");
            print_r($dataout);
            print("\n--Now the output de-json'd\n");
            $decoded = json_decode($dataout,TRUE);
            print_r($decoded);
            ?>
        </pre>
    </body>
</html>
