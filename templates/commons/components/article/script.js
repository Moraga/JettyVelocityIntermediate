
def('article', function() {
	
	expose.init = function() {
		var d = document.createElement('div');
		d.innerHTML = '<hr>Hello from Exposejs';
		
		
		this.dom.appendChild(d);
	}
	
});