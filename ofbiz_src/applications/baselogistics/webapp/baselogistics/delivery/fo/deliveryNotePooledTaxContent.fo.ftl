<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="20px">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.DeliveryAndExportNote)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
		      	<fo:table-row>
		    		<fo:table-cell text-align="center">
		    			<fo:block>${labelInstance}</fo:block>
		    		</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
		    		<fo:table-cell text-align="center">
		    		</fo:table-cell>
				</fo:table-row>
		    </fo:table-body>
		 </fo:table>
	</fo:block>
	<fo:block margin-bottom="10px">
		<fo:table table-layout="fixed" space-after.optimum="10pt">
			<fo:table-column column-width="320pt"/>
			<fo:table-column column-width="10pt"/>
			<fo:table-column column-width="300pt"/>
			 <fo:table-body>
				<fo:table-row >
					<fo:table-cell>
						<fo:block margin-top="0px">${uiLabelMap.CustomerName}: ${partyTo?if_exists.lastName?if_exists} ${partyTo?if_exists.middleName?if_exists} ${partyTo?if_exists.firstName?if_exists} ${partyTo?if_exists.groupName?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block >${uiLabelMap.OrderId}: ${orderIdByDelivery?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell>
						<fo:block margin-top="3px">${uiLabelMap.CustomerId}: 
						<#if partyIdTo?exists && "_NA_" == partyIdTo>
						<#else>
							${partyIdTo?if_exists} 
						</#if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block margin-top="3px">${uiLabelMap.SalesChannel}: ${descriptionChannel?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<#assign fullAddress = ""/>
						<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId)! />
						<#list orderContactMechValueMaps as orderContactMechValueMap>
				          	<#assign contactMech = orderContactMechValueMap.contactMech>
				          	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
			              	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
				                <#assign postalAddress = orderContactMechValueMap.postalAddress>
				                <#if postalAddress?has_content>
							    	<#if postalAddress.toName?has_content>
							    		<#assign fullAddress = fullAddress + postalAddress.toName/>
							    		<#if postalAddress.attnName?has_content> 
							    		<#assign fullAddress = fullAddress + "(" +postalAddress.attnName + ")"/>
					    				</#if>
					    				<#assign fullAddress = fullAddress + "."/>
						    		</#if>
						            <#if postalAddress.address1?has_content> 
						            	<#assign fullAddress = fullAddress + " " + postalAddress.address1 + ","/> 
					            	</#if>
						            <#if postalAddress.address2?has_content> 
						             	<#assign fullAddress = fullAddress + " " +  postalAddress.address2 + ","/> 
						            </#if>
						            <#if postalAddress.wardGeoId?has_content>
						            	<#if "_NA_" == postalAddress.wardGeoId>
						            		<#assign fullAddress = fullAddress + "___, "/> 
							           	<#else>
							           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)!/>
							           		<#assign fullAddress = fullAddress + " " + wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId) + ", "/> 
										</#if>
									</#if>
						            <#if postalAddress.districtGeoId?has_content>
						            	<#if "_NA_" == postalAddress.districtGeoId>
						            		<#assign fullAddress = fullAddress + "___, "/> 
							           	<#else>
							            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)!/>
							            	<#assign fullAddress = fullAddress + " " + districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId) + ", "/> 
							           	</#if>
									</#if>
						            <#if postalAddress.city?has_content> ${postalAddress.city}, </#if>
						            <#if postalAddress.countryGeoId?has_content>
								      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)!>
								      	<#assign fullAddress = fullAddress + " " + country.get("geoName", locale)?default(country.geoId)/>
							    	</#if>
							    	<#if postalAddress.attnName?exists>
							    		<#assign receiverNumber = postalAddress.attnName/>
						    		<#else>
							    		<#assign receiverNumber = ''/>
							    	</#if>
				                </#if>
			                </#if>
			            </#list>
						<fo:block margin-top="3px">${uiLabelMap.PhoneNumber}: 
							<#if receiverNumber?has_content>
								${receiverNumber}
							<#elseif contactNumber?has_content>
								${contactNumber}
							</#if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell >
						<#if personCall?exists>
							<#if contactNumberCall?exists && contactNumberCall != ''>
								<fo:block margin-top="3px">${uiLabelMap.Orderer}: ${personCall.lastName?if_exists} ${personCall.middleName?if_exists} ${personCall.firstName?if_exists} - ${contactNumberCall}</fo:block>
							<#else>
								<fo:block margin-top="3px">${uiLabelMap.Orderer}: ${personCall.lastName?if_exists} ${personCall.middleName?if_exists} ${personCall.firstName?if_exists}</fo:block>
							</#if>
						<#else>
							<fo:block margin-top="3px">${uiLabelMap.Orderer}: </fo:block>
						</#if>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<fo:block margin-top="3px">
							${uiLabelMap.Address}: ${fullAddress?if_exists}
				    	</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell >
						<#if personSale?exists>
							<#if contactNumberSales?exists && contactNumberSales != ''>
								<fo:block margin-top="3px">${uiLabelMap.Saller}: ${personSale.lastName?if_exists} ${personSale.middleName?if_exists} ${personSale.firstName?if_exists} - ${contactNumberSales}</fo:block>
							<#else>
								<fo:block margin-top="3px">${uiLabelMap.Saller}: ${personSale.lastName?if_exists} ${personSale.middleName?if_exists} ${personSale.firstName?if_exists}</fo:block>
							</#if>
						<#else>
							<fo:block margin-top="3px">${uiLabelMap.Saller}: </fo:block>
						</#if>
					</fo:table-cell>
				</fo:table-row>
			 </fo:table-body>
		</fo:table>
	</fo:block>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.3mm black"> 
		<fo:table-column border="solid 0.3mm black" column-width="90pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="200pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="35pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="40pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="50pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="70pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="60pt"/>
		<fo:table-header border="solid 0.3mm black">
          <fo:table-row border="solid 0.3mm black">
		      	<fo:table-cell text-align="center" border="1px solid">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogMaHang)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" border="1px solid">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogTenHangHoa)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" border="1px solid">
		      		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitSum)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" border="1px solid">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantitySum)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center" border="1px solid">
		      		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</fo:block>
		        </fo:table-cell>
	         	<fo:table-cell text-align="center" border="1px solid">
	         		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ApTotal)}</fo:block>
	         	</fo:table-cell>
	         	<fo:table-cell text-align="center" border="1px solid">
	         		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Notes)}</fo:block>
         		</fo:table-cell>
          </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
		    <#assign listItemSum = []>
		    <#assign listProductIds = []>
		    <#list listItemTmp as item >
		    	<#assign existed = false>
		    	<#list listProductIds as idTmp >
		    		<#if item.code == idTmp>
		    			<#assign existed = true>
		    			<#break>
		    		</#if>
		    	</#list>
		    	<#if existed == false>
		    		<#assign listProductIds = listProductIds + [item.code]>
		    	</#if>
		    </#list>
		    <#list listProductIds as idTmp >
		    	<#assign quantityTmp = 0>
		    	<#assign prodNameTmp = "">
		    	<#assign unitTmp = "">
		    	<#assign unitPriceTmp = 0>
		    	<#assign itemTotalTmp = 0>
			    <#list listItemTmp as item >
			    	<#if item.code == idTmp>
			    		<#assign quantityTmp = quantityTmp + item.actualExportedQuantity?if_exists>
			    		<#assign prodNameTmp = item.productName?if_exists>
			    		<#assign unitTmp = item.unit?if_exists>
			    		<#assign unitPriceTmp = item.unitPriceWithTax?if_exists>
			    		<#assign itemTotalTmp = itemTotalTmp + item.itemTotal?if_exists>
			    	</#if>
			    </#list>
			    <#assign sampleQty = 0>
			    <#assign mapTmp = {"code": "${idTmp}", "productName": prodNameTmp, "unit": unitTmp, "actualExportedQuantity": quantityTmp, "unitPrice": unitPriceTmp, "itemTotal": itemTotalTmp, "sampleQuantity": sampleQty}>
			    <#assign listItemSum = listItemSum + [mapTmp]>
		    </#list>
		    <#list listItemSum as item >
		   		<fo:table-row border="solid 0.3mm black">
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${item.code?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="left">
						<fo:block line-height="20px" margin-left="2px">
							<#if item.productName?has_content>
				        		<#if item.productName?length &lt; 40>
					                ${StringUtil.wrapString(item.productName?html)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?html?substring(0, 40))} ...
					            </#if>
				        	</#if>
						</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">${item.unit?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<#if item.actualExportedQuantity?has_content && item.actualExportedQuantity &gt; 0>
		   					<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
		   					<#assign itemTotal = (item.unitPrice * item.actualExportedQuantity)>
			        	<#else>
			        		<#if item.actualExportedQuantity == 0 && ("DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId)>
			        			<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0", locale)}</fo:block>
			        		<#else>
			        			<fo:block></fo:block>
			        		</#if>
			        	</#if>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
				       <fo:block line-height="20px" margin-right="2px">
				       		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0", locale)}
				       </fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
					   <#if itemTotal?has_content>
					   		<fo:block line-height="20px" margin-right="2px">
				   				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemTotal?if_exists, "#,##0", locale)}
					   		</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block></fo:block>
				   </fo:table-cell>
			   </fo:table-row>
		    </#list>
		    <#assign listItemPromoSum = []>
		    <#assign listProductIds = []>
		    <#list listItemPromo as item >
		    	<#assign existed = false>
		    	<#list listProductIds as idTmp >
		    		<#if item.code == idTmp>
		    			<#assign existed = true>
		    			<#break>
		    		</#if>
		    	</#list>
		    	<#if existed == false>
		    		<#assign listProductIds = listProductIds + [item.code]>
		    	</#if>
		    </#list>
		    <#list listProductIds as idTmp >
		    	<#assign quantityTmp = 0>
		    	<#assign prodNameTmp = "">
		    	<#assign unitTmp = "">
			    <#list listItemPromo as item >
			    	<#if item.code == idTmp>
			    		<#assign quantityTmp = quantityTmp + item.actualExportedQuantity?if_exists>
			    		<#assign prodNameTmp = item.productName?if_exists>
			    		<#assign unitTmp = item.unit?if_exists>
			    	</#if>
			    </#list>
			    <#assign mapTmp = {"code": "${idTmp}", "productName": prodNameTmp, "unit": unitTmp, "actualExportedQuantity": quantityTmp}>
			    <#assign listItemPromoSum = listItemPromoSum + [mapTmp]>
		    </#list>
		    <#list listItemPromoSum as item >
				<fo:table-row border="solid 0.3mm black">
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${item.code?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="left">
						<fo:block line-height="20px" margin-left="2px">${item.productName?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">${item.unit?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<#if item.actualExportedQuantity?has_content && item.actualExportedQuantity &gt; 0>
		   					<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
			        	<#else>
		   					<fo:block line-height="20px" margin-right="2px">0</fo:block>
			        	</#if>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
				       <fo:block line-height="20px" margin-right="2px">
				       		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0", locale)}
				       </fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
				   		<#if item.actualExportedQuantity?exists>
					   		<fo:block line-height="20px" margin-right="2px">
					   			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##0", locale)}
					   		</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				       <fo:block></fo:block>
				   </fo:table-cell>
			   </fo:table-row>
			</#list>
			<#if displayTotal == true>
				<fo:table-row border="solid 0.2mm black">
				 	<fo:table-cell text-align="left" number-columns-spanned="5">
			        	<fo:block line-height="20px" margin-left="5px">${uiLabelMap.BSOrderItemsSubTotal}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
			        		<#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
			        			<fo:block line-height="20px" margin-right="2px"> 	
			        				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalWithTax, "#,##0", locale)}
			        			</fo:block>
			        		<#else>
				        		<fo:block line-height="20px" margin-right="2px"> 	
			        			</fo:block>
			        		</#if>
			        </fo:table-cell>
			        <fo:table-cell text-align="center">
				       <fo:block></fo:block>
			        </fo:table-cell>
			    </fo:table-row>
			    <#assign grandTotalByDelivery = totalWithTax>
				<#list orderHeaderAdjustments as orderHeaderAdjustment>
		            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
		            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
		            <#if orderHeaderAdjustment.orderItemSeqId?has_content && orderHeaderAdjustment.orderItemSeqId != "_NA_">
			            <#assign adjustmentAmountInDelivery = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].calcOrderItemAdjustmentInDelivery(delegator, orderHeaderAdjustment, deliveryId)>
			            <#if adjustmentAmountInDelivery != 0>
			            	<#assign grandTotalByDelivery = grandTotalByDelivery + adjustmentAmountInDelivery>
			            	<fo:table-row border="solid 0.2mm black">
		                        <fo:table-cell text-align="left" number-columns-spanned="5">
			            			<fo:block line-height="20px" margin-left="5px"> 
				            			<#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}</#if>
				                        <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}</#if>
			                        </fo:block>
		                        </fo:table-cell>        
		                        <fo:table-cell text-align="right">
			                        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
				                        <fo:block line-height="20px" margin-right="2px"> 
					                    	<#if (adjustmentAmount &lt; 0)>
					                    		<#assign adjustmentAmountNegative = -adjustmentAmountInDelivery>
					                    		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmountNegative, "#,##0", locale)}
					                        <#else>
					                    		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmountInDelivery, "#,##0", locale)}
					                        </#if>
				                        </fo:block>
					        		<#else>
						        		<fo:block line-height="20px" margin-right="2px"> 	
					        			</fo:block>
					        		</#if>
		                        </fo:table-cell>
			 			       <fo:table-cell text-align="center">
							       <fo:block></fo:block>
						        </fo:table-cell>
		                    </fo:table-row>
			            </#if>
		            </#if>
		        </#list>
		        <#list orderHeaderAdjustments as orderHeaderAdjustment>
		            <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
		            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
		            <#if orderHeaderAdjustment.orderItemSeqId?has_content && orderHeaderAdjustment.orderItemSeqId == "_NA_">
			            <#assign adjustmentAmountInDelivery = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].calcOrderAdjustmentInDelivery(delegator, orderHeaderAdjustment, deliveryId)>
			            <#if adjustmentAmountInDelivery != 0>
			            	<#assign grandTotalByDelivery = grandTotalByDelivery + adjustmentAmountInDelivery>
			            	<fo:table-row border="solid 0.2mm black">
		                        <fo:table-cell text-align="left" number-columns-spanned="5">
			            			<fo:block line-height="20px" margin-left="5px"> 
			            			<#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}</#if>
			                        <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}</#if>
		                        </fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell text-align="right">
			                        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
				                        <fo:block line-height="20px" margin-right="2px"> 
					                    	<#if (adjustmentAmount &lt; 0)>
					                    		<#assign adjustmentAmountNegative = -adjustmentAmountInDelivery>
					                    		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmountNegative, "#,##0", locale)}
					                        <#else>
					                    		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmountInDelivery, "#,##0", locale)}
					                        </#if>
				                        </fo:block>
					        		<#else>
						        		<fo:block line-height="20px" margin-right="2px"> 	
					        			</fo:block>
					        		</#if>
		                        </fo:table-cell>
			 			       <fo:table-cell  text-align="center">
							       <fo:block></fo:block>
						        </fo:table-cell>
		                    </fo:table-row>
			            </#if>
		            </#if>
		        </#list>
			    <fo:table-row border="solid 0.2mm black">
			    	 <fo:table-cell text-align="left" number-columns-spanned="5">
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.TotalAmountPay}: </fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
					        <fo:block line-height="20px" margin-right="2px"> 	
					        	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery, "#,##0", locale)}
				        	</fo:block> 
			    		<#else>
			        		<fo:block line-height="20px" margin-right="2px"> 	
			    			</fo:block>
			    		</#if>
			        </fo:table-cell>
			       <fo:table-cell text-align="center">
				       <fo:block></fo:block>
			        </fo:table-cell>
			    </fo:table-row>
			    <fo:table-row border="solid 0.2mm black">
			    		<fo:table-cell text-align="left" number-columns-spanned="7">
				        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
					        <#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery, "#,##0", locale)>
					        <#if locale == "vi">
				        		<#assign stringTotal = abc.replaceAll("\\.", "")>
				        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(stringTotal?number)>
				        		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.ByString}: ${totalDlvString?if_exists}</fo:block>
				        	<#elseif locale == "en">
				        		<#assign stringTotal = abc.replaceAll("\\,", "")>
				        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].changeToWords(stringTotal, true)>
				        		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.ByString}: ${totalDlvString?if_exists}</fo:block>
				        	</#if>
				        <#else>
			        		<fo:block line-height="20px" margin-right="2px"> 	
			    			</fo:block>
			    		</#if>
			        </fo:table-cell>
			    </fo:table-row>
		    </#if>
		</fo:table-body>
	</fo:table>
	<#if displayTotal == true>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
			<fo:table-column column-width="100pt"/>
			<fo:table-column column-width="300pt"/>
			<fo:table-column column-width="90pt"/>
			<fo:table-column column-width="100pt"/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
		 		<fo:table-cell>
		 			<fo:block>${uiLabelMap.ProductPromotion}:</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>
						<#if discountAmountTotal?has_content && discountAmountTotal &gt; 0 > 
							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(discountAmountTotal, "#,##0", locale)}
						</#if>
					</fo:block>
				</fo:table-cell>
				<#assign checkShipCharges = false>
				<#list orderHeaderAdjustments as x>
					<#if x.orderAdjustmentTypeId?has_content && x.orderAdjustmentTypeId== "SHIPPING_CHARGES">
						<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(x.amount.abs()?if_exists, orderHeader.currencyUom?if_exists?default('VND'), locale)>
						<#assign checkShipCharges = true>
					</#if>
				</#list>
				<#if checkShipCharges == true>
					<fo:table-cell>
						<fo:block>${uiLabelMap.TransportCost}:</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block font-weight="bold" text-align="left" font-size="12" margin-top="-2px">
							${amount?if_exists}
						</fo:block>
					</fo:table-cell>
				<#else>
					<fo:table-cell>
						<fo:block>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block>
						</fo:block>
					</fo:table-cell>
				</#if>
			 </fo:table-body>
		</fo:table>
		<fo:block font-weight="bold"><fo:inline>${uiLabelMap.PromotionUpperCase}: </fo:inline> <fo:inline font-weight="bold"></fo:inline></fo:block>
		
		<#if orderAdjustmentsPromoWithTax?exists && orderAdjustmentsPromoWithTax?has_content>
			<#list orderAdjustmentsPromoWithTax as objAdj>
				<#if objAdj.amount &lt; 0>
					<#assign stringTotalNagative = - objAdj.amount>
					<fo:block margin-top="3px"> - ${objAdj.promoName}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(stringTotalNagative, "#,##0", locale)}</fo:block>
				<#else>
					<fo:block margin-top="3px"> - ${objAdj.promoName}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(objAdj.amount, "#,##0", locale)}</fo:block>
				</#if>
			</#list>
		</#if>
		<#assign labelPm = uiLabelMap.PaymentMethod>
		<fo:block margin-top="6px"><fo:inline font-weight="bold">${labelPm?upper_case}: </fo:inline> <fo:inline>${paymentMethodDescription?if_exists?upper_case}</fo:inline></fo:block>
		<fo:block padding="6pt">
			<fo:inline font-weight="bold" font-size="100%">${uiLabelMap.DeliveryUpperCase}: </fo:inline> 
		</fo:block>
		<fo:block padding="3pt">${uiLabelMap.ShippingAddress}: 
			<fo:inline font-style="italic">
				<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
				<#list orderContactMechValueMaps as orderContactMechValueMap>
			      	<#assign contactMech = orderContactMechValueMap.contactMech>
			      	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
			      	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
			            <#assign postalAddress = orderContactMechValueMap.postalAddress>
			            <#if postalAddress?has_content>
					    	<#if postalAddress.toName?has_content>${postalAddress.toName}<#if postalAddress.attnName?has_content> (${postalAddress.attnName})</#if>.</#if>
				            <#if postalAddress.address1?has_content> ${postalAddress.address1}, </#if>
				            <#if postalAddress.address2?has_content> ${postalAddress.address2}, </#if>
				            <#if postalAddress.wardGeoId?has_content>
				            	<#if "_NA_" == postalAddress.wardGeoId>
					            	 ___, 
					           	<#else>
					           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)!/>
					            	 ${wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId)}, 
								</#if>
							</#if>
				            <#if postalAddress.districtGeoId?has_content>
				            	<#if "_NA_" == postalAddress.districtGeoId>
					            	 ___, 
					           	<#else>
					            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)!/>
					            	 ${districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId)}, 
					           	</#if>
							</#if>
				            <#if postalAddress.city?has_content> ${postalAddress.city}, </#if>
				            <#if postalAddress.countryGeoId?has_content>
						      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
						      	${country.get("geoName", locale)?default(country.geoId)}
					    	</#if>
			            </#if>
			        </#if>
			        <#break>
			    </#list>
			</fo:inline>
		</fo:block>
		<#if shipAfterDate?exists && shipBeforeDate?exists>
			<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!} ${uiLabelMap.LogTo} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
		<#else>
			<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!} ${uiLabelMap.LogTo} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
		</#if>
		<fo:block padding="3pt"><fo:inline> ${StringUtil.wrapString(uiLabelMap.Notes)}: </fo:inline><fo:inline> ${shippingInstructions?if_exists}</fo:inline></fo:block>
	</#if>
</fo:block>