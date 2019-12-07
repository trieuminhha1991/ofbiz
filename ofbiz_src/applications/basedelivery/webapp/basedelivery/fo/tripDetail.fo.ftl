<#escape x as x?xml>
<fo:block>

    <fo:block text-align="center" margin-bottom="0.5cm">
        <fo:block text-transform="uppercase" font-size="18pt" font-weight="700">
        ${uiLabelMap.BDTripDetail}
        </fo:block>
    <#--
    <fo:block font-style="italic" font-size="10pt">
        ${uiLabelMap.BSDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(nowTimestamp, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
    </fo:block>
    <fo:block font-style="italic" font-size="10pt">
        ${uiLabelMap.BSId}: ${orderHeader.orderId?if_exists}
    </fo:block>-->
    </fo:block>

    <fo:block>
        <fo:table border-color="black" border-style="solid" border-width="0">
            <fo:table-column column-width="1.5cm"/>
            <fo:table-column column-width="1.3cm"/>
            <fo:table-column column-width="0.5cm"/>
            <fo:table-column column-width="0.8cm"/>
            <fo:table-column column-width="5cm"/>
            <fo:table-column column-width="3.3cm"/>
            <fo:table-column column-width="0.5cm"/>
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell padding="1mm" number-columns-spanned="2">
                        <fo:block padding-bottom="1mm">${uiLabelMap.BDTripId}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="3">
                        <fo:block padding-bottom="1mm">${trip.tripId}</fo:block>
                    </fo:table-cell>

                    <fo:table-cell padding="1mm">
                        <fo:block>${uiLabelMap.BDTripStartDate}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="2">
                        <fo:block>
                            <#if trip.tripStartDate?exists>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(trip.tripStartDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
							</#if>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <fo:table-cell padding="1mm" number-columns-spanned="3">
                        <fo:block padding-bottom="1mm">${uiLabelMap.BDTotalWeight}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="2">
                        <fo:block padding-bottom="1mm">${trip.totalWeight?if_exists} kg</fo:block>
                    </fo:table-cell>

                    <fo:table-cell padding="1mm">
                        <fo:block>${uiLabelMap.BDVehicleId}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="1mm" font-weight="700"  number-columns-spanned="2">
                        <fo:block>
                            ${trip.vehicleName?if_exists}
                            <#--<#if trip.vehicleId?exists>-->
								<#--${Static["com.olbius.basedelivery.vehicle.VehicleHelper"].getVehicleName(delegator, trip.vehicleId?if_exists)?if_exists}-->
							<#--</#if>-->
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row>
                    <#--<fo:table-cell padding="1mm" number-columns-spanned="2">-->
                        <#--<fo:block padding-bottom="1mm">${uiLabelMap.BDDriverId}:</fo:block>-->
                    <#--</fo:table-cell>-->
                    <#--<fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="3">-->
                        <#--<fo:block>-->
                            <#--<#if trip.driverId?exists>-->
								<#--${Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, trip.driverId?if_exists, true, true)?if_exists}-->
							<#--</#if>-->
                        <#--</fo:block>-->
                    <#--</fo:table-cell>-->

                    <fo:table-cell padding="1mm" number-columns-spanned="2">
                        <fo:block>${uiLabelMap.BDContractorId}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="4">
                        <fo:block>
                            <#if trip.contractorId?exists>
								${Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, trip.contractorId?if_exists, true, true)?if_exists}
							</#if>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>

            </fo:table-body>
        </fo:table>
    </fo:block>

    <fo:block>
        <fo:table border-color="black" border-style="solid" border-width="1pt">
            <fo:table-column column-width="2cm"/>
            <fo:table-column column-width="2.3cm"/>
            <fo:table-column/>
            <fo:table-column column-width="2.3cm"/>
            <fo:table-column column-width="2.3cm"/>
            <fo:table-header>
                <fo:table-row border-color="black" background-color="#DDDDDD">
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BDSTT}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BDStoreName}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BDAddress}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BDPhone}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center"
                                   border="1pt solid black">
                        <fo:block font-weight="bold">${uiLabelMap.BDTotalWeight}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-header>
            <fo:table-body>
                <#list listDelivery as itemLine>
                    <#assign address = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId": itemLine.destContactMechId}, false).fullName?if_exists>
                    <#assign sendFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, itemLine.partyIdTo, false)?if_exists/>
                    <#assign listParty = delegator.findList("PartyAndTelecomNumber", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", itemLine.partyIdFrom?if_exists)), null, null, null, false) />
                    <#assign totalWeight = Static["com.olbius.basedelivery.trip.TripWorker"].getWeightInDelivery(delegator, dispatcher, itemLine.deliveryId)?if_exists/>
                    <#if listParty?has_content && listParty?length &gt; 0>
                        <#assign phone = listParty[0].contactNumber?if_exists>
                    </#if>
                    <fo:table-row>
                        <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
                            <fo:block>${itemLine_index + 1}</fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if itemLine.partyIdFrom?exists>
                               ${sendFrom }
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if address?exists>
                                    ${address}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if phone?exists>
                                    ${phone}
                                    </#if>
                            </fo:block>
                        </fo:table-cell>

                        <fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
                            <fo:block>
                                <#if totalWeight?exists>
                                    ${totalWeight} kg
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