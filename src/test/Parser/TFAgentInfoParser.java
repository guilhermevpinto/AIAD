package test.Parser;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import test.Agents.TrafficLightAgentInfo;

public class TFAgentInfoParser {

	public static ArrayList<TrafficLightAgentInfo> parseTFAgentInfo(String filename){

		try{
			File xml = new File(filename);

			if ( xml.exists())
				System.out.println("Exists");
			else
				System.out.println("NOPE");

			return parseTFAgentInfo(xml);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static ArrayList<TrafficLightAgentInfo> parseTFAgentInfo(File file){

		try{
			Document xml;
			xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			ArrayList<TrafficLightAgentInfo> tfai = new ArrayList<TrafficLightAgentInfo>();
			parseTrafficLightInfo(xml,tfai);
			return tfai;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	private static void parseTrafficLightInfo(Document xml, ArrayList<TrafficLightAgentInfo> tfai){
		Element root = xml.getDocumentElement();
		NodeList nl = root.getElementsByTagName("TrafficLight");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				Element el = (Element)nl.item(i);
				TrafficLightAgentInfo t = getTrafficLightInfo(el);
				//add it to list
				tfai.add(t);
			}
		}
		
	}
	
	private static TrafficLightAgentInfo getTrafficLightInfo(Element e){
		String identifier = e.getAttribute("id");
		String beginState = getTextValue(e, "beginState");
		NodeList times = e.getElementsByTagName("times");
		
		ArrayList<Integer> timeStates = new ArrayList<Integer>();
		
		for ( int h = 0; h < times.getLength(); h++){
			Element temp = (Element)times.item(h);
			NodeList temp2 = temp.getElementsByTagName("timeState");
			for ( int k = 0; k < temp2.getLength(); k++){
				System.out.println(temp2.item(k).getTextContent());
				timeStates.add(new Integer(temp2.item(k).getTextContent()));
			}
			
		}	
		ArrayList<String> states = new ArrayList<String>();
		
		NodeList stat = e.getElementsByTagName("states");
		for ( int i = 0; i < stat.getLength(); i++){
			Element temp = (Element)stat.item(i);
			NodeList temp2 = temp.getElementsByTagName("state");
			for ( int j = 0; j < temp2.getLength(); j++){
				System.out.println(temp2.item(j).getTextContent());
				states.add(temp2.item(j).getTextContent());
			}
			
		}	
		for ( int ml = 0; ml < states.size(); ml++)
			System.out.println("STATES INSIDE XML" + states.get(ml));
		
		return new TrafficLightAgentInfo(identifier, timeStates, states);
	}
	
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Calls getTextValue and returns a int value
	 */
	private static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}

}
