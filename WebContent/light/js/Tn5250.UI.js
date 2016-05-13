/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * 5250 UI handler.
 */
Tn5250.UI = (function () {

    var doc = document;
    var win = window;

    var interactive = {

            /**
             * Base initialization
             */
            init: function (config) {
                var me = this;
                me.overlay = $('.overlay');
                MicroEvent.mixin(me);
            },

            /**
             * Auto resize font based on screen resize
             */
            resizer: function () {

                var me = this, el = doc.body.querySelector('.tngrid');

                if (!el) {
                    return;
                }

                var rows = $(el).find('tr').length;
                var div = {
                    w: rows > 26 ? 132 : 80,
                    h: rows > 26 ? 27 : 24
                };

                var fontsize = ((win.innerWidth / div.w) / 0.63);
                if (div.w === 80) {
                    var fsize2 = win.innerHeight / (div.h + 2) / 1.2;
                    if (fsize2 < fontsize) fontsize = fsize2;
                }

                me.fontsize = Math.floor(fontsize);
                el.style.fontSize = me.fontsize + 'px';

                if (me.rule) {
                    me.lsp = (el.offsetWidth / div.w) - (me.fontsize * 0.635);
                    me.rule.style.letterSpacing = me.lsp + '5px';
                }
            },

            /**
             * Check if character is in RTL character range
             */
            isRtlChar: function (c) {
                if (
                    (c === 0x05BE) || (c === 0x05C0) || (c === 0x05C3) || (c === 0x05C6) ||
                    ((c >= 0x05D0) && (c <= 0x05F4)) ||
                    (c === 0x0608) || (c === 0x060B) || (c === 0x060D) ||
                    ((c >= 0x061B) && (c <= 0x064A)) ||
                    ((c >= 0x066D) && (c <= 0x066F)) ||
                    ((c >= 0x0671) && (c <= 0x06D5)) ||
                    ((c >= 0x06E5) && (c <= 0x06E6)) ||
                    ((c >= 0x06EE) && (c <= 0x06EF)) ||
                    ((c >= 0x06FA) && (c <= 0x0710)) ||
                    ((c >= 0x0712) && (c <= 0x072F)) ||
                    ((c >= 0x074D) && (c <= 0x07A5)) ||
                    ((c >= 0x07B1) && (c <= 0x07EA)) ||
                    ((c >= 0x07F4) && (c <= 0x07F5)) ||
                    ((c >= 0x07FA) && (c <= 0x0815)) ||
                    (c === 0x081A) || (c === 0x0824) || (c === 0x0828) ||
                    ((c >= 0x0830) && (c <= 0x0858)) ||
                    ((c >= 0x085E) && (c <= 0x08AC)) ||
                    (c === 0x200F) || (c === 0xFB1D) ||
                    ((c >= 0xFB1F) && (c <= 0xFB28)) ||
                    ((c >= 0xFB2A) && (c <= 0xFD3D)) ||
                    ((c >= 0xFD50) && (c <= 0xFDFC)) ||
                    ((c >= 0xFE70) && (c <= 0xFEFC)) ||
                    ((c >= 0x10800) && (c <= 0x1091B)) ||
                    ((c >= 0x10920) && (c <= 0x10A00)) ||
                    ((c >= 0x10A10) && (c <= 0x10A33)) ||
                    ((c >= 0x10A40) && (c <= 0x10B35)) ||
                    ((c >= 0x10B40) && (c <= 0x10C48)) ||
                    ((c >= 0x1EE00) && (c <= 0x1EEBB))
                ) {
                    return 1;
                }
                return 0;
            },

            isRtl : function (cfg) {
            	return  Tn5250.Decoder.isRightToLeft(cfg.obj) || cfg.isRtl;
            },

            /**
             * Update current field UI from processed field value
             */
            updateUIField: function (cfg) {

                var o, v, h, me = this;
                o = cfg.obj;
                h = Tn5250.Decoder.isHidden(o);

                $("#" + cfg.id).find('.fld[g="' + cfg.active + '"]').each(function (idx, el) {
                    v = '\u00a0';
                    if (o.x[idx]) {
                        if (o.x[idx] !== ' '){
                        	if (h) {
                        		v = '\u2022';
                        	} else {
                        		v = o.x[idx];
                        	}
                        }
                      }
                    el.innerHTML = v;
                });
            },

            /**
             * Focus to selected field
             */
            focusField: function (id, seg, cfg) {

                var p, b, f = $('#' + id + '_' + seg);
                b = f.length > 0;

                if (b) {
                    //f.focus();
                    cfg.active = id;
                    cfg.seg = parseInt(seg,10);
                    cfg.obj = cfg.fields[id];
                    this.focusRowCol(f[0].parentNode.sectionRowIndex, f[0].cellIndex, cfg);
                } else {
                    //$('#' + cfg.id).focus();
                    $(doc).focus();
                }

                return b;
            },

            /**
             * if focus on field, set field as active
             */
            postFocusRowCol : function(cfg) {

            	var el = doc.body.querySelector('.tngrid');

            	if (el.rows && cfg.row < el.rows.length) {
            		el = el.rows[cfg.row].cells[cfg.col];
                	if (el && el.id) {
                  	  var fs = el.id.split('_');
                  	  this.focusField(fs[0], fs[1] ,cfg);
                  	}
            	}
            },

            /**
             * Move cursor to new location
             */
            focusRowCol : function(row, col, cfg) {

            	var o, el = doc.body.querySelector('.tngrid');

            	if (el.rows && cfg.row < el.rows.length) {
            		$(el.rows[cfg.row].cells[cfg.col]).removeClass('focus');
                	cfg.row = row;
                	cfg.col = col;
                	$(el.rows[cfg.row].cells[cfg.col]).addClass('focus');
            	}

            },

            /**
             * Go to first cursor position in current field
             */
            firstFieldSeg: function (cfg) {

                var rl, l, me = this;
                l = Tn5250.Decoder.getLength(cfg.obj);
                rl = me.isRtl(cfg);

                if (rl) {
                   return me.focusField(cfg.active, l-1, cfg);
                } else {
                  return me.focusField(cfg.active, 0, cfg);
                }

            },

            /**
             * Go to last cursor position in current field
             */
            lastFieldSeg: function (cfg) {
                var o, p, me = this;
                o = cfg.obj;
                p = me.getFieldSeg(cfg, cfg.obj);
                return  me.focusField(cfg.active, p, cfg);
            },

            /**
             * Go to previous cursor position in current field
             */
            prevFieldSeg: function (cfg) {

                var p = cfg.seg - 1;

                if (p >= 0) {
                    return this.focusField(cfg.active, p, cfg);
                }

                return false;
            },

            /**
             * Go to next cursor position in current field
             */
            nextFieldSeg: function (cfg) {

                var p, l, o, me = this;
                o = cfg.obj;
                l = Tn5250.Decoder.getLength(o);

                p = cfg.seg + 1;
                if (p < l) {
                    return me.focusField(cfg.active, p, cfg);
                }
                return false;
            },

            /**
             * Get current field cursor position.
             * Support for RTL
             */
            getFieldSeg: function (cfg, o, fex) {

                var rl, l, c, tl, ps, me = this;
                rl = me.isRtl(cfg);
                l = Tn5250.Decoder.getLength(o);

                // if field exit
                if (fex) {
                  if (rl) {
                	  return l-1;
                  } else {
                	  return 0;
                  }
                }

                ps = [];
                c = l;
                while (c--) {
                  if (o.x[c] && o.x[c]!=='' && o.x[c]!==' ') {
                    ps.push(c);
                  }
                }

                if (ps.length===0) {
                   if (rl) {
                     return l-1;
                   } else {
                     return 0;
                   }
                }

                if (rl) {
                  c = ps[ps.length-1];
                  if(c>0) c--;
                } else {
                  c = ps[0];
                  if (c<l-1) c++;
                }

                return c;
            },

            /**
             * Go to next field
             */
            nextField: function (cfg, fex) {

                var i, k, p, me = this;
                k = Object.keys(cfg.fields);
                p = k.indexOf(cfg.active) + 1;

                if (p === k.length) {
                    p = 0;
                }

                i = k[p];
                me.focusField(i, 0, cfg);
            },

            /**
             * Go to previous field
             */
            prevField: function (cfg, fex) {

                var i, k, p, me = this;

                k = Object.keys(cfg.fields);
                p = k.indexOf(cfg.active) - 1;

                if (p < 0) {
                    p = k.length - 1;
                }
                i = k[p];

                //p = me.getFieldSeg(cfg, cfg.fields[i], fex);
                //me.focusField(i, p, cfg);
                me.focusField(i, 0, cfg);
            },

            /**
             * Go to next field
             */
            newLine: function (cfg, fex) {

                var i, k, p, me = this;

                k = Object.keys(cfg.fields).filter(function(fld){
                    return fld.length<5;
                });

                p = k.indexOf(cfg.active) + 1;

                if (p === k.length) {
                    p = 0;
                }

                i = k[p];
                p = me.getFieldSeg(cfg, cfg.fields[i], fex);
                me.focusField(i, p, cfg);
            },

            /**
             * Process field exit key
             */
            fieldExitField: function (e, cfg) {

            	if (!cfg.obj) {
                    return;
                }

                var rl, l, me = this;
                var o = cfg.obj;
                rl = me.isRtl(cfg);
                l = cfg.seg;

                if (rl) {
                    l++;
                    //l = Array(l).fill(' ').concat(o.x.splice(l));
                    l = Array(l+1).join(' ').split('').concat(o.x.splice(l));
                    o.x = l;
                } else {
                    o.x = o.x.slice(0, l).concat(Array(Tn5250.Decoder.getLength(o) - l + 1).join(' ').split(''));
                }

                o.c=true;
                me.updateUIField(cfg);

                if (e.shiftKey) {
                    me.prevField(cfg, true);
                } else {
                    me.nextField(cfg, true);
                }

                return true;
            },

            /**
             * Process del key press
             * TODO RTL me.isRtlChar(o.x[p])
             */
            delChar: function (cfg) {

                var rtl, p, l, o, me = this;

                o = cfg.obj;
                l = Tn5250.Decoder.getLength(o);
                p = cfg.seg;
                rtl = me.isRtl(cfg);

                if (p > -1) {
                	var space = [' '];
                   if (rtl) {
                    o.x.splice(p, 1);
                    //o.x = Array(1).fill(' ').concat(o.x);
                    o.x = space.concat(o.x);
                   } else {
                    o.x.splice(p, 1);
                    //o.x = o.x.concat(Array(1).fill(' '));
                    o.x = o.x.concat(space);
                   }
                   o.c=true;
                }
                me.updateUIField(cfg);
            },

            /**
             * Process backspace key press
             * TODO RTL me.isRtlChar(o.x[p])
             */
            backChar: function (cfg) {

                var rtl, p, l, o, me = this;

                o = cfg.obj;
                l = Tn5250.Decoder.getLength(o);
                rtl = me.isRtl(cfg);
                p = cfg.seg;
                p--;

                if (p > -1) {

                  var space = [' '];

                  if (rtl) {
                    o.x.splice(p+2, 1);
                    o.x = space.concat(o.x);
                  } else {
                    o.x.splice(p, 1);
                    o.x = o.x.concat(space);
                  }

                  o.c=true;
                }
            },

            /**
             * Handle text paste into simulated input fields
             */
            paste: function (v, cfg) {

                var rl, l, p, t, o, me = this;

                o = cfg.obj;
                l = Tn5250.Decoder.getLength(o);
                p = cfg.seg;
                rl = me.isRtl(cfg);
                v = v.substr(0, l - cfg.seg).split('');

                if (rl) {
                	p = l - cfg.seg -1;
                	v.reverse();
                	o.x.reverse();
                }

           	    t = o.x.splice(0, p);
        	    t = t.concat(v);
        	    o.x = t.splice(0, l);
                o.c=true;

                if (rl) {
                	o.x = new Array(l-o.x.length).concat(o.x.reverse());
                }

                me.updateUIField(cfg);

                if (rl) {
                	me.prevFieldSeg(cfg);
                } else {
                	me.lastFieldSeg(cfg);
                }
            },

            /**
             * Show prompt for sysreq value
             */
            doSysReq : function (name, cfg) {

                var  v = null, me = this;

                if (name === 'SYSREQ' ) {

                	cfg.sysreq = true;
                	v = win.prompt('System Request - Enter value','');

                	setTimeout( function cleanSysreq() {
                		cfg.sysreq = false;
                	}, 1000 );

                	if (!(v && v.length >0)) {
                		v = ' ';
                	}
                }

                return v;
            },

            /**
             * Prepare input field data for remote request
             */
            sendRemote: function (name, cfg) {

                var me = this;
                cfg.locked = true;
                cfg.inhibited = true;

                var req = {
                    keyRequest: name,
                    blank : cfg.blank,
                    cursorField: Tn5250.Decoder.getFieldId(cfg.obj),
                    cursorCol: cfg.col + 1,
                    cursorRow: cfg.row + 1,
                    displayID: cfg.displayID,
                    data : me.doSysReq(name, cfg)
                };

                var v, i, f, k = Object.keys(cfg.fields);
                var flds = [];

                k.every(function (v, idx) {

                    f = cfg.fields[v];
                    var id = Tn5250.Decoder.getFieldId(f);

                    if (id > 1000) {
                    	// issue with trim, i don't know is it space or continuation
                        //flds[i].t = flds[i].t.trim() + f.x.join('');
                    	flds[i].t = flds[i].t + f.x.join('');
                    } else {

                    	if (Tn5250.Decoder.isRightToLeft(f)) {
                    		v = f.x.reverse().join('');
                    	} else {
                    		v = f.x.join('');
                    	}

                    	f.t = v.slice(0,Tn5250.Decoder.getLength(f));
                        flds.push({
                            d: [0, 0, id],
                            t: f.t,
                            c: f.c
                        });
                        i = idx;
                    }
                    return true;
                });

                me.trigger('send', req, flds, cfg);
            },

        	/**
        	 * Send cursor position request
        	 */
        	sendCursor: function(row, col, cfg) {

    			var request = {
    					cursorCol: col + 1,
    	                cursorRow: row + 1,
    	                blank : true,
    	                data: null,
    	                displayID: cfg.displayID,
    	                keyRequest: "ENTER"
                      };
    			this.trigger('send', request, [], cfg);
        	},

            /**
             * Update screen based on lock/unlock status
             */
            updateUI: function (obj, cfg) {

            	var me = this;

                if (obj) {

                	if (obj.locked) {
                		Tn5250.Keyboard.unbind(false);
                	}

                	me.focusRowCol(obj.row, obj.col, cfg);
                }

                if (cfg.obj) {
                	me.firstFieldSeg(cfg);
                }
            },

            /**
             * Update screen based on lock/unlock status
             */
            updateMode: function (obj, cfg) {

            	var me = this;

                if (cfg.obj) {
                	me.firstFieldSeg(cfg);
                }
            },

            /**
             * Process local modification keys
             */
            backSpace: function (cfg) {

            	var me = this;
            	me.backChar(cfg);

            	if (cfg.isRtl) {
                   me.nextFieldSeg(cfg);
                } else {
                   me.prevFieldSeg(cfg);
                }

                me.updateUIField(cfg);
            },

            /**
             * Process input keys for input fields
             */
            processChr: function (e, cfg) {

                var i, o, rtl, c, l, cl, me = this;

                c = String.fromCharCode(e.which);
                o = cfg.obj;
                o.c = true;
                l = Tn5250.Decoder.getLength(o);
                i = cfg.seg;
                rtl = me.isRtl(cfg); //|| me.isRtlChar(e.which);

                if (cfg.ower) {
                    o.x[i] = c;
                } else {

                   cl = o.x.join('').trim().length;

                   if (cl < l) {

                       if(rtl) {
                           o.x.splice(i+1, 0, c);
                           if (o.x.length>l) {
                             //o.x.splice(l-o.x.length);
                             o.x.shift();
                           }
                         } else {
                           o.x.splice(i, 0, c);
                           o.x.splice(l);
                         }

                  } else {
                      me.trigger('error:input', cfg);
                      return false;
                  }

                }

                me.updateUIField(cfg);
                return true;
            },

            /**
             * Insert cursor at position
             */
            insert: function (e, cfg) {

            	var rtl, me = this;

            	rtl = me.isRtl(cfg); //|| me.isRtlChar(e.which);

            	if (!me.processChr(e, cfg)) {
            		return;
            	}

                if (rtl) {
                    if (me.prevFieldSeg(cfg)) return;
                } else {
                    if (me.nextFieldSeg(cfg)) return;
                }

                me.nextField(cfg);
            },

            /**
             * Attach events to generated screen
             */
            attach: function (cnf) {

            	var me = this, cfg = cnf;
                me.focusField(cfg.active, 0, cfg);
            }
        };

    $(win).resize(interactive.resizer);
    interactive.init();

    return {

    	moveTo: function(row, col, cfg) {
    		interactive.focusRowCol(row, col, cfg);
    		interactive.postFocusRowCol(cfg);
    	},

    	fieldExit : function(e, cfg) {
    		return interactive.fieldExitField(e, cfg);
    	},

    	sendCursor: function(e, row, col, cfg) {
    		return interactive.sendCursor(row, col, cfg);
    	},

        // move to previous field
    	previousField : function(cfg) {
    		interactive.prevField(cfg, null);
    		interactive.firstFieldSeg(cfg);
    	},

    	// move next field
    	nextField : function(cfg) {
    		interactive.nextField(cfg, null);
    		interactive.firstFieldSeg(cfg);
    	},

        // move to begin of text in field
    	homeField : function(cfg) {
    		return interactive.firstFieldSeg(cfg);
    	},

    	// move end of text in field
    	endField : function(cfg) {
    		return interactive.lastFieldSeg(cfg);
    	},

    	// delete char of text in field
    	deleteField : function(cfg) {
    		return interactive.delChar(cfg);
    	},

    	// backspace char of text in field
    	backspaceField : function(cfg) {
    		return interactive.backSpace(cfg);
    	},

    	// insert new char
    	insertField : function(e, cfg) {
    		return interactive.insert(e, cfg);
    	},

        // move next field, skip multiline
    	newLine : function(cfg) {
    		interactive.newLine(cfg, null);
    		interactive.firstFieldSeg(cfg);
    	},

    	// paste command data
    	paste : function (data, cfg) {
    		return interactive.paste(data, cfg);
    	},

    	// send key command
    	command : function (data, cfg) {
    		return interactive.sendRemote(data, cfg);
    	},

        updateUI: function (obj, cfg) {
            return interactive.updateUI(obj, cfg);
        },

        updateMode: function (cfg) {
            return interactive.updateMode(cfg);
        },

        resizer: function () {
            return interactive.resizer();
        },

        attach: function (cfg) {
            return interactive.attach(cfg);
        },

        listen: function (name, callback) {
            return interactive.listen(name, callback);
        }
    };

}());
