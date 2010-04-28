/*
 * jQuery ScreenSaver plugin 0.1
 *
 * http://docs.jquery.com/Plugins/screensaver
 *
 * Copyright (c) 2008 Rafael Almeida
 *
 * $Id$
 * 
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 * Creates a screensaver.
 *
 * @example $.screensaver();
 *
 * @name screensaver
 * @type jQuery
 * @cat Plugins/screensaver
 */
;(function($) {
	/*
	 * Plugin defaults
	 */
	var defaults = {
		animationUrl: "../images/rocks.jpg",
		delay: 60000,
		docStrict: true,
		events: 'mousemove mousedown keydown',
		height: '100%',
		textAlt: "Screensaver active",
		type: 'image',
		width: '100%'
	};
	
	$.screensaver = function(settings) {
		$.screensaver.settings = $.extend({}, defaults, settings);
		
		if (settings.docStrict == true) {
			var endEmpty = " />";
		} else {
			var endEmpty = ">";
		}
		if ($.screensaver.settings.type == "image") {
			scrsvrHtml = "<div id='screensaver' style='line-height: "+$.screensaver.settings.height+"; text-align: center;'>"+
			"<img src='"+$.screensaver.settings.animationUrl+"' alt='"+$.screensaver.settings.textAlt+"' id='screensaver'"+endEmpty;
		} else if ($.screensaver.settings.type == "swf") {
			scrsvrHtml = "<div id='screensaver'><object type='application/x-shockwave-flash' data='"+$.screensaver.settings.animationUrl
			 + "' width='100%' height='100%'>"
			 + "<param name='movie' value='"+$.screensaver.settings.animationUrl+"'"+endEmpty
			 + "<param name='flashVars' value='playerMode=embedded'"+endEmpty
			 + "<param name='wmode' value='transparent'"+endEmpty
			 + "</object></div>";
		}
		var scrsvrObj = $(scrsvrHtml)
			.appendTo("body").hide()
			.css({
				'position': 'absolute', 'top': '0', 'left': '0', 'overflow': 'hidden',
				'width': $.screensaver.settings.width, 'height': $.screensaver.settings.height
			});
		$.screensaver.startCounter($.screensaver.settings.delay);
		bindEvents($.screensaver.settings.events);
		return scrsvrObj;
	};
	
	/*
	 * Public Functions
	 */
	$.screensaver.hide = function() {
		$("#screensaver").fadeOut("slow");
		$.screensaver.resetCounter();
	};
	$.screensaver.show = function() {
		$("#screensaver").fadeIn("slow");
	};
	$.screensaver.startCounter = function(timeout) {
		$.screensaver.counter = setInterval("$.screensaver.show()", timeout);
	};
	$.screensaver.resetCounter = function() {
		clearInterval($.screensaver.counter);
		$.screensaver.startCounter($.screensaver.settings.delay);
	};
	
	/*
	 * Private functions
	 */
	function debug($msg, $obj) {
		if (window.console && window.console.log)
			window.console.log($msg + $obj);
		else 
			alert($msg + $obj);
	};
	function bindEvents(events, elmt) {
		$(window).bind(events, $.screensaver.hide);
		$("#screensaver, #screensaver object").bind(events, $.screensaver.hide);
	};
})(jQuery);