olbius.service('Sidebar', function() {
	this.employee = null;
	this.setEmployee = function(emp){
		this.employee = emp;
	};
	this.getEmployee = function(){
		return this.employee;
	};
});