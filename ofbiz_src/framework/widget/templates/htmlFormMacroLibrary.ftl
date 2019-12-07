<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<script>
    function escapeHTML(a) {
        return a.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
    }
</script>
<#--
    jq renderer
-->
<#macro jqDataMinimumLib>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
</#macro>
<#macro jqTable url entityName columnlist dataField pageable="true" viewSize="20" columnsResize="true" width="1000" dataType="json" 
        sortable="true" filterable="true">
    <@jqDataMinimumLib/>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
    <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            // prepare the data
            var source =
            {
                dataType: '${dataType}',
                dataFields: [
                    ${dataField}
                ],
                id: 'id',
                type: 'POST',
                data: {
                    noConditionFind: "Y"
                },
                sortcolumn: 'glAccountId',
                entityName: '${entityName}',
                sortdirection: 'asc',
                contentType: 'application/x-www-form-urlencoded',
                url: '${url}'
            };
            var filterChanged = false;
            var dataAdapter = new $.jqx.dataAdapter(source,
                {
                    formatData: function (data) {
                        <#if sortable == "true">
                            if (source.totalRecords) {
                                // update the $skip and $top params of the OData service.
                                // data.pagenum - page number starting from 0.
                                // data.pagesize - page size
                                // data.sortdatafield - the column's datafield value(ShipCountry, ShipCity, etc.).
                                // data.sortorder - the sort order(asc or desc).
                                if (data.sortdatafield && data.sortorder) {
                                    data.$orderby = data.sortdatafield + " " + data.sortorder;
                                }
                            }
                        </#if>
                        <#if pageable == "true">
                            // update the $skip and $top params of the OData service.
                            // data.pagenum - page number starting from 0.
                            // data.pagesize - page size
                             data.$skip = data.pagenum * data.pagesize;
                             data.$top = data.pagesize;
                             data.$inlinecount = "allpages";
                        </#if>
                        <#if filterable == "true">
                            if (data.filterslength) {
                                filterChanged = true;
                                var filterParam = "";
                                for (var i = 0; i < data.filterslength; i++) {
                                    // filter's value.
                                    var filterValue = data["filtervalue" + i];
                                    // filter's condition. For the filterMode="simple" it is "CONTAINS".
                                    var filterCondition = data["filtercondition" + i];
                                    // filter's data field - the filter column's datafield value.
                                    var filterDataField = data["filterdatafield" + i];
                                    // "and" or "or" depending on the filter expressions. When the filterMode="simple", the value is "or".
                                    var filterOperator = data[filterDataField + "operator"];
                                    var startIndex = 0;
                                    if (filterValue.indexOf('-') == -1) {
                                        if (filterCondition == "CONTAINS") {
                                            filterParam += "substringof('" + filterValue + "', " + filterDataField + ") eq true";
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                    else {
                                        if (filterDataField == "ShippedDate") {
                                            var dateGroups = new Array();
                                            var startIndex = 0;
                                            var item = filterValue.substring(startIndex).indexOf('-');
                                            while (item > -1) {
                                                dateGroups.push(filterValue.substring(startIndex, item + startIndex));
                                                startIndex += item + 1;
                                                item = filterValue.substring(startIndex).indexOf('-');
                                                if (item == -1) {
                                                    dateGroups.push(filterValue.substring(startIndex));
                                                }
                                            }
                                            if (dateGroups.length == 3) {
                                                filterParam += "year(ShippedDate) eq " + parseInt(dateGroups[0]) + " and month(ShippedDate) eq " + parseInt(dateGroups[1]) + " and day(ShippedDate) eq " + parseInt(dateGroups[2]);
                                            }
                                            filterParam += " " + filterOperator + " ";
                                        }
                                    }
                                }
                                // remove last filter operator.
                                filterParam = filterParam.substring(0, filterParam.length - filterOperator.length - 2);
                                data.$filter = filterParam;
                                source.totalRecords = 0;
                            }
                            else {
                                if (filterChanged) {
                                    source.totalRecords = 0;
                                    filterChanged = false;
                                }
                            }
                        </#if>
                        data.entityName = '${entityName}';
                        return data;
                    },
                    downloadComplete: function (data, status, xhr) {
                        if (!source.totalRecords) {
                            source.totalRecords = parseInt(data["odata.count"]);
                        }
                    }
                }
            );
            $("#dataTable").jqxDataTable(
            {
                width: ${width},
                pageable: ${pageable},
                pagerButtonsCount: ${viewSize},
                serverProcessing: true,
                source: dataAdapter,
                filterable: ${filterable},
                filterMode: 'simple',
                sortable: ${sortable},
                columnsReorder: true,
                columnsResize: ${columnsResize},
                columns: [
                  ${columnlist}
              ]
            });
        });
    </script>
    <div id="dataTable"></div>
</#macro>

<#include "component://securityolbius/webapp/ftl/marco/olbiusPermission.ftl">

<#include "JqwLibrary.ftl"/>
<#include "jqxMacro.ftl"/>
<#global jqTable=jqTable />
<#global loading=loading />
<#global jqGrid=jqGrid />
<#global jqxCombobox=jqxCombobox />
<#global useLocalizationNumberFunction=useLocalizationNumberFunction />
<#global jqGridMinimumLib=jqGridMinimumLib />

<#-- global renderFilterType=renderFilterType -->
<#-- global renderDateTimeFilterType=renderDateTimeFilterType -->
<#--
    End of jq renderer
-->

<#macro renderField text>
  <#if text?exists>
    ${text}<#lt/>
  </#if>
</#macro>

<#macro renderDisplayField type imageLocation idName description title class alert inPlaceEditorUrl="" inPlaceEditorParams="">
  <#if type?has_content && type=="image">
    <img src="${imageLocation}" alt=""><#lt/>
  <#else>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true" || title?has_content>
      <span <#if idName?has_content>id="cc_${idName}"</#if> <#if title?has_content>title="${title}"</#if> <@renderClass class alert />><#t/>
    </#if>

    <#if description?has_content>
      ${description?replace("\n", "<br />")}<#t/>
    <#else>
      &nbsp;<#t/>
    </#if>
    <#if inPlaceEditorUrl?has_content || class?has_content || alert=="true">
      </span><#lt/>
    </#if>
    <#if inPlaceEditorUrl?has_content && idName?has_content>
      <script language="JavaScript" type="text/javascript"><#lt/>
        ajaxInPlaceEditDisplayField('cc_${idName}', '${inPlaceEditorUrl}', ${inPlaceEditorParams});<#lt/>
      </script><#lt/>
    </#if>
  </#if>
</#macro>
<#macro renderHyperlinkField></#macro>

