<h3>${uiLabelMap.CoolerManager}</h3>
<div id="coolerTabs" class="cooler-tabs">
	<div id="loading-mk" style="display: block"><div class="loading-img">&nbsp;</div></div>
	<ul>
		<li><a href="#allCoolerContainer">${uiLabelMap.coolerListAll}</a></li>
		<li><a href="#listAgreementContainer">${uiLabelMap.coolerListAgreementTitleShort}</a></li>
		<li><a href="#listInventoryContainer">${uiLabelMap.coolerListInventory}</a></li>
	</ul>
	<div id="allCoolerContainer">
		<#assign dataField="[{ name: 'fixedAssetId', type: 'string' },
							 { name: 'fixedAssetName', type: 'string'},
							 { name: 'serialNumber', type: 'string'},
							 { name: 'groupName', type: 'string'},
							 { name: 'address1', type: 'string'},
							 { name: 'contactNumber', type: 'string'},
							 { name: 'city', type: 'string'}, 
							 { name: 'agreementId', type: 'string'}]"/>
		<#assign columnlist="{ text: '${uiLabelMap.fixedAssetId}', datafield: 'fixedAssetId', width: '160px', filterable: true, editable: false},
							 { text: '${uiLabelMap.fixedAssetName}', datafield: 'fixedAssetName', width: '120px', editable: false},
							 { text: '${uiLabelMap.serialNumber}', datafield: 'serialNumber', width: '150px', editable: false},
							 { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: '200px', editable: false},
							 { text: '${uiLabelMap.address}', datafield: 'address1', width: '200px', editable: false},
							 { text: '${uiLabelMap.phone}', datafield: 'contactNumber', width: '120px', editable: false},
							 { text: '${uiLabelMap.aggreementId}', datafield: 'agreementId', width: '100px', editable: false}"/>
		
		<@jqGrid id="allCooler" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false"
				 	url="jqxGeneralServicer?sname=JQGetAssetCooler"/>
	</div>
	<div id="listAgreementContainer">
		<#assign dataField="[{ name: 'fixedAssetId', type: 'string' },
							 { name: 'fixedAssetName', type: 'string'},
							 { name: 'serialNumber', type: 'string'},
							 { name: 'groupName', type: 'string'},
							 { name: 'address1', type: 'string'},
							 { name: 'contactNumber', type: 'string'},
							 { name: 'city', type: 'string'}, 
							 { name: 'agreementId', type: 'string'}]"/>
		<#assign columnlist="{ text: '${uiLabelMap.fixedAssetId}', datafield: 'fixedAssetId', width: '160px', filterable: true, editable: false},
							 { text: '${uiLabelMap.fixedAssetName}', datafield: 'fixedAssetName', width: '120px', editable: false},
							 { text: '${uiLabelMap.serialNumber}', datafield: 'serialNumber', width: '150px', editable: false},
							 { text: '${uiLabelMap.groupName}', datafield: 'groupName', width: '200px', editable: false},
							 { text: '${uiLabelMap.address}', datafield: 'address1', width: '200px', editable: false},
							 { text: '${uiLabelMap.phone}', datafield: 'contactNumber', width: '120px', editable: false},
							 { text: '${uiLabelMap.aggreementId}', datafield: 'agreementId', width: '100px', editable: false}"/>
		
		<@jqGrid id="listAgreement" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false"
				 	url="jqxGeneralServicer?sname=JQGetAssetCoolerAgreement"/>
	</div>
	<div id="listInventoryContainer">
		<#assign dataField="[{ name: 'fixedAssetId', type: 'string' },
							 { name: 'fixedAssetName', type: 'string'},
							 { name: 'serialNumber', type: 'string'},
							 { name: 'agreementId', type: 'string'}]"/>
		<#assign columnlist="{ text: '${uiLabelMap.fixedAssetId}', datafield: 'fixedAssetId', filterable: true, editable: false},
							 { text: '${uiLabelMap.fixedAssetName}', datafield: 'fixedAssetName', editable: false},
							 { text: '${uiLabelMap.serialNumber}', datafield: 'serialNumber', editable: false}"/>
		
		<@jqGrid id="listInventor" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" showtoolbar="false" addrow="false" deleterow="false"
				 	url="jqxGeneralServicer?sname=JQGetAssetCoolerInventory"/>
	</div>
</div>
