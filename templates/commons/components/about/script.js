def('about', function() {

include('modal');

expose.init = function() {
	this.on('click', null, function() {
		
		this.modal.open('About');
		
	});
}

});