<#macro renderTextField name className alert value textSize maxlength id event action disabled clientAutocomplete ajaxUrl ajaxEnabled mask placeholder="">
  <#if mask?has_content>
    <script type="text/javascript">
      jQuery(function($){jQuery("#${id}").mask("${mask}");});
    </script>
  </#if>
  <input type="text" name="${name?default("")?html}"<#t/>
    <@renderClass className alert />
    <#if value?has_content> value="${value}"</#if><#rt/>
    <#if textSize?has_content> size="${textSize}"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>
    <#if disabled?has_content && disabled> disabled="disabled"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
    <#if clientAutocomplete?has_content && clientAutocomplete=="false"> autocomplete="off"</#if><#rt/>
    <#if placeholder?has_content> placeholder="${placeholder}"</#if><#rt/>
  /><#t/>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
    <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', false, ${defaultMinLength!2}, ${defaultDelay!300});</script><#lt/>
  </#if>
</#macro>

<#macro renderTextareaField name className alert cols rows id readonly value visualEditorEnable buttons language="">
  <textarea name="${name}"<#t/><@renderClass className alert />
    <#if cols?has_content> cols="${cols}"</#if><#rt/>
    <#if rows?has_content> rows="${rows}"</#if><#rt/>
    <#if id?has_content> id="${id}"</#if><#rt/>
    <#if readonly?has_content && readonly=='readonly'> readonly="readonly"</#if><#rt/>
    <#if maxlength?has_content> maxlength="${maxlength}"</#if><#rt/>><#t/>
    <#if value?has_content>${value}</#if><#t/>
  </textarea><#lt/>
  <#if visualEditorEnable?has_content>
    <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/elrte.min.js" type="text/javascript"></script><#rt/>
    <#if language?has_content && language != "en">
      <script language="javascript" src="/images/jquery/plugins/elrte-1.3/js/i18n/elrte.${language!"en"}.js" type="text/javascript"></script><#rt/>
    </#if>
    <link href="/images/jquery/plugins/elrte-1.3/css/elrte.min.css" rel="stylesheet" type="text/css">
    <script language="javascript" type="text/javascript">
      var opts = {
         cssClass : 'el-rte',
         lang     : '${language!"en"}',
         toolbar  : '${buttons?default("maxi")}',
         doctype  : '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">', //'<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN">',
         cssfiles : ['/images/jquery/plugins/elrte-1.3/css/elrte-inner.css']
      }
      jQuery('#${id?default("")}').elrte(opts);
    </script>
  </#if>
</#macro>

