/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * Decode field mask received from server,
 * mask is encoded in bit's
 */
Tn5250.Decoder = (function () {

    // string lpad function
    function lpad(padString, length) {
        return new Array(length + 1).join(padString);
    }

    // match Fn(n) text to generate clickable buttons
    var btnmatch = /F\d{1,2}=/;
    var urlmatch = /((http|https|ftp|ftps|mailto)\:\/\/)/;

    // 5250 attribute to color map
    var colorMap = {
        "32": "green",
        "33": "green-rv",
        "34": "white",
        "35": "white-rv",
        "36": "green-ul",
        "37": "green-rv-ul",
        "38": "white-ul",
        "39": "nondisp",
        "40": "red",
        "41": "red-rv",
        "42": "red-hi",
        "43": "red-hi-rv",
        "44": "red-ul",
        "45": "red-ul-hi-rv",
        "46": "red-ul-hi",
        "47": "non-disp",
        "48": "turq-cs",
        "49": "turq-rv",
        "50": "yellow",
        "51": "yellow-rv",
        "52": "turq-ul",
        "53": "turq-rv-ul",
        "54": "yellow-ul",
        "55": "nondisp",
        "56": "pink",
        "57": "pink-rv",
        "58": "blue",
        "59": "blue-rv",
        "60": "pink-ul",
        "61": "pink-rv-ul",
        "62": "blue-ul",
        "63": "nondisp"
    };

    var Decoder = {

        isButton: function (o) {
            return o.t.search(btnmatch) > -1;
        },

        isURL: function (o) {
            return o.t.search(urlmatch) > -1;
        },

        isBreak: function (o) {
            return (o && o.br) ? o.br : false;
        },

        isField: function (o) {
            return (o && o.d) ? o.d[2] > 0 : false;
        },

        isHidden: function (o) {
            return (o && o.d) ? o.d[0] : -1;
        },

        getFieldType: function (o) {
            return (o && o.d) ? o.d[1] : -1;
        },

        getFieldId: function (o) {
            return (o && o.d) ? o.d[2] : -1;
        },

        getAttributeId: function (o) {
            return (o && o.d) ? o.d[3] : -1;
        },

        getLength: function (o) {
            return (o && o.d) ? o.d[4] : -1;
        },

        getMaxLength: function (o) {
            return (o && o.d) ? o.d[5] : -1;
        },

        getRow: function (o) {
            return (o && o.d) ? o.d[6] : -1;
        },

        isRightAlign: function (o) {
            return (o && o.d) ? o.d[7] === 1 : false;
        },

        getButton: function (v) {
            return {
                b1: v.split(btnmatch),
                b2: v.match(btnmatch)[0].replace('=', '')
            };
        },

        getValue: function (o) {

            var _o = o || {};

            if (this.isBypassField(_o)) {
                if (_o.hasOwnProperty('t') && _o.t.length < 2) {
                    return lpad(" ", this.getLength(_o));
                }
            }

            return (o && o.t) ? o.t : '';
        },

        isBlink: function (o) {
            var id = this.getAttributeId(o);
            return id == 42 || id == 43 || id == 46;
        },


        getClass: function (o) {

            var me = this,
                cm = colorMap,
                code = me.getAttributeId(o).toString();

            if (cm.hasOwnProperty(code)) {
                return cm[code];
            }

            return 'green';
        },

        _check: function (o, v) {
            var i = this.getFieldType(o);
            return (i >> v) & 1;
        },

        isSelection: function (o) {
            return this._check(o, 15);
        },

        isRightToLeft: function (o) {
            return this._check(o, 14);
        },

        isFocused: function (o) {

            if (this.getFieldId(o) > 1000) {
                return false;
            }

            return this._check(o, 13);
        },

        isAutoEnter: function (o) {
            return this._check(o, 12);
        },

        isBypassField: function (o) {
            return this._check(o, 11);
        },

        isContinued: function (o) {
            return this._check(o, 10);
        },

        isContinuedFirst: function (o) {
            return this._check(o, 9);
        },

        isContinuedLast: function (o) {
            return this._check(o, 8);
        },

        isContinuedMiddle: function (o) {
            return this._check(o, 7);
        },

        isDupEnabled: function (o) {
            return this._check(o, 6);
        },

        isFER: function (o) {
            return this._check(o, 5);
        },

        isHiglightedEntry: function (o) {
            return this._check(o, 4);
        },

        isMandatoryEnter: function (o) {
            return this._check(o, 3);
        },

        isNumeric: function (o) {
            return this._check(o, 2);
        },

        isSignedNumeric: function (o) {
            return this._check(o, 1);
        },

        isToUpper: function (o) {
            var i = this.getFieldType(o);
            return (i & 1);
        }
    };

    return Decoder;

}());
