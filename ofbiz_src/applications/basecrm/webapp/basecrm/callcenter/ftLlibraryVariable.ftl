<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/crmresources/js/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script src="/crmresources/js/bootbox.min.js"></script>
<script type="text/javascript" src="/crmresources/js/notify.js"></script>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<@jqOlbCoreLib hasDropDownList=true hasComboBox=true hasValidator=true/>
<script>
	var multiLang = {
			addSuccess: "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}",
			updateSuccess: "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}",
			male: "${StringUtil.wrapString(uiLabelMap.DmsMale)}",
			female: "${StringUtil.wrapString(uiLabelMap.DmsFemale)}",
			age: "${StringUtil.wrapString(uiLabelMap.DmsAge)}</span>",
			updateError: "${StringUtil.wrapString(uiLabelMap.DmsUpdateError)}",
			chooseImagesToUpload: "${StringUtil.wrapString(uiLabelMap.ChooseImagesToUpload)}",
			fileNotFound: "${StringUtil.wrapString(uiLabelMap.FileNotFound)}",
			fieldRequired: "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}",
			containSpecialSymbol: "${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}",
			dateNotValid: "${StringUtil.wrapString(uiLabelMap.DateNotValid)}",
			DmsQuantityNotValid: "${StringUtil.wrapString(uiLabelMap.DmsQuantityNotValid)}",
			IdentityNotValid: "${StringUtil.wrapString(uiLabelMap.IdentityNotValid)}",
			DmsPriceNotValid: "${StringUtil.wrapString(uiLabelMap.DmsPriceNotValid)}",
			filterchoosestring: "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}",
			notEnterAPhoneNumber: "${StringUtil.wrapString(uiLabelMap.DmsNotEnterAPhoneNumber)}",
			PhoneNotValid: "${StringUtil.wrapString(uiLabelMap.PhoneNotValid)}",
			EmailNotValid:"${StringUtil.wrapString(uiLabelMap.EmailNotValid)}",
			DmsPartyId: "${StringUtil.wrapString(uiLabelMap.DmsPartyId)}",
			DmsPartyLastName: "${StringUtil.wrapString(uiLabelMap.PartyLastName)}",
			DmsPartyMiddleName: "${StringUtil.wrapString(uiLabelMap.PartyMiddleName)}",
        	DmsPartyFullName: "${StringUtil.wrapString(uiLabelMap.PartyFullName)}",
			DmsPartyFirstName: "${StringUtil.wrapString(uiLabelMap.PartyFirstName)}",
			geoIdAlreadyExists: "${StringUtil.wrapString(uiLabelMap.GeoIdAlreadyExists)}",
			provinceAlreadyExists: "${StringUtil.wrapString(uiLabelMap.ProvinceAlreadyExists)}",
			CatalogIdAlreadyExists: "${StringUtil.wrapString(uiLabelMap.CatalogIdAlreadyExists)}",
			ContainSpecialSymbol: "${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}",
			DmsAddMember: "${StringUtil.wrapString(uiLabelMap.DmsAddMember)}",
			
			CommonUpdate: "${StringUtil.wrapString(uiLabelMap.CommonUpdate)}",
			CommonCancel: "${StringUtil.wrapString(uiLabelMap.CommonCancel)}",
			CommonCreate: "${StringUtil.wrapString(uiLabelMap.CommonCreate)}",
			CommonSave: "${StringUtil.wrapString(uiLabelMap.CommonSave)}",
			CommonSubmit: "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
			CommonClose: "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
			BSCodeAlreadyExists: "${StringUtil.wrapString(uiLabelMap.BSCodeAlreadyExists)}",
			
			DmsSequenceId: "${uiLabelMap.DmsSequenceId}",
			DmsAddress1: "${StringUtil.wrapString(uiLabelMap.DmsAddress1)}",
			DmsWard: "${StringUtil.wrapString(uiLabelMap.DmsWard)}",
			DmsCounty: "${StringUtil.wrapString(uiLabelMap.DmsCounty)}",
			DmsProvince: "${StringUtil.wrapString(uiLabelMap.DmsProvince)}",
			DmsCountry: "${StringUtil.wrapString(uiLabelMap.DmsCountry)}",
			ConfirmDeleteAddress: "${StringUtil.wrapString(uiLabelMap.ConfirmDeleteAddress)}",
			ConfirmDelete: "${StringUtil.wrapString(uiLabelMap.ConfirmDelete)}",
			ChooseProvinceToDelete: "${StringUtil.wrapString(uiLabelMap.ChooseProvinceToDelete)}",
			ChooseMemberToDelete: "${StringUtil.wrapString(uiLabelMap.ChooseMemberToDelete)}",
			ConfirmChoiceProductCategory: "${StringUtil.wrapString(uiLabelMap.ConfirmChoiceProductCategory)}",
			DmsWeekly: "${StringUtil.wrapString(uiLabelMap.DmsWeekly)}",
			DmsMonthly: "${StringUtil.wrapString(uiLabelMap.DmsMonthly)}",
			DmsPackageDetails: "${StringUtil.wrapString(uiLabelMap.DmsPackageDetails)}",
			DmsUnitBottle: "${StringUtil.wrapString(uiLabelMap.DmsUnitBottle)}",
			DmsUnitPrice: "${StringUtil.wrapString(uiLabelMap.DmsUnitPrice)}",
			DAOrder: "${StringUtil.wrapString(uiLabelMap.BSOrder)}",
			DmsValueCost: "${StringUtil.wrapString(uiLabelMap.DmsValueCost)}",
			DmsNote: "${StringUtil.wrapString(uiLabelMap.DmsNote)}",
			DmsAgreementBusinesses: "${StringUtil.wrapString(uiLabelMap.DmsAgreementBusinesses)}",
			DmsAgreementSchool: "${StringUtil.wrapString(uiLabelMap.DmsAgreementSchool)}",
			DmsAgreementSubscribers: "${StringUtil.wrapString(uiLabelMap.DmsAgreementSubscribers)}",
			DmsPartnerId: "${StringUtil.wrapString(uiLabelMap.DmsPartnerId)}",
			DmsPartnerName: "${StringUtil.wrapString(uiLabelMap.DmsPartnerName)}",
			DmsPleaseChoicePartner: "${StringUtil.wrapString(uiLabelMap.DmsPleaseChoicePartner)}",
			DmsGroupName: "${StringUtil.wrapString(uiLabelMap.DmsGroupName)}",
			DmsEditAgreement: "${StringUtil.wrapString(uiLabelMap.DmsEditAgreement)}",
			DmsAddress: "${StringUtil.wrapString(uiLabelMap.DmsAddress)}",
			DmsMemberName: "${StringUtil.wrapString(uiLabelMap.DmsMemberName)}",
			DmsMemberType: "${StringUtil.wrapString(uiLabelMap.DmsMemberType)}",
			DmsPartyGender: "${StringUtil.wrapString(uiLabelMap.DmsPartyGender)}",
			DmsPartyBirthDate: "${StringUtil.wrapString(uiLabelMap.DmsPartyBirthDate)}",
			DmsAddNewMember: "${StringUtil.wrapString(uiLabelMap.DmsAddNewMember)}",
			DmsMonths: "${StringUtil.wrapString(uiLabelMap.DmsMonths)}",
			ConfirmDeleteMember: "${StringUtil.wrapString(uiLabelMap.ConfirmDeleteMember)}",
			MobilePhone: "${StringUtil.wrapString(uiLabelMap.MobilePhone)}",
			HomePhone: "${StringUtil.wrapString(uiLabelMap.HomePhone)}",
			OfficePhone: "${StringUtil.wrapString(uiLabelMap.OfficePhone)}",
			DmsPrimaryAddress: "${StringUtil.wrapString(uiLabelMap.DmsPrimaryAddress)}",
			CreateSuccessfully: "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}",
			CreateError: "${StringUtil.wrapString(uiLabelMap.CreateError)}",
			SmallerThan20: "${StringUtil.wrapString(uiLabelMap.SmallerThan20)}",
			DmsNotFindInfor: "${StringUtil.wrapString(uiLabelMap.DmsNotFindInfor)}",
			DmsPeriod: "${StringUtil.wrapString(uiLabelMap.DmsPeriod)}",
			
			DmsMonday: "${StringUtil.wrapString(uiLabelMap.Mon)}",
			DmsTuesday: "${StringUtil.wrapString(uiLabelMap.Tue)}",
			DmsWednesday: "${StringUtil.wrapString(uiLabelMap.Wed)}",
			DmsThursday: "${StringUtil.wrapString(uiLabelMap.Thu)}",
			DmsFriday: "${StringUtil.wrapString(uiLabelMap.Fri)}",
			DmsSaturday: "${StringUtil.wrapString(uiLabelMap.Sat)}",
			DmsSunday: "${StringUtil.wrapString(uiLabelMap.Sun)}",
			DmsAnd: "${StringUtil.wrapString(uiLabelMap.DmsAnd)}",
			DmsDay: "${StringUtil.wrapString(uiLabelMap.DmsDay)}",
			ConfirmAssignData: "${StringUtil.wrapString(uiLabelMap.ConfirmAssignData)}",
			DmsTheNewContractWasCreated: "${StringUtil.wrapString(uiLabelMap.DmsTheNewContractWasCreated)}",
			DmsTheContractWasUpdated: "${StringUtil.wrapString(uiLabelMap.DmsTheContractWasUpdated)}",
			ProductIdAlreadyExists: "${StringUtil.wrapString(uiLabelMap.ProductIdAlreadyExists)}",
			CategoryIdAlreadyExists: "${StringUtil.wrapString(uiLabelMap.CategoryIdAlreadyExists)}",
			DmsWeightNotValid: "${StringUtil.wrapString(uiLabelMap.DmsWeightNotValid)}",
			today: "${StringUtil.wrapString(uiLabelMap.Today?default(""))}",
			clear: "${StringUtil.wrapString(uiLabelMap.ClearString?default(""))}",
			CreateScheduleCallSuccess: "${StringUtil.wrapString(uiLabelMap.CreateScheduleCallSuccess?default(""))}",
			
			BSSlideName: "${StringUtil.wrapString(uiLabelMap.BSSlideName)}",
			BSLink: "${StringUtil.wrapString(uiLabelMap.BSLink)}",
			BSStatus: "${StringUtil.wrapString(uiLabelMap.BSStatus)}",
			DmsDeactivate: "${StringUtil.wrapString(uiLabelMap.DmsDeactivate)}",
			DmsActive: "${StringUtil.wrapString(uiLabelMap.DmsActive)}",
			BSThumbnail: "${StringUtil.wrapString(uiLabelMap.BSThumbnail)}",
			
			DmsThruDate: "${StringUtil.wrapString(uiLabelMap.DmsThruDate)}",
			DmsFromDate: "${StringUtil.wrapString(uiLabelMap.DmsFromDate)}",
			DmsSequence: "${StringUtil.wrapString(uiLabelMap.DmsSequence)}",
			DmsCategoryName: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}",
			DmsCategoryId: "${StringUtil.wrapString(uiLabelMap.DmsCategoryId)}",
			DmsProdCatalogId: "${StringUtil.wrapString(uiLabelMap.DmsProdCatalogId)}",
			BSProductStoreId: "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}",
			DmsStoreName: "${StringUtil.wrapString(uiLabelMap.DmsStoreName)}",
			DmsdefaultCurrencyUomId: "${StringUtil.wrapString(uiLabelMap.DmsdefaultCurrencyUomId)}",
			
			DmsSequenceId: "${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}",
			BSCommentId: "${StringUtil.wrapString(uiLabelMap.BSCommentId)}",
			BSPartyComment: "${StringUtil.wrapString(uiLabelMap.BSPartyComment)}",
			BSTimeComment: "${StringUtil.wrapString(uiLabelMap.BSTimeComment)}",
			BSComment: "${StringUtil.wrapString(uiLabelMap.BSComment)}",
			BSNumberOfReplies: "${StringUtil.wrapString(uiLabelMap.BSNumberOfReplies)}",
			BSReplyId: "${StringUtil.wrapString(uiLabelMap.BSReplyId)}",
			BSPartyReply: "${StringUtil.wrapString(uiLabelMap.BSPartyReply)}",
			BSReply: "${StringUtil.wrapString(uiLabelMap.BSReply)}",
			BSTimeReply: "${StringUtil.wrapString(uiLabelMap.BSTimeReply)}",
			validFieldRequire: "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}",
			BSStatus: "${StringUtil.wrapString(uiLabelMap.BSStatus)}",
			DmsDeactivate: "${StringUtil.wrapString(uiLabelMap.DmsDeactivate)}",
			DmsActive: "${StringUtil.wrapString(uiLabelMap.DmsActive)}",

			BSYearsAgo: "${StringUtil.wrapString(uiLabelMap.BSYearsAgo)}",
			BSMonthsAgo: "${StringUtil.wrapString(uiLabelMap.BSMonthsAgo)}",
			BSWeeksAgo: "${StringUtil.wrapString(uiLabelMap.BSWeeksAgo)}",
			BSDaysAgo: "${StringUtil.wrapString(uiLabelMap.BSDaysAgo)}",
			BSHoursAgo: "${StringUtil.wrapString(uiLabelMap.BSHoursAgo)}",
			BSMinutesAgo: "${StringUtil.wrapString(uiLabelMap.BSMinutesAgo)}",
			BSUpdate: "${StringUtil.wrapString(uiLabelMap.BSUpdate)}",

			DmsCategoryId: "${StringUtil.wrapString(uiLabelMap.DmsCategoryId)}",
			DmsCategoryTypeId: "${StringUtil.wrapString(uiLabelMap.DmsCategoryTypeId)}",
			DmsCategoryName: "${StringUtil.wrapString(uiLabelMap.DmsCategoryName)}",
			DmsDescription: "${StringUtil.wrapString(uiLabelMap.DmsDescription)}",
			BSSelectProduct: "${StringUtil.wrapString(uiLabelMap.BSSelectProduct)}",

			BsConfirmDeleteMenu: "${StringUtil.wrapString(uiLabelMap.BsConfirmDeleteMenu)}",
			BSUpdateMenu: "${StringUtil.wrapString(uiLabelMap.BSUpdateMenu)}",
			BSCreateMenu: "${StringUtil.wrapString(uiLabelMap.BSCreateMenu)}",
			DmsFieldRequired: "${StringUtil.wrapString(uiLabelMap.DmsFieldRequired)}",
			BsChooseMenuFirst: "${StringUtil.wrapString(uiLabelMap.BsChooseMenuFirst)}",
			AddProductCategory: "${StringUtil.wrapString(uiLabelMap.AddProductCategory)}",
			DmsProductId: "${StringUtil.wrapString(uiLabelMap.DmsProductId)}",
			DmsProductName: "${StringUtil.wrapString(uiLabelMap.DmsProductName)}",
			BSReceiveQuantity: "${StringUtil.wrapString(uiLabelMap.BSReceiveQuantity)}",
			BSDeliveryQuantity: "${StringUtil.wrapString(uiLabelMap.BSDeliveryQuantity)}",
			
			BSInTheListSelling: "${StringUtil.wrapString(uiLabelMap.BSInTheListSelling)}",
			BSIsPromosProduct: "${StringUtil.wrapString(uiLabelMap.BSIsPromosProduct)}",
			BSIsNewProduct: "${StringUtil.wrapString(uiLabelMap.BSIsNewProduct)}",
			BEFeaturedProducts: "${StringUtil.wrapString(uiLabelMap.BEFeaturedProducts)}",
			BSSequenceNumber: "${StringUtil.wrapString(uiLabelMap.BSSequenceNumber)}",
			DmsQuantityUomId: "${StringUtil.wrapString(uiLabelMap.DmsQuantityUomId)}",
			BSProductId: "${StringUtil.wrapString(uiLabelMap.BSProductId)}",
			BSQuantity: "${StringUtil.wrapString(uiLabelMap.BSQuantity)}",
			UnitPrice: "${StringUtil.wrapString(uiLabelMap.UnitPrice)}",
			ManufactureDate: "${StringUtil.wrapString(uiLabelMap.ManufactureDate)}",
			ExpireDate: "${StringUtil.wrapString(uiLabelMap.ExpireDate)}",
			InventoryItemType: "${StringUtil.wrapString(uiLabelMap.InventoryItemType)}",
			ManufactureDateMustBeBeforeNow: "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeNow)}",
			ManufactureDateMustBeBeforeExpireDate: "${StringUtil.wrapString(uiLabelMap.ManufactureDateMustBeBeforeExpireDate)}",
			ExpireDateMustBeBeforeManufactureDate: "${StringUtil.wrapString(uiLabelMap.ExpireDateMustBeBeforeManufactureDate)}",
			BEPasswordNotMatch: "${StringUtil.wrapString(uiLabelMap.BEPasswordNotMatch)}",
			BSUserLoginIdAlreadyExists: "${StringUtil.wrapString(uiLabelMap.BSUserLoginIdAlreadyExists)}",
			ConfirmReassignContact: "${StringUtil.wrapString(uiLabelMap.ConfirmReassignContact)}",
			DmsInternalName: "${StringUtil.wrapString(uiLabelMap.DmsInternalName)}",
			BSAgreementCodeAlreadyExists: "${StringUtil.wrapString(uiLabelMap.BSAgreementCodeAlreadyExists)}",
			CommonDepartment: "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}",
			EmployeeId: "${StringUtil.wrapString(uiLabelMap.EmployeeId)}",
			DmsPartyCodeAlreadyExists: "${StringUtil.wrapString(uiLabelMap.DmsPartyCodeAlreadyExists)}",
			CreateNewConfirm: "${StringUtil.wrapString(uiLabelMap.CreateNewConfirm)}",
			UpdateConfirm: "${StringUtil.wrapString(uiLabelMap.UpdateConfirm)}",
        	ConfirmActiveMT: "${StringUtil.wrapString(uiLabelMap.ConfirmActiveMT)}",
        	ConfirmDeactiveMT: "${StringUtil.wrapString(uiLabelMap.ConfirmDeactiveMT)}",
			DANotData: "${StringUtil.wrapString(uiLabelMap.BPONoData)}",
			CommonId: "${StringUtil.wrapString(uiLabelMap.CommonId)}",
			BSCompanyId: "${StringUtil.wrapString(uiLabelMap.BSCompanyId)}",
			BSCompanyName: "${StringUtil.wrapString(uiLabelMap.BSCompanyName)}",
			BSUpdateSaleManRouteAndParty: "${StringUtil.wrapString(uiLabelMap.BSUpdateSaleManRouteAndParty)}",
			BSUpdateSaleManRouteAndPartyMT: "${StringUtil.wrapString(uiLabelMap.BSUpdateSaleManRouteAndPartyMT)}",
        	BSDisagreeAndContinueBT: "${StringUtil.wrapString(uiLabelMap.BSDisagreeAndContinueBT)}",
        	BSOkAndContinueBT: "${StringUtil.wrapString(uiLabelMap.BSOkAndContinueBT)}",
	};
	if (typeof (getLocalization) == "undefined") {
		var getLocalization = function () {
			var localizationobj = {};
			localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
			localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
			localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
			localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
			localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
			localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
			localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
			localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
			localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
			localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
			localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
			localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
			localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
			localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
			localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
			return localizationobj;
		};
	}
	if (typeof (locale) == "undefined") {
		var locale = "vi";
	}
	function fixSelectAll(dataList) {
		var sourceST = {
				localdata: dataList,
				datatype: "array"
		};
		var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
		var uniqueRecords2 = filterBoxAdapter2.records;
		uniqueRecords2.splice(0, 0, "(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})");
		return uniqueRecords2;
	}
	function hasWhiteSpace(s) {
		return /\s/g.test(s);
	}
</script>