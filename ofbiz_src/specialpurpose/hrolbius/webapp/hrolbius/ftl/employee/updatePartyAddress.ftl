<div class='row-fluid'>
<script>
	var listCountry = [<#if listCountry?exists><#list listCountry as country>{geoId : "${country.geoId}",geoName: "${StringUtil.wrapString(country.geoName?default(''))}"},</#list></#if>];
	var listState = [<#if listState?exists><#list listState as state>{geoId : "${state.geoId}",geoName: "${StringUtil.wrapString(state.geoName?default(''))}",geoIdFrom : "${state.geoIdFrom}",},</#list></#if>];
	var listCounty = [<#if listCounty?exists><#list listCounty as county>{geoId : "${county.geoId}",geoName: "${StringUtil.wrapString(county.geoName?default(''))}",geoIdFrom : "${county.geoIdFrom}",},</#list></#if>];
	var listContactMechPurposeType = [<#if listContactMechPurposeType?exists><#list listContactMechPurposeType as pt>{contactMechPurposeTypeId : "${pt.contactMechPurposeTypeId}",description: "${StringUtil.wrapString(pt.description?default(''))}"},</#list></#if>];
</script>
<#assign dataField="[{ name: 'contactMechId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'fromDate', type: 'date' },
					 { name: 'address1', type: 'string' },
					 { name: 'city', type: 'string' },
					 { name: 'postalCode', type: 'string' },
					 { name: 'countryGeoId', type: 'string'},
					 { name: 'stateProvinceGeoId', type: 'string'},
					 { name: 'countyGeoId', type: 'string' },
					 { name: 'contactMechPurposeTypeId', type: 'string' }]
					"/>				

<#assign columnlist="{ text: '${uiLabelMap.contactMechId}', datafield: 'contactMechId', hidden: true},
					 { text: '${uiLabelMap.partyId}', datafield: 'partyId', hidden: true},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', hidden: true},
 					 { text: '${uiLabelMap.PartyAddressLine}', datafield: 'address1'},
 					 { text: '${uiLabelMap.postalCode}', datafield: 'postalCode', hidden: true},
 					 { text: '${uiLabelMap.city}', datafield: 'city', hidden: true},
                     { text: '${uiLabelMap.HRCommonNational}', datafield: 'countryGeoId', width: 150, filtertype: 'checkedlist',columntype: 'dropdownlist',
                     	createfilterwidget: function(column, columnElement, widget){
						    var filterBoxAdapter = new $.jqx.dataAdapter(listCountry, {autoBind: true});
							var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'geoName', autoDropDownHeight: false,valueMember : 'geoId', filterable:true, searchMode:'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupAddress').jqxGrid('getrowdata', row);
							for(var x in listCountry){
								if(listCountry[x].geoId  && val.countryGeoId && listCountry[x].geoId == val.countryGeoId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listCountry[x].geoName+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
                            var sourceGlat =
					            {
					                localdata: listCountry,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300,  displayMember: 'geoName', valueMember : 'geoId', filterable: true, searchMode: 'containsignorecase'});
							 	editor.on('select', function(event){
							       var args = event.args;
							       if (args) {
							       		countrySelected = args.item.value;
							       }
							 	});
							 }
                     },{ text: '${uiLabelMap.DAStateProvinceGeo}', datafield: 'stateProvinceGeoId', width: 150, filtertype: 'checkedlist',columntype: 'dropdownlist',
                     	createfilterwidget: function(column, columnElement, widget){
                     		var valid = localStorage.getItem('currentCountryFilter');
                     		if(!valid){
                     			var filterBoxAdapter = new $.jqx.dataAdapter(listState, {autoBind: true});
								var dataSoureList = filterBoxAdapter.records;
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'geoName', autoDropDownHeight: false,valueMember : 'geoId', filterable:true, searchMode:'containsignorecase'});
                     		}else{
                     			var tmp = findGeo(listState, valid);
                     			if(tmp.length){
                     				var filterBoxAdapter = new $.jqx.dataAdapter(tmp, {autoBind: true});
	                     			var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'geoName', autoDropDownHeight: false,valueMember : 'geoId', filterable:true, searchMode:'containsignorecase'});
	                     			localStorage.removeItem('currentCountryFilter');
                     			}
                     		}
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupAddress').jqxGrid('getrowdata', row);
							for(var x in listState){
								if(listState[x].geoId  && val.stateProvinceGeoId && listState[x].geoId == val.stateProvinceGeoId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listState[x].geoName+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
							var tmp = new Array();
							if(countrySelected != -1){
								tmp = findGeo(listState, countrySelected)
							}
							var dm = tmp.length ? tmp : listState;
                            var sourceGlat =
					            {
					                localdata: dm,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300, displayMember: 'geoName', valueMember : 'geoId', filterable: true, searchMode: 'containsignorecase'}); 
								editor.on('select', function(event){
							       var args = event.args;
							       if (args) {
							       		stateSelected = args.item.value;
							       }
							 	});
							 }
                   },{ text: '${uiLabelMap.PartyDistrictGeoId}', datafield: 'countyGeoId', width: 150, filtertype: 'checkedlist',columntype: 'dropdownlist',
                  		createfilterwidget: function(column, columnElement, widget){
                  			var valid = localStorage.getItem('currentStateFilter');
                     		if(!valid){
                     			var filterBoxAdapter = new $.jqx.dataAdapter(listCounty, {autoBind: true});
								var dataSoureList = filterBoxAdapter.records;
							    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'geoName', autoDropDownHeight: false,valueMember : 'geoId', filterable:true, searchMode:'containsignorecase', filterable: true, searchMode: 'containsignorecase'});
                     		}else{
                     			var tmp = findGeo(listCounty, valid);
                     			if(tmp.length){
                     				var filterBoxAdapter = new $.jqx.dataAdapter(tmp, {autoBind: true});
	                     			var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'geoName', autoDropDownHeight: false,valueMember : 'geoId', filterable:true, searchMode:'containsignorecase', filterable: true, searchMode: 'containsignorecase'});
                     			}
                     			localStorage.removeItem('currentStateFilter');
                     		}
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupAddress').jqxGrid('getrowdata', row);
							for(var x in listCounty){
								if(listCounty[x].geoId  && val.countyGeoId && listCounty[x].geoId == val.countyGeoId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listCounty[x].geoName+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
							var tmp = new Array();
							if(stateSelected != -1){
								tmp = findGeo(listCounty, stateSelected)
							}
							var dm = tmp.length ? tmp : listCounty;
                            var sourceGlat =
					            {
					                localdata: tmp,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300,  displayMember: 'geoName', valueMember : 'geoId'}); 
							 }
                     },{ text: '${uiLabelMap.ContactMechType}', datafield: 'contactMechPurposeTypeId', width: 150, filtertype: 'checkedlist',columntype: 'dropdownlist',
                  		createfilterwidget: function(column, columnElement, widget){
             				var filterBoxAdapter = new $.jqx.dataAdapter(listContactMechPurposeType, {autoBind: true});
                 			var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'contactMechPurposeTypeId', filterable:true, searchMode:'containsignorecase', filterable: true, searchMode: 'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupAddress').jqxGrid('getrowdata', row);
							for(var x in listContactMechPurposeType){
								if(listContactMechPurposeType[x].contactMechPurposeTypeId  
									&& val.contactMechPurposeTypeId 
									&& listContactMechPurposeType[x].contactMechPurposeTypeId == val.contactMechPurposeTypeId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listContactMechPurposeType[x].description+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
                            var sourceGlat =
					            {
					                localdata: listContactMechPurposeType,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300,  displayMember: 'description', valueMember : 'contactMechPurposeTypeId'}); 
							 }
                     }"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartyGroupAddress&partyId=${party.partyId}" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	id="partyGroupAddress"
	showtoolbar = "true" deleterow="true"
	width="980"
	bindresize="false"
	autorowheight="true"
	customLoadFunction="true"
	removeUrl="jqxGeneralServicer?sname=deletePartyContact&jqaction=D" deleteColumn="partyId;contactMechId;contactMechPurposeTypeId;fromDate(java.sql.Timestamp)"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyPostalAddress" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="partyId;city;countryGeoId;stateProvinceGeoId;countyGeoId;address1;contactMechPurposeTypeId;postalCode" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyPostalAddress"  editColumns="contactMechId;address1;city;postalCode;partyId;countryGeoId;stateProvinceGeoId;countyGeoId"
