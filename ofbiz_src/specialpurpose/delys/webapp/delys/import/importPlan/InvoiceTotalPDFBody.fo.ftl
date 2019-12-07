<#escape x as x?xml>

	<fo:table table-layout="fixed" width="100%" margin-top="20px">
		<fo:table-column column-width="50%"/>
		<fo:table-column column-width="50%"/>
		<fo:table-body>
			<fo:table-row>
			<fo:table-cell font-size= "80%">
				<#list txtInvoicePartyInfo as livif>
					<fo:block font-weight="bold">${livif}</fo:block>
				</#list>
			</fo:table-cell>
			<fo:table-cell>
						<fo:table table-layout="fixed" width="100%" border= "1px solid black">
							<fo:table-column column-width="100%"/>
							<fo:table-body>
								<fo:table-row>
										<fo:table-cell height="25px">
											<fo:block margin="10px 5px 5px 5px" font-size= "90%" font-weight="bold">INVOICE</fo:block>
										</fo:table-cell>
								</fo:table-row>
								<fo:table-row>
									<fo:table-cell border= "1px solid black">
													<fo:table table-layout="fixed" width="100%">
															<fo:table-column column-width="50%"/>
															<fo:table-column column-width="50%"/>
															<fo:table-body>
																<#list txtInvoiceInfo as ivf>
																		<fo:table-row>
																			<fo:table-cell>
																				<fo:block text-align="left" margin-left="5px">${ivf.label}</fo:block>
																			</fo:table-cell>
																			<fo:table-cell>
																				<fo:block text-align="left">${ivf.text}</fo:block>
																			</fo:table-cell>
																		</fo:table-row>
																</#list>
															</fo:table-body>
													</fo:table>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
			</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block width="100%" border= "1px solid black" margin-top="25px"></fo:block>
	<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
	<fo:table-column column-width="5%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="9%"/>
	<fo:table-column column-width="13%"/>
	<fo:table-column column-width="14%"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					<fo:block>Pos</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Article-No</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Delevery-Quantity</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Price</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">per</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Unit</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>VAT</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Goods-Value</fo:block>
				</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
	<fo:table-column column-width="18%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="15%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="15%"/>
	<fo:table-column column-width="10%"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-align="center">Denotation</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="right">EAN-No</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="center">Net-Weight</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left">Unit</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block text-align="left">Gross-Weight</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Unit</fo:block>
				</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
	<fo:block font-size="80%" margin-left="25px" margin-top="15px">We invoice the products which were dilivered through Zott SE &amp; Co.KG, Germany</fo:block>
	<fo:table table-layout="fixed" font-size="80%" width="100%" margin="4px 0px 0px 12px">
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="70%"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					<fo:block text-decoration="underline">Delivery</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Delys Trading Development Investment Joint Stock Company N6E Trung Hoa Nhanh Chinh Thanh Xuan District, 00000 HANOI VIETNAM</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>
					<fo:table table-layout="fixed" font-size="100%" width="100%" margin-top="4px">
					<fo:table-column column-width="4%"/>
					<fo:table-column column-width="32%"/>
					<fo:table-column column-width="32%"/>
					<fo:table-column column-width="32%"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>Client-No</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>${txtClientNo}</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block>External-Order-No</fo:block>
								</fo:table-cell>
						 </fo:table-row>
						 <fo:table-row>
							 <fo:table-cell>
								<fo:block></fo:block>
							 </fo:table-cell>
							 <fo:table-cell>
							 	<fo:block>Order-No</fo:block>
							 </fo:table-cell>
							 <fo:table-cell>
							 	<fo:block>${txtOrderNo}</fo:block>
							 </fo:table-cell>
							 <fo:table-cell>
							 	<fo:block>${txtExternalOrderNo}</fo:block>
							 </fo:table-cell>
						 </fo:table-row>
						 
						 <#list txtDynamicField as dnmf>
									 <fo:table-row>
										 <fo:table-cell>
											<fo:block>${dnmf.thisIndex}</fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block>Delivery-No</fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block>${dnmf.thisDeliveryNo}</fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block></fo:block>
										 </fo:table-cell>
									 </fo:table-row>
									 <fo:table-row>
										 <fo:table-cell>
											<fo:block></fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block>Delivery-Date</fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block>${dnmf.thisDeliveryDate}</fo:block>
										 </fo:table-cell>
										 <fo:table-cell>
										 	<fo:block></fo:block>
										 </fo:table-cell>
									 </fo:table-row>
						</#list>
						</fo:table-body>
					</fo:table>
					</fo:block>
				</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
	
	<fo:table table-layout="fixed"  font-size="70%" width="100%" margin-top="20px">
	<fo:table-column column-width="5%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="10%"/>
	<fo:table-column column-width="14%"/>
	<fo:table-column column-width="5%"/>
	<fo:table-column column-width="13%"/>
	<fo:table-column column-width="14%"/>
	
	<fo:table-body>
		<#list listProductInfor as lst>
				<fo:table-row>
					<fo:table-cell>
						<fo:block >${lst.txtPos}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.txtArticleNO}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.txtKAR}  KAR</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.txtQuantity} ${lst.lblQuantityUomId}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">${lst.txtPrice}  ${lst.lblCurrencyUom}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block  text-align="center">${lst.txtPer}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.lblQuantityUomId} ${lst.txtVAT}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.txtGoodsValue}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				
				<fo:table-row>
					<fo:table-cell>
						<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.lblInternalName}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>${lst.txtEANNO}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block text-align="right">${lst.txtNetWeight} ${lst.lblWeightUomId}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell colspan="2">
						<fo:block>${lst.txtGrossWeight} ${lst.lblWeightUomId}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell>
						<fo:block></fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>Commodity Code</fo:block>
					</fo:table-cell>
					<fo:table-cell colspan="6">
						<fo:block>${lst.txtCommodity}</fo:block>
					</fo:table-cell>
				</fo:table-row>
		</#list>
		
	</fo:table-body>
