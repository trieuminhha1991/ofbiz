<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<fo:layout-master-set>
		<fo:simple-page-master master-name="main" page-height="8in" page-width="8.5in"
		margin-top="0.1in" margin-bottom="1in" margin-left="0.2in" margin-right="0.5in">
			<fo:region-body margin-top="1in"/>
		</fo:simple-page-master>
	</fo:layout-master-set>
	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-size="11" font-family="Arial">
			<fo:block>
				<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
					${StringUtil.wrapString(uiLabelMap.PageTitleFindTransaction)}
				</fo:block>	
			</fo:block>	
			<fo:block>
				<fo:table table-layout="fixed">
					<fo:table-body>
						<fo:table-row>
							<fo:table-cell>
								<fo:table table-layout="fixed">
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.acctgTransId)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.acctgTransId?exists>${acctgTrans.acctgTransId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_acctgTransTypeId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.acctgTransTypeId?exists>${listDesAccTrans.accType?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${uiLabelMap.glFiscalTypeId?if_exists} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.glFiscalTypeId?exists>${listDesAccTrans.glFiscal?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.transactionDate?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.transactionDate?exists>${acctgTrans.transactionDate?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_partyId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.partyId?exists>${acctgTrans.partyId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_roleTypeId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.roleTypeId?exists>${listDesAccTrans.roleType?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_glJournalId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.glJournalId?exists>${listDesAccTrans.glJournal?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_scheduledPostingDate?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.scheduledPostingDate?exists>${acctgTrans.scheduledPostingDate?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_voucherRef?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.voucherRef?exists>${acctgTrans.voucherRef?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_voucherDate?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.voucherDate?exists>${acctgTrans.voucherDate?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_fixedAssetId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.fixedAssetId?exists>${listDesAccTrans.fxType?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.Description?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.description?exists>${StringUtil.wrapString(acctgTrans.description?if_exists)}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

									</fo:table-body>
								</fo:table>
							</fo:table-cell>
							<fo:table-cell>
								<fo:table>
									<fo:table-body>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.isPosted?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.isPosted?exists>${acctgTrans.isPosted?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.postedDate?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.postedDate?exists>${acctgTrans.postedDate?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_paymentId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.paymentId?exists>${acctgTrans.paymentId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_inventoryItemId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.inventoryItemId?exists>${acctgTrans.inventoryItemId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_physicalInventoryId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.physicalInventoryId?exists>${acctgTrans.physicalInventoryId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_theirAcctgTransId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.theirAcctgTransId?exists>${acctgTrans.theirAcctgTransId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_workEffortId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.workEffortId?exists>${listDesAccTrans.workEffortType?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_invoiceId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.invoiceId?exists>${acctgTrans.invoiceId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_finAccountTransId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.finAccountTransId?exists>${acctgTrans.finAccountTransId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_shipmentId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.shipmentId?exists>${acctgTrans.shipmentId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>

										<fo:table-row >
											<fo:table-cell width="150px" text-align="right">
												<fo:block font-style="italic" font-weight="bold">
													${StringUtil.wrapString(uiLabelMap.FormFieldTitle_receiptId?if_exists)} :&#160;
												</fo:block>
											</fo:table-cell>
											<fo:table-cell width="150px" text-align="left">
												<fo:block>
													<#if acctgTrans.receiptId?exists>${acctgTrans.receiptId?if_exists}</#if>
												</fo:block>
											</fo:table-cell>
										</fo:table-row>
									</fo:table-body>
								</fo:table>
							</fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
			<#if acctgTransEntries?has_content>	
			
			<fo:table border-color="black" border-style="solid" border-width="1pt">
			    <fo:table-column column-width="4.4cm"/>
			    <fo:table-column column-width="4.6cm"/>
			    <fo:table-column column-width="2cm"/>
			    <fo:table-column column-width="2cm"/>
			    <fo:table-column column-width="3cm"/>
			    <fo:table-column column-width="1cm"/>
			    <fo:table-column column-width="2cm"/>
			    <fo:table-column column-width="1cm"/>
			    <fo:table-header>
			        <fo:table-row border-color="black" background-color="#FFFF99" font-size="10pt">
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.glAccountClassId}</fo:block>
			        	</fo:table-cell>
			            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold" >${uiLabelMap.reconcileStatusId}</fo:block>
			        	</fo:table-cell>
			            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.accountCode}</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.organizationName}</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.glAccountTypeId}</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.debitCreditFlag}</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.origAmount}</fo:block>
			        	</fo:table-cell>
			        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" >
			            	<fo:block font-weight="bold">${uiLabelMap.amount}</fo:block>
			        	</fo:table-cell>
			        </fo:table-row>
			    </fo:table-header>
				<fo:table-body>
				<#list listDesAcctgTransEntries as entry>
						<fo:table-row>
				        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.glAccountClassDes?if_exists}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.descriptionStatus?if_exists}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${StringUtil.wrapString(entry.accountName?if_exists)}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.fullName?if_exists?default("")}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.accountType?if_exists}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.debitCreditFlag?if_exists}</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>
				                <@ofbizCurrency amount=entry.origAmount?if_exists/>
				                </fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>${entry.origCurrencyUomId?if_exists}</fo:block>
				            </fo:table-cell>
			            </fo:table-row>
			       </#list>     
				</fo:table-body>
			</fo:table>
			<!--  asdsd -->
			</#if>
		</fo:flow>
	</fo:page-sequence>
</fo:root>