<#macro renderDateTimeField name className alert title value size maxlength id dateType shortDateInput timeDropdownParamName defaultDateTimeString localizedIconTitle timeDropdown timeHourName classString hour1 hour2 timeMinutesName minutes isTwelveHour ampmName amSelected pmSelected compositeType formName mask="" event="" action="" step="" timeValues="">
  <span class="view-calendar">
    <#if dateType!="time" >
      <input type="text" name="${name}_i18n" <@renderClass className alert /><#rt/>
        <#if title?has_content> title="${title}"</#if>
        <#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#rt/>
        <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
        <#if id?has_content> id="${id}_i18n"</#if>/><#rt/>
    </#if>
    <#-- the style attribute is a little bit messy but when using disply:none the timepicker is shown on a wrong place -->
    <input type="text" name="${name}" style="height:1px;width:1px;border:none;background-color:transparent;display:none;" <#if event?has_content && action?has_content> ${event}="${action}"</#if> <@renderClass className alert /><#rt/>
      <#if title?has_content> title="${title}"</#if>
      <#if value?has_content> value="${value}"</#if>
      <#if size?has_content> size="${size}"</#if><#rt/>
      <#if maxlength?has_content>  maxlength="${maxlength}"</#if>
      <#if id?has_content> id="${id}"</#if>/><#rt/>
    <#if dateType!="time" >
      <script type="text/javascript">
        <#-- If language specific lib is found, use date / time converter else just copy the value fields -->
        if (Date.CultureInfo != undefined) {
          var initDate = <#if value?has_content>jQuery("#${id}_i18n").val()<#else>""</#if>;
          if (initDate != "") {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
            <#-- bad hack because the JS date parser doesn't understand dots in the date / time string -->
            if (initDate.indexOf('.') != -1) {
              initDate = initDate.substring(0, initDate.indexOf('.'));
            }
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var dateObj = Date.parseExact(initDate, ofbizTime);
            var formatedObj = dateObj.toString(dateFormat);
            jQuery("#${id}_i18n").val(formatedObj);
          }
          
          jQuery("#${id}").change(function() {
            var ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
            var newValue = ""
            if (this.value != "") {
              var dateObj = Date.parseExact(this.value, ofbizTime);
              var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>;
              newValue = dateObj.toString(dateFormat);
            }
            jQuery("#${id}_i18n").val(newValue);
          });
          jQuery("#${id}_i18n").change(function() {
            var dateFormat = "dd/MM/yyyy"<#if shortDateInput?exists && !shortDateInput> + " " + "HH:mm:ss"</#if>,
            newValue = "",
            dateObj = Date.parseExact(this.value, dateFormat),
            ofbizTime;
            if (this.value != "" && dateObj !== null) {
              ofbizTime = "<#if shortDateInput?exists && shortDateInput>yyyy-MM-dd<#else>yyyy-MM-dd HH:mm:ss</#if>";
              newValue = dateObj.toString(ofbizTime);
            }
            else { // invalid input
              jQuery("#${id}_i18n").val("");
            }
            jQuery("#${id}").val(newValue);
          });
        } else {
          <#-- fallback if no language specific js date file is found -->
          jQuery("#${id}").change(function() {
          jQuery("#${id}_i18n").val(this.value);
        });
        jQuery("#${id}_i18n").change(function() {
          jQuery("#${id}").val(this.value);
        });
      }

      <#if shortDateInput?exists && shortDateInput>
        jQuery("#${id}").datepicker({
      <#else>
        jQuery("#${id}").datetimepicker({
          showSecond: true,
          <#-- showMillisec: true, -->
          timeFormat: 'hh:mm:ss',
          stepHour: 1,
          stepMinute: 1,
          stepSecond: 1,
      </#if>
          showOn: 'button',
          buttonText: '',
          buttonImageOnly: false,
          dateFormat: 'yy-mm-dd'
        })
        <#if mask?has_content>.mask("${mask}")</#if>
        ;
      </script>
    </#if>
    <#if timeDropdown?has_content && timeDropdown=="time-dropdown">
      <select name="${timeHourName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
        <#if isTwelveHour>
          <#assign x=11>
          <#list 0..x as i>
            <option value="${i}"<#if hour1?has_content><#if i=hour1> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        <#else>
          <#assign x=23>
          <#list 0..x as i>
            <option value="${i}"<#if hour2?has_content><#if i=hour2> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </#if>
        </select>:<select name="${timeMinutesName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
          <#assign values = Static["org.ofbiz.base.util.StringUtil"].toList(timeValues)>
          <#list values as i>
            <option value="${i}"<#if minutes?has_content><#if i?number== minutes ||((i?number==(60 -step?number)) && (minutes &gt; 60 - (step?number/2))) || ((minutes &gt; i?number )&& (minutes &lt; i?number+(step?number/2))) || ((minutes &lt; i?number )&& (minutes &gt; i?number-(step?number/2)))> selected="selected"</#if></#if>>${i}</option><#rt/>
          </#list>
        </select>
        <#rt/>
        <#if isTwelveHour>
          <select name="${ampmName}" <#if classString?has_content>class="${classString}"</#if>><#rt/>
            <option value="AM" <#if amSelected == "selected">selected="selected"</#if> >AM</option><#rt/>
            <option value="PM" <#if pmSelected == "selected">selected="selected"</#if>>PM</option><#rt/>
          </select>
        <#rt/>
      </#if>
    </#if>
    <input type="hidden" name="${compositeType}" value="Timestamp"/>
  </span>
</#macro>

<#macro renderDropDownField name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>
      <#list options as item>
        <#if multiple?has_content>
          <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
        <#else>
          <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderDropDownFieldSelectedAll name className alert id multiple formName otherFieldName event action size firstInList currentValue explicitDescription allowEmpty options fieldName otherFieldName otherValue otherFieldSize dDFCurrent ajaxEnabled noCurrentSelectedKey ajaxOptions frequency minChars choices autoSelect partialSearch partialChars ignoreCase fullSearch selectedAll>
  <span class="ui-widget">
    <select name="${name?default("")}<#rt/>" <@renderClass className alert /><#if id?has_content> id="${id}"</#if><#if multiple?has_content> multiple="multiple"</#if><#if otherFieldSize gt 0> onchange="process_choice(this,document.${formName}.${otherFieldName})"</#if><#if event?has_content> ${event}="${action}"</#if><#if size?has_content> size="${size}"</#if>>
      <#if firstInList?has_content && currentValue?has_content && !multiple?has_content>
        <option selected="selected" value="${currentValue}">${explicitDescription}</option><#rt/>
        <option value="${currentValue}">---</option><#rt/>
      </#if>
      <#if allowEmpty?has_content || !options?has_content>
        <option value="">&nbsp;</option>
      </#if>
      
      <#list options as item>
        <#if multiple?has_content>
            <#if (currentValue?exists && currentValue?has_content) || (currentValue?has_content && item.selected?has_content) || (!currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key)>
                <option<#if currentValue?has_content && item.selected?has_content> selected="${item.selected}" <#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected" </#if> value="${item.key}">${item.description}</option><#rt/>
            <#else>
                <#if selectedAll?has_content>
                    <option selected="selected" value="${item.key}">${item.description}</option><#rt/>
                <#else>
                    <option value="${item.key}">${item.description}</option><#rt/>
                </#if>
            </#if>
        <#else>
            <option<#if currentValue?has_content && currentValue == item.key && dDFCurrent?has_content && "selected" == dDFCurrent> selected="selected"<#elseif !currentValue?has_content && noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> selected="selected"</#if> value="${item.key}">${item.description}</option><#rt/>
        </#if>
      </#list>
    </select>
  </span>
  <#if otherFieldName?has_content>
    <noscript><input type='text' name='${otherFieldName}' /></noscript>
    <script type='text/javascript' language='JavaScript'><!--
      disa = ' disabled';
      if(other_choice(document.${formName}.${fieldName}))
        disa = '';
      document.write("<input type='text' name='${otherFieldName}' value='${otherValue?js_string}' size='${otherFieldSize}'"+disa+" onfocus='check_choice(document.${formName}.${fieldName})' />");
      if(disa && document.styleSheets)
      document.${formName}.${otherFieldName}.style.visibility  = 'hidden';
    //--></script>
  </#if>

  <#if ajaxEnabled>
    <script language="JavaScript" type="text/javascript">
      ajaxAutoCompleteDropDown();
      jQuery(function() {
        jQuery("#${id}").combobox();
      });
    </script>
  </#if>
</#macro>

<#macro renderCheckField items className alert id allChecked currentValue name event action>
  <#list items as item>
    <label style="display:inline;" <@renderClass className alert />><#rt/>
      <input type="checkbox"<#if (item_index == 0)> id="${id}"</#if><#rt/>
        <#if allChecked?has_content && allChecked> checked="checked" <#elseif allChecked?has_content && !allChecked>
          <#elseif currentValue?has_content && currentValue==item.value> checked="checked"</#if> 
          name="${name?default("")?html}" value="${item.value?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
        ${item.description?default("")}
        <span class="lbl"></span>
    </label>
  </#list>
</#macro>

<#macro renderRadioField items className alert currentValue noCurrentSelectedKey name event action>
  <#list items as item>
    <span <@renderClass className alert />><#rt/>
      <input type="radio"<#if currentValue?has_content><#if currentValue==item.key> checked="checked"</#if>
        <#elseif noCurrentSelectedKey?has_content && noCurrentSelectedKey == item.key> checked="checked"</#if> 
        name="${name?default("")?html}" value="${item.key?default("")?html}"<#if event?has_content> ${event}="${action}"</#if>/><#rt/>
      ${item.description}
    </span>
  </#list>
</#macro>

<#macro renderSubmitField buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <button type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> </#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>><i class="${imgSrc}"></i>${title}</button>
  </#if>
</#macro>

<#macro renderSubmitFieldBU buttonType className alert formName title name event action imgSrc confirmation containerId ajaxUrl>
  <#if buttonType=="text-link">
    <a <@renderClass className alert /> href="javascript:document.${formName}.submit()" <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>><#if title?has_content>${title}</#if> </a>
  <#elseif buttonType=="image">
    <input type="image" src="${imgSrc}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if>
    <#if title?has_content> alt="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content>onclick="return confirm('${confirmation?js_string}');"</#if>/>
  <#else>
    <input type="<#if containerId?has_content>button<#else>submit</#if>" <@renderClass className alert />
    <#if name?exists> name="${name}"</#if><#if title?has_content> value="${title}"</#if><#if event?has_content> ${event}="${action}"</#if>
    <#if containerId?has_content> onclick="<#if confirmation?has_content>if (confirm('${confirmation?js_string}')) </#if>ajaxSubmitFormUpdateAreas('${containerId}', '${ajaxUrl}')"
      <#else><#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}');"</#if>
    </#if>/>
  </#if>
</#macro>

<#macro renderResetField className alert name title>
  <input type="reset" <@renderClass className alert /> name="${name}"<#if title?has_content> value="${title}"</#if>/>
</#macro>

<#macro renderHiddenField name value id event action>
  <input type="hidden" name="${name}"<#if value?has_content> value="${value}"</#if><#if id?has_content> id="${id}"</#if><#if event?has_content && action?has_content> ${event}="${action}"</#if>/>
</#macro>

<#macro renderIgnoredField></#macro>

<#macro renderFieldTitle style title id fieldHelpText="" for="">
  <label <#if for?has_content>for="${for}"</#if> <#if fieldHelpText?has_content> title="${fieldHelpText}"</#if><#if style?has_content> class="${style}"</#if><#if id?has_content> id="${id}"</#if>><#t/>
    ${title}<#t/>
  </label><#t/>
</#macro>

<#macro renderSingleFormFieldTitle></#macro>

<#macro renderFormOpen linkUrl formType targetWindow containerId containerStyle autocomplete name viewIndexField viewSizeField viewIndex viewSize useRowSubmit>
  <form method="post" action="${linkUrl}"<#if formType=="upload"> enctype="multipart/form-data"</#if><#if targetWindow?has_content> target="${targetWindow}"</#if><#if containerId?has_content> id="${containerId}"</#if> class=<#if containerStyle?has_content>"${containerStyle} form-horizontal"<#else>"basic-form form-horizontal"</#if> onsubmit="javascript:submitFormDisableSubmits(this)"<#if autocomplete?has_content> autocomplete="${autocomplete}"</#if> name="${name}"><#lt/>
    <#if useRowSubmit?has_content && useRowSubmit>
      <input type="hidden" name="_useRowSubmit" value="Y"/>
      <#if linkUrl?index_of("VIEW_INDEX") &lt;= 0 && linkUrl?index_of(viewIndexField) &lt;= 0>
        <input type="hidden" name="${viewIndexField}" value="${viewIndex}"/>
      </#if>
      <#if linkUrl?index_of("VIEW_SIZE") &lt;= 0 && linkUrl?index_of(viewSizeField) &lt;= 0>
        <input type="hidden" name="${viewSizeField}" value="${viewSize}"/>
      </#if>
    </#if>
    <div class="row-fluid">
</#macro>
<#macro renderFormClose focusFieldName formName containerId hasRequiredField>
  </div></form><#lt/>
  <#if focusFieldName?has_content>
    <script language="JavaScript" type="text/javascript">
      var form = document.${formName};
      form.${focusFieldName}.focus();
      <#-- enable the validation plugin for all generated forms
      only enable the validation if min one field is marked as 'required' -->
      if (jQuery(form).find(".required").size() > 0) {
          jQuery(form).validate();
      }
    </script><#lt/>
  </#if>
  <#if containerId?has_content && hasRequiredField?has_content>
    <script type="text/javascript">
      jQuery("#${containerId}").validate({
        errorElement: 'div',
        errorClass: "invalid",
        errorPlacement: function(error, element) {
            element.addClass("border-error");
            if (element.parent() != null ){   
                element.parent().find("button").addClass("button-border");              
                error.appendTo(element.parent());
            }
          },
        unhighlight: function(element, errorClass) {
            $(element).removeClass("border-error");
            $(element).parent().find("button").removeClass("button-border");
        },
        submitHandler:
          function(form) {
            form.submit();
          }
      });
    </script>
  </#if>
</#macro>
<#macro renderMultiFormClose>
  </form><#lt/>
</#macro>

<#macro renderFormatListWrapperOpen formName style columnStyles>
  <table cellspacing="0" class="<#if style?has_content>${style}<#else>basic-table form-widget-table dark-grid</#if>"><#lt/>
</#macro>

<#macro renderFormatListWrapperClose formName>
  </table><#lt/>
</#macro>

<#macro renderFormatHeaderRowOpen style>
  <thead><tr role="row" class="<#if style?has_content>${style}<#else>header-row</#if>">
</#macro>
<#macro renderFormatHeaderRowClose>
  </tr></thead>
</#macro>
<#macro renderFormatHeaderRowCellOpen style positionSpan>
  <th class="hidden-phone"><i class="${style}"></i>${style}
</#macro>
<#macro renderFormatHeaderRowCellClose>
  </th>
</#macro>

<#macro renderFormatHeaderRowFormCellOpen style>
  <td <#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatHeaderRowFormCellClose>
  </td>
</#macro>
<#macro renderFormatHeaderRowFormCellTitleSeparator style isLast>
  <#if style?has_content><span class="${style}"></#if> - <#if style?has_content></span></#if>
</#macro>

<#macro renderFormatItemRowOpen formName itemIndex altRowStyles evenRowStyle oddRowStyle>
  <tr <#if itemIndex?has_content><#if itemIndex%2==0><#if evenRowStyle?has_content>class="${evenRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if><#else><#if oddRowStyle?has_content>class="${oddRowStyle}<#if altRowStyles?has_content> ${altRowStyles}</#if>"<#elseif altRowStyles?has_content>class="${altRowStyles}"</#if></#if></#if> >
</#macro>
<#macro renderFormatItemRowClose formName>
  </tr>
</#macro>
<#macro renderFormatItemRowCellOpen fieldName style positionSpan>
  <td <#if positionSpan?has_content && positionSpan gt 1>colspan="${positionSpan}"</#if><#if style?has_content>class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowCellClose fieldName>
  </td>
</#macro>
<#macro renderFormatItemRowFormCellOpen style>
  <td<#if style?has_content> class="${style}"</#if>>
</#macro>
<#macro renderFormatItemRowFormCellClose>
  </td>
</#macro>

<#macro renderFormatSingleWrapperOpen formName style>
  <#--
  <table cellspacing="0" <#if style?has_content>class="${style}"</#if>>
  -->
</#macro>
<#macro renderFormatSingleWrapperClose formName>
  <#--
  </table>
  -->
</#macro>

<#macro renderFormatFieldRowOpen>
  <#--  
  <tr>
  -->
  <div class="control-group no-left-margin">
</#macro>
<#macro renderFormatFieldRowOpenRow style>
  <#--  
  <tr>
  -->
  <div class="control-group no-left-margin ${style?if_exists}">
</#macro>
<#--
<#macro renderFormatFieldRowOpenRow widgetStyleRow>
  <div class="control-group no-left-margin ${widgetStyleRow}">
</#macro>
-->
<#macro renderFormatFieldRowClose>
  <#-- 
  </tr>
  -->
  </div>
</#macro>
<#macro renderFormatFieldRowTitleCellOpen style>
  <#--
  <td class="<#if style?has_content>${style}<#else>label</#if>">
  -->
  <label class="${style}">
</#macro>
<#macro renderFormatFieldRowTitleCellClose>
  </label>
</#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>
  <#--
  <td<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>>
  -->
  <div class="controls">
</#macro>
<#macro renderFormatFieldRowWidgetCellClose>
  <#--
  </td>
  -->
  </div>
</#macro>

<#--
    Initial work to convert table based layout for "single" form to divs.
<#macro renderFormatSingleWrapperOpen style> <div <#if style?has_content>class="${style}"</#if> ></#macro>
<#macro renderFormatSingleWrapperClose> </div></#macro>

<#macro renderFormatFieldRowOpen>  <div></#macro>
<#macro renderFormatFieldRowClose>  </div></#macro>
<#macro renderFormatFieldRowTitleCellOpen style>   <div class="<#if style?has_content>${style}<#else>label</#if>"></#macro>
<#macro renderFormatFieldRowTitleCellClose></div></#macro>
<#macro renderFormatFieldRowSpacerCell></#macro>
<#macro renderFormatFieldRowWidgetCellOpen positionSpan style>   <div<#if positionSpan?has_content && positionSpan gt 0> colspan="${1+positionSpan*3}"</#if><#if style?has_content> class="${style}"</#if>></#macro>
<#macro renderFormatFieldRowWidgetCellClose></div></#macro>

-->


<#macro renderFormatEmptySpace>&nbsp;</#macro>

<#macro renderTextFindField name value defaultOption opEquals opBeginsWith opContains opIsEmpty opNotEqual className alert size maxlength autocomplete titleStyle hideIgnoreCase ignCase ignoreCase>
  <#if opEquals?has_content>
    <select <#if name?has_content>name="${name}_op"</#if>    class="selectBox"><#rt/>
      <option value="equals"<#if defaultOption=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="like"<#if defaultOption=="like"> selected="selected"</#if>>${opBeginsWith}</option><#rt/>
      <option value="contains"<#if defaultOption=="contains"> selected="selected"</#if>>${opContains}</option><#rt/>
      <option value="empty"<#rt/><#if defaultOption=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
      <option value="notEqual"<#if defaultOption=="notEqual"> selected="selected"</#if>>${opNotEqual}</option><#rt/>
    </select>
  <#else>
    <input type="hidden" name=<#if name?has_content> "${name}_op"</#if>    value="${defaultOption}"/><#rt/>
  </#if>
    <input type="text" <@renderClass className alert /> name="${name}"<#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <#if titleStyle?has_content><span class="${titleStyle}" ><#rt/></#if>
    <label style="display:inline;">
        <#if hideIgnoreCase>
          <input type="hidden" name="${name}_ic" value=<#if ignCase>"Y"<#else> ""</#if>/><#rt/>
        <#else>
          <input style="height:20px;" type="checkbox" name="${name}_ic" value="Y" <#if ignCase> checked="checked"</#if> /> <span class="lbl">${ignoreCase}</span><#rt/>
        </#if>
    </label>
    <#if titleStyle?has_content>
  </#if>
  <input type="checkbox" />
</#macro>

<#macro renderDateFindField className alert name localizedInputTitle value size maxlength dateType formName defaultDateTimeString imgSrc localizedIconTitle titleStyle defaultOptionFrom defaultOptionThru opEquals opSameDay opGreaterThanFromDayStart opGreaterThan opGreaterThan opLessThan opUpToDay opUpThruDay opIsEmpty>
  <span class="view-calendar">
    <input id="${name?html}_fld0_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name?html}_fld0_value"</#if><#if localizedInputTitle?has_content> title="${localizedInputTitle}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld0_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld0_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select<#if name?has_content> name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
      <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
      <option value="sameDay"<#if defaultOptionFrom=="sameDay"> selected="selected"</#if>>${opSameDay}</option><#rt/>
      <option value="greaterThanFromDayStart"<#if defaultOptionFrom=="greaterThanFromDayStart"> selected="selected"</#if>>${opGreaterThanFromDayStart}</option><#rt/>
      <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span><#rt/>
    </#if>
    <#rt/>
    <input id="${name?html}_fld1_value" type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if localizedInputTitle?exists> title="${localizedInputTitle?html}"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if>/><#rt/>
    <#if dateType != "time">
      <script type="text/javascript">
        <#if dateType == "date">
          jQuery("#${name?html}_fld1_value").datepicker({
        <#else>
          jQuery("#${name?html}_fld1_value").datetimepicker({
            showSecond: true,
            <#-- showMillisec: true, -->
            timeFormat: 'hh:mm:ss',
            stepHour: 1,
            stepMinute: 5,
            stepSecond: 10,
        </#if>
            showOn: 'button',
            buttonImage: '',
            buttonText: '',
            buttonImageOnly: false,
            dateFormat: 'yy-mm-dd'
          });
      </script>
      <#rt/>
    </#if>
    <#if titleStyle?has_content>
      <span class="${titleStyle}"><#rt/>
    </#if>
    <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
      <option value="opLessThan"<#if defaultOptionThru=="opLessThan"> selected="selected"</#if>>${opLessThan}</option><#rt/>
      <option value="upToDay"<#if defaultOptionThru=="upToDay"> selected="selected"</#if>>${opUpToDay}</option><#rt/>
      <option value="upThruDay"<#if defaultOptionThru=="upThruDay"> selected="selected"</#if>>${opUpThruDay}</option><#rt/>
      <option value="empty"<#if defaultOptionFrom=="empty"> selected="selected"</#if>>${opIsEmpty}</option><#rt/>
    </select><#rt/>
    <#if titleStyle?has_content>
      </span>
    </#if>
  </span>
</#macro>

<#macro renderRangeFindField className alert name value size maxlength autocomplete titleStyle defaultOptionFrom opEquals opGreaterThan opGreaterThanEquals opLessThan opLessThanEquals value2 defaultOptionThru>
  <input type="text" <@renderClass className alert /> <#if name?has_content>name="${name}_fld0_value"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select <#if name?has_content>name="${name}_fld0_op"</#if> class="selectBox"><#rt/>
    <option value="equals"<#if defaultOptionFrom=="equals"> selected="selected"</#if>>${opEquals}</option><#rt/>
    <option value="greaterThan"<#if defaultOptionFrom=="greaterThan"> selected="selected"</#if>>${opGreaterThan}</option><#rt/>
    <option value="greaterThanEqualTo"<#if defaultOptionFrom=="greaterThanEqualTo"> selected="selected"</#if>>${opGreaterThanEquals}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span><#rt/>
  </#if>
  <br /><#rt/>
  <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}_fld1_value"</#if><#if value2?has_content> value="${value2}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
  <#if titleStyle?has_content>
    <span class="${titleStyle}" ><#rt/>
  </#if>
  <select name=<#if name?has_content>"${name}_fld1_op"</#if> class="selectBox"><#rt/>
    <option value="lessThan"<#if defaultOptionThru=="lessThan"> selected="selected"</#if>>${opLessThan?html}</option><#rt/>
    <option value="lessThanEqualTo"<#if defaultOptionThru=="lessThanEqualTo"> selected="selected"</#if>>${opLessThanEquals?html}</option><#rt/>
  </select><#rt/>
  <#if titleStyle?has_content>
    </span>
  </#if>
</#macro>

<#--
@renderLookupField

Description: Renders a text input field as a lookup field.

Parameter: name, String, required - The name of the lookup field.
Parameter: formName, String, required - The name of the form that contains the lookup field.
Parameter: fieldFormName, String, required - Contains the lookup window form name.
Parameter: className, String, optional - The CSS class name for the lookup field.
Parameter: alert, String, optional - If "true" then the "alert" CSS class will be added to the lookup field.
Parameter: value, Object, optional - The value of the lookup field.
Parameter: size, String, optional - The size of the lookup field.
Parameter: maxlength, String or Integer, optional - The max length of the lookup field.
Parameter: id, String, optional - The ID of the lookup field.
Parameter: event, String, optional - The lookup field event that invokes "action". If the event parameter is not empty, then the action parameter must be specified as well.
Parameter: action, String, optional - The action that is invoked on "event". If action parameter is not empty, then the event parameter must be specified as well.
Parameter: readonly, boolean, optional - If true, the lookup field is made read-only.
Parameter: autocomplete, String, optional - If not empty, autocomplete is turned off for the lookup field.
Parameter: descriptionFieldName, String, optional - If not empty and the presentation parameter contains "window", specifies an alternate input field for updating.
Parameter: targetParameterIter, List, optional - Contains a list of form field names whose values will be passed to the lookup window.
Parameter: imgSrc, Not used.
Parameter: ajaxUrl, String, optional - Contains the Ajax URL, used only when the ajaxEnabled parameter contains true.
Parameter: ajaxEnabled, boolean, optional - If true, invokes the Ajax auto-completer.
Parameter: presentation, String, optional - Contains the lookup window type, either "layer" or "window".
Parameter: width, String or Integer, optional - The width of the lookup field.
Parameter: height, String or Integer, optional - The height of the lookup field.
Parameter: position, String, optional - The position style of the lookup field.
Parameter: fadeBackground, ?
Parameter: clearText, String, optional - If the readonly parameter is true, clearText contains the text to be displayed in the field, default is CommonClear label.
Parameter: showDescription, String, optional - If the showDescription parameter is true, a special span with css class "tooltip" will be created at right of the lookup button and a description will fill in (see setLookDescription in selectall.js). For now not when the lookup is read only.
Parameter: initiallyCollapsed, Not used.
Parameter: lastViewName, String, optional - If the ajaxEnabled parameter is true, the contents of lastViewName will be appended to the Ajax URL.
Parameter: zIndex, String, optional - set z-index for dialog
-->
<#macro renderLookupField name formName fieldFormName className="" alert="false" value="" size="" maxlength="" id="" event=""  action="" readonly=false autocomplete="" descriptionFieldName="" targetParameterIter="" imgSrc="" ajaxUrl="" ajaxEnabled=false javaScriptEnabled=false presentation="layer" width="" height="" position="" fadeBackground="true" clearText="" showDescription="" initiallyCollapsed="" title="" zIndex="" lastViewName="main" >
  <#if Static["org.ofbiz.widget.ModelWidget"].widgetBoundaryCommentsEnabled(context)>
  <!-- @renderLookupField -->
  </#if>
  <#if (!ajaxUrl?has_content) && ajaxEnabled?has_content && ajaxEnabled>
    <#local ajaxUrl = requestAttributes._REQUEST_HANDLER_.makeLink(request, response, fieldFormName)/>
    <#local ajaxUrl = id + "," + ajaxUrl + ",ajaxLookup=Y" />
  </#if>
  <#if (!showDescription?has_content)>
    <#local showDescriptionProp = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.lookup.showDescription", "N")>
    <#if "Y" == showDescriptionProp>
      <#local showDescription = "true" />
    <#else>
      <#local showDescription = "false" />
    </#if>
  </#if>
  <#if ajaxEnabled?has_content && ajaxEnabled>
    <script type="text/javascript">
      jQuery(document).ready(function(){
        if (!jQuery('form[name="${formName}"]').length) {
          alert("Developer: for lookups to work you must provide a form name!")
        }
      });
    </script>
    <style type="text/css">
        .ui-dialog{
            padding: 0px !important;
            <!--position : absolute !important;-->
        }
        .ui-widget-header{
            border: 1px solid #FFF !important;
            opacity:1 !important;
        }
        .ui-corner-all{
            border-bottom-right-radius: 0px !important;
            border-bottom-left-radius: 0px !important;
            border-top-right-radius: 0px !important;
            border-top-left-radius: 0px !important;
        }
        .ui-widget-overlay{
            opacity: 0.9 !important;
            background-color: black !important;
        }
      </style>
  </#if>
  <span class="field-lookup">
    <#if size?has_content && size=="0">
      <input type="hidden" <#if name?has_content> name="${name}"/></#if>
    <#else>
      <input type="text" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if>
        <#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#rt/>
        <#if readonly?has_content && readonly> readonly="readonly"</#if><#rt/><#if event?has_content && action?has_content> ${event}="${action}"</#if><#rt/>
        <#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/></#if>
    <#if presentation?has_content && descriptionFieldName?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup3(document.${formName?html}.${name?html},document.${formName?html}.${descriptionFieldName},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#elseif presentation?has_content && presentation == "window">
      <a href="javascript:call_fieldlookup2(document.${formName?html}.${name?html},'${fieldFormName}', '${presentation}'<#rt/>
      <#if targetParameterIter?has_content>
        <#list targetParameterIter as item>
          ,document.${formName}.${item}.value<#rt>
        </#list>
      </#if>
      );"></a><#rt>
    <#else>
      <#if ajaxEnabled?has_content && ajaxEnabled>
        <#assign defaultMinLength = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultMinLength")>
        <#assign defaultDelay = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("widget.properties", "widget.autocompleter.defaultDelay")>
        <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
        <#if !ajaxUrl?contains("searchValueFieldName=")>
          <#if descriptionFieldName?has_content && showDescription == "true">
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + descriptionFieldName />
          <#else>
            <#local ajaxUrl = ajaxUrl + "&amp;searchValueFieldName=" + name />
          </#if>
        </#if>
      </#if>
      <script type="text/javascript">
        jQuery(document).ready(function(){
          var options = {
            requestUrl : "${fieldFormName}",
            inputFieldId : "${id}",
            dialogTarget : document.${formName?html}.${name?html},
            dialogOptionalTarget : <#if descriptionFieldName?has_content>document.${formName?html}.${descriptionFieldName}<#else>null</#if>,
            formName : "${formName?html}",
            width : <#if width?has_content>"${width}"<#else>"1000"</#if>,
            height : <#if height?has_content>"${height}"<#else>"600"</#if>,
            position : "center",
            modal : "${fadeBackground}",
            ajaxUrl : <#if ajaxEnabled?has_content && ajaxEnabled>"${ajaxUrl}"<#else>""</#if>,
            showDescription : <#if ajaxEnabled?has_content && ajaxEnabled>"${showDescription}"<#else>false</#if>,
            presentation : "${presentation!}",
            defaultMinLength : "${defaultMinLength!2}",
            defaultDelay : "${defaultDelay!300}",
            show : { effect: 'slide-up',duration :200 },    
            hide: { effect: 'slide-up',duration : 100 },
            title : <#if title?has_content>"${title}"<#else>""</#if>,
            zIndex: "${zIndex}",
            args :
              <#rt/>
                <#if targetParameterIter?has_content>
                  <#assign isFirst = true>
                  <#lt/>[<#rt/>
                  <#list targetParameterIter as item>
                    <#if isFirst>
                      <#lt/>document.${formName}.${item}<#rt/>
                      <#assign isFirst = false>
                    <#else>
                      <#lt/> ,document.${formName}.${item}<#rt/>
                    </#if>
                  </#list>
                  <#lt/>]<#rt/>
                <#else>[]
                </#if>
                <#lt/>
          };
          new Lookup(options).init();
        });
      </script>
    </#if>
    <#if readonly?has_content && readonly>
      <a id="${id}_clear" 
        style="background:none;margin-left:5px;margin-right:15px;" 
        class="clearField" 
        href="javascript:void(0);" 
        onclick="javascript:document.${formName}.${name}.value='';
          jQuery('#' + jQuery('#${id}_clear').next().attr('id').replace('_button','') + '_${id}_lookupDescription').html('');
          <#if descriptionFieldName?has_content>document.${formName}.${descriptionFieldName}.value='';</#if>">
          <#if clearText?has_content>${clearText}<#else>${uiLabelMap.CommonClear}</#if>
      </a>
    </#if>
  </span>
  <#if ajaxEnabled?has_content && ajaxEnabled && (presentation?has_content && presentation == "window")>
    <#if ajaxUrl?index_of("_LAST_VIEW_NAME_") < 0>
      <#local ajaxUrl = ajaxUrl + "&amp;_LAST_VIEW_NAME_=" + lastViewName />
    </#if>
    <script language="JavaScript" type="text/javascript">ajaxAutoCompleter('${ajaxUrl}', ${showDescription}, ${defaultMinLength!2}, ${defaultDelay!300});</script><#t/>
  </#if>
</#macro>

<#macro renderNextPrev paginateStyle paginateFirstStyle viewIndex highIndex listSize viewSize ajaxEnabled javaScriptEnabled ajaxFirstUrl firstUrl paginateFirstLabel paginatePreviousStyle ajaxPreviousUrl previousUrl paginatePreviousLabel pageLabel ajaxSelectUrl selectUrl ajaxSelectSizeUrl selectSizeUrl commonDisplaying paginateNextStyle ajaxNextUrl nextUrl paginateNextLabel paginateLastStyle ajaxLastUrl lastUrl paginateLastLabel paginateViewSizeLabel renderBottom=true>
  <#if listSize gt 0>
  <#if listSize gt viewSize>
    <div class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">&nbsp;
      <div style="float:left">
      <ul>
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">, 
        <select name="pageSize" size="1" onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>">
        <#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>
      <div style="float:right;padding-right:15px">
      <ul>
        <li class="${paginateFirstStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxFirstUrl}')<#else>${firstUrl}</#if>"><i class="icon-double-angle-left"></i></a><#else>-disabled"><span><i class="icon-double-angle-left"></i></span></#if></li>
        <li class="${paginatePreviousStyle}<#if viewIndex gt 0>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxPreviousUrl}')<#else>${previousUrl}</#if>"><i class="icon-angle-left"></i></a><#else>-disabled"><span><i class="icon-angle-left"></i></span></#if></li>
        <#if listSize gt 0 && javaScriptEnabled><li class="nav-page-select">
            <select name="page" size="1" style="display:none;"></select>
            <script type="text/javascript">
                function pagenvg(inputvalue){
                    var div = document.createElement('div');
                    div.innerHTML = "${selectUrl}";
                    var decoded = div.firstChild.nodeValue;
                    return decoded + inputvalue;
                }
            </script>
            <#rt/>
          <#assign x=(listSize/viewSize)?ceiling>
            <#if (x>5)>
                <#if (viewIndex < 3)>
                    <#list 1..4 as i>
                      <#if i == (viewIndex+1)>
                        <li class="active"><a 
                      <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>" 
                      </#if> >${i}</a>
                        </li>
                    </#list>
                    <li><a>-></a></li>
                    <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                <#else>
                    <#if (x-viewIndex <4)>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <#list 3..0 as i>
                          <#if (x-i) == (viewIndex+1)>
                            <li class="active"><a 
                          <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-i-1}</#if>" 
                          </#if> >${x-i}</a>
                            </li>
                        </#list>
                    <#else>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}0</#if>">1</a>
                        <li><a><-</a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex-1}</#if>">${viewIndex}</a></li>
                        <li class="active">
                            <a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex}</#if>">${viewIndex+1}</a>
                        </li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${viewIndex+1}</#if>">${viewIndex+2}</a></li>
                        <li><a>-></a></li>
                        <li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${x-1}</#if>">${x}</a>
                    </#if>
                </#if>
            <#else>
                <#list 1..x as i>
                  <#if i == (viewIndex+1)>
                    <li class="active"><a 
                  <#else><li><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectUrl}')<#else>${selectUrl}${i-1}</#if>" 
                  </#if> >${i}</a>
                    </li>
                </#list>
            </#if>
        </#if>
        <li class="${paginateNextStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxNextUrl}')<#else>${nextUrl}</#if>"><i class="icon-angle-right"></i></a><#else>-disabled"><span><i class="icon-angle-right"></i></span></#if></li>
        <li class="${paginateLastStyle}<#if highIndex lt listSize>"><a href="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxLastUrl}')<#else>${lastUrl}</#if>"><i class="icon-double-angle-right"></i></i></a><#else>-disabled"><span><i class="icon-double-angle-right"></i></span></#if></li>
      </ul>
      </div>
    </div>
  <#else>
    <div  class="${paginateStyle} row-fluid dataTables_paginate paging_bootstrap pagination">
      <div style="float:left">
      <ul style="float:inherit;width:100%;" >
        <li class="nav-displaying">${commonDisplaying}</li>
        <#if javaScriptEnabled><li class="nav-pagesize">, <select name="pageSize" size="1"  onchange="<#if ajaxEnabled>javascript:ajaxUpdateAreas('${ajaxSelectSizeUrl}')<#else>location.href='${selectSizeUrl}';</#if>"><#rt/>
          <#assign availPageSizes = [10, 20, 30, 50, 100, 200]>
          <#list availPageSizes as ps>
            <option<#if viewSize == ps> selected="selected" </#if> value="${ps}">${ps}</option>
          </#list>
          </select> ${paginateViewSizeLabel}</li>
        </#if>
      </ul>
      </div>    
    </div>
  </#if>
  </#if>
