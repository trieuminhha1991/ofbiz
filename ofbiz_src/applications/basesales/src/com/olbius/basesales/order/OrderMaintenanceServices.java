package com.olbius.basesales.order;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.http.client.entity.EntityBuilder;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.party.PartyWorker;
import com.olbius.basesales.product.ProductStoreWorker;
import com.olbius.basesales.util.ProcessConditionUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.common.util.EntityMiscUtil;
import com.olbius.security.api.OlbiusSecurity;
import com.olbius.security.util.SecurityUtil;

class Rel {
	public String[] primaryKeyNames;
	public String tableName;
	public String[] foreignKeyNames;

	public Rel(String[] primaryKeyNames, String tableName,
			String[] foreignKeyNames) {
		super();
		this.primaryKeyNames = primaryKeyNames;
		this.tableName = tableName;
		this.foreignKeyNames = foreignKeyNames;
	}

}

class UPDATEINFO {
	public List<String> pkNames;
	public List<String> pkValues;
	public List<String> fieldNames;
	public List<Object> fieldValues;

	public UPDATEINFO(List<String> pkNames, List<String> pkValues,
			List<String> fieldNames, List<Object> fieldValues) {
		super();
		this.pkNames = pkNames;
		this.pkValues = pkValues;
		this.fieldNames = fieldNames;
		this.fieldValues = fieldValues;
	}
}

class TablePrimaryKeyValue {
	public String tableName;
	public List<String> pkNames;
	public List<Object> pkValues;
	public GenericValue gv;
	public boolean cut;

	// public TablePrimaryKeyValue parent;

	public TablePrimaryKeyValue(String tableName, List<String> pkNames,
			List<Object> pkValues) {
		super();
		this.tableName = tableName;
		this.pkNames = pkNames;
		this.pkValues = pkValues;
		cut = false;
	}

	public boolean equal(TablePrimaryKeyValue e) {
		if (!tableName.equals(e.tableName))
			return false;
		for (int i = 0; i < pkNames.size(); i++) {
			if (!pkNames.get(i).equals(e.pkNames.get(i)))
				return false;
			if (!pkValues.get(i).equals(e.pkValues.get(i)))
				return false;
		}

		return true;
	}

	public String toString() {
		String s = tableName + ": ";
		for (int i = 0; i < pkNames.size(); i++) {
			s = s + "[" + pkNames.get(i) + "," + pkValues.get(i) + "] ";
		}
		return s;

	}
}

public class OrderMaintenanceServices {
	public static String module = OrderMaintenanceServices.class.getName();

	public static String MAJUCULES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String MINUCULES = "abcdefghijklmnopqrstuvwxyz";
	public static int PAGE_SZ = 1000;
	public static double t0 = System.currentTimeMillis();
	public static long nbRemovedRecords = 0;
	public static long nbRemovedTables = 0;

