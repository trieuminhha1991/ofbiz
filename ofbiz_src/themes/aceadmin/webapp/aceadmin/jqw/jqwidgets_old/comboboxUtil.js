var Comboxbox = (function() {
	var initSource = function(config) {
		var source = {
			datatype : config.datatype ? config.datatype : "json",
			datafields : config.datafields,
			type : config.type ? config.type : "POST",
			root : config.root,
			contentType : config.contentType ? config.contentType :'application/x-www-form-urlencoded',
			url : config.url
		};
		return source;
	};
	var initDataAdapter = function(config, source, container){
		var defaultFormatData = function (data) {
            if (container.jqxComboBox('searchString') != undefined) {
                data.searchKey = container.jqxComboBox('searchString');
                return data;
            }
        };
		var formatData = config.formatData ? config.formatData : defaultFormatData;
		var dataAdapter = new $.jqx.dataAdapter(source, { formatData: formatData });
        return dataAdapter;
	};
	var init = function(config, container) {
		var souceConfig = config.source;
		var source = initSource(souceConfig);
		var dataAdapter = initDataAdapter(souceConfig, source, container);
		var value = config.value;
		var obj = {
			source : dataAdapter,
			width: config.width,
		height: config.height,
		disabled: config.disabled,
			rtl: config.rtl,
			theme: config.theme,
			selectedIndex: config.selectedIndex,
			multiSelect: config.multiSelect,
			showArrow: config.showArrow,
			// showCloseButtons: config.showCloseButtons,
			// validateSelection: config.validateSelection,
			minLength: config.minLength,
			displayMember: config.displayMember,
			valueMember: config.valueMember,
			placeHolder: config.placeHolder,
			popupZIndex: config.popupZIndex,
			checkboxes: config.checkboxes,
			scrollBarSize: config.scrollBarSize,
			enableHover: config.enableHover,
			enableSelection: config.enableSelection,
			enableBrowserBoundsDetection: config.enableBrowserBoundsDetection,
			autoOpen: config.autoOpen,
			dropDownHorizontalAlignment: "config.dropDownHorizontalAlignment}",
			dropDownHeight: config.dropDownHeight,
			dropDownWidth: "config.dropDownWidth}",
			autoDropDownHeight: config.autoDropDownHeight,
			itemHeight: config.itemHeight,
			renderer: config.renderer,
			renderSelectedItem: config.renderSelectedItem,
			openDelay: config.openDelay,
			closeDelay: config.closeDelay,
			animationType: "config.animationType}"
			// width : config.width,
			// placeHolder : config.placeHolder,
			// dropDownWidth : config.dropDownWidth,
			// height : config.height,
			// autoOpen: config.autoOpen,
			// source : dataAdapter,
			// remoteAutoComplete : true,
			// autoDropDownHeight : true,
			// selectedIndex : 0,
			// displayMember : config.displayMember,
			// valueMember : config.valueMember,
			// renderer : config.renderer,
			// renderSelectedItem : config.renderSelectedItem,
			// search : function(searchString) {
				// dataAdapter.dataBind();
			// }
		};
		if(config.filterable){
			$.extend(obj, {
				searchMode: config.searchMode,
				autoComplete: config.autoComplete,
				remoteAutoComplete: config.remoteAutoComplete,
				remoteAutoCompleteDelay: config.remoteAutoCompleteDelay,
				search: config.search ? config.search : function(searchString) {
					dataAdapter.dataBind();
				},
			});
		}
		container.jqxComboBox(obj);
		if(config.value){
			container.jqxComboBox('val', config.value);
			if(config.filterable){
				container.jqxComboBox('source').dataBind();
			}
		}
	};
	return {
		init : init
	};
})();
