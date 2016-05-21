v2.7 (15.05.2016.)
-------------------

**JavaScript**
>- Fix - for websocket subprotocols which prevented proper operation on Tomcat
>- Fix - for IE cursor positioning, renderer table sections

**Java**
>- Added - support for Servlet only containers
>- Fix - multiline field error index calculation 


v2.6 (02.01.2016.)
-------------------

**JavaScript**
>- Added - url parameter redirect to redirect to new page after close connection
>- Added - copy / paste to copy screen data

**Java**

>- Improvement - logging hashed autosignon improved

v2.5 (11.01.2016.)
-------------------

**JavaScript**
>- Fix - cursor positioning representation fix
>- Fix - field navigation for RTL
>- Fix - FieldExit -> next field and position to begin
>- Fix - FieldExit + shift -> prev field and position to begin
>- Fix - Arrow UP / DWON -> next/prev field, position to last char (rtl support)
>- Fix - HOME / END positioning -> rtl support
>- Fix - Multiline field support
>- Added - Method to detect session closed from server side (close window  or redirect)
>- Added - Light version renderer implemented to ExtJS version
>- Added - switch rtl/ltr mode with auto cursor positioning
   Alt+shift+"/" (at key 7)-> for keyboard without numpad
   "/" for keyboard with numpad
   alt+shift -> should be set on windows to switch multilanguage input keyboard

**Java**
>- Added - AES encryption
>- Added - Close message trigger

v2.4 (08.01.2016.)
-------------------

**JavaScript**
>- Fix - F13-F24 handling
>- Added ExtJS with new Renderer (uses iframe)
>- Added - clickable hotspots support

**Java**
>- Added - DisplayName prefix property


v2.3 (20.12.2015.)
-------------------

**JavaScript**
>- Fix - rendering issues for windowed screens
>- Fix - CSS for themes
>- Fix - CSS for screen rows spacing
>- Fix - Field display
>- Fix - Focus field for IE/EDGE partial fix - Not supported by ExtJS <5.1.2
>- Fix - Fnn key mouse click
>- Fix - Fnn key for field position selection
>- Fix - UTF-8 header in web page update
>- Fix - for WebSocket events received without request (does not contain tid)
>- Fix - field change status - fix non opening screens
>- Added light 5250 - version that does not use ExtJS, support for RTL fields
>- Added prevent context menu for Fn keys in Ext.ux.Tn5250.KeyHandler
>- Added support feature for RTL fields
>- Added support for single session for browser tab
>- Added context menu for single session for browser tab
>- Added demo for embedded telnet
>- Added keyboard mode 1 & 2 - different Enter/Field Exit handling
>- Added button to retrieve current screen raw data for debug purposes
>- Added CTRL+R for screen refresh from local cache - internal debug
>- Added RSA encryption for password for non SSL sessions
>- Added support for ExtJS 5
>- Added support for ExtJS 6
>- Added signaler for lost WebSocket connection (if server restarted etc..)
>- Ext.ux.Tn5250.Panel - moved close handler from Ext.ux.Tn5250.TabPanel
>- Ext.ux.Tn5250.IFrame - created to support full screen inside frame
>- Ext.ux.Tn5250.KeyHandler - support for keyboard mode 1 & 2
>- Ext.ux.Tn5250.KeyManager - support for keyboard mode 1 & 2
>- Ext.ux.Tn5250.Renderer - new 5250 rendering engine - pixel perfect alignment.
>- Ext.ux.Tn5250.View refactoring. Improved speed, pixel perfect rendering. Autoscaling.

**Java**
>- Fix - Number field for empty values
>- Fix - invalid Unicode character
>- Fix - 27x132 resolution fix
>- Fix - screen flickering
>- Fix - Right CTRL for OIA updates in Tn5250SessionListener
>- Fix - Bidi rendering for combination of RTL & LTR data (https://docs.oracle.com/javase/tutorial/2d/text/textlayoutbidirectionaltext.html)
>- Fix - Bidi input processing
>- Fix - error when app is stopped (connection / session clearance)
>- Fix - number field handling
>- Added Filter GZIP compression and server cache for JS & CSS files
>- Added keepalive parameter option to kill 5250 sessions on WebSocket close
>- Added RSA decryption support to decode RSA encrypted password from browser
>- Added Sign-on message screen bypass feature
>- Added 5250.msgbypass parameter for multi login screen to bypass "Another user job active" message.
>- Added TN5250 SHA1 password exchange for autosignon
>- Optimize Response data size optimization
>- Optimize Speed rendering improvement
>- Optimize Updated fieldmask in Tn5250StreamProcessor for new options


v2.2 (21.11.2015.)
-------------------

**JavaScript**
>- Ext.ux.Tn5250.Field - field exit handling for IE fixed
>- Ext.ux.Tn5250.Proxy.CreateSession extended with new parameter - display name
>- Ext.ux.Tn5250.Proxy.CreateSessionAuto - bypass sign-on support
>- Ext.ux.Tn5250.Proxy - methods callback wrapped in options for automatic message popup on error
>- WebSocket url is now generated from location object

**Java**
>- Tn5250Controller.open5250Session - extended with new parameter - displayName
>- Tn5250Controller.open5250SessionAuto - new method for bypass sign-on support
>- TnConstants extended with error codes so front can map them to specific language on received error
>- ExtJSResponse added error code field
>- lib tn5250j.jar updated to latest version, contains some screen rendering fixes

 ---

 v2.1 (24.03.2014.)
-------------------

**JavaScript**
>- Support for ExtJS 5 added

**Java**
>- WebSockets implemented for bi-directional communication
>- Threading issues solved
>- Complete rewrite of code to use CDI  

