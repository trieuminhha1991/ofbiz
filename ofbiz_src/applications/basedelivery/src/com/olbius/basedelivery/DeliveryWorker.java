package com.olbius.basedelivery;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by user on 12/1/17.
 */
public class DeliveryWorker {
    public static final String STATUS_PROPOSED = "DLV_PROPOSED";

    public static Boolean updateDeliveryStatus(Delegator delegator, List<String> listDeliveryId) {
        try {
            List<GenericValue> listDelivery = FastList.newInstance();
            String[] deliveryIds = listDeliveryId.get(0).split(",");
            for (String deliveryId : deliveryIds) {
                String originDeliveryId = deliveryId.replace("\"", "");
                GenericValue delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId", originDeliveryId), false);
                delivery.set("statusId", STATUS_PROPOSED);
                listDelivery.add(delivery);
            }
            delegator.storeAll(listDelivery);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getPricePetrolium() throws IOException {
        String url = "https://laythongtin.net/mini-content/petroleum.php?type=json";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
        return response.toString();
    }

    private static MultiValueMap<String, String> generateHeader() throws IOException {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("User-Agent", "JVM");
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Accept-Encoding", "gzip;q=1.0,deflate;q=0.6,identity;q=0.3");
        headers.add("Accept", "*/*");
        return headers;
    }
}
