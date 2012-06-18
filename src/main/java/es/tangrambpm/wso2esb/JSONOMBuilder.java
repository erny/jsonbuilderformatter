package es.tangrambpm.wso2esb;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.io.IOUtils;

public class JSONOMBuilder extends org.apache.axis2.json.JSONOMBuilder {
	String encoding = "UTF-8";
    public static String ROOT1 = "_root";
    public static String ROOT2 = "_root2";
	public static String PREFIX = "{\"" + ROOT1 + "\":{\"" + ROOT2 + "\":";
	public static String POSTFIX = "}}";

	@Override
	public OMElement processDocument(InputStream inputStream, String contentType,
            MessageContext messageContext) throws AxisFault {
    	// Read string
    	StringWriter writer = new StringWriter();
    	try {
			IOUtils.copy(inputStream, writer, encoding);
	    	String str = writer.toString();
	    	str = PREFIX + str + POSTFIX;
            InputStream is = new ByteArrayInputStream(str.getBytes(encoding));
        	return super.processDocument(is, contentType, messageContext);
    	} catch (Exception e) {
            throw new AxisFault(e.toString());
		}
    }
}