</#macro>

<#macro renderFileField className alert name value size maxlength autocomplete>
  <input type="file" id="${name}" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/><#rt/>
    <style type="text/css">
        .ace-file-input{
            width: 220px !important;
        }
    </style>
    <script type="text/javascript">
    function addadditionImages(){
        $('#${name}').ace_file_input({
                no_file:'No File ...',
                btn_choose:'Choose',
                btn_change:'Change',
                droppable:false,
                onchange:null,
                thumbnail:false //| true | large
                //whitelist:'gif|png|jpg|jpeg'
                //blacklist:'exe|php'
                //onchange:''
                //
            });
            }
  </script>
</#macro>
<#macro renderPasswordField className alert name value size maxlength id autocomplete>
  <input type="password" <@renderClass className alert /><#if name?has_content> name="${name}"</#if><#if value?has_content> value="${value}"</#if><#if size?has_content> size="${size}"</#if><#if maxlength?has_content> maxlength="${maxlength}"</#if><#if id?has_content> id="${id}"</#if><#if autocomplete?has_content> autocomplete="off"</#if>/>
</#macro>
<#macro renderImageField value description alternate style event action><img<#if value?has_content> src="${value}"</#if><#if description?has_content> title="${description}"</#if> alt="<#if alternate?has_content>${alternate}"</#if><#if style?has_content> class="${style}"</#if><#if event?has_content> ${event?html}="${action}" </#if>/></#macro>

