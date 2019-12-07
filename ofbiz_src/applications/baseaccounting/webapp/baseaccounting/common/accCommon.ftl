<#-- load lib jqx -->
<@jqGridMinimumLib />
<#include "initAccDropDownCommon.ftl"/>
<#-- !end  -->

<script>
$.jqx.theme = 'olbius'; 
var formatDefault = 'dd/MM/yyyy';
var organization = '${parameters.organizationPartyId?if_exists?default("null")}'
/*init class with construtor*/
var Class = function(methods) {   
    var klass = function() {   
        this.initialize.apply(this, arguments);          
    };  
	$.getScript('/accresources/js/utils/date.utils.js', function(){
		 klass.prototype['dateUtil'] =  filterDate;
	})
	klass.prototype['theme'] = $.jqx.theme;
    for (var property in methods) { 
       klass.prototype[property] = methods[property];
    }
    if (!klass.prototype.initialize) klass.prototype.initialize = function(){};      
    
    return klass;    
};
var accCommon = Class({
	initialize: function() {
		
    },
    DropDownUtils : DropDownUtils,
    formatDate : function(format,date){
		if(Object.prototype.toString.call(date) == '[object Date]'){
			if(format != 'undefined' && format != null){
				return date.format(format);
			}else return date.format(formatDefault);
		}
		return null;
	},
	processSource : function(source){
		if(typeof(source) != 'undefined'){
			if(Object.prototype.toString.call(source) == '[object Array]'){
				return source;
			}else if(Object.prototype.toString.call(source) == '[object Object]'){
				if(source.hasOwnProperty('url')){
					source = {
						datatype: "json",
		                datafields: config.dataFields, 
		                data: {
							noConditionFind: 'Y',
							conditionsFind: 'N',
						},
		                url: config.url,
		                async: false,
					}
					var dataAdapter = new $.jqx.dataAdapter(source);
					return dataAdapter;
				}else if(source.hasOwnProperty('localdata')){
					return source;
				}
			}else return [];
		}
		return [];	
	},
	processConfig : function(config){
		var source = config.obj.processSource(config.source);
		if(typeof(source._source) != 'undefined'){
			var length = source._source.length;
		}else var length = source.length;
		var configCf = {
				autoOpen  : typeof(config.autoOpen) == 'undefined' ? false : config.autoOpen,
				autoDropDownHeight : (typeof(config.autoDropDownHeight) == 'undefined' && length < 10)? true : config.autoDropDownHeight,
				checkBoxes : typeof(config.checkBoxes) == 'undefined' ? false : config.checkBoxes,
				closeDelay : typeof(config.closeDelay) == 'undefined' ? 400 : config.closeDelay,
				disabled : 	typeof(config.disabled) == 'undefined' ? false : config.disabled,	
				displayMember : config.displayMember,
				valueMember : config.valueMember,
				dropDownHorizontalAlignment  : typeof(config.dropDownHorizontalAlignment) == 'undefined' ? 'left' : config.dropDownHorizontalAlignment,	
				width : typeof(config.width) == 'undefined' ? '250' : config.width,
				height : typeof(config.height) == 'undefined' ? '25' : config.height,		
				dropDownHeight : typeof(config.dropDownHeight) == 'undefined' ? '300' : config.dropDownHeight,
				dropDownWidth : typeof(config.dropDownWidth) == 'undefined' ? '250' : config.dropDownWidth,
				filterable : typeof(config.filterable) == 'undefined' ? true : config.filterable,
				filterPlaceHolder : typeof(config.filterPlaceHolder) == 'undefined' ? '${StringUtil.wrapString(uiLabelMap.BACCSearchDropDownList)}' : config.filterPlaceHolder,
				placeHolder :  typeof(config.placeHolder) == 'undefined' ? '${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc)}' : config.placeHolder,	
				renderer : typeof(config.renderer) == 'undefined' ? false : config.renderer,
				selectionRenderer : typeof(config.selectionRenderer) == 'undefined' ? false : config.selectionRenderer,
				searchMode : typeof(config.searchMode) == 'undefined' ? 'containsignorecase' : config.searchMode,
				selectedIndex : typeof(config.selectedIndex) == 'undefined' ? null : config.selectedIndex,
				source :source,
				enableHover : typeof(config.enableHover) == 'undefined' ? true : config.enableHover,
				theme : typeof(config.theme) == 'undefined' ? theme : config.theme
		};
		return configCf;
	},
	createDropDownList : function(id,config){
		if(id != 'undefined'){
			var obj = config.obj;
			config = obj.processConfig(config);
			$(id).jqxDropDownList(config);
		}
		return;
	},
	ArrayIsNotEmpty : function(array){
		if(typeof(array) != 'undefined' && Object.prototype.toString.call(array) == '[object Array]'){
			if(array.length > 0 ){ return true; }else return false;
		}
		return false;
	}
})
</script>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         