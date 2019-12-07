<#escape x as x?xml>
<fo:block font-size="11" font-family="Times New Roman">
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	 	<fo:table-column/>
 	<fo:table-body>
	    	<fo:table-row>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" font-size="150%">${uiLabelMap.TransferNote}</fo:block>
		        </fo:table-cell>
	      	</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 <fo:table table-layout="fixed" space-after.optimum="10pt" margin-left="50px">
	 	<fo:table-column/>
	 	<fo:table-column/>
	    <fo:table-body>
		    <fo:table-row >
			   <fo:table-cell width="100px" text-align="left">
			       <fo:block font-style="italic" font-weight="bold">${uiLabelMap.FacilityFrom}:</fo:block>
			       <fo:block font-style="italic" font-weight="bold">${uiLabelMap.FacilityTo}:</fo:block>
			   </fo:table-cell>
			   <fo:table-cell >
			       <fo:block>${originFacility?if_exists.facilityName?if_exists} <fo:leader /> </fo:block>
			       <fo:block>${destFacility?if_exists.facilityName?if_exists} <fo:leader /> </fo:block>
			   </fo:table-cell>
		    </fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.3mm black">
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		<fo:table-column border="solid 0.3mm black"/>
		
		<fo:table-header border="solid 0.3mm black">
	       <fo:table-row border="solid 0.3mm black">
	           <fo:table-cell text-align="center" width="20px">
	           		<fo:block font-weight="bold" margin-bottom="10px" >${uiLabelMap.SequenceId}</fo:block>
	           </fo:table-cell>
	           <fo:table-cell text-align="center">
	          		<fo:block font-weight="bold">${uiLabelMap.accProductId}</fo:block>
	          </fo:table-cell>
	           <fo:table-cell text-align="center">
	           		<fo:block font-weight="bold">${uiLabelMap.ProductName}</fo:block>
	           </fo:table-cell>
	           <fo:table-cell text-align="center">
	           		<fo:block font-weight="bold" vertical-align="middle">${uiLabelMap.ExportedQuantity}</fo:block>
	           </fo:table-cell>
	           <fo:table-cell text-align="center">
	           		<fo:block font-weight="bold" vertical-align="middle">${uiLabelMap.ReceiptQuantity}</fo:block>
	           </fo:table-cell>
	           <fo:table-cell text-align="center">
	         		<fo:block font-weight="bold" vertical-align="middle">${uiLabelMap.Unit}</fo:block>
	     	  </fo:table-cell>
	           <fo:table-cell text-align="center">
	           		<fo:block font-weight="bold">${uiLabelMap.ExpireDate}</fo:block>
	           </fo:table-cell>
	       </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#list listItems as item >
			    <fo:table-row border="solid 0.3mm black">
				    <fo:table-cell >
				       <fo:block text-align="center">${item_index + 1}</fo:block>
				    </fo:table-cell>
				  	<fo:table-cell  text-align="center">
				       <fo:block>${item.productId?if_exists}</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell  text-align="center">
				       <fo:block>${delegator.findOne('Product', false, Static["org.ofbiz.base.util.UtilMisc"].toMap('productId', item.productId)).internalName}</fo:block>
				   	</fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${item.actualExportedQuantity?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
			       		<fo:block>${item.actualDeliveredQuantity?if_exists}</fo:block>
			       </fo:table-cell>
			       <fo:table-cell  text-align="center">
				       <fo:block>${delegator.findOne('Uom', false, Static["org.ofbiz.base.util.UtilMisc"].toMap('uomId', item.quantityUomId)).description}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="center">
				       <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(item.expireDate?if_exists, "dd/MM/yyyy", locale, timeZone)!}</fo:block>
				   </fo:table-cell>
			    </fo:table-row>
		    </#list>
		</fo:table-body>
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt">
		<fo:table-column/>
		<fo:table-column/>
		 <fo:table-body>
			 <fo:table-row >
				<fo:table-cell text-align="center">
				</fo:table-cell>
				<fo:table-cell text-align="center">
				    <fo:block>${uiLabelMap.CommonDay} ........ ${uiLabelMap.CommonMonth}........ ${uiLabelMap.CommonYear} ........</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell text-align="center">
				    <fo:block font-weight="bold">${uiLabelMap.StorekeeperDelivery}</fo:block>
				</fo:table-cell>
				<fo:table-cell text-align="center">
				    <fo:block font-weight="bold">${uiLabelMap.StorekeeperReceive}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell text-align="center">> 
				    <fo:block>(${uiLabelMap.Sign})</fo:block>
				</fo:table-cell>
				<fo:table-cell text-align="center">>
				    <fo:block>(${uiLabelMap.Sign})</fo:block>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
</fo:block>
</#escape>