<#macro renderBanner style leftStyle rightStyle leftText text rightText>
  <table width="100%">
    <tr><#rt/>
      <#if leftText?has_content><td align="left"><#if leftStyle?has_content><div class="${leftStyle}"></#if>${leftText}<#if leftStyle?has_content></div></#if></td><#rt/></#if>
      <#if text?has_content><td align="center"><#if style?has_content><div class="${style}"></#if>${text}<#if style?has_content></div></#if></td><#rt/></#if>
      <#if rightText?has_content><td align="right"><#if rightStyle?has_content><div class="${rightStyle}"></#if>${rightText}<#if rightStyle?has_content></div></#if></td><#rt/></#if>
    </tr>
  </table>
</#macro>

<#macro renderContainerField id className><div id="${id}" class="${className}"/></#macro>

<#macro renderFieldGroupOpen style id title collapsed collapsibleAreaId collapsible expandToolTip collapseToolTip>
  <#if style?has_content || id?has_content || title?has_content><#if style?contains("begin-group-group")><span class="span12 no-left-margin"></#if><div class="<#if style?has_content> ${style}</#if>">
    <#if !style?contains("no-widget-header")>
    <div class="widget-box <#if collapsed && collapsible>collapsed<#else></#if>">
    <div class="widget-header widget-header-small header-color-blue2">
      <#if collapsible>
        <#--
        <ul>
          <li class="<#if collapsed>collapsed">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    <#else>expanded">
                      <a onclick="javascript:toggleCollapsiblePanel(this, '${collapsibleAreaId}', '${expandToolTip}', '${collapseToolTip}');">
                    </#if>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<#if title?has_content>${title}</#if></a>
          </li>
        </ul>
        -->
        <h6><#if title?has_content>${title}</#if></h6>
        <div class="widget-toolbar">
                <a href="#" data-action="collapse"><i class="icon-chevron-down" onclick="javascript:changeIconChev($(this));toggleScreenlet(this, '${collapsibleAreaId}', 'true', '${expandToolTip}', '${collapseToolTip}');"<#if expandToolTip?has_content> title="${expandToolTip}"</#if>></i></a>
        </div>
      <#else>
        <#if title?has_content>${title}</#if>
      </#if><#rt/>
    </div>
    <div class="widget-body" id="${collapsibleAreaId}">
    <div class="widget-body-inner" style="display: block;">
    <div class="widget-main row-fluid span12">
    </#if>
  </#if>
