/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * Informational Status bar
 */
Tn5250.OIA = (function () {

	var STATUS = {
		0:'OFFLINE',
		1:'CONNECTING',
		2:'ONLINE',
		3:'X-WAIT',
		4:'X-LOCK'
	};

    var renderer = {

        init: function (config) {
            var me = this;
            me.printer = 0;
            me.net = 0;
            me.ower = true;
            me.rtl = false;
            me.isBeep = false;
            me.overlay = $('.overlay');
            me.content = $('.view');
            me.status = {
            		status : $('.tnstatus .sts'),
            		action : $('.tnstatus .action'),
            		msg : $('.tnstatus .msg'),
            		msgw : $('.tnstatus .msgw_icon'),
            		pos : $('.tnstatus .pos'),
            		wrk : $('.tnstatus .wrk'),
            		mod : $('.tnstatus .mod'),
            		lng : $('.tnstatus .lng')
            }
        },

		// update status bar based on options
        updateUI: function (cfg) {
            var me = this;

            if (me.net === 2) {
                if (cfg.locked === true) {
                    me.lock(cfg);
                } else {
                    me.unlock(cfg);
                }
            }

            if (cfg.clearScr) {
                me.content.html('');
            }

            // store values
            if (typeof cfg.net === 'number') {
            	me.net = cfg.net || 0;
            	if (me.net === 2) {
            		window.onbeforeunload = function(){
            			   return "Closing this tab will kill telnet session!";
            		};            		
            	} else if (me.net < 2) {
            		window.onbeforeunload = null;
            	}
            }

            if (typeof cfg.printer === 'number') {
            	me.printer = cfg.printer || 0;
            }

            if (typeof cfg.isRtl  === 'boolean') {
            	me.rtl = cfg.isRtl;
            }

            if (typeof cfg.ower === 'boolean') {
            	me.ower = cfg.ower;
            }

            // network status
            me.status.status.html(STATUS[me.net]);

        	// RTL / LTR input mode
            if (me.rtl) {
            	me.status.lng.html('RTL');
            } else {
            	me.status.lng.html('LTR');
            }

            // INSERT / OVERWRITE mode
            if (me.ower) {
            	me.status.mod.html('OWR');
            } else {
            	me.status.mod.html('INS');
            }

            // ROW / COL position
            if (typeof cfg.row === 'number' && typeof cfg.col === 'number' ) {
            	me.status.pos.html(cfg.row + ':' + cfg.col);
            } else {
            	me.status.pos.html('0:0');
            }

            me.status.msg.removeClass('red');

            // system oia's
            if (me.net <2 ) return;

            me.doOIA(cfg);

            if (cfg.success === false || cfg.code) {
            	var msg = '';
            	if (cfg.code) msg = cfg.code + ': ';
            	msg = msg + cfg.msg;
            	me.status.msg.addClass('red');
            	me.status.msg.html(msg);
            	me.beep();
            	delete cfg.code;
            }
        },

		// process remote statuses
        doOIA : function(cfg) {

        	var me = this;

            // message wait
            if (cfg.level === 3) {
            	me.status.msgw.addClass('msgw');
            }
            if (cfg.level === 4) {
            	me.status.msgw.removeClass('msgw');
            }

            // not inhibited
            if (cfg.inhibited === 0) {
            	me.isBeep = false;
            	if (cfg.level === 1) {
            		me.status.action.html(STATUS['3']);
            	} else {
            		me.status.action.html('');
            	}
            }

            // LOCK / WAIT status
            if (cfg.inhibited === 1 || cfg.locked) {
                me.status.action.html(STATUS['3']);
            	if (cfg.level === 10) {
            		me.status.action.html(STATUS['4']);
            		if (!me.isBeep) {
            			me.beep();
            			me.isBeep = true;
            		}
            	}
            }

            // machine check
            if (cfg.inhibited === 4) {
            	me.status.msg.html('MACH-' + cfg.mach);
            	me.status.msg.addClass('red');
            } else {
            	me.status.msg.html('');
            	me.status.msg.removeClass('red');
            }

            delete cfg.inhibited;
            delete cfg.level;

        },

		// clear status barr to initial state
        clear: function (cfg) {
        	this.updateUI({
        		lock:false,
        		clearScr : true,
        		level : 4
        	});
        },

		// lock screen when sending data
        lock: function (cfg) {
            cfg.locked = true;
            this.overlay.show();
        },

		// unslocka after screen is rendered and available
        unlock: function (cfg) {
            cfg.locked = false;
            this.overlay.hide();
        },

		// play sound as an error informational signal
        beep: function() {
            var sound = document.getElementById('beep');
            sound.load();
            sound.play();
        }

    };

    renderer.init();

	// public methods avaialble outside closure
	//
    return {

    	isBeep : function(){
    		return renderer.isBeep;
    	},

    	beep : function(){
    		renderer.beep();
    	},

        updateUI: function (cfg) {
            return renderer.updateUI(cfg);
        },

        clear: function (cfg) {
            return renderer.clear(cfg);
        }
    };

}());
