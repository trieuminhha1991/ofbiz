if (typeof (CreateContact) == "undefined") {
	var CreateContact = (function() {
		var initJqxElements = function() {
			$("#menuContactType").jqxMenu({
				theme : 'olbius',
				width : 150,
				autoOpenPopup : false,
				mode : 'popup'
			});
		};
		var handleEvents = function() {
			$("#createContact").on(
					'click',
					function(event) {
						var scrollTop = $(window).scrollTop();
						var scrollLeft = $(window).scrollLeft();
						$("#menuContactType").jqxMenu('open',
								parseInt(event.clientX) - 30 + scrollLeft,
								parseInt(event.clientY) + 15 + scrollTop);
					});
			$("#menuContactType").on('itemclick', function(event) {
				var args = event.args;
				var itemId = $(args).attr('id');
				CreateMode = true;
				clearAllForm();
				switch (itemId) {
				case "btnCreateFamily":
					RepresentativeMember.showLayer();
					globalPartyTypeId = "PERSON";
					$("#txtFulName").focus();
					break;
				case "btnCreateBusinesses":
					Bussinesses.showLayer();
					$("#txtCorporationName").focus();
					globalPartyTypeId = "BUSINESSES";
					break;
				case "btnCreateSchool":
					School.showLayer();
					globalPartyTypeId = "SCHOOL";
					$("#txtCorporationName").focus();
					break;
				default:
					break;
				}
			});
		};
		var clearAllForm = function() {
			Grid.clearForm($('#infoContainer'));
			ContactAddressLayer.setValue("");
			CookieLayer.setCurrentParty("", "");
			$("#txtPrimaryPhone").jqxDropDownList('clearSelection');
			$("#txtShippingPhone").jqxDropDownList('clearSelection');
			$("#txtGender").jqxDropDownList('clearSelection');
			$("#txtGenderBusinesses").jqxDropDownList('clearSelection');
			$("#ClaimPartyId").jqxDropDownList('clearSelection');
			OwnerContact.clean();
			OwnerContact.disabled();

			RepresentativeMember.extendId = {};
			Bussinesses.extendId = {};
			School.extendId = {};
			$('#postalCtm').val("");
			$("#BPartyIdBussiness").html("");
			$("#BPartyId").html("___");

			$('#familyInfoEditable').jqxValidator('hide');
			$('#businessesInfoEditable').jqxValidator('hide');
			$('#generalInformation').jqxValidator('hide');
			globalPartyTypeId = "";
			globalFamilyId = "";
			Family.setValue("");
			ContactLayer.setValue({
				listPrimaryLocation : [ {
					countryGeoId : 'VNM'
				} ]
			});
			ExtendScreen.refresh();
		};
		return {
			init : function() {
				initJqxElements();
				handleEvents();
			},
			clearAllForm : clearAllForm
		};
	})();
}