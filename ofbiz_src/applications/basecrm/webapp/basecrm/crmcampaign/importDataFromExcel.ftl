<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<div class="row-fluid">
	<div class="span6">
		<input type="file" id="id-input-file-1" name="document" accept=".xlsx, .xls" />
	</div>
	<div class="span6">
		<button id="btnUpload" type="button" class="btn btn-primary form-action-button disabled">
			${uiLabelMap.CommonUpload}
		</button>
	</div>
</div>

<#assign dataField="[{ name: 'customerId', type: 'string' },
					{ name: 'partyFullName', type: 'string' },
					{ name: 'birthDate', type: 'date', other: 'date' },
					{ name: 'address1', type: 'string' },
					{ name: 'countryGeoId', type: 'string' },
					{ name: 'countryGeoName', type: 'string' },
					{ name: 'stateProvinceGeoId', type: 'string' },
					{ name: 'stateProvinceGeoName', type: 'string' },
					{ name: 'districtGeoId', type: 'string' },
					{ name: 'districtGeoName', type: 'string' },
					{ name: 'wardGeoId', type: 'string' },
					{ name: 'wardGeoName', type: 'string' },
					{ name: 'phoneHome', type: 'string' },
					{ name: 'phoneMobile', type: 'string' },
					{ name: 'phoneWork', type: 'string' },
					{ name: 'emailAddress', type: 'string' },
					{ name: 'childName1', type: 'string' },
					{ name: 'childName1birthDate', type: 'date', other: 'date' },
					{ name: 'childName2', type: 'string' },
					{ name: 'childName2birthDate', type: 'date', other: 'date' }]"/>
<#assign columnlist = "{ text: '${StringUtil.wrapString(uiLabelMap.MotherName)}', datafield: 'partyFullName', columntype: 'textbox', width: 200,
							validation: function (cell, value) {
								if (value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.MotherDOB)}', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range', columntype: 'datetimeinput',
							validation: function (cell, value) {
								var currentTime = new Date().getTime();
								value?value=value.getTime():value;
								if (value == 0) {
									return true;
								}
								if (currentTime > value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DateNotValid}' };
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.Phone1)}', datafield: 'phoneHome', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.Phone2)}', datafield: 'phoneMobile', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.Phone3)}', datafield: 'phoneWork', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.PRIMARY_EMAIL)}', datafield: 'emailAddress', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSWard)}', datafield: 'wardGeoName', width: 150, editable: false },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCounty)}', datafield: 'districtGeoName', width: 150, editable: false },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSStateProvince)}', datafield: 'stateProvinceGeoName', width: 150, editable: false },
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCountry)}', datafield: 'countryGeoName', width: 150, editable: false },
						{ text: '${StringUtil.wrapString(uiLabelMap.ChildName1)}', datafield: 'childName1', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.DOBChild1)}', datafield: 'childName1birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range', columntype: 'datetimeinput',
							validation: function (cell, value) {
								var currentTime = new Date().getTime();
								value?value=value.getTime():value;
								if (value == 0) {
									return true;
								}
								if (currentTime > value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DateNotValid}' };
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.ChildName2)}', datafield: 'childName2', columntype: 'textbox', width: 200 },
						{ text: '${StringUtil.wrapString(uiLabelMap.DOBChild2)}', datafield: 'childName2birthDate', cellsformat: 'dd/MM/yyyy', width: 200, filtertype: 'range', columntype: 'datetimeinput',
							validation: function (cell, value) {
								var currentTime = new Date().getTime();
								value?value=value.getTime():value;
								if (value == 0) {
									return true;
								}
								if (currentTime > value) {
									return true;
								}
								return { result: false, message: '${uiLabelMap.DateNotValid}' };
							}
						}"/>
						
<@jqGrid url="jqxGeneralServicer?sname=JQGetListTempCustomer" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
	showtoolbar="true" filtersimplemode="true" addrow="false" editable="true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deleteTempCustomer&jqaction=D" deleteColumn="customerId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateTempCustomer"
	editColumns="customerId;partyFullName;birthDate(java.sql.Date);address1;phoneHome;phoneMobile;phoneWork;emailAddress;childName1;childName1birthDate(java.sql.Date);childName2;childName2birthDate(java.sql.Date)"/>

