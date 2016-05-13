<?php

require 'mccrypt.php';

$foo = new MCrypt;

$millitime = round(microtime(true) * 1000);
$data = 'mypassword' . "\n" . 'TOM' . "\n" . $millitime;

$enc = $foo->encrypt($data);
$dec = $foo->decrypt($enc);

echo $enc;
echo "\n";
echo $dec;
?>
