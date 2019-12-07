<#macro loading id="loadingMacro" width="100" height="100" 
	autofit="true" isRandom="true" fixed="true" option=0
	top="40%" zIndex="99999" hide="false" background="rgba(255,255,255,0.3)" opacity="">
	<#assign aDateTime = .now?long>
	
	<#if option != 0>
		<#assign index = option/>
	<#else>
		<#assign index = aDateTime % 11/>
	</#if>
	<div id="${id}" class='loading-container
		<#if fixed=="true">loading-container-fixed</#if>
		<#if hide=="true">hide</#if>'
		style="z-index: ${zIndex}; 
		<#if background!="">background: ${background};</#if>
		">
		<div class='loading-content' style="top: ${top}">
			<#if index == 0>
				<div class="sk-rotating-plane loading-item"></div>
			</#if>
			<#if index == 1>
				<div class="sk-double-bounce loading-item">
			      <div class="sk-child sk-double-bounce1"></div>
			      <div class="sk-child sk-double-bounce2"></div>
			    </div>
			</#if>
			<#if index == 2>
				<div class="sk-wave loading-item">
			      <div class="sk-rect sk-rect1"></div>
			      <div class="sk-rect sk-rect2"></div>
			      <div class="sk-rect sk-rect3"></div>
			      <div class="sk-rect sk-rect4"></div>
			      <div class="sk-rect sk-rect5"></div>
			    </div>
			</#if>
			<#if index == 3>
				<div class="sk-wandering-cubes loading-item">
			      <div class="sk-cube sk-cube1"></div>
			      <div class="sk-cube sk-cube2"></div>
			    </div>
			</#if>
			<#if index == 4>
				<div class="sk-spinner sk-spinner-pulse loading-item"></div>
			</#if>
			<#if index == 5>
				<div class="sk-chasing-dots loading-item">
			      <div class="sk-child sk-dot1"></div>
			      <div class="sk-child sk-dot2"></div>
			    </div>
			</#if>
			<#if index == 6>
				<div class="sk-three-bounce loading-item">
			      <div class="sk-child sk-bounce1"></div>
			      <div class="sk-child sk-bounce2"></div>
			      <div class="sk-child sk-bounce3"></div>
			    </div>
			</#if>
			<#if index == 7>
				<div class="sk-circle loading-item">
			      <div class="sk-circle1 sk-child"></div>
			      <div class="sk-circle2 sk-child"></div>
			      <div class="sk-circle3 sk-child"></div>
			      <div class="sk-circle4 sk-child"></div>
			      <div class="sk-circle5 sk-child"></div>
			      <div class="sk-circle6 sk-child"></div>
			      <div class="sk-circle7 sk-child"></div>
			      <div class="sk-circle8 sk-child"></div>
			      <div class="sk-circle9 sk-child"></div>
			      <div class="sk-circle10 sk-child"></div>
			      <div class="sk-circle11 sk-child"></div>
			      <div class="sk-circle12 sk-child"></div>
			    </div>
			</#if>
			<#if index == 8>
				<div class="sk-cube-grid loading-item">
			      <div class="sk-cube sk-cube1"></div>
			      <div class="sk-cube sk-cube2"></div>
			      <div class="sk-cube sk-cube3"></div>
			      <div class="sk-cube sk-cube4"></div>
			      <div class="sk-cube sk-cube5"></div>
			      <div class="sk-cube sk-cube6"></div>
			      <div class="sk-cube sk-cube7"></div>
			      <div class="sk-cube sk-cube8"></div>
			      <div class="sk-cube sk-cube9"></div>
			    </div>
			</#if>
			<#if index == 9>
				<div class="sk-fading-circle loading-item">
			      <div class="sk-circle1 sk-circle"></div>
			      <div class="sk-circle2 sk-circle"></div>
			      <div class="sk-circle3 sk-circle"></div>
			      <div class="sk-circle4 sk-circle"></div>
			      <div class="sk-circle5 sk-circle"></div>
			      <div class="sk-circle6 sk-circle"></div>
			      <div class="sk-circle7 sk-circle"></div>
			      <div class="sk-circle8 sk-circle"></div>
			      <div class="sk-circle9 sk-circle"></div>
			      <div class="sk-circle10 sk-circle"></div>
			      <div class="sk-circle11 sk-circle"></div>
			      <div class="sk-circle12 sk-circle"></div>
			    </div>
			</#if>
			<#if index == 10>
				<div class="sk-folding-cube loading-item">
			      <div class="sk-cube1 sk-cube"></div>
			      <div class="sk-cube2 sk-cube"></div>
			      <div class="sk-cube4 sk-cube"></div>
			      <div class="sk-cube3 sk-cube"></div>
			    </div>
			</#if>
		</div>
	</div>
