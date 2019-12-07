<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField = "[
			{name: 'enumId', type: 'string'}, 
			{name: 'enumCode', type: 'string'},
			{name: 'sequenceId', type: 'number'},
			{name: 'description', type: 'string'}
		]"/>

<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSPriorityId)}', dataField: 'enumId', width: '25%', editable: false,}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', dataField: 'description'},
			{text: '${StringUtil.wrapString(uiLabelMap.BSSequenceNumber)}', dataField: 'sequenceId', width: '100px', filtertype: 'number', columntype: 'numberinput',
				initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
			        editor.jqxNumberInput({ decimalDigits: 0, digits: 1 });
			    }
			}
		"/>

<@jqGrid id="jqxgrid" addrow="true" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListPriority"
		createUrl="jqxGeneralServicer?sname=createPriority&jqaction=C" addColumns="enumId;enumCode;description;sequenceId"
		updateUrl="jqxGeneralServicer?sname=updatePriority&jqaction=U" editColumns="enumId;description;sequenceId" 
		bindresize="true"
	/>

<div>
	${screens.render("component://basesales/widget/SettingScreens.xml#NewPriority")}
</div>