/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

// 5250 virtual keyboard, show on mouse over status bar
Tn5250.KeyBar = (function () {

	// map events to button
	var MAP = {
			        "ENTER":  { which : 13},
			        "SYSREQ": { which : 27, shiftKey : true},
			        "ATTN":   { which : 27},
			        "PGUP":   { which : 33},
			        "PGDOWN": { which : 34},
			        "PF1":  { which : 112},
			        "PF2":  { which : 113},
			        "PF3":  { which : 114},
			        "PF4":  { which : 115},
			        "PF5":  { which : 116},
			        "PF6":  { which : 117},
			        "PF7":  { which : 118},
			        "PF8":  { which : 119},
			        "PF9":  { which : 120},
			        "PF10": { which : 121},
			        "PF11": { which : 122},
			        "PF12": { which : 123},
			        "PF13": { which : 112, shiftKey : true},
			        "PF14": { which : 113, shiftKey : true},
			        "PF15": { which : 114, shiftKey : true},
			        "PF16": { which : 115, shiftKey : true},
			        "PF17": { which : 116, shiftKey : true},
			        "PF18": { which : 117, shiftKey : true},
			        "PF19": { which : 118, shiftKey : true},
			        "PF20": { which : 119, shiftKey : true},
			        "PF21": { which : 120, shiftKey : true},
			        "PF22": { which : 121, shiftKey : true},
			        "PF23": { which : 122, shiftKey : true},
			        "PF24": { which : 123, shiftKey : true},

			        //"CLEAR" : { type: 'keyup', which : 17, originalEvent :{location: 1}},
			        "CLEAR" :  { type: 'keydown',   which : 46, shiftKey : true},

			        "fex" :  { type: 'keydown', which : 17, originalEvent :{location: 2}},
			        "rtl" :  { which : 111, shiftKey : true},
			        "ower" : { which : 45},
			        "del" :  { type: 'keydown',   which : 46}
			};

	/**
	 * Check if running in mobile device
	 */
	function isMobile() {
		return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
	}

	/**
	 * For non mobile create popup window
	 */
	function setPopup() {

		$(".tnstatus").hover(function(e) {
		    $(".kbar").filter(':not(:animated)').animate({ bottom:"0px" }, 200);
		}, null);

		$(".kbar").hover(null, function() {
		    $(".kbar").animate({ bottom:"-50px" }, 200);
		});
	}

	/**
	 * Set fixed for mobile device
	 */
	function setVisible() {
		$('.view').css({bottom:81});
		$('.tnstatus').css({bottom:47});
		$('.kbar').css({bottom:0});
	}

    /**
     * Set keyboard mode
     * 1 - ENTER = FIELD EXIT; RCTRL = ENTER
     * 2 - ENTER = ENTER; RCTRL = FIELD EXIT
     */
    function setMode(mode) {

        if (mode === 1) {
            MAP.ENTER.which = 17;
            MAP.fex.which = 13;
        } else {
            MAP.ENTER.which = 13;
            MAP.fex.which = 17;
        }
    }

    /**
     * Initialize animation and click events on buttons
     */
	function initialize(fixed, mode) {

		if (fixed === true || isMobile()) {
			setVisible();
		} else {
			setPopup();
		}

		$('.kbar span[type="button"]').click(function(e) {
			var val = e.target.getAttribute('data');
			var evt = MAP[val];
			if (! evt.hasOwnProperty('type')) evt.type = 'keyup';
			if (e.shiftKey) evt.shiftKey = true;
			$.event.trigger(evt);
		});
	}

    return {
        init: function (isFixed, mode) {
        	setMode(mode);
        	initialize(isFixed);
        }
    };

}());
