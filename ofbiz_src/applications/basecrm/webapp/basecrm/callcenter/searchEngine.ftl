<script src="/crmresources/js/callcenter/searchEngine.js"></script>

<div class="row-fluid margin-bottom-10">
	<div class="span12">
		<div class="search-container zone" id="searchContainer">

			<div type="text" class="search-input" placeholder="${uiLabelMap.searchCustomerHint}">
				<#assign dataFieldSearch="[{ name: 'partyIdFrom', type: 'string' },
										{ name: 'partyCode', type: 'string' },
										{ name: 'partyTypeIdFrom', type: 'string' },
										{ name: 'firstName', type: 'string' },
										{ name: 'middleName', type: 'string' },
										{ name: 'lastName', type: 'string' },
										{ name: 'groupName', type: 'string' },
										{ name: 'infoString', type: 'string' },
										{ name: 'roleTypeIdFrom', type: 'string' },
										{ name: 'contactNumber', type: 'string' },
										{ name: 'ownerEmployee', type: 'string' },
										{ name: 'birthDate', type: 'date' }]"/>
				<@jqxCombobox id="searchCustomerInput" url="getContacts"  datafields=dataFieldSearch root="results" height="33px"
					placeHolder="${StringUtil.wrapString(uiLabelMap.CustomerFilterPlaceHolder)}" dropDownWidth="100%" customLoadFunction="true"
					data="SearchEngine.getSearchCondition()" renderer="SearchEngine.renderSearchResult" formatData="SearchEngine.formatData"
					remoteAutoComplete="true" autoDropDownHeight="true"/>
			</div>
		
		</div>
	</div>
</div>