</fo:table>
<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
	<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					<fo:block></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Total</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>VAT</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>VAT-Value</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>VAT-Amount</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>Final-Amount</fo:block>
				</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
	
<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
	<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
	<fo:table-column column-width="20%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
	<fo:table-column column-width="16%"/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					<fo:block></fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>${lblTotal}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>${lblVAT}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>${lblVATValue}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>${lblVATAmount}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>${lblFinalAmount}</fo:block>
				</fo:table-cell>
		 </fo:table-row>
		</fo:table-body>
	</fo:table>
<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
<fo:block  width="100%" text-align="right">${currencyUom}</fo:block>

<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
<fo:table-column column-width="18%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="18%"/>
	<fo:table-body>
		<fo:table-row>
			<fo:table-cell>
				<fo:block>No-of-Pallet</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${txtNoOfPallet}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>Total-Net-Weight</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${txtTotalNetWeight}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${weightUomId}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>Term-of-delivery</fo:block>
			</fo:table-cell>
	 </fo:table-row>
	 <fo:table-row>
		<fo:table-cell>
			<fo:block>No-of-Sale-Unit</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block>${txtNoOfSaleUnit}</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block>Total-Gross-Weight</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block>${txtTotalGrossWeightt}</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block>${weightUomId}</fo:block>
		</fo:table-cell>
		<fo:table-cell>
			<fo:block>CFR/ICD My Dinh</fo:block>
		</fo:table-cell>
	</fo:table-row>
	</fo:table-body>
</fo:table>
<fo:block width="100%" border= "1px solid black" margin-top="2px"></fo:block>
<fo:table table-layout="fixed" font-size="80%" width="100%" margin-top="4px">
<fo:table-column column-width="18%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="16%"/>
<fo:table-column column-width="18%"/>
	<fo:table-body>
		<fo:table-row>
			<fo:table-cell>
				<fo:block>DB-Tragerpallette</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${txtDBTragerpallette}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>x  ${txtX} ${weightUomId}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${txtTotalNoPallet}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>${weightUomId}</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>(additional gross weight)</fo:block>
			</fo:table-cell>
	 </fo:table-row>
	</fo:table-body>
</fo:table>

<fo:block font-size="80%">We here with certify that the mentioned goods in this invoice have been manufactured</fo:block>
<fo:block font-size="80%">In the Federal Republic of Germany and that the origin is at there as well as the</fo:block>
<fo:block font-size="80%">Indicated prices are the current export market prices.</fo:block>
<fo:block font-size="80%">For tax purposes the date of performing an abligation is the day of loading. Sales Condition are included in the Valid Listing.</fo:block>
</#escape>