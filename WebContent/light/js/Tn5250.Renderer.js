/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * Engine to render screen from received data
 */
Tn5250.Renderer = (function () {

    var doc = document;

    var renderer = {

        // intialization
        init: function (config) {

            var me = this;
            me.screenEl = Tn5250.Decoder;

            me.sh = {
            	tb : doc.createElement('table'),
            	tbody : doc.createElement('tbody'),
                td: doc.createElement('td'),
                tr: doc.createElement('tr')
            };

            me.overlay = $('.overlay');
            me.content = $('.view');

        },

        /**
         * Build flag for field
         */
        getFlags: function (jobj) {

            var SCR = Tn5250.Decoder;

            var cfg = {
                n: SCR.getRow(jobj),
                b: SCR.isBlink(jobj),
                v: SCR.getValue(jobj),
                s: SCR.getClass(jobj),
                h: SCR.isHidden(jobj),
                p: SCR.isField(jobj),
                t: SCR.isBypassField(jobj),
                z: SCR.isButton(jobj),
                l: SCR.getLength(jobj),
                r: SCR.isRightAlign(jobj),
                c:  SCR.isRightToLeft(jobj),
                ss: ''
            };

            if (cfg.b) {
                cfg.ss = cfg.ss + ' blink';
            }

            return cfg;
        },

        /**
         * Generate clickable button
         */
        generateButton: function (row, params, jobj, cfg, SCR) {

            var me, id, b, v, cell, i;

            me = this;
            id = Object.keys(params.buttons).length + 1;
            v = cfg.v;
            b = SCR.getButton(v);
            v = b.b1[0];
            params.buttons[id] = jobj;
            me.generateCells(row, v, cfg.s + cfg.ss);
            i=0;

            while (i<b.b2.length) {
                cell = me.sh.td.cloneNode();
                cell.setAttribute('data', 'P' + b.b2);
                cell.className = cfg.s + ' btn';
                cell.innerHTML = b.b2[i];
                row.appendChild(cell);
            	i++;
            }

            cfg.v = '=' + b.b1[1];

        },

        /**
         * Fill grid cells
         */
        generateCells: function (row, v, s) {

            var j, me = this, cell = null, k = v.length;

            for (j = 0; j < k; j++) {

                cell = me.sh.td.cloneNode();
                cell.className = s;

                if (v[j] === ' ') {
                    cell.innerHTML = '\u00a0';
                } else {
                    cell.innerHTML = v[j];
                }

                row.appendChild(cell);

            }
        },

        /**
         * Mark cells as fields
         */
        generateField: function (row, cfg, jobj, params) {

            var a, id, i, j, l, n, cell, field, me = this, SCR = Tn5250.Decoder;

            id = 'f' + SCR.getFieldId(jobj);
            n = cfg.l; //SCR.getLength(jobj);
            l = jobj.t.length;

            // init tmp array
            //jobj.x = Array(n).fill(' '); - not in ie
            jobj.x = new Array(n+1).join(' ').split('');
            i = 0;
            j=0;

            if (cfg.c) {
            	i = n - l;
            }

            while (j < l) {
                jobj.x[i] = jobj.t[j];
                i++;
                j++;
            }

            params.fields[id] = jobj;
            if (SCR.isFocused(jobj)) {
                params.f = id;
                params.active = id;
                params.obj = jobj;
            }

            // generate td's
            for (i = 0; i < n; i++) {
                cell = me.sh.td.cloneNode();

                if (cfg.h) {
                    cell.className = 'fld green txt_green';
                } else {
                    cell.className = ' fld ' + cfg.s + ' txt_' + cfg.s.split('-')[0];
                }

                if (SCR.isToUpper(jobj)) {
                    cell.className = cell.className + ' upper';
                }

                cell.setAttribute("g", id);
                cell.id = id + '_' + i;

                if (SCR.isRightToLeft(jobj)) {
                    cell.dir = 'rtl';
                }

                if (jobj.x[i] === ' ') {
                	cell.innerHTML = '\u00a0';
                } else {
                	cell.innerHTML = jobj.x[i];
                }

                row.appendChild(cell);
            }
        },

        /**
         * Fill up the screen grid
         */
        generate: function (data, params) {
            var me = this,
                k = 0, fragment;

            var SCR = Tn5250.Decoder;
            var tbl, tbody, row, coll, jobj, cfg, f, n, r, c, t1, t2;

            fragment = doc.createDocumentFragment();

            params.id = 'tn_' + Date.now();
            tbl = me.sh.tb.cloneNode();
            tbl.id = params.id;
            tbl.className = 'tngrid';
            tbl.setAttribute('did', params.displayID);
            tbl.setAttribute('style', 'display:none;');
            tbody = me.sh.tbody.cloneNode();
            tbl.appendChild(tbody);
            fragment.appendChild(tbl);

            r = -1;
            for (var i = 0, l = data.length; i < l; i++) {

                jobj = data[i];
                cfg = me.getFlags(jobj);

                if (cfg.n > r) {
                    if (n > 0 && c < me.hsize) {
                        c = me.hsize - c;
                        coll = me.sh.td.cloneNode();
                        coll.colSpan = c;
                        row.appendChild(coll);
                    }
                    row = me.sh.tr.cloneNode();
                    row.id = 'r' + cfg.n;
                    tbody.appendChild(row);
                    c = 0;
                }
                r = cfg.n;

                if (cfg.p) {
                    if (cfg.t) {

                        c = c + cfg.l;
                        t1 = cfg.l - cfg.v.length;
                        t2 = new Array(t1 + 1).join(" ");

                        if (cfg.r) {
                            me.generateCells(row, t2, cfg.s + cfg.ss);
                        }

                        me.generateCells(row, cfg.v, cfg.s + cfg.ss);

                        if (!cfg.r) {
                            me.generateCells(row, t2, cfg.s + cfg.ss);
                        }

                    } else {
                        me.generateField(row, cfg, jobj, params);
                        c = c + cfg.n;
                    }
                } else {

                    c = c + cfg.v.length;

                    if (!cfg.h && cfg.z) {
                        me.generateButton(row, params, jobj, cfg, SCR);
                    }

                    me.generateCells(row, cfg.v, cfg.s + cfg.ss);

                }
            }

            tbl.removeAttribute('style');
            return fragment;
        },

        /**
         * Render shadow DOM to the browser
         */
        renderFragment: function (fragment) {
            this.content.html('')[0].appendChild(fragment);
        },

        /**
         * Main rendering method
         */
        render: function (data, displayID, size) {
            var frag, cfg, me = this;
            cfg = {
            		fields: {},
            		buttons: {},
            		f: null,
            		displayID: displayID,
            		cols : size,
            		rows : size === 80 ? 24 : 27,
            		col :0,
            		row :0,
            		ower : true,
            		isRtl  :false,
            		blank : false
            		};
            frag = me.generate(data, cfg);
            me.renderFragment(frag);
            return cfg;
        }

    };

    renderer.init();

    // Public methods available outside closure
    return {

        render: function (data, displayID, size) {
            return renderer.render(data, displayID, size);
        },

        clear: function (cfg) {
            return renderer.clear(cfg);
        }
    };

}());
