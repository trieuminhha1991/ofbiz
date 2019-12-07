import java.util.Locale;

listEnum = delegator.findByAnd("Enumeration", [enumTypeId : "CONVERSION_PURPOSE"],["sequenceId"],false);
Locale locale = (Locale) context.get("locale");
for(int i = 0; i < listEnum.size();i++){
	listEnum.get(i).set("description", listEnum.get(i).get("description", locale));
}
context.listEnum = listEnum;

listUom = delegator.findByAnd("Uom", [uomTypeId : "CURRENCY_MEASURE"],["description"],false);
for(int i = 0; i < listEnum.size();i++){
	listUom.get(i).set("description", listUom.get(i).get("description", locale));
}
context.listUom = listUom;