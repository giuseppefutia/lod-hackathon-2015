package org.dvcama.lodview.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dvcama.lodview.bean.PropertyBean;
import org.dvcama.lodview.bean.ResultBean;
import org.dvcama.lodview.bean.TripleBean;
import org.dvcama.lodview.conf.ConfigurationBean;
import org.dvcama.lodview.response.ResourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LinkedResourcesController implements MessageSourceAware {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	ConfigurationBean conf;

	public LinkedResourcesController() {
	}

	public LinkedResourcesController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ResponseBody
	@RequestMapping(value = "/linkedResourceTitles", produces = "application/xml;charset=UTF-8")
	public String resourceTitles(Model model, HttpServletRequest req, HttpServletResponse res, Locale locale, @RequestParam(value = "IRI") String IRI, @RequestParam(value = "abouts[]") String[] abouts) throws IOException, Exception {
		System.out.println("---------------------------------------------------");
		System.out.println(messageSource);
		System.out.println("---------------------------------------------------");
		return resourceTitles(model, conf, req, res, locale, IRI, abouts);
	}

	public String resourceTitles(Model model, ConfigurationBean conf, HttpServletRequest req, HttpServletResponse res, Locale locale, String IRI, String[] abouts) throws IOException, Exception {
		System.out.println("LinkedResourcesController.resourceTitles() locale: "+locale.getLanguage() );
		StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>");
		try {
			ResultBean results = new ResourceBuilder(messageSource).buildPartialHtmlResource(IRI, abouts, locale, conf, conf.getTitleProperties());
			Map<PropertyBean, List<TripleBean>> literals = results.getLiterals(IRI);
			if (literals == null || literals.size() == 0) {
				return ("<root error=\"true\"><title></title><msg>" + messageSource.getMessage("error.noLiteral", null, "no literal values where found", locale) + "</msg></root>");
			}
			for (PropertyBean key : literals.keySet()) {
				for (TripleBean tripleBean : literals.get(key)) {
					result.append("<resource about=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getProperty()) + //
							"\" nsabout=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getNsProperty()) + //
							"\"><title><![CDATA[" + StringEscapeUtils.escapeHtml4(tripleBean.getValue()) + //
							"]]></title></resource>\n");
				}
			}
			result.append("</root>");
			return result.toString();
		} catch (Exception e) {
			// 404?
			return ("<root error=\"true\"><title>" + messageSource.getMessage("error.linkedResourceUnavailable", null, "unable to retrieve data", locale) + "</title><msg>" + e.getMessage() + "</msg></root>");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/linkedResourceInverses", produces = "application/xml;charset=UTF-8")
	public String resourceInversesController(Model model, HttpServletRequest req, HttpServletResponse res, Locale locale, @RequestParam(value = "IRI") String IRI, @RequestParam(value = "property", defaultValue = "") String property, @RequestParam(value = "start", defaultValue = "-1") int start) throws IOException, Exception {
		return resourceInverses(model, conf, req, res, locale, IRI, property, start);
	}

	public String resourceInverses(Model model, ConfigurationBean conf, HttpServletRequest req, HttpServletResponse res, Locale locale, String IRI, String property, int start) throws IOException, Exception {
		StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>");
		System.out.println("LinkedResourcesController.resourceInverses()");
		if (property.equals("")) {
			/* counting inverse relations */
			try {

				ResultBean results = new ResourceBuilder(messageSource).buildHtmlInverseResource(IRI, locale, conf);
				Map<PropertyBean, List<TripleBean>> resources = results.getResources(IRI);
				if (resources != null) {
					for (PropertyBean key : resources.keySet()) {
						for (TripleBean tripleBean : resources.get(key)) {
							if (tripleBean.getProperty().getProperty() == null || tripleBean.getProperty().getProperty().equals("")) {
								throw new Exception("no content");
							}
							result.append("<resource " //
									+ "about=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getProperty()) + "\" " //
									+ "nsabout=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getNsProperty()) + "\" " //
									+ "propertyurl=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getPropertyUrl()) + "\"" //
									+ "><count><![CDATA[" + StringEscapeUtils.escapeHtml4(tripleBean.getValue()) + "]]></count></resource>\n");
						}
					}
				}

				result.append("</root>");
				return result.toString();

			} catch (Exception e) {
				// e.printStackTrace();
				return ("<root  error=\"true\" ><title>" + messageSource.getMessage("error.linkedResourceUnavailable", null, "unable to retrieve data", locale) + "</title><msg>" + e.getMessage() + "</msg></root>");
			}
		} else {

			/* retrieving inverse relations */
			try {
				ResultBean results = new ResourceBuilder(messageSource).buildHtmlInverseResource(IRI, property, start, locale, conf);
				Map<PropertyBean, List<TripleBean>> resources = results.getResources(IRI);
				for (PropertyBean key : resources.keySet()) {
					for (TripleBean tripleBean : resources.get(key)) {
						result.append("<resource " //
								+ "about=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getProperty()) + "\" " //
								+ "nsabout=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getNsProperty()) + "\" " //
								+ "propertyurl=\"" + StringEscapeUtils.escapeXml11(tripleBean.getProperty().getPropertyUrl()) + "\" " //
								+ "></resource>\n");
					}
				}

				result.append("</root>");
				return result.toString();

			} catch (Exception e) {
				e.printStackTrace();
				return ("<root  error=\"true\" ><title>" + messageSource.getMessage("error.linkedResourceUnavailable", null, "unable to retrieve data", locale) + "</title><msg><![CDATA[" + e.getMessage() + "]]></msg></root>");
			}
		}
	}

	@Override
	public void setMessageSource(MessageSource arg0) {
		// TODO Auto-generated method stub

	}
}
