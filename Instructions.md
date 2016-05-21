GreenScreens Terminal Instructions
======================

<div class="break"></div>

----

## Prerequisites

**Requirements**
>- Java EE 1.7+
>- Any Full JEE7 server like WildFly 8/9/10 or Tomcat 
>- WebKit (Chrome, Firefox), IE 9+

**Features**
> Multiple web 5250 sessions
> Bypass signon

<div class="break"></div>

## Configuration

### Properties file

Properties file named **io.greenscreens.properties** is a key/value pair based text file where  AS/400 connections are defined and mapped to virtual names used from web application.

File should be set to user folder - owner of Java web server running process so web app can load properties file.

#### Properties file location

##### <i class="icon-file"></i> Windows

 Copy io.greenscreens.properties to  
  C:\Users\CURRENT_WINDOWS_USER_NAME

##### <i class="icon-file"></i> Linux
For Linux it is important that **io.greenscreens.properties** is located in home folder of the user under which Java server is running. Otherwise, Java server will not be able to find configuration file.

 Copy **io.greenscreens.properties** to  
 /home/JAVA_SERVER_LINUX_USER

 **NOTE:** If running as a root user on Linux, location for **io.greenscreens.properties** is /root

<div class="break"></div>

#### **Properties file options**

File contains global properties and server level properties.

##### <i class="icon-file"></i> Global values

Property           | Type    | Description
------------------ | -------------------------------------------------------------
*prefixes*    | Strings | List of virtual names of defined servers available to web app.
*shared_pwd*  | String  | 16 chars long password
*shared_iv*   | String  | 16 chars long initialization vector
*shared_time* | Number  | Password expiration in seconds


##### <i class="icon-file"></i> Server values  

Property              | Type     | Description
--------------------- | ---------|-------------
[PREF].ip        | String   | Host name of IP address of AS/400 server
[PREF].port      | String   | Port of 5250 telnet service
[PREF].name      | String   | Virtual name to be used in web app to access server
[PREF].codepage  | String   | Code page to use
[PREF].msgbypass | Boolean  | Auto bypass post sign-on screen
[PREF].keepalive | Boolean  | Default false, if true, keep session on WebSocket channel close
[PREF].display_prefix | Boolean | Prefix for display names (optional)
[PREF].close_msg | String  | Screen message trigger to close sessions

<div class="break"></div>

##### <i class="icon-file"></i> Supported code pages
This is list of supported codepages by TN5250j lib.

"Big5", "Cp037", "Cp273", "Cp277", "Cp278", "Cp280", "Cp284", "Cp285", "Cp297", "Cp420", "Cp424", "Cp437", "Cp500", "Cp737", "Cp775", "Cp838", "Cp850", "Cp852", "Cp855", "Cp856", "Cp857", "Cp858", "Cp860", "Cp861", "Cp862", "Cp863", "Cp864", "Cp865", "Cp866", "Cp868", "Cp869", "Cp870", "Cp871", "Cp874", "Cp875", "Cp918", "Cp921", "Cp922", "Cp923", "Cp930", "Cp933", "Cp935", "Cp937", "Cp939", "Cp942", "Cp943", "Cp948", "Cp949", "Cp950", "Cp964", "Cp970", "Cp1006", "Cp1025", "Cp1026", "Cp1046", "Cp1097", "Cp1098", "Cp1112", "Cp1122", "Cp1123", "Cp1124", "Cp1140", "Cp1141", "Cp1142", "Cp1143", "Cp1144", "Cp1145", "Cp1146", "Cp1147", "Cp1148", "Cp1149", "Cp1252", "Cp1250", "Cp1251", "Cp1253", "Cp1254", "Cp1255", "Cp1256", "Cp1257", "Cp1258", "Cp1381", "Cp1383", "Cp33722"

<div class="break"></div>

#### **Config example**

If there is more than one server, 5250_prefixes key will have a list of all defined servers. For each server, prefix name is used as a mapping to virtual names. Here we used **dev** and **prod**, **name** property is used in front end.

<pre>
shared_pwd=cf34cqDCQerc3f4f
shared_iv=01234567890qwertz
shared_time=900

prefixes=dev,prod

dev.ip=192.168.1.50
dev.port=23
dev.name=DEV_400
dev.codepage=Cp870
dev.msgbypass=true

prod.ip=192.168.1.51
prod.port=23
prod.name=PROD_400
prod.codepage=Cp870
prod.msgbypass=true
prod.display_prefix=WEB
</pre>

----

<div class="break"></div>

### Install Java web app

To install war file, Java server should be full JEE 7 certified server. Web container only servers are supported with servlet 3.1 specification. If servlet only is used, CDI implementation jar should be added to the application. We have tested JBoss WELD. 