</#macro>

<#macro renderFieldGroupClose style id title>
<#if style?has_content || id?has_content || title?has_content>
    <#if !style?contains("no-widget-header")>
    </div></div></div></div></div>
    <#else>
    </div>
    </#if>
    <#if style?contains("end-group-group")></span></#if>
</#if>
</#macro>

<#macro renderHyperlinkTitle name title showSelectAll="N">
  <#if title?has_content>${title}<br /></#if>
  <#if showSelectAll="Y"><input type="checkbox" name="selectAll" value="Y" onclick="javascript:toggleAll(this, '${name}');"/></#if>
</#macro>

<#macro renderSortField style title linkUrl ajaxEnabled tooltip="">
  <a<#if style?has_content> class="${style}"</#if> href="<#if ajaxEnabled?has_content && ajaxEnabled>javascript:ajaxUpdateAreas('${linkUrl}')<#else>${linkUrl}</#if>"<#if tooltip?has_content> title="${tooltip}"</#if>>${title}</a>
</#macro>

<#macro formatBoundaryComment boundaryType widgetType widgetName><!-- ${boundaryType}  ${widgetType}  ${widgetName} --></#macro>

<#macro renderTooltip tooltip tooltipStyle>
  <#if tooltip?has_content><span class="<#if tooltipStyle?has_content>${tooltipStyle}<#else>tooltipob</#if>"><#--${tooltip}--></span><#rt/></#if>
