import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil

import java.util.Calendar;
import javolution.util.FastMap;
import javolution.util.FastSet;

String agreementId = parameters.agreementId;
String regrandTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSPRRagrandTitleData", locale);
String regrandTitleP2 = null; //UtilProperties.getMessage("BaseSalesUiLabels", "BSPRRagrandTitleDataP2", locale);
if (agreementId) {
	GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
	if (agreement) {
		context.agreement = agreement;
		
		// get agreement role
		List<GenericValue> agreementRoles = delegator.findByAnd("AgreementRole", UtilMisc.toMap("agreementId", agreementId), null, false);
		if (agreementRoles != null) {
			List<String> listSupervisorId = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByAnd(agreementRoles, UtilMisc.toMap("roleTypeId", "SALESSUP_EMPL")), "partyId", true);
			if (listSupervisorId != null) {
				findCond = EntityCondition.makeCondition("partyId", EntityOperator.IN, listSupervisorId);
				findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				GenericValue supervisor = EntityUtil.getFirst(delegator.findList("PartyFullNameDetailSimple", findCond, null, null, findOptions, false));
				if (supervisor) {
					context.supervisorNameStr = supervisor.fullName;
				}
				GenericValue roleSupervisor = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", "SALESSUP_EMPL"), false);
				if (roleSupervisor != null) {
					String descriptionRole = roleSupervisor.get("description", locale);
					context.descriptionRole = descriptionRole;
				}
			}
		}
		// get list agreement items
		List<GenericValue> agreementItems = delegator.findByAnd("AgreementItem", UtilMisc.toMap("agreementId", agreementId, "agreementItemTypeId", "AGREEMENT_EXHIBIT"), null, false);
		if (agreementItems != null) {
			List<String> agreementItemIds = EntityUtil.getFieldListFromEntityList(agreementItems, "agreementItemSeqId", true);
			// get list agreement promotion apply
			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
			listCond.add(EntityCondition.makeCondition("agreementId", agreementId));
			listCond.add(EntityCondition.makeCondition("agreementItemSeqId", EntityOperator.IN, agreementItemIds));
			findCond = EntityCondition.makeCondition(listCond, EntityOperator.AND);
			findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			List<String> listPromoIds = EntityUtil.getFieldListFromEntityList(EntityUtil.filterByDate(delegator.findList("AgreementPromoExtAppl", findCond, null, null, findOptions, false)), "productPromoId", true);
			if (listPromoIds != null) {
				findCond = EntityCondition.makeCondition("productPromoId", EntityOperator.IN, listPromoIds);
				findOptions = new EntityFindOptions();
				findOptions.setDistinct(true);
				List<GenericValue> listProductPromo = delegator.findList("ProductPromoExt", findCond, null, null, findOptions, false);
				String fromDateStr = "";
				String thruDateStr = "";
				if (listProductPromo != null && listProductPromo.size() > 0) {
					//regrandTitle = listProductPromo.get(0).get("promoText");
					GenericValue productPromo = listProductPromo.get(0);
					context.productPromo = productPromo;
					if (productPromo.fromDate != null) {
						int productPromoDate = productPromo.get("fromDate").getDate();
						int productPromoMonth = productPromo.get("fromDate").getMonth() + 1;
						int productPromoYear = productPromo.get("fromDate").getYear() + 1900;
						String productPromoDateStr = productPromoDate < 10 ? "0" + productPromoDate : productPromoDate;
						String productPromoMonthStr = productPromoMonth < 10 ? "0" + productPromoMonth : productPromoMonth;
						String productPromoYearStr = productPromoYear < 10 ? "0" + productPromoYear : productPromoYear;
						context.productPromoDate = productPromoDateStr;
						context.productPromoMonth = productPromoMonthStr;
						context.productPromoYear = productPromoYearStr;
						fromDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSDay", locale);
						if (productPromoDateStr != null) {fromDateStr += " " + productPromoDateStr + " "}
						else {fromDateStr += " .... "}
						fromDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSMonth", locale);
						if (productPromoMonthStr != null) {fromDateStr += " " + productPromoMonthStr + " "}
						else {fromDateStr += " .... "}
						fromDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSYear", locale);
						if (productPromoYearStr != null) {fromDateStr += " " + productPromoYearStr + " "}
						else {fromDateStr += " .... "}
					}
					if (productPromo.thruDate != null) {
						int productPromoThruDate = productPromo.get("thruDate").getDate();
						int productPromoThruMonth = productPromo.get("thruDate").getMonth() + 1;
						int productPromoThruYear = productPromo.get("thruDate").getYear() + 1900;
						String productPromoThruDateStr = productPromoThruDate < 10 ? "0" + productPromoThruDate : productPromoThruDate;
						String productPromoThruMonthStr = productPromoThruMonth < 10 ? "0" + productPromoThruMonth : productPromoThruMonth;
						String productPromoThruYearStr = productPromoThruYear < 10 ? "0" + productPromoThruYear : productPromoThruYear;
						context.productPromoThruDate = productPromoThruDateStr;
						context.productPromoThruMonth = productPromoThruMonthStr;
						context.productPromoThruYear = productPromoThruYearStr;
						thruDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSDay", locale);
						if (productPromoThruDateStr != null) {thruDateStr += " " + productPromoThruDateStr + " "}
						else {thruDateStr += " .... "}
						thruDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSMonth", locale);
						if (productPromoThruMonthStr != null) {thruDateStr += " " + productPromoThruMonthStr + " "}
						else {thruDateStr += " .... "}
						thruDateStr += UtilProperties.getMessage("BaseSalesUiLabels", "BSYear", locale);
						if (productPromoThruYearStr != null) {thruDateStr += " " + productPromoThruYearStr + " "}
						else {thruDateStr += " .... "}
					}
					if (productPromo != null) {
						List<GenericValue> productPromoRegisters = delegator.findByAnd("ProductPromoExtRegister", UtilMisc.toMap("agreementId", agreement.get("agreementId")), null, false);
						GenericValue productpromoRegister = EntityUtil.getFirst(productPromoRegisters);
						
						if (productpromoRegister != null) {
							// get rules of product promotion
							List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoExtRule", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId"), "productPromoRuleId", productpromoRegister.get("productPromoRuleId")), null, false);
							GenericValue productPromoRule = EntityUtil.getFirst(productPromoRules);
							if (productPromoRule != null) {
								List<GenericValue> productPromoConds = delegator.findByAnd("ProductPromoExtCond", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId"), "productPromoRuleId", productPromoRule.get("productPromoRuleId")), null, false);
								List<GenericValue> productPromoActions = delegator.findByAnd("ProductPromoExtAction", UtilMisc.toMap("productPromoId", productPromo.get("productPromoId"), "productPromoRuleId", productPromoRule.get("productPromoRuleId")), null, false);
								
								String condExhibited = "";
								int disparitySize = 0;
								int disparityCond = 0;
								int dispartityAction = 0;
								int rowCount = 0;
								int rowCountCond = 0;
								int rowCountAction = 0;
								if (productPromoConds != null && productPromoConds.size() > 0 && 
									productPromoActions != null && productPromoActions.size() > 0) {
									disparitySize = productPromoConds.size() - productPromoActions.size();
									if (disparitySize > 0) {
										disparityCond = disparitySize;
										rowCount = productPromoConds.size();
										rowCountCond = rowCount - disparityCond;
									} else if (disparitySize < 0) {
										dispartityAction = - disparitySize;
										rowCount = productPromoActions.size();
										rowCountAction = rowCount - dispartityAction;
									} else {
										rowCount = productPromoConds.size();
									}
								}
								
								List<String> listCond2 = new ArrayList<String>();
								if (productPromoConds != null && productPromoConds.size() > 0) {
									for (GenericValue productPromoCond : productPromoConds) {
										if (productPromoCond.condExhibited != null) {
											condExhibited = productPromoCond.condExhibited;
										}
										String condStr = "";
										condStr += productPromoCond.get("condValue");
										Set<String> productUomIds = FastSet.newInstance();
										List<GenericValue> listCondProduct = new ArrayList<GenericValue>();
										List<GenericValue> listCondProductItem = delegator.findByAnd("ProductPromoExtProduct", UtilMisc.toMap("productPromoId", productPromoCond.productPromoId, "productPromoRuleId", productPromoCond.productPromoRuleId, "productPromoCondSeqId", productPromoCond.productPromoCondSeqId), null, false);
										for (GenericValue condProductItem : listCondProductItem) {
											GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", condProductItem.productId), false);
											if (product != null && product.quantityUomId != null) {
												productUomIds.add(product.quantityUomId);
												listCondProduct.add(product);
											}
										}
										int index1 = 0;
										for (String productUomId : productUomIds) {
											GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", productUomId), false);
											if (quantityUom != null) {
												if (index1 == 0) {
													condStr += " " + quantityUom.get("description", locale);
												} else {
													condStr += ", " + quantityUom.get("description", locale);
												}
												index1++;
											}
										}
										
										if (listCondProduct != null) {
											if (listCondProduct.size() > 1) {
												condStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProducts", locale);
											} else if (listCondProduct.size() == 1) {
												condStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
											}
										}
										for (int i = 0; i < listCondProduct.size(); i++) {
											GenericValue productItem = listCondProduct.get(i);
											if (i == 0) {
												condStr += " " + productItem.internalName;
											} else {
												condStr += ", " + productItem.internalName;
											}
										}
										List<GenericValue> listCondCategory = new ArrayList<GenericValue>();
										List<GenericValue> listCondCategoryItem = delegator.findByAnd("ProductPromoExtCategory", UtilMisc.toMap("productPromoId", productPromoCond.productPromoId, "productPromoRuleId", productPromoCond.productPromoRuleId, "productPromoCondSeqId", productPromoCond.productPromoCondSeqId), null, false);
										if (listCondCategoryItem != null) {
											if (listCondCategoryItem.size() > 1) {
												condStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProductsOfCategory", locale);
											} else if (listCondCategoryItem.size() == 1) {
												condStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProductOfCategory", locale);
											}
										}
										for (GenericValue condCategorytItem : listCondCategoryItem) {
											GenericValue productCategory = delegator.findOne("ProductCategory", UtilMisc.toMap("productCategoryId", condCategorytItem.productCategoryId), false);
											if (productCategory != null && productCategory.categoryName != null) {
												condStr += " " + productCategory.categoryName;
												listCondCategory.add(productCategory);
											}
										}
										listCond2.add(condStr);
									}
								}
								
								List<String> listAction = new ArrayList<String>();
								if (productPromoActions != null && productPromoActions.size() > 0) {
									for (GenericValue productPromoAction : productPromoActions) {
										String actionStr = "";
										if (productPromoAction.get("quantity") != null) actionStr += UtilFormatOut.formatDecimalNumber(productPromoAction.get("quantity"), "#,##0.###", locale);
										Set<String> productUomIds = FastSet.newInstance();
										List<GenericValue> listActionProduct = new ArrayList<GenericValue>();
										List<GenericValue> listActionProductItem = delegator.findByAnd("ProductPromoExtProduct", UtilMisc.toMap("productPromoId", productPromoAction.productPromoId, "productPromoRuleId", productPromoAction.productPromoRuleId, "productPromoActionSeqId", productPromoAction.productPromoActionSeqId), null, false);
										for (GenericValue condProductItem : listActionProductItem) {
											GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", condProductItem.productId), false);
											if (product != null && product.quantityUomId != null) {
												productUomIds.add(product.quantityUomId);
												listActionProduct.add(product);
											}
										}
										int index1 = 0;
										for (String productUomId : productUomIds) {
											GenericValue quantityUom = delegator.findOne("Uom", UtilMisc.toMap("uomId", productUomId), false);
											if (quantityUom != null) {
												if (index1 == 0) {
													actionStr += " " + quantityUom.get("description", locale);
												} else {
													actionStr += ", " + quantityUom.get("description", locale);
												}
												index1++;
											}
										}
										
										if (listActionProduct != null) {
											if (listActionProduct.size() > 1) {
												actionStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProducts", locale);
											} else if (listActionProduct.size() == 1) {
												actionStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSProduct", locale);
											}
										}
										for (int i = 0; i < listActionProduct.size(); i++) {
											GenericValue productItem = listActionProduct.get(i);
											if (i == 0) {
												actionStr += " " + productItem.internalName;
											} else {
												actionStr += ", " + productItem.internalName;
											}
										}
										
										List<GenericValue> listActionCategory = new ArrayList<GenericValue>();
										List<GenericValue> listActionCategoryItem = delegator.findByAnd("ProductPromoExtCategory", UtilMisc.toMap("productPromoId", productPromoAction.productPromoId, "productPromoRuleId", productPromoAction.productPromoRuleId, "productPromoActionSeqId", productPromoAction.productPromoActionSeqId), null, false);
										if (listActionCategoryItem != null) {
											if (listActionCategoryItem.size() > 1) {
												actionStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProductsOfCategory", locale);
											} else if (listActionCategoryItem.size() == 1) {
												actionStr += " " + UtilProperties.getMessage("BaseSalesUiLabels", "BSPRProductOfCategory", locale);
											}
										}
										for (GenericValue condCategorytItem : listActionCategoryItem) {
											GenericValue productCategory = delegator.findOne("Category", UtilMisc.toMap("productCategoryId", condCategorytItem.productCategoryId), false);
											if (productCategory != null && productCategory.categoryName != null) {
												actionStr += " " + productCategory.categoryName;
												listActionCategory.add(productCategory);
											}
										}
										listAction.add(actionStr);
									}
								}
								
								List<Map<String, Object>> listProductPromoData = new ArrayList<Map<String, Object>>();
								for (int i = 0; i < rowCount; i++) {
									Map<String, Object> productPromoData = FastMap.newInstance();
									if (listCond2.size() > i) {
										productPromoData.condition = listCond2.get(i);
									}
									if (listAction.size() > i) {
										productPromoData.action = listAction.get(i);
									}
									listProductPromoData.add(productPromoData);
								}
								context.disparityCond = disparityCond;
								context.dispartityAction = dispartityAction;
								context.condExhibited = condExhibited;
								context.rowCount = rowCount;
								context.listProductPromoData = listProductPromoData;
								context.rowCountAction = rowCountAction;
								context.rowCountCond = rowCountCond;
								context.listAction = listAction;
								
								// get terms
								List<GenericValue> generalExhTermsGV = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", "GENERAL_EXHIBITION_TERM", "agreementId", agreementId));
								List<String> generalExhTerms = new ArrayList<String>();
								for (GenericValue agreementTerm : generalExhTermsGV) {
									if (agreementTerm.get("textValue") != null) {
										String textValue = agreementTerm.get("textValue");
										if (regrandTitleP2 != null) {
											textValue = textValue.replaceAll("#\\[productListName\\]", regrandTitleP2);
										} else {
											textValue = textValue.replaceAll("#\\[productListName\\]", "..................................................");
										}
										if (condExhibited != null) {
											textValue = textValue.replaceAll("#\\[numberCooler\\]", condExhibited);
										} else {
											textValue = textValue.replaceAll("#\\[numberCooler\\]", "............................");
										}
										if (fromDateStr != "") {
											textValue = textValue.replaceAll("#\\[fromDate\\]", fromDateStr);
										} else {
											textValue = textValue.replaceAll("#\\[fromDate\\]", "............................");
										}
										if (thruDateStr != "") {
											textValue = textValue.replaceAll("#\\[thruDate\\]", thruDateStr);
										} else {
											textValue = textValue.replaceAll("#\\[thruDate\\]", "............................");
										}
										generalExhTerms.add(textValue);
									}
								}
								
								List<GenericValue> rightsRreTermsGV = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", "RIGHTS_EXH_TERM", "agreementId", agreementId));
								List<String> rightsRreTerms = EntityUtil.getFieldListFromEntityList(rightsRreTermsGV, "textValue", false);
								List<GenericValue> partyAResppreTermsGV = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", "PARTY_A_RESPONSIBILITY_EXH_TERM", "agreementId", agreementId));
								List<String> partyAResppreTerms = new ArrayList<String>();
								for (GenericValue agreementTerm : partyAResppreTermsGV) {
									if (agreementTerm.get("textValue") != null) {
										String textValue = agreementTerm.get("textValue");
										if (regrandTitleP2 != null) {
											textValue = textValue.replaceAll("#\\[productListName\\]", regrandTitleP2);
										} else {
											textValue = textValue.replaceAll("#\\[productListName\\]", "..................................................");
										}
										partyAResppreTerms.add(textValue);
									}
								}
								List<GenericValue> partyBResppreTermsGV = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", "PARTY_B_RESPONSIBILITY_EXH_TERM", "agreementId", agreementId), null, false);
								List<String> partyBResppreTerms = new ArrayList<String>();
								for (GenericValue agreementTerm : partyBResppreTermsGV) {
									if (agreementTerm.get("textValue") != null) {
										String textValue = agreementTerm.get("textValue");
										textValue = textValue.replaceAll("#\\[conditionSum\\]", ".........................");
										if (condExhibited != null) {
											textValue = textValue.replaceAll("#\\[numberCooler\\]", condExhibited);
										} else {
											textValue = textValue.replaceAll("#\\[numberCooler\\]", "............................");
										}
										String actionApply = "";
										if (listAction != null) {
											for (int i=0; i < listAction.size(); i++) {
												if (i != 0) {
													actionApply += ", ";
												}
												actionApply += listAction.get(i);
											}
										}
										if (actionApply != "") {
											if (condExhibited != null) {
												textValue = textValue.replaceAll("#\\[actionApply\\]", actionApply);
											} else {
												textValue = textValue.replaceAll("#\\[actionApply\\]", "............................");
											}
										}
										partyBResppreTerms.add(textValue);
									}
								}
								List<GenericValue> partiesResppreTermsGV = delegator.findByAnd("AgreementTerm", UtilMisc.toMap("termTypeId", "PARTIES_RESPONSIBILITY_EXH_TERM", "agreementId", agreementId), null, false);
								List<String> partiesResppreTerms = EntityUtil.getFieldListFromEntityList(partiesResppreTermsGV, "textValue", false);
								
								context.generalExhTerms = generalExhTerms;
								context.rightsRreTerms = rightsRreTerms;
								context.partyAResppreTerms = partyAResppreTerms;
								context.partyBResppreTerms = partyBResppreTerms;
								context.partiesResppreTerms = partiesResppreTerms;
							}
						}
						
					}
				}
			}
		}
		
		// get info of Ben A
		String partyIdA = agreement.get("partyIdTo");
		if (partyIdA) {
			GenericValue partyA = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyIdA), false);
			context.partyA = partyA;
			
			if (partyA) {
				GenericValue postalAddressGVId = EntityUtil.getFirst(delegator.findByAnd("PartyPostalAddressMaxActiving", UtilMisc.toMap("partyId", partyA.partyId), null, false));
				if (postalAddressGVId) {
					GenericValue postalAddress = EntityUtil.getFirst(delegator.findByAnd("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", postalAddressGVId.contactMechId), null, false));
					String positionAddress = postalAddress.stateProvinceGeoName;
					context.partyAPostalAddress = postalAddress;
				}
			}
		}
		
		// get info of Ben B
		String partyIdB = agreement.get("partyIdFrom");
		if (partyIdB) {
			GenericValue partyB = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", partyIdB), false);
			context.partyB = partyB;
			
			if (partyB) {
				GenericValue postalAddressGVId = EntityUtil.getFirst(delegator.findByAnd("PartyPostalAddressMaxActiving", UtilMisc.toMap("partyId", partyB.partyId), null, false));
				if (postalAddressGVId) {
					GenericValue postalAddress = EntityUtil.getFirst(delegator.findByAnd("PostalAddressFullNameDetail", UtilMisc.toMap("contactMechId", postalAddressGVId.contactMechId), null, false));
					String positionAddress = postalAddress.stateProvinceGeoName;
					context.positionAddress = positionAddress;
					context.partyBPostalAddress = postalAddress;
				}
			}
		}
		
	}
}
context.regrandTitle = regrandTitle;
context.regrandTitleP2 = regrandTitleP2;
