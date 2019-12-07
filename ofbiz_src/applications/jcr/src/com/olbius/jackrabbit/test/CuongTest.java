package com.olbius.jackrabbit.test;

import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import javolution.util.FastMap;

import org.jdom.JDOMException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;



import org.ofbiz.service.ModelService;


//import com.olbius.data.services.DataResource;

import com.olbius.jackrabbit.services.JackrabbitOlbiusSearchServices;
import com.olbius.jackrabbit.services.JackrabbitOlbiusSessionServices;

public class CuongTest {
	public final static String module = CuongTest.class.getName();

	public static Map<String, Object> jackrabbitTestScaleImage(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException {
		Map<String, Object> result = FastMap.newInstance();


/*
//		LocalDispatcher dispatcher = ctx.getDispatcher();
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(new File("/home/coc/data/quai-cai.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		context.put("locale", Locale.ENGLISH);
//		Map<String, Object> context2 = FastMap.newInstance();
//		context2.put("locale", context.get("locale"));
//		context2.put("bufferedImage", bufferedImage);


		// = ScaleImage.jackrabbitScaleImageInAllSize(context2);

//		try {
//			result2 = dispatcher.runSync("jackrabbitScaleImageInAllSize", context2);
//		} catch (GenericServiceException e) {
//			throw new GenericServiceException(e);
//		}


		result = ScaleImage.jackrabbitScaleImageInAllSize(context2);


		List<BufferedImage> resultScaleImgList = (List<BufferedImage>) result2.get("resultScaleImgList");

		for(BufferedImage bufImg: resultScaleImgList) {
			try {
				ImageIO.write(bufImg, "jpg", new File("/home/coc/data2/quai-cai" + resultScaleImgList.indexOf(bufImg) + ".jpg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

*/

		return result;
	}
}