	public static String[][] depended = {
			{ "OrderItemShipGroupAssoc", "OrderItemShipGroup" },
			{ "CountryAddressFormat", "Geo" },
			{ "PartyGroup", "Party" },
			{ "OrderAdjustmentBilling", "Invoice" },
			{ "OrderAdjustmentBilling", "InvoiceItem" },
			{ "OrderAdjustmentBilling", "OrderAdjustment" },
			{ "OrderShipment", "OrderHeader" },
			{ "OrderShipment", "OrderItem" }, { "OrderShipment", "Shipment" },
			{ "OrderShipment", "ShipmentItem" },
			{ "OrderShipment", "OrderItemShipGroupAssoc" },
			{ "OrderItem", "OrderHeader" }, { "ShipmentItem", "Shipment" },
			{ "AcctgTransEntry", "AcctgTrans" },
			{ "ShipmentItemBilling", "Shipment" },
			{ "ShipmentItemBilling", "ShipmentItem" },
			{ "ShipmentItemBilling", "Invoice" },
			{ "ShipmentItemBilling", "InvoiceItem" },
			{ "OrderItemBilling", "OrderHeader" },
			{ "OrderItemBilling", "OrderItem" },
			{ "OrderItemBilling", "Invoice" },
			{ "OrderItemBilling", "InvoiceItem" },
			{ "DeliveryItem", "Delivery" },
			{ "ItemIssuanceRole", "ItemIssuance" },
			{ "ItemIssuanceRole", "Party" },
			{ "ItemIssuanceRole", "PartyRole" },
			{ "InventoryItemDetail", "InventoryItem" },
			{"OrderRole","OrderHeader"},
			{"OrderRole","Party"},
			{"OrderRole","PartyRole"},
			{"OrderRole","RoleType"},
			{"TaxAuthority","Geo"},
			{"TaxAuthority","Party"},
			{"OrderItemShipGrpInvRes","OrderItem"},
			{"OrderItemShipGrpInvRes","OrderItemShipGroup"},
			{"OrderItemShipGrpInvRes","OrderItemShipGroupAssoc"},
			{"OrderItemShipGrpInvRes","InventoryItem"},
			{"PartyRole","RoleType"},
			{"InvoiceTaxInfo","Invoice"},
			{"PartyAcctgPreference","Party"},
			{"PartyContactTempData","Party"},
			{"PartyDistributor","Party"},
			{"Person","Party"},
			{"TelecomNumber","ContactMech"},
			{"PostalAddress","ContactMech"},
			{"ProductCalculatedInfo","Product"},
			{"ProductTempData","Product"},
			{"PartySalesman","Party"},
			{"PartyCustomer","Party"},
			
			
			
			
	};
	public static String[] notTables = {
			"CommunicationEventAndOrder",
			"DeliveryEntryAndOrderDelivery",
			"PartyFullNameDetail",
			"AcctgTransEntrySums",
			"PayHistoryAndDetail",
			"PayHistoryAndPosType",
			"ShippingTripPackPackItemSummaryView",
			"ProductCategoryMemberAndPrice",
			"InvoiceAndInvoiceItem",
			"PartyRelationShipWithPartyGroup",
			"EmplPositionAndFulfillment",
			"ProductStoreRoleAndPostalAddress3",
			"InvoiceItemInvoiceSalesTotalPdf",
			"CustRequestAndRole",
			"PartyRoleAndPartyDetail",
			"OrderItemTotalGroup",
			"ShipmentItemBillingAndII",
			"ProductStorePartyView",
			"FacilityProductUpdateTime",
			"PerfCriteriaAssessmentPartyAndRGAndCTP",
			"AcctgTransFactAndGlAccountAndParty",
			"PartyInternalOrgRelDim",
			"AgreementAndAgreementTerm",
			"InvoicePaymentGlFact",
			"InventoryItemDetailSummary",
			"ProductTempDataDetailAndSupplierRef",
			"ProductQuotationAndPriceRCADetail",
			"RouteCustomerAndPartyCustomer",
			"QuotaItemAvailableGroupByProductAlias",
			"ProductAndCatalogTempAndUoms",
			"OrderStatusFact",
			"PackedQtyVsOrderItemQuantity",
			"CustomerReturnItemDetail",
			"InventoryItemAndLocation",
			"ReturnHeaderAndShipmentReceipt",
			"PartyNameAndPhoneWork",
			"EquipmentAndAbilityAllocate",
			"HRPlanningAndEmplPositionType",
			"DeliveryINVOrderItem",
			"AcctgTransEntryView",
			"ShipmentAndInvoiceForReceiving",
			"InvoiceItemAndTaxSum",
			"ProductReviewDetail",
			"DeliveryItemChangeAndII",
			"GoodIdentificationBarcodePrimary",
			"SalesRouteScheduleAvailable",
			"PartyRoleOrder",
			"WebSiteContentCategoryContentDetail",
			"ReturnHeaderAndItem",
			"GlAccountOrgDetail",
			"ReturnItemDetailAndInventory",
			"CostBillAccountingDetail",
			"WorkEffortAssocFromView",
			"CommunicationCampaignFact",
			"InvoiceItemAndAssocProduct",
			"PartySalesmanAndPerson",
			"ReturnHeaderItemAndBilling",
			"LevelRelationship",
			"EquipmentAllocItemStoreAndDetail",
			"RecruitmentSalesEmplAndPerson",
			"PartyGroupGeoView",
			"ReturnReasonDimension",
			"PaymentAndType",
			"CustomerFullAndDeliveryCluster",
			"PerfCriteriaAndTypeAndPosType",
			"PosTerminalStateHasChanged",
			"DeliveryItemView",
			"EnumerationDimension",
			"RepresentativeParty",
			"ProductPromoActionProduct",
			"TransferItemAndInventoryExportGroup",
			"AcctgTransDetail",
			"TrainingCourseAndTotalPartyAtt",
			"StockEventItemGroupByDate",
			"PersonFactoryFact",
			"PicklistAndBinAndItem",
			"ShipmentAndType",
			"SaleOrderItem",
			"RequirementCustRequestView",
			"ProductInventoryItem",
			"ProductFeatureAndAppl",
			"MTCustomerAndPartyToAndRepresentative",
			"PayHistoryDetail",
			"ShipmentAndInvoiceForReceivingAndPartyGroup",
			"PartyPerfAndCriteriaAndResultAndParty",
			"PartyAndTelecomNumberOrderTemp",
			"PartyCampaignRelationshipCondition",
			"InvoiceItemInvoicePdf",
			"FacilityAndPostalAddressAndTelecomNumber",
			"ReturnDirectlyPos",
			"ProductStoreShipmentMethView",
			"MTPartyRelPartyCusAndProdInvent",
			"TrainingCoursePartyAttAndPtyRel",
			"OrderItemBillingAndInvoiceAndItem",
			"CustomerHasInventoryDetail",
			"PartyGroupRelationship",
			"InventoryItemVarianceGroupByExpireAndManufacturedDate",
			"ProductPromoStoreAppl",
			"PartyAndPostalAddress",
			"DeliveryItemRequirementView",
			"PartyNameWithContactPurpose",
			"ProductAndCategoryPrimaryAndGoodIdsSKU",
			"WorkOvertimeRegistrationAndPtyRel",
			"OrderHeaderAndRoleAndDate",
			"StockEventFact",
			"OrderItemGroupProduct",
			"DeliveryClusterCustomerView",
			"EmplLeaveAndEmplPosTypeGroupBy",
			"OldOrderShipmentPreference",
			"PartyRoleDetailAndPartyDetail",
			"TrackingCodeOrderAndOrderHeader",
			"InvoiceItemAndProductGroupBy",
			"MarketingRoleAndPerson",
			"SupplierAndCurrencyDetail",
			"OrderHeaderAndShipGroups",
			"SupplierProductGroupDetailAndAgreement",
			"GeoAssocAndGeoTo",
			"ProductCategoryMemberAndProductBrand",
			"VoucherInvoiceAndTotal",
			"RecruitmentRoundCandidateDetail",
			"OrderItemMinItemSeq",
			"SurveyResponseAndAnswer",
			"CommunicationEventAndCustRequest",
			"PartyRoleNameDetail",
			"CountryTeleCodeAndName",
			"PartyCurrencyDetail",
			"InvoiceContentAndInfo",
			"PartyInsuranceSalaryAndParty",
			"OrderRolePartyNameView",
			"PartyRivals",
			"TaxAuthRateProdDimension",
			"PartyPersonAndInfo",
			"FacilityLocationAndGeoPoint",
			"DeliveryAndShipmentDetail",
			"CustomTimePeriodRelationship",
			"WSEmployeeAndWS",
			"TotalOrderQuantitySum",
			"ProductInventoryCustomerFact",
			"ProductIdAndGoodIdSKUAgg",
			"OrderHeaderAndPaymentPref",
			"PromoSettleResultTotalPaid",
			"ProductSimpleAverageCostAvailable",
			"ProductCategoryAndProductMember",
			"AgreementAndPartyFrom",
			"FacilityDimension",
			"OrderItemMinSeq",
			"CustRequestItemNoteView",
			"ShippingTripAndPackAndOrder",
			"KeyPerfIndicatorAndPositionType",
			"MarketingPlaceDetail",
			"ProductBrand",
			"GeoDimension",
			"WorkingLateRegisterAndPos",
			"DateDimension",
			"OrderShipmentReceipt",
			"InvoiceTypeDimension",
			"SalesBonusSummaryAndStatementAmount",
			"GlAccountTypeDefaultAndDetail",
			"PartyGroupAndParty",
			"OrderItemAssocAndOrderItemFromTo",
			"RequirementPartyProductCount",
			"ProductPromoExtRegistrationEvalDetail",
			"InvItemAndOrdItem",
			"PayrollTableRecordStatusAndUserLogin",
			"PartyGroupAndInfo",
			"FacilityAndContactMech",
			"RouteCustomerAndPartyCustomerDetail",
			"ProductFacilityHistoryInternalSumProduct",
			"InvoiceItemDiscountPdf",
			"ProductQuotationRulesCateFOL",
			"ShipmentAndInvoice",
			"AgreementDetail",
			"DeliveryEntryAndDeliverer",
			"ProductContentAndInfo",
			"GlAccountOrganizationAndClass",
			"AllocCostPeriodItemPartyGroupBy",
			"PartyRelationShipAndPerson",
			"FormulaProductDetail",
			"PosWorkShiftAndOrg",
			"TimekeepingSummaryDetailAndDetail",
			"OrderHeaderDetail",
			"ProductPromoExtRegisterDetail",
			"CommunicationEventAndSubscr",
			"ProductQuotationAndRulesAndStoreGroup",
			"RoleTypeGroupMemberDetail",
			"FormulaParameterProductStoreDetail",
			"SalesStatementItemDetail",
			"CustomerReturnTotalDetail",
			"AcctgWorkShiftFact",
			"OrderItemShipGrpInvResAndItemLocation",
			"ProductQuotationStoreGroupApplActive",
			"SupplierToSearch",
			"ContentApprovalProductContentAndInfo",
			"AcctgTransEntryCD",
			"FacilityPartyAndPerson",
			"OrderHeaderFullView",
			"PartyAndUserLogin",
			"CustRequestInfoAndWorkEffortAndPartyRel",
			"FixedAssetDecrementView",
			"ProductFeatureGroupAndAppl",
			"RecruitmentRequireCondAndType",
			"DeliveryNotCancel",
			"DeliveryItemTotalOrder",
			"RecruitmentCostItemAndCatType",
			"PartyRelationShipWithPerson",
			"FacilityGeo",
			"OrderItemAndProduct",
			"RecruitmentAnticipateAndItem",
			"PackingListDetailSum",
			"QuotaItemAndProduct",
			"RoleTypeAndParty",
			"ProductPromoCodeCountUse",
			"ContactListPartyAndStatus",
			"PartyPerfCriteriaSummary",
			"ProductPromoUseCheck",
			"InventoryItemTotalByProductFull",
			"ProductPromoDimension",
			"PartyCustomerDetailInfoView",
			"RecruitmentPlanAndBoardAndPerson",
			"InvoiceDiscountAndPromoView",
			"AllEmplPositionAndFulfillment",
			"QuoteWorkEffortView",
			"EnumTypeChildAndEnum",
			"CustomTimePeriodAndParent",
			"OrderHeaderItemAndInv",
			"ContentAssocDataResourceViewFrom",
			"SuppProAndProdConfAndParty",
			"FacilityPartyAndRoleType",
			"DistributorPerfCriteriaResultGroup",
			"CustomTimePeriodDimensionView",
			"ProductEventItemAndProductAvailable",
			"AgreementProductApplView",
			"OrderHeaderNoteView",
			"WorkShiftCost",
			"AcctgTransFactAndGlAccountAndParty2",
			"ProductInventoryNearDateTotalCount",
			"ReceiveInventoryItemDetailAndInventory",
			"InvoiceItemAndProduct",
			"PartyRelationshipSalesmanSup",
			"CustRequestAndCommEvent",
			"PartySalesmanAndSupNameDetail",
			"PortalPagePortletView",
			"PartyContactGeoDetailByPurpose",
			"InventoryItemProductTotal",
			"UomAndGroup",
			"ProductStoreFacilityAndStore",
			"PosHistoryProductStore",
			"AllocCostPeriodItemAndParty",
			"ProductVirtualAndAssocPrices",
			"ReturnSalesOrderNewFact",
			"ProductPlanHeaderAndParty",
			"SalesForecastFact",
			"SupplierProductAndQuotaAndTax",
			"ProductConfigAndProduct",
			"OlbiusPartyPermissionDetail",
			"ProductCategoryAndTax",
			"InsuranceDelarationDimension",
			"PayrollEmplParamAndPtyRel",
			"RateAmountAndRelations",
			"PartyInsuranceProfileFact",
			"EquipmentAndTypeAndAbilityAllocate",
			"RecruitmentSalesEmplFullDetail",
			"PartyPrimaryEmailAndContactMech",
			"PartyMTCustomerDetailView",
			"WorkEffortAssocView",
			"InvoiceTaxFact",
			"RecruitmentCandidateAndPerson",
			"PhysicalInventoryFact",
			"FacilityAndFacilityParty",
			"PartyContentDetail",
			"ProductPlanAndCustomTime",
			"PriceAndGoodIdentification",
			"ShippingTripPackOrderDeliveryPackSummaryView",
			"DeliveryItemGroupByOrderItem",
			"OldAgreementWorkEffortAppl",
			"WorkEffortProductGoods",
			"ProductPromoExtStoreAppl",
			"ProductAndGoodIds",
			"ContentImageView",
			"OrderHeaderItemAndRoles",
			"ProductQuotationDetail",
			"PartyPersonRelationship",
			"EntitySyncInclGrpDetailView",
			"EquipmentTypeAndTotalPrice",
			"CommunicationEventAndProduct",
			"AcctgTransAndEntryDistinct",
			"ShipmentAndItem",
			"PartyAndPartyGroupDetail",
			"ProductPromoSettlementResultDetail",
			"PurchaseAgreementDetail",
			"SalesStatementAndCustomTimePeriod",
			"PartyNameAndPrimaryAddressAndMobileAndEmailAndUserLoginAndRole",
			"LoginHistoryPOS",
			"OrderRoleAndProductContentInfo",
			"InventoryItemTotalDetail",
			"ProductPromoCodePartyDetail",
			"InvoiceItemAndSalesTax",
			"OrderPaymentPrefAndPayment",
			"EquipmentAllocateAndItemDetail",
			"OldEmplPositionTypeRate",
			"PartyPerfCriteriaItemProductAndProduct",
			"UomDimension",
			"PartyFixedAssetAssignmentAndParty",
			"DeliveryEntryDetail",
			"SalesOrderExportData",
			"OrderItemQuantityReportGroupByItem",
			"ReturnHistoryLog",
			"PermanentAndCurrentlyAddress",
			"FixedAssetAndDetail",
			"CustRequestAndWorkEffort",
			"OrderReportPurchasesGroupByProduct",
			"PartyExport",
			"ReturnItemProduct",
			"InventoryItemCountGroupByExpireAndManufacturedDate",
			"PartyRelationshipAndPartyToPromosExt",
			"CustomerAndStore",
			"PicklistItemAndInventoryItem",
			"PurchaseOrderFact",
			"InventoryItemGroupByProductOwnerFacilityStatus",
			"PartyNameAndGeneralAddress",
			"InventoryItemAndLabelDetail",
			"ProductStoreFacilityByOrder",
			"WorkShiftInCome",
			"InventoryItemGroupExpireManufacturedLot",
			"ProductInventoryExpOfCustomerDetail",
			"ContentAssocAndContentPurpose",
			"InventoryItemCountDetail",
			"AcctgCustomTimePeriodSumFact",
			"ExportInventoryItemDetailAndInventory",
			"EquipmentDecreaseItemSumByDate",
			"PromoExtRegisterNeedSettlement",
			"RouteScheduleAgg",
			"PartyRegion",
			"InvoiceItemTaxView",
			"ImportExportEntityDataOlap",
			"ProductAndUomAndCatePriAndGoodIdsSKU",
			"MobileDeviceAndPartySalesman",
			"ProductFacilityAndPostalAddress",
			"ProductStoreCatalogActive",
			"PartyRelationshipFromPartyOlbius",
			"InventoryItemGroupByProductAndFacility",
			"FactPayroll",
			"ShipmentItemIssuanceDetail",
			"InventoryItemAndLabelTotal",
			"EmplPositionFact",
			"GlAccountBalanceFactView",
			"DeliveryItemGroupOrderAndInventory",
			"PartyHealthInsuranceAndHospitalPerson",
			"OrderProductDetail",
			"PruchaseOrderHeaderFullView",
			"ProductAndGoodIdsAndCatalog",
			"PicklistBinAndRoleAndDate",
			"PartyPerfCriteriaAndCriteria",
			"ProductStoreAndManagerRole",
			"EquipmentAllocItemPartyAndDetail",
			"FullEmployeeInformation",
			"EmploymentThruDate",
			"OrderShipmentDetail",
			"WebSiteContentAndDataresourceAndContent",
			"GlAccOrgAndAcctgTransAndEntry",
			"AgreementAndOrderDetail",
			"PicklistBinStatusDate",
			"RecruitAnticipateAndPartyAndPosType",
			"TransferHeaderDetail",
			"InvoiceItemDiscountAndPromoView",
			"MrpEventView",
			"PartyCustomerAndRoute",
			"RecruitmentCandidateInterviewAndPerson",
			"EmplPositionTypeDimension",
			"RequirementItemIssuance",
			"OrderHeaderAndPackDeliveryClusterCustomerView",
			"VisitAndFacilityAndParty",
			"PicklistItemSum4",
			"PicklistItemSum3",
			"PicklistItemSum2",
			"InventoryItemAndDetailSumFacility",
			"ProductCategoryMemberAndRole",
			"ContactMechEmail",
			"EquipmentDecreaseItemAndDecrTotal",
			"UomAndType",
			"OldOrderItemAssociation",
			"CustomerRelationship",
			"StockEventAggregated",
			"ProductOrderItemAndOrderHeader",
			"SupplierProductAndOrderItem",
			"OrderItemMinDetail",
			"InvoiceItemTaxAuthRateProduct",
			"PostalAddressAndGeo",
			"OldOrderItemInventoryRes",
			"ReturnSupplierNotCount",
			"ProductCategorySupplier",
			"GlAccountHistoryFact",
			"FinAccountAndRole",
			"CommunicationEventSum",
			"CheckInHistoryDetails",
			"QuoteNoteView",
			"PosWorkShiftReturnDirectly",
			"EmplTerminationProposalAndPerson",
			"CommEventContentDataResource",
			"NtfNtfGroup",
			"OrderAndClusterCompleted",
			"InvoiceAndRole",
			"ContactMechInFacility",
			"PartyToDistributorActive",
			"PartyNameAndPrimaryAddress",
			"ProductAndTax",
			"FixedAssetAndGeoPoint",
			"ProductAndPriceAndGoodIdentificationAndFacility",
			"KeyPerfIndPartyTargetAndItemAndPty",
			"DistBonusSummaryPartyAndSalesState",
			"FinAccountTransSum",
			"PartyNameVisitView",
			"SimpleOrderDetail2",
			"PaymentAndApplication",
			"InventoryItemGroupByDateAndLabelDetail",
			"PicklistDetail",
			"PaymentMethodAndFinAccount",
			"KHTTCheckReturnPromo",
			"PartyPerfCriteriaSalesAndItemProduct",
			"SupplierProductAndProductFind",
			"ProductPromoReturnItem",
			"ProductCategoryContentDetail",
			"PartyContactAddressAndGeoPoint",
			"EmplPositionTypeAndInsuranceSalary",
			"PartyDetail",
			"EmplLeaveAndPartyRelPosGroupBy",
			"PaymentMethodAndEftAccount",
			"DeliveryItemTransferGroupView",
			"SupplierProductDistinctAndGroupByProduct",
			"ProductCategoryRollupAndRole",
			"InventoryItemDetailFacilityDistributter",
			"PartyOutletOrdered",
			"ProductCategoryMemberDetail",
			"ProductStoreFacilityDetail",
			"FixedAssetCustomTimePeriodView",
			"ProductStoreBarcodeDetail",
			"ProductInventoryTotalCountGroupByProduct",
			"AcctgTransAndEntriesDetail",
			"InsPayDeclAndPartyAndLeave",
			"PartyNameContactMechView",
			"ProductEventItemAndProduct",
			"OrderInvoiceNoteAndOrder",
			"PartyContactMechPurposePrimary",
			"ProductPromoApplStoreDisplay",
			"AgreementAndAgreementAttribute",
			"ProductQuotationStoreApplStrAgg",
			"ShoppingCartLogDetail",
			"ProductAndCategoryPrimaryAndPriceAndUom",
			"ProductCategoryAndMember",
			"PromosEventRole",
			"PartyPerfCriItemProductAndResult",
			"VoucherTotalAndInv",
			"WorkEffortContactMechView",
			"ProductQuotationProductStoreApplPriceRCADetail",
			"AssocRevisionItemView",
			"ProductStoreCatalogDetail",
			"DeliveryEntryAndCarrier",
			"AllocCostPeriodItemGlAccGroupBy",
			"ProductAverageCostFact",
			"CustomerAndDeliveryClusterFullDetails",
			"ProductQuotationRulesCondAndTax",
			"PartyContactDetailByPurpose",
			"TimekeepingDetailPtyAndPtyRelGroupBy",
			"PromosEvents",
			"QuotaItemAvailable",
			"PayHistoryMaxDateAndDetail",
			"FacilityAndProductStore",
			"PersonAndPrimayEmail",
			"OrderItemQuantityReturned",
			"BillingAccountRoleAndAddress",
			"EmplTimekeepingSignDimension",
			"PayrollParamsCharacteristicDimension",
			"ShipmentCostEstimateDetail",
			"OrderAndCustomer",
			"PayrollTableRecordAndSum",
			"TransferItemAndProduct",
			"ReturnItemDetail",
			"InventoryItemTotal",
			"ProductStoreRoleDetail",
			"ContactMechInFacilityTypePurpose",
			"OrderAndContainerDetail",
			"ProductPromoExtRegisterPartyOnly",
			"CheckInHistoryDetail",
			"PartyContactMechPurposeAndPostalAddress",
			"PartyAndRoleFullNameSimple",
			"PayrollFormulaDimension",
			"InvoiceItemCategorySummary",
			"PosWorkShiftOrder",
			"PaymentAndTypeAndCreditCard",
			"ContentAssocOptViewFrom",
			"ProductPromoExtApplStore",
			"VoucherInvoiceAndVoucher",
			"OrderPurchaseProductSummary",
			"ProductFacilityFact",
			"OrderContacMechPostalAddress",
			"ProductFacilityLocationView",
			"GlAccountAndHistoryTotals",
			"VoucherAndInvDetail",
			"PartyResourceBirthDateFact",
			"ProductSalesPriceLogAndUoms",
			"FacilityPartyFacility",
			"PartyResourceCommunicationArea",
			"PurchaseOrderItem",
			"InventoryItemGroupByExpireAndManufacturedDate",
			"RecruitmentPlanRoundAndInterviewer",
			"PartyAndUserPOS",
			"KeyPerfIndicatorAndParty",
			"TemporalExpressionChild",
			"ContainerAndGeoPoint",
			"OrderHeaderAndRoleSummary",
			"GlAccountAndHistory",
			"FixedAssetDecreaseItemSumByDate",
			"PayrollFact",
			"PerfCriteriaAssessmentAndPG",
			"ProductFacilityLocationQuantityTest",
			"OrderHeaderReturnable",
			"RecruitmentRequiredDetail",
			"FacilityPartyAndRole",
			"GlAccountTypeDefaultDetail",
			"InventoryProductCostFact",
			"GroupProductInventory",
			"PartySupplierDistinct",
			"PartyGroupAndContactMechDetail",
			"WorkEffortContentAndInfo",
			"RequirementAndRole",
			"OrderItemBillingAndInvoice",
			"WorkEffortAndSalesOpportunity",
			"RouteCustomerFullDetail",
			"DeliveryInvTransferItem",
			"SupplierAndCurrency",
			"SalesOrderRollStoreAndProductFact",
			"InsParticipateReportAndMonth",
			"ProductStoreRoleAndRelSalesman",
			"EmplPositionAndType",
			"DeliveryProductStoreGroup",
			"ProductInventoryTotalCount",
			"RecruitmentPlanCostItemAndType",
			"SalesForecastAndPeriod",
			"OrderHeaderAndAssoc",
			"ProductPriceDefaultPrice",
			"SalesBonusPolicyRuleAndEnum",
			"StockEventAggregated2",
			"OlbiusPartyPermissionGroup",
			"InvoiceItemAndOrderAndType",
			"OrderHeaderAndRoleAndPerson",
			"InventoryItemQuantityWarning",
			"PicklistBinAndRole",
			"InventoryItemGroupByExpireAndManufacturedDateAll",
			"AcctgTransAndEntriesAndLink",
			"ReturnAndReturnAdjustment",
			"ProductDetailSearch",
			"UserLoginDetail",
			"PayrollTableAndFormulaAndChar",
			"EmplPositionTypeRateAndAmount",
			"RecruitmentPlanSalesAndEmpl",
			"PartyNameAndContactMechAndContacMechPurpose",
			"ProductStoreRoleAndParty",
			"SupplierProductFilterByDate",
			"InvoiceAndPaymentApplDetail",
			"OrderShipmentInfoSummary",
			"PartyNameAndPrimaryAddressAndMobileAndEmailAndUserLogin",
			"InvoiceTermAndType",
			"ContainerAndBillLading",
			"InventoryItemTotalNegative",
			"PartyGrpCTMTelecom",
			"InsuranceContentTypeDimension",
			"InventoryItemTotalByProduct",
			"AbsentFact",
			"InvoiceAndType",
			"DescriptionDimension",
			"PartyPurposeAndTelecomNumberOrder",
			"ProductInventoryCustomerRole",
			"PickingItemTempDataGrouped",
			"ProductAndCatalogTempDataMore",
			"InventoryItemStatusForCount",
			"OrderPurchaseCountTotal",
			"TrackingCodeAndVisit",
			"Tenant",
			"PartyCustomerDetailSimple",
			"PartyNameAndPrimaryPhone",
			"ProductPromoApplStore",
			"ProductPromoActionCategory",
			"ProductStoreWifiDetail",
			"SalesForecastDimension",
			"MarketingCampaignAndOrderHeader",
			"SalesExecutiveRoleOrder",
			"InvItemAndType",
			"ShipmentFact",
			"TrainingCourseAndParty",
			"CustRequestAndContent",
			"InventoryItemDetailFreezedSum",
			"PaymentMethodTypeNotGlAccount",
			"PartyRelationshipRouteDetail",
			"OrderItemQuantityReportGroupByProduct",
			"EquipmentAndDecrTotalAndRemain",
			"FixedAssetAndPartyInfo",
			"VoucherTaxPaymentReport",
			"PicklistItemLocationCode",
			"PartyRelationshipCustomerDistributor",
			"RecurrenceInfoAndRule",
			"TemporaryPartyDetail",
			"ProductInventoryNearDate",
			"InsuranceEmplAndDetail",
			"RecruitmentPlanRoundAndSubjectParty",
			"WorkingLateRegisterAndPtyRel",
			"LoyaltyPointAndItems",
			"OrderHeaderQuantityReturnable",
			"PersonAndPartyGroupSimple",
			"CategoryRelationship",
			"PartyAndContactMech",
			"FacilityAndContactMechAndPostalAddress",
			"ProductPriceAndTax",
			"UserLoginAndSecurityGroup",
			"RecruitmentPlanDimension",
			"FacilityCTMAndCTM",
			"WorkEffortPartyAssignByRole",
			"AcctgTransFactRecip",
			"CheckInHistoryPartyCustomer",
			"DeliveryItemTransferView",
			"TransferItemFact",
			"FixedAssetDecreaseAndItem",
			"PartyCustomerAddressGeoPoint",
			"MarketingPlanContentDetail",
			"PartyResourceArea",
			"PayrollEmplParamAndPtyGroupBy",
			"PartyAndGroup",
			"EmplLeaveAndReasonType",
			"UserLoginAndProtectedView",
			"GlOrganizationClassAndParent",
			"KeyPerfIndPartyTargetItemAndKPI",
			"AgreementItemAndProductAppl",
			"SkillTypeAndParent",
			"OrderItemIssuanceShipmentDelivery",
			"TopicDetail",
			"PosTerminalLogAndReturnDirectlyPos",
			"InvoiceItemAndSaleTaxGroupBy",
			"EquipmentAllocateAndItem",
			"DeliveryItemGroupTransferAndInventory",
			"PartyCustomerFullDetail",
			"ProductPromoExtRegisterPartyRule",
			"PartyRelationshipDistributorTotalGroup",
			"ProductPromoExtActionCategory",
			"ProductAverageCostFilterDate",
			"DeliveryItemTransferGroupBound",
			"RouteScheduleDetailDateAndSalesmanAndCust",
			"UserLoginAndPartyDetails",
			"TimekeepingSummaryPtyAndPtyRelGroupBy",
			"InvoiceItemAndTypeAndProduct",
			"PartyAndTelecomNumberOrder",
			"ReturnSupplierTotalSum",
			"PartyContactMechPurposePrimaryDetail",
			"ProductRelationshipDetail",
			"OpponentEventAndPartyGroup",
			"PhysicalInventoryAndVariance",
			"EquipmentDecreaseAndItemTotal",
			"AcctgTransEntrySumsByAccount",
			"WorkEffortFindView",
			"ProductReviewSum",
			"OrderAndCustomerSynthetic",
			"FixedAssetIncreaseAndItemAndFA",
			"PaymentApplicationInvoice",
			"OrderAdjustmentGroupTax",
			"PartyGroupAndPartyRole",
			"PayrollParametersAndType",
			"InventoryItemGroupByDate",
			"FixedAssetDecreaseItemAndFA",
			"InventoryItemVarianceDetail",
			"PartyDistributorAddressGeoPoint",
			"EmplPositionSummary",
			"PartyAndPerson",
			"MarketingCampaignAndVisit",
			"InventoryItemLocationSum",
			"EquipmentAndProductStore",
			"PostalAddressAndGeoPoint",
			"AcctgTransSumFact",
			"PartyAndGeoPoint",
			"InvoiceItemTaxAuthProduct",
			"RouteHistoryFact",
			"EmplLeaveDimension",
			"AttendanceTrackerFact",
			"SupplierPromoApplFilter",
			"OrderItemAndProductTax",
			"WorkEffortCustRequestItemView",
			"OrderHeaderAndOrderRolePO",
			"ReturnInvoicePaymentDetail",
			"EmplLeaveRegulationAndPosition",
			"OrderHeaderAndItemsDetail",
			"PartySupplierAndContactMech",
			"FAPartyGlAccountView",
			"AcctgTransHistoryAndParty",
			"ProductAndProductCategoryMember",
			"WorkingShiftDayWeekAndType",
			"GlAccountHistoryDetail",
			"PartyEmployeeDetail",
			"SalesmanAndSalesSup",
			"ProductTempDataDetailAndSupplier",
			"InvoiceAndTypeAndRole",
			"TerminalFacilityStore",
			"InvoiceItemTaxAuthGroupProduct",
			"PayrollTableRecordPartyAndRel",
			"DistBonusSummaryPartyAndAmount",
			"GeoAssocAndGeoFrom",
			"PartyCustomerAndDetail",
			"PayrollTableRecordAndCustomTimeAndParty",
			"PayrollEmplAndParametersPG",
			"TaxAuthorityGlAccountAndGeo",
			"GlAccountBalancePartyView",
			"EmplLeaveRegulationAndPtyRel",
			"DeliveryItemAndOrderInvoiceProduct",
			"PayrollTableRecordPartyAndTotalReceipt",
			"AgreementAndPartyNameView",
			"InventoryAndItemProduct",
			"PayrollTableRecordPartyAmountAndSal",
			"CustomerReturnTotalCountByReceiveDate",
			"VoucherTaxReport",
			"PartyPerfCriteriaAndResult",
			"InvoiceItemProductWithTaxView",
			"UomConversionDatedView",
			"ContentDataResourceView",
			"PartyRelationshipDepartment",
			"OrderAndCustomerMostRecent",
			"TrainingCoursePartyAttAndPos",
			"VarianceReasonGlAccountDetail",
			"EnumerationRelProductDetail",
			"PartyRelationshipToFrom",
			"ShipmentTransferItemIssuanceDetail",
			"PartyPayrollFormulaInvoiceItemTypeDetail",
			"ReturnHeaderAndReturnItemBilling",
			"WebSiteContentDetail",
			"EmplPositionFulfillmentAndReportingStruct",
			"FixedAssetDepreciationCalcAndAmount",
			"PayrollEmplParamatersParty",
			"OrderHeaderAndRoleTypeAndReturn",
			"PicklistBinRoleActive",
			"TrainingCourseStatusAndParty",
			"WorkEffortPartyAssignView",
			"OrderAndSupplier",
			"CommunicationEventDetail",
			"ProductStoreRoleAndPartyDetail",
			"SupplierProductMainActive",
			"ShipmentItemBillingAndInvoice",
			"FixedAssetDepreCalcItemAndFA",
			"OrderHeaderAndRoleType",
			"InventoryItemProductTotalFacility",
			"ReturnHeaderDetail",
			"PackDeliveryClusterCustomerAndOrderHeaderView",
			"RequirementItemIssuanceGroupByExpMnfLot",
			"VoucherAndInvTotal",
			"InvoiceTypeRelationship",
			"PerfCriteriaAndType",
			"OrderAndShipAndContactAndRole",
			"ProductAndPriceTotal",
			"ProductPromoExtCondCategory",
			"OrderAndShippingLocation",
			"PartyNameViewAndRole",
			"ProdFeaGrpAppAndProdFeaApp",
			"ProductStoreGroupAndMember",
			"PayHistoryAndMaxDate",
			"PartyAndRelAndEmplPosTypGroup",
			"PartyContactWithPurpose",
			"SalesForecastAndCustomTimePeriod",
			"OldFacilityRole",
			"LoyaltyUseCheck",
			"InventoryOfCustomerDetail",
			"AcctgTransEntrySumsByParties",
			"RouteCustomerAndCustomerAndScheduleAvail",
			"PartyAndSupplierRelationship",
			"PartyNameAndPrimaryAddressAndMobileAndEmail",
			"PaymentApplicationTotal",
			"OrderHeaderAndDeliveryClusterCustomerView",
			"WorkEffortAndInventoryAssign",
			"TranferHeaderCountTotal",
			"CommunicationEventAggreateResult",
			"InventoryItemDetailForSum",
			"ProductStoreFacilityTerminal",
			"DeliveryItemTransferAndExportCost",
			"ReturnItemAndProduct",
			"EquipmentDecreaseAndItem",
			"InsEmplAdjustParticipateAndMaxDate",
			"OrderSumTotalRow",
			"InventoryItemGroupByProductOwnerFacilityStatusExpireDate",
			"GoodIdentificationAndProduct",
			"ProductAndProductFacility",
			"PackItemAndProductIdView",
			"WorkingLateRegisterAndDetail",
			"PartyResourceAreaMember",
			"ProductIdAndGoodIdSKUPrimary",
			"InvoiceItemAndVATCategory",
			"ProductAndPriceView",
			"InventoryItemQOHGreateZEROGroupByDate",
			"InventoryItemGroupByDateNotDateReciveDetail",
			"FacilityExportExpectation",
			"OlbiusPartyPermissionAndGroup",
			"EnumerationAndEnumerationType",
			"AcctgTransEntrySumsByPartiesLimitDueDate",
			"ConfigLabelDetail",
			"DeliveryItemSumTransfer",
			"DeliveryItemAndProductIdView",
			"ProductIdAndGoodIdSKU",
			"AcctgTransFact",
			"FinAccountAndGeo",
			"InsuranceEmplDetailAndMaxDate",
			"ShipmentPackageRouteDetail",
			"ShipmentItemAcctgTrans",
			"ProductTaxAndCategoryTax",
			"GlAccountDimension",
			"PartyRelAndCK",
			"EmplLeaveReasonTypeAndSign",
			"RecruitmentPlanRoundAndSubject",
			"OrderHeaderAndShipGroupsByProduct",
			"PicklistItemSum",
			"InvoiceItemTaxSumPromoSumWithProduct",
			"DeliveryItemOrderInvoice",
			"InvoiceVoucherFact",
			"TransferItemShipGrpInvResDetail",
			"PostalAddressFullNameDetail",
			"InvoicePartyFromDetail",
			"SalesChannelDimension",
			"PartyTypeEnumAssocDetail",
			"InsAllowBenefitTypeDim",
			"DataOlapImportExportEntity",
			"PartyContactMechPurposeActive",
			"ProductQuotationStoreApplDetailQuotation",
			"PaymentApplicationAndInvoice",
			"CommunicationEventLastestResult",
			"InventoryItemAndLabelAgg",
			"RouteCustomerAvailable",
			"TrainingCourseDetail",
			"ProductStoreRoleAndPartyCodeDetail",
			"PayrollCharacteristicDimension",
			"OrderDetail",
			"PostalAddressDetail",
			"ProductPromoApplSupplier",
			"PartyRelationShipAndGroup",
			"SupplierProductGroupAndProduct",
			"PurchaseOnOrder",
			"PartyRelationshipAndContactMechDetail",
			"PartyResourceCommunicationMember",
			"WorkOvertimeRegistrationAndDetail",
			"GlAccountHistoryFactView",
			"SalesOpportunityAndRole",
			"DeliveryItemSumOrder",
			"PartyInsuranceFact",
			"FixedAssetIncreaseItemAndFA",
			"PackingListHeaderDetail",
			"ShipmentTypeEnumDetail",
			"ProductAverageCostFilterByDate",
			"EquipmentAndTotalPrice",
			"EntitySyncIncludeGroupSync",
			"SimpleOrderDetail",
			"WorkEffortQuoteView",
			"TimekeepingDetailPtyAndPtyRel",
			"DeliveryEntryAndDriver",
			"ReturnItemAndBilling",
			"PartyToAndPartyNameAndTelephoneDetail",
			"ProductIdAndGoodIdSKUAggUom",
			"OutletAddressGeo",
			"ProductAndUomAndGoodIds",
			"PosWorkShiftReturnOrder",
			"PartyAndContactMechAndState",
			"VoucherAndInvGroupBy",
			"PartyNameAndShippingAddress",
			"ShipmentTypeDimension",
			"ProductPromoExtRegisterDetailSUP",
			"PartyRelAndListCustomer",
			"PartyPerfAndCriteriaAndResult",
			"OrderDeliveryPackItemSummaryView",
			"CustomTimePeriodDimension",
			"ProductSummaryAndSupplierProduct",
			"CustomerMtlAndContactMech",
			"AgreementAndRole",
			"PosTerminalLogAndPosTerminal",
			"TrackingCodeOrderReturnAndReturnHeader",
			"OrderHeaderItemAndShipGroup",
			"PartyGroupFromGeoView",
			"CommunicationEventAggreateResultParty",
			"DeliveryItemAndChange",
			"InvoiceItemGroupBy",
			"RouteCustomerView",
			"GoodIdentificationMeasureAndProduct",
			"PartyToAndPartyNameDetail",
			"ConfigLabelAvailable",
			"AcctgTransAndEntry",
			"ProductPromoExtAndProductPromoExtRuleAndAppStore",
			"ProductAndTaxAuthorityRate",
			"WorkEffortAndPartyAssign",
			"StockEventVarianceComparison",
			"AcctgTransPartySumFact",
			"ProductPromoExtRegisterParty",
			"SupplierProductAndQuota",
			"ShipmentManifestView",
			"ProductStoreGroupAndMemberAndOwner",
			"TaxAuthorityAndDetail",
			"TimekeepingDetailAndParty",
			"PayrollEmplAllowanceAndDetail",
			"EmplPositionDimension",
			"InsuranceParticipateTypeDimension",
			"InventoryItemTotalDetailAndSupplier",
			"MaxContentApprovalView",
			"MTCustomerRepresentatives",
			"PartyNameAndBillingAddress",
			"EquipmentIncreaseAndItem",
			"RecruitRoundCandidateExaminerAndPerson",
			"GeoExcludeNA",
			"EmployeePOSAndRole",
			"InvoiceAndTotalAndPaymentView",
			"ProductEventItemDeclarationAvailable",
			"PartyIdentificationAndParty",
			"OrderAndContainerAndAgreementAndOrderDetail",
			"RequirementPartyDetail",
			"PaymentAcctgTrans",
			"PartyResourceCommunication",
			"FacilityContentDetail",
			"OrderItemMinSeqDetail",
			"PayrollFormulaAndCharAndPyllItemType",
			"CostAccountingDetail",
			"ListPartySupplierByRole",
			"CommunicationEventAndRole",
			"SupplierProductDetail",
			"DeliveryItemGroupByProductAndPromo",
			"TrackingCodeAndOrderHeader",
			"CustReqAndTypeAndPartyRel",
			"VendorReturnItemDetail",
			"AcctgCustomTimePartySumFact",
			"OrderHeaderDelivery",
			"OrderAndInvoiceAndPayment",
			"AgreementTermAllowanceAndAttr",
			"ProductAndAverageCostAndInventoryTotal",
			"AcctgDocumentSumFact",
			"InventoryItemAndDetail",
			"PartyEmplOrgEeeRelationship",
			"PartyRelationshipActive",
			"OrderAndClusterTotal",
			"InsuranceTypeDimension",
			"ListWorkShiftFact",
			"ProdCatalogCategoryAndProductCategory",
			"FixedAssetDecreaseAndDetail",
			"PartyRelationshipAndPartyFrom",
			"SupplierProductPartyActiveRef",
			"PaymentMethodTypeAndGlAccountDetail",
			"ProductFacilityAndAverageCost",
			"ProductPromoCondCategory",
			"PaymentMethodAndGiftCard",
			"InventoryItemGroupByDateDetail",
			"ProductCategoryRollupAndChild",
			"ProductCategoryContentAndInfo",
			"OrderHeaderAndItems",
			"ProductCategoryMemberRel",
			"OrderHeaderItemAndInvRoles",
			"ReturnHeaderOrderDetailPos",
			"ProductAndUom",
			"GeoAssocAndGeoToWithState",
			"SupplierProductAndProduct",
			"ProductEventDetail",
			"DeliveryClusterCustomerAvailable",
			"PartyRelationshipAndDetail",
			"GlAccountAndGlAccountCategoryMember",
			"PartyAddressPurpose",
			"InventoryItemDetailAndFreezed",
			"TrainingCourseAndSkillType",
			"ProductInventoryNearDateGroupByProductTotalCount",
			"ProductPromoStoreApplStrAgg",
			"ProductPromoApplRoleType",
			"FacilityFact",
			"InvoiceItemAndSaleTaxRtnGroupBy",
			"DeliveryItemFact",
			"PartySalesmanDetail",
			"OrderAndCluster",
			"InventoryItemReceiveByOrder",
			"PartyClassificationFact",
			"AgreementAndOrderHeader",
			"ProductAndCategoryMember",
			"InventoryItemGroupByDateAndLabel",
			"SupplierProductGroup",
			"VoucherInvoiceAndResource",
			"PhysicalInventoryDetail",
			"OldCustRequestRole",
			"PersonRelationship",
			"StockEventItemSum",
			"PartyAcctgPrefAndGroup",
			"OrderRoleViewSupplier",
			"OrderReqCommitDetail",
			"VoucherAndResourceAndInv",
			"AcctgTransGlAccountSums",
			"PartyPostalAddressMaxActiving",
			"ProductCategoryRelationship",
			"PosWorkShiftCost",
			"OrderItemIssuance",
			"InventoryItemAndDetailFull",
			"OlbiusPartyPermissionActive",
			"PaymentApplicationAndInvAndParty",
			"ProductAndAssoc",
			"WorkEffortRequirementView",
			"MTCustomerAndPartyTo",
			"TripItemDetail",
			"InvoiceItemAndVATCategorySum",
			"PosTerminalBankDetail",
			"WorkEffortAndContentDataResource",
			"TaxAuthorityGlAccountBalance",
			"QuotaItemAndProductAvailable",
			"CustomerMtl",
			"SchedulePentaho",
			"PartyFixedAssetAssignAndRole",
			"AgreementItemAndPartyAppl",
			"PartyGroupAndPrimaryAddressAndEmailAndMobileAndRole",
			"ProductAndPriceAndGoodIdentificationSimple",
			"FixedAssetAllocAndParty",
			"MarketingCostDetail",
			"ProductCategoryAndProduct",
			"PartyGrpCTMPostal",
			"PartyDetailAndWorkEffortAssign",
			"ContentAssocCommentCount",
			"PartyFromSalesmanActive",
			"SupplierProductGroupAndProductAndInventory",
			"ProductStoreGroupRollupAndChild",
			"TenantComponent",
			"EmploymentAndPartyGroupOlbius",
			"GroupInvoiceItemInvoiceSalesTotalPdf",
			"ProductQuotationProductStoreAppl",
			"FixedAssetDepreciationCalcAndItem",
			"ProdCatalogCategoryAndProduct",
			"CostAccDepartmentDetail",
			"PartyClassGroupDimension",
			"CategoryAndProductAndTaxRate",
			"PartyAndSupplierRelationshipInformation",
			"ProductIdAndGoodIdPLU",
			"InvoiceItemProductSummary",
			"TrainingCourseAndCost",
			"DetailInfoSalesMan",
			"ProductSummaryAndProduct",
			"PaymentTypeAndGlAccountType",
			"ReturnItemBillingMinItemSeq",
			"CarrierAndShipmentMethod",
			"ProductAndContent",
			"PartyCustomerAndProductStore",
			"ShipmentAndShipmentReceiveAndOrderItemBillingAndInvoice",
			"MaxRevisionItemView",
			"KeyPerfIndicatorAndDetail",
			"DeliveryClusterCutomerAndPack",
			"DataResourceContentView",
			"PosWorkShift",
			"ProductAndProductEventItemDeclarationAvailable",
			"WorkEffortNoteAndData",
			"InventoryItemQuantityWarningMax",
			"ProductCategoryMemberStampDetail",
			"PartyFromAndPartyNameDetail",
			"OldWorkEffortAssignmentRate",
			"WorkingShiftDimension",
			"ContentAndRole",
			"InventoryItemVarianceGroupByDetail",
			"ReturnInventoryItemDetail",
			"ProductTddAndSkuAndSupplierRef",
			"OrderAndReturnHistoryPOS",
			"InventoryItemAndLabelAppl",
			"ContactMechDetail",
			"PortalPageAndUserLogin",
			"ProductPromoExtDimension",
			"OrderHistoryLog",
			"OrderItemAndShipGroupAssoc",
			"AddressDimension",
			"AcctgTransEntryAndCredit",
			"RequirementItemAndTransfer",
			"OldPartyTaxInfo",
			"EquipmentIncreaseAndItemTotal",
			"CostAccBaseAndInvoiceItem",
			"InventoryItemLabelTotalDetail",
			"OrderHeaderAndOrderRoleFromTo",
			"ProductSummaryAndProductConfigPacking",
			"ConfigLabelGroupByProduct",
			"AcctgTransEntryParty",
			"PaymentPartyDetail",
			"ShippingTripView",
			"InvoiceAndPaymentApplGroupBy",
			"ReturnOrderFact",
			"InvoiceItemInvoicePdfSum",
			"OrderBillingInvoice",
			"PromosCondAction",
			"OrderItemAndProductContentInfo",
			"WorkEffortAndTimeEntry",
			"StockEventDetail",
			"InvoiceShipmentItemAndAcctgTrans",
			"PartyAndPartyGroup",
			"MTPartyRelAndParty",
			"RecruitmentPlanAndParty",
			"ShipmentPackageContentDetail",
			"DeliveryDetail",
			"ReturnAndReturnItemBilling",
			"ShipmentReceiptAndItem",
			"PosWorkShiftInCome",
			"CommunicationEventFact",
			"OrderItemPromoNeedSettlement",
			"PartyAndPostalAddressDetail",
			"PartyNoteView",
			"PosTerminalLogAndOrderHeader",
			"RecruitmentRoundCandidateAndPerson",
			"RecruitmentFormTypeDimension",
			"CustomerSalesman",
			"OrderHeaderAndRoleCustomer",
			"KeyPerfIndicatorAndPartyFull",
			"CustomerAndDeliveryCluster",
			"EmplTSAndCustomTimePeriodAndPartyGroup",
			"EmploymentAndPerson",
			"ProductTddAndSkusAndSupplierRef",
			"ProductFacilityAndProduct",
			"ProductAndPriceAndGoodIdentificationAndProStore",
			"ProdCatProdCatMem",
			"FAQCategory",
			"AcctgDocumentListFact",
			"AcctgTransSumDayFact",
			"EquipmentAndParty",
			"PackItemAndDeliveryView",
			"PartyPostalAddressActiving",
			"PartyPerfCriteriaAndItemProduct",
			"OrderRoleViewOrgani",
			"SupplierProductDistinctAvailable",
			"ReturnHeaderAndParty",
			"PartyAndFaxNumber",
			"InvoiceItemTypeAndGlAccountDetail",
			"AcctgTransEntryAndDebitCredit",
			"EmplPositionAndTypeAndParty",
			"PartyGroupAndPrimaryAddressAndEmailAndMobile",
			"OrderItemShipGrpInvResDetail",
			"ProductAndCatalogTempDataDetailSearch",
			"FixedAssetIncreaseAndItem",
			"ProductPromoExtActionProduct",
			"SupplierAndProductAndTarget",
			"WorkEffortOrderHeaderView",
			"InsBenefitTypeFreqDimension",
			"PartyGrpCTMEmail",
			"AgreementAndAgreementTermCampaign",
			"ShipmentReceiptOrderDetailInfo",
			"ProductAndGoodIdsAndCatalogAndSupplier",
			"OrderRoleAndPartyNameView",
			"HolidayAndEmplTimekeepingSign",
			"EmployeeCallCenter",
			"OrderHeaderAndItemFacilityLocation",
			"InventoryItemAndProductGroupBy",
			"PartyCustomerRouteAddressGeoPoint",
			"ProductConfigProductAndProduct",
			"DeliveryEntryRoleAndPartyAgg",
			"InventoryItemFact",
			"ProductNameByInventoryItemTotal",
			"OrderAdjustmentReturnAdjustment",
			"CustomTimePeriodAndType",
			"ContentAssocRevisionItemView",
			"FacilityAll",
			"RouteAndSalesRouteScheduleArr",
			"TimekeepingDetailPartyAndGroupBy",
			"OrderItemAndProductDetail",
			"PayHistoryAndPtyRel",
			"PartyClassificationParty",
			"VarianceReasonDimension",
			"ProductCacheDataBrand",
			"EquipmentAndAllocateCount",
			"CustomerToSearch",
			"EquipmentDecreaseItemAndPrice",
			"PartyNameAndPhoneHome",
			"PentahoServices",
			"CostAccBaseAndType",
			"DeliveryInventoryItemDetail",
			"ProductsOfRivals",
			"ShipmentAndShipmentItemBillingAndInvoice",
			"InventoryItemReceiveAndOrderItem",
			"OrderItemBillingAndInvGroupBy",
			"ProductStoreDimension",
			"InvoiceItemAndDiscountRtnGroupBy",
			"PayrollTableRecordPartyIncomeAmount",
			"AgreementItemAndFacilityAppl",
			"TimekeepingSummaryPtyAndEmplPosTyp",
			"PartyGroupAndPerson",
			"CustomerTimePaymentInfoView",
			"OrderReportView",
			"ProductAndCatalogTempAndUomsComplexPro",
			"RequirementItemDetail",
			"EmplLeaveReasonTypeDimension",
			"PayrollItemTypeDimension",
			"OldWorkEffortContactMech",
			"GlOrganizationClassAndChild",
			"WorkingShiftConfigAndParty",
			"AgreementAndAgreementAttributeName",
			"GlacountAndParent",
			"PartyTaxAuthInfoAndDetail",
			"PartyNameAndHomePhone",
			"InvoiceExport",
			"WorkEffortAndGoods",
			"EquipmentAllocItemAndEquipment",
			"AcctgDocumentSumNoOrderFact",
			"ContactListPartyAndContactMech",
			"PartyFromRelAndPartyToPromosExt",
			"PartyRelationshipAndPartyTo",
			"PayrollTableRecordPartyDeductionAmount",
			"PartyEmplOrgRelationship",
			"PaidInOutAndFacility",
			"PartyTypeOpponent",
			"PostalAddressFullDetail",
			"PackingListDetailAndOrderAndProduct",
			"PurchaseOrderItemAndShipmentReceipt",
			"TopicAndComment",
			"EntitySyncHistoryDetail",
			"InvoicePartyDetail",
			"PartyNameAndBillingShippingAddressAndMobileAndEmailAndRole",
			"AcctgTransEntrySumsLimitDueDate",
			"ProductPromoExtCondProduct",
			"ShippingTripDeliveryOrderItemView",
			"PaymentAndTypePartyNameView",
			"PartyCustomerDetailAndRoute",
			"WorkEffortShoppingListView",
			"LocationFacilityInventoryItemLocationSum",
			"AcctgPartyPreferenceFact",
			"OrderPromoFact",
			"InvoiceItemView",
			"BalanceInventoryItemsView",
			"PosTerminalStateAndPaymet",
			"OrderReceiptNoteDetail",
			"WorkEffortAndFixedAssetAssign",
			"SupplierProductAndProductAndUom",
			"RequirementItemShipmentReceipt",
			"PartySupplierDetail",
			"WorkEffortAndFulfillment",
			"EmplPositionDetail",
			"EmployeePOSAndInfo",
			"ReturnItemFact",
			"ProductPromoSettlementDetailMore",
			"Employee",
			"EmplPosTypeAndPerfCri",
			"OrderReportGroupByProduct",
			"PartyResource",
			"ProductPromoCondProduct",
			"ProductAndPriceAndGoodIds",
			"PartyCustomerAddressGeoPointAndSalesman",
			"CustomerReturnTotalCount",
			"StatusValidChangeToDetail",
			"SaleOrderItemCompleted",
			"ContentAssocDataResourceViewTo",
			"EmplPositionFulfillmentDetail",
			"GroupInvoiceItem",
			"SurveyQuestionAndAppl",
			"PosHistoryProductStoreAndOrderHeader",
			"InvoiceItemAndProductTaxInc",
			"EquipmentDecreaseItemAndEquipment",
			"DeliveryItemChangeLast",
			"StoreCatalogCategory",
			"ReturnItemMinItemSeq",
			"EmploymentAgreementAndDetail",
			"InvoiceAndApplAndPaymentCardAndOrder",
			"PersonInsuranceFact",
			"GeoRelationship",
			"PosTerminalLogAndReturnOrder",
			"OrderSumTotalRowNotCount",
			"InsReportOriginateAndTypeGroup",
			"InvoiceOrderDetail",
			"OrderContentPaymentDataResource",
			"PortalPageAndPortlet",
			"RecruitmentRequireAndParty",
			"ShipmentReceiptForPO",
			"WorkEffortAndInventoryProduced",
			"HolidayConfigAndEmplTimekeepingSign",
			"DeliveryItemGroupByTransferItemDetail",
			"ShippingTripPackActiveView",
			"InvoiceItemAndProductTaxSum",
			"OrderHistoryPOS",
			"AcctgTransAndEntryByInventoryItem",
			"ProductAndTaxAuthorityRateSimple",
			"OrderAndShipAndContactAndRequirement",
			"PaymentMethodTypeGlAccountDetail",
			"ProductStoreRoleAndPartyContactTemp",
			"PartyRelationShipAndDetail",
			"OldProductKeywordResult",
			"PartyNameAndMobilePhone",
			"DataResourceAndContent",
			"ProductPromoExtRegisterRuleDetail",
			"AcctgTransCalcFactAndGlAccountAndPartyAndCurrency",
			"ContentAssocViewFrom",
			"ProductStoreLoyaltyApplFilter",
			"FixedAssetDecreaseAndItemAndSum",
			"CustRequestNoteView",
			"CustomTimePeriodsWithOrgFullName",
			"InvoiceItemReturnGroupBy",
			"PartyAndUserLoginAndPerson",
			"GlAccountRelationship",
			"ShoppingCartLogLiteDetail",
			"OrderItemQuantityReturnable",
			"DeliveryItemGroupByOrderItemDetail",
			"RecruitmentPlanBoardAndRecruitment",
			"OrderItemAndShipGrpInvResAndItemSum",
			"StockEventItemAndRoleTempData",
			"InvoiceAndPaymentApplied",
			"PartyTypeDimension",
			"OldPartyRate",
			"PartyOutletSimple",
			"ProductQuotationRulesAndActionTax",
			"OrderItemTotalExcel",
			"PartyNameAndPhoneMobile",
			"ProductStoreFacilityAndFacility",
			"ProductCatergoryAndTaxRate",
			"ContentAssocViewTo",
			"AgreementContentAndDataResource",
			"InvoiceAndApplAndPaymentCashAndOrder",
			"SumInvoiceItemInvoiceSalesTotalPdf",
			"OrderTaskList",
			"PartyCustomerDetailView",
			"UserLoginAndSecurityGroupSales",
			"FacilityPartyFacilityDetail",
			"RouteAndSalesRouteSchedule",
			"InvoiceItemGroupByTax",
			"RequirementItemDetailAndInventory",
			"InvoiceItemPromoView",
			"DeliveryClusterAndPartyDetail",
			"RouteViewDetail",
			"OrderHeaderAndOrderItem",
			"WorkEffortCustRequestView",
			"RequirementItemShipmentReceiptGroupByExpMnfLot",
			"StockEventRoleDetail",
			"WorkOvertimeRegistrationAndPos",
			"EmplPositionTypeAndClass",
			"OrderHeaderAndParty",
			"TimekeepingSummaryPtyAndPtyRel",
			"InsAlwBenefClassTypDim",
			"ShipmentAndContactMechDetail",
			"PersonAndPrimaryPhone",
			"ProductDimension",
			"PersonAndContactMechDetail",
			"SegmentGroupViewRelatedParties",
			"TimekeepingDetailPtyAndEmplPosTyp",
			"PartyFullNameDetailSimple",
			"BenefitTypeAndParty",
			"FinAccountAuthSum",
			"InventoryItemAndProduct",
			"SalesOrderNewFact",
			"SupplierProductPartyCodeActiveRef",
			"SupplierProductGroupAndProductAll",
			"PartySupplier",
			"OrderAndSupplierAndReturn",
			"ListInvoiceItem",
			"RoleTypeIn3Levels",
			"RequirementDetail",
			"StockEventItemDetail",
			"PayrollEmplParamAndDetail",
			"CustomerReturnTotalByReceiveDateDetail",
			"ConfigPackingAndUom",
			"AcctgDocumentListFactSum3",
			"AcctgDocumentListFactSum2",
			"PicklistItemAndBin",
			"OldProductKeyword",
			"RouteInformationByDay",
			"DeliveryItemSumTransferView",
			"FacilityAndContactmechAndTelecomNumber",
			"PerfCriteriaPolicyAndRateGrade",
			"ProductQuotationStoreApplActive",
			"PersonRelationshipFact",
			"OrderItemAndAdjustment",
			"InvoiceAndApplAndPayment",
			"SupplierProductGroupDetailAndAgreementDetail",
			"InvoiceAndPaymentIdGroupBy",
			"OrderItemMini",
			"InventoryItemLocationAndInventoryItem",
			"PromoSettleResultOrderCommit",
			"ShipmentItemDetail",
			"OrderSettlementCommitmentDetail",
			"InventoryItemMin",
			"ProductCategoryMemberAndPriceFilter",
			"EmplPositionAndFulfillmentAndPartyRelationship",
			"PayrollTableRecordPartyAndPos",
			"AcctgDocumentListFactSum",
			"ProductAndUomAndGoodIdsAndCatalog",
			"EmplLeaveDetail",
			"CurrencyDimension",
			"ProductCacheDataDetail",
			"LocationFacilityAndInventoryItemLocation",
			"InventoryItemVarianceFact",
			"GlAccountBalanceFact",
			"FixedAssetIncreaseAndTotal",
			"AgreementAndPartyFromDetail",
			"EmplPositionAndPartyShiper",
			"PayrollEmplParamAndPos",
			"EmploymentAndPersonTermination",
			"OrderAdjustmentPromoView",
			"PartyFamilyView",
			"ConfigPackingDetail",
			"RequirementByProductFacility",
			"PartyOutletLocation",
			"OrderHeaderAndUserLogin",
			"GlAccountBalanceView",
			"ProductCategoryMemberAndProduct",
			"ProductAndCategoryPrimary",
			"OrderHeaderAndWorkEffort",
			"InventoryItemAndTransfer",
			"InvoiceItemAndDiscountGroupBy",
			"ProductAndPriceAndGoodIdentification",
			"AcctgTransHistoryLast",
			"PartyRoleAndContactMechDetail",
			"EntityGroupEntrySync",
			"PersonAndPartyGroup",
			"WorkEffortCommunicationEventView",
			"PartyRelationshipPartyShiper",
			"TransferItemDetail",
			"InventoryItemAvailable",
			"ProductQuotationStoreGroupApplDetailQuotation",
			"ReturnAdjustmentOrderAdjustment",
			"PartyFromAndNameRelOutletDetail",
			"OrderHeaderNeedDelivery",
			"ShippingCustomerGroup",
			"InvoiceAndItemAndProductGroupBy",
			"InvoiceAndTotalAmountView",
			"PartySalesmanInformationDetails",
			"PartyPrimartyAddressAndPostalAddress",
			"CustRequestAndNote",
			"OrderItemBillingMinItemSeq",
			"ShipmentRouteSegmentDetail",
			"ProductAndCatalogTempDataDetail",
			"PartyToAndNameRelOutletDetail",
			"OrderPurchasePaymentSummary",
			"PartyPostalAddrFullDetailAndCurrRes",
			"GeneralSaleOrderInfo",
			"Topic",
			"ProductAndConfigPacking",
			"SalesOrderRollStoreFact",
			"AcctgTransAndEntries",
			"PartyNameAndPrimaryAddressAndMobileAndEmailAndUserLoginAndRoleAndFacility",
			"PicklistItemDetail", "RecruitmentSalesOfferAndCustomTime",
			"WorkEffortAndChild", "LoyaltyCustomer", "AcctgTransCalcFact",
			"QuotaItemAvailableGroupByProduct",
			"PayrollTableRecordPartyOrgPaidAmount",
			"ProductPromoCodeEmailParty", "EquipmentAllocateAndTotalAmount",
			"RecruitmentSalesEmplAndDetail",
			"ProductEventDeclarationItemAndProductAvailable",
			"InvoiceItemIssuranceView", "OldEmplPositionTypeRateAndGeoAppl",
			"ProductAndGoodIdsAndProductStore", "ProductPromotionsCondAction",
			"WebsiteContentCategoryDetail", "ProductVirtualAndVariantInfo",
			"DeliveryEntryDetailAndOrder", "PartyAcctgPrefAndGroupAndRole",
			"ContentAndContentType", "CustomerAndDeliverySimple",
			"PartyPeriodTypeDetail", "WorkEffortAndPartyAssignAndType",
			"PaymentPartyFromDetail", "InventoryItemGroupByDateNotDateReceive",
			"InvoiceTaxInfoAndGeo", "AcctgTransEntryAndDebit",
			"ProductQuotationAndPriceRCA", "WebSiteAndContent",
			"ProductFacilitySummaryAndFacility",
			"HRPlanningAndCustomTimePeriod", "WorkEffortPartyAssignByGroup",
			"ProductQuotationRulesAndTax", "InsReportPartyOriginateAndDetail",
			"ConfigPackingAndProduct", "InventoryItemGroupByDate2",
			"FacilityContactMechDetail", "SumATPByProductAndEXP",
			"SalesOrderPromoCodeFact", "OrderItemTotalInBill",
			"PayrollTableRecordPartyAmountAndFormula",
			"TimekeepingDetailPartyAndWorkdayPaid",
			"TimekeepingSummaryAndParty", "ReturnHeaderOrderDetail",
			"DeliveryEntryRoleTutorial", "MTCustomerRelationship",
			"PartyCampaignRelationshipAndPerson",
			"WorkEffortPartyAssignAndRoleType", "PartyDistributorInfos",
			"RecruitRoundCandidateAndExaminer",
			"OrderItemShipGrpInvResAndItem", "SubscriptionAndCommEvent",
			"ConfigPrintOrderAndStore", "AddressDetail", "ImageAttribute",
			"InsAllowanceBenefitTypeAndLeaveReason", "PartyDimension",
			"PartyNameAndWorkPhone", "DeliveryEntryAndOrderAgg",
			"ProductStorePromoAndAppl", "RecruitmentPlanBoardAndPerson",
			"TimekeepingDetailPartyAndParty", "OlbiusPartyPermissionAndUser",
			"AllocCostPeriodItemAndGlAccount", "GlAccountTypeAndGlAccount",
			"CategoryDimension", "AcctgInvoiceVoucherFact",
			"PartySalesmanAndEmplPostion", "SalesBonusSummaryAndBonusAmount",
			"SalesBonusSummaryAndDetail", "TaxAuthorityCategoryView",
			"LotDimension", "ProductPlanItemAndProduct",
			"WorkEffortAssocToView", "ItemIssuanceAndInventoryItem",
			"PartyCampaignRelationshipAndPartyGroup",
			"CostAccMapDepartmentDetail", "TransferItemAndInventoryExport",
			"PartyAndUserLoginFull", "PartyRelationshipToPartyOlbius",
			"PartyCustomerAndRouteFullDetail", "OldValueLinkFulfillment",
			"UserloginSecurityGroupPermission", "ProductStorePromoApplFilter",
			"PmtGrpMembrPaymentAndFinAcctTrans", "RecruitmentFact",
			"OrderReportSalesMan", "BillingAccountAndRole",
			"PartyAndTelecomNumber", "TenantDataSource",
			"SupplierProductGroupAll", "ProductQuotationAndRulesAndStore",
			"PartyResourceCommunicationAreaMember",
			"CommunicationEventBrandProduct",
			"OrderAndContainerAndAgreementAndOrderDetailAll",
			"InvoicesItemTypesGlAccountDetail", "HospitalAndPostalAddress",
			"SupplierProductPartyActive", "OrderPurchaseTotalRowNotCount",
			"ShipmentReceiptAndInvoice", "ProductStoreAndEntitySync",
			"PartyNameAndBillingShippingAddressAndMobileAndEmail",
			"FacilityPartyDetail",
			"ProductAndPriceAndGoodIdentificationAndFacilityAndProStore",
			"FacilityGroupDimension", "EmploymentAndPersonOlbius",
			"PromoOrderHeaderFullView", "Component",
			"EmplLeaveRegulationAndDetail", "SalesOrderFact",
			"StatusDimension", "OrderHeaderAndRoles",
			"SubContentDataResourceView", "OrderReqCommitItemDetail",
			"CustomerAndPartyGroupAndGeoPoint", "PaymentMethodAndCreditCard",
			"PayrollTablePartyInComeAndDeductionAndOrgPaidAmount",
			"PayrollParamPositionTypeAndParameters", "PartyResourceMember",
			"PartyPrimaryPhoneAndTelecomNbr", "ProductPackagingUomAndUom",
			"MarketingCampaignDetail", "RouteScheduleDetailDateAndSchedule",
			"OrderItemAndShipGrpInvResAndItem", "OrderInvoiceNoteAndDetail",
			"InvoiceAndPaymentNotApplied", "KeyPerfIndPartyTargetAndParty",
			"ShippingTripUnpaidInvoice", "PartyNameView", "InventoryOrderItem",
			"PartyTaxAuthInfoAndActive", "SumInvoiceItemInvoicePdf",
			"ReturnStatusTotalGroup", "PartyNameAndEmail",
			"MTPartyRelAndPartyAndPartyCus", "OrderReportSalesGroupByProduct" };
	public static String[] tables = { "DeliverableType", "ShippingDocument",
			"SupplierProduct", "ItemIssuance", "ProductCalculatedInfo",
			"DayOfWeek", "PartyIdentificationType", "AcctgTransTypeAttr",
			"PerfCriteriaAssessmentParty", "ProductCategoryLink",
			"RejectionReason", "PayrollTableCode", "ShoppingListItem",
			"ProductAverageCost", "ProductMaint", "InvoiceContent",
			"TransferType", "EntitySyncIncludeGroup", "FixedAssetMaintOrder",
			"WorkFlowProcess", "StockEventParty", "RequirementTypeAttr",
			"Content", "FacilityContactMech", "PartyIcsAvsOverride",
			"ProductOrderItem", "ProductStoreGroupRollup", "TermTypeAttr",
			"AgreementTermAttribute", "EmplPositionTypeRateGeoAppl",
			"PartyType", "ProductStoreGroup", "RecruitmentCostCategoryType",
			"ContentAssocPredicate", "ReturnRequirementCommitment",
			"PartyStatus", "PartyRole", "WorkingShiftPartyConfig",
			"EquipmentAllocate", "CommContentAssocType",
			"PartyClassificationType", "EducationSchool",
			"ProductPromoExtType", "StatusType", "TrainingPurposeType",
			"FacilityGroup", "ShipmentGatewayFedex", "ProductPromoRule",
			"FixedAssetPartyGlAccount", "Addendum", "CountryTeleCode",
			"FixedAssetDepreciation", "ContentTypeAttr",
			"PaymentGatewayAuthorizeNet", "OrderRequirementCommitment",
			"DeliveryEntryRole", "WebUserPreference", "ContentSearchResult",
			"EmplPosition", "FacilityAttribute", "BillOfLadingType",
			"VoucherInvoice", "SgcConnectDataCenterKhttLog", "PaymentType",
			"ContainerGeoPoint", "EmplPositionClassType", "InvoiceContentType",
			"InvoiceTaxInfo", "PartyGroup", "WorkEffortPurposeType",
			"RecruitmentAnticipateStatus", "WorkEffortKeyword",
			"ShipmentReceiptRole", "Budget", "AgreementItemType",
			"RuntimeData", "InsuranceType", "ProductAttribute",
			"VoucherInvoiceSystemConfig", "UserPreference", "Survey",
			"TransferItemIssuance", "WorkEffortSearchConstraint",
			"RecruitmentChannelType", "FixedAssetTypeGlAccount",
			"SalesStatementType", "KeyPerfIndPartyTargetItem", "Voucher",
			"SalaryStep", "TerminationType", "DataSourceType",
			"BudgetScenario", "GroupPeriodType", "OrderItemShipGroup",
			"CustomerProductInventory", "FinAccountTypeGlAccount", "PartyRate",
			"GlAccount", "ContentCategoryType", "SalesStatementDetail",
			"InventoryItemLabel", "CommunicationEventRole",
			"FixedAssetStdCostType", "ProductFeatureIactn", "EmplTimesheets",
			"ReturnItemType", "MarketingPlanContent", "EquipmentDecreaseItem",
			"FileExportedTemp", "ProductPriceCond", "PayrollFormulaHistory",
			"ProductKeyword", "RecruitmentRoundSubjectParty",
			"ProductPromoExtRegister", "AgreementAndOrder", "CustomMethod",
			"WorkEffortGoodStandardType", "WorkingLateRegister",
			"FacilityLocationGeoPoint", "CountryCode", "FixedAssetAccompany",
			"DeliveryTypeEnum", "PartyTypeEnumAssoc", "StudyModeType",
			"TrackingCode", "CommunicationEventType",
			"ProductStoreVendorPayment", "ProductEventType",
			"ProductCategoryRole", "InventoryTransfer",
			"AgreementPromoExtAppl", "WorkEffortSearchResult",
			"EmploymentAppSourceType", "FixedAssetRegistration",
			"GlAccountReciprocal", "GlAccountHistoryParty",
			"FixedAssetCustomTimePeriod", "PartySalesman",
			"PerfCriteriaAssessment", "CommunicationEventOrder",
			"OrderBlacklist", "StockEventItemRoleTempData",
			"PromotionDimension", "TaxAuthority", "InventoryItemTempRes",
			"OrderItemPriceInfo", "AllocationCostType", "VisitFrequencyType",
			"ShoppingListItemSurvey", "PicklistBinItem", "CostComponentType",
			"ServerHitType", "PayrollScheduleFormula",
			"WorkEffortAssocTypeAttr", "CommunicationEventProduct",
			"ContactMechTypeAttr", "VatDeclarationTemplate",
			"ProductConfigItem", "OlbiusOverridePermission",
			"ProductPromoSettlement", "PosTerminal", "EmplPositionType",
			"CustRequestResolution", "QuoteAdjustment", "FinAccountTypeAttr",
			"InvoiceItemTypeMap", "EntitySyncHistory", "RequirementItem",
			"WorkEffortIcalData", "ProdCatalogInvFacility",
			"SalesRouteSchedule", "RoleTypeAttr", "OrderDeliverySchedule",
			"CostAccMapDepartment", "BudgetReviewResultType", "Equipment",
			"ProductSubscriptionResource", "Religion", "ProductTempData",
			"PaymentTypeAttr", "OrderSchedule", "ProductStoreWifi",
			"ProductStorePaymentSetting", "Formular", "ProductStoreRole",
			"PortalPagePortlet", "GlAccountCategoryType", "GlAccountBalance",
			"ShoppingCartLogLite", "QuoteNote", "PartyPerfCriteriaResult",
			"InsBenefitTypeAction", "WorkEffortAttribute",
			"ProductPromoExtCond", "EmplPositionTypeClass", "FacilityContent",
			"TaxAuthorityRateProduct", "WorkEffortSkillStandard",
			"SalesOpportunityHistory", "ApplicationSettingType", "ReturnType",
			"PaymentGatewayConfig", "InvoiceItemTypeAttr",
			"PersonWorkingProcess", "ReceiptMoneyEmployee",
			"OrderItemAttribute", "Facility", "WorkEffortSurveyAppl",
			"FileExtension", "Pack", "CreditCard", "WorkFlowGroup",
			"ProductManufacturingRule", "DeliveryClusterCustomer",
			"PlanPurchaseOrder", "PayrollTableRecordPartyAmount",
			"ShoppingListWorkEffort", "ProductPromo",
			"PayrollParameterHistory", "PaymentMethod",
			"EnumerationRelProduct", "TrainingCoursePartyAttendance",
			"TemporaryParty", "FixedAssetMeter", "PicklistItem",
			"EnumerationInvoiceItemType", "EquipmentDecrease", "VehicleType",
			"ProductPromoExtTypeEnumAssoc", "ProductStorePromoAppl", "Visitor",
			"ShipmentGatewayConfig", "PaymentBudgetAllocation",
			"PicklistItemLocation", "EquipmentAllocation", "ProtectedView",
			"WorkEffortAssocAttribute", "SalesForecast", "StockEventItemRole",
			"OrderItemBilling", "TransferHeaderNote",
			"KeyPerfIndPositionTypeAppl", "SubscriptionAttribute",
			"UserLoginHistory", "ConfigMoq", "RecruitmentSalesEmpl",
			"TaxAuthorityRateType", "SegmentGroup", "FinAccountAuth",
			"DegreeClassificationType", "AgreementWorkEffortApplic",
			"MrpEvent", "GeoAssocType", "ReturnReason", "OrderInvoiceNote",
			"MarketingCampaignPromo", "SalesOpportunityQuote",
			"EmplPositionFulfillment", "PartyInsuranceAllowancePayment",
			"PerfCriDevelopmentType", "TransferItemShipGroupAssoc",
			"ProductFeatureCategoryAppl", "PartyNameHistory",
			"PayrollTableRecordStatus", "PackingListHeader",
			"VatInvoiceInputTax", "ProductPromoContent", "VarianceReason",
			"PicklistBinRole", "PartyBenefit", "AccommodationMap",
			"ProductQuotationStoreGroupAppl", "PartyGeoPoint",
			"ProductUsingHistory", "InsuranceAllowancePaymentDecl",
			"StockEventVariance", "FixedAssetDecrReasonType", "RouteHistory",
			"ZipSalesRuleLookup", "ProdConfItemContentType",
			"TimekeepingSummaryParty", "InvoiceRole", "CountryAddressFormat",
			"ApplicationSandbox", "CustRequestParty", "ContactMechPurposeType",
			"EmplLeaveRegulation", "PaymentGatewayClearCommerce",
			"ImageDataResource", "TripDetail", "TaxAuthorityRatePayroll",
			"ExchangedRateHistory", "OrderAttribute", "InventoryItemAttribute",
			"MarketInterest", "AcctgTransEntry", "ShipmentContactMechType",
			"GoodIdentificationType", "DecrementType", "RateType",
			"RecruitmentPlanRound", "ShipmentPackage", "TransferRequirement",
			"ProductConfigOption", "InsurancePartyOriginate", "ListWorkShift",
			"AgreementGeographicalApplic", "FinAccountTrans",
			"ProductSearchResult", "CharacterSet", "LabelItem", "InvoiceType",
			"WorkEffortInventoryProduced", "ServerHit", "BudgetItemTypeAttr",
			"ProductCategoryRollup", "ReturnItemResponse", "SupplierTarget",
			"PayrollPreference", "DataSource", "ProductEvent",
			"SalesBonusSummaryPartyAmount", "DeliveryCluster",
			"CustRequestStatus", "Container", "RoleTypeGroupMember",
			"EmplTimekeepingSign", "PartyRelationship", "ProductConfigStats",
			"FacilityType", "ProductFeaturePrice", "VisualThemeResource",
			"ProductPromoExtRegistrationEval", "CostAccDepartment",
			"EquipmentProductStore", "CountryCapital", "ProductQuotationType",
			"ShipmentPackageContent", "CommonSalaryMinimum",
			"RecruitmentSalesOffer", "SalesOpportunityStage",
			"EmplAttendanceTracker", "ProductStoreCatalog",
			"InventoryItemDetail", "ProductReview", "ResponsibilityType",
			"ContentRevisionItem", "ProductFeatureAppl", "OrderHeaderNote",
			"InventoryItemAndLabel", "QuantityBreak",
			"BudgetScenarioApplication", "MarketingCampaignNote",
			"ProductStoreKeywordOvrd", "ProductPromoSettlementStatus",
			"BudgetScenarioRule", "ProductStoreStatus", "GlAccountClass",
			"PartyPerfCriItemProductResult", "ShipmentItemBilling",
			"ReturnItemShipment", "JobRequisition_OLD", "FixedAssetAttribute",
			"ProductPromoCode", "WorkFlowActionRoleType",
			"AgreementItemTypeAttr", "WorkFlowStatusActivity",
			"SubscriptionActivity", "ProductStoreGroupMember", "WebSite",
			"ProductPriceAction", "WebSiteRole", "ProductPriceType",
			"InvoiceTerm", "OlbiusApplication", "Geo", "BudgetRevisionImpact",
			"DataResourcePurpose", "ReturnItem", "PartyFixedAssetAssignment",
			"WebSiteContactList", "RecruitmentPlanCostItem", "Loyalty",
			"DataResource", "ProductStoreLoyaltyAppl",
			"PartyContactMechPurpose", "PerformanceNote", "LoyaltyPointDetail",
			"PaymentGatewayRespMsg", "WorkFlowActivityType", "DataCategory",
			"RouteSchedule", "SalesStatement", "BudgetTypeAttr",
			"PartyContent", "OtherDataResource", "PosHistory",
			"ReturnHeaderType", "VendorProduct", "InventoryItemCount",
			"QuoteWorkEffort", "OrderItemChange", "JobSandbox",
			"QuantityBreakType", "EntitySyncInclude", "SalesBonusPolicyRule",
			"EmplLeaveReasonType", "RecurrenceRule", "SalesBonusSummaryType",
			"TimekeepingSummaryDetail", "BudgetRevision",
			"PartyPayrollFormulaInvoiceItemType", "ReturnContactMech",
			"ProductFacilityParameter", "ProductSalesPriceLog",
			"ShippingTripPack", "ContentOperation", "AcctgTransEntryType",
			"SystemProperty", "TechDataCalendarExcDay", "PartyTypeAttr",
			"WorkEffortStatus", "ProductPromoCond", "WorkEffortContent",
			"LabelType", "ContentPurposeType", "FixedAssetIdent",
			"TimeKeepingSignType", "ProductRelationshipType",
			"WorkEffortTypeAttr", "MarketingCost", "Notification",
			"OrderContentType", "ApplicationSetting", "TrackingCodeType",
			"SalesOpportunity", "PayrollParameters", "SettlementTerm",
			"ProductPromoExt", "ProdCatalogCategory",
			"EmplPosTypePartyRelConfig", "KeywordThesaurus",
			"PicklistStatusHistory", "PartyInsuranceSalary",
			"ProductStoreShipmentMeth", "EquipmentPartyAlloc",
			"ShipmentGatewayDhl", "InvoiceItemTypeGlAccount",
			"WebSitePublishPoint", "VisualTheme", "AgreementRole",
			"WorkFlowServiceAppl", "TransferShipment",
			"InsEmplAdjustParticipate", "SkillType", "AcctgTransAttribute",
			"PartyNeed", "SystemConfigType", "CustomerTimePayment",
			"ProductPromoExtRoleTypeAppl", "ProductMeter", "SegmentGroupRole",
			"GlJournal", "ReturnAdjustmentType", "BudgetItemType",
			"InvoiceNote", "Invoice", "FormulaProduct", "SecurityGroup",
			"AcctgTransHistory", "UserLoginSession", "Agreement",
			"AccReportFunction", "OrderBlacklistType", "OrderItemAssocType",
			"CartAbandonedLine", "ServiceSemaphore", "ExternalLogin",
			"SalesOpportunityCompetitor", "BillingAccount", "QuotaItem",
			"ShipmentReceipt", "SupplierPrefOrder", "TempAgent",
			"ReturnItemBilling", "OrderTerm", "AgreementPartyContactMech",
			"PartyCustomer", "ProductAssocType", "QualityAssuranceReason",
			"SgcOrderOffline", "ConfigLabel", "Vendor", "TransferTypeEnum",
			"QuoteTypeAttr", "ReturnStatus", "ProductPromoExtAction",
			"WorkEffortFixedAssetAssign", "ProductRelationship",
			"PaymentGatewaySecurePay", "WorkingShiftWorkType",
			"OrderContentPaymentOrder", "ShoppingCartItemLogLite",
			"StockEventSummary", "PartyResume", "ContactListParty",
			"FixedAssetIncreaseItem", "DeliveryEntryItem",
			"WorkFlowActivityAction", "PartyContactTempData",
			"PartyTaxAuthInfo", "ProductPromoRoleTypeAppl", "ProductQuotation",
			"GlReconciliationEntry", "SurveyQuestionAppl", "ShipmentStatus",
			"InventoryFreezed", "RequirementAttribute", "GlAccountGroupMember",
			"ContentAssocType", "TaxAuthorityCategory", "CustomTimePeriod",
			"SurveyMultiResp", "FixedAssetDecrease", "EntityAuditLog",
			"TrainingResultType", "GoodIdentificationMeasure",
			"CostComponentCalc", "GlReconciliation", "PartyGlAccount",
			"FacilityPartyExchange", "PartyHealthInsurance",
			"PartyAcctgPreference", "OrderShipment", "OrderItemContactMech",
			"TarpittedLoginView", "CostAccounting", "RecruitmentCostItemType",
			"OrderHeader", "AccReportType", "TimeEntry",
			"TaxAuthorityRateRankPayroll", "ProductSearchConstraint",
			"InvoiceItemAssocType", "ProductPriceAutoNotice",
			"AgreementAttribute", "TempCustomer", "TransferHeader",
			"ProductStorePromoExtAppl", "LocationFacility",
			"ProductFeatureApplAttr", "WorkFlowGroupMember",
			"TransferItemShipGroup", "ShipmentVarianceReason",
			"ShipmentBoxType", "PartyIdentification", "PartyPeriodType",
			"WorkEffortDeliverableProd", "EntitySyncRemove",
			"RequirementBudgetAllocation", "TrainingCourse", "RequirementNote",
			"TenantKeyEncryptingKey", "PlanItem", "ProductCategoryContent",
			"WorkFlowActivity", "ProductConfigConfig",
			"CustRequestItemWorkEffort", "WorkEffortContentType",
			"Enumeration", "EquipmentParty", "ProductFeatureCatGrpAppl",
			"UnemploymentClaim", "TrainingCoursePurpose", "WorkEffortReview",
			"PayrollFormulaType", "OrderContactMech", "GeoPoint",
			"PayPalPaymentMethod", "EquipmentAllocItem",
			"ProductStoreFinActSetting", "InvoiceContactMech",
			"ProdConfItemContent", "PickingItemTempData", "ProductSummary",
			"ProductFeatureCategory", "RecruitmentPlanSales",
			"DocumentAttribute", "DeliverySchedule",
			"ProductFacilitySaleHistory", "TimekeepingDetailParty",
			"AudioDataResource", "OrderTermAttribute",
			"ProductCategoryTypeAttr", "MimeType", "InventoryItemLabelAppl",
			"CommEventContentAssoc", "WorkEffort", "PyrllParamPosTypeGeoAppl",
			"OrderNotification", "DataResourceAttribute",
			"AgreementFacilityAppl", "WorkEffortNote", "PicklistRole",
			"ShipmentPackageRouteSeg", "FinAccountTransAttribute",
			"ProductFeatureType", "CarrierShipmentMethod", "NoteData",
			"ProductPlanAndOrder", "TemporalExpression", "ProdCatalog",
			"SalesBonusSummaryParty", "PartyClassification", "TransferStatus",
			"InvoiceTermAttribute", "PosTerminalLog", "ContactList",
			"OrderItemAssoc", "RoleType", "TrainingCourseStatus",
			"GlAccountCategory", "RecruitmentPlanRoundSubject",
			"SurveyResponse", "PrepaidExp", "ContentAssoc",
			"TaxAuthorityGlAccount", "NotificationGroup",
			"GiftCardFulfillment", "EmplTerminationProposal",
			"PaymentGatewayWorldPay", "EmplPosTypeInsuranceSalary",
			"OpponentEvent", "FinAccountAttribute", "PartyRelationshipType",
			"InsBenefitTypeRule", "FormulaParameterType",
			"InventoryItemTypeAttr", "CustRequestContent", "LoyaltyUse",
			"ServerHitBin", "WebSiteContentType", "UserAgentType",
			"MarketingCampaignRole", "OrderHeaderWorkEffort", "SgcOrder",
			"FinAccountTransTypeAttr", "StandardLanguage", "PaymentMethodType",
			"RequirementCustRequest", "WebsiteContentCategory",
			"PosTerminalState", "EntityGroupEntry", "SegmentGroupType",
			"PosTerminalInternTx", "StockEventRole", "AgreementPromoAppl",
			"UserPrefGroupType", "TerminationReason", "X509IssuerProvision",
			"BudgetItem", "EnumerationInvoiceType", "ExternalOrderType",
			"UserAgent", "SgcOrderNegative", "Payment",
			"WorkFlowActivityRoleType", "PaymentGatewayPayflowPro",
			"PosTerminalBank", "ProductPromoCodeParty",
			"ProductPromoSettlementResult", "OrderProductPromoCode",
			"ContentCategoryMember", "FixedAssetType", "AgreementPartyApplic",
			"ContentKeyword", "TransferItem", "ContentApproval",
			"InsuranceContentType", "QuoteTerm", "LoyaltyPoint",
			"ProductPromoExtCategory", "PartyDistributor", "PartyQual",
			"ValidContactMechRole", "MimeTypeHtmlTemplate",
			"PerfCriteriaPolicyItem", "RecruitmentRoundIntvwEval", "UomGroup",
			"ProtocolType", "InsBenefitTypeCond", "InventoryItemType",
			"ProductQuotationModuleType", "DeliveryItem", "ReorderGuideline",
			"PayrollFormula", "PartyNote", "RequirementShipment", "QuotaType",
			"PayrollTable", "PayrollParamCharacteristic", "OrderItemTypeAttr",
			"FixedAssetAlloc", "Visit", "CostBillAccounting",
			"UserLoginPasswordHistory", "PortletAttribute", "PortalPage",
			"WebAnalyticsConfig", "NotificationStatus",
			"WorkFlowRequestAction", "JobInterviewType",
			"StockEventItemStatus", "OrderTypeAttr", "EmplPosTypePerfCri",
			"ProductTypeAttr", "TrainingCourseSkillType", "OrderPathFile",
			"SurveyQuestionOption", "ProductPromoCategory", "OrderItemGroup",
			"Delivery", "PartyContactMech", "FacilityGroupRole",
			"ContactMechAttribute", "EmplPositionTypeRate", "ContactMechLink",
			"OrderAdjustmentTypeAttr", "AccommodationSpot", "ShipmentTypeAttr",
			"SalesOpportunityWorkEffort", "ProductFacilityHistory",
			"GlAccountHistory", "ProductFeature", "Decrement", "LoyaltyAction",
			"WorkEffortBilling", "ShipmentQualityAssurance",
			"WorkEffortPartyAssignment", "PaymentGatewayPayPal",
			"RecurrenceInfo", "InvoiceStatus", "RouteScheduleDetailDate",
			"BudgetAttribute", "ProductPromoExtRule", "SequenceValueItem",
			"ProductGeo", "KeyPerfIndicator", "EmailTemplateSetting",
			"FacilityTypeAttr", "CustRequestItemNote", "ItemIssuanceRole",
			"EmplLeaveType", "ProductStoreBarcode", "DocumentTypeAttr",
			"TimekeepingDetail", "SegmentGroupGeo", "PerfCriteria",
			"VarianceReasonGlAccount", "TrainingClassType", "ContentType",
			"FacilityGroupType", "RecruitmentCandidate",
			"PaymentMethodTypeGlAccount", "FixedAssetIncrease", "PlatformType",
			"WebAnalyticsType", "MetaDataPredicate", "OrderRequirement",
			"Holiday", "PayrollTableRecord", "InventoryLabelChangeReason",
			"RouteInformation", "ProductFacilitySale", "BenefitType",
			"Shipment", "EquipmentIncrease", "FixedAssetMaint",
			"SalesForecastDetail", "PaymentGatewaySagePay", "ShoppingList",
			"ProductFacilitySummary", "ShipmentRouteSegment",
			"PayrollParamPositionType", "StatusItem",
			"WorkShiftJobScheduleConfig", "ProductFeatureGroup", "VoucherBook",
			"CostAccBase", "FacilityCarrierShipment",
			"ProductStoreVendorShipment", "VehicleV2",
			"ProductPriceActionType", "AcctgTransType", "Requirement",
			"MarketingCampaignTypeAttr", "TempOlapGlAccountRelation",
			"ContentTypeMember", "SalesStatementStatus",
			"OrderAdjustmentAttribute", "PaymentApplication",
			"OrderReceiptNote", "QuoteItem", "InsAllowanceBenefitType",
			"FixedAssetIdentType", "TomcatSessions", "RecruitmentRequire",
			"ShipmentGatewayUps", "GlAccountTypeDefault",
			"EquipmentIncreaseItem", "SurveyQuestionType",
			"ShipmentMethodType", "QuoteType", "WorkEffortTransBox",
			"PerfCriteriaPolicy", "GlAccountHistoryAccumulate", "Document",
			"UserAgentMethodType", "SalesBonusType", "WorkFlowAction",
			"DeliveryEntry", "SgcOrderSequence", "JobInterview",
			"WorkFlowRequestStakeHolder", "TrackingCodeVisit",
			"FinAccountType", "Deliverable", "FixedAssetStdCost",
			"ProductAndCatalogTempData", "SalesCommissionData",
			"RequirementStatus", "WorkEffortAssoc", "PaySalaryHistory",
			"SystemConfig", "CustRequestType", "ConfigPrintOrder",
			"GlXbrlClass", "AgreementContent", "MobileDevice",
			"AddressMatchMap", "TaxableType", "PaymentCodeSeqValue",
			"PartyPerfCriteria", "ShipmentTypeEnum",
			"EmplPositionResponsibility", "WorkEffortCostCalc",
			"AgreementEmploymentAppl", "PartyParticipateInsurance",
			"ProductPromoAction", "RecruitmentRequireStatus",
			"SubscriptionFulfillmentPiece", "EquipmentAllocItemParty",
			"EmplPosTypeSecGroupConfig", "ProductPromoCodeEmail",
			"ProductPromoType", "MarketingCampaignAttribute",
			"InsuranceParticipateReport", "ProductAverageCostType",
			"QuotaHeader", "GlAccountType", "OrderItemGroupOrder",
			"PaymentGatewayOrbital", "DeliveryEntryFixedAsset",
			"PartyClassificationGroup", "TemporalExpressionAssoc",
			"PhysicalInventory", "GlAccountOrganization", "PicklistBinStatus",
			"CustRequestItem", "ProductSummaryServices",
			"ProductStoreSurveyAppl", "BillingAccountRole",
			"WorkFlowTransition", "GeoAssoc", "ShoppingCartItemLog",
			"AllocationCostPeriod", "FormulaHistory", "RecruitmentResultType",
			"RecruitmentFormType", "MarketingPlace", "PlanType",
			"InvoiceAttribute", "CustRequestWorkEffort", "SalesBonusPolicyAct",
			"StockEventItemTempData", "ProductQuotationStoreAppl",
			"DocumentCustomsType", "TransferItemType", "TermType",
			"ProductFeatureDataResource", "ProductPromoProduct",
			"PortletPortletCategory", "Hospital", "EntityKeyStore", "Uom",
			"Subscription", "WorkEffortEventReminder", "LoyaltyRule",
			"BankConversion", "PaymentGroupMember", "ProductCategory",
			"RecruitmentSourceType", "TransferItemShipGrpInvRes",
			"ShipmentGatewayUsps", "BudgetStatus", "PerfCriteriaRateGrade",
			"ProductConfig", "DocumentType", "AccClosingEntry",
			"MarketingPlan", "DesiredFeature", "ProductContentType",
			"ProductCategoryAttribute", "OrderAdjustment",
			"BillingAccountTerm", "InventoryItemStatus",
			"OrderInvoiceReference", "FixedAssetProductType", "BudgetRole",
			"ProductPlanType", "EquipmentAllocItemStore", "QuoteRole",
			"CostComponentAttribute", "AgreementTerm", "FixedAssetGeoPoint",
			"MarketingCostType", "GlBudgetXref", "ProductPrice",
			"MarketingCampaign", "Nationality", "InvoiceTypeAttr",
			"QualityPublication", "PayGrade", "BudgetType", "TelecomNumber",
			"PlanHeader", "Affiliate", "StockEvent", "Trip",
			"ProductGlAccount", "CostComponent", "Formula", "MrpEventType",
			"SecurityPermission", "PostalAddress", "FinAccountStatus",
			"ProductCostComponentCalc", "WorkFlowProcessStatus",
			"CustomerProductExpDateInventory", "EnumerationRelType",
			"OrderContent", "InsBenefitTypeFreq", "OrderSummaryEntry",
			"PayHistory", "ProductStoreFacility", "CostComponentTypeAttr",
			"ShipmentType", "InvoiceItemAssoc", "WorkingShiftDayWeek",
			"TechDataCalendar", "ValueLinkKey", "ProductConfigProduct",
			"UomType", "FacilityGroupRollup", "TempOlapCustomTimeRelation",
			"ProductPromoExtStatus", "Lot", "PaymentGatewayConfigType",
			"VideoDataResource", "PlanPurchaseOrderItem",
			"InventoryItemLabelType", "RecruitmentPlan", "CustomMethodType",
			"RecruitmentIntvwStandardEval", "OrderType",
			"InvoiceItemAttribute", "DocumentCustoms", "InvoiceItemType",
			"PayrollScheduleLog", "SubscriptionType", "EmploymentApp",
			"ContactListType", "UomConversionDated", "AgreementType",
			"CatalinaSession", "OrderItem", "SupplierPromoAppl",
			"FixedAssetDecrementItem", "SupplierVehicle", "PortletCategory",
			"PartyCarrierAccount", "PartyCampaignCommEvent",
			"ProductStoreGroupRole", "PayrollEmplParameters", "GeoType",
			"InsuranceOriginateType", "TechDataCalendarWeek",
			"FixedAssetDepreCalcItem", "CustRequestAttribute",
			"ProductPromoExtUse", "ContactMechType", "WebPage",
			"WorkEffortGoodStandard", "ContentRevision",
			"DataResourceMetaData", "FinAccountRole", "StatusValidChange",
			"PartyProfileDefault", "ShipmentCostEstimate",
			"ProductStoreGroupType", "WorkingLateRegisEnum", "PayrollItemType",
			"KeyPerfIndPartyTarget", "PortalPageColumn",
			"ReceiptMoneyEmployeeGlAccount", "PartyRelationDmsLog",
			"OrderAndGeoPoint", "PartyContentType", "ProductGroupOrder",
			"AgreementItem", "GlFiscalType", "ContentAttribute",
			"RecruitmentAnticipateItem", "ProductPromoStatus",
			"SalesOpportunityRole", "InsAllowanceBenefClassTyp",
			"ContentSearchConstraint", "FinAccountTransType",
			"PaymentGatewayiDEAL", "WorkFlowRequest", "Quote",
			"FixedAssetDecreaseItem", "CommunicationEventPrpTyp",
			"OrderItemShipGrpInvRes", "InsuranceReportPartyOriginate",
			"CustRequestTypeAttr", "RecruitmentRequireCond",
			"OlbiusPartyPermission", "WebSiteContent",
			"EmplTimekeepingSignValidCombine", "WorkFlowActionType",
			"EntitySync", "AgreementPartyCTMPurpose", "PriorityType",
			"ProductPromoExtProduct", "WorkFlowTransitionAction",
			"PaymentGatewayResponse", "NeedType", "RequirementRole",
			"HolidayConfig", "FacilityContactMechPurpose",
			"FormulaParameterApply", "InsBenefitLeaveReasonType",
			"TaxAuthorityAssocType", "WorkEffortFixedAssetStd",
			"ConfigOptionProductOption", "PartyQualType",
			"BudgetItemAttribute", "EmailAddressVerification",
			"SalesOpportunityTrckCode", "MarketingContentType",
			"TrackingCodeOrder", "DataResourceType",
			"TransferItemIssuanceRole", "ProductEventItem", "CustRequestNote",
			"OrderAdjustmentBilling", "TaxAuthorityAssoc", "PartyCurrency",
			"FixedAssetDepMethod", "WorkFlowRequestAttr", "RequirementType",
			"Picklist", "ProductCategoryType", "ProductPlanItem",
			"ContactMechTypePurpose", "PersonEducation", "LoyaltyStatus",
			"PortalPortlet", "ProductCacheData", "ContentRole",
			"ValidResponsibility", "QuoteAttribute",
			"WorkOvertimeRegistration", "UserCookie", "DeliveryStatus",
			"ProductFeatureGroupAppl", "WorkingShift",
			"FormulaParameterProductStore", "FormulaParameter", "PaymentGroup",
			"PaidAdvanceShipmentRelation", "WorkEffortAssocType",
			"ContentCategory", "SurveyPage", "ProductStoreWebSiteContentAppl",
			"TaxAuthorityRateTypeGlAccount", "ImageDataTextResource",
			"ShoppingCartLog", "WorkEffortContactMech", "ProductPlanHeader",
			"PaymentGatewayCyberSource", "EnumerationType",
			"SalesBonusSummary", "QuoteTermAttribute",
			"UserLoginSecurityGroup", "RecruitmentRoundCandidate",
			"WorkingShiftEmployee", "SaleType", "PerfCriteriaType",
			"WorkEffortInventoryAssign", "TimekeepingSummary", "Person",
			"FacilityLocation", "PayrollTableRecordParty", "Deduction",
			"MarketingType", "EmplPositionReportingStruct", "ShoppingListType",
			"TempOlapPartyGroupRelation", "OrderPaymentPreference",
			"ContainerType", "PaymentReceipt", "GlAccountBalanceParty",
			"ProductPromoUse", "PaySalaryItemHistory", "StockEventItem",
			"PaymentGatewayEway", "Product", "EquipmentType",
			"ContactListPartyStatus", "CommunicationEvent",
			"MarketingCampaignPrice", "FixedAssetTypeAttr", "VisualThemeSet",
			"ProductMeterType", "PartyPerfCriteriaItemProduct",
			"InventoryItem", "CarrierShipmentBoxType",
			"ProductConfigOptionIactn", "RecruitmentPlanBoard",
			"PayrollCharacteristic", "ShipmentItem", "SupplierRatingType",
			"FixedAsset", "SubscriptionResource", "EmplWorkingLate",
			"SurveyMultiRespColumn", "RecruitmentPlanRoundInterviewer",
			"RecruitRoundCandidateExaminer", "PartyInvitationGroupAssoc",
			"EntityGroup", "BudgetReview", "TechDataCalendarExcWeek",
			"GlAccountGroupType", "ShipmentAttribute", "LoyaltyRoleTypeAppl",
			"ProductFacility", "ProductSocial", "CommunicationEventPurpose",
			"FixedAssetDepreciationCalc", "ProductPriceRule",
			"VatInvoiceOutputTax", "PackingListDetail", "MarketingProduct",
			"Timesheet", "ProductQuotationStatus",
			"WorkRequirementFulfillment", "SurveyTrigger", "CheckInHistory",
			"GlAccountRole", "CustRequestCommEvent", "ShippingTrip",
			"ConfigPacking", "DataTemplateType", "TrackingCodeOrderReturn",
			"PaymentGlAccountTypeMap", "ContentPurpose", "ProdCatalogRole",
			"PartyCampaignRelationship", "Major",
			"PlanPurchaseOrderItemFacility", "ContentMetaData", "PackItem",
			"SurveyResponseAnswer", "AccReport", "HumanResourcePlanning",
			"CustRequestCategory", "ProductPackagingUom", "ProductPriceChange",
			"ContainerItem", "SupplierProductFeature", "DataResourceRole",
			"RespondingParty", "AgreementItemAttribute",
			"CommunicationEventWorkEff", "PayrollEmplParameterType",
			"DeliveryType", "AcctgTrans", "InventoryItemVariance", "Label",
			"AllocationCostPeriodItem", "LoyaltyProduct",
			"KeyPerfIndPartyAppl", "RecruitmentSubject",
			"CreditCardTypeGlAccount", "WorkReqFulfType", "PeriodType",
			"SalesBonusPolicy", "AcctgTransEntryHistory", "StandardTimePeriod",
			"RequirementItemBilling", "OlbiusPartyRelationshipType",
			"SecurityGroupPermission", "SalesForecastHistory", "Route",
			"FacilityParty", "SalesBonusPolicyCond", "ShipmentContactMech",
			"OrderAdjustmentType", "PartySkill", "LoyaltyCategory",
			"FixedAssetMaintMeter", "ZipSalesTaxLookup",
			"RequirementItemAssoc", "Gender", "PartyInvitation",
			"AccRepFuncRepType", "ProductPaymentMethodType", "Employment",
			"ReturnItemTypeMap", "OrderSettlementCommitment",
			"PaymentApplicationStatus", "RequirementEnumType",
			"ReturnAdjustment", "PartyAttribute", "RequirementItemAssocType",
			"DataResourceTypeAttr", "OrderItemType", "GlAccountGroup",
			"ProductCategoryGlAccount", "UomConversion", "ContactMech",
			"ProdCatalogCategoryType", "PersonFamilyBackground",
			"EmplTimesheetAttendance", "OrderItemRole", "EftAccount",
			"WebPreferenceType", "EthnicOrigin", "EmplLeave",
			"LoyaltyCondition", "FormulaType", "InventoryItemLocation",
			"PostalAddressBoundary", "QuoteCoefficient",
			"ShipmentGatewayConfigType", "DeliveryEntryShipment",
			"AccommodationClass", "ProductStoreEmailSetting", "GlResourceType",
			"LocationFacilityType", "WorkOrderItemFulfillment",
			"RecruitmentAnticipate", "RateAmount", "Party",
			"SegmentGroupClassification", "OrderItemShipGroupAssoc",
			"SubscriptionTypeAttr", "CostProductInOrder",
			"RecruitmentPlanConds", "ProductFacilityAttribute",
			"PrepaidExpCustomTimePeriod", "ProductCategoryContentType",
			"RoleTypeGroup", "TransferRole", "ReturnHeader", "GiftCard",
			"AgreementTypeAttr", "DeductionType", "PartyInvitationRoleAssoc",
			"ProductCategoryMember", "TrainingFormType", "PaymentAttribute",
			"ProductMaintType", "MarketingRole", "ContentPurposeOperation",
			"ElectronicText", "SurveyQuestion", "AccommodationMapType",
			"GlAccountCategoryMember", "ShipmentReceiptQA",
			"SuspendInsReasonType", "PaymentGroupType", "TimesheetRole",
			"SalesBonusPolicyType", "SurveyQuestionCategory",
			"DeliveryItemChange", "CustRequest", "OrderStatus", "InvoiceItem",
			"OrderAndContainer", "FixedAssetProduct", "ProductStore",
			"ProductType", "WorkEffortType", "ProductContent",
			"GoodIdentification", "ShipmentItemFeature", "RouteCustomer",
			"PartyDataSource", "ContactListCommStatus", "FacilityGroupMember",
			"OlbiusPermission", "ProductRole", "BrowserType",
			"PrepaidExpAlloc", "ProductFeatureApplType", "EducationSystemType",
			"SurveyApplType", "ProductAssoc", "OrderRole", "AccReportTarget",
			"ProductPricePurpose", "ProductFeatureIactnType",
			"WebSitePathAlias", "FinAccount", "RecruitmentReqCondType",
			"UserLogin", "TrainingRequest", "ProductFacilityLocation",
			"BillOfLading", "ProductPromoSettlementDetail", "PicklistBin",
			"BillingAccountTermAttr", "WorkOvertimeRegisEnum",
			"SubscriptionCommEvent", "AgreementProductAppl",
			"ShippingTripInvoice" };

