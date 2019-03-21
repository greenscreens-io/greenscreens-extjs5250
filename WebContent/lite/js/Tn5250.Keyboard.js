/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * 5250 UI keyboard handler.
 */
Tn5250.Keyboard = (function () {

    var doc = document;

    // keys that modifies local screen data
    var KEY_MOD = {
        "BACKSPACE": 8,
        "ENTER": 13,
        "SHIFT": 16,
        "CTRL": 17,
        "ALT": 18,
        "END": 35,
        "HOME": 36,
        "INSERT": 45,
        "DELETE": 46,
        "FIELDEXIT": 0,
        "FIELDEXIT2": 107,
        codes: [0, 8, 13, 35, 36, 45, 46, 107]
    };

    // local screen navigation commands
    var KEY_NAV = {
        "TAB": 9,
        "LEFT": 37,
        "UP": 38,
        "RIGHT": 39,
        "DOWN": 40,
        codes: [9, 37, 38, 39, 40]
    };

    // 5250 remote commands
    var KEY_REMOTE = {
        "ENTER": 0,
        "RESET": 17,
        "ESC": 27,
        "ATTN": 27,
        "SYSREQ": 27,
        "PGUP": 33,
        "PGDOWN": 34,
        "PF1": 112,
        "PF2": 113,
        "PF3": 114,
        "PF4": 115,
        "PF5": 116,
        "PF6": 117,
        "PF7": 118,
        "PF8": 119,
        "PF9": 120,
        "PF10": 121,
        "PF11": 122,
        "PF12": 123,
        "PA1": 112,
        "PA2": 113,
        "PA3": 114,
        "CLEAR" : 19,
        codes: [0, 19, 27, 33, 34, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123]
    };

    var keyboard = {

            /**
             * Base initialization
             */
            init: function (config) {
                var me = this;
                me.ower = true;
                me.isRTL = false;
                me.cpa = false;
                me.setMode(1);
                MicroEvent.mixin(me);
            },

            /**
             * Set keyboard mode
             * 1 - ENTER = FIELD EXIT; RCTRL = ENTER
             * 2 - ENTER = ENTER; RCTRL = FIELD EXIT
             */
            setMode: function (mode) {
                this.mode = mode;
                if (mode === 1) {
                    KEY_REMOTE.ENTER = KEY_MOD.CTRL;
                    KEY_REMOTE.codes[0] = KEY_MOD.CTRL;
                    KEY_MOD.FIELDEXIT = KEY_MOD.ENTER;
                    KEY_MOD.codes[0] = KEY_MOD.ENTER;
                } else {
                    KEY_REMOTE.ENTER = KEY_MOD.ENTER;
                    KEY_REMOTE.codes[0] = KEY_MOD.ENTER;
                    KEY_MOD.FIELDEXIT = KEY_MOD.CTRL;
                    KEY_MOD.codes[0] = KEY_MOD.CTRL;
                }
            },

            /**
             * Is field or text on the screen
             */
            isProtected : function(cfg) {
            	var el = doc.body.querySelector('.tngrid');
                el = el.rows[cfg.row].cells[cfg.col];
            	cfg.blank = !$(el).hasClass('fld');
            	return cfg.blank;
            },

            /**
             * Check event which event processing
             */
            // flip RTL / LTR
            isSwitch : function(e) {
                var me = this, sts = false;
                // handle for / or 7 (on some keyboard layout / is on key 7)
                sts = ((e.which === 55 && e.altKey && e.shiftKey) || (e.which === 111 && e.shiftKey));
                return sts;
            },

            isKeyNav: function (e) {
                return KEY_NAV.codes.indexOf(e.which) > -1;
            },

            isKeyMod1: function (e) {
            	return e.which === KEY_MOD.BACKSPACE || e.which === KEY_MOD.DELETE || this.isKeyFE(e);
            },

            isKeyMod: function (e) {
                return KEY_MOD.codes.indexOf(e.which) > -1;
            },

            isKeyFE: function (e) {
            	var oe = e.originalEvent ? e.originalEvent.location : -1;
                return (KEY_MOD.FIELDEXIT === e.which && (oe ===  2 || oe === 0)) || KEY_MOD.FIELDEXIT2 === e.which ;
            },

            isKeyReset: function (e) {
            	var oe = e.originalEvent ? e.originalEvent.location : -1;
                return KEY_REMOTE.RESET === e.which && oe  === 1;
            },

            isKeyEnter: function (e) {
            	var oe = e.originalEvent ? e.originalEvent.location : -1;
                return KEY_REMOTE.ENTER === e.which && oe === 2 && !e.shiftKey;
            },

            isKeyNewLine: function (e) {
                return KEY_REMOTE.ENTER === e.which && e.shiftKey;
            },

            isKeyAttn: function (e) {
                return KEY_REMOTE.ESC === e.which && !e.shiftKey;
            },

            isKeySysReq: function (e) {
                return KEY_REMOTE.ESC === e.which && e.shiftKey;
            },

            isKeyPA: function (e) {
                return e.altKey && (KEY_REMOTE.PF1 === e.which || KEY_REMOTE.PF2 === e.which || KEY_REMOTE.PF3 === e.which);
            },

            // is pressed key one of 5250 request keys
            isKeyRemote: function (e, cfg) {
                var me = this;

                if (cfg.sysreq) return false;

                if (me.isKeyEnter(e)) {
                    return true;
                }

                if (me.isKeyReset(e)) {
                    return true;
                }

                if (me.isKeyPA(e)) {
                    return true;
                }

                if (KEY_REMOTE.codes.indexOf(e.which) > -1) {
                    return true;
                }

                return false;
            },

            /**
             * Find TD element by x,y pixel position
             */
            findEl: function (x, y, cfg) {
            	var tbl = doc.querySelector('.tngrid');
            	var rows = cfg.rows;
            	var cols = cfg.cols;

            	var cw = tbl.offsetWidth / cols;
            	var ch = tbl.offsetHeight / rows;

            	var cc = Math.round( x / cw);
            	var cr = Math.round( y / ch) ;
            	if (cr < tbl.rows.length) {
            		return tbl.rows[cr].cells[cc];
            	}
            	return null;
            },

            /**
             * Calculate new position row/col, if out of screen, reposition
             */
            focusRowCol : function(row, col, cfg) {

            	var _row = row;
            	var _col = col;

            	if(_row<0) _row = cfg.rows-1;
            	if(_row=== cfg.rows) _row = 0;

            	if(_col<0) _col = cfg.cols-1;
            	if(_col===cfg.cols) _col = 0;

            	this.trigger('move', _row, _col, cfg);

            },


            /**
             * Handle text paste into simulated input fields
             */
            paste: function (e, cfg) {

            	var v, me = this;

            	if (cfg.locked) {
            		me.trigger('error:input', cfg);
            		return;
            	}

                if (/text\/plain/.test(e.originalEvent.clipboardData.types)) {
                    v = e.originalEvent.clipboardData.getData('text/plain').trim();
                } else {
                  return;
                }

                me.trigger('paste', e, v, cfg);
            },

            /**
             * Find name of remote command mapped to key
             */
            getCommandName: function (e) {
                var me = this, name = '';

                (Object.keys(KEY_REMOTE)).every(function (v, i) {
                    if (KEY_REMOTE[v] === e.which) {
                        if (!e.altKey) {
                            if (e.which > 111 ) {
                            	if (e.shiftKey) {
                            		name = 'PF' + (e.which - 99);
                            	} else {
                            		name = 'PF' + (e.which - 111);
                            	}
                        		return false;
                        	} else {
                        		name = v;
                        	}
                        } else {
                            if (me.isKeyPA(e)) {
                                name = 'PA' + (e.which - 111);
                                return false;
                            }
                        }

                        if (me.isKeyReset(e) && v === 'RESET') {
                            return false;
                        }
                        if (me.isKeyEnter(e) && v === 'ENTER') {
                            return false;
                        }
                        if (me.isKeySysReq(e) && v === 'SYSREQ') {
                        	name = v;
                            return false;
                        }
                        if (me.isKeyAttn(e) && v === 'ATTN') {
                            return false;
                        }
                    }
                    return true;
                });
                return name;
            },

            /**
             * Process remote command keys
             */
            processRemote: function (e, cfg) {
                var me = this, name = me.getCommandName(e);
                if (name.length === 0) return;
                me.isProtected(cfg);
                me.trigger('command', e, name, cfg);
            },

            /**
             * Process local field navigation keys
             */
            processNav: function (e, cfg) {

                var pr, bck, rl = false, col = 0, row = 0, me = this;

                var o = cfg.obj;
                if (cfg.locked) {
                    return;
                }

                if (KEY_NAV.TAB === e.which) {

                    me.isRTL = false;
                    cfg.isRtl = false;
                	if ((e.shiftKey)) {
                		me.trigger('prev', e, cfg);
                	} else {
                		me.trigger('next', e, cfg);
                	}
                	return;
                }

                // shift used for selection
                if (e.shiftKey) {
                	return;
                }

                col = cfg.col;
                row = cfg.row;

                if (KEY_NAV.RIGHT === e.which) {
                	col++;
                } else if (KEY_NAV.LEFT === e.which) {
                	col--;
                }

                if (KEY_NAV.UP === e.which) {
                	row--;
                } else if (KEY_NAV.DOWN === e.which) {
                	row++;
                }

                me.focusRowCol(row, col, cfg);
            },

            /**
             * Switch input mode ins/owr
             */
            switchOwr : function(cfg) {
            	var color, me = this;
                me.ower = !me.ower;
                cfg.ower = me.ower;

                if (me.ower) {
                	color = '#949494';
                } else {
                	color = 'yellowgreen';
                }

                jss.set('.focus', {'background-color': color + ' !important'})
                me.trigger('mode', 1, cfg);
            },


            /**
             * Process local modification keys
             */
            processMod: function (e, cfg) {

                var rl, me = this;

                if (KEY_MOD.INSERT === e.which) {
                	return me.switchOwr(cfg);
                }

                if (cfg.locked || me.isProtected(cfg)) {
                	me.trigger('error:input', cfg);
                    return;
                }

                if (me.isKeyFE(e)) {
                    me.isRTL = false;
                    cfg.isRtl = false;
                    return me.trigger('fex', e, cfg);
                }

                if (me.isKeyNewLine(e)) {
                	return me.trigger('newLine', e, cfg);
                }

                if (KEY_MOD.HOME === e.which) {
                    return me.trigger('home', e, cfg);
                }

                if (KEY_MOD.END === e.which) {
                	return me.trigger('end', e, cfg);
                }

                if (KEY_MOD.DELETE === e.which) {
                	if (e.shiftKey) {
                		return me.trigger('erase', e, cfg);
                	}
                	return me.trigger('del', e, cfg);
                }

                if (KEY_MOD.BACKSPACE === e.which) {
                	return me.trigger('back', e, cfg);
                }

            },

            /**
             * Process input keys for input fields
             */
            processChr: function (e, cfg) {
                var i, o, rtl, c, l, cl, me = this;
                if (cfg.locked || me.isProtected(cfg)) {
                	me.trigger('error:input', cfg);
                	return;
                }
                if (KEY_MOD.ENTER === e.which) return;

                me.trigger('input', e, cfg);
            },

            /**
             * Keyboard pre-processing,
             * catch key events before char keys
             */
            onKeyDown: function (e, cfg) {
                var me = this;

                if (me.isKeyMod1(e)) {
                	me.stopEvent(e);
                    me.processMod(e, cfg);
                    return;
                }

                if (me.isKeyNav(e)) {
                	me.stopEvent(e);
                    me.processNav(e, cfg);
                    return;
                }

                if (me.isKeyMod(e) || me.isKeyRemote(e, cfg)) {
                	me.stopEvent(e);
                	return;
                }

                if (me.isSwitch(e)) {
                	me.stopEvent(e);
                	return;
                }

            },

            /**
             * Keyboard post-processing,
             * catch key events after char keys
             */
            onKeyUp : function(e, cfg) {

            	var me = this;

                // copy paste keys presssed
            	if (e.which === 86 || e.which === 65 || e.which ===  67) {
            		me.cpa = true;
            	}

                // copy paste released
            	if (e.which === 17 && me.cpa) {
            		me.cpa = false;
            		return;
            	}

            	// system commands
                if (me.isKeyRemote(e, cfg)) {
                	me.stopEvent(e);
                    me.processRemote(e, cfg);
                    return;
                }

                // editing keys
                if (me.isKeyMod(e) && !me.isKeyMod1(e)) {
                	me.stopEvent(e);
                	if (!me.isKeyFE(e)) {
                		me.processMod(e, cfg);
                	}
                    return;
                }

                // flip RTL / LTR
                if (me.isSwitch(e)) {
                    me.stopEvent(e);
                    me.isRTL = !me.isRTL;
                    cfg.isRtl = me.isRTL;
                    me.trigger('mode', 2, cfg);
                    return;
                }

            },

            // position cursor on click
            onSingleClick : function(e, cfg) {
            	var me = this;
            	var t = me.findEl(e.pageX, e.pageY, cfg);
            	if (t) {
            		if (t.classList.contains('btn')) {
            			// support for mouse click on detected Fnn functions
            			me.trigger('click:button', t, t.parentNode.sectionRowIndex, t.cellIndex, cfg);
            		} else {
            			// position cursor
            			me.trigger('click:single', t, t.parentNode.sectionRowIndex, t.cellIndex, cfg);
            		}

            	}
            },

            // send to the host new cursor position, support for selectable scren elements
            onDoubleClick : function(e, cfg) {
            	var me = this;
            	var t = me.findEl(e.pageX, e.pageY, cfg);
            	if (t) {
                	if (t.getAttribute("g") === "true") return;
                	if (t.classList.contains('btn')) return ;
            		me.trigger('click:double', t, t.parentNode.sectionRowIndex, t.cellIndex, cfg);
            	}
            },

            /**
             * Stop all event processing
             */
            stopEvent : function(e) {
                e.stopPropagation();
                e.preventDefault();
            },

            /**
             * Remove all listeners
             */
            unbind : function(keys) {

                $(doc).off('.mouse');
                if (keys === true) {
                	$(doc).off('.keyboard');
                }
            },

            /**
             * Attach events to generated screen
             */
            attach: function (cnf) {
                var me = this, cfg = cnf;

                me.isRTL = false;

                if (cfg.events) return;
                cfg.events = true;

                me.unbind(true);

                // $('#' + cfg.id) // issues with screens without input fields
                $(doc).on('keydown.keyboard', function (e) {
                    	me.onKeyDown(e, cfg);
                    })
                    .on('keyup.keyboard', function (e) {
                    	me.onKeyUp(e, cfg);
                    })
                    .on('keypress.keyboard', function (e) {
                    	me.stopEvent(e);
                        me.processChr(e, cfg);
                    })
                    .on('paste.keyboard', function (e) {
                    	me.stopEvent(e);
                        me.paste(e, cfg);
                    });

                // hotspot cursor positioning
                $(doc)
                .on('click.mouse', function (e) {
                	me.onSingleClick(e, cfg);
                })
                .on('dblclick.mouse', function (e) {
                	me.onDoubleClick(e, cfg);
                });

            }
        };

    keyboard.init();

    // public methods available outside closure

    return {

        // set keyboard mode
        setMode: function (mode) {
            keyboard.setMode(mode);
        },

        // reattach events afte rrerendering
        attach: function (cfg) {
            return keyboard.attach(cfg);
        },

        unbind : function(keys) {
        	return keyboard.unbind(keys);
        },

        // lsiten for events of this object
        listen: function (name, callback) {
            return keyboard.listen(name, callback);
        }
    };

}());
