var jqxDropDownObject = (function(){
	
	var createJqxDropDown = function(divDropDown, source,valueMember, displayMember, width, height, config){
		 var dataAdapter = new $.jqx.dataAdapter(source);
		 $("#"+divDropDown).jqxDropDownList({
			 source: source, 
			 theme: 'olbius',
			 selectedIndex: -1,
			 displayMember: displayMember,
			 valueMember: valueMember,
			 width: width,
			 height: height
			 
		 });
		 if(typeof(config) != 'undefined' && config != null){
			 $("#"+divDropDown).jqxDropDownList(config);
		 }
	};
	var setItemDropdown = function(divEle,value){
		$("#"+divEle).jqxDropDownList('selectItem',value);
	};
	return{
		createJqxDropDown: createJqxDropDown,
		setItemDropdown: setItemDropdown
	}
}());