</#macro>

<#macro renderClass className="" alert="">
  <#if className?has_content || (alert?has_content && alert=="true")> class="${className}<#if alert?has_content && alert=="true"> alert</#if>" </#if>
</#macro>

<#macro renderAsterisks requiredField requiredStyle>
  <#if requiredField=="true"><#if !requiredStyle?has_content></#if></#if>
</#macro>

<#macro makeHiddenFormLinkForm actionUrl name parameters targetWindow>
  <form method="post" action="${actionUrl}" <#if targetWindow?has_content>target="${targetWindow}"</#if> onsubmit="javascript:submitFormDisableSubmits(this)" name="${name}">
    <#list parameters as parameter>
      <input name="${parameter.name}" value="${parameter.value}" type="hidden"/>
    </#list>
  </form>
</#macro>
<#macro makeHiddenFormLinkAnchor linkStyle hiddenFormName event action imgSrc description confirmation>
  <a <#if linkStyle?has_content>class="${linkStyle}"</#if> href="javascript:document.${hiddenFormName}.submit()"
    <#if action?has_content && event?has_content> ${event}="${action}"</#if>
    <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>>
      <#if imgSrc?has_content><img src="${imgSrc}" alt=""/></#if>${description}</a>
</#macro>
<#macro makeHyperlinkString linkStyle hiddenFormName event action imgSrc title alternate linkUrl targetWindow description confirmation>
    <a <#if linkStyle?has_content>class="${linkStyle}"</#if> 
      href="${linkUrl}"<#if targetWindow?has_content> target="${targetWindow}"</#if>
      <#if action?has_content && event?has_content> ${event}="${action}"</#if>
      <#if confirmation?has_content> onclick="return confirm('${confirmation?js_string}')"</#if>
      <#if imgSrc?length == 0 && title?has_content> title="${title}"</#if>>
        <#if imgSrc?has_content><img src="${imgSrc}" alt="${alternate}" title="${title}"/></#if>${description}</a>
  </#macro>
<#macro test></#macro>