</#macro>
<#macro jqxCombobox url datafields root="resutls" id="jqxcombobox" width="100%" height="25" disabled="false" rtl="false" theme="olbius" selectedIndex="-1" multiSelect="false" showArrow="true"
					showCloseButtons="true" validateSelection="null" searchMode="startswith" autoComplete="false" remoteAutoComplete="false" 
					remoteAutoCompleteDelay="300" minLength="2" search="null" displayMember="" valueMember="" placeHolder="" popupZIndex="20000"
					checkboxes="false" scrollBarSize="17" enableHover="true" enableSelection="true" enableBrowserBoundsDetection="false" autoOpen="false"
					dropDownHorizontalAlignment="left" dropDownHeight="200" dropDownWidth="200" autoDropDownHeight="false" itemHeight="-1" renderer="null"
					renderSelectedItem="null" openDelay="350" closeDelay="400" animationType="slide" type="POST" value=""
					contentType="application/x-www-form-urlencoded" data="null" formatData="" customLoadFunction="false" filterable="true">
	<div id="${id}"></div> 
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.extend.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/comboboxUtil.js"></script>
    <script>
	<#if customLoadFunction != "true">
	    $(document).ready(function(){
	    <#else>
		var initCombobox${id} = function(){
		</#if>
	    	var container = $("#${id}");
	    	var configCombobox${id} = {
			filterable: ${filterable},
	    		width: "${width}",
	    		height: "${height}",
	    		disabled: ${disabled},
				rtl: ${rtl},
				theme: "${theme}",
				selectedIndex: ${selectedIndex},
				multiSelect: ${multiSelect},
				showArrow: ${showArrow},
				showCloseButtons: ${showCloseButtons},
				validateSelection: ${validateSelection},
				value: "${value}",
				<#if filterable=="" || filterable=="true">
					searchMode: "${searchMode}",
					autoComplete: ${autoComplete},
					remoteAutoComplete: ${remoteAutoComplete},
					remoteAutoCompleteDelay: ${remoteAutoCompleteDelay},
					search: ${search},
				</#if>
				minLength: ${minLength},
				displayMember: "${displayMember}",
				valueMember: "${valueMember}",
				placeHolder: "${placeHolder}",
				popupZIndex: ${popupZIndex},
				checkboxes: ${checkboxes},
				scrollBarSize: ${scrollBarSize},
				enableHover: ${enableHover},
				enableSelection: ${enableSelection},
				enableBrowserBoundsDetection: ${enableBrowserBoundsDetection},
				autoOpen: ${autoOpen},
				dropDownHorizontalAlignment: "${dropDownHorizontalAlignment}",
				dropDownHeight: ${dropDownHeight},
				dropDownWidth: "${dropDownWidth}",
				autoDropDownHeight: ${autoDropDownHeight},
				itemHeight: ${itemHeight},
				renderer: ${renderer},
				renderSelectedItem: ${renderSelectedItem},
				openDelay: ${openDelay},
				closeDelay: ${closeDelay},
				animationType: "${animationType}"
	    	};
	    	var sourceConfig${id} = {
				datafields : ${datafields},
				type : "${type}",
				root : "${root}",
				contentType : "${contentType}",
				url: "${url}",
				data: ${data},
				<#if formatData != "">
				formatData : ${formatData} 
				</#if>
	    	};
	    	configCombobox${id}.source = sourceConfig${id};
	    	Comboxbox.init(configCombobox${id}, container);
	    	<#if dropDownWidth=="100%">
	    		(function scaleFullWidthDropDown(){
	    			var par = container.parent();
	    			var width = par.width();
	    			container.jqxComboBox("dropDownWidth", width - 19);
	    		})();
	    	</#if>
	    <#if customLoadFunction != "true">
	    });
	    <#else>
	    }
		</#if>

    </script>
</#macro>