For Tomcat 8+ it is enough to add weld-servlet.jar file to WebContent/WEB-INF/lib.

**war** file can be copied to Java server drop-in folder or installed through web interface. This depends on Java server that is used.  

After app is successfully installed, app can be opened at http://YOUR_WEB_SERVER:PORT/NAME
where **NAME** is name of **.war** file

##### <i class="icon-file"></i> WebSocket testing

To test bi-directional communication between AS/400 and web app, one can send responses from server at any time (websocket channel) with this command from AS/400

>QSYS/SNDBRKMSG MSG(TEST MESSAGE) TOMSGQ(QPADEV****) MSGTYPE(*INQ) RPYMSGQ(QUSER)

Where QPADEV**** is device of your web session

----
<div class="break"></div>

## Additional info   

##### <i class="icon-file"></i> **Issues**

>- Tn5250j uses thread based connections. This is limitation for having many tn5250 sessions, best option would be to switch to event based communication.
>- When building in Eclipse, check that display-name inside web.xml match to project name and exported war file.   

------
<div class="break"></div>

## Source folder descriptions

 **src **
Directory contains all needed logic for web rest services to enable remote commands for 5250 session management  

**src_base **
 Directory with websocket implementation specifically customized for ExtJS framework

## **Light version**

This version is based on jQuery only and Vanilla JavaScript. Only single 5250 session per browser tab  is possible and supports two modes.

>- with sign-on screen
>- with bypass sign-on screen

To start light version use the following link (HOST_NAME is value defined in properties file)
http://localhost:8080/exttn5250/light?host=[HOST_NAME]

To use bypass sign-on, one can use additional parameters with username and password

http://localhost:8080/exttn5250/light?host=[HOST_NAME]&user=[USER]&password=[PASSWORD]

##### <i class="icon-file"></i> URL parameters

Property           | Type    | Description
------------------ | -------------------------------------------------------------
host               | Strings | host name mapped in configuration file  
user               | String  | user name
password           | String  | password (clear, rsa, aes)
displayName        | String  | name of display device

<div class="break"></div>

## **Security**

If Java web server has configured SSL, and web app is accessed by https protocol, web app will automatically use wss:// instead of ws:// for encrypted WebSocket connection.
Additionally, password encryption for embedded screens are supported with timestamp based timeout.  

#### **String format**

Encrypted string contains password, user, timestamp separated by new line.

Example: encrypt(mypassword\njohn\n1234567890123)

#### **Server timestamp**

If embedding Tn5200 in other web apps based on some other servers, one can get current Java server timestamp to be sure timestamp is synchronized.

Url is http://[serer]:[port]/exttn5250/timestamp

This timestamp can be used for password encryption as shown above.

<div class="break"></div>

#### **PHP AES encryption example**

To generate URL for embedding in server side generated page for TN 5250 access, one can use this example based on PHP. (password and iv must be the same as set to properties file for java web app)

```php
        <?php

        class AESCrypt
        {
                private $iv = 'fedcba9876543210'; #Same as in JAVA
                private $key = '0123456789abcdef'; #Same as in JAVA


                function __construct()
                {
                }

                function encrypt($str) {

                  //$key = $this->hex2bin($key);    
                  $iv = $this->iv;

                  $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

                  mcrypt_generic_init($td, $this->key, $iv);
                  $encrypted = mcrypt_generic($td, $str);

                  mcrypt_generic_deinit($td);
                  mcrypt_module_close($td);

                  return bin2hex($encrypted);
                }

                function decrypt($code) {

                  //$key = $this->hex2bin($key);
                  $code = $this->hex2bin($code);
                  $iv = $this->iv;

                  $td = mcrypt_module_open('rijndael-128', '', 'cbc', $iv);

                  mcrypt_generic_init($td, $this->key, $iv);
                  $decrypted = mdecrypt_generic($td, $code);

                  mcrypt_generic_deinit($td);
                  mcrypt_module_close($td);

                  return utf8_encode(trim($decrypted));
                }

                protected function hex2bin($hexdata) {
                  $bindata = '';

                  for ($i = 0; $i < strlen($hexdata); $i += 2) {
                        $bindata .= chr(hexdec(substr($hexdata, $i, 2)));
                  }

                  return $bindata;
                }
        }

			$foo = new AESCrypt;

			$millitime = round(microtime(true) * 1000);
			$data = 'mypassword' . "\n" . 'TOM' . "\n" . $millitime;

			$enc = $foo->encrypt($data);
			$dec = $foo->decrypt($enc);

			echo $enc;
			echo "\n";
			echo $dec;        

      ?>
```

----
NOTE: Best viewed with https://stackedit.io/editor

