package com.yy.cs.base.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;


/**
 * 
 */
public final class XmlUtils {

	private static final Logger logger = LoggerFactory
	.getLogger(XmlUtils.class);
	
	private XmlUtils() {
	}
	
	/** 
	 * 根据文件名获取RootElement
	 * @param fileName	文件名
	 * 		xml文档的文件名
	 * @return Element   
	 * 		xml文档的根元素
	 */
	public static Element getRootElement(String fileName) {
		if (fileName == null || fileName.length() == 0)
			return null;
		try {
			Element rootElement = null;
			FileInputStream fis = new FileInputStream(fileName);
			rootElement = getRootElement(((InputStream) (fis)));
			fis.close();
			return rootElement;
		} catch (Exception e) {
			return null;
		}
	}
	
	/** 
	 * 根据InputStream获取RootElement
	 * @param is	InputStream
	 * 		xml文档的输入流
	 * @return Element   
	 * 		xml文档的根元素
	 */
	public static Element getRootElement(InputStream is) {
		if (is == null)
			return null;
		Element rootElement = null;
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = db.parse(is);
			rootElement = doc.getDocumentElement();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return rootElement;
	}
	
	/** 
	 * 根据InputSource获取RootElement
	 * @param is	InputSource
	 * 		xml输入流
	 * @return    rootElement
	 * 		xml文档的根元素
	 */
	public static Element getRootElement(InputSource is) {
		if (is == null)
			return null;
		Element rootElement = null;
		try {
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document doc = db.parse(is);
			rootElement = doc.getDocumentElement();
		} catch (Exception e) {
			logger.error(e.toString());
		}
		return rootElement;
	}
	
	/** 
	 * 根据父Element获取其子Elements信息
	 * @param element
	 * 			父级节点元素
	 * @return Element[]  
	 * 			子节点元素数组 
	 */
	public static Element[] getChildElements(Element element) {
		if (element == null)
			return null;
		Vector<Element> childs = new Vector<Element>();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling())
			if (node instanceof Element)
				childs.add((Element) node);

