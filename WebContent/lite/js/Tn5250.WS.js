/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

/*jslint node: true */
"use strict";

var Tn5250 = Tn5250 || {};

/**
 * Base WebSoket Direct API Engine
 * Can link request with transaction ID with callbacks
 * events : data, close, open
 */
Tn5250.WS = (function () {

    var win = window;

    if (!win.WebSocket) {
        alert("WebSocket NOT supported by your Browser!");
    }

    var tid = 0;
    var transactions = {};
    var protocol = {"http:" : "ws:", "https:":"wss:"};

    // generate url address for websocket
    function getAddr() {
        var el = win.location.href.split('/').slice(1, 4);
        el.unshift(protocol[win.location.protocol]);
        el.push('socket');
        return el.join('/');
    }

    var ws = new win.WebSocket(getAddr(), "ws4is");
    ws.tid = 0;
    ws.transactions = {};

    var wsapi = {};
    MicroEvent.mixin(wsapi);

    // clean timeouted transactions
    ws.cleanup = function () {

        var t, dif, l, k = Object.keys(ws.transactions);
        l = k.length;
        t = Date.now();

        while (l--) {
            dif = t - ws.transactions[k[l]].ts;
            if (dif > 3000) {
                delete ws.transactions[k[l]];
            }
        }

    };

    // send data through websocket from Direct API
    wsapi.send = function (data, callback) {
        ws.tid++;
        ws.transactions[ws.tid] = {cb: callback, ts: Date.now()};
        data.type = "rpc";
        data.tid = ws.tid;
        var json = {"type": "ws", "cmd": "data", "data": [data]};
        ws.send(JSON.stringify(json));
    };

    // trigger when WebSocket connection open
    ws.onopen = function () {
        ws.iid = setInterval(ws.cleanup, 5000);
        wsapi.trigger('open');
    };

    // handle received message
    ws.processMessage = function (data, obj) {

        if (!obj) {
            return;
        }

        var tr = ws.transactions[obj.tid];

        if (tr) {
            delete ws.transactions[obj.tid];
            if (typeof tr.cb === 'function') {
                return tr.cb(obj);
            }
        }

        if (data.cmd === 'err') {
        	wsapi.trigger('error', null, obj);
        } else {
        	wsapi.trigger('data', obj);
        }

    };

    // trigger when WebSocket message received
    ws.onmessage = function (evt) {

        var data = JSON.parse(evt.data);

        if (Array.isArray(data.data)) {

            data.data.every(function (obj) {

                try {
                    ws.processMessage(data, obj);
                } catch (e) {
                    wsapi.trigger('error', e, obj);
                }
                return true;
            });

        } else {
            ws.processMessage(data, data.data);
        }
    };

    // trigger when WebSocket connection closed
    ws.onclose = function () {
        clearInterval(ws.iid);
        wsapi.trigger('close');
    };

    return wsapi;

});
