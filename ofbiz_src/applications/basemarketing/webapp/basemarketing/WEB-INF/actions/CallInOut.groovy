import com.olbius.dms.util.CommonUtil;

int age = 60 * 60 * 24 * 365;
CommonUtil.setCookie(response, "sidebar", "true", age);