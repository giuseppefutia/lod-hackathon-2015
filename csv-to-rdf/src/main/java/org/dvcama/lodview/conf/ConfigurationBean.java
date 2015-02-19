package org.dvcama.lodview.conf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletContext;

import org.apache.jena.riot.RDFDataMgr;
import org.springframework.web.context.ServletContextAware;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class ConfigurationBean implements ServletContextAware, Cloneable {

	private Model confModel = null;
	private ServletContext context;

	private String confFile, EndPointUrl, IRInamespace, contentEncoding, staticResourceURL, preferredLanguage, publicUrlPrefix = null, publicUrlSuffix = "", authUsername = null, authPassword = null, defaultInverseBehaviour = "collapse";

	private List<String> defaultQueries, defaultRawDataQueries, defaultInversesQueries, defaultInversesTest, defaultInversesCountQueries, typeProperties, imageProperties, linkingProperties, titleProperties, descriptionProperties, longitudeProperties, latitudeProperties = null;
	private List<String> colorPair = null, skipDomains = null;
	Random rand = new Random();

	public ConfigurationBean() throws IOException, Exception {

	}

	public void populateBean() throws IOException, Exception {
		System.out.println("Initializing configuration " + confFile);
		File configFile = new File(confFile);
		if (!configFile.isAbsolute()) {
			configFile = new File(context.getRealPath("/") + "/WEB-INF/" + confFile);
		}
		if (!configFile.exists()) {
			throw new Exception("Configuration file not found (" + configFile.getAbsolutePath() + ")");
		}
		confModel = RDFDataMgr.loadModel(configFile.getAbsolutePath());

		EndPointUrl = getSingleConfValue("endpoint");
		authPassword = getSingleConfValue("authPassword");
		authUsername = getSingleConfValue("authUsername");

		IRInamespace = getSingleConfValue("IRInamespace", "<not provided>");

		publicUrlPrefix = getSingleConfValue("publicUrlPrefix", "");
		publicUrlPrefix = publicUrlPrefix.replaceAll(".+/auto$", context.getContextPath() + "/");

		contentEncoding = getSingleConfValue("contentEncoding");
		staticResourceURL = getSingleConfValue("staticResourceURL", "");
		staticResourceURL = staticResourceURL.replaceAll(".+/auto$", context.getContextPath() + "/staticResources/");

		preferredLanguage = getSingleConfValue("preferredLanguage");

		typeProperties = getMultiConfValue("typeProperties");
		titleProperties = getMultiConfValue("titleProperties");
		descriptionProperties = getMultiConfValue("descriptionProperties");
		imageProperties = getMultiConfValue("imageProperties");
		linkingProperties = getMultiConfValue("linkingProperties");
		longitudeProperties = getMultiConfValue("longitudeProperties");
		latitudeProperties = getMultiConfValue("latitudeProperties");

		defaultQueries = getMultiConfValue("defaultQueries");
		defaultRawDataQueries = getMultiConfValue("defaultRawDataQueries");
		defaultInversesQueries = getMultiConfValue("defaultInversesQueries");
		defaultInversesTest = getMultiConfValue("defaultInversesTest");
		defaultInversesCountQueries = getMultiConfValue("defaultInversesCountQueries");

		defaultInverseBehaviour = getSingleConfValue("defaultInverseBehaviour", defaultInverseBehaviour);

		colorPair = getMultiConfValue("colorPair");
		skipDomains = getMultiConfValue("skipDomains");
	}

	private String getSingleConfValue(String prop) {
		return getSingleConfValue(prop, null);
	}

	private String getSingleConfValue(String prop, String defaultValue) {
		NodeIterator iter = confModel.listObjectsOfProperty(confModel.createProperty(confModel.getNsPrefixURI("conf"), prop));
		while (iter.hasNext()) {
			RDFNode node = iter.next();
			return node.toString();
		}
		return defaultValue;
	}

	private List<String> getMultiConfValue(String prop) {
		List<String> result = new ArrayList<String>();
		NodeIterator iter = confModel.listObjectsOfProperty(confModel.createProperty(confModel.getNsPrefixURI("conf"), prop));
		while (iter.hasNext()) {
			RDFNode node = iter.next();
			result.add(node.toString());
		}
		return result;
	}

	public void setConfFile(String confFile) {
		this.confFile = confFile;
	}

	@Override
	public void setServletContext(ServletContext arg0) {
		this.context = arg0;
		try {
			populateBean();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Model getConfModel() {
		return confModel;
	}

	public Map<String, String> getPrefixes() {
		return confModel.getNsPrefixMap();
	}

	public String getNsPrefixURI(String prefix) {
		return confModel.getNsPrefixURI(prefix);
	}

	public String getNsURIPrefix(String IRI) {
		return confModel.getNsURIPrefix(IRI);
	}

	public String getEndPointUrl() {
		return EndPointUrl;
	}

	public void setEndPointUrl(String EndPointUrl) {
		this.EndPointUrl = EndPointUrl;
	}

	public void setIRInamespace(String IRInamespace) {
		this.IRInamespace = IRInamespace;
	}

	public List<String> getDefaultQueries() {
		return defaultQueries;
	}

	public List<String> getTypeProperties() {
		return typeProperties;
	}

	public String getIRInamespace() {
		return IRInamespace;
	}

	public String getPublicUrlPrefix() {
		return publicUrlPrefix;
	}

	public String getPublicUrlSuffix() {
		return publicUrlSuffix;
	}

	public void setPublicUrlPrefix(String publicUrlPrefix) {
		this.publicUrlPrefix = publicUrlPrefix;
	}

	public void setPublicUrlSuffix(String publicUrlSuffix) {
		this.publicUrlSuffix = publicUrlSuffix;
	}

	public List<String> getDefaultRawDataQueries() {
		return defaultRawDataQueries;
	}

	public List<String> getDefaultInversesQueries() {
		return defaultInversesQueries;
	}

	public List<String> getDefaultInversesTest() {
		return defaultInversesTest;
	}

	public List<String> getDefaultInversesCountQueries() {
		return defaultInversesCountQueries;
	}

	public String getStaticResourceURL() {
		return staticResourceURL;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public List<String> getTitleProperties() {
		return titleProperties;
	}

	public List<String> getLongitudeProperties() {
		return longitudeProperties;
	}

	public List<String> getLatitudeProperties() {
		return latitudeProperties;
	}

	public List<String> getDescriptionProperties() {
		return descriptionProperties;
	}

	public List<String> getImageProperties() {
		return imageProperties;
	}

	public List<String> getLinkingProperties() {
		return linkingProperties;
	}

	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	public List<String> getColorPair() {
		return colorPair;
	}

	public String getRandomColorPair() {
		int randomNum = rand.nextInt(colorPair.size());
		return colorPair.get(randomNum);
	}

	public void setColorPair(List<String> colorPair) {
		this.colorPair = colorPair;
	}

	public List<String> getSkipDomains() {
		return skipDomains;
	}

	public void setSkipDomains(List<String> skipDomains) {
		this.skipDomains = skipDomains;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getAuthUsername() {
		return authUsername;
	}

	public void setAuthUsername(String authUsername) {
		this.authUsername = authUsername;
	}

	public String getDefaultInverseBehaviour() {
		return defaultInverseBehaviour;
	}

	public void setDefaultInverseBehaviour(String defaultInverseBehaviour) {
		this.defaultInverseBehaviour = defaultInverseBehaviour;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error("Something impossible just happened");
		}
	}

	@Override
	public String toString() {
		return "ConfigurationBean [confModel=" + confModel + ", context=" + context + ", confFile=" + confFile + ", EndPointUrl=" + EndPointUrl + ", IRInamespace=" + IRInamespace + ", contentEncoding=" + contentEncoding + ", staticResourceURL=" + staticResourceURL + ", preferredLanguage=" + preferredLanguage + ", publicUrlPrefix=" + publicUrlPrefix + ", authUsername=" + authUsername + ", authPassword=" + authPassword + ", defaultInverseBehaviour=" + defaultInverseBehaviour + ", defaultQueries=" + defaultQueries + ", defaultRawDataQueries=" + defaultRawDataQueries + ", defaultInversesQueries=" + defaultInversesQueries + ", defaultInversesTest=" + defaultInversesTest + ", defaultInversesCountQueries=" + defaultInversesCountQueries + ", typeProperties=" + typeProperties
				+ ", imageProperties=" + imageProperties + ", linkingProperties=" + linkingProperties + ", titleProperties=" + titleProperties + ", descriptionProperties=" + descriptionProperties + ", longitudeProperties=" + longitudeProperties + ", latitudeProperties=" + latitudeProperties + ", colorPair=" + colorPair + ", skipDomains=" + skipDomains + ", rand=" + rand + "]";
	}

}
