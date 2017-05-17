package com.zju.campustour.presenter.handler;

import com.zju.campustour.model.database.data.MajorClass;
import com.zju.campustour.model.database.data.MajorModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class MajorXmlParserHandler extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 */
	private List<MajorClass> provinceList = new ArrayList<MajorClass>();

	public MajorXmlParserHandler() {

	}

	public List<MajorClass> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	MajorClass majorClass = new MajorClass();
	MajorModel majorModel = new MajorModel();

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("class")) {
			majorClass = new MajorClass();
			majorClass.setName(attributes.getValue(0));
			majorClass.setMajorList(new ArrayList<MajorModel>());
		} else if (qName.equals("major")) {
			majorModel = new MajorModel();
			majorModel.setName(attributes.getValue(0));
			majorModel.setTag(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("major")) {
			majorClass.getMajorList().add(majorModel);
		} else if (qName.equals("class")) {
			provinceList.add(majorClass);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