	String[][] R = { { "OrderHeader", "orderId", "Delivery", "orderId" },
			{ "OrderHeader", "orderId", "OrderAdjustment", "orderId" },
			{ "OrderHeader", "orderId", "OrderItem,", "orderId" },

			{ "OrderItem", "", "ShipmentReceipt" }, };

	HashMap<String, List<Rel>> mTable2SubTables;
	public static SCCAnalyzer sa = null;
	public static HashSet<String> emptyTables = new HashSet<String>();
	public static HashSet<String> notTableSet = null;
	public static HashSet<String> tableSet = null;

	/*
	 * public static GenericValue findOne(Delegator delegator, String tableName,
	 * List<String> pkNames, List<Object> pkValues) { try { GenericValue g =
	 * null; if (pkNames.size() == 1) { g = delegator.findOne(tableName,
	 * UtilMisc.toMap(pkNames.get(0), pkValues.get(0)), false);
	 * 
	 * } else if (pkNames.size() == 2) { g = delegator.findOne( tableName,
	 * UtilMisc.toMap(pkNames.get(0), pkValues.get(0), pkNames.get(1),
	 * pkValues.get(1)), false);
	 * 
	 * } else if (pkNames.size() == 3) { g = delegator.findOne(tableName,
	 * UtilMisc.toMap(pkNames.get(0), pkValues.get(0), pkNames.get(1),
	 * pkValues.get(1), pkNames.get(2), pkValues.get(2)
	 * 
	 * ), false);
	 * 
	 * } else if (pkNames.size() == 4) { g = delegator.findOne(tableName,
	 * UtilMisc.toMap(pkNames.get(0), pkValues.get(0), pkNames.get(1),
	 * pkValues.get(1), pkNames.get(2), pkValues.get(2), pkNames.get(3),
	 * pkValues.get(3)
	 * 
	 * ), false);
	 * 
	 * } else if (pkNames.size() == 5) { g = delegator.findOne(tableName,
	 * UtilMisc.toMap(pkNames.get(0), pkValues.get(0), pkNames.get(1),
	 * pkValues.get(1), pkNames.get(2), pkValues.get(2), pkNames.get(3),
	 * pkValues.get(3), pkNames.get(4), pkValues.get(4)
	 * 
	 * ), false); } return g; } catch (Exception ex) { ex.printStackTrace();
	 * return null; } }
	 */
	public static boolean isNotTable(String tblName) {
		if (notTableSet == null) {
			notTableSet = new HashSet<String>();
			for (int i = 0; i < notTables.length; i++)
				notTableSet.add(notTables[i]);
		}
		return notTableSet.contains(tblName);
	}

