/**
 * Copyright (C) 2015, 2016  GreenScreens Ltd.
 *
 * 5250 UI key / mouse select for screen copy
 */
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
    function findEl(x, y, isStart) {

    	var tbl = doc.querySelector('.view table');
    	if (tbl === null) return;

    	var rows = tbl.rows.length;
    	var cols = 80;
    	if (rows == 27) cols = 132;

    	var cw = tbl.offsetWidth / cols;
    	var ch= tbl.offsetHeight / rows;

    	var cc = 0;    	
    	var cr = 0;
    	
    	if (isStart) {
    		cc = Math.round( x / cw);
    		cr = Math.round( y / ch) ;
    	} else {
    		cc = Math.trunc( x / cw);
    		cr = Math.trunc( y / ch) ;
    	}
    	
    	if (cc >= cols) {
    		cc = cols - 1;
    	}
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

		var sp = findEl(selPos.left, selPos.top, true);
		var ep = findEl(selPos.left + selW, selPos.top + selH, false);
		
        if (sp && ep && sp !== ep ) {

        	first = sp;
        	last = ep;

        	$("body").append("<div id='big-ghost' class='big-ghost'></div>");
        	$("#big-ghost").css({
                'width': (last.offsetLeft  - first.offsetLeft + last.offsetWidth),
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

			    //console.log('mousedown.mselector 1');
			   if (paused || e.which !== 1) return;

			   //console.log('mousedown.mselector 2');
			   
 			   // fix for virtual keyboard
			    if (e.target.tagName === 'DIV' || e.target.tagName === 'BUTTON') {
			    	//console.log('mousedown.mselector 3');
			    	return;
			    }

			    if (down) {
			    	//console.log('mousedown.mselector 4');
			    	//return;
			    }
			    down = true;
			    //console.log('mousedown.mselector 5');
			    
		        $("#big-ghost").remove();
		        
		        var selecting = false;
		        initialW = -1;
		        initialH = -1;
		        
		        $(doc).on("mousemove.mselector", '.view', function(e){
		        	//console.log('mousemove.mselector');
		        	
		        	if (e.buttons === 0) {
		        		$(doc).off("mousemove.mselector").off("mouseup.mselector");
		        		return;
		        	}
		        	
		        	if (!selecting) {
				        $(".ghost-select").addClass("ghost-active");
				        $(".ghost-select").css({
				            'left': e.pageX,
				            'top': e.pageY
				        });

				        initialW = e.pageX;
				        initialH = e.pageY;
				        first = e;
				        	        
		        	}
		        	selecting = true;
		        	openSelector(e);
		        });
		        
		        $(doc).on("mouseup.mselector", function(e) {
		        	//console.log('mouseup.mselector 1');
		        	
		        	$(doc).off("mousemove.mselector").off("mouseup.mselector");
		        	
		        	if (!selecting) {
		        		//console.log('mouseup.mselector 2');
		        		return;
		        	}
		        	selecting = false;
		        						
		        	if (down=false) {
		        		//console.log('mouseup.mselector 3');
		        		return;
		        	}
		        	//console.log('mouseup.mselector 4');
		        	last = e;
		        	selectElements(e);
		        	if (typeof callback === 'function' && first != null && last != null) {
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
        	if (!isSelectionKeys(e)) return;
        	stopEvents(e);

        	if (down) {
        		return openSelectorKey(e);
        	}
        	down = true;

        	var td = $('.tngrid .focus').get(0);
        	if (!td) return;

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

        	if (!(down && e.which === 16)) return;
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
		
		if (first === null  || last === null) return null;

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
		 if (r1<r2) {
			 s =s + '\n';
		 }
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

    	$("#big-ghost").remove();
    	var str = getTextFromRange();
    	if (str === null) return;
    	
        try {
        	setTimeout( function () {
                var sel = win.getSelection();
                sel.removeAllRanges();

            	var d = doc.createElement('textarea');
            	d.id="copyData";
            	d.value=str;
            	doc.body.appendChild(d);

            	var range = doc.createRange();
            	range.selectNode(d);
            	sel.addRange(range);

            	var sts = doc.execCommand('copy');
            	
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

    	if (!doc.queryCommandSupported('copy')) {
    		return;
    	}

    	var second = false;

    	$(doc).unbind('copy');
		$(doc).on('copy', function (e) {
			
	    	if (second === true) {
	    		second = false;	    		
	    	} else {
				e.preventDefault();
				e.stopImmediatePropagation();
				e.stopPropagation();
				$('#copyData').remove();
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
    	isSelecting : function() {
    		return down;
    	},
    	pause : function(val) {
    		paused = val;
    	},
        init: function (callback) {
        	monitor();
            start(callback);
        }
    };

}());
