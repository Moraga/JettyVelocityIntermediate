def('modal', function() {

/**
 * Modals opened (visible and hidden)
 * @var array
 */
instances.modal = [];

/**
 * Creates a new modal
 * @return Modal
 */
expose.create = function() {
	return new this.Modal();
}

/**
 * Creates and open a new modal
 * @return Modal
 */
expose.open = function(content, mode, style) {
	var modal = this.create();
	if (mode)
		modal.setMode.apply(modal, mode.split(':').reverse());		
	if (style)
		modal.setStyle(style);
	modal.setContent(content, true);
	var hasScrollbar = ( window.innerWidth - document.documentElement.clientWidth );
	if(hasScrollbar > 0)
		document.body.style.paddingRight = hasScrollbar + "px";
	document.body.className += " modal-open";
	return modal;
}

/**
 * Modal constructor
 * @var function
 */
expose.Modal = function() {
	this.buildStructure();
	this.enableUIControl();
	this.enableEscClose();
	this.enableClickClose();
	// registrate the instance
	instances.modal.push(this);
}

expose.Modal.prototype = {
	/**
	 * View mode
	 * @var string
	 */
	mode: 'normal',
	
	/**
	 * View mode for user definitions
	 * @var string
	 */
	upmode: '',
	
	/**
	 * Display state
	 * @var boolean
	 */
	visible: false,
	
	/**
	 * Runs on show modal
	 * @var function
	 */
	onshow: function() {},
	
	/**
	 * Runs before show modal
	 * False cancel the display
	 * @var function
	 */
	onbeforeshow: function() {},
	
	/**
	 * Runs on hide modal
	 * @var function
	 */
	onhide: function() {},
	
	/**
	 * Runs on close the modal
	 * Hide event are executed too
	 * @var function
	 */
	onclose: function() {},
	
	/**
	 * Runs before close the modal
	 * False stops the close
	 * @var function
	 */
	onbeforeclose: function() {},
	
	/**
	 * @var boolean
	 */
	visible_: false,
	
	esc_: false,

	click_: false,

	/**
	 * @var object
	 */
	queue_: [],
	
	/**
	 * @param string modal View mode
	 */
	setMode: function(mode, upmode) {
		if (mode)
			this.closure.className += ' modal-mode-' + (this.mode = mode);
		// atualiza / define upmode
		if (typeof upmode != 'undefined') {
			// remove modo antigo
			if (this.upmode) {
				document.body.className = document.body.className.replace(' modal-' + this.upmode, '');
			}
			// define upmode
			this.upmode = upmode;
			document.body.className += ' modal-' + this.upmode;
		}
	},
	
	/**
	 * @param object style Stylesheet rules
	 */
	setStyle: function(style) {
		$(this.dom).css(style);
	},
	
	/**
	 * Atualiza o conteúdo do modal
	 * @return void
	 */
	setContent: function(content, callback) {
		if (callback === true) {
			callback = this.show;
		}
		
		if (content instanceof jQuery)
			content = content.get(0);
		
		if (content instanceof Node)
			content = content.outerHTML;
		
		if (typeof content == 'object') {
			var url = '/service.htm?json&type=' + content.type + '&args=' + JSON.stringify(content.args);
			// always update the view mode
			this.setMode(null, content.type);
			// loads the content
			def.prototype.getjson.call(this, url, function(data) {
				this.dom.innerHTML = data.body;
				callback && callback.call(this);
				render();				
			});
		}
		// normal xhr request
		else if (content.indexOf('\n') == -1 && /^(https?:)?\/+[a-z0-9]|\..{2,4}$/.test(content)) {
			def.prototype.getjson.call(this, content, function(data) {
				this.dom.innerHTML = data.body;
				callback && callback.call(this);
				render();
			});
		}
		// content as string
		else {
			this.dom.innerHTML = '<div class="modal-content">' + content + '</div>';
			callback && callback.call(this);
			render();
		}
	},
	
	/**
	 * Shows the modal
	 * @param function callback
	 * @return void
	 */
	show: function(callback) {
		this.visible = true;
		this.closure.className = this.closure.className.replace(/\s+hide/, '');
		
		// executa antes de exibir o modal
		if (this.onbeforeshow() == false)
			return false;
		
		if ('left' == this.mode) {
			var self = this;
			$(this.closure).fadeIn();
			$(this.dom).animate({left: '0'}, 'normal', function() {
				callback && callback.call(self);
				self.onshow();
				render();
			});
		}
		else {
			callback && callback.call(self);
			this.onshow();
		}
	},
	
	/**
	 * Hides the modal
	 * @param function callback
	 * @return void
	 */
	hide: function(callback) {
		this.visible = false;
		if ('left' == this.mode) {
			var self = this;
			$(this.closure).fadeOut();
			$(this.dom).animate({left: '-100%'}, 'normal', function() {
				self.closure.className += ' hide';
				callback && callback.call(self);
				self.onhide();
			});
		}
		else {
			this.closure.className += ' hide';
			callback && callback.call(this);
			this.onhide();
		}
		
		if (this.upmode)
			document.body.className = document.body.className.replace(' modal-' + this.upmode, '');
		
		remove: {
			for (var i = instances.modal.length; i--;) 
				if (instances.modal[i].visible)
					break remove;
			document.body.className = document.body.className.replace(/\smodal-open/g, '');
			document.body.style.paddingRight = "";
		}
	},
	
	/**
	 * 
	 * @return void
	 */
	enableUIControl: function() {
		var self = this;
		// fechar modal
		$(this.dom).on('click', '.modal-close', function(e) {
			e.preventDefault();
			self.close();
		});
	},
	
	/**
	 * 
	 * @return void
	 */
	enableEscClose: function() {
		this.esc_ = true;
	},
	
	/**
	 * 
	 * @return void
	 */
	disableEscClose: function() {
		this.esc_ = false;
	},
	
	/**
	 * 
	 * @return void
	 */
	enableClickClose: function() {		
		this.click_ = true;
	},
	
	/**
	 * 
	 * @return void
	 */
	disableClickClose: function() {
		this.click_ = false;
	},
	
	/**
	 * Adds button close
	 * @return void
	 */
	addButtonClose: function() {
		$(this.dom).prepend('' +
			'<button class="btn modal-close">' +
				'<i class="icon-close-2"></i>' +
			'</button>');
	},
	
	/**
	 * Adds a default modal header
	 * @param string title Header title (optional)
	 * @param string description Header description (optional)
	 * @return void
	 */
	addHeader: function(title, description) {
		var html = '';
		
		// prepares the header content
		if (title)
			html += '<h4>' + title + '</h4>';
		if (description)
			html += '<p class="node"><small>' + description + '</small></p>';
		html = '<div class="modal-header">' + html + '</div>';
		
		// remove the previous modal header
		$('.modal-header', this.dom).remove();
		
		// adds the new header
		if ('BUTTON' == (this.dom.firstElementChild || {}).nodeName)
			$(this.dom.firstElementChild).after(html);
		else
			$(this.dom).prepend(html);
	},
	
	/**
	 * @param function callback
	 */
	addScrollEvent: function(callback) {
		var self = this;
		$(this.closure).on('scroll', function() {
			callback.apply(self, arguments);
		});
	},
	
	/**
	 * Hides the other modals
	 * @return void
	 */
	standalone: function() {
		for (var i = instances.modal.length; i--;) {
			var modal = instances.modal[i];
			if (modal !== this) {
				modal.visible_ = modal.visible;
				if (modal.visible)
					modal.hide();
				this.queue_.push(instances.modal.splice(i, 1)[0]);
			}
		}
	},
	
	/**
	 * Fecha o modal
	 * @return void
	 */
	close: function() {
		// onbeforeclose can stops the close action
		if (this.onbeforeclose() === false)
			return;
		
		// searches in modal instances
		for (var i = instances.modal.length; i--; )
			if (instances.modal[i] === this)
				break;

		// remove all instances
		if (i != -1) {
			for (var j = instances.modal.length; --j > i;)
				instances.modal.splice(j, 1)[0].close();
			instances.modal.splice(i, 1)
		}

		this.hide(function() {
			// removes the element
			this.closure.parentNode.removeChild(this.closure);

			// closes the method
			this.close = function() {};
			
			// trigger callback
			this.onclose();

			if (this.queue_) {
				for (var i = this.queue_.length; i--; )
					if (this.queue_[i].visible_)
						this.queue_[i].show();
				instances.modal = this.queue_;
			}
		});
	},
	
	/**
	 * Creates the structure of the modal
	 * @return void
	 */
	buildStructure: function() {
		// external layer
		this.closure = document.createElement('div');
		this.closure.className = 'modal hide';
		// internal layer
		this.dom = document.createElement('div');
		this.dom.className = 'modal-dialog';
		// appends to DOM tree
		this.closure.appendChild(this.dom);
		document.body.appendChild(this.closure);
	},
	
	/**
	 * Exclusive mode
	 * Close the other modals open
	 * @return void
	 */
	unique: function() {
		for (var i = instances.modal.length; i--; )
			instances.modal[i] !== this && instances.modal[i].close();
	}
};

//
//
//

window.addEventListener('keyup', function(event) {
	switch (event.which || event.keyCode) {
		// escape key
		case 27:
			var modal = instances.modal.slice(-1)[0];
			if (modal && modal.esc_)
				modal.close();
			break;
	}
});

window.addEventListener('click', function(event) {
	var modal = instances.modal.slice(-1)[0];
	if (modal && modal.click_) {
		var elem = event.target;
		// closes the modal on click out
		while (elem) {
			if (elem.className && elem.className.indexOf('modal-wrapper') != -1)
				break;
			elem = elem.parentNode;
		}
		if (!elem)
			modal.close();
	}
}, true);


});