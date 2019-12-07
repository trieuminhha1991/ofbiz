<#escape x as x?xml>
<fo:block>
    <fo:block text-align="center" margin-bottom="0.5cm">
        <fo:block text-transform="uppercase" font-size="18pt" font-weight="700">
        ${uiLabelMap.BSSalesOrder}
        </fo:block>
    </fo:block>

    <fo:block>
        <fo:table border-color="black" border-style="solid" border-width="1pt">
            <fo:table-column column-width="1cm"/>
            <fo:table-column column-width="2.3cm"/>
            <fo:table-column/>
            <fo:table-column column-width="2.3cm"/>
            <fo:table-column/>
            <fo:table-header>
                <fo:table-row border-color="black" background-color="#DDDDDD">
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BSSTT}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BSProductId}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BSProductName}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BSProdPromo}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BSUom}</fo:block>
                    </fo:table-cell>

                </fo:table-row>
            </fo:table-header>
            <fo:table-body>
                <#list listVehicles as itemLine>
                    <#assign person = itemLine.getRelatedOne("Party", false).getRelatedOne("Person")?if_exists>
                    <#assign vehicleId = itemLine.vehicleId?if_exists/>
                    <#assign description = itemLine.description?if_exists/>
                    <#assign plate = itemLine.plate?if_exists/>
                    <fo:table-row>
                        <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
                            <fo:block>${itemLine_index + 1}</fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if vehicleId?exists>
                                    ${vehicleId}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if description?exists>
                                    ${description}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if plate?exists>
                                    ${plate}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if person?exists>
                                    <#assign fullName = person.lastName + ' ' + person.middleName  +
                               ' ' + person.firstName/>
                                    ${fullName}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </#list>
            </fo:table-body>
        </fo:table>
    </fo:block>
</fo:block>
</#escape>