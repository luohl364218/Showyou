package com.zju.campustour.presenter.handler;

import com.zju.campustour.model.area.CityModel;
import com.zju.campustour.model.area.DistrictModel;
import com.zju.campustour.model.area.ProvinceModel;
import com.zju.campustour.model.database.data.ProvinceWithCollegeModel;
import com.zju.campustour.model.database.data.SchoolModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class SchoolXmlParserHandler extends DefaultHandler {

	/**
	 * 存储所有的解析对象
	 */
	private List<ProvinceWithCollegeModel> provinceList = new ArrayList<ProvinceWithCollegeModel>();

	public SchoolXmlParserHandler() {

	}

	public List<ProvinceWithCollegeModel> getDataList() {
		return provinceList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	ProvinceWithCollegeModel provinceModel = new ProvinceWithCollegeModel();
	SchoolModel schoolModel = new SchoolModel();

	@Override
	public void startElement(String uri, String localName, String qName,
							 Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		if (qName.equals("province")) {
			provinceModel = new ProvinceWithCollegeModel();
			provinceModel.setName(attributes.getValue(0));
			provinceModel.setSchoolList(new ArrayList<SchoolModel>());
		} else if (qName.equals("college")) {
			schoolModel = new SchoolModel();
			schoolModel.setName(attributes.getValue(0));
			schoolModel.setTag(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("college")) {
			provinceModel.getSchoolList().add(schoolModel);
		} else if (qName.equals("province")) {
			provinceList.add(provinceModel);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
