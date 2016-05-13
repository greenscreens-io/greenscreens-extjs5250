/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * Main 5250 application.
 * Integrates renderer, ui and remote api calls
 */
Tn5250.Application = (function (isScript, isDebug) {

	// *******************   GENERAL PART  ******************* //

    var doc = document;
    var win = window;

	// 	Prevent page context menu
    doc.oncontextmenu = function (e) {
        e.preventDefault();
        return false;
    };

	// log to console received data
    function log(isDebug, name, data) {
    	if (isDebug) {
    		console.log(name, data);
    	}
    }

	// convert url params to json object
    function getURLParams() {
        var search = win.location.search.substring(1);
        search = search.replace(/&/g, '","').replace(/=/g, '":"');
        var params = search ? JSON.parse('{"' + search + '"}', function (key, value) {
            return key === "" ? value : win.decodeURIComponent(value)
        }) : {};
        return params;
    }

	// log params, if not set, exit
    var params = getURLParams();
    log(isDebug, 'urlParams', params);
    if (!params.host) return;

    // close window or frame - security issue , browser limitation
    // or redirect to end page
    function postClose(isScript) {

    	if (isScript) {
        	if (win.parent && typeof win.parent.onClientClosed === 'function') {
        		if (win.parent.onClientClosed()) {
        			return;
        		}
        	}
    	}

    	if (params.url) {
    		var redir = params.url.toLowerCase();
    		if (redir === "true") {
            	// redirect to session end
            	redir = win.location.pathname.split('/').slice(0,2);
            	redir.push("end.html");
            	win.location.pathname = redir.join('/');
    		} else {
    			win.location.href = redir;
    		}
    	}
    }

    // *******************   MAIN PART  ******************* //

    var me = this, displayID = null, last = null, ws = new Tn5250.API();

    // *******************   KEYBOARD EVENTS HANDLING PART  ******************* //

    Tn5250.OIA.updateUI({net:0});

    // initialize virtual keyboard
    Tn5250.KeyBar.init(false, 2);

    //keyboard mode
    Tn5250.Keyboard.setMode(2);

    // beep signal
    Tn5250.Keyboard.listen('error:input', function (cfg) {
    	Tn5250.OIA.beep();
    });

    // ins/owr; RTL/LTR change
    Tn5250.Keyboard.listen('mode', function (type, cfg) {
    	if (type === 2) { // rtl
    		Tn5250.UI.updateUI(null, cfg);
    	}
    	Tn5250.OIA.updateUI(cfg);
    });

    // row/ col change
    Tn5250.Keyboard.listen('move', function (row, col, cfg) {
    	Tn5250.UI.moveTo(row, col, cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // move to previous field
    Tn5250.Keyboard.listen('prev', function (e, cfg) {
    	Tn5250.UI.previousField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // move next field
    Tn5250.Keyboard.listen('next', function (e, cfg) {
    	Tn5250.UI.nextField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // move to begin of text in field
    Tn5250.Keyboard.listen('home', function (e, cfg) {
    	Tn5250.UI.homeField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // move end of text in field
    Tn5250.Keyboard.listen('end', function (e, cfg) {
    	Tn5250.UI.endField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // field exit
    Tn5250.Keyboard.listen('fex', function (e, cfg) {
    	Tn5250.UI.fieldExit(e, cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // new line
    Tn5250.Keyboard.listen('newLine', function (e, cfg) {
    	Tn5250.UI.newLine(e, cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // delete char of text in field
    Tn5250.Keyboard.listen('del', function (e, cfg) {
    	Tn5250.UI.deleteField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    Tn5250.Keyboard.listen('erase', function (e, cfg) {
    	Tn5250.Application.refresh();
    });

    // backspace char of text in field
    Tn5250.Keyboard.listen('back', function (e, cfg) {
    	Tn5250.UI.backspaceField(cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // insert new char
    Tn5250.Keyboard.listen('input', function (e, cfg) {
    	Tn5250.UI.insertField(e, cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // paste command data
    Tn5250.Keyboard.listen('paste', function (e, data, cfg) {
    	Tn5250.UI.paste(data, cfg);
    });

    // send key command
    Tn5250.Keyboard.listen('command', function (e, data, cfg) {
    	Tn5250.UI.command(data, cfg);
    });

    // move to row / col
    Tn5250.Keyboard.listen('click:single', function (e, row, col, cfg) {
    	Tn5250.UI.moveTo(row, col, cfg);
    	Tn5250.OIA.updateUI(cfg);
    });

    // send row / col to server
    Tn5250.Keyboard.listen('click:double', function (e, row, col, cfg) {
    	Tn5250.UI.moveTo(row, col, cfg);
    	Tn5250.OIA.updateUI(cfg);
    	Tn5250.UI.sendCursor(e, row, col, cfg);
    });

    // send Fnn button key to server
    Tn5250.Keyboard.listen('click:button', function (e, row, col, cfg) {
    	Tn5250.UI.command(e.getAttribute('data'), cfg);
    });


    // *******************   UI EVENTS HANDLING PART  ******************* //

    // beep signal
    Tn5250.UI.listen('error:input', function (cfg) {
    	Tn5250.OIA.beep();
    });

    // send data to remote - get focus field and active fields; cursor row/col
    Tn5250.UI.listen('send', function (request, fields, cfg) {
    	log(isDebug, 'send', arguments);
        Tn5250.OIA.updateUI(cfg);
        request.displayID = displayID;
        io.greenscreens.Tn5250Controller.requestSession(request, fields, function (res) {
            log(isDebug, 'send_response', arguments);
        });
    });

    // ******************* NETWORK EVENTS HANDLING PART  ******************* //

    // process received display data
    function doDisplay(obj) {

        // closed session
        if (obj.size === 0) {
        	postClose(isScript);
        }

        if (cfg) {
            cfg.locked = obj.locked;
            cfg.inhibited = obj.inhibited;
            cfg.level = obj.level;
            Tn5250.UI.updateUI(obj, cfg);
        }

        // if response contains creen data, rerender it
        if (obj.data) {
        	last = obj;
            cfg = Tn5250.Renderer.render(obj.data, obj.displayID, obj.size);
        }

        // if cfg is set, refresh screen and statuses
        if (cfg) {
        	Tn5250.UI.resizer();
        	Tn5250.UI.updateUI(obj, cfg);
            Tn5250.OIA.updateUI(cfg);
            Tn5250.Keyboard.attach(cfg);
            Tn5250.Selector.init();
        }
    }

    // on open screen session
    function onScreen(res, obj, sts) {

        //console.log('openSession', arguments);
        if (sts === false) {
            Tn5250.OIA.updateUI({net:0, success:false, msg : res.msg});
        } else {
        	Tn5250.OIA.updateUI({net:2});
        }

        displayID = res.displayID;
    }


    // ********* websocket events

    var cfg = null;

    ws.listen('data', function (obj) {
    	log(isDebug, 'data', arguments);

    	var sts = obj && obj.success;
    	if (!sts) {
    		return;
    	}

    	if (obj.displayID && obj.displayID === displayID) {
    		return doDisplay(obj);
    	}
    });

    ws.listen('error', function (e, o) {
        log(isDebug, 'error', arguments);
        if (o && o.msg) {
        	Tn5250.OIA.updateUI({success:false, msg : o.msg});
        }
    });

    ws.listen('close', function () {
        Tn5250.OIA.clear();
        Tn5250.OIA.updateUI({net:0});
        log(isDebug, 'close', arguments);
    });

    ws.listen('open', function () {

    	log(isDebug, 'open', arguments);

    	var cfg = {net:1};
        if (params.password) {
        	// bypass signon screen, uses rsa encrypted password
        	// 3 nulls are program, menu & lib params as optional
        	io.greenscreens.Tn5250Controller.openSessionAuto(params.host, params.displayName, params.user, params.password, null, null, null, onScreen);
        } else {
        	// open standard telnet session with signon screen
        	io.greenscreens.Tn5250Controller.openSession(params.host, params.displayName, onScreen);
        }

        Tn5250.OIA.updateUI(cfg);
    });

	// public methods available outside closure

    return {

		// refresh screen from server
    	refresh : function() {
    		var me = this;
    		io.greenscreens.Tn5250Controller.refreshSession(displayID, function(data){
    			cfg.locked = data.locked;
    			me.updateUI(data);
    		});
    	},

		// render screen from raw data
    	updateUI : function (data) {
    		last = data;
    		cfg = Tn5250.Renderer.render(data.data, '0', data.size);
    		Tn5250.UI.resizer();
    		Tn5250.UI.updateUI(null, cfg);
    		Tn5250.OIA.updateUI(cfg);
    		Tn5250.Keyboard.attach(cfg);
    	},

		// get last received screen data
    	getScreen : function () {
    		return last;
    	},

		// get registered dispalyID, unique per session
    	getDisplayID : function() {
    		return displayID;
    	}
    };

});

// initializa application whn document is fully loaded
$(document).ready(function () {


    if( typeof(WebSocket) != "function" ) {
       alert("Application on this device is not supported!");
       return;
    }

    Tn5250.Selector.init();
    Tn5250.Application = Tn5250.Application(false, false);
});
