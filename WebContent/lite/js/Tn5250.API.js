/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * TN5250 WebSocket API
 * Used to call remote services.
 * All Direct functions linked to io.greenscreens namespace
 */
Tn5250.API = (function () {

    var API = {
        "namespace": "io.greenscreens",
        "actions": {
            "Tn5250Controller": [
                {"name": "reloadDefinitions", "len": 0},
                {"name": "listDefinitions", "len": 0},
                {"name": "listSessions", "len": 0},
                {"name": "openSession", "len": 2},
                {"name": "openSessionAuto", "len": 7},
                {"name": "closeSession", "len": 1},
                {"name": "closeSessions", "len": 0},
                {"name": "refreshSession", "len": 1},
                {"name": "requestSession", "len": 2}
            ],
            "HostsController": [
                {"name": "listDefinitions", "len": 0},
                {"name": "reloadDefinitions", "len": 0}
            ]
        }
    };

    var ws = new Tn5250.WS();

    /**
     * API generator
     */
    function apiFn(params) {
        var prop = params;

        function fn() {

            var l, cb, args, req;

            l = prop.l + 1;
            if (arguments.length !== l && typeof arguments[prop.l] !== 'function') {
                throw 'Invalid arguments';
            }

            cb = arguments[prop.l];
            args = Array.prototype.slice.call(arguments);
            req = {
                "namespace": prop.n,
                "action": prop.c,
                "method": prop.m,
                "data": args.slice(0, args.length - 1)
            };

            ws.send(req, function (obj) {

                var sts = (prop.c === obj.action) && (prop.m === obj.method) && obj.result && obj.result.success;
                if (sts) {
                    return cb(obj.result, obj.result.data, sts);
                }
                cb(obj.result, null, sts);
                
            });
        }

        return fn;
    }

    /**
     * Create object tree structure like packages in Java
     * @return {[type]} [description]
     */
    function buildNamespace() {
        var tmp = null;
        API.namespace.split('.').every(function (v) {
            if (!tmp) {
                window[v] = {};
                tmp = window[v];
            } else {
                tmp[v] = {};
                tmp = tmp[v];
            }
            return true;
        });
        return tmp;
    }

    /**
     * From API tree generate namespace tree and
     * links generated functions to WebScoket api calls
     */
    function buildAPI() {

        var n, n1, e, f, l, fl,
            tmp = buildNamespace(),
            k = Object.keys(API.actions),
            kl = k.length;

        while (kl--) {
            n = k[kl];
            e = API.actions[n];
            tmp[n] = {};
            fl = e.length;

            while (fl--) {
                n1 = e[fl].name;
                l = parseInt(e[fl].len, 10) || 0;
                tmp[n][n1] = apiFn({n: API.namespace, c: n, m: n1, l: l});
            }

        }
    }

    /* not used here, just for reference
     ws.listen('data', function(data){

     });
     ws.listen('error', function(err, data){

     });
     ws.listen('close', function(){

     });
     */

    ws.listen('open', function () {
        buildAPI();
    });

    return ws;

});