/>
<div id="popupAddRow">
	<div>${uiLabelMap.DAAddNewAddress}</div>
    <div style="overflow: hidden;">
    	<form class="form-horizontal">
    		<input type="hidden" value="${party.partyId?if_exists}" id="partyCreated"/>
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.HRCommonNational}
					</label>
					<div class="controls">
						<div id="nation"></div>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.DAStateProvinceGeo}
					</label>
					<div class="controls">
						<div id="province"></div>
					</div>
				</div>				
				<div class="control-group no-left-margin">
					<label class="control-label">
						${uiLabelMap.PartyDistrictGeoId}
					</label>
					<div class="controls">
						<div id="district"></div>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.PartyAddressLine}
					</label>
					<div class="controls">
						<input type="text" id="address"/>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.ContactMechType}
					</label>
					<div class="controls">
						<div id="ContactMechType"></div>
					</div>
				</div>				
		   	</div>	
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary' style="margin-right: 5px; margin-top: 10px; padding: 0 10px!important;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>
<script>
	if(!loadFormPA){
		loadFormPA = true;
		function findGeo(list, par){
			var tmp = new Array();
			for(var x in list){
				if(list[x].geoIdFrom == par){
					tmp.push(list[x]);
				}
			}
			return tmp;
		}
		$(function(){
			var countrySelected = -1;
			var stateSelected = -1;
			var creatingPA = false;
			$('#partyGroupAddress').on("filter", function(e){
  				var filter = $("#partyGroupAddress").jqxGrid("getfilterinformation");
  				for(var x in filter){
  					if(filter[x].filtercolumn == "countryGeoId"){
  						var fi = filter[x].filter.getfilters();
  						if(fi.length){
  							//widget.jqxDropDownList(\"destroy\");
  							localStorage.setItem("currentCountryFilter", fi[0].value);
  						}
  					}else if(filter[x].filtercolumn == "stateProvinceGeoId"){
  						var fi = filter[x].filter.getfilters();
  						if(fi.length){
  							localStorage.setItem("currentStateFilter", fi[0].value);
  						}
  					}
  				}
  			});
			var popup = $("#popupAddRow");
			popup.jqxWindow({
		        width: 800, height: 350, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01, theme: 'olbius'           
		    });
		    popup.on('close', function (event) { 
		    	popup.jqxValidator('hide');
		    }); 
		    var nationDd = $('#nation');
			nationDd.jqxDropDownList({
				theme: 'olbius',
				source: listCountry,
				width: 218,
				displayMember: "geoName",
				filterable: true,
				valueMember : 'geoId'
			});
			nationDd.on("select", function(event){
				var args = event.args;
		       if (args) {
		       		var selected = args.item.value;
		       		var arr = findGeo(listState, selected);
		       		provinceDd.jqxDropDownList("source", arr);
		       }
			});
			var provinceDd = $('#province');
			provinceDd.jqxDropDownList({
				theme: 'olbius',
				source: listState,
				width: 218,
				displayMember: "geoName",
				filterable: true,
				valueMember : 'geoId'
			});
			provinceDd.on("select", function(event){
				var args = event.args;
		       if (args) {
		       		var selected = args.item.value;
		       		var arr = findGeo(listCounty, selected);
		       		districtDd.jqxDropDownList("source", arr);
		       }
			});
			var districtDd = $('#district');
			districtDd.jqxDropDownList({
				theme: 'olbius',
				source: listCounty,
				width: 218,
				displayMember: "geoName",
				filterable: true,
				valueMember : 'geoId'
			});
			var ctmDd = $('#ContactMechType');
			ctmDd.jqxDropDownList({
				theme: 'olbius',
				source: listContactMechPurposeType,
				width: 218,
				filterable: true,
				displayMember: "description",
				valueMember : 'contactMechPurposeTypeId'
			});
			popup.jqxValidator({
			   	rules: [{
		            input: "#nation", 
		            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
		            action: 'blur', 
		            rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		        },{
		            input: "#province", 
		            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
		            action: 'blur', 
		            rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		        },{
		            input: "#address", 
		            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
		            action: 'blur', 
		            rule: 'required'
			 	},{
		            input: "#ContactMechType", 
		            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
		            action: 'blur', 
		            rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		        }]
			 });
			 var skillJqx = $("#partyGroupAddress");
			 $("#alterSave").click(function () {
				if(!$('#popupAddRow').jqxValidator('validate')){
					return;
				}
				var index = provinceDd.jqxDropDownList("getSelectedItem");
				var city = index.label;
				var stateProvinceGeoId = index ? index.value : "";
				index = nationDd.jqxDropDownList("getSelectedItem");
				var countryGeoId = index ? index.value : "";
				index = districtDd.jqxDropDownList("getSelectedItem");
				var countyGeoId = index ? index.value : "";
				var address1 = $("#address").val();
				index = ctmDd.jqxDropDownList("getSelectedItem");
				var contactMechPurposeTypeId = index ? index.value : "";
		    	var row = { 
		    		partyId : $("#partyCreated").val(),
		    		city: city,
		    		countryGeoId: countryGeoId,
		    		stateProvinceGeoId: stateProvinceGeoId,
		    		countyGeoId: countyGeoId,
		    		address1: address1,
		    		contactMechPurposeTypeId: contactMechPurposeTypeId,
		    		postalCode: 10000
		    	  };
			    skillJqx.jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        skillJqx.jqxGrid('clearSelection');                        
		        skillJqx.jqxGrid('selectRow', 0);  
		        popup.jqxWindow('close');
		    });
		});
	}
</script>
</div>