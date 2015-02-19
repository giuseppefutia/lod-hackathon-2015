package org.dvcama.lodview.controllers;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.atlas.web.AcceptList;
import org.apache.jena.atlas.web.MediaType;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.dvcama.lodview.conf.ConfigurationBean;
import org.dvcama.lodview.response.ResourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UrlPathHelper;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

@Controller
@RequestMapping(value = "/")
public class ResourceController {
	@Autowired
	private MessageSource messageSource;

	@Autowired
	ConfigurationBean conf;

	final AcceptList offeringRDF = new AcceptList("text/turtle, application/turtle, " //
			+ "application/x-turtle, application/rdf+xml, " //
			+ "application/rdf+json, application/ld+json, " //
			+ "text/plain, application/n-triples, text/trig, " //
			+ "application/n-quads, application/x-trig, application/trig, " //
			+ "text/n-quads, text/nquads, application/trix+xml, " //
			+ "application/rdf+json, text/rdf+n3, application/n3, " //
			+ "text/n3");

	final AcceptList offeringResources = new AcceptList("text/html, application/xhtml+xml");

	public ResourceController() {

	}

	public ResourceController(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RequestMapping(value = { "{path:(?!staticResources).*$}", "{path:(?!staticResources).*$}/**" })
	public Object resourceController(Model model, HttpServletRequest req, HttpServletResponse res, Locale locale, @RequestParam(value = "output", defaultValue = "") String output, @CookieValue(value = "colorPair", defaultValue = "") String colorPair) throws UnsupportedEncodingException {
		if (colorPair.equals("")) {
			colorPair = conf.getRandomColorPair();
			Cookie c = new Cookie("colorPair", colorPair);
			c.setPath("/lodview/");
			res.addCookie(c);
		}
		return resource(conf, model, req, res, locale, output, "", colorPair);
	}

	public Object resource(ConfigurationBean conf, Model model, HttpServletRequest req, HttpServletResponse res, Locale locale, String output, String forceIRI, String colorPair) throws UnsupportedEncodingException {
		System.out.println("colorPaircolorPair " + colorPair);
		System.out.println("ResourceController.resource() " + conf.getEndPointUrl());
		model.addAttribute("conf", conf);
		model.addAttribute("colorPair", colorPair);

		String IRIsuffix = new UrlPathHelper().getLookupPathForRequest(req).replaceAll("/lodview/", "/").replaceAll("^/", "");
		System.out.println("IRIsuffix " + IRIsuffix);

		model.addAttribute("path", new UrlPathHelper().getContextPath(req).replaceAll("/lodview/", "/"));

		String IRIprefix = conf.getIRInamespace().replaceAll("/$", "");
		System.out.println("IRIprefix " + IRIprefix);

		String IRI = IRIprefix + "/" + IRIsuffix.replaceAll(" ", "%20");
		if (forceIRI != null && !forceIRI.equals("")) {
			IRI = forceIRI;
		}

		System.out.println("looking for " + IRI);
		System.out.println("client locale " + locale.getLanguage());
		model.addAttribute("locale", locale.getLanguage());

		if (locale.getLanguage().equals("it")) {
			model.addAttribute("lodliveUrl", conf.getStaticResourceURL()+"lodlive/app_it.html?" + IRI.replaceAll("#", "%23"));
		} else if (locale.getLanguage().equals("fr")) {
			model.addAttribute("lodliveUrl", conf.getStaticResourceURL()+"lodlive/app_it.html?" + IRI.replaceAll("#", "%23"));
		} else {
			model.addAttribute("lodliveUrl", conf.getStaticResourceURL()+"lodlive/app_it.html?" + IRI.replaceAll("#", "%23"));
		}

		System.out.println("Accept " + req.getHeader("Accept"));

		AcceptList a = AcceptList.create(req.getHeader("Accept").split(","));
		System.out.println("-- AcceptList: " + a);
		System.out.println("-- OffertList: " + offeringRDF);

		MediaType matchItem = AcceptList.match(offeringRDF, a);
		Lang lang = RDFLanguages.contentTypeToLang(matchItem.getContentType());

		// override content negotiation
		if (!output.equals("")) {
			try {
				output = output.replaceAll("([a-zA-Z]) ([a-zA-Z])", "$1+$2");
				a = AcceptList.create(output.split(","));
				matchItem = AcceptList.match(offeringRDF, a);
				lang = RDFLanguages.contentTypeToLang(matchItem.getContentType());
			} catch (Exception e) {
				return new ErrorController(conf).error406(res, model);
			}
			System.out.println("override content type " + matchItem.getContentType());
		}

		System.out.println("content type " + matchItem.getContentType());
		System.out.println("lang " + lang);

		System.out.println("--------------");
		try {
			if (lang == null) {
				matchItem = AcceptList.match(offeringResources, a);
				System.out.println("matchItem " + matchItem);
				if (matchItem != null) {
					// probably you are asking for an HTML page
					model.addAttribute("contextPath", new UrlPathHelper().getContextPath(req));
					model.addAttribute("results", new ResourceBuilder(messageSource).buildHtmlResource(IRI, locale, conf));
					return "resource";
				} else {
					return new ErrorController(conf).error406(res, model);
				}
			} else {
				// return "forward:/rawdata?IRI=" + IRI + "&sparql=" +
				// java.net.URLEncoder.encode(conf.getEndPointUrl(),"UTF-8") +
				// "&contentType=" + matchItem.getContentType();
				return resourceRaw(conf, model, IRI, conf.getEndPointUrl(), matchItem.getContentType());
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().startsWith("404")) {
				return new ErrorController(conf).error404(res, model, e.getMessage(), IRI, conf.getEndPointUrl());
			} else {
				return new ErrorController(conf).error500(res, model, e.getMessage(), IRI, conf.getEndPointUrl());
			}
		}

	}

	@RequestMapping(value = "/rawdata")
	public ResponseEntity<String> resourceRawController(Model model, @RequestParam(value = "IRI") String IRI, @RequestParam(value = "sparql") String sparql, @RequestParam(value = "contentType", defaultValue = "application/rdf+xml") String contentType) {
		return resourceRaw(conf, model, IRI, sparql, contentType);
	}

	public ResponseEntity<String> resourceRaw(ConfigurationBean conf, Model model, @RequestParam(value = "IRI") String IRI, @RequestParam(value = "sparql") String sparql, @RequestParam(value = "contentType", defaultValue = "application/rdf+xml") String contentType) {
		System.out.println("ResourceController.resourceRaw()");
		contentType = contentType.replaceAll("([a-zA-Z]) ([a-zA-Z])", "$1+$2");
		Lang lang = RDFLanguages.contentTypeToLang(contentType);
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", contentType + "; charset=" + conf.getContentEncoding());
			if (sparql != null && (sparql.equals("<>") || sparql.startsWith("file:///"))) {
				if (sparql.startsWith("file:///")) {
					Dataset dataset = TDBFactory.createDataset(sparql.replaceAll("file:///", "/"));
					dataset.begin(ReadWrite.READ);
					com.hp.hpl.jena.rdf.model.Model m = dataset.getDefaultModel();

					return new ResponseEntity<String>(new ResourceBuilder(messageSource).buildRDFResource(IRI, m, lang, conf), headers, HttpStatus.OK);

				} else {
					com.hp.hpl.jena.rdf.model.Model m = ModelFactory.createDefaultModel();
					try {
						m.read(IRI);
					} catch (Exception e) {
						throw new Exception(messageSource.getMessage("error.noContentNegotiation", null, "sorry but content negotiation is not supported by the IRI", Locale.ENGLISH));
					}
					return new ResponseEntity<String>(new ResourceBuilder(messageSource).buildRDFResource(IRI, m, lang, conf), headers, HttpStatus.OK);
				}
			} else {
				return new ResponseEntity<String>(new ResourceBuilder(messageSource).buildRDFResource(IRI, sparql, lang, conf), headers, HttpStatus.OK);
			}

		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().startsWith("404")) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

	}
}
