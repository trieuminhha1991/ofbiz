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
<script type="text/javascript">
jQuery(document).ready(function() {
    <#--
    if (jQuery('#${dependentForm}').length) {
        jQuery("#${dependentForm}_${mainId}").change(function(e, data) {
            getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}');
      });
      getDependentDropdownValues('${requestName}', '${paramKey}', '${dependentForm}_${mainId}', '${dependentForm}_${dependentId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    }
    -->
    
    if (jQuery('#${dependentForm}').length) {
        jQuery("#${mainId}").change(function(e, data) {
            getDependentDropdownValues('${requestName}', '${paramKey}', '${mainId}', '${stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${currentResidenceState?if_exists}');
        });
        
        <#if currentResidence?exists>
            <#assign district2 = currentResidence.districtGeoId?if_exists>
            <#assign ward2 = currentResidence.wardGeoId?if_exists>
        </#if>
        jQuery("#${stateProvinceGeoId}").change(function(e, data) {
            getDependentDropdownValues('${requestName_district}', '${paramKey_district}', '${stateProvinceGeoId}', '${distGeoId}', '${responseName_district}', '${dependentKeyName}', '${descName}', '${district2?if_exists}');
        });
        
        jQuery("#${distGeoId}").change(function(e, data) {
            getDependentDropdownValues('${requestName_ward}', '${paramKey_ward}', '${distGeoId}', '${wardGeoId}', '${responseName_ward}', '${dependentKeyName}', '${descName}', '${ward2?if_exists}');
        });
        
        getDependentDropdownValues('${requestName}', '${paramKey}', '${mainId}', '${stateProvinceGeoId}', '${responseName}', '${dependentKeyName}', '${descName}', '${selectedDependentOption}');
    }
})
</script>