<div class="row-fluid margin-top10">
	<div class="span12">
		<button id="btnCommit" type="button" class="btn btn-primary form-action-button pull-right hidden">
			${uiLabelMap.CommonSubmit}
		</button>
	</div>
</div>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>

<script>
	$( window ).unload(function() {
		DataAccess.execute({ url: "deleteAllTempCustomer", data: {} });
	});
	$(document).ready(function() {
		Uploader.init();
	});
	var Uploader = (function() {
		var initElements = function() {
			$("#id-input-file-1").ace_file_input({
				no_file: "${StringUtil.wrapString(uiLabelMap.EcommerceNoFiles)} ...",
				btn_choose: "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}",
				btn_change: "${StringUtil.wrapString(uiLabelMap.CommonChange)}",
				droppable:false,
				onchange: null,
				thumbnail:true,
				before_change:function(file, dropped) {
					if ($("#id-input-file-1").val()) {
						if ($("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").removeClass("disabled");
						}
					} else {
						if (!$("#btnUpload").hasClass("disabled")) {
							$("#btnUpload").addClass("disabled");
						}
					}
					return true;
				}
			});
			$("#jqxNotificationNestedSlide").jqxNotification({ opacity: 0.9, autoClose: true, template: "info" });
		};
		var handleEvents = function() {
			$("#btnUpload").click(function() {
				if ($("#id-input-file-1").val()) {
					Loading.show();
					setTimeout(function() {
						var form_data= new FormData();
						form_data.append("uploadedFile", $("#id-input-file-1")[0].files[0]);
						form_data.append("fileName", $("#id-input-file-1").val());
						$.ajax({
							url: "uploadExcelDocument",
							type: "POST",
							data: form_data,
							cache : false,
							contentType : false,
							processData : false,
							success: function() {}
						}).done(function(res) {
							Uploader.notify(res);
						});
					}, 300);
				}
			});
			$("#btnCommit").click(function() {
				Loading.show();
				setTimeout(function() {
					DataAccess.executeAsync({ url: "importCustomer", data: {} }, Uploader.notifyAndReload);
				}, 300);
			});
		};
		var notify = function(res) {
			Loading.hide();
			$("#jqxNotificationNestedSlide").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "error"});
				$("#notificationContentNestedSlide").text(errormes);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			} else {
				var message = "";
				res["_MESSAGE_"]?message=res["_MESSAGE_"]:message="${StringUtil.wrapString(uiLabelMap.uploadSuccessfully)}";
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "info"});
				$("#notificationContentNestedSlide").text(message);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				$("#btnCommit").removeClass("hidden");
				$("#jqxgrid").jqxGrid("updatebounddata");
			}
		};
		var notifyAndReload = function(res) {
			Loading.hide();
			$("#jqxNotificationNestedSlide").jqxNotification("closeLast");
			if (res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				var errormes = "";
				res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "error"});
				$("#notificationContentNestedSlide").text(errormes);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
			} else {
				var message = res["size"] + " ${StringUtil.wrapString(uiLabelMap.RecordsInsertedSuccessfully)}";
				$("#jqxNotificationNestedSlide").jqxNotification({ template: "info", autoClose: false});
				$("#notificationContentNestedSlide").text(message);
				$("#jqxNotificationNestedSlide").jqxNotification("open");
				$("#jqxgrid").jqxGrid("updatebounddata");
			}
		};
		var deleteAllTempCustomer = function() {
			DataAccess.executeAsync({ url: "deleteAllTempCustomer", data: {} });
		};
		return {
			init: function() {
				initElements();
				handleEvents();
			},
			notify: notify,
			notifyAndReload: notifyAndReload,
			deleteAllTempCustomer: deleteAllTempCustomer
		};
	})();
</script>
