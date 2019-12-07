<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<#include 'script/productSupplierViewTotalScript.ftl'/>

${screens.render("component://basepo/widget/SupplierProductScreens.xml#ProductSupplierViewGrid")}

<script type="text/javascript" src="/poresources/js/productSupplier/productSupplierViewForGrid.js?v=0.0.5"></script>

${screens.render("component://basepo/widget/SupplierProductScreens.xml#ProductSupplierNewProductSupplierPopup")}

<#include "productSupplierNewQuickAddPopup.ftl">