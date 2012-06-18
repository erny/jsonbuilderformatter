package es.tangrambpm.wso2esb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.wsdl.extensions.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JSONMessageFormatter extends
		org.apache.axis2.json.JSONMessageFormatter {

	static final Log log = LogFactory.getLog(JSONMessageFormatter.class);
	static String prefix = JSONOMBuilder.PREFIX;
	static String postfix = JSONOMBuilder.POSTFIX;
	static byte[] prefixbytes = JSONOMBuilder.PREFIX.getBytes();
	static byte[] postfixbytes = JSONOMBuilder.POSTFIX.getBytes();
	
	
	// Filter the string
	private String filterPrefix(String str)
	{
    	if (str != null){
    		if (str.startsWith(prefix)) {
    			str = str.substring(prefix.length(), str.length()-postfix.length());
    		}
    	}
    	return str;
	}
	
	private byte[] filterPrefix(byte[] bytes){
    	if (startsWith(bytes, prefixbytes)) {
    		bytes = Arrays.copyOfRange(bytes, prefixbytes.length , bytes.length - postfixbytes.length);
    	}
    	return bytes;
	}
	
	@Override
    public String getStringToWrite(OMDataSource dataSource) {
		return filterPrefix(super.getStringToWrite(dataSource));
    }

    @Override
    public byte[] getBytes(MessageContext msgCtxt, OMOutputFormat format) throws AxisFault {
    	return filterPrefix(super.getBytes(msgCtxt, format));
    }

    public static boolean startsWith(byte[] source, byte[] match){
    	for (int i = 0; i < match.length; i++) {
    		if (source[i] != match[i]) {
	        return false;
	      }
	    }
	    return true;
    }
    
    private XMLStreamWriter getJSONWriter(OutputStream outStream, OMOutputFormat format)
            throws AxisFault {
        try {
            return getJSONWriter(new OutputStreamWriter(outStream, format.getCharSetEncoding()));
        } catch (UnsupportedEncodingException ex) {
            throw AxisFault.makeFault(ex);
        }
    }
    @Override
    public void writeTo(MessageContext msgCtxt, OMOutputFormat format,
            OutputStream out, boolean preserve) throws AxisFault {
    	
		OMElement element = msgCtxt.getEnvelope().getBody().getFirstElement();
		try {
		//Mapped format cannot handle element with namespaces.. So cannot handle Faults
			if (element instanceof SOAPFault) {
			    SOAPFault fault = (SOAPFault)element;
			    OMElement element2 = new OMElementImpl("Fault", null, element.getOMFactory());
			    element2.setText(fault.toString());
			    element = element2;
			}
			if (element instanceof OMSourcedElementImpl &&
			        getStringToWrite(((OMSourcedElementImpl)element).getDataSource()) != null) {
			    String jsonToWrite =
			            getStringToWrite(((OMSourcedElementImpl)element).getDataSource());
			
			    out.write(jsonToWrite.getBytes());
			} else {
				// Changed for filtering output. Could be more efficient.
				ByteArrayOutputStream myout = new ByteArrayOutputStream();
			    XMLStreamWriter jsonWriter = getJSONWriter(myout, format);
			    element.serializeAndConsume(jsonWriter);
			    jsonWriter.writeEndDocument();
			    myout.flush();
			    out.write(filterPrefix(myout.toByteArray()));
			}
		} catch (IOException e) {
			throw AxisFault.makeFault(e);
		} catch (XMLStreamException e) {
			throw AxisFault.makeFault(e);
		} catch (IllegalStateException e) {
			throw new AxisFault(
		        "Mapped formatted JSON with namespaces are not supported in Axis2. " +
		                "Make sure that your request doesn't include namespaces or " +
		                "use the Badgerfish convention");
		}
	}
}