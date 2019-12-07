<script src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var tabsdiv = null;
    var information = null;
    var notes = null;
    tabsdiv = $($(parentElement).children()[0]);
    if (tabsdiv != null) {
        information = tabsdiv.find('.information');
        notes = tabsdiv.find('.notes');
        var title = tabsdiv.find('.title');
        title.text(\"${StringUtil.wrapString(uiLabelMap.DAOwnerStoreInfo)}\");
        
        var container = $('<div style=\"margin: 5px;\"></div>')
        container.appendTo($(information));
        
        var photocolumn = $('<div style=\"float: left; width: 100%;\"></div>');
        container.append(photocolumn);
        
        var loadingStr = '<div id=\"info_loader_' + index + '\" style=\"overflow: hidden; position: absolute; display: none; left: 45%; top: 25%;\" class=\"jqx-rc-all jqx-rc-all-olbius\">';
        loadingStr += '<div style=\"z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;\" ';
        loadingStr += ' class=\"jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius\">';
        loadingStr += '<div style=\"float: left;\"><div style=\"float: left; overflow: hidden; width: 32px; height: 32px;\" class=\"jqx-grid-load\"></div>';
        loadingStr += '<span style=\"margin-top: 10px; float: left; display: block; margin-left: 5px;\">${uiLabelMap.DALoading}...</span></div></div></div>';
        var notescontainer = $(loadingStr);
        $(notes).append(notescontainer);
        var partyId = datarecord.partyId;
        var dataField = [
                  {name : \"partyId\", type:\"String\"},
                  {name : \"fullName\", type : \"String\"},
                  {name : \"birthDate\", type :\"date\"},
            	  {name : \"fromDate\", type : \"date\"},
             ];
        var column = [
                  {text : \"${StringUtil.wrapString(uiLabelMap.DAOwnerStoreId)}\", datafield : \"partyId\", width : \"15%\"},
                  {text : \"${StringUtil.wrapString(uiLabelMap.DAOwnerStoreName)}\", datafield : \"fullName\"},
                  {text : \"${StringUtil.wrapString(uiLabelMap.DABirthday)}\", datafield :\"birthDate\", width :\"15%\"},
            	  {text : \"${StringUtil.wrapString(uiLabelMap.DAFromDate)}\", datafield : \"fromDate\", cellsformat : \"dd/MM/yyyy HH:mm:ss\", width : \"20%\"}
             ];
        var grid = $(photocolumn);
        GridUtils.initGrid({url : 'JQGetOwnerStoreInformation&partyId=' + partyId,width : '100%',localization: getLocalization(),height :175},dataField,column,null,grid);
        $(tabsdiv).jqxTabs({ theme: 'energyblue', width: '98%', height: 170});
        var loadpage = function(url, tabClass, data, index){
        	$.ajax({
        		type: 'POST',
			  	url: url,
			  	data: data,
			  	dataType: 'json',
			  	beforeSend: function () {
					$(\"#info_loader_\" + index).show();
				},
				success : function(data){
					var tabActive = tabsdiv.find('.' + tabClass);
					var container2 = $('<div style=\"margin: 5px;\"></div>')
			        container2.appendTo($(tabActive));
					var column1 = $('<div style=\"float: left; width: 40%;\"></div>');
			        var column2 = $('<div style=\"float: left; width: 60%;\"></div>');
			        container2.append(column1);
			        container2.append(column2);
			        
			        var partyIdQ = data.partyId;
			        var partyNameViewQ = data.partyNameView;
			        var postalAddressQ = data.postalAddress;
			        var phoneQ = data.phone;
			        var emailQ = data.email;
			        
			        if (partyIdQ != undefined) {
			        	var partyIdDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DACustomerId)}:</b> ' + partyIdQ + '</div>';
			        	$(column1).append(partyIdDiv);
			        }
			        if (phoneQ != undefined) {
			        	var phoneDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAPhone)}:</b> ' + phoneQ + '</div>';
			        	$(column1).append(phoneDiv);
			        }
                    if (emailQ != undefined) {
			        	var emailDiv = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAEmail)}:</b> ' + emailQ + '</div>';
			        	$(column1).append(emailDiv);
			        }
                    if (postalAddressQ != undefined) {
			        	for (var i = 0; i < postalAddressQ.length; i++) {
			        		var postalAddressQItem = postalAddressQ[i];
			        		var div1 = '<div style=\"margin: 10px;\"><b>${StringUtil.wrapString(uiLabelMap.DAAddress)} ' + (i + 1) + ':</b></div>';
			        		$(column2).append(div1);
			        		var div2 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAAddress)}: ' + postalAddressQItem.address1 + '</div>';
			        		$(column2).append(div2);
			        		if (postalAddressQItem.countryGeoId) {
			        			var div3 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountry)}: ' + postalAddressQItem.countryGeoId + '</div>';
				        		$(column2).append(div3);
							}
			        		if (postalAddressQItem.stateProvinceGeoId) {
			        			var div4 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DAStateProvince)}: ' + postalAddressQItem.stateProvinceGeoId + '</div>';
				        		$(column2).append(div4);
							}
			        		if (postalAddressQItem.countyGeoId) {
			        			var div5 = '<div style=\"margin: 2px 15px;\">${StringUtil.wrapString(uiLabelMap.DACountyGeoId)}: ' + postalAddressQItem.countyGeoId + '</div>';
				        		$(column2).append(div5);
							}
			        	}
			        }
				},
				error : function(){
					
				},
				complete: function() {
			        $(\"#info_loader_\" + index).hide();
			    }
        	})
        }
        loadpage('getGeneralInformationOfParty', 'notes', {'partyId' : partyId}, index);
    }
}"/>
<#assign rowdetailstemplateAdvance= "<ul style='margin-left: 30px;'><li>${StringUtil.wrapString(uiLabelMap.DAGeneralInformation)}</li><li class='title'></li></ul><div class='notes'></div><div class='information'></div>"/>

<#assign dataField = "[
		{name : 'partyId', type : 'string'},
		{name : 'partyIdFrom', type : 'string'},
		{name : 'fullName', type : 'string'},
		{name : 'fromDate', type : 'date', other : 'Timestamp'},
		{name : 'thruDate', type : 'date', other : 'Timestamp'}]"/>
<#assign columnlist = "
		{text : '${StringUtil.wrapString(uiLabelMap.distributorName)}', datafield : 'partyIdFrom', width : '15%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DACustomerId)}', datafield : 'partyId', width : '15%'},
		{text : '${StringUtil.wrapString(uiLabelMap.DACustomerName)}', datafield : 'fullName'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', datafield : 'fromDate', width : '25%', cellsformat : 'dd/MM/yyyy', filtertype: 'range'},
		{text : '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', datafield : 'thruDate', width : '25%', cellsformat : 'dd/MM/yyyy', filtertype: 'range'}"/>
<@jqGrid dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true" showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup"
		 url="jqxGeneralServicer?sname=JQGetListCustomerBySupp&partyId=${parameters.userLogin.partyId?if_exists}" initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" rowdetailstemplateAdvance=rowdetailstemplateAdvance
	 />