	public static boolean isTable(String tblName) {
		if (tableSet == null) {
			tableSet = new HashSet<String>();
			for (int i = 0; i < tables.length; i++)
				tableSet.add(tables[i]);
		}
		return tableSet.contains(tblName);
	}

	public static GenericValue findOne(Delegator delegator, String tableName,
			List<String> pkNames, DataStrings pkValues) {
		try {
			GenericValue g = null;
			if (pkNames.size() == 1) {
				g = delegator.findOne(tableName,
						UtilMisc.toMap(pkNames.get(0), pkValues.get(0)), false);

			} else if (pkNames.size() == 2) {
				g = delegator.findOne(
						tableName,
						UtilMisc.toMap(pkNames.get(0), pkValues.get(0),
								pkNames.get(1), pkValues.get(1)), false);

			} else if (pkNames.size() == 3) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2)

				), false);

			} else if (pkNames.size() == 4) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3)

				), false);

			} else if (pkNames.size() == 5) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4)

				), false);
			}
			return g;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static GenericValue findOne(Delegator delegator, String tableName,
			List<String> pkNames, List<Object> pkValues) {
		try {
			GenericValue g = null;
			if (pkNames.size() == 1) {
				g = delegator.findOne(tableName,
						UtilMisc.toMap(pkNames.get(0), pkValues.get(0)), false);

			} else if (pkNames.size() == 2) {
				g = delegator.findOne(
						tableName,
						UtilMisc.toMap(pkNames.get(0), pkValues.get(0),
								pkNames.get(1), pkValues.get(1)), false);

			} else if (pkNames.size() == 3) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2)

				), false);

			} else if (pkNames.size() == 4) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3)

				), false);

			} else if (pkNames.size() == 5) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4)),
						false);
			} else if (pkNames.size() == 6) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4),
						pkNames.get(5), pkValues.get(5)), false);
			} else if (pkNames.size() == 7) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4),
						pkNames.get(5), pkValues.get(5), pkNames.get(6),
						pkValues.get(6)), false);
			} else if (pkNames.size() == 8) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4),
						pkNames.get(5), pkValues.get(5), pkNames.get(6),
						pkValues.get(6), pkNames.get(7), pkValues.get(7)),
						false);
			} else if (pkNames.size() == 9) {
				g = delegator.findOne(tableName, UtilMisc.toMap(pkNames.get(0),
						pkValues.get(0), pkNames.get(1), pkValues.get(1),
						pkNames.get(2), pkValues.get(2), pkNames.get(3),
						pkValues.get(3), pkNames.get(4), pkValues.get(4),
						pkNames.get(5), pkValues.get(5), pkNames.get(6),
						pkValues.get(6), pkNames.get(7), pkValues.get(7),
						pkNames.get(8), pkValues.get(8)), false);
			}

			return g;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void updateTables(Delegator delegator, String tableName,
			List<UPDATEINFO> infos) {
		try {
			/*
			 * for (UPDATEINFO info : infos) { GenericValue g =
			 * findOne(delegator, tableName, info.pkNames, info.pkValues);
			 * 
			 * if (g != null) { for (int i = 0; i < info.fieldNames.size(); i++)
			 * { g.put(info.fieldNames.get(i), info.fieldValues.get(i)); }
			 * delegator.store(g); } }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Timestamp getLastUpdatedStamp(Delegator delegator) {
		Timestamp lastUpdated = null;

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse("2018-11-21 08:50:51.541");
			lastUpdated = new java.sql.Timestamp(parsedDate.getTime());
			if (true)
				return lastUpdated;

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("job",
					EntityOperator.EQUALS, "fact/PurchaseOrderFact.ktr"));

			List<GenericValue> lst = delegator.findList("SchedulePentaho",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			for (GenericValue p : lst) {
				Timestamp lu = p.getTimestamp("lastUpdated");
				Debug.log(module + "::getLastUpdatedStamp, lu = " + lu);
				if (lastUpdated == null)
					lastUpdated = lu;
				else {
					if (lu.after(lastUpdated))
						lastUpdated = lu;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastUpdated;
	}

	public static boolean exists(Delegator delegator, String tableName,
			List<String> primaryKeyNames, List<Object> primaryKeyValues) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			for (int i = 0; i < primaryKeyNames.size(); i++)
				if (primaryKeyNames.get(i) != null) {
					conds.add(EntityCondition.makeCondition(
							primaryKeyNames.get(i), EntityOperator.EQUALS,
							primaryKeyValues.get(i)));
				}
			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst != null && lst.size() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static List<DataStrings> getPrimaryKeyValues(Delegator delegator,
			String tableName, List<String> frkNames, DataStrings frkValues) {
		List<DataStrings> retList = FastList.newInstance();
		try {

			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<EntityCondition> conds = FastList.newInstance();
			if (frkNames != null) {
				for (int i = 0; i < frkNames.size(); i++) {
					conds.add(EntityCondition.makeCondition(frkNames.get(i),
							EntityOperator.EQUALS, frkValues.get(i)));
				}
			}
			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			for (GenericValue gv : lst) {
				DataStrings L = new DataStrings();
				for (int i = 0; i < tbl.getPkFieldNames().size(); i++) {
					String v = gv.getString(tbl.getPkFieldNames().get(i));
					L.add(v);
				}
				retList.add(L);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retList;
	}

	public static List<DataStrings> getPrimaryKeyValues(Delegator delegator,
			String tableName, List<String> frkNames, List<String> frkValues) {
		List<DataStrings> retList = FastList.newInstance();
		try {

			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<EntityCondition> conds = FastList.newInstance();
			if (frkNames != null) {
				for (int i = 0; i < frkNames.size(); i++) {
					conds.add(EntityCondition.makeCondition(frkNames.get(i),
							EntityOperator.EQUALS, frkValues.get(i)));
				}
			}
			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			for (GenericValue gv : lst) {
				DataStrings L = new DataStrings();
				for (int i = 0; i < tbl.getPkFieldNames().size(); i++) {
					String v = gv.getString(tbl.getPkFieldNames().get(i));
					L.add(v);
				}
				retList.add(L);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retList;
	}

	public static Map<String, Object> setNullForeignKeyValues(
			DispatchContext ctx, Map<String, ? extends Object> context) {

		try {
			Delegator delegator = ctx.getDelegator();

			List<Object> table = (List<Object>) context.get("table");
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			for (Object o : table) {
				TablePrimaryKeyValue t = (TablePrimaryKeyValue) o;
				setNULLForeighKeyValues(t.tableName, t.gv, delegator);
			}
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}

	}

	public static void setNULLForeighKeyValues(String tableName,
			GenericValue gv, Delegator delegator) {
		try {
			ModelEntity me = delegator.getModelEntity(tableName);
			List<String> pkNames = me.getPkFieldNames();
			Set<String> fkNames = getFkNamesExclusivePkNames(tableName,
					delegator);
			Debug.log(module + "::setNULLForeighKeyValues, table " + tableName
					+ ", fkNames = " + fkNames.size());
			for (String f : fkNames) {
				Debug.log(module + "::setNULLForeighKeyValues, table "
						+ tableName + ", set null field " + f + " value = "
						+ gv.get(f));
				gv.put(f, null);
			}
			delegator.storeIgnoreECA(gv);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<GenericValue> getRecordsWithForeignKeyValues(
			Delegator delegator, String tableName, List<String> frkNames,
			List<Object> frkValues) {
		// List<DataStrings> retList = FastList.newInstance();
		try {

			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<EntityCondition> conds = FastList.newInstance();
			if (frkNames != null) {
				for (int i = 0; i < frkNames.size(); i++) {
					conds.add(EntityCondition.makeCondition(frkNames.get(i),
							EntityOperator.EQUALS, frkValues.get(i)));
				}
			}
			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static List<GenericValue> getRecordsWithForeignKeyValues(
			Delegator delegator, String tableName, List<String> frkNames,
			List<Object> frkValues, int maxSz) {
		// List<DataStrings> retList = FastList.newInstance();
		try {
			if(maxSz == -1){
				return getRecordsWithForeignKeyValues(delegator, tableName, frkNames, frkValues);
			}
			
			String info_cond = "";
			List<EntityCondition> conds = FastList.newInstance();
			if (frkNames != null) {
				for (int i = 0; i < frkNames.size(); i++) {
					conds.add(EntityCondition.makeCondition(
							frkNames.get(i), EntityOperator.EQUALS,
							frkValues.get(i)));
					info_cond += "[" + frkNames.get(i) + "," + frkValues.get(i) + "], ";
				}
			}
			EntityCondition condition = EntityCondition
					.makeCondition(conds);
			Long totalRows = delegator.findCountByCondition(
					tableName, condition, null, null, null);
			
			
			EntityFindOptions opts = new EntityFindOptions();
			int pageSz = maxSz;

			long nbPages = totalRows / pageSz;
			if (totalRows % pageSz != 0)
				nbPages++;
			int cnt = 0;
				opts.setLimit(pageSz);
				opts.setOffset(0);

				EntityListIterator listIterator = delegator.find(
						tableName, condition, null, null, null,
						opts);

				List<GenericValue> lst = listIterator.getCompleteList();
				
				if (listIterator != null)
					listIterator.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static List<GenericValue> getRecordsWithForeignKeyValues(
			Delegator delegator, String tableName, List<String> frkNames,
			DataStrings frkValues) {
		// List<DataStrings> retList = FastList.newInstance();
		try {

			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<EntityCondition> conds = FastList.newInstance();
			if (frkNames != null) {
				for (int i = 0; i < frkNames.size(); i++) {
					conds.add(EntityCondition.makeCondition(frkNames.get(i),
							EntityOperator.EQUALS, frkValues.get(i)));
				}
			}
			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			return lst;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void backup(Object[] primaryKeys, String tableName,
			String[] foreignKeyNames, Delegator delegator,
			Delegator delegatorbkp, Map<String, List<Rel>> mTable2SubTables,
			Map<String, String> mShipmentId2DeliveryId,
			Map<String, String> mDeliveryId2ShipmentId,
			Set<String> shipmentIds, Set<String> deliveryIds) {
		Debug.log(module + "::backup, table " + tableName + ", foreignKeys = ");
		for (int i = 0; i < foreignKeyNames.length; i++)
			Debug.log(foreignKeyNames[i] + " = " + primaryKeys[i]);
		try {
			ModelEntity tableModel = delegator.getModelEntity(tableName);
			List<String> F = tableModel.getAllFieldNames();

			List<EntityCondition> conds = FastList.newInstance();
			for (int i = 0; i < primaryKeys.length; i++)
				if (primaryKeys[i] != null) {
					conds.add(EntityCondition.makeCondition(foreignKeyNames[i],
							EntityOperator.EQUALS, primaryKeys[i]));
				}

			List<GenericValue> lst = delegator.findList(tableName,
					EntityCondition.makeCondition(conds), null, null, null,
					false);
			for (GenericValue gv : lst) {
				List<String> pkFieldNames = tableModel.getPkFieldNames();
				List<Object> pkValues = FastList.newInstance();
				for (int i = 0; i < pkFieldNames.size(); i++) {
					pkValues.add(gv.get(pkFieldNames.get(i)));
				}

				boolean create = !exists(delegatorbkp, tableName, pkFieldNames,
						pkValues);
				if (tableName.equals("Delivery")
						|| tableName.equals("Shipment"))
					create = false;
				if (create) {
					GenericValue gvb = delegatorbkp.makeValue(tableName);
					for (String f : F) {
						// if (gv.get(f) != null)
						gvb.put(f, gv.get(f));

					}
					/*
					 * if (tableName.equals("Delivery")) { gvb.put("shipmentId",
					 * null);
					 * mDeliveryId2ShipmentId.put(gv.getString("deliveryId"),
					 * gv.getString("shipmentId"));
					 * deliveryIds.add(gv.getString("deliveryId")); } else if
					 * (tableName.equals("Shipment")) { gvb.put("deliveryId",
					 * null);
					 * mShipmentId2DeliveryId.put(gv.getString("shipmentId"),
					 * gv.getString("deliveryId"));
					 * shipmentIds.add(gv.getString("shipmentId")); }
					 */
					delegatorbkp.create(gvb);
					Debug.log(module + "::backup, CREATED " + tableName);
				}
				if (tableName.equals("Delivery")) {
					mDeliveryId2ShipmentId.put(gv.getString("deliveryId"),
							gv.getString("shipmentId"));
					deliveryIds.add(gv.getString("deliveryId"));
				} else if (tableName.equals("Shipment")) {
					mShipmentId2DeliveryId.put(gv.getString("shipmentId"),
							gv.getString("deliveryId"));
					shipmentIds.add(gv.getString("shipmentId"));
				}

				List<Rel> subTables = mTable2SubTables.get(tableName);
				if (subTables != null)
					for (Rel r : subTables) {
						Object[] prk = new String[r.primaryKeyNames.length];
						for (int i = 0; i < r.primaryKeyNames.length; i++) {
							prk[i] = gv.get(r.primaryKeyNames[i]);
						}
						backup(prk, r.tableName, r.foreignKeyNames, delegator,
								delegatorbkp, mTable2SubTables,
								mShipmentId2DeliveryId, mDeliveryId2ShipmentId,
								shipmentIds, deliveryIds);
					}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void updateDelivery(Set<String> deliveryIds,
			Map<String, String> mDeliveryId2ShipmentId, Delegator delegator) {
		try {
			if (deliveryIds != null) {
				for (String deliveryId : deliveryIds) {
					GenericValue gv = delegator.findOne("Delivery",
							UtilMisc.toMap("deliveryId", deliveryId), false);
					if (gv != null) {
						gv.put("shipmentId",
								mDeliveryId2ShipmentId.get(deliveryId));
						Debug.log(module + "::updateDelivery, shipmentId of "
								+ deliveryId + " = "
								+ mDeliveryId2ShipmentId.get(deliveryId));

						delegator.store(gv);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void updateShipment(Set<String> shipmentIds,
			Map<String, String> mShipmentId2DeliveryId, Delegator delegator) {
		try {
			if (shipmentIds != null) {
				for (String shipmentId : shipmentIds) {
					GenericValue gv = delegator.findOne("Shipment",
							UtilMisc.toMap("shipmentId", shipmentId), false);
					if (gv != null) {
						gv.put("deliveryId",
								mShipmentId2DeliveryId.get(shipmentId));
						Debug.log(module + "::updateShipment, deliveryId of "
								+ shipmentId + " = "
								+ mShipmentId2DeliveryId.get(shipmentId));

						delegator.store(gv);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Map<String, Object> backupDelivery(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		try {
			Delegator delegator = ctx.getDelegator();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			ModelEntity tableModel = delegator.getModelEntity("Delivery");
			List<String> F = tableModel.getAllFieldNames();

			List<String> orderIds = (List<String>) context.get("orderIds");
			Set<String> setOrderIds = FastSet.newInstance();
			for (String orderId : orderIds)
				setOrderIds.add(orderId);

			Map<String, Object> retSucc = ServiceUtil.returnSuccess();

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("orderId",
					EntityOperator.IN, setOrderIds));
			List<GenericValue> deliveries = delegator.findList("Delivery",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			int cnt = 0;
			for (GenericValue d : deliveries) {
				String deliveryId = d.getString("deliveryId");
				GenericValue db = delegatorbkp.findOne("Delivery",
						UtilMisc.toMap("deliveryId", deliveryId), false);
				if (db != null)
					continue;
				db = delegatorbkp.makeValue("Delivery");
				for (String f : F) {
					db.put(f, d.get(f));
				}
				db.put("shipmentId", null);
				delegatorbkp.create(db);
				cnt++;
			}
			retSucc.put("total", cnt + "");
			return retSucc;

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> backupShipment(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		try {
			Delegator delegator = ctx.getDelegator();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			ModelEntity tableModel = delegator.getModelEntity("Shipment");
			List<String> F = tableModel.getAllFieldNames();

			List<String> orderIds = (List<String>) context.get("orderIds");
			Set<String> setOrderIds = FastSet.newInstance();
			for (String orderId : orderIds)
				setOrderIds.add(orderId);

			Map<String, Object> retSucc = ServiceUtil.returnSuccess();

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("primaryOrderId",
					EntityOperator.IN, setOrderIds));
			List<GenericValue> shipments = delegator.findList("Shipment",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			int cnt = 0;
			for (GenericValue s : shipments) {
				String shipmentId = s.getString("shipmentId");
				GenericValue sb = delegatorbkp.findOne("Shipment",
						UtilMisc.toMap("shipmentId", shipmentId), false);
				if (sb != null)
					continue;
				sb = delegatorbkp.makeValue("Shipment");
				for (String f : F) {
					sb.put(f, s.get(f));
				}
				sb.put("deliveryId", null);
				delegatorbkp.create(sb);
				cnt++;
			}
			retSucc.put("total", cnt + "");
			return retSucc;

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> backupOrders(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		try {
			Delegator delegator = ctx.getDelegator();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			ModelEntity tableModel = delegator.getModelEntity("OrderHeader");
			List<String> F = tableModel.getAllFieldNames();

			List<String> orderIds = (List<String>) context.get("orderIds");
			Set<String> setOrderIds = FastSet.newInstance();
			for (String orderId : orderIds)
				setOrderIds.add(orderId);

			Map<String, Object> retSucc = ServiceUtil.returnSuccess();

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("orderId",
					EntityOperator.IN, setOrderIds));
			List<GenericValue> orders = delegator.findList("OrderHeader",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			int cnt = 0;
			for (GenericValue d : orders) {
				String orderId = d.getString("orderId");
				GenericValue db = delegatorbkp.findOne("OrderHeader",
						UtilMisc.toMap("orderId", orderId), false);
				if (db != null)
					continue;
				db = delegatorbkp.makeValue("OrderHeader");
				for (String f : F) {
					db.put(f, d.get(f));
				}
				delegatorbkp.create(db);
				cnt++;
			}
			retSucc.put("total", cnt + "");
			return retSucc;

		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Set<String> getFkNamesExclusivePkNames(String tableName,
			Delegator delegator) {
		try {
			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<String> pkNames = tbl.getPkFieldNames();
			List<ModelRelation> rel = tbl.getRelationsOneList();

			Set<String> fkNames = FastSet.newInstance();
			for (ModelRelation mr : rel) {
				for (ModelKeyMap mkm : mr.getKeyMaps())
					if (!pkNames.contains(mkm.getFieldName())) {
						fkNames.add(mkm.getFieldName());

					}
			}
			return fkNames;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String setNullForeignKeys(String tableName,
			Delegator delegator) {
		try {
			ModelEntity tbl = delegator.getModelEntity(tableName);
			List<String> pkNames = tbl.getPkFieldNames();
			List<ModelRelation> rel = tbl.getRelationsOneList();
			List<EntityCondition> conds = FastList.newInstance();
			// List<GenericValue> lst = null;FastList.newInstance();
			try {
				if (!tableExists(tableName)) {
					Debug.log(module + "::setNullForeignKeys, table "
							+ tableName + " NOT EXISTS???");
					return "N/A";
				}
				String rs = "";
				Set<String> fkNames = FastSet.newInstance();
				for (ModelRelation mr : rel) {
					for (ModelKeyMap mkm : mr.getKeyMaps())
						if (!pkNames.contains(mkm.getFieldName())) {
							fkNames.add(mkm.getFieldName());
							rs = rs + mkm.getFieldName() + ", ";
						}
				}
				Debug.log(module + "::setNullForeignKeys, table " + tableName
						+ ", fkNames = " + rs);

				Long totalRows = delegator.findCountByCondition(tableName,
						null, null, null, null);
				EntityFindOptions opts = new EntityFindOptions();
				int pageSz = PAGE_SZ;

				long nbPages = totalRows / pageSz;
				if (totalRows % pageSz != 0)
					nbPages++;

				for (int idx = 0; idx < nbPages; idx++) {
					opts.setLimit(pageSz);
					opts.setOffset(idx * pageSz);
					EntityListIterator listIterator = delegator.find(tableName,
							null, null, null, null, opts);

					List<GenericValue> lst = null;
					// if(idx == 0) lst = listIterator.getPartialList(0,
					// pageSz);
					// else lst = listIterator.getPartialList(idx*pageSz + 1,
					// pageSz);
					lst = listIterator.getCompleteList();

					Debug.log(module + "::setNullForeignKeys, table "
							+ tableName + ", index = " + idx + "/" + nbPages
							+ ", size data = " + lst.size() + ", relation = "
							+ rel.size() + ", fkNames = " + rs);
					for (GenericValue gv : lst) {
						String pkValues = "";
						for (String pkName : pkNames) {
							pkValues = pkValues + gv.get(pkName) + ",";
						}
						for (String fkN : fkNames) {

							gv.put(fkN, null);
							Debug.log(module
									+ "::setNullForeignKeys, set null field "
									+ fkN + " of table " + tableName
									+ " with PK " + pkValues);
						}
						delegator.storeIgnoreECA(gv);

					}
					// delegator.storeAll(lst);

					if (listIterator != null)
						listIterator.close();
				}

				return rs;

			} catch (GenericEntityException ge) {
				ge.printStackTrace();
				Debug.log(module + "::setNullForeignKeys table " + tableName
						+ " EXCEPTION");
			}

		} catch (Exception ex) {
			// ex.printStackTrace();
			Debug.log(module + "::setNullForeignKeys table " + tableName
					+ " EXCEPTION");
			return "";
		}
		return "";
	}

	public static long deleteDataOneTableDBPass(String tableName,
			Delegator delegator) {
		try {
			if (tableExists(tableName)) {
				long count = delegator.findCountByCondition(tableName, null,
						null, null);
				delegator.removeAll(tableName);
				return count;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return 0;
	}

	public static String removeAllDataTable(String tableName,
			Delegator delegator, DispatchContext ctx, boolean reallyRemove) {
		try {
			if (tableExists(tableName)) {
				String rs = "";
				Map<String, Object> in = FastMap.newInstance();
				in.put("tableName", tableName);
				in.put("accumulate", "Y");
				LocalDispatcher dispatcher = ctx.getDispatcher();

				return rs;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public static boolean checkNullForeignKey(List<String> fkNames,
			String tblName, Delegator delegatorbkp) {
		try {
			List<EntityCondition> conds = FastList.newInstance();
			for (String fkn : fkNames) {
				conds.add(EntityCondition.makeCondition(fkn,
						EntityOperator.NOT_EQUAL, null));
			}
			long cnt = delegatorbkp.findCountByCondition(tblName,
					EntityCondition.makeCondition(conds), null, null);
			return cnt == 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static Map<String, Object> removeDataOneTableDBPassMain(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String tableName = (String) context.get("tableName");
			String reallyRemove = (String) context.get("reallyRemove");

			nbRemovedRecords = 0;
			nbRemovedTables = 0;
			t0 = System.currentTimeMillis();
			emptyTables.clear();

			Map<String, Object> in = FastMap.newInstance();
			in.put("tableName", tableName);
			in.put("reallyRemove", reallyRemove);
			Map<String, Object> retSucc = dispatcher.runSync(
					"removeDataOneTableDBPass", in);
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}

	}

	public static Map<String, Object> removeDataOneTableDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			retSucc.put("result", "N/A");
			double t = System.currentTimeMillis() - t0;
			t = t * 0.001;
			// if (t > 60) {
			// Debug.log(module + "::removeDataOneTableDBPass, time = " + t
			// + " EXPIRED, return");
			// return retSucc;
			// }

			String tableName = (String) context.get("tableName");
			String s_reallyRemove = (String) context.get("reallyRemove");
			boolean reallyRemove = false;
			if (s_reallyRemove != null)
				if (s_reallyRemove.equals("Y"))
					reallyRemove = true;

			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			if (!tableExists(tableName)) {
				retSucc.put("result", "table " + tableName + " NOT EXISTS");
				return retSucc;
			}

			long count = delegatorbkp.findCountByCondition(tableName, null,
					null, null);
			if (count == 0) {
				Debug.log(module + "::removeDataOneTableDBPass, time = " + t
						+ ", table " + tableName + " ALREADY EMPTY");
				retSucc.put("result", "SUCCESS");
				return retSucc;
			}
			ModelEntity entity = delegatorbkp.getModelEntity(tableName);
			List<String> pkNames = entity.getPkFieldNames();
			List<GenericValue> lst = delegatorbkp.findList(tableName, null,
					null, null, null, false);
			int cntf = 0;
			for (GenericValue g : lst) {
				List<Object> pkValues = FastList.newInstance();
				for (String pk : pkNames)
					pkValues.add(g.get(pk));
				Map<String, Object> in = FastMap.newInstance();
				in.put("dmspass", "Y");
				in.put("tableName", tableName);
				in.put("pkValueList", pkValues);
				Map<String, Object> ret = dispatcher.runSync(
						"removeRecordAndDependedRecords", in);
				cntf++;
				Debug.log(module + "::removeDataOneTableDBPass FINISHED "
						+ cntf + "/" + lst.size());
			}

			if (true)
				return retSucc;

			Delegator delegator = ctx.getDelegator();
			// LocalDispatcher dispatcher = ctx.getDispatcher();

			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}
			int idx = sa.mEntityName2Index.get(tableName);
			boolean nullKey = true;
			for (int v : sa.A[idx]) {
				String tblName = sa.entityNames[v];
				List<String> fkNames = sa.getFieldNames(tableName, tblName);
				if (!checkNullForeignKey(fkNames, tblName, delegatorbkp)) {
					nullKey = false;
				}
			}

			if (nullKey) {// can delete from table tableName
				Debug.log(module + "::removeDataOneTableDBPass, time = " + t
						+ ", table " + tableName
						+ " NOT REFERENCED, CAN BE DELETED");
				count = deleteDataOneTableDBPass(tableName, delegatorbkp);
				nbRemovedRecords += count;
				emptyTables.add(tableName);

				Debug.log(module + "::removeDataOneTableDBPass, table "
						+ tableName
						+ " NOT REFERENCED, DELETE FINISHED, count = " + count
						+ ", nbRemovedRecords = " + nbRemovedRecords
						+ ", emptyTables = " + emptyTables.size());

				retSucc.put("result", "SUCCESS");
				return retSucc;
			}

			String tables = "";
			int cnt = 0;
			Map<String, Object> in = FastMap.newInstance();
			if (sa.mEntityName2Index.get(tableName) != null) {
				String[] TBL = sa.findSuccessors(tableName);
				if (TBL != null && TBL.length > 0) {
					for (int i = 0; i < TBL.length; i++) {
						long c = delegatorbkp.findCountByCondition(TBL[i],
								null, null, null);
						if (c == 0) {
							Debug.log(module + "::removeDataOneTableDBPass("
									+ tableName + "), time = " + t
									+ ", table TBL[" + i + "] = " + TBL[i]
									+ " ALREADY EMPTY, continue");
							continue;
						}

						if (emptyTables.contains(TBL[i])) {
							Debug.log(module + "::removeDataOneTableDBPass("
									+ tableName + ") TBL[" + i + "] = "
									+ TBL[i] + " EMPTY, continue, time = " + t);
							continue;
						}
						in.put("reallyRemove", s_reallyRemove);
						in.put("tableName", TBL[i]);
						dispatcher.runSync("removeDataOneTableDBPass", in);
						Debug.log(module + "::removeDataOneTableDBPass("
								+ tableName + "), time = " + t + ", removed "
								+ i + "/" + TBL.length + " successor " + TBL[i]);
					}
					cnt = TBL.length;
				}
			}
			Debug.log(module + "::removeDataOneTableDBPass, time = " + t
					+ ", table " + tableName + ", REMOVED " + cnt
					+ " successors, START");
			count = deleteDataOneTableDBPass(tableName, delegatorbkp);
			nbRemovedRecords += count;

			emptyTables.add(tableName);
			Debug.log(module + "::removeDataOneTableDBPass, table " + tableName
					+ " FINISHED, count = " + count + ", nbRemovedRecords = "
					+ nbRemovedRecords + ", emptyTables = "
					+ emptyTables.size());

			// removeAllDataTable(tableName, delegatorbkp);

			retSucc.put("result", "SUCCESS");
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> getTableSizes(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		try {

			String dmspass = (String) context.get("dmspass");

			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			// Delegator delegatorbkp = DelegatorFactory
			// .getDelegator("backuppass");
			Delegator delegator = ctx.getDelegator();
			if (dmspass != null && dmspass.equals("Y")) {
				delegator = DelegatorFactory.getDelegator("backuppass");
				Debug.log(module + "::getTableSizes, use dmspass");
			}
			// if (sa == null) {
			// sa = new SCCAnalyzer(delegator);
			// sa.analyze();
			// sa.computeTOPO();
			// }
			SCCAnalyzer sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();

			String notTable = "";
			String tables = "";
			int nbTables = 0;
			int nbNotTables = 0;
			String rs = "";
			int cnt = 0;
			for (int i = 0; i < sa.entityNames.length; i++) {
				String tableName = sa.entityNames[i];
				if (!tableExists(tableName)) {
					// Debug.log(module + "::getTableSizes, table " + i + "/" +
					// sa.entityNames.length + " " +
					// tableName + " NOT EXISTS");
					notTable += "\"" + tableName + "\",";
					nbNotTables++;
					continue;
				} else {
					// Debug.log(module + "::getTableSizes, table " + i + "/" +
					// sa.entityNames.length + " " +
					// tableName + " EXISTS");
					tables += "\"" + tableName + "\",";
					nbTables++;
				}

				long sz = delegator.findCountByCondition(tableName, null, null,
						null);
				if (sz > 0) {
					cnt++;
					rs = rs + "[" + cnt + "] " + tableName + "(" + sz + "), ";
					Debug.log(module + "::getTableSizes, table " + cnt + " "
							+ tableName + ", sz = " + sz);
				}
			}
			Debug.log(module + "::getTableSizes, notTable = [" + nbNotTables
					+ "] " + notTable);
			Debug.log(module + "::getTableSizes, tables = [" + nbTables + "] "
					+ tables);

			retSucc.put("total", rs);
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> deleteDataOneTableDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		try {
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			String tableName = (String) context.get("tableName");
			String s_reallyRemove = (String) context.get("reallyRemove");

			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			deleteDataOneTableDBPass(tableName, delegatorbkp);

			retSucc.put("result", "SUCCESS");
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> getSSCIndexOfTable(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		try {
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			String tableName = (String) context.get("tableName");
			Delegator delegator = ctx.getDelegator();
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}
			if (sa.mEntityName2Index.get(tableName) == null) {
				retSucc.put("result", "NOT FOUND");
				return retSucc;
			}
			int idx = sa.mEntityName2Index.get(tableName);
			String tbls = "";
			for (String t : sa.entitySCC[sa.scc[idx] - 1])
				tbls = tbls + t + ", ";
			retSucc.put("result", "indexOfTable " + tableName + " = " + idx
					+ ", ssc = " + (sa.scc[idx] - 1) + ", indexTOPO = "
					+ sa.indexTOPO[sa.scc[idx] - 1] + ", entities = " + tbls);

			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Map<String, Object> getDependedTables(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		try {
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			String tableName = (String) context.get("tableName");
			String accumulate = (String) context.get("accumulate");

			Delegator delegator = ctx.getDelegator();
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}

			// ModelEntity entity = delegator.getModelEntity(tableName);
			// for(ModelRelation mr: entity.getRelationsOneList()){
			// Debug.log(module + "::getDependedTables table " + tableName +
			// ", " + mr.getRelEntityName());
			// }

			String tables = "";
			if (sa.mEntityName2Index.get(tableName) != null) {
				int cnt = 0;
				if (accumulate != null && accumulate.equals("Y")) {
					String[] TBL = sa.findSuccessors(tableName);
					for (int i = 0; i < TBL.length; i++) {
						int idx = sa.mEntityName2Index.get(TBL[i]);
						int scc = sa.scc[idx];
						int idxTOPO = sa.indexTOPO[scc - 1];

						tables = tables + "[" + TBL[i] + "," + (scc - 1) + ","
								+ idxTOPO + "], ";
						cnt++;
					}
				} else {
					int v = sa.mEntityName2Index.get(tableName);
					for (int u : sa.A[v]) {
						int idx = sa.mEntityName2Index.get(sa.entityNames[u]);
						int scc = sa.scc[idx];
						int idxTOPO = sa.indexTOPO[scc - 1];
						tables = tables + "[" + sa.entityNames[u] + ","
								+ (scc - 1) + "," + idxTOPO + "], ";
						// Debug.log(module + "::getDependedTables of (" +
						// tableName + ", index " + v + "), got "
						// + sa.entityNames[u] + ", index " + u);
						cnt++;
					}
				}
				tables = tables + " [Size = " + cnt + "]";
			} else {
				tables = "Table " + tableName + " NOT EXISTS";
			}
			retSucc.put("result", tables);
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> getDependingTables(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		try {
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			String tableName = (String) context.get("tableName");
			String accumulate = (String) context.get("accumulate");

			Delegator delegator = ctx.getDelegator();
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}
			String tables = "";
			if (sa.mEntityName2Index.get(tableName) != null) {
				int cnt = 0;
				if (accumulate != null && accumulate.equals("Y")) {
					String[] TBL = sa.findPredecesors(tableName);
					for (int i = 0; i < TBL.length; i++) {
						int idx = sa.mEntityName2Index.get(TBL[i]);
						int scc = sa.scc[idx];
						int idxTOPO = sa.indexTOPO[scc - 1];
						tables = tables + "[" + TBL[i] + "," + (scc - 1) + ","
								+ idxTOPO + "], ";
						cnt++;
					}
				} else {
					int v = sa.mEntityName2Index.get(tableName);
					for (int u : sa.AT[v]) {
						int idx = sa.mEntityName2Index.get(sa.entityNames[u]);
						int scc = sa.scc[idx];
						int idxTOPO = sa.indexTOPO[scc - 1];
						tables = tables + "[" + sa.entityNames[u] + ","
								+ (scc - 1) + "," + idxTOPO + "], ";
						cnt++;
					}
				}
				tables = tables + " [SIZE = " + cnt + "]";
			} else {
				tables = "Table " + tableName + " NOT EXISTS";
			}
			retSucc.put("result", tables);
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ServiceUtil.returnError(ex.getMessage());
		}
	}

	public static Map<String, Object> setNullForeignKeyDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		try {
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			Debug.log(module + "::setNullForeignKeyDBPass, START");
			SCCAnalyzer sa = new SCCAnalyzer(delegatorbkp);
			sa.analyze();
			for (int i = 0; i < sa.entityNames.length; i++) {
				String tableName = sa.entityNames[i];
				Debug.log(module
						+ "::setNullForeignKeyDBPass, START setNullForeignKeys, table["
						+ i + "/" + sa.n + "] " + tableName);
				setNullForeignKeys(tableName, delegatorbkp);
				Debug.log(module
						+ "::setNullForeignKeyDBPass, FINISHED setNullForeignKeys, table["
						+ i + "/" + sa.n + "] " + tableName);
			}
			// setNullForeignKeys("SupplierProduct", delegator);
			retSucc.put("total", "N/A");
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Map<String, Object> setNullForeignKeyOneTableDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		try {
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			String tableName = (String) context.get("tableName");
			Map<String, Object> retSucc = ServiceUtil.returnSuccess();
			Debug.log(module + "::setNullForeignKeyOneTableDBPass, table "
					+ tableName + " START");
			SCCAnalyzer sa = new SCCAnalyzer(delegatorbkp);
			sa.analyze();
			String rs = setNullForeignKeys(tableName, delegatorbkp);

			// setNullForeignKeys("SupplierProduct", delegator);
			retSucc.put("total", rs);
			return retSucc;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String removeDataAllTables(Delegator delegator,
			LocalDispatcher dispatcher, boolean reallyRemove) {
		try {
			SCCAnalyzer sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
			Map<String, Object> in = FastMap.newInstance();
			String rs = "";
			int idx = 0;
			for (int i = sa.sequenceTOPO.length - 1; i >= 0; i--) {
				int scc = sa.sequenceTOPO[i];
				for (String tableName : sa.entitySCC[scc]) {
					Debug.log(module
							+ "::removeDataAllTables, START removeAllDataTable, table["
							+ i + "/" + sa.sequenceTOPO.length + "] = "
							+ tableName);

					// removeAllDataTable(tableName, delegator);
					in.put("tableName", tableName);

					if (reallyRemove)
						dispatcher.runSync("removeDataOneTableDBPass", in);

					Debug.log(module
							+ "::removeDataAllTables, FINISHED removeAllDataTable, table["
							+ i + "/" + sa.sequenceTOPO.length + "] = "
							+ tableName);
					idx++;
					rs = rs + "[" + idx + "] " + tableName + ",\n ";
				}
			}
			return rs;
			/*
			 * for(int i = 0; i < sa.entityNames.length; i++){ String tableName
			 * = sa.entityNames[i]; Debug.log(module +
			 * "::removeDataAllTables, START removeAllDataTable, table[" + i +
			 * "/" + sa.n + "] = " + tableName); removeAllDataTable(tableName,
			 * delegator); Debug.log(module +
			 * "::removeDataAllTables, FINISHED removeAllDataTable, table[" + i
			 * + "/" + sa.n + "] = " + tableName); }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	public static Map<String, Object> removeDataAllTablesDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Map<String, Object> in = FastMap.newInstance();

			Map<String, Object> ret = dispatcher.runSync(
					"setNullForeignKeyDBPass", in);

			ret = dispatcher.runSync("deleteDataAllTablesDBPass", in);

			retSucc.put("total", "N/A");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> deleteDataAllTablesDBPass(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String s_reallyRemove = (String) context.get("reallyRemove");
		try {
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");

			boolean reallyRemove = false;
			if (s_reallyRemove.equals("Y"))
				reallyRemove = true;

			String rs = removeDataAllTables(delegatorbkp, ctx.getDispatcher(),
					reallyRemove);

			retSucc.put("total", rs);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> getNumberSCCClusters(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String dmspass = (String) context.get("dmspass");
		try {
			Delegator delegator = ctx.getDelegator();
			if (dmspass != null && dmspass.equals("Y"))
				delegator = DelegatorFactory.getDelegator("backuppass");

			SCCAnalyzer sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();

			retSucc.put("result", sa.sequenceTOPO.length + "");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> getSCCCluster(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		String dmspass = (String) context.get("dmspass");
		try {
			String s_index = (String) context.get("index");
			int idx = (int) Integer.valueOf(s_index);
			Delegator delegator = ctx.getDelegator();
			if (dmspass != null && dmspass.equals("Y"))
				delegator = DelegatorFactory.getDelegator("backuppass");

			// if (sa == null) {
			// sa = new SCCAnalyzer(delegator);
			// sa.analyze();
			// sa.computeTOPO();
			// }
			SCCAnalyzer sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();

			List<String> L = sa.getSCC(idx);
			String rs = "";
			for (String s : L)
				rs = rs + s + ", ";
			retSucc.put("result", rs);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> getIndexTopoOfSCC(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			String s_indexSCC = (String) context.get("indexSCC");
			int idx = (int) Integer.valueOf(s_indexSCC);
			Delegator delegator = ctx.getDelegator();
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}

			retSucc.put("result", sa.indexTOPO[idx] + "");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> getMoreThanOneSCCClusters(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			Delegator delegator = ctx.getDelegator();
			SCCAnalyzer sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
			String rs = "";
			for (int i = 0; i < sa.sequenceTOPO.length; i++) {
				if (sa.entitySCC[sa.sequenceTOPO[i]].size() > 1) {
					rs = rs + "(" + i + ","
							+ sa.entitySCC[sa.sequenceTOPO[i]].size() + ") ";
				}
			}
			retSucc.put("result", rs);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static List<String> getFkNames(Delegator delegator, String tableName) {
		try {
			ModelEntity modelEntity = delegator.getModelEntity(tableName);
			List<String> fkNames = FastList.newInstance();
			for (ModelRelation R : modelEntity.getRelationsOneList()) {
				for (ModelKeyMap mkm : R.getKeyMaps()) {
					fkNames.add(mkm.getFieldName());
				}
			}
			return fkNames;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean checkExists(
			List<TablePrimaryKeyValue> tablePrimaryKeyValues,
			TablePrimaryKeyValue e) {
		for (TablePrimaryKeyValue ei : tablePrimaryKeyValues) {
			if (ei.equal(e))
				return true;
		}
		return false;
	}

	public static boolean createEntity(TablePrimaryKeyValue e,
			Delegator delegator, List<String> fieldNames, List<String> pkNames,
			Set<String> fkNames) {
		try {
			List<Object> pkValues = FastList.newInstance();
			for (int i = 0; i < pkNames.size(); i++)
				pkValues.add(e.gv.get(pkNames.get(i)));

			GenericValue gv = findOne(delegator, e.tableName, pkNames, pkValues);
			if (gv == null) {
				gv = delegator.makeValue(e.tableName);

				for (String f : fieldNames) {
					gv.put(f, e.gv.get(f));
				}
				for (String f : fkNames) {
					gv.put(f, null);
				}

				String des = "";
				for (String f : fieldNames) {
					des = des + "[" + f + "," + gv.get(f) + "] ";
				}
				des = des + " pkNames = ";
				for (String f : pkNames)
					des = des + f + ",";
				des = des + " fkNames = ";
				for (String f : fkNames)
					des = des + f + ",";
				Debug.log(module + "::createEntity, table " + e.tableName
						+ ", PREPARE des = " + des);
				for (String pk : pkNames)
					if (gv.get(pk) == null) {
						des = des + " EXCEPTION pkName NULL " + pk + ", ";
						Debug.log(module + "::createEntity, table "
								+ e.tableName + ", PREPARE des = " + des);
					}

				delegator.createIgnoreECA(gv);
			} else {
				Debug.log(module + "::createEntity, table " + e.tableName
						+ " EXIST -> UPDATE");
				for (String f : fieldNames) {
					gv.put(f, e.gv.get(f));
				}
				for (String f : fkNames) {
					gv.put(f, null);
				}
				String des = "";
				for (String f : fieldNames) {
					des = des + "[" + f + "," + gv.get(f) + "] ";
				}
				des = des + " pkNames = ";
				for (String f : pkNames)
					des = des + f + ",";
				des = des + " fkNames = ";
				for (String f : fkNames)
					des = des + f + ",";
				Debug.log(module + "::createEntity, table " + e.tableName
						+ ", PREPARE des = " + des);
				for (String pk : pkNames)
					if (gv.get(pk) == null) {
						des = des + " EXCEPTION pkName NULL " + pk + ", ";
						Debug.log(module + "::createEntity, table "
								+ e.tableName + ", PREPARE des = " + des);
					}

				delegator.storeIgnoreECA(gv);
			}

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static boolean createEntityUpdateForeignKeyValues(
			TablePrimaryKeyValue e, Delegator delegator,
			List<String> fieldNames, Set<String> fkNames) {
		try {
			// GenericValue gv = delegator.makeValue(e.tableName);
			GenericValue gv = findOne(delegator, e.tableName, e.pkNames,
					e.pkValues);

			// for (int i = 0; i < e.pkNames.size(); i++) {
			// gv.put(e.pkNames.get(i), e.pkValues.get(i));
			// }
			// for (String f : fieldNames) {
			// gv.put(f, e.gv.get(f));
			// }
			for (String f : fkNames) {
				gv.put(f, e.gv.get(f));
			}
			delegator.storeIgnoreECA(gv);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/*
	 * public static void backupOrders(String tableName, Set<DataStrings>
	 * primaryKeyValues, Delegator delegator, Delegator delegatorbkp) { try {
	 * Debug.log(module + "::backupOrders, START"); if (sa == null) { sa = new
	 * SCCAnalyzer(delegator); sa.analyze(); sa.computeTOPO(); }
	 * 
	 * Map<String, Set<DataStrings>> mTableName2PrimaryKeyValues = new
	 * HashMap<String, Set<DataStrings>>();// FastMap.newInstance();
	 * mTableName2PrimaryKeyValues.put(tableName, primaryKeyValues);
	 * 
	 * List<TablePrimaryKeyValue> tablePrimaryKeyValues = FastList
	 * .newInstance();// new ArrayList<TablePrimaryKeyValue>();
	 * Queue<TablePrimaryKeyValue> QT = new LinkedList<>(); ModelEntity table =
	 * delegator.getModelEntity(tableName); List<String> pkNames =
	 * table.getPkFieldNames(); for (DataStrings D : primaryKeyValues) {
	 * TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName, pkNames,
	 * D.getData());
	 * 
	 * GenericValue gv = findOne(delegator, tableName, pkNames, D); e.gv = gv;
	 * QT.add(e); tablePrimaryKeyValues.add(e); } Debug.log(module +
	 * "::backupOrders, START queue QT"); while (QT.size() > 0) {
	 * TablePrimaryKeyValue tpkv = QT.remove(); String tblName = tpkv.tableName;
	 * ModelEntity tbl = delegator.getModelEntity(tblName); List<String>
	 * prkNames = tbl.getPkFieldNames(); int v =
	 * sa.mEntityName2Index.get(tblName);
	 * 
	 * int[] a = new int[sa.A[v].size()]; int idx = -1; for (int x : sa.A[v]) {
	 * idx++; a[idx] = x; } for (int i = 0; i < a.length; i++) { for (int j = i
	 * + 1; j < a.length; j++) { if (sa.indexTOPO[sa.scc[a[i]] - 1] >
	 * sa.indexTOPO[sa.scc[a[j]] - 1]) { int tmp = a[i]; a[i] = a[j]; a[j] =
	 * tmp; } } } String info = tblName + ": "; for (int i = 0; i < a.length;
	 * i++) { int x = a[i]; String relationed_entity = sa.entityNames[x]; info =
	 * info + "[" + i + "," + relationed_entity + ", topo " +
	 * sa.indexTOPO[sa.scc[a[i]] - 1] + "] "; } Debug.log(module +
	 * "::backupOrders, CONSIDER INFO " + info);
	 * 
	 * // for (int x : sa.A[v]) { for (int i = 0; i < a.length; i++) { int x =
	 * a[i]; String relationed_entity = sa.entityNames[x]; List<String>
	 * pkNames_relationed_entity = delegator .getModelEntity(relationed_entity)
	 * .getPkFieldNames();
	 * 
	 * List<String> frkNames = sa.getFieldNames(tblName, relationed_entity);
	 * List<DataStrings> data = getPrimaryKeyValues(delegator,
	 * relationed_entity, frkNames, tpkv.pkValues);
	 * 
	 * List<GenericValue> GV = getRecordsWithForeignKeyValues( delegator,
	 * relationed_entity, frkNames, tpkv.pkValues); String sub_info = ""; for
	 * (int j = 0; j < frkNames.size(); j++) sub_info += "[" + frkNames.get(j) +
	 * "," + tpkv.pkValues.get(j) + "] "; Debug.log(module +
	 * "::backupOrders, from table " + tblName + ", get records " +
	 * relationed_entity + ", sub_info = " + sub_info + ", GV = " + GV.size());
	 * 
	 * // for (DataStrings d : data) { for (GenericValue gv : GV) { //
	 * setNULLForeighKeyValues(relationed_entity, gv, // delegator); DataStrings
	 * d = new DataStrings(); for (String pkn : pkNames_relationed_entity) {
	 * d.add(gv.getString(pkn)); }
	 * 
	 * Debug.log(module + "::backupOrders, from table " + tblName +
	 * " consider create table(" + relationed_entity + ") with data " +
	 * d.toString()); TablePrimaryKeyValue e = new TablePrimaryKeyValue(
	 * relationed_entity, pkNames_relationed_entity, d.getData()); e.gv = gv;
	 * 
	 * if (!checkExists(tablePrimaryKeyValues, e)) { QT.add(e);
	 * tablePrimaryKeyValues.add(e); Debug.log(module +
	 * "::backupOrders, from table " + tblName + " decide create table(" +
	 * relationed_entity + ") with data " + d.toString());
	 * 
	 * } }
	 * 
	 * } } for (int i = 0; i < tablePrimaryKeyValues.size(); i++) {
	 * TablePrimaryKeyValue e = tablePrimaryKeyValues.get(i); if
	 * (tableExists(e.tableName)) { Debug.log(module +
	 * "::backupOrders, START create entity " + e.toString());
	 * 
	 * List<String> fieldNames = delegator.getModelEntity(
	 * e.tableName).getAllFieldNames();
	 * 
	 * boolean ok = createEntity(e, delegatorbkp, fieldNames,
	 * getFkNamesExclusivePkNames(e.tableName, delegator)); // boolean ok =
	 * true; // delegatorbkp.create(e.gv);
	 * 
	 * if (ok) Debug.log(module + "::backupOrders, created entity " +
	 * e.toString() + " OK"); else { Debug.log(module +
	 * "::backupOrders, created entity " + e.toString() + " FAILED -> BREAK");
	 * break; } } else { Debug.log(module + "::backupOrders, entity " +
	 * e.tableName + " does not exist"); } }
	 * 
	 * // restore values of fkNames // for(String tblName:
	 * mTableName2PrimaryKeyValues.keySet()){ // List<UPDATEINFO> LUI =
	 * mTableName2UpdateInfo.get(tblName); // updateTables(delegatorbkp,
	 * tableName, LUI); // }
	 * 
	 * } catch (Exception ex) { ex.printStackTrace(); } }
	 */
	public static String toString(List<Object> pkValues) {
		String s = "";
		for (int i = 0; i < pkValues.size(); i++) {
			String e = "";
			if (pkValues.get(i) instanceof java.sql.Timestamp) {
				e = pkValues.get(i).toString();
			} else {
				e = pkValues.get(i) + "";
			}
			s = s + "@" + e;
		}
		return s;
	}

	public static String composeTablePKValue(String tableName,
			List<Object> pkValues) {
		String s = tableName;
		for (int i = 0; i < pkValues.size(); i++) {
			String e = "";
			if (pkValues.get(i) instanceof java.sql.Timestamp) {
				e = pkValues.get(i).toString();
			} else {
				e = pkValues.get(i) + "";
			}
			s = s + "@" + e;
		}
		return s;
	}

	/*
	 * unsafe
	 */
	/*
	 * public static void setNullForeignKeyValues(Delegator delegator,
	 * GenericValue gv, String tableName){ try{ ModelEntity me =
	 * delegator.getModelEntity(tableName); //if(tableExists(tableName)){
	 * List<String> pkNames = me.getPkFieldNames(); List<String> fkNames =
	 * getFkNames(delegator, tableName); for(String f: fkNames)
	 * if(!pkNames.contains(f)){ gv.put(f, null); } //} }catch(Exception ex){
	 * ex.printStackTrace(); } }
	 */
	public static Map<String, Object> removeRecordAndDependedRecords(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		retSucc.put("result", "N/A");
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Delegator delegator = ctx.getDelegator();
			String tableName = (String) context.get("tableName");
			Debug.log(module
					+ "::removeRecordAndDependedRecords START table = "
					+ tableName);

			String str_pkValues = (String) context.get("pkValues");
			List<Object> pkValues = (List<Object>) context.get("pkValueList");
			if (str_pkValues != null && pkValues != null) {
				Debug.log(module
						+ "::removeRecordAndDependedRecords, ONLY str_pkValues OR pkValues are NOT NULL");
				return retSucc;
			}
			String dmspass = (String) context.get("dmspass");
			if (dmspass != null && dmspass.equals("Y"))
				delegator = DelegatorFactory.getDelegator("backuppass");

			if (pkValues == null) {
				pkValues = FastList.newInstance();
				String[] s = str_pkValues.split(",");
				for (int i = 0; i < s.length; i++)
					pkValues.add(s[i].trim());
			}
			/*
			Map<String, GenericValue> mTableKey2GenericValue = FastMap.newInstance();
			
			removeRecordAndDependedRecords(dispatcher, delegator, tableName,
					pkValues, dmspass, mTableKey2GenericValue);

			if (true)
				return retSucc;
			*/
			
			TablePrimaryKeyValue[] T = collectDependedRecords(delegator,
					tableName, pkValues, -1);
			
			// SORT: if T[i] depends T[j] then i must greater than j
			for (int i = 0; i < T.length; i++) {
				for (int j = i + 1; j < T.length; j++) {
					if (predefinedDepends(T[i].tableName, T[j].tableName)) {
						TablePrimaryKeyValue tmp = T[i];
						T[i] = T[j];
						T[j] = tmp;
					}
				}
			}
			
			Debug.log(module + "::removeRecordAndDependedRecords GOT T.sz = "
					+ T.length);
			for (int i = T.length - 1; i >= 0; i--) {
				String tblName = T[i].tableName;
				setNULLForeighKeyValues(tblName, T[i].gv, delegator);
				Debug.log(module
						+ "::removeRecordAndDependedRecords, FINISHED setNULLForeighKeyValues "
						+ i + "/" + T.length);
			}

			for (int i = T.length - 1; i >= 0; i--) {
				String tblName = T[i].tableName;

				// GenericValue gv = findOne(delegator, T[i].tableName,
				// T[i].pkNames, T[i].pkValues);
				if (T[i].gv != null) {
					delegator.removeValueIgnoreECA(T[i].gv);
					Debug.log(module
							+ "::removeRecordAndDependedRecords, removed (" + i
							+ "/" + T.length + ")" + T[i].toString());
				} else {
					Debug.log(module
							+ "::removeRecordAndDependedRecords, NOT FOUND record "
							+ T[i].toString());
				}
			}
			retSucc.put("result", "N/A");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static void removeRecordAndDependedRecords(
			LocalDispatcher dispatcher, Delegator delegator, String tableName,
			List<Object> pkValues, String dmspass, Map<String, GenericValue> mTableKey2GenericValue) {
		if (sa == null) {
			sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
		}
		int u = sa.mEntityName2Index.get(tableName);
		ModelEntity entity = delegator.getModelEntity(tableName);
		List<String> pkNames = entity.getPkFieldNames();
		GenericValue gv = findOne(delegator, tableName, pkNames, pkValues);
		if (gv == null) {
			Debug.log(module
					+ "::removeRecordAndDependedRecords START recursive table "
					+ tableName + ", pkValues = " + toString(pkValues)
					+ ", gv = NULL -> return");
			return;
		}
		String key = composeTablePKValue(tableName, pkValues);
		mTableKey2GenericValue.put(key,gv);
		
		Debug.log(module
				+ "::removeRecordAndDependedRecords START recursive table "
				+ tableName + ", pkValues = " + toString(pkValues)
				+ ", number of depended = " + sa.A[u].size());

		try {
			for (int x : sa.A[u]) {

				String relationed_entity = sa.entityNames[x];

				if (!tableExists(relationed_entity))
					continue;

				ModelEntity r_entity = delegator
						.getModelEntity(relationed_entity);

				for (ModelRelation mr : r_entity.getRelationsOneList()) {
					if (!mr.getRelEntityName().equals(tableName))
						continue;

					List<String> pkNames_relationed_entity = r_entity
							.getPkFieldNames();

					List<String> frkNames = FastList.newInstance();
					//List<Object> frkValues = FastList.newInstance();
					//for(ModelKeyMap mkm: mr.getKeyMaps()){
					//	
					//}
					
					for (String pk : pkNames) {
						String fk = "";
						for (ModelKeyMap mkm : mr.getKeyMaps()) {
							if (mkm.getRelFieldName().equals(pk)) {
								fk = mkm.getFieldName();
								break;
							}
						}
						frkNames.add(fk);
					}
					String info_cond = "";
					
					List<EntityCondition> conds = FastList.newInstance();
					if (frkNames != null) {
						for (int i = 0; i < frkNames.size(); i++) {
							conds.add(EntityCondition.makeCondition(
									frkNames.get(i), EntityOperator.EQUALS,
									pkValues.get(i)));
							info_cond += "[" + frkNames.get(i) + "," + pkValues.get(i) + "], ";
						}
					}
					EntityCondition condition = EntityCondition
							.makeCondition(conds);
					Long totalRows = delegator.findCountByCondition(
							relationed_entity, condition, null, null, null);
					
					Debug.log(module
							+ "::removeRecordAndDependedRecords START recursive table "
							+ tableName + ", pkValues = " + toString(pkValues)
							+ ", number of depended = " + sa.A[u].size() + ", info_cond = " + info_cond + ", totalRows = " + totalRows);
					
					EntityFindOptions opts = new EntityFindOptions();
					int pageSz = PAGE_SZ;

					long nbPages = totalRows / pageSz;
					if (totalRows % pageSz != 0)
						nbPages++;
					int cnt = 0;
					for (int idx = 0; idx < nbPages; idx++) {
						opts.setLimit(pageSz);
						opts.setOffset(idx * pageSz);

						EntityListIterator listIterator = delegator.find(
								relationed_entity, condition, null, null, null,
								opts);

						List<GenericValue> lst = null;

						lst = listIterator.getCompleteList();
						List<Object> r_pkValues = FastList.newInstance();
						for (GenericValue g : lst) {
							r_pkValues.clear();
							for (String rpk : pkNames_relationed_entity) {
								// for(String rpk: frkNames){
								r_pkValues.add(g.get(rpk));
								// Debug.log(module +
								// "::removeRecordAndDependedRecords for table "
								// + tableName +
								// ", pkValues = " + toString(pkValues) +
								// ", set foreign-key(" + relationed_entity +
								// ") "
								// + rpk + " = " + g.get(rpk) + ", g = " +
								// g.toString());
							}
							// Map<String, Object> in = FastMap.newInstance();
							// in.put("tableName", relationed_entity);
							// in.put("dmspass", dmspass);
							// in.put("pkValueList", r_pkValues);
							// Map<String, Object> rs =
							// dispatcher.runSync("removeRecordAndDependedRecords",
							// in);
							String r_key = composeTablePKValue(relationed_entity, r_pkValues);
							if(mTableKey2GenericValue.get(r_key) != null) continue;
							
							removeRecordAndDependedRecords(dispatcher,
									delegator, relationed_entity, r_pkValues,
									dmspass, mTableKey2GenericValue);
							cnt++;
							Debug.log(module
									+ "::removeRecordAndDependedRecords for table "
									+ tableName + ", pkValues = "
									+ toString(pkValues) + " FINISHED remove "
									+ cnt + "/" + totalRows
									+ " relationed_entity " + relationed_entity
									+ " key " + toString(r_pkValues));
						}
						if (listIterator != null)
							listIterator.close();
					}

				}

			}

			delegator.removeValueIgnoreECA(gv);
			Debug.log(module + "::removeRecordAndDependedRecords for table "
					+ tableName + ", pkValues = " + toString(pkValues)
					+ " FINISHED remove record " + toString(pkValues));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static Map<String, Object> getDependedRecords(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			Delegator delegator = ctx.getDelegator();
			String tableName = (String) context.get("tableName");
			String str_pkValues = (String) context.get("pkValues");
			String in_maxRecordsPerTable = (String)context.get("maxRecordsPerTable");
			int maxRecordsPerTable = -1;
			if(in_maxRecordsPerTable != null){
				try{
					maxRecordsPerTable = Integer.valueOf(in_maxRecordsPerTable);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			List<Object> pkValues = FastList.newInstance();
			String[] s = str_pkValues.split(",");
			for (int i = 0; i < s.length; i++)
				pkValues.add(s[i].trim());
			
			
			TablePrimaryKeyValue[] T = collectDependedRecords(delegator,
					tableName, pkValues, maxRecordsPerTable);

			String des = "[";
			for (int i = 0; i < T.length; i++) {
				int idx = sa.mEntityName2Index.get(T[i].tableName);
				int scc = sa.scc[idx] - 1;
				int idxTOPO = sa.indexTOPO[scc];
				String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
						+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
				for (int j = 0; j < T[i].pkNames.size(); j++) {
					item = item + "{\"key\":\"" + T[i].pkNames.get(j)
							+ "\",\"value\":\"" + T[i].pkValues.get(j) + "\"}";
					if (j < T[i].pkNames.size() - 1)
						item = item + ",";
				}
				des = des + item + "]}";
				if (i < T.length - 1)
					des = des + ",";
				Debug.log(module + "::getDependedRecords: " + item);

			}
			des = des + "]";
			retSucc.put("result", des);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;

	}

	public static Map<String, Object> collectDependedRecords(
			DispatchContext ctx, Map<String, ? extends Object> context) {

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();

			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			String dmspass = (String) context.get("dmspass");

			String tableName = (String) context.get("tableName");
			String in_pkValues = (String) context.get("pkValues");
			String in_maxRecordsPerTable = (String)context.get("maxRecordsPerTable");
			int maxRecordsPerTable = -1;
			if(in_maxRecordsPerTable != null){
				try{
					maxRecordsPerTable = Integer.valueOf(in_maxRecordsPerTable);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			if (dmspass != null && dmspass.equals("Y"))
				delegator = delegatorbkp;

			List<Object> pkValues = FastList.newInstance();
			String[] s = in_pkValues.split(",");
			for (int i = 0; i < s.length; i++)
				pkValues.add(s[i].trim());

			TablePrimaryKeyValue[] T = collectDependedRecords(delegator,
					tableName, pkValues, maxRecordsPerTable);

			HashSet<String> cutTable = new HashSet<String>();
			HashSet<String> notCutTable = new HashSet<String>();

			String des = "[";
			for (int i = 0; i < T.length; i++) {
				int idx = sa.mEntityName2Index.get(T[i].tableName);
				int scc = sa.scc[idx] - 1;
				int idxTOPO = sa.indexTOPO[scc];
				String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
						+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
				for (int j = 0; j < T[i].pkNames.size(); j++) {
					item = item + "{\"key\":\"" + T[i].pkNames.get(j)
							+ "\",\"value\":\"" + T[i].pkValues.get(j)
							+ "\",\"cut\":\"" + T[i].cut + "\"}";
					if (j < T[i].pkNames.size() - 1)
						des = des + ",";
				}
				item = item + "]}";
				if (i < T.length - 1)
					item = item + ",";
				Debug.log(module + "::collectDependedRecords: " + item);
				des = des + item;

				if (T[i].cut)
					cutTable.add(T[i].tableName);
				else
					notCutTable.add(T[i].tableName);
			}
			String s_not_cut_tables = "";
			String s_cut_tables = "";
			for (String st : cutTable)
				s_cut_tables = s_cut_tables + st + ",";
			for (String st : notCutTable)
				s_not_cut_tables = s_not_cut_tables + st + ",";
			des = des + ",{\"cuttable\":" + "\"" + s_cut_tables + "\"}";
			des = des + ",{\"notcuttable\":" + "\"" + s_not_cut_tables + "\"}";

			des = des + "]";

			retSucc.put("result", des);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static TablePrimaryKeyValue[] collectDependedRecords(
			Delegator delegator, String tableName, List<Object> pkValues, int maxSz) {
		// return list of records that are depended on the current record of
		// tableName
		List<TablePrimaryKeyValue> tables = FastList.newInstance();
		Set<String> tablePKValue = FastSet.newInstance();
		collectDependedRecords(delegator, tableName, pkValues, tables,
				tablePKValue, maxSz);
		TablePrimaryKeyValue[] A = new TablePrimaryKeyValue[tables.size()];
		int[] topo = new int[A.length];
		for (int i = 0; i < tables.size(); i++) {
			A[i] = tables.get(i);
			int idx = sa.mEntityName2Index.get(A[i].tableName);
			int scc = sa.scc[idx] - 1;
			int idxTOPO = sa.indexTOPO[scc];
			topo[i] = idxTOPO;
		}

		for (int i = 0; i < A.length; i++) {
			// String t1 = A[i].tableName;
			// int i1 = sa.mEntityName2Index.get(A[i].tableName);
			for (int j = i + 1; j < A.length; j++) {
				// int j1 = sa.mEntityName2Index.get(A[j].tableName);
				// if(sa.indexTOPO[sa.scc[i1]-1] > sa.indexTOPO[sa.scc[j1]-1]){
				boolean swap = topo[i] > topo[j]
						|| (topo[i] == topo[j] && A[i].tableName
								.compareTo(A[j].tableName) > 0);
				if (swap) {
					int t = topo[i];
					topo[i] = topo[j];
					topo[j] = t;
					TablePrimaryKeyValue tmp = A[i];
					A[i] = A[j];
					A[j] = tmp;
				}
			}
		}

		return A;
	}

	public static void collectDependedRecords(Delegator delegator,
			String tableName, List<Object> pkValues,
			List<TablePrimaryKeyValue> table, Set<String> tablePKValue,
			int maxRecordsPerTable) {
		// collect records predecessors and successors of record (tableName,
		// pkValues) into table
		// tablePKValue store set of records
		if (sa == null) {
			sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
		}
		// Debug.log(module + "::collectDependedRecords(" + tableName +
		// "), table.sz = " + table.size() + ", tablePKValue.sz = " +
		// tablePKValue.size());
		// if(maxSz > 0) if(table.size() > maxSz && tablePKValue.size() >
		// maxSz){
		// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
		// "), table.sz = " + table.size() + " > " + maxSz + ", RETURN");
		// return;
		// }

		double t = System.currentTimeMillis() - t0;
		t = t * 0.001;
		// if(t > 20){
		// Debug.log(module + "::establishCopiedRecordTables, time = " + t +
		// " EXPIRED -> BREAK");
		// return;
		// }
		ModelEntity entity = delegator.getModelEntity(tableName);
		int v = sa.mEntityName2Index.get(tableName);
		int[] a = sa.getSortedDependedEntityIndex(v);
		String msg = sa.entityNames[v] + " has dependedTables = ";
		if (a != null)
			for (int i = 0; i < a.length; i++) {
				msg += sa.entityNames[a[i]] + ",";
			}
		// Debug.log(module + "::collectDependedRecords(" + tableName +
		// "), msg = " + msg);

		// int[] b = sa.getSortedDependingEntityIndex(v);

		List<String> pkNames = entity.getPkFieldNames();
		// List<Object> oPkValues = FastList.newInstance();
		// for(int i= 0; i < pkValues.size(); i++)
		// oPkValues.add(pkValues.get(i));

		TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName, pkNames,
				pkValues);
		e.gv = findOne(delegator, tableName, pkNames, pkValues);
		if (e.gv == null) {
			Debug.log(module + "::collectDependedRecords(" + tableName
					+ "), e.gv = null BUG?? -> RETURN");
			return;
		}

		table.add(e);
		String KEY = composeTablePKValue(tableName, pkValues);
		tablePKValue.add(KEY);

		for (int i = 0; i < a.length; i++) {
			int x = a[i];
			String relationed_entity = sa.entityNames[x];
			// for(ModelRelation mr: entity.getRelationsOneList()){
			// String relationed_entity = mr.getRelEntityName();

			if (!tableExists(relationed_entity))
				continue;

			ModelEntity r_entity = delegator.getModelEntity(relationed_entity);

			// if(relationed_entity.equals(tableName)){this may happen when
			// parent-records is consider
			// Debug.log(module + "::collectDependedRecords(" + tableName +
			// "), relationed_entity = tableName->BUG???");
			// return;
			// }
			for (ModelRelation mr : r_entity.getRelationsOneList()) {
				if (!mr.getRelEntityName().equals(tableName))
					continue;

				List<String> pkNames_relationed_entity = r_entity
						.getPkFieldNames();

				// List<String> frkNames = sa.getFieldNames(tableName,
				// relationed_entity);

				// List<DataStrings> data = getPrimaryKeyValues(delegator,
				// relationed_entity, frkNames, pkValues);

				List<String> frkNames = FastList.newInstance();
				for (String pk : pkNames) {
					String fk = "";
					for (ModelKeyMap mkm : mr.getKeyMaps()) {
						if (mkm.getRelFieldName().equals(pk)) {
							fk = mkm.getFieldName();
							break;
						}
					}
					frkNames.add(fk);
				}

				List<GenericValue> GV = getRecordsWithForeignKeyValues(
						delegator, relationed_entity, frkNames, pkValues, maxRecordsPerTable);
				String sub_info = "";
				for (int j = 0; j < frkNames.size(); j++)
					sub_info += "[" + frkNames.get(j) + "," + pkValues.get(j)
							+ "] ";

				boolean DEBUG = tableName.equals("Facility");
				// && relationed_entity.equals("TransferHeader"))

				if (DEBUG)
					Debug.log(module + "::collectDependedRecords, time = " + t
							+ ", from table " + tableName
							+ ", get records relationed_entity = "
							+ relationed_entity + ", sub_info = " + sub_info
							+ ", GV = " + GV.size());

				for (GenericValue gv : GV) {
					// DataStrings d = new DataStrings();
					List<Object> d = FastList.newInstance();
					for (String pkn : pkNames_relationed_entity) {
						d.add(gv.get(pkn));
					}
					String key = composeTablePKValue(relationed_entity, d);
					if (tablePKValue.contains(key)) {
						if (DEBUG)
							Debug.log(module
									+ "::collectDependedRecords, time = " + t
									+ ", from table " + tableName
									+ ", get records relationed_entity = "
									+ relationed_entity + ", sub_info = "
									+ sub_info + ", GV = " + GV.size()
									+ ", key = " + key + " EXISTS");
						continue;
					}
					if (DEBUG)
						Debug.log(module
								+ "::collectDependedRecords, [START] time = "
								+ t
								+ ", from table "
								+ tableName
								+ ", get records relationed_entity = "
								+ relationed_entity
								+ ", sub_info = "
								+ sub_info
								+ ", GV = "
								+ GV.size()
								+ ", d = "
								+ toString(d)
								+ " START call recursive collectDependedRecords");

					collectDependedRecords(delegator, relationed_entity, d,
							table, tablePKValue, maxRecordsPerTable);
					// tablePKValue.add(key);
				}
			}
		}
	}

	public static void establishCopiedRecordTables(Delegator delegator,
			String tableName, List<Object> pkValues,
			List<TablePrimaryKeyValue> table, Set<String> tablePKValue,
			HashMap<String, TablePrimaryKeyValue> map,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out,
			TablePrimaryKeyValue from, int maxSz) {
		// collect records predecessors and successors of record (tableName,
		// pkValues) into table
		// tablePKValue store set of records
		String tmpK = toString(pkValues);
		String info = composeTablePKValue(tableName, pkValues);
		Debug.log(module + "::establishCopiedRecordTables info = " + info + "("
				+ tableName + "), table.sz = " + table.size()
				+ ", pkValues.sz = " + pkValues.size() + ", pkValues = " + tmpK
				+ ", tablePKValue.sz = " + tablePKValue.size());

		if (sa == null) {
			sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
		}

		// if(maxSz > 0) if(table.size() > maxSz && tablePKValue.size() >
		// maxSz){
		// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
		// "), table.sz = " + table.size() + " > " + maxSz + ", RETURN");
		// return;
		// }

		double t = System.currentTimeMillis() - t0;
		t = t * 0.001;
		// if(t > 20){
		// Debug.log(module + "::establishCopiedRecordTables, time = " + t +
		// " EXPIRED -> BREAK");
		// return;
		// }
		int v = sa.mEntityName2Index.get(tableName);
		int[] a = sa.getSortedDependedEntityIndex(v);
		int[] b = sa.getSortedDependingEntityIndex(v);

		ModelEntity entity = delegator.getModelEntity(tableName);
		List<String> pkNames = entity.getPkFieldNames();
		// List<Object> oPkValues = FastList.newInstance();
		// for(int i= 0; i < pkValues.size(); i++)
		// oPkValues.add(pkValues.get(i));

		TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName, pkNames,
				pkValues);
		e.gv = findOne(delegator, tableName, pkNames, pkValues);
		if (e.gv == null) {
			// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
			// "), e.gv = null -> RETURN");
			return;
		}

		table.add(e);
		String KEY = composeTablePKValue(tableName, pkValues);
		tablePKValue.add(KEY);
		map.put(KEY, e);
		e.cut = true;

		if (from != null) {
			in.put(e, new HashSet<TablePrimaryKeyValue>());
			if (out.get(from) == null)
				out.put(from, new HashSet<TablePrimaryKeyValue>());

			in.get(e).add(from);

			// if(from.tableName.equals("CountryAddressFormat") &&
			// e.tableName.equals("Geo")){
			Debug.log(module + "::establishCopiedRecordTables info = " + info
					+ " BUG?? add to OUT from = "
					+ composeTablePKValue(from.tableName, from.pkValues)
					+ " -> " + composeTablePKValue(e.tableName, e.pkValues));
			// }else
			out.get(from).add(e);
		} else {

		}

		for (int i = 0; i < a.length; i++) {

			int x = a[i];
			String relationed_entity = sa.entityNames[x];
			if (!tableExists(relationed_entity))
				continue;

			// if(sa.defineDepends(tableName, relationed_entity)){
			// Debug.log(module + "::establishCopiedRecordTables IGNORE CASE " +
			// tableName + " -> " + relationed_entity);
			// continue;
			// }

			boolean DEBUG = tableName.equals("PartyRole")
					&& relationed_entity.equals("CarrierShipmentMethod");
			// if(relationed_entity.equals(tableName)){
			// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
			// "), relationed_entity = tableName->BUG???");
			// return;
			// }

			ModelEntity r_entity = delegator.getModelEntity(relationed_entity);

			// if(relationed_entity.equals(tableName)){this may happen when
			// parent-records is consider
			// Debug.log(module + "::collectDependedRecords(" + tableName +
			// "), relationed_entity = tableName->BUG???");
			// return;
			// }
			for (ModelRelation mr : r_entity.getRelationsOneList()) {
				if (!mr.getRelEntityName().equals(tableName))
					continue;

				List<String> pkNames_relationed_entity = r_entity
						.getPkFieldNames();

				// List<String> frkNames = sa.getFieldNames(tableName,
				// relationed_entity);

				// List<DataStrings> data = getPrimaryKeyValues(delegator,
				// relationed_entity, frkNames, pkValues);

				List<String> frkNames = FastList.newInstance();
				for (String pk : pkNames) {
					String fk = "";
					for (ModelKeyMap mkm : mr.getKeyMaps()) {
						if (mkm.getRelFieldName().equals(pk)) {
							fk = mkm.getFieldName();
							break;
						}
					}
					frkNames.add(fk);
				}

				List<GenericValue> GV = getRecordsWithForeignKeyValues(
						delegator, relationed_entity, frkNames, pkValues);
				String sub_info = "";
				for (int j = 0; j < frkNames.size(); j++)
					sub_info += "[" + frkNames.get(j) + "," + pkValues.get(j)
							+ "] ";
				// Debug.log(module + "::establishCopiedRecordTables, time = " +
				// t + ", from table " + tableName
				// + ", get records relationed_entity = " + relationed_entity
				// + ", sub_info = " + sub_info + ", GV = "
				// + GV.size());

				for (GenericValue gv : GV) {
					// DataStrings d = new DataStrings();
					List<Object> d = FastList.newInstance();
					for (String pkn : pkNames_relationed_entity) {
						d.add(gv.get(pkn));
					}
					String key = composeTablePKValue(relationed_entity, d);
					// if(tablePKValue.contains(key)){
					if (map.get(key) != null) {
						if (DEBUG)
							Debug.log(module
									+ "::establishCopiedRecordTables CHECK START call recursively establishCopiedRecordTables"
									+ " from [" + tableName + ","
									+ toString(pkValues) + "] NOT generate ["
									+ relationed_entity + "," + toString(d)
									+ "]");
						map.get(key).cut = true;
						TablePrimaryKeyValue to = map.get(key);
						if (in.get(to) == null)
							in.put(to, new HashSet<TablePrimaryKeyValue>());
						in.get(to).add(e);

						if (out.get(e) == null)
							out.put(e, new HashSet<TablePrimaryKeyValue>());

						// if(e.tableName.equals("CountryAddressFormat") &&
						// to.tableName.equals("Geo")){
						Debug.log(module
								+ "::establishCopiedRecordTables info = "
								+ info
								+ " BUG EXISTS?? add to OUT e = "
								+ composeTablePKValue(e.tableName, e.pkValues)
								+ " -> "
								+ composeTablePKValue(to.tableName, to.pkValues));
						// }else
						out.get(e).add(to);

					} else {
						if (DEBUG)
							Debug.log(module
									+ "::establishCopiedRecordTables CHECK START call recursively establishCopiedRecordTables"
									+ " from [" + tableName + ","
									+ toString(pkValues) + "] generate ["
									+ relationed_entity + "," + toString(d)
									+ "]");
						establishCopiedRecordTables(delegator,
								relationed_entity, d, table, tablePKValue, map,
								in, out, e, maxSz);
						// tablePKValue.add(key);
					}
				}
			}
		}

		for (int i = 0; i < b.length; i++) {
			int x = b[i];
			String relationing_entity = sa.entityNames[x];
			if (!tableExists(relationing_entity))
				continue;

			// if(sa.defineDepends(relationing_entity, tableName)){
			// Debug.log(module + "::establishCopiedRecordTables IGNORE CASE " +
			// tableName + " <- " + relationing_entity);
			// continue;
			// }

			boolean DEBUG = tableName.equals("OrderHeader");// &&
															// relationing_entity.equals("PartyRole");

			// List<String> fkNames_relationing_entity = sa.getFieldNames(
			// relationing_entity, tableName);
			// List<String> pkNames_relationing_entity = sa.getRelFieldNames(
			// relationing_entity, tableName);

			//ModelEntity r_entity = delegator.getModelEntity(relationing_entity);
			
			for (ModelRelation mr : entity.getRelationsOneList()) {
				if (!relationing_entity.equals(mr.getRelEntityName()))
					continue;

				List<String> fkNames_relationing_entity = FastList
						.newInstance();
				List<String> pkNames_relationing_entity = FastList
						.newInstance();
				for (ModelKeyMap mkm : mr.getKeyMaps()) {
					fkNames_relationing_entity.add(mkm.getFieldName());
					pkNames_relationing_entity.add(mkm.getRelFieldName());
				}

				// List<String> pkNames_relationing_entity =
				// delegator.getModelEntity(relationing_entity).getPkFieldNames();

				// ex: tableName = DeliveryType (OrderHeader),
				// relationing_entity =
				// DeliveryType (OrderType)
				// fkNames_relationing_entity of DeliveryType (OrderHeader) =
				// parentDeliveryTypeId (orderTypeId),
				// pkNames_relationing_entity of DeliveryType (OrderType) =
				// deliveryTypeId (orderTypeId)

				List<Object> pkV = FastList.newInstance();

				for (int j = 0; j < fkNames_relationing_entity.size(); j++) {
					pkV.add(e.gv.get(fkNames_relationing_entity.get(j)));
				}
				String sub_info = "";
				for (int j = 0; j < pkNames_relationing_entity.size(); j++)
					sub_info += "[" + pkNames_relationing_entity.get(j) + ","
							+ pkV.get(j) + "] ";

				if (DEBUG)
					Debug.log(module
							+ "::establishCopiedRecordTables, for b time = "
							+ t
							+ ", from table ["
							+ tableName
							+ ","
							+ toString(pkValues)
							+ "], get records relationing_entity = ["
							+ relationing_entity
							+ ", sub_info = "
							+ sub_info
							+ "] START call establishCopiedRecordPredecessorTables");
				String key = composeTablePKValue(relationing_entity, pkV);
				// if (!tablePKValue.contains(key)) {
				if (map.get(key) == null) {
					establishCopiedRecordPredecessorTables(delegator,
							relationing_entity, pkV, table, tablePKValue, map,
							in, out, e, maxSz);
				} else {
					TablePrimaryKeyValue frome = map.get(key);
					if (out.get(frome) == null)
						out.put(frome, new HashSet<TablePrimaryKeyValue>());

					// if(frome.tableName.equals("CountryAddressFormat") &&
					// e.tableName.equals("Geo")){
					if (DEBUG)
						Debug.log(module
								+ "::establishCopiedRecordTables info = "
								+ info
								+ " BUG EXISTS?? add to OUT frome = "
								+ composeTablePKValue(frome.tableName,
										frome.pkValues) + " -> "
								+ composeTablePKValue(e.tableName, e.pkValues));
					// }else
					out.get(frome).add(e);

					if (in.get(e) == null)
						in.put(e, new HashSet<TablePrimaryKeyValue>());
					in.get(e).add(frome);

					if (DEBUG)
						Debug.log(module
								+ "::establishCopiedRecordTables [EXISTS] for b time = "
								+ t
								+ ", from table ["
								+ tableName
								+ ","
								+ toString(pkValues)
								+ "], get records relationing_entity = ["
								+ relationing_entity
								+ ", sub_info = "
								+ sub_info
								+ "] NOT call establishCopiedRecordPredecessorTables");
				}
			}

			// if(relationing_entity.equals(tableName)){
			// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
			// "), relationing_entity = tableName->BUG???");
			// return;
			// }

			/*
			 * 
			 * List<Object> pkV = FastList.newInstance(); for(int j = 0; j <
			 * pkNames_relationing_entity.size(); j++){from
			 * pkV.add(e.gv.get(pkNames_relationing_entity.get(j))); }
			 * List<GenericValue> GV = getRecordsWithForeignKeyValues(
			 * delegator, relationing_entity, pkNames_relationing_entity, pkV);
			 * 
			 * 
			 * 
			 * String sub_info = ""; for (int j = 0; j <
			 * pkNames_relationing_entity.size(); j++) sub_info += "[" +
			 * pkNames_relationing_entity.get(j) + "," + pkV.get(j) + "] ";
			 * Debug.log(module + "::establishCopiedRecordTables, for b time = "
			 * + t + ", from table " + tableName +
			 * ", get records relationing_entity = " + relationing_entity +
			 * ", sub_info = " + sub_info + ", GV = " + GV.size());
			 * 
			 * for (GenericValue gv : GV) { //DataStrings d = new DataStrings();
			 * List<Object> d = FastList.newInstance(); for (String pkn :
			 * pkNames_relationing_entity) { d.add(gv.get(pkn)); } String key =
			 * composeTablePKValue(relationing_entity, d);
			 * if(tablePKValue.contains(key)){ Debug.log(module +
			 * "::establishCopiedRecordTables, [EXISTS] time = " + t +from
			 * ", from table " + tableName +
			 * ", get records relationing_entity = " + relationing_entity +
			 * ", sub_info = " + sub_info + ", GV = " + GV.size() + ", key = " +
			 * key + " EXISTS"); continue; }
			 * 
			 * Debug.log(module +
			 * "::establishCopiedRecordTables, for b START establishCopiedRecordPredecessorTables with d = "
			 * + toString(d)); //tablePKValue.add(key);
			 * establishCopiedRecordPredecessorTables
			 * (delegator,relationing_entity,d,table,tablePKValue,maxSz);
			 * //tablePKValue.add(key); }
			 */
		}

	}

	public static void establishCopiedRecordPredecessorTables(
			Delegator delegator, String tableName, List<Object> pkValues,
			List<TablePrimaryKeyValue> table, Set<String> tablePKValue,
			HashMap<String, TablePrimaryKeyValue> map,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out,
			TablePrimaryKeyValue from, int maxSz) {
		// collect records predecessors (with recursion) record (tableName,
		// pkValues) into table
		// tablePKValue store set of records

		String info = composeTablePKValue(tableName, pkValues);

		if (sa == null) {
			sa = new SCCAnalyzer(delegator);
			sa.analyze();
			sa.computeTOPO();
		}
		// Debug.log(module + "::establishCopiedRecordPredecessorTables(" +
		// tableName + "), table.sz = " + table.size() + ", tablePKValue.sz = "
		// + tablePKValue.size());
		// if(maxSz > 0) if(table.size() > maxSz && tablePKValue.size() >
		// maxSz){
		// Debug.log(module + "::establishCopiedRecordTables(" + tableName +
		// "), table.sz = " + table.size() + " > " + maxSz + ", RETURN");
		// return;
		// }

		double t = System.currentTimeMillis() - t0;
		t = t * 0.001;
		// if(t > 20){
		// Debug.log(module + "::establishCopiedRecordTables, time = " + t +
		// " EXPIRED -> BREAK");
		// return;
		// }
		int v = sa.mEntityName2Index.get(tableName);
		// int[] a = sa.getSortedDependedEntityIndex(v);
		int[] b = sa.getSortedDependingEntityIndex(v);// predecessors

		List<String> pkNames = delegator.getModelEntity(tableName)
				.getPkFieldNames();

		ModelEntity entity = delegator.getModelEntity(tableName);
		
		TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName, pkNames,
				pkValues);
		e.gv = findOne(delegator, tableName, pkNames, pkValues);
		if (e.gv == null) {
			// Debug.log(module + "::establishCopiedRecordPredecessorTables(" +
			// tableName + "), e.gv = null -> RETURN");
			return;
		}
		table.add(e);
		String KEY = composeTablePKValue(tableName, pkValues);
		tablePKValue.add(KEY);
		map.put(KEY, e);

		if (in.get(from) == null)
			in.put(from, new HashSet<TablePrimaryKeyValue>());
		out.put(e, new HashSet<TablePrimaryKeyValue>());
		in.get(from).add(e);

		// if(e.tableName.equals("CountryAddressFormat") &&
		// from.tableName.equals("Geo")){
		Debug.log(module + "::establishCopiedRecordPredecessorTables info = "
				+ info + " BUG?? add to OUT e = "
				+ composeTablePKValue(e.tableName, e.pkValues) + " -> "
				+ composeTablePKValue(from.tableName, from.pkValues));
		// }else
		out.get(e).add(from);

		for (int i = 0; i < b.length; i++) {
			int x = b[i];
			String relationing_entity = sa.entityNames[x];
			if (!tableExists(relationing_entity))
				continue;

			// if(sa.defineDepends(relationing_entity, tableName)){
			// Debug.log(module +
			// "::establishCopiedRecordPredecessorTables, IGNORE CASE " +
			// tableName + " <- " + relationing_entity);
			// continue;
			// }

			boolean DEBUG = tableName.equals("InventoryItemDetail");
					

			// List<String> fkNames_relationing_entity = sa.getFieldNames(
			// relationing_entity, tableName);
			// List<String> pkNames_relationing_entity = sa.getRelFieldNames(
			// relationing_entity, tableName);ModelEntity r_entity =
			// delegator.getModelEntity(relationing_entity);
			
			//ModelEntity r_entity = delegator.getModelEntity(relationing_entity);
			
			for (ModelRelation mr : entity.getRelationsOneList()) {
				if (!relationing_entity.equals(mr.getRelEntityName()))
					continue;

				List<String> fkNames_relationing_entity = FastList
						.newInstance();
				List<String> pkNames_relationing_entity = FastList
						.newInstance();
				for (ModelKeyMap mkm : mr.getKeyMaps()) {
					fkNames_relationing_entity.add(mkm.getFieldName());
					pkNames_relationing_entity.add(mkm.getRelFieldName());
				}

				// List<String> pkNames_relationing_entity =
				// delegator.getModelEntity(relationing_entity).getPkFieldNames();

				// ex: tableName = DeliveryType (OrderHeader),
				// relationing_entity =
				// DeliveryType (OrderType)
				// fkNames_relationing_entity of DeliveryType (OrderHeader) =
				// parentDeliveryTypeId (orderTypeId),
				// pkNames_relationing_entity of DeliveryType (OrderType) =
				// deliveryTypeId (orderTypeId)

				List<Object> pkV = FastList.newInstance();

				for (int j = 0; j < fkNames_relationing_entity.size(); j++) {
					pkV.add(e.gv.get(fkNames_relationing_entity.get(j)));
				}
				String sub_info = "";
				for (int j = 0; j < pkNames_relationing_entity.size(); j++)
					sub_info += "[" + pkNames_relationing_entity.get(j) + ","
							+ pkV.get(j) + "] ";
				if (DEBUG)
					Debug.log(module
							+ "::establishCopiedRecordPredecessorTables, for b time = "
							+ t
							+ ", from table "
							+ tableName
							+ " with pkValues = "
							+ toString(pkValues)
							+ ", get records relationing_entity = "
							+ relationing_entity
							+ ", sub_info = "
							+ sub_info
							+ " START call recursive establishCopiedRecordPredecessorTables");
				String key = composeTablePKValue(relationing_entity, pkV);
				// if(!tablePKValue.contains(key)){
				if (map.get(key) == null) {
					establishCopiedRecordPredecessorTables(delegator,
							relationing_entity, pkV, table, tablePKValue, map,
							in, out, e, maxSz);
				} else {
					TablePrimaryKeyValue frome = map.get(key);
					if (out.get(frome) == null)
						out.put(frome, new HashSet<TablePrimaryKeyValue>());

					// if(frome.tableName.equals("CountryAddressFormat") &&
					// e.tableName.equals("Geo")){
					if(DEBUG)
						Debug.log(module
							+ "::establishCopiedRecordPredecessorTables info = "
							+ info
							+ " BUG EXISTS?? add to OUT frome = "
							+ composeTablePKValue(frome.tableName,
									frome.pkValues) + " -> "
							+ composeTablePKValue(e.tableName, e.pkValues));
					// }else
					out.get(frome).add(e);

					if (in.get(e) == null)
						in.put(e, new HashSet<TablePrimaryKeyValue>());
					in.get(e).add(frome);

					if (DEBUG)
						Debug.log(module
								+ "::establishCopiedRecordPredecessorTables [EXISTS] for b time = "
								+ t
								+ ", from table "
								+ tableName
								+ " with pkValues = "
								+ toString(pkValues)
								+ ", get records relationing_entity = "
								+ relationing_entity
								+ ", sub_info = "
								+ sub_info
								+ " NOT call recursive establishCopiedRecordPredecessorTables");
				}
			}
			// if(relationing_entity.equals(tableName)){
			// Debug.log(module + "::establishCopiedRecordPredecessorTables(" +
			// tableName + "), relationing_entity = tableName->BUG???");
			// return;
			// }

			/*
			 * List<String> pkNames_relationing_entity = delegator
			 * .getModelEntity(relationing_entity) .getPkFieldNames();
			 * 
			 * List<Object> pkV = FastList.newInstance(); for(int j = 0; j <
			 * pkNames_relationing_entity.size(); j++){
			 * pkV.add(e.gv.get(pkNames_relationing_entity.get(j))); }
			 * List<GenericValue> GV = getRecordsWithForeignKeyValues(
			 * delegator, relationing_entity, pkNames_relationing_entity, pkV);
			 * 
			 * 
			 * 
			 * String sub_info = ""; for (int j = 0; j <
			 * pkNames_relationing_entity.size(); j++) sub_info += "[" +
			 * pkNames_relationing_entity.get(j) + "," + pkV.get(j) + "] ";
			 * Debug.log(module +
			 * "::establishCopiedRecordPredecessorTables, time = " + t +
			 * ", from table " + tableName +
			 * ", get records relationing_entity = " + relationing_entity +
			 * ", sub_info = " + sub_info + ", GV = " + GV.size());
			 * 
			 * for (GenericValue gv : GV) { //DataStrings d = new DataStrings();
			 * List<Object> d = FastList.newInstance(); for (String pkn :
			 * pkNames_relationing_entity) { d.add(gv.get(pkn)); } String key =
			 * composeTablePKValue(relationing_entity, d);
			 * if(tablePKValue.contains(key)){ //Debug.log(module +
			 * "::establishCopiedRecordPredecessorTables, time = " + t +
			 * ", from table " + tableName // +
			 * ", get records relationing_entity = " + relationing_entity // +
			 * ", sub_info = " + sub_info + ", GV = " // + GV.size() +
			 * ", key = " + key + " EXISTS"); continue; } Debug.log(module +
			 * "::establishCopiedRecordPredecessorTables, START recursive with d = "
			 * + toString(d)); //tablePKValue.add(key);
			 * establishCopiedRecordPredecessorTables
			 * (delegator,relationing_entity,d,table,tablePKValue,maxSz); }
			 */
		}

	}

	public static boolean predefinedDepends(String t1, String t2) {
		if (depended == null)
			return false;
		for (int k = 0; k < depended.length; k++) {
			if (depended[k][0].equals(t1) && depended[k][1].equals(t2)) {
				return true;
			}
		}
		return false;
	}

	public static TablePrimaryKeyValue[] copyRecordsFromTableAndDependencyIgnoreForeignKeyValues(
			String tableName, Set<List<Object>> primaryKeyValues,
			Delegator delegator, Delegator delegatorbkp, int maxSz,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out
			) {
		try {
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, START, DEBUG "
					+ "primaryKeyValues.sz = " + primaryKeyValues.size());
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}

			List<TablePrimaryKeyValue> tables = FastList.newInstance();
			Set<String> tablePKValue = FastSet.newInstance();
			HashMap<String, TablePrimaryKeyValue> map = new HashMap<String, TablePrimaryKeyValue>();
			
			//Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in = FastMap.newInstance();
			//Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out = FastMap.newInstance();
			// in[e] : set of records that are generated before e
			// out[e]: set of records that are generated after e

			for (List<Object> pkValues : primaryKeyValues) {
				establishCopiedRecordTables(delegator, tableName, pkValues,
						tables, tablePKValue, map, in, out, null, maxSz);
			}

			String des = "records: {";
			int cnt = 0;
			for (TablePrimaryKeyValue t : tables) {
				cnt++;
				des = des + "\"" + t.tableName + "\"";
				if (cnt < tables.size())
					des = des + ",";
			}
			des = des + "}, cnt = " + cnt;
			des = des + "record-dependency: {";
			cnt = 0;
			for (TablePrimaryKeyValue t : tables) {
				if (out.get(t) != null)
					for (TablePrimaryKeyValue t1 : out.get(t)) {
						cnt++;
						des = des
								+ "{"
								+ composeTablePKValue(t.tableName, t.pkValues)
								+ " -> "
								+ composeTablePKValue(t1.tableName, t1.pkValues)
								+ "}, ";
					}
			}
			des = des + "}, nbRecord-Dependency = " + cnt;

			HashSet<String> tableNames = new HashSet<String>();
			HashMap<String, HashSet<String>> mTable2DependedTables = new HashMap<String, HashSet<String>>();
			for (TablePrimaryKeyValue t : tables) {
				tableNames.add(t.tableName);
			}
			for (String tn : tableNames)
				mTable2DependedTables.put(tn, new HashSet<String>());
			for (TablePrimaryKeyValue t : tables) {
				if (out.get(t) != null)
					for (TablePrimaryKeyValue t1 : out.get(t)) {
						mTable2DependedTables.get(t.tableName)
								.add(t1.tableName);
					}
			}

			des = des + "tableNames: {";
			cnt = 0;
			for (String tn : tableNames) {
				cnt++;
				des = des + "\"" + tn + "\"";
				if (cnt < tableNames.size())
					des = des + ",";
			}
			des = des + "}, nbTables = " + cnt;

			des = des + "table-dependency : {";
			cnt = 0;
			for (String tn : tableNames) {
				for (String tn1 : mTable2DependedTables.get(tn)) {
					cnt++;
					des = des + "{\"" + tn + "\",\"" + tn1 + "\"},";
				}
			}
			des = des + "}, nbTable-Dependency = " + cnt;

			des = des + ", bi-dependency = {";
			for (String t1 : tableNames) {
				for (String t2 : tableNames) {
					if (mTable2DependedTables.get(t1).contains(t2)
							&& mTable2DependedTables.get(t2).contains(t1)) {
						des = des + "{\"" + t1 + "\",\"" + t2 + "\"}, ";
					}
				}
			}
			des = des + "}";

			// SORT
			TablePrimaryKeyValue[] A = new TablePrimaryKeyValue[tables.size()];
			int ia = -1;
			for (TablePrimaryKeyValue t : tables) {
				ia++;
				A[ia] = t;
			}
			for (int i = 0; i < A.length; i++) {
				for (int j = i + 1; j < A.length; j++) {
					if (predefinedDepends(A[i].tableName, A[j].tableName)) {
						TablePrimaryKeyValue tmp = A[i];
						A[i] = A[j];
						A[j] = tmp;
					}
				}
			}

			des = des
					+ " TEST predefined-depended = "
					+ predefinedDepends("OrderItemShipGroupAssoc",
							"OrderItemShipGroup");

			des = des + ", SORTED A = [";
			for (int i = 0; i < A.length; i++) {
				des = des + composeTablePKValue(A[i].tableName, A[i].pkValues)
						+ "],";
			}

			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, description = "
					+ des);
			if (true)
				return A;
			
			A = topoSort(tables,in,out);

			return A;
			/*
			 * TablePrimaryKeyValue[] A = new
			 * TablePrimaryKeyValue[tables.size()]; int[] topo = new
			 * int[A.length]; //int[] a = new int[A.length]; for (int i = 0; i <
			 * tables.size(); i++) { A[i] = tables.get(i); //a[i] = i; int idx =
			 * sa.mEntityName2Index.get(A[i].tableName); int scc = sa.scc[idx] -
			 * 1; int idxTOPO = sa.indexTOPO[scc]; topo[i] = idxTOPO; } for (int
			 * i = 0; i < A.length; i++) { // String t1 = A[i].tableName; // int
			 * i1 = sa.mEntityName2Index.get(A[i].tableName); for (int j = i +
			 * 1; j < A.length; j++) { // int j1 =
			 * sa.mEntityName2Index.get(A[j].tableName); //
			 * if(sa.indexTOPO[sa.scc[i1]-1] > // sa.indexTOPO[sa.scc[j1]-1]){
			 * 
			 * //boolean swap = topo[i] > topo[j] // || (topo[i] == topo[j] &&
			 * A[i].tableName // .compareTo(A[j].tableName) > 0); boolean swap =
			 * false; if(topo[i] > topo[j]) swap = true; if(topo[i] < topo[j])
			 * swap = false; if(topo[i] == topo[j]){
			 * if(sa.depends(A[i].tableName, A[j].tableName)) swap = true; else{
			 * if(A[i].tableName .compareTo(A[j].tableName) > 0) swap = true; }
			 * }
			 * 
			 * if (swap) { int t = topo[i]; topo[i] = topo[j]; topo[j] = t;
			 * TablePrimaryKeyValue tmp = A[i]; A[i] = A[j]; A[j] = tmp; } } }
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, FINISHED"
			 * ); if (true) return A;
			 */

			/*
			 * Map<String, Set<DataStrings>> mTableName2PrimaryKeyValues = new
			 * HashMap<String, Set<DataStrings>>();// FastMap.newInstance();
			 * mTableName2PrimaryKeyValues.put(tableName, primaryKeyValues);
			 * 
			 * List<TablePrimaryKeyValue> tablePrimaryKeyValues = FastList
			 * .newInstance();// new ArrayList<TablePrimaryKeyValue>();
			 * Queue<TablePrimaryKeyValue> QT = new LinkedList<>(); ModelEntity
			 * table = delegator.getModelEntity(tableName); List<String> pkNames
			 * = table.getPkFieldNames(); for (DataStrings D : primaryKeyValues)
			 * { TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName,
			 * pkNames, D.getData());
			 * 
			 * GenericValue gv = findOne(delegator, tableName, pkNames, D); e.gv
			 * = gv; QT.add(e); tablePrimaryKeyValues.add(e); } Debug.log(module
			 * + "::copyRecordsFromTableAndDependency, START queue QT"); while
			 * (QT.size() > 0) { TablePrimaryKeyValue tpkv = QT.remove(); String
			 * tblName = tpkv.tableName; ModelEntity tbl =
			 * delegator.getModelEntity(tblName); List<String> prkNames =
			 * tbl.getPkFieldNames(); int v = sa.mEntityName2Index.get(tblName);
			 * 
			 * int[] a = new int[sa.A[v].size()]; int idx = -1; for (int x :
			 * sa.A[v]) { idx++; a[idx] = x; } for (int i = 0; i < a.length;
			 * i++) { for (int j = i + 1; j < a.length; j++) { if
			 * (sa.indexTOPO[sa.scc[a[i]] - 1] > sa.indexTOPO[sa.scc[a[j]] - 1])
			 * { int tmp = a[i]; a[i] = a[j]; a[j] = tmp; } } } String info =
			 * tblName + ": "; for (int i = 0; i < a.length; i++) { int x =
			 * a[i]; String relationed_entity = sa.entityNames[x]; info = info +
			 * "[" + i + "," + relationed_entity + ", topo " +
			 * sa.indexTOPO[sa.scc[a[i]] - 1] + "] "; } Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, CONSIDER INFO " + info);
			 * 
			 * // for (int x : sa.A[v]) { for (int i = 0; i < a.length; i++) {
			 * int x = a[i]; String relationed_entity = sa.entityNames[x];
			 * List<String> pkNames_relationed_entity = delegator
			 * .getModelEntity(relationed_entity) .getPkFieldNames();
			 * 
			 * List<String> frkNames = sa.getFieldNames(tblName,
			 * relationed_entity); List<DataStrings> data =
			 * getPrimaryKeyValues(delegator, relationed_entity, frkNames,
			 * tpkv.pkValues);
			 * 
			 * List<GenericValue> GV = getRecordsWithForeignKeyValues(
			 * delegator, relationed_entity, frkNames, tpkv.pkValues); String
			 * sub_info = ""; for (int j = 0; j < frkNames.size(); j++) sub_info
			 * += "[" + frkNames.get(j) + "," + tpkv.pkValues.get(j) + "] ";
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, from table " + tblName +
			 * ", get records " + relationed_entity + ", sub_info = " + sub_info
			 * + ", GV = " + GV.size());
			 * 
			 * // for (DataStrings d : data) { for (GenericValue gv : GV) { //
			 * setNULLForeighKeyValues(relationed_entity, gv, // delegator);
			 * DataStrings d = new DataStrings(); for (String pkn :
			 * pkNames_relationed_entity) { d.add(gv.getString(pkn)); }
			 * 
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, from table " + tblName +
			 * " consider create table(" + relationed_entity + ") with data " +
			 * d.toString()); TablePrimaryKeyValue e = new TablePrimaryKeyValue(
			 * relationed_entity, pkNames_relationed_entity, d.getData()); e.gv
			 * = gv;
			 * 
			 * if (!checkExists(tablePrimaryKeyValues, e)) { QT.add(e);
			 * tablePrimaryKeyValues.add(e); Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, from table " + tblName +
			 * " decide create table(" + relationed_entity + ") with data " +
			 * d.toString());
			 * 
			 * } }
			 * 
			 * } } for (int i = 0; i < tablePrimaryKeyValues.size(); i++) {
			 * TablePrimaryKeyValue e = tablePrimaryKeyValues.get(i); if
			 * (tableExists(e.tableName)) { Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, START create entity " +
			 * e.toString());
			 * 
			 * List<String> fieldNames = delegator.getModelEntity(
			 * e.tableName).getAllFieldNames();
			 * 
			 * boolean ok = createEntity(e, delegatorbkp, fieldNames,
			 * getFkNamesExclusivePkNames(e.tableName, delegator)); // boolean
			 * ok = true; // delegatorbkp.create(e.gv);
			 * 
			 * if (ok) Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, created entity " +
			 * e.toString() + " OK"); else { Debug.log(module +
			 * "::copyRecordsFromTableAndDependency, created entity " +
			 * e.toString() + " FAILED -> BREAK"); break; } } else {
			 * Debug.log(module + "::copyRecordsFromTableAndDependency, entity "
			 * + e.tableName + " does not exist"); } }
			 */

			// restore values of fkNames
			// for(String tblName: mTableName2PrimaryKeyValues.keySet()){
			// List<UPDATEINFO> LUI = mTableName2UpdateInfo.get(tblName);
			// updateTables(delegatorbkp, tableName, LUI);
			// }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	public static TablePrimaryKeyValue[] topoSort(List<TablePrimaryKeyValue> tables, 
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in,
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out){
		
		TablePrimaryKeyValue[] A = new TablePrimaryKeyValue[tables.size()];
		
		HashMap<TablePrimaryKeyValue, Integer> inDeg = new HashMap<TablePrimaryKeyValue, Integer>();
		for (TablePrimaryKeyValue e : tables)
			inDeg.put(e, 0);
		for (TablePrimaryKeyValue e : tables) {
			if (out.get(e) != null)
				for (TablePrimaryKeyValue e1 : out.get(e)) if(tables.contains(e1)) {
					int d = inDeg.get(e1) + 1;
					inDeg.put(e1, d);
				}
		}
		for (TablePrimaryKeyValue e : tables) {
			String s_in = "";
			if (in.get(e) != null)
				for (TablePrimaryKeyValue e1 : in.get(e))if(tables.contains(e1))
					s_in += "[" + e1.tableName + ","
							+ toString(e1.pkValues) + "], ";
			String s_out = "";
			if (out.get(e) != null)
				for (TablePrimaryKeyValue e1 : out.get(e))if(tables.contains(e1))
					s_out += "[" + e1.tableName + ","
							+ toString(e1.pkValues) + "], ";
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, table "
					+ "[" + e.tableName + "," + toString(e.pkValues)
					+ "], inDeg = " + inDeg.get(e));
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, in = "
					+ s_in);
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, out = "
					+ s_out);

		}

		Queue<TablePrimaryKeyValue> Q = new LinkedList<TablePrimaryKeyValue>();
		for (TablePrimaryKeyValue e : tables) {
			// if(inDeg.get(e) != null)
			if (inDeg.get(e) == 0)
				Q.add(e);
		}
		int idx = -1;
		while (Q.size() > 0) {
			TablePrimaryKeyValue e = Q.remove();
			idx++;
			A[idx] = e;
			if (out.get(e) != null)
				for (TablePrimaryKeyValue e1 : out.get(e)) if(tables.contains(e1)){
					int d = inDeg.get(e1) - 1;
					inDeg.put(e1, d);
					if (d == 0)
						Q.add(e1);
				}
		}
		Debug.log(module
				+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues, FINISHED, tables.sz = "
				+ tables.size() + ", idx = " + idx);
		return A;
	}
	public static void copyRecordsFromTableAndDependencyUpdateForeignKeyValues(
			String tableName, Set<DataStrings> primaryKeyValues,
			Delegator delegator, Delegator delegatorbkp) {
		try {
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, START");
			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}
			/*
			 * Map<String, Set<DataStrings>> mTableName2PrimaryKeyValues = new
			 * HashMap<String, Set<DataStrings>>();// FastMap.newInstance();
			 * mTableName2PrimaryKeyValues.put(tableName, primaryKeyValues);
			 * 
			 * List<TablePrimaryKeyValue> tablePrimaryKeyValues = FastList
			 * .newInstance();// new ArrayList<TablePrimaryKeyValue>();
			 * Queue<TablePrimaryKeyValue> QT = new LinkedList<>(); ModelEntity
			 * table = delegator.getModelEntity(tableName); List<String> pkNames
			 * = table.getPkFieldNames(); for (DataStrings D : primaryKeyValues)
			 * { TablePrimaryKeyValue e = new TablePrimaryKeyValue(tableName,
			 * pkNames, D.getData());
			 * 
			 * GenericValue gv = findOne(delegator, tableName, pkNames, D); e.gv
			 * = gv; QT.add(e); tablePrimaryKeyValues.add(e); } Debug.log(module
			 * +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, START queue QT"
			 * ); while (QT.size() > 0) { TablePrimaryKeyValue tpkv =
			 * QT.remove(); String tblName = tpkv.tableName; ModelEntity tbl =
			 * delegator.getModelEntity(tblName); List<String> prkNames =
			 * tbl.getPkFieldNames(); int v = sa.mEntityName2Index.get(tblName);
			 * 
			 * int[] a = new int[sa.A[v].size()]; int idx = -1; for (int x :
			 * sa.A[v]) { idx++; a[idx] = x; } for (int i = 0; i < a.length;
			 * i++) { for (int j = i + 1; j < a.length; j++) { if
			 * (sa.indexTOPO[sa.scc[a[i]] - 1] > sa.indexTOPO[sa.scc[a[j]] - 1])
			 * { int tmp = a[i]; a[i] = a[j]; a[j] = tmp; } } } String info =
			 * tblName + ": "; for (int i = 0; i < a.length; i++) { int x =
			 * a[i]; String relationed_entity = sa.entityNames[x]; info = info +
			 * "[" + i + "," + relationed_entity + ", topo " +
			 * sa.indexTOPO[sa.scc[a[i]] - 1] + "] "; } Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, CONSIDER INFO "
			 * + info);
			 * 
			 * // for (int x : sa.A[v]) { for (int i = 0; i < a.length; i++) {
			 * int x = a[i]; String relationed_entity = sa.entityNames[x];
			 * List<String> pkNames_relationed_entity = delegator
			 * .getModelEntity(relationed_entity) .getPkFieldNames();
			 * 
			 * List<String> frkNames = sa.getFieldNames(tblName,
			 * relationed_entity); List<DataStrings> data =
			 * getPrimaryKeyValues(delegator, relationed_entity, frkNames,
			 * tpkv.pkValues);
			 * 
			 * List<GenericValue> GV = getRecordsWithForeignKeyValues(
			 * delegator, relationed_entity, frkNames, tpkv.pkValues); String
			 * sub_info = ""; for (int j = 0; j < frkNames.size(); j++) sub_info
			 * += "[" + frkNames.get(j) + "," + tpkv.pkValues.get(j) + "] ";
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, from table "
			 * + tblName + ", get records " + relationed_entity +
			 * ", sub_info = " + sub_info + ", GV = " + GV.size());
			 * 
			 * // for (DataStrings d : data) { for (GenericValue gv : GV) { //
			 * setNULLForeighKeyValues(relationed_entity, gv, // delegator);
			 * DataStrings d = new DataStrings(); for (String pkn :
			 * pkNames_relationed_entity) { d.add(gv.getString(pkn)); }
			 * 
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, from table "
			 * + tblName + " consider create table(" + relationed_entity +
			 * ") with data " + d.toString()); TablePrimaryKeyValue e = new
			 * TablePrimaryKeyValue( relationed_entity,
			 * pkNames_relationed_entity, d.getData()); e.gv = gv;
			 * 
			 * if (!checkExists(tablePrimaryKeyValues, e)) { QT.add(e);
			 * tablePrimaryKeyValues.add(e); Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, from table "
			 * + tblName + " decide create table(" + relationed_entity +
			 * ") with data " + d.toString());
			 * 
			 * } }
			 * 
			 * } } for (int i = 0; i < tablePrimaryKeyValues.size(); i++) {
			 * TablePrimaryKeyValue e = tablePrimaryKeyValues.get(i); if
			 * (tableExists(e.tableName)) { Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, START create entity "
			 * + e.toString());
			 * 
			 * List<String> fieldNames = delegator.getModelEntity(
			 * e.tableName).getAllFieldNames();
			 * 
			 * boolean ok = createEntityUpdateForeignKeyValues(e, delegatorbkp,
			 * fieldNames, getFkNamesExclusivePkNames(e.tableName, delegator));
			 * 
			 * // boolean ok = true; // delegatorbkp.create(e.gv);
			 * 
			 * if (ok) Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, created entity "
			 * + e.toString() + " OK"); else { Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, created entity "
			 * + e.toString() + " FAILED -> BREAK"); break; } } else {
			 * Debug.log(module +
			 * "::copyRecordsFromTableAndDependencyUpdateForeignKeyValues, entity "
			 * + e.tableName + " does not exist"); } }
			 */
			// restore values of fkNames
			// for(String tblName: mTableName2PrimaryKeyValues.keySet()){
			// List<UPDATEINFO> LUI = mTableName2UpdateInfo.get(tblName);
			// updateTables(delegatorbkp, tableName, LUI);
			// }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String convertChar(char c) {
		String cc = c + "";
		for (int i = 0; i < MAJUCULES.length(); i++)
			if (c == MAJUCULES.charAt(i)) {
				cc = "_" + MINUCULES.charAt(i);
				break;
			}
		return cc;
	}

	public static String downCaseChar(char c) {
		String cc = c + "";
		for (int i = 0; i < MAJUCULES.length(); i++)
			if (c == MAJUCULES.charAt(i)) {
				cc = "" + MINUCULES.charAt(i);
				break;
			}
		return cc;
	}

	public static String convertFromModelEntityName2TableName(String entityName) {
		String tableName = "";
		for (int i = 0; i < entityName.length(); i++) {
			char c = entityName.charAt(i);
			if (i == 0)
				tableName = tableName + downCaseChar(c);
			else {
				tableName = tableName + convertChar(c);
			}
		}
		return tableName;

	}

	public static boolean tableExists(String entityName) {

		// if(true) return isNotTable(entityName);
		if (true)
			return isTable(entityName);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection cnn = DriverManager
					.getConnection(
							"jdbc:mysql://127.0.0.1/dmspass?autoReconnect=true&amp;characterEncoding=UTF-8",
							"root", "123456");

			String tableName = convertFromModelEntityName2TableName(entityName);
			// Debug.log(module + "::tableExists, entityName = " + entityName
			// + ", tableName = " + tableName);
			DatabaseMetaData dbm = cnn.getMetaData();
			// check if "employee" table is there
			ResultSet tables = dbm.getTables(null, null, tableName, null);
			if (tables.next()) {
				cnn.close();
				return true;
			} else {
				cnn.close();
				return false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// cnn.close();
			return false;
		}

	}

	public static void createEntities(TablePrimaryKeyValue[] T,
			Delegator delegator, Delegator delegatorbkp) {
		for (int i = 0; i < T.length; i++) {
			TablePrimaryKeyValue e = T[i];
			if (tableExists(e.tableName)) {
				// Debug.log(module + "::createEntities, START create entity "
				// + e.toString());
				ModelEntity me = delegator.getModelEntity(e.tableName);
				List<String> fieldNames = me.getAllFieldNames();

				boolean ok = createEntity(e, delegatorbkp, fieldNames,
						me.getPkFieldNames(),
						getFkNamesExclusivePkNames(e.tableName, delegator));
				// boolean ok = true;
				// delegatorbkp.create(e.gv);

				if (ok)
					Debug.log(module + "::createEntities, created entity "
							+ e.toString() + " OK");
				else {
					Debug.log(module + "::createEntities, created entity "
							+ e.toString() + " FAILED -> BREAK");
					break;
				}
			} else {
				Debug.log(module + "::createEntities, entity " + e.tableName
						+ " does not exist");
			}
		}

	}

	public static void createEntitiesUpdateForeignKeyValues(
			TablePrimaryKeyValue[] T, Delegator delegator,
			Delegator delegatorbkp) {
		for (int i = 0; i < T.length; i++) {
			TablePrimaryKeyValue e = T[i];
			if (tableExists(e.tableName)) {
				// Debug.log(module +
				// "::createEntitiesUpdateForeignKeyValues, START create entity "
				// + e.toString());

				List<String> fieldNames = delegator.getModelEntity(e.tableName)
						.getAllFieldNames();

				boolean ok = createEntityUpdateForeignKeyValues(e,
						delegatorbkp, fieldNames,
						getFkNamesExclusivePkNames(e.tableName, delegator));
				// boolean ok = true;
				// delegatorbkp.create(e.gv);

				if (ok)
					Debug.log(module
							+ "::createEntitiesUpdateForeignKeyValues, created-updated entity "
							+ e.toString() + " OK");
				else {
					Debug.log(module
							+ "::createEntitiesUpdateForeignKeyValues, created-updated entity "
							+ e.toString() + " FAILED -> BREAK");
					break;
				}
			} else {
				Debug.log(module
						+ "::createEntitiesUpdateForeignKeyValues, entity "
						+ e.tableName + " does not exist");
			}
		}

	}

	public static Map<String, Object> getDependencyList(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		retSucc.put("result", "N/A");
		try {

			Delegator delegator = ctx.getDelegator();
			/*
			 * ModelEntity mgeo = delegator.getModelEntity("Geo"); ModelEntity
			 * mcaf = delegator.getModelEntity("CountryAddressFormat");
			 * for(ModelRelation mr: mgeo.getRelationsOneList()){ //for(int i =
			 * 0; i < mgeo.getRelationsSize(); i++){ //ModelRelation mr =
			 * mgeo.getRelation(i); Debug.log(module +
			 * "::getDependencyList, Geo[" + mgeo.getPkFieldNames() +
			 * "] has relation with " + mr.getRelEntityName() + ":");
			 * for(ModelKeyMap mkm: mr.getKeyMaps()){ Debug.log(module +
			 * "::getDependencyList, key-map field " + mkm.getFieldName() +
			 * ", rel-field " + mkm.getRelFieldName()); } } Debug.log(module +
			 * "::getDependencyList --------------------------------------------"
			 * ); for(ModelRelation mr: mcaf.getRelationsOneList()){ //for(int i
			 * = 0; i < mcaf.getRelationsSize(); i++){ // ModelRelation mr =
			 * mcaf.getRelation(i);
			 * 
			 * Debug.log(module + "::getDependencyList, CountryAddressFormat[" +
			 * mcaf.getPkFieldNames() + "] has relation with " +
			 * mr.getRelEntityName() + ":"); for(ModelKeyMap mkm:
			 * mr.getKeyMaps()){ Debug.log(module +
			 * "::getDependencyList, key-map field " + mkm.getFieldName() +
			 * ", rel-field " + mkm.getRelFieldName()); } }
			 */

			if (sa == null) {
				sa = new SCCAnalyzer(delegator);
				sa.analyze();
				sa.computeTOPO();
			}
			String rs = "";
			if (sa.entityNames != null)
				for (int i = 0; i < sa.entityNames.length; i++) {
					for (int j = 0; j < sa.entityNames.length; j++) {
						if (sa.depends(i, j)) {
							rs = rs + "[" + i + "," + j + "] -> ["
									+ sa.entityNames[i] + ","
									+ sa.entityNames[j] + "]; ";
							Debug.log(module + "::getDependencyList GOT [" + i
									+ "," + j + "] -> [" + sa.entityNames[i]
									+ "," + sa.entityNames[j] + "]; ");
						}
					}
				}
			String brs = "";
			if (sa.entityNames != null)
				for (int i = 0; i < sa.entityNames.length; i++) {
					for (int j = 0; j < sa.entityNames.length; j++) {
						if (sa.bidirectionalDepends(i, j)) {
							brs = brs + "[" + i + "," + j + "] <-> ["
									+ sa.entityNames[i] + ","
									+ sa.entityNames[j] + "]; ";
							Debug.log(module + "::getDependencyList GOT [" + i
									+ "," + j + "] <-> [" + sa.entityNames[i]
									+ "," + sa.entityNames[j] + "]; ");
						}
					}
				}

			brs = brs + ", DefineDependency = " + sa.getDefineDependList();

			retSucc.put("result", rs + "***" + brs);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static Map<String, Object> moveBackupOrders(DispatchContext ctx,
			Map<String, ? extends Object> context) {

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		retSucc.put("result", "N/A");
		String viewonly = (String)context.get("viewonly");
		
		try {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			String orderIds = (String) context.get("orderIds");

			List<String> pkValues = FastList.newInstance();
			String[] s = orderIds.split(",");
			for (int i = 0; i < s.length; i++)
				pkValues.add(s[i].trim());
			Debug.log(module + "::moveBackupOrders, orderIds = " + orderIds
					+ ", pkValues.sz = " + pkValues.size());

			Set<List<Object>> primaryKeyValues = new HashSet<List<Object>>();
			for (String orderId : pkValues) {
				List<Object> d = FastList.newInstance();
				d.add(orderId);
				primaryKeyValues.add(d);
			}
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in = FastMap.newInstance();
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out = FastMap.newInstance();
			
			String des = "";
			TablePrimaryKeyValue[] T = copyRecordsFromTableAndDependencyIgnoreForeignKeyValues(
					"OrderHeader", primaryKeyValues, delegator, delegatorbkp,
					-1,in,out);

			HashSet<String> S = new HashSet<String>();
			for(int i = 0; i < T.length; i++)
				S.add(T[i].tableName);
			String table_str = "";
			for(String ts: S) table_str += ts + ",";
			
			for(int i = 0; i < T.length; i++){
				if(out.get(T[i])!=null && out.get(T[i]).contains(T[i])){
					table_str += ", SELF reference " + composeTablePKValue(T[i].tableName, T[i].pkValues) + ",";
					
					out.get(T[i]).remove(T[i]);
					in.get(T[i]).remove(T[i]);
				}
			}
			
			for(int i = 0; i < T.length; i++){
				for(int j = i+1; j < T.length; j++){
					if(out.get(T[i]) != null && out.get(T[i]).contains(T[j]) 
							&& out.get(T[j])!=null && out.get(T[j]).contains(T[i])){
						if(predefinedDepends(T[i].tableName, T[j].tableName)){
							out.get(T[i]).remove(T[j]);
							in.get(T[j]).remove(T[i]);
						}else if(predefinedDepends(T[j].tableName, T[i].tableName)){
							out.get(T[j]).remove(T[i]);
							in.get(T[i]).remove(T[j]);
						}
					}
				}
			}
			Debug.log(module
					+ "::moveBackupOrders, after copyRecordsFromTableAndDependencyIgnoreForeignKeyValues "
					+ "GOT T.sz = " + (T != null ? T.length : "NULL"));
			des = "[";
			for (int i = 0; i < T.length; i++) {
				int idx = sa.mEntityName2Index.get(T[i].tableName);
				int scc = sa.scc[idx] - 1;
				int idxTOPO = sa.indexTOPO[scc];
				String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
						+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
				for (int j = 0; j < T[i].pkNames.size(); j++) {
					item = item + "{\"key\":\"" + T[i].pkNames.get(j)
							+ "\",\"value\":\"" + T[i].pkValues.get(j) + "\"}";
					if (j < T[i].pkNames.size() - 1)
						des = des + ",";
				}
				item = item + "]}";
				if (i < T.length - 1)
					item = item + ",";
				Debug.log(module + "::moveBackupOrders: " + item);
				des = des + item;
			}
			
			
			des = des + "], (T.sz = " + T.length + ")";

			for(int i = 0; i < T.length; i++){
				TablePrimaryKeyValue ti = T[i];
				if(out.get(ti) != null)for(TablePrimaryKeyValue tj: out.get(ti)){
					if(out.get(tj) != null && out.get(tj).contains(ti)){
						des = des + "{" + composeTablePKValue(ti.tableName, ti.pkValues) + "<->"
								+ composeTablePKValue(tj.tableName, tj.pkValues) + "} ";
					}
				}
			}
			
			int sz = T.length;
			
			
			// TOPO SORT on T
			List<TablePrimaryKeyValue> tables = FastList.newInstance();
			List<TablePrimaryKeyValue> tmp = FastList.newInstance();
			for(int i = 0; i < T.length; i++){
				if(T[i].tableName.equals("ProductStore") || T[i].tableName.equals("Facility")
						|| T[i].tableName.equals("Shipment")
						|| T[i].tableName.equals("Delivery")
						){
					tmp.add(T[i]);
				}else
					tables.add(T[i]);
			}
			
			for(int i = 0; i < tmp.size(); i++)
				table_str += ", TMP " + composeTablePKValue(tmp.get(i).tableName, tmp.get(i).pkValues) + ", ";
			
			int cnt_before = tables.size();
			des = des + ", BEFORE-TOPO-SORT, tables.sz = " + cnt_before;
			TablePrimaryKeyValue[] ST = topoSort(tables,in,out);
			int cnt = 0;
			for(int i = 0; i < ST.length; i++) if(ST[i] != null) cnt++;
			
			T = new TablePrimaryKeyValue[sz];
			for(int i = 0; i < tmp.size(); i++) T[i] = tmp.get(i);
			for(int i = 0; i < ST.length; i++) T[tmp.size() + i] = ST[i];
			
			//for(int i = 0; i < ST.length; i++) T[i] = ST[i];
			//for(int i = 0; i < tmp.size(); i++) T[ST.length + i] = tmp.get(i);
			
			des = des + ", AFTER-TOPO-SORT, tables.sz = " + cnt;// + ", TABLES = " + table_str;
			
			if(cnt_before != cnt) des = des + ", BUG BEFORE and AFTER TOPO-SORT GOT different size????";
			
			des = des + ", TABLES = " + table_str;
			
			if(viewonly != null && viewonly.equals("Y")){
				retSucc.put("result", des);
				return retSucc;
			}
			
			if(cnt_before != cnt){// RETURN BUG
				retSucc.put("result", des);
				return retSucc;
			}
			
			createEntities(T, delegator, delegatorbkp);

			createEntitiesUpdateForeignKeyValues(T, delegator, delegatorbkp);

			Map<String, Object> m_in = FastMap.newInstance();
			m_in.put("tableName", "OrderHeader");
			for (String orderId : pkValues) {
				m_in.put("pkValues", orderId);
				Map<String, Object> rs = dispatcher.runSync(
						"removeRecordAndDependedRecords", m_in);
			}

			retSucc.put("result", des);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static Map<String, Object> collectOrderRecordsFromTableAndDependency(
			DispatchContext ctx, Map<String, ? extends Object> context) {

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			String orderIds = (String) context.get("orderIds");

			List<String> pkValues = FastList.newInstance();
			String[] s = orderIds.split(",");
			for (int i = 0; i < s.length; i++)
				pkValues.add(s[i].trim());
			Debug.log(module
					+ "::collectOrderRecordsFromTableAndDependency, orderIds = "
					+ orderIds + ", pkValues.sz = " + pkValues.size());

			Set<List<Object>> primaryKeyValues = new HashSet<List<Object>>();
			for (String orderId : pkValues) {
				List<Object> d = FastList.newInstance();
				d.add(orderId);
				primaryKeyValues.add(d);
			}
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in = FastMap.newInstance();
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out = FastMap.newInstance();
			
			String des = "";
			TablePrimaryKeyValue[] T = copyRecordsFromTableAndDependencyIgnoreForeignKeyValues(
					"OrderHeader", primaryKeyValues, delegator, delegatorbkp,
					-1,in,out);

			Debug.log(module
					+ "::collectOrderRecordsFromTableAndDependency, after copyRecordsFromTableAndDependencyIgnoreForeignKeyValues "
					+ "GOT T.sz = " + (T != null ? T.length : "NULL"));

			HashSet<String> cutTable = new HashSet<String>();
			HashSet<String> notCutTable = new HashSet<String>();

			des = "[";
			for (int i = 0; i < T.length; i++) {
				int idx = sa.mEntityName2Index.get(T[i].tableName);
				int scc = sa.scc[idx] - 1;
				int idxTOPO = sa.indexTOPO[scc];
				String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
						+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
				for (int j = 0; j < T[i].pkNames.size(); j++) {
					item = item + "{\"key\":\"" + T[i].pkNames.get(j)
							+ "\",\"value\":\"" + T[i].pkValues.get(j)
							+ "\",\"cut\":\"" + T[i].cut + "\"}";
					if (j < T[i].pkNames.size() - 1)
						des = des + ",";
				}
				item = item + "]}";
				if (i < T.length - 1)
					item = item + ",";
				Debug.log(module
						+ "::collectOrderRecordsFromTableAndDependency: "
						+ item);
				des = des + item;

				if (T[i].cut)
					cutTable.add(T[i].tableName);
				else
					notCutTable.add(T[i].tableName);
			}
			String s_not_cut_tables = "";
			String s_cut_tables = "";
			for (String st : cutTable)
				s_cut_tables = s_cut_tables + st + ",";
			for (String st : notCutTable)
				s_not_cut_tables = s_not_cut_tables + st + ",";
			des = des + ",{\"cuttable\":" + "\"" + s_cut_tables + "\"}";
			des = des + ",{\"notcuttable\":" + "\"" + s_not_cut_tables + "\"}";

			des = des + "]";

			// createEntities(T, delegator, delegatorbkp);
			// createEntitiesUpdateForeignKeyValues(T, delegator, delegatorbkp);
			// Map<String, Object> in = FastMap.newInstance();
			// in.put("tableName", "OrderHeader");
			// for(String orderId: pkValues){
			// in.put("pkValues", orderId);
			// Map<String, Object> rs =
			// dispatcher.runSync("removeRecordAndDependedRecords", in);
			// }

			retSucc.put("result", des);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static Map<String, Object> collectRecordsFromTableAndDependency(
			DispatchContext ctx, Map<String, ? extends Object> context) {

		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		try {
			Delegator delegator = ctx.getDelegator();
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Delegator delegatorbkp = DelegatorFactory
					.getDelegator("backuppass");
			String tableName = (String) context.get("tableName");
			String in_pkValues = (String) context.get("pkValues");

			List<String> pkValues = FastList.newInstance();
			String[] s = in_pkValues.split(",");
			for (int i = 0; i < s.length; i++)
				pkValues.add(s[i].trim());
			Debug.log(module
					+ "::collectRecordsFromTableAndDependency, orderIds = "
					+ in_pkValues + ", pkValues.sz = " + pkValues.size());

			Set<List<Object>> primaryKeyValues = new HashSet<List<Object>>();
			for (String orderId : pkValues) {
				List<Object> d = FastList.newInstance();
				d.add(orderId);
				primaryKeyValues.add(d);
			}

			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in = FastMap.newInstance();
			Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out = FastMap.newInstance();
			
			String des = "";
			TablePrimaryKeyValue[] T = copyRecordsFromTableAndDependencyIgnoreForeignKeyValues(
					tableName, primaryKeyValues, delegator, delegatorbkp, -1, in, out);

			Debug.log(module
					+ "::collectRecordsFromTableAndDependency, after copyRecordsFromTableAndDependencyIgnoreForeignKeyValues "
					+ "GOT T.sz = " + (T != null ? T.length : "NULL"));

			HashSet<String> cutTable = new HashSet<String>();
			HashSet<String> notCutTable = new HashSet<String>();

			des = "[";
			for (int i = 0; i < T.length; i++) {
				int idx = sa.mEntityName2Index.get(T[i].tableName);
				int scc = sa.scc[idx] - 1;
				int idxTOPO = sa.indexTOPO[scc];
				String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
						+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
				for (int j = 0; j < T[i].pkNames.size(); j++) {
					item = item + "{\"key\":\"" + T[i].pkNames.get(j)
							+ "\",\"value\":\"" + T[i].pkValues.get(j)
							+ "\",\"cut\":\"" + T[i].cut + "\"}";
					if (j < T[i].pkNames.size() - 1)
						des = des + ",";
				}
				item = item + "]}";
				if (i < T.length - 1)
					item = item + ",";
				Debug.log(module + "::collectRecordsFromTableAndDependency: "
						+ item);
				des = des + item;

				if (T[i].cut)
					cutTable.add(T[i].tableName);
				else
					notCutTable.add(T[i].tableName);
			}
			String s_not_cut_tables = "";
			String s_cut_tables = "";
			for (String st : cutTable)
				s_cut_tables = s_cut_tables + st + ",";
			for (String st : notCutTable)
				s_not_cut_tables = s_not_cut_tables + st + ",";
			des = des + ",{\"cuttable\":" + "\"" + s_cut_tables + "\"}";
			des = des + ",{\"notcuttable\":" + "\"" + s_not_cut_tables + "\"}";

			des = des + "]";

			// createEntities(T, delegator, delegatorbkp);
			// createEntitiesUpdateForeignKeyValues(T, delegator, delegatorbkp);
			// Map<String, Object> in = FastMap.newInstance();
			// in.put("tableName", "OrderHeader");
			// for(String orderId: pkValues){
			// in.put("pkValues", orderId);
			// Map<String, Object> rs =
			// dispatcher.runSync("removeRecordAndDependedRecords", in);
			// }

			retSucc.put("result", des);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return retSucc;
	}

	public static Map<String, Object> cutBackupOrders(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> retSucc = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Delegator delegatorbkp = DelegatorFactory.getDelegator("backuppass");
		String s_maxNumberOrders = (String) context.get("maxNumberOrders");

		ModelEntity table = delegator.getModelEntity("OrderHeader");
		List<ModelRelation> rel = table.getRelationsOneList();
		for (ModelRelation mr : rel) {
			String keyMaps = "";
			for (ModelKeyMap mkm : mr.getKeyMaps())
				keyMaps += "[" + mkm.getFieldName() + ","
						+ mkm.getRelFieldName() + "]";
			Debug.log(module + ":: cutBackupOrders relation entity "
					+ mr.getRelEntityName() + ", FK = " + mr.getFkName() + ", "
					+ keyMaps);
		}
		int maxSz = -1;
		try {
			maxSz = Integer.valueOf(s_maxNumberOrders);

			Set<String> entityNames = delegator.getModelReader()
					.getEntityNames();
			for (String e : entityNames) {
				Debug.log(module + ":: cutBackupOrders, entity " + e);
			}
		} catch (Exception ex) {

		}
		if (sa == null) {
			sa = new SCCAnalyzer(delegator);
			sa.analyze();
			Debug.log(module + "::cutBackupOrders, START TOPO sort");
			sa.computeTOPO();
		}
		// String[] P = sa.findPredecesors("OrderHeader");
		// String[] S = sa.findSuccessors("OrderHeader");
		// if (P != null)
		// for (int i = 0; i < P.length; i++)
		// Debug.log(module + "::cutBackupOrders P[" + i + "] = " + P[i]);
		// if (S != null)
		// for (int i = 0; i < S.length; i++)
		// Debug.log(module + "::cutBackupOrders S[" + i + "] = " + S[i]);

		Set<List<Object>> primaryKeyValues = new HashSet<List<Object>>();
		// DataStrings d = new DataStrings();
		List<Object> d = FastList.newInstance();
		d.add("ORDMB120014");
		primaryKeyValues.add(d);

		String des = "";
		// backupOrders("OrderHeader", primaryKeyValues, delegator,
		// delegatorbkp);
		
		Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> in = FastMap.newInstance();
		Map<TablePrimaryKeyValue, HashSet<TablePrimaryKeyValue>> out = FastMap.newInstance();
		
		TablePrimaryKeyValue[] T = copyRecordsFromTableAndDependencyIgnoreForeignKeyValues(
				"OrderHeader", primaryKeyValues, delegator, delegatorbkp, maxSz, in,out);
		des = "[";
		for (int i = 0; i < T.length; i++) {
			int idx = sa.mEntityName2Index.get(T[i].tableName);
			int scc = sa.scc[idx] - 1;
			int idxTOPO = sa.indexTOPO[scc];
			String item = "{" + "\"table\":\"" + T[i].tableName + "-" + idx
					+ "-" + (scc) + "-" + idxTOPO + "\",\"keyvalue\":[";
			for (int j = 0; j < T[i].pkNames.size(); j++) {
				item = item + "{\"key\":\"" + T[i].pkNames.get(j)
						+ "\",\"value\":\"" + T[i].pkValues.get(j) + "\"}";
				if (j < T[i].pkNames.size() - 1)
					des = des + ",";
			}
			item = item + "]}";
			if (i < T.length - 1)
				item = item + ",";
			Debug.log(module
					+ "::copyRecordsFromTableAndDependencyIgnoreForeignKeyValues: "
					+ item);
			des = des + item;
		}
		des = des + "]";
		// copyRecordsFromTableAndDependencyUpdateForeignKeyValues("OrderHeader",
		// primaryKeyValues, delegator, delegatorbkp);

		createEntities(T, delegator, delegatorbkp);

		createEntitiesUpdateForeignKeyValues(T, delegator, delegatorbkp);

		retSucc.put("totalOrders", des);

		if (true)
			return retSucc;

		String beforeDateStr = (String) context.get("beforeDate");
		String maxNumberOrdersStr = (String) context.get("maxNumberOrders");
		Debug.log(module + "::cutBackupOrders, beforeDate " + beforeDateStr);

		Map<String, List<Rel>> mTable2SubTables = FastMap.newInstance();
		{
			List<Rel> L = FastList.newInstance();
			{
				String[] pkNames = { "orderId" };
				String[] fkNames = { "orderId" };
				L.add(new Rel(pkNames, "Delivery", fkNames));
			}
			{
				String[] pkNames = { "orderId" };
				String[] fkNames = { "orderId" };
				L.add(new Rel(pkNames, "OrderItem", fkNames));
			}
			{
				String[] pkNames = { "orderId" };
				String[] fkNames = { "orderId" };
				L.add(new Rel(pkNames, "OrderAdjustment", fkNames));
			}
			mTable2SubTables.put("OrderHeader", L);
		}
		{
			List<Rel> L = FastList.newInstance();
			{
				String[] pkNames = { "orderId", "orderItemSeqId" };
				String[] fkNames = { "orderId", "orderItemSeqId" };
				L.add(new Rel(pkNames, "ShipmentReceipt", fkNames));
			}
			mTable2SubTables.put("OrderItem", L);
		}

		try {

			// Map<String, Object> inMap = FastMap.newInstance();
			// inMap.put("beforeDate", beforeDateStr);
			// inMap.put("maxNumberOrders", maxNumberOrdersStr);
			// Map<String, Object> ret =
			// ctx.getDispatcher().runSync("cutBackupOrdersIgnoreDeliveryShipmentIds",
			// inMap);

			// Delegator delegatorbkp = DelegatorFactory
			// .getDelegator("backuppass");

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss.SSS");
			Date parsedDate = dateFormat.parse(beforeDateStr);
			Timestamp beforeDate = new java.sql.Timestamp(parsedDate.getTime());

			int maxNumberOrder = Integer.valueOf(maxNumberOrdersStr);

			Timestamp lastUpdated = getLastUpdatedStamp(delegator);

			Class.forName("com.mysql.jdbc.Driver");
			Connection cnn = DriverManager
					.getConnection(
							"jdbc:mysql://127.0.0.1/dms?autoReconnect=true&amp;characterEncoding=UTF-8",
							"root", "123456");

			String sql = "select * from order_header";
			if (lastUpdated != null) {
				sql = sql + " where last_updated_stamp < '" + lastUpdated
						+ "' and last_updated_stamp < '" + beforeDate + "'";
			}
			System.out.println(module + "::cutBackupOrders, sql = " + sql);
			Statement st = cnn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			HashSet<String> orderIds = new HashSet<String>();
			List<String> listOrderIds = FastList.newInstance();

			int cnt = 0;
			while (rs.next()) {
				if (cnt >= maxNumberOrder)
					break;

				System.out.println(rs.getString("order_id") + "\t"
						+ rs.getString("last_updated_stamp"));
				orderIds.add(rs.getString("order_id"));
				listOrderIds.add(rs.getString("order_id"));
				cnt++;

			}
			// create delivery
			Map<String, Object> inMap = FastMap.newInstance();
			inMap.put("orderIds", listOrderIds);
			Map<String, Object> ret = null;
			ret = ctx.getDispatcher().runSync("backupOrders", inMap);
			ret = ctx.getDispatcher().runSync("backupDelivery", inMap);
			ret = ctx.getDispatcher().runSync("backupShipment", inMap);

			// if(true) return retSucc;

			ModelEntity tableModel = delegator.getModelEntity("OrderHeader");

			List<String> F = tableModel.getAllFieldNames();
			// String names = "";
			// for(String f: F){
			// Debug.log(module + "::cutBackupOrders field " + f);
			// names += f;
			// }
			// List<GenericValue> lstPOF =
			// delegator.findList("PurchaseOrderFact",
			// null,null,null,null,false);
			// Debug.log(module + "::cutBackupOrders, lstPOF.sz = " +
			// lstPOF.size());

			List<EntityCondition> conds = FastList.newInstance();
			conds.add(EntityCondition.makeCondition("statusId",
					EntityOperator.EQUALS, "ORDER_COMPLETED"));
			conds.add(EntityCondition.makeCondition("orderDate",
					EntityOperator.LESS_THAN_EQUAL_TO, beforeDate));
			conds.add(EntityCondition.makeCondition("orderId",
					EntityOperator.IN, orderIds));

			List<GenericValue> lst = delegator.findList("OrderHeader",
					EntityCondition.makeCondition(conds), null, null, null,
					false);

			// int cnt = 0;
			for (GenericValue o : lst) {

				// GenericValue op = delegatorbkp.makeValue("OrderHeader");
				// for (String f : F) {
				// if (o.get(f) != null) {
				// op.put(f, o.get(f));
				// }
				// }

				Map<String, String> mDeliveryId2ShipmentId = FastMap
						.newInstance();
				Map<String, String> mShipmentId2DeliveryId = FastMap
						.newInstance();
				Set<String> deliveryIds = FastSet.newInstance();
				Set<String> shipmentIds = FastSet.newInstance();

				Object[] orderId = { o.get("orderId") };
				String[] primaryKeyNames = { "orderId" };
				backup(orderId, "OrderHeader", primaryKeyNames, delegator,
						delegatorbkp, mTable2SubTables, mShipmentId2DeliveryId,
						mDeliveryId2ShipmentId, shipmentIds, deliveryIds);

				updateDelivery(deliveryIds, mDeliveryId2ShipmentId,
						delegatorbkp);
				updateShipment(shipmentIds, mShipmentId2DeliveryId,
						delegatorbkp);

				for (String deliveryId : deliveryIds) {
					Debug.log(module + "::cutBackupOrders, delivery "
							+ deliveryId + " shipmentId = "
							+ mDeliveryId2ShipmentId.get(deliveryId));
				}
				for (String shipmentId : shipmentIds) {
					Debug.log(module + "::cutBackupOrders, shipment "
							+ shipmentId + " deliveryId = "
							+ mDeliveryId2ShipmentId.get(shipmentId));
				}

				// cnt++;
				// delegatorbkp.create(op);

				// delegator.removeValue(o);
				Debug.log(module + "::cutBackupOrders, created "
						+ o.get("orderId") + " finished " + cnt + "/"
						+ lst.size());
				// if (cnt >= maxNumberOrder)
				// break;
			}
			retSucc.put("totalOrders", lst.size() + "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return retSucc;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListSalesOrderMapPass(
			DispatchContext ctx, Map<String, Object> context) {
		// Delegator delegator = ctx.getDelegator();
		Delegator delegator = DelegatorFactory.getDelegator("backuppass");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Security security = ctx.getSecurity();
		OlbiusSecurity securityOlb = SecurityUtil.getOlbiusSecurity(security);

		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<GenericValue> listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context
				.get("listAllConditions");
		List<String> listSortFields = (List<String>) context
				.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		try {
			// check permission for each order type
			boolean isRoleDistributor = false;
			boolean hasPermission = securityOlb.olbiusHasPermission(userLogin,
					"VIEW", "ENTITY", "SALESORDER");
			if (!hasPermission) {
				hasPermission = securityOlb.olbiusHasPermission(userLogin,
						null, "MODULE", "DIS_PURCHORDER_VIEW");
				isRoleDistributor = true;
			}
			if (!hasPermission) {
				Debug.logWarning("**** Security [" + (new Date()).toString()
						+ "]: " + userLogin.get("userLoginId")
						+ " attempt to run manual payment transaction!", module);
				// return
				// ServiceUtil.returnError(UtilProperties.getMessage(resource,
				// "BSTransactionNotAuthorized", locale));
				return successResult;
			}
			String userLoginPartyId = userLogin.getString("partyId");

			// only get sales orders
			listAllConditions.add(EntityCondition.makeCondition("orderTypeId",
					EntityOperator.EQUALS, "SALES_ORDER"));

			// check status of order
			String statusId = SalesUtil.getParameter(parameters, "_statusId");
			if (UtilValidate.isNotEmpty(statusId)) {
				listAllConditions.add(EntityCondition.makeCondition("statusId",
						EntityOperator.EQUALS, statusId));
			}
			// listAllConditions.add(EntityCondition.makeCondition("statusId",
			// EntityOperator.NOT_EQUAL, null));

			// check sales method channel enumeration of order
			String channelCode = SalesUtil.getParameter(parameters, "cn");
			if (UtilValidate.isNotEmpty(channelCode)) {
				String salesMethodChannelEnumId = null;
				if ("ts".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil
							.getPropertyValue(delegator,
									"sales.method.channel.enum.id.telesales");
				} else if ("ps".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil.getPropertyValue(
							delegator, "sales.method.channel.enum.id.pos");
				} else if ("ec".equals(channelCode)) {
					salesMethodChannelEnumId = SalesUtil
							.getPropertyValue(delegator,
									"sales.method.channel.enum.id.ecommerce");
				}
				if (salesMethodChannelEnumId != null) {
					listAllConditions.add(EntityCondition.makeCondition(
							"salesMethodChannelEnumId",
							salesMethodChannelEnumId));
				}
			}

			// check customer of order
			String partyId = SalesUtil.getParameter(parameters, "partyId");
			if (!isRoleDistributor) {
				if (SalesUtil.propertyValueEqualsIgnoreCase(
						"get.order.by.created.by", "true")) {
					// get order by employee created
					if (!SalesPartyUtil.isCallCenterManager(delegator,
							userLoginPartyId)
							&& SalesPartyUtil.isCallCenter(delegator,
									userLoginPartyId)) {
						// user login is a CallCenter employee
						String ia = SalesUtil.getParameter(parameters, "ia"); // for
																				// case
																				// view
																				// list
																				// order
																				// from
																				// call
																				// in/out
																				// screens
						if (!"Y".equals(ia)) {
							listAllConditions.add(EntityCondition
									.makeCondition("createdBy",
											EntityOperator.EQUALS,
											userLoginPartyId));
						}
					}
				} else {
					if (!SalesPartyUtil.isSalesManager(delegator,
							userLoginPartyId)
							&& !SalesPartyUtil.isCallCenterManager(delegator,
									userLoginPartyId)
							&& !SalesPartyUtil.isCallCenter(delegator,
									userLoginPartyId)) {

						if (SalesPartyUtil.isSalesAdmin(delegator,
								userLoginPartyId)
								|| SalesPartyUtil.isSalesAdminManager(
										delegator, userLoginPartyId)) {
							List<String> productStoreIds = EntityUtil
									.getFieldListFromEntityList(
											ProductStoreWorker
													.getListProductStoreView(
															delegator,
															userLogin,
															userLoginPartyId,
															false),
											"productStoreId", true);
							listAllConditions
									.add(EntityCondition.makeCondition(
											"productStoreId",
											EntityOperator.IN, productStoreIds));
						} else if (SalesPartyUtil.isSalesEmployee(delegator,
								userLoginPartyId)) {
							List<String> distributorIds = PartyWorker
									.getDistributorInOrgByManager(delegator,
											userLoginPartyId);
							listAllConditions.add(EntityCondition
									.makeCondition("customerId",
											EntityOperator.IN, distributorIds));
							if (!distributorIds.contains(partyId))
								partyId = null;
						}
					}
				}
			} else {
				// customer is user login
				partyId = userLoginPartyId;
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition(
						"customerId", EntityOperator.EQUALS, partyId));
			}

			// check seller of order
			String organizationId = SalesUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			boolean searchable = false;
			if (organizationId != null || UtilValidate.isNotEmpty(partyId)) {
				if (organizationId != null)
					listAllConditions.add(EntityCondition.makeCondition(
							"sellerId", organizationId));
				searchable = true;
			}

			// process
			if (searchable) {
				if (UtilValidate.isEmpty(listSortFields)) {
					listSortFields.add("-orderDate");
					// listSortFields.add("priority");
				}
				Set<String> listSelectFields = FastSet.newInstance();
				listSelectFields.add("orderDate");
				listSelectFields.add("orderId");
				listSelectFields.add("orderName");
				listSelectFields.add("estimatedDeliveryDate");
				listSelectFields.add("shipBeforeDate");
				listSelectFields.add("shipAfterDate");
				listSelectFields.add("customerId");
				listSelectFields.add("customerCode");
				listSelectFields.add("customerFullName");
				listSelectFields.add("fullContactNumber");
				listSelectFields.add("productStoreId");
				listSelectFields.add("grandTotal");
				listSelectFields.add("statusId");
				listSelectFields.add("currencyUom");
				listSelectFields.add("agreementId");
				listSelectFields.add("agreementCode");
				listSelectFields.add("priority");
				listSelectFields.add("isFavorDelivery");
				listSelectFields.add("createdBy");

				listAllConditions = ProcessConditionUtil
						.processOrderCondition(listAllConditions);
				listSortFields = ProcessConditionUtil
						.processOrderSort(listSortFields);

				// String tableName = "OrderHeaderFullViewPass";
				String tableName = "OrderHeaderFullView";
				String productPromoId = SalesUtil.getParameter(parameters,
						"productPromoId");
				if (productPromoId != null) {
					listAllConditions.add(EntityCondition.makeCondition(
							"productPromoId", productPromoId));
					// tableName = "PromoOrderHeaderFullViewPass";
					tableName = "PromoOrderHeaderFullView";
				}

				/*
				 * EntityCondition mainCond =
				 * EntityCondition.makeCondition(listAllConditions,
				 * EntityOperator.AND); Long totalRows =
				 * delegator.findCountByCondition(tableName, mainCond, null,
				 * null); successResult.put("TotalRows", totalRows.toString());
				 * 
				 * String viewIndexStr = (String) parameters.get("pagenum")[0];
				 * String viewSizeStr = (String) parameters.get("pagesize")[0];
				 * int viewIndex = viewIndexStr == null ? 0 : new
				 * Integer(viewIndexStr); int viewSize = viewSizeStr == null ? 0
				 * : new Integer(viewSizeStr); int viewOffset = viewIndex *
				 * viewSize;
				 * 
				 * opts.setLimit(viewSize); opts.setOffset(viewOffset);
				 * listIterator = delegator.findList(tableName, mainCond,
				 * listSelectFields, listSortFields, opts, false);
				 */
				listIterator = EntityMiscUtil.processIteratorToList(parameters,
						successResult, delegator, tableName, EntityCondition
								.makeCondition(listAllConditions,
										EntityOperator.AND), null,
						listSelectFields, listSortFields, opts);

			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListSalesOrder service: "
					+ e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}

}
