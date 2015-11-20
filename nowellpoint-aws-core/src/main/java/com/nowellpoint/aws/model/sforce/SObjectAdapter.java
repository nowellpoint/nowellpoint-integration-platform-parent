package com.nowellpoint.aws.model.sforce;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.w3c.dom.Element;

public class SObjectAdapter extends XmlAdapter<Object, SObject> {

	@Override
	public SObject unmarshal(Object value) throws Exception {
		if (Element.class.isAssignableFrom(value.getClass())) {
			String type = ((Element) value).getAttribute("xsi:type").replaceFirst("sf:", "");
			String id = ((Element) value).getElementsByTagName("sf:Id").item(0).getTextContent();
			SObject sobject = new SObject();
			sobject.setType(type);
			sobject.setId(id);
			return sobject;
		} else {
			return null;
		}
	}

	@Override
	public Element marshal(SObject value) throws Exception {
		return null;
	}
}