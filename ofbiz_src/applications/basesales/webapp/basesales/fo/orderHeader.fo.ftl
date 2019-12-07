<#escape x as x?xml>
<fo:block>
<#if orderHeader?exists>
	<fo:block>
		<fo:table border-color="black" border-style="solid" border-width="0">
			<fo:table-column/>
			<fo:table-column column-width="2cm"/>
			<fo:table-column column-width="3.2cm"/>
		    <fo:table-body>
		    	<fo:table-row>
		    		<fo:table-cell padding="1mm">
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" text-align="right" text-transform="uppercase" font-weight="bold">
		                <fo:block>${uiLabelMap.BSId}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" font-weight="bold">
		                <fo:block>${orderHeader.orderId?if_exists}</fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		    	<fo:table-row>
		    		<fo:table-cell padding="1mm">
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" text-align="right" text-transform="uppercase" font-weight="bold">
		                <fo:block>${uiLabelMap.BSDate}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" font-weight="bold">
		                <fo:block>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(nowTimestamp, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}</fo:block>
		            </fo:table-cell>
		        </fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>
</#if>
</fo:block>
</#escape>