/*
* LEGAL NOTICE
* This computer software was prepared by US EPA.
* THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* EXPORT CONTROL
* User agrees that the Software will not be shipped, transferred or
* exported into any country or used in any manner prohibited by the
* United States Export Administration Act or any other applicable
* export laws, restrictions or regulations (collectively the "Export Laws").
* Export of the Software may require some form of license or other
* authority from the U.S. Government, and failure to obtain such
* export control license may result in criminal liability under
* U.S. laws. In addition, if the Software is identified as export controlled
* items under the Export Laws, User represents and warrants that User
* is not a citizen, or otherwise located within, an embargoed nation
* (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
*     and that User is not otherwise prohibited
* under the Export Laws from receiving the Software.
*
* SUPPORT
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
* Parks and Yadong Xu of ARA through the EPA�s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package glimpseBuilder;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLModifier {

	Document xmlDocument = null;

	public static Document openXmlDocument(String filename) {
		File xmlFile = new File(filename);
		return openXmlDocument(xmlFile);
	}
	
	public static Document openXmlDocument(File xmlFile) {
		//File xmlFile = new File(filename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			System.out.println("Error opening xml document: " + e);
		}

		return doc;
	}

	public static void writeXmlDocument(Document doc, String filename) {
		try {
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filename));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("XML file updated successfully"); System.out.println("---");
		} catch (Exception e) {
			System.out.println("Error updating XML file: " + e);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filePath = "C:\\projects\\GCAM-GUI\\test\\configuration_GCAM_4p2_Original.xml";
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			// add new element
			addElement(doc, "String", "Value", "newElement", "barbasol shaving cream");

			// update Element value
			updateElementValue(doc, "String", "Value", "newElement", "barbasol shaving creams");

			// delete element
			deleteElement(doc, "String", "Value", "MAGICC-input-dir");

			// write the updated document to file or console
			doc.getDocumentElement().normalize();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					"c:\\projects\\GCAM-GUI\\test\\configuration_GCAM_4p2_Updated.xml"));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("XML file updated successfully"); System.out.println("---");

		} catch (SAXException | ParserConfigurationException | IOException | TransformerException e1) {
			e1.printStackTrace();
		}
	}

	public static void addElement(Document doc, String topNodeStr, String subNodeStr, String attributeNameStr,
			String attributeValStr) {
		NodeList nodeList = doc.getElementsByTagName(topNodeStr);
		Element node = null;
		// loop for each employee
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = (Element) nodeList.item(i);
			Element newElement = doc.createElement(subNodeStr);
			newElement.setAttribute("name", attributeNameStr);
			newElement.appendChild(doc.createTextNode(attributeValStr));
			node.appendChild(newElement);
		}
	}

	public static void deleteElement(Document doc, String topNodeStr, String subNodeStr, String attributeValStr) {
		NodeList nodeList = doc.getElementsByTagName(topNodeStr);
		NodeList subNodeList;
		Element node = null;
		Element subNode = null;
		// loop for each employee
		for (int i = 0; i < nodeList.getLength(); i++) {
			node = (Element) nodeList.item(i);
			subNodeList = node.getElementsByTagName(subNodeStr);
			for (int j = 0; j < subNodeList.getLength(); j++) {
				subNode = (Element) subNodeList.item(j);
				String attributeName = subNode.getAttribute("name");
				// System.out.println(".... subnodename " + subNodeName +
				// " attributename " + attributeName);
				if (attributeName.equals(attributeValStr)) {
					// System.out.println("   ....match");
					node.removeChild(subNode);
					break;
				}
			}
		}

	}

	public static void updateElementValue(Document doc, String topNodeStr, String subNodeStr, String attributeNameStr,
			String attributeValStr) {
		NodeList nodeList = doc.getElementsByTagName(topNodeStr);
		NodeList subNodeList;
		Element node = null;
		Element subNode = null;
		// loop for each employee
		for (int i = 0; i < nodeList.getLength(); i++) {

			node = (Element) nodeList.item(i);
			// System.out.println("i " + i + " " + node.getNodeName() + " ");
			subNodeList = node.getElementsByTagName(subNodeStr);

			for (int j = 0; j < subNodeList.getLength(); j++) {
				subNode = (Element) subNodeList.item(j);
				String attributeName = subNode.getAttribute("name");
				String attributeValue = subNode.getTextContent();

				// System.out.println("  --> j " + j + " " + topNodeName + " " +
				// subNodeName + " " + attributeName + " "
				// + attributeValue);
				if (attributeName.equals(attributeNameStr)) {
					attributeValue = attributeValue.toUpperCase();
					subNode.setTextContent(attributeValStr);
				}
			}

		}

	}

	public static void updateAttributeValue(Document doc, String topNodeStr, String subNodeStr, String nameStr, String attributeNameStr,
			String attributeValStr) {
		NodeList nodeList = doc.getElementsByTagName(topNodeStr);
		NodeList subNodeList;
		Element node = null;
		Element subNode = null;
		// loop for each employee
		
		System.out.println("tried to set :"+attributeNameStr+": to :"+attributeValStr+":-> top:"+topNodeStr+" sub:"+subNodeStr+" ValueName:"+nameStr+": attr:"+attributeNameStr); 
		
		for (int i = 0; i < nodeList.getLength(); i++) {

			node = (Element) nodeList.item(i);
			// System.out.println("i " + i + " " + node.getNodeName() + " ");
			subNodeList = node.getElementsByTagName(subNodeStr);

			for (int j = 0; j < subNodeList.getLength(); j++) {
				subNode = (Element) subNodeList.item(j);
				String topNodeName = node.getTagName();
				String subNodeName = subNode.getTagName();
				String valueName = subNode.getAttribute("name");
				if (topNodeName.equals(topNodeStr)) {
					if (subNodeName.equals(subNodeStr)){
						if (valueName.equals(nameStr)) {
							subNode.setAttribute(attributeNameStr,attributeValStr);
						}
					}
				}

			}

		}

	}
	
	
}
