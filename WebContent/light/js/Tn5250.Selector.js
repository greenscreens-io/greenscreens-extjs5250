/*
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 */

// 5250 UI key / mouse select for screen copy
Tn5250.Selector = (function () {

    var KEY_NAV = {
            "LEFT": 37,
            "UP": 38,
            "RIGHT": 39,
            "DOWN": 40,
            codes: [37, 38, 39, 40]
        };

    var doc = document;
    var win = window;
    var initialC = 0;
    var initialR = 0;
    var initialW = 0;
    var initialH = 0;
    var first = null;
    var last = null;
    var down = false;
    var paused = false;

    /**
     * Stop all event processing
     */
    function stopEvents(e) {
        e.stopPropagation();
        e.preventDefault();
    }

    /**
     * Find TR element at pixel position
     */
    function findEl(x, y) {

    	var tbl = doc.querySelector('.view table');
    	if (tbl === null) return;

    	var rows = tbl.rows.length;
    	var cols = 80;
    	if (rows == 27) cols = 132;

    	var cw = tbl.offsetWidth / cols;
    	var ch= tbl.offsetHeight / rows;

    	var cc = Math.ceil( x / cw);
    	var cr = Math.floor( y / ch) ;
    	if (cr < tbl.rows.length) {
    		return tbl.rows[cr].cells[cc];
    	}
    	return null;
    }

    /**
     * Select TD region
     */
	function selectElements(e) {

		var sel = $(".ghost-select");
		var selPos = sel.offset();
		var selW = sel.width();
		var selH = sel.height();

		var sp = findEl(selPos.left + 5, selPos.top + 5);
		var ep = findEl(selPos.left + selW, selPos.top + selH - 5);

        if (sp && ep && sp !== ep ) {

        	first = sp;
        	last = ep;

        	$("body").append("<div id='big-ghost' class='big-ghost'></div>");
        	$("#big-ghost").css({
                'width': (last.offsetLeft  - first.offsetLeft),
                'height': (last.offsetTop - first.offsetTop + last.offsetHeight),
                'top': first.offsetTop,
                'left': first.offsetLeft
        	});

        } else {
        	first = null;
            last = null;
        }

	    $(".ghost-select").removeClass("ghost-active");
	    $(".ghost-select").width(0).height(0);
	}

	/**
	 * Animate selector window
	 */
	function openSelector(e) {

	    var w = Math.abs(initialW - e.pageX);
	    var h = Math.abs(initialH - e.pageY);
	    var tbl = doc.querySelector('.view table');

	    if (tbl === null) return;

	    if (e.pageX > tbl.offsetWidth || e.pageX  < tbl.offsetLeft) {
	    	return ;
	    }

	    if (e.pageY > tbl.offsetHeight || e.pageY  < tbl.offsetTop) {
	    	return ;
	    }

	    $(".ghost-select").css({
	        'width': w,
	        'height': h
	    });

	    if (e.pageX <= initialW && e.pageY >= initialH) {
	        $(".ghost-select").css({
	            'left': e.pageX
	        });
	    } else if (e.pageY <= initialH && e.pageX >= initialW) {
	        $(".ghost-select").css({
	            'top': e.pageY
	        });
	    } else if (e.pageY < initialH && e.pageX < initialW) {
	        $(".ghost-select").css({
	            'left': e.pageX,
	            "top": e.pageY
	        });
	    }
	}

	/**
	 * Initialize mouse actions
	 */
	function mouseEvents(callback) {

		  $(doc).off("mousedown.mselector");
		  $(doc).on('mousedown.mselector', '.view', function (e) {

			   if (paused || e.which !== 1) return;

 			   // fix for virtual keyboard
			    if (e.target.tagName === 'DIV' || e.target.tagName === 'BUTTON') {
			    	return;
			    }

			    if (down) return;
			    down = true;

		        $("#big-ghost").remove();
		        $(".ghost-select").addClass("ghost-active");
		        $(".ghost-select").css({
		            'left': e.pageX,
		            'top': e.pageY
		        });

		        initialW = e.pageX;
		        initialH = e.pageY;
		        first = e;

		        $(doc).on("mousemove.mselector", '.view', openSelector);
		        $(doc).on("mouseup.mselector", '.ghost-select', function(e) {

					$(doc).off("mousemove.mselector").off("mouseup.mselector");

		        	if (down===false) {
                        return;
                    }

		        	last = e;
		        	selectElements(e);

		        	if (typeof callback === 'function' && first !== null && last !== null) {
		        		callback(first, last);
		        	}

		        	down = false;

		        });

		    });
	}

	// keyboard hanlers

    /**
     * Select TD region
     */
	function selectElementsKey(e) {
        var tbl = $('.tngrid').get(0);
        if (tbl === null) return;
        var pos = $(tbl.rows[initialR].cells[initialC]).offset();
        e.pageX = pos.left;
        e.pageY = pos.top;
        selectElements(e);
	}

	/**
	 * Animate selector window for key
	 */
	function openSelectorKey(e) {

        if (KEY_NAV.RIGHT === e.which) {
        	initialC++;
        } else if (KEY_NAV.LEFT === e.which) {
        	initialC--;
        }

        if (KEY_NAV.UP === e.which) {
        	initialR--;
        } else if (KEY_NAV.DOWN === e.which) {
        	initialR++;
        }

        var tbl = $('.tngrid').get(0);
        if (initialC<0) initialC = 0;
        if (initialR<0) initialR = 0;
        if (initialC>=tbl.rows[0].cells.length) initialC = tbl.rows[0].cells.length-1;
        if (initialR>= tbl.rows.length) initialR = tbl.rows.length-1;

        var td = tbl.rows[initialR].cells[initialC];

        if (KEY_NAV.UP === e.which || KEY_NAV.LEFT === e.which) {
            e.pageX = td.offsetLeft;
            e.pageY = td.offsetTop;
        } else if (KEY_NAV.DOWN === e.which || KEY_NAV.RIGHT === e.which) {
            e.pageX = td.offsetLeft + td.offsetWidth;
            e.pageY = td.offsetTop + td.offsetHeight;
        }

        openSelector(e);
	}

	/**
	 * Check if arrow and shift keys
	 */
	function isSelectionKeys(e) {
    	return e.shiftKey && KEY_NAV.codes.indexOf(e.which) > -1;
	}

	/**
	 * Initialize key actions
	 */
	function keyEvents(callback) {

		$(doc).off(".kselector");

        $(doc).on('keydown.kselector', function (e) {

        	if (paused) return;
			// fix for virtual keyboard
		    if (e.target.tagName === 'DIV' || e.target.tagName === 'BUTTON') {
		    	return;
		    }

        	if (!isSelectionKeys(e)) {
                return;
            }
        	stopEvents(e);

        	if (down) {
        		return openSelectorKey(e);
        	}
        	down = true;

        	var td = $('.tngrid .focus').get(0);
        	if (!td){
                return;
            }

        	initialC = td.cellIndex;
        	initialR =  td.parentNode.sectionRowIndex;
        	initialW = td.offsetLeft;
	        initialH = td.offsetTop;

	        $("#big-ghost").remove();
	        $(".ghost-select").addClass("ghost-active");
	        $(".ghost-select").css({
	            'left': initialW,
	            'top': initialH
	        });

	        e.pageX = td.offsetLeft + td.offsetWidth;
	        e.pageY = td.offsetTop + td.offsetHeight;
	        openSelector(e);

        })
        .on('keyup.kselector', function (e) {

        	if (!(down && e.which === 16)) {
                return;
            }
        	stopEvents(e);

        	last = e;
        	selectElementsKey(e);
        	if (typeof callback === 'function' && first !== null && last !== null) {
        		callback(first, last);
        	}

        	down = false;
        });
	}

	/**
	 * Start selector monitor
	 */
	function start(callback) {
		mouseEvents(callback);
		keyEvents(callback);
	}

	// you're my first, you're my last, you're my everything
	function getTextFromRange()	{

		$("#big-ghost").remove();
		if (first === null  || last === null) {
            return;
        }

		var r1 = first.parentNode.sectionRowIndex;
		var r2 = last.parentNode.sectionRowIndex;
		var c1 = first.cellIndex;
		var c2 = last.cellIndex;

		var row = first.parentNode;
		var tbl = first.parentNode.parentNode;

		var i,s='', el;
		while (r1<=r2) {

		  i = c1;
		  while (i<=c2) {
			el = tbl.rows[r1].cells[i];
			if (el) {
				if (el.colSpan>1) {

					if (el.innerText.trim().length>0) {
						s = s + el.innerText;
					} else {
						s = s + new Array(el.colSpan+1).join(' ');
					}

				} else  if (el.innerText) {
					s = s + el.innerText;
				} else {
					s = s + ' ';
				}
			}
		   i++;
		  }
		 s =s + '\n';
		 r1++;
		}

		return s;
	}

    /**
     * Copy selected data into clipboard
     *
     * Grid uses colspan for speed optimization, thus having missing spaces
     * because of this copy will not be char fixed
     */
    function copy(e) {

    	if (first === null  || last === null) return;
    	var str = getTextFromRange();

        try {
        	setTimeout( function () {
                var sel = win.getSelection();
                sel.removeAllRanges();

            	var d = doc.createElement('textarea');
            	d.id="copy";
            	d.value=str;
            	doc.body.appendChild(d);

            	var range = doc.createRange();
            	range.selectNode(d);
            	sel.addRange(range);

            	doc.execCommand('copy');
        	}, 0 );

        } catch(err) {
           console.log('Oops, unable to copy');
        }

        return str;
    }

    /**
     * Monitor copy request
     */
    function monitor() {

    	var second = false;

    	$(doc).unbind('copy');
		$(doc).on('copy', function (e) {

	    	if (!doc.queryCommandSupported('copy')) {
	    		return;
	    	}

	    	if (second === true) {
	    		second = false;
	    	} else {
	    		$('#copy').remove();
	    		second = true;
	    		copy(e);
	    	}

	    });
    }

    /**
     * Cleanup selector on window resize
     */
	$(win).resize(function(){
		$("#big-ghost").remove();
	});

    return {

    	pause : function(val) {
    		paused = val;
    	},

        init: function (callback) {
        	monitor();
            start(callback);
        }
    };

}());
