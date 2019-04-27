/**
 * JSTT for jQuery.
 * <p>
 * JavaScript ToolTips
 * </p>
 * 
 * @author GuoLin
 */
(function($, window){
	
	// Check dependencies
	if (!$) {
		throw new Error("jQuery is required.");
	}
	
	/**
	 * Jstt class constructor.
	 * @classDescription This tooltips just for some guild requirments.
	 * @param {Object} options Object with all parameters.
	 */
	Jstt = function(options) {
		if (!options) {
			throw "The parameter options must be specified";
		}
		if (!options.content) {
			throw "The parameter content must be specified.";
		}
		
		// merge options
		this.options = $.extend({}, this.options, options);
		if (!this.options.id) {
			this.options.id = this._generateId();
		}
		
		// initialize
		this.box = this._createBox();
	};
	
	/**
	 * Jstt prototype.
	 */
	Jstt.prototype = {
		
		/**
		 * Options object, should set by user.
		 */
		options: {
			
			/**
			 * Id of top div element in the box.
			 * @type string
			 */
			id: null,
			
			/**
			 * Global id which affect on cookie status.
			 * Must be a number, as less as possible.
			 * Big number will increase length of cookie.
			 * @type number
			 */
			globalId: 0,
			
			/**
			 * Parent HTML element.
			 * @type HTMLElement
			 */
			parent: null,
			
			/**
			 * Html title.
			 * @type string
			 */
			title: "",
			
			/**
			 * Action HTML on right top of the box.
			 * @type string
			 */
			action: "",
			
			/**
			 * Html content.
			 * @type string
			 */
			content: "",
			
			/**
			 * Width of box.
			 * @type number
			 */
			width: 0,
			
			/**
			 * Height of box.
			 * @type number
			 */
			height: 0,
			
			/**
			 * Direction of the arrow on box.
			 * One of "left", "right", "down".
			 * @type string
			 */
			direction: null,
			
			/**
			 * Never show tooltips before from now on(second).
			 * Default is one year.
			 * @type number
			 */
			expires: 31536000,
			
			/**
			 * Z-index.
			 * @type number
			 */
			z: 100,
			
			/**
			 * The path of cookie.
			 * Default is root path "/".
			 * @type string
			 */
			path: "/"
			
		},
		
		/**
		 * Box object.
		 * @type Object
		 */
		box: {
			container: null,
			title: null,
			action: null,
			arrow: null,
			body: null
		},
		
		/**
		 * Position object.
		 * @type Object
		 */
		position: {
			x: 100, y: 100
		},
		
		/**
		 * Default ID prefix.
		 * @private
		 * @type string
		 */
		idPrefix: "jsttBox",
		
		/**
		 * Show the box.
		 * @param {Object} x Position on the x-axis
		 * @param {Object} y Position on the y-axis
		 * @param {boolean} withoutInvalidate If do invalidate operation when show the box, default is false
		 * @param {boolean} force If need force box to show whether cookie is valid, default is false
		 */
		show: function(x, y, withoutInvalidate, force) {
			// check force
			if (!force && !this.isValid()) {
				return;
			}
			
			// check x and y setting
			if (!isNaN(x)) {
				this.position.x = x;
			}
			if (!isNaN(y)) {
				this.position.y = y;
			}

			// Set direction if user did not specify before
			var direction;
			if (!this.options.direction) {
				direction = this._calculateDirection(x, y);
				this.setDirection(direction);
			}
			
			// show the box
			this.box.container.offset({ left: this.position.x, top: this.position.y }).show();
			
			// check if need invalidate
			if (!withoutInvalidate) {
				this.invalidate();
			}
		},
		
		/**
		 * Stick JSTT on specified element.
		 * @method stickOn
		 * @param {HTMLElement} el The element JSTT stick on
		 * @param {boolean} withoutInvalidate If do invalidate operation when show the box, default is false
		 * @param {boolean} force If need force box to show whether cookie is valid, default is false
		 */
		stickOn: function(el, withoutInvalidate, force) {
			el = $(el);
			
			var offset = el.offset(), position,
				direction = this.options.direction || this._calculateDirection(offset.left, offset.top, el.width(), el.height());
			
			position = this._calculatePosition(el, direction);
			this.setDirection(direction);
			
			this.show(position.x, position.y, withoutInvalidate, force);
		},
		
		/**
		 * Hide the box.
		 */
		hide: function() {
			this.box.container.hide();
		},
		
		/**
		 * Check if cookie is valid.
		 * @return {boolean} True is cookie is valid, false otherwise
		 */
		isValid: function() {
			var flagSequence = this._getCookie("jsttfs");
			return !this._checkFlagSequence(flagSequence, this.options.globalId);
		},
		
		/**
		 * Make the box never show for a long time.
		 * Mark flag invalid on flag sequence.
		 */
		invalidate: function() {
			// get exists cookie, may null
			var flagSequence = this._getCookie("jsttfs");
			
			// calculate new flag sequence
			var newFlagSequence = this._generateFlagSequence(flagSequence, this.options.globalId);
			
			// store cookie
			var expiration = new Date((new Date()).getTime() + this.options.expires * 1000);
			document.cookie = "jsttfs=" + newFlagSequence + "; path=" + this.options.path + "; expires=" + expiration.toGMTString();
		},
		
		/**
		 * Set arrow direction.
		 * @param {string} direction One of "left", "right" or "down".
		 */
		setDirection: function(direction) {
			if (direction != "left" && direction != "right" && direction != "down") {
				throw new Error("The parameter direction must be one of 'left', 'right' or 'down'.");
			}
			if (direction == this.options.direction) {
				return;
			}
			this.options.direction = direction;
			this.box.arrow.removeClass().addClass("arrow").addClass("arrow-" + direction);
		},
		
		/**
		 * Calculate right direction.
		 * <p>
		 * Automatically choose a direction depends on the specified x, y, width, height.
		 * If there are several directions can be used, will choose by the order:
		 * 'top', 'left', 'right'.
		 * </p>
		 * @param {number} x The x-axis position
		 * @param {number} y The y-axis position
		 * @param {number} width Optional, width for calculating right coordinate of target object
		 * @param {number} height Optional, height for calculating bottom coordinate of target object
		 * @return {string} Direction that automatically choosen
		 */
		_calculateDirection: function(x, y, width, height) {
			width = width || 0;
			height = height || 0;
			
			var box = this.box.container, $document = $(document),
				boxWidth = box.outerWidth(), boxHeight = box.outerHeight(),
				docWidth = $document.width(), docHeight = $document.height(),
				topGap = y, bottomGap = docHeight - y - height,
				leftGap = x, rightGap = docWidth - x - width;
			
			if (topGap > boxHeight) {
				return "down";
			}
			else if (leftGap > boxWidth) {
				return "right";
			}
			else if (rightGap > boxWidth) {
				return "left";
			}
			else {
				return "left";
			}
		},
		
		/**
		 * Calculate showing position.
		 * @param {HTMLElement | jQuery} el The element that box will stick on
		 * @param {string} direction The direction user specified
		 */
		_calculatePosition: function(el, direction) {
			el = $(el);
			var box = this.box.container, elOffset = el.offset(), 
				elLeft = elOffset.left + $(document).scrollLeft(),
				elTop = elOffset.top + $(document).scrollTop(), 
				elWidth = el.width(), elHeight = el.height(),
				elScrollLeft = el.scrollLeft(), elScrollTop = el.scrollTop(),
				boxWidth = box.outerWidth(), boxHeight = box.outerHeight(),
				x, y;
			if (direction === "left") {
				x = elLeft + elWidth;
				y = elTop + elHeight - 80;  // XXX 80px depends on CSS
			}
			else if (direction === "right") {
				x = elLeft - boxWidth;
				y = elTop + elHeight - 80;
			}
			else if (direction === "down") {
				x = elLeft - 40;
				y = elTop - boxHeight;
			}
			return { x: x, y: y };
		},
		
		/**
		 * Generate the flag sequence depends on global id.
		 * @param {Object} source The source sequence, may be null
		 * @param {Object} globalId The global id which set by user
		 * @return {string} The new flag sequence
		 * @private
		 */
		_generateFlagSequence: function(source, globalId) {
			var values = (source ? source : "0").split(",");
			var pos = globalId >> 5;  // js number is 32 bits
			
			// make a suitable array
			var results = new Array(Math.max(pos + 1, values.length));
			for (var i = 0; i < results.length; i++) {
				results[i] = values[i] ? values[i] : "0";
			}
			
			// bit arithmetic
			results[pos] = results[pos] | (1 << (globalId & 31));
			return results.toString();
		},
		
		/**
		 * Check the value of global id on flag sequence. 
		 * @param {Object} source The source sequence, may be null
		 * @param {Object} globalId The global id which set by user
		 * @return {boolean} True if global id already setted on the flag sequence, false otherwise
		 * @private
		 */
		_checkFlagSequence: function(source, globalId) {
			var values = (source ? source : "0").split(",");
			var pos = globalId >> 5;  // js number is 32 bits
			return (typeof values[pos] == "undefined") ? false : ((values[pos] & (1 << (globalId & 31))) != 0);
		},
		
		/**
		 * Create a box div append to body with all elements in it.
		 * @return {Object} box object contains needed properties
		 * @private
		 */
		_createBox: function() {
			/* The complete html text would like this:
	        <div id="my" class="jstt">
	        	<div class="arrow-right arrow"></div>
	        	<div class="box">
		        	<div class="box-outer">
		        		<div class="box-inner">
		        			<div class="box-content">
		        				<div class="box-header">
		        					<div class="box-header-content">
		        						<h1>title</h1>
		        						<span></span>
		        					</div>
		        				</div>
		        				<div class="box-body">body</div>
		        			</div>
		        		</div>
		        	</div>
	        	</div>
	        </div> */
			// Build main container
			var jstt = $("<div>").attr("id", this.options.id)
				.addClass("jstt").css("z-index", this.options.z)
				.hide();
			if (this.options.height) {
				jstt.height(this.options.height);
			}
			if (this.options.width) {
				jstt.width(this.options.width);
			}
			
			// Build arrow icon, default direction is left
			var arrow = $("<div>").addClass("arrow")
				.addClass(this.options.direction ? "arrow-" + this.options.direction : "")
				.appendTo(jstt);
			
			// Build containers for radius border background image
			var box = $("<div>").addClass("box").appendTo(jstt);
			var boxOuter = $("<div>").addClass("box-outer").appendTo(box);
			var boxInner = $("<div>").addClass("box-inner").appendTo(boxOuter);
			var boxContent = $("<div>").addClass("box-content").appendTo(boxInner);
			
			// construct header
			var boxHeader = $("<div>").addClass("box-header").appendTo(boxContent);
			var boxHeaderContent = $("<div>").addClass("box-header-content").appendTo(boxHeader);
			var title = $("<h1>").html(this.options.title || "").appendTo(boxHeaderContent);
			var action = $("<span>");
			if (this.options.action) {
				action.html(this.options.action);
			} else {
				var close = $('<a href="javascript:void(0)" class="close"></a>').appendTo(action);
				var me = this;  // for closure
				close.click(function() {
					me.hide();
				});
			}
			boxHeaderContent.append(action);

			// construct body
			var boxBody = $("<div>").addClass("box-body").html(this.options.content).appendTo(boxContent);
			
			var parent = this.options.parent || document.getElementsByTagName("body")[0];
			jstt.appendTo(parent);
			
			// assemble result.
			return {
				container: jstt,
				title: title ? title : null,
				action: action ? action : null,
				arrow: arrow,
				body: boxBody
			};
		},
		
		/**
		 * Get cookie.
		 * @param {Object} cookieName Cookie name
		 * @return {string} The cookie value
		 * @private
		 */
		_getCookie: function(cookieName) {
			var start = document.cookie.indexOf(cookieName);
			if (start >= 0) {
				start += cookieName.length + 1;
				var end = document.cookie.indexOf(";", start);
				if (end < 0) {
					end = document.cookie.length;
				}
				return unescape(document.cookie.substring(start, end));
			}
			return null;
		},
		
		/**
		 * Generate a valid id.
		 * @return {string} a not exists id
		 * @private
		 */
		_generateId: function() {
			var box, id, i = 0;
			do {
				id = this.idPrefix + "-" + i++;
				box = document.getElementById(id);
			} while (box);
			return id;
		}
		
	};
	
	/**
	 * JSTT plugin.
	 * @param {Object | string} options Invoke method if is a string, otherwise will create a JSTT
	 * @return {jQuery} jQuery itself
	 */
	$.fn.jstt = function(options) {
		var jstt, args = Array.prototype.slice.call(arguments, 1),
			el = this[0];
		
		// Command mode
		if (typeof options === "string") {
			jstt = $.data(el, "jstt");
			if (!jstt) {
				return null;
			}
			
			// Get option
			if (options === "options" && args.length === 1) {
				return jstt.options[args[0]];
			}
			else if (options === "box" && !args.length) {
				return jstt.box;
			}
			// Set option
			else if (options === "options" && args.length === 2) {
				jstt.options[args[0]] = args[1];
			}
			// Execute methods
			else if (options === "show") {
				jstt.stickOn.apply(jstt, [ el ].concat(args));
			}
			else if (options === "hide") {
				jstt.hide();
			}
			else if (options === "invalidate") {
				jstt.invalidate();
			}
		}
		// Creation mode
		else {
			jstt = new Jstt(options);
			$.data(el, "jstt", jstt);
		}
		
		
		return this;
	};
	
})(jQuery, this);