		Element elmt[] = new Element[childs.size()];
		childs.toArray(elmt);
		return elmt;
	}
	
	/** 
	 * 根据父Element和名称获取子Element信息
	 * @param element 
	 * 		父级元素
	 * @param childName	
	 * 		子节点名陈3
	 * @return Element[]
	 * 		子节点数组   
	 */
	public static Element[] getChildElements(Element element,
			String childName) {
		if (element == null || childName == null || childName.length() == 0)
			return null;
		Vector<Element> childs = new Vector<Element>();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling())
			if ((node instanceof Element)
					&& node.getNodeName().equals(childName))
				childs.add((Element) node);

		Element elmt[] = new Element[childs.size()];
		childs.toArray(elmt);
		return elmt;
	}
	
	/** 
	 * 根据父Node获取子Nodes信息
	 * @param  node	
	 * 		父Node
	 * @return Node[]  
	 * 		子节点数组 
	 */
	public static Node[] getChildNodes(Node node) {
		if (node == null)
			return null;
		Vector<Element> childs = new Vector<Element>();
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling())
			childs.add((Element) n);

		Node childNodes[] = new Element[childs.size()];
		childs.toArray(childNodes);
		return childNodes;
	}
	
	/** 
	 * 根据父Element和名称获取子Element信息
	 * @param  element	
	 * 		父Element
	 * @param childName	
	 * 		子名称
	 * @return Element   
	 */
	public static Element getChildElement(Element element,
			String childName) {
		if (element == null || childName == null || childName.length() == 0)
			return null;
		Element childs = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (!(node instanceof Element)
					|| !node.getNodeName().equals(childName))
				continue;
			childs = (Element) node;
			break;
		}

		return childs;
	}
	
	/** 
	 * 根据父Element获取子Element信息
	 * @param  element	
	 * 		父Element
	 * @return 
	 * 		childElement   
	 */
	public static Element getChildElement(Element element) {
		if (element == null)
			return null;
		Element childs = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (!(node instanceof Element))
				continue;
			childs = (Element) node;
			break;
		}

		return childs;
	}
	
	/** 
	 * 根据Element获取Element Values信息
	 * @param 	element
	 * 		节点元素
	 * @return String[]  
	 * 		Element Values信息  
	 */
	public static String[] getElenentValues(Element element) {
		if (element == null)
			return null;
		Vector<String> childs = new Vector<String>();
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling())
			if (node instanceof Text)
				childs.add(node.getNodeValue());

		String values[] = new String[childs.size()];
		childs.toArray(values);
		return values;
	}
	
	/** 
	 * 根据Element获取Element Value信息
	 * @param 	element 
	 * 		节点元素
	 * @return 
	 * 		String  Element Value信息  
	 */
	public static String getElenentValue(Element element) {
		if (element == null)
			return null;
		String retnStr = null;
		for (Node node = element.getFirstChild(); node != null; node = node
				.getNextSibling()) {
			if (!(node instanceof Text))
				continue;
			String str = node.getNodeValue();
			if (str == null || str.length() == 0 || str.trim().length() == 0)
				continue;
			retnStr = str;
			break;
		}

		return retnStr;
	}
	
	/** 
	 * 根据Element和名字获取子Element信息
	 * @param e
	 * 		节点元素
	 * @param name	
	 * 		节点名字
	 * @return Element  
	 * 		返回子节点Element元素  
	 */
	public static Element findElementByName(Element e, String name) {
		if (e == null || name == null || name.length() == 0)
			return null;
		String nodename = null;
		Element childs[] = getChildElements(e);
		for (int i = 0; i < childs.length; i++) {
			nodename = childs[i].getNodeName();
			if (name.equals(nodename))
				return childs[i];
		}

		for (int i = 0; i < childs.length; i++) {
			Element retn = findElementByName(childs[i], name);
			if (retn != null)
				return retn;
		}

		return null;
	}
	
	/** 
	 * 根据Element、属性名、属性值获取子Element信息
	 * @param e 
	 * 		指定的Element
	 * @param attrName	
	 * 		属性名
	 * @param attrVal	
	 * 		属性值
	 * @return Element  
	 * 		返回Element  
	 */
	public static Element findElementByAttr(Element e,
			String attrName, String attrVal) {
		return findElementByAttr(e, attrName, attrVal, true);
	}
	/**
	 * 根据Element、属性名、属性值获取子Element信息
	 * @param  e 
	 * 		指定的Element
	 * @param attrName 
	 * 		属性名
	 * @param attrVal 
	 * 		属性值
	 * @param dept 
	 * 		如果在子Element找不到是否在孙辈element中寻找
	 * @return
	 * 		Element 元素节点
	 */
	public static Element findElementByAttr(Element e,
			String attrName, String attrVal, boolean dept) {
		if (e == null || attrName == null || attrName.length() == 0
				|| attrVal == null || attrVal.length() == 0)
			return null;
		String tmpValue = null;
		Element childs[] = getChildElements(e);
		for (int i = 0; i < childs.length; i++) {
			tmpValue = childs[i].getAttribute(attrName);
			if (attrVal.equals(tmpValue))
				return childs[i];
		}

		if (dept) {
			for (int i = 0; i < childs.length; i++) {
				Element retn = findElementByAttr(childs[i], attrName, attrVal);
				if (retn != null)
					return retn;
			}

		}
		return null;
	}
	
	/** 
	 * 获取Element指定名的属性
	 * @param e  
	 * 			节点元素
	 * @param name	
	 * 			名称
	 * @return String	
	 * 			返回值属性的名称
	 */
	public static String getAttribute(Element e, String name) {
		return getAttribute(e, name, null);
	}
	/**
	 * 获取Element指定名的属性，元素或属性名称为空时返回指定字符串
	 * @param e 
	 * 		指定的元素
	 * @param name 
	 * 		属性名称
	 * @param defval 
	 * 		元素或属性名称为空时返回的值
	 * @return
	 * 		name 指定元素节点的属性名称
	 */
	public static String getAttribute(Element e, String name,
			String defval) {
		if (e == null || name == null || name.length() == 0)
			return defval;
		else
			return e.getAttribute(name);
	}

}

