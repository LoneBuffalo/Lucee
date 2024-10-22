package lucee.runtime.mvn;

import java.io.IOException;
import java.io.Reader;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import lucee.aprint;
import lucee.commons.io.CharsetUtil;
import lucee.commons.io.IOUtil;
import lucee.commons.io.res.Resource;
import lucee.commons.lang.StringUtil;
import lucee.runtime.text.xml.XMLUtil;
import lucee.transformer.library.function.FunctionLibEntityResolver;
import lucee.transformer.library.function.FunctionLibException;

public final class POMReader extends DefaultHandler {

	private XMLReader xmlReader;
	private StringBuilder content = new StringBuilder();
	private int level = 0;

	private String modelVersion;
	private String groupId;
	private String artifactId;
	private String version;
	private String packaging;
	private String name;
	private String description;
	private String url;

	private static boolean debug = false;

	private boolean insideProperties = false;
	private Map<String, String> properties = new LinkedHashMap<>();

	private boolean insideDependencies = false;
	private List<Dependency> dependencies = new ArrayList<>();
	private Dependency dependency;

	private boolean insideDependencyManagements = false;
	private boolean insideDependencyManagement = false;
	private List<Dependency> dependencyManagements = new ArrayList<>();
	private Dependency dependencyManagement;

	private boolean insideRepositories = false;
	private List<Repository> repositories = new ArrayList<>();
	private Repository repository;

	private boolean insideParent = false;
	private Dependency parent;

	private Resource file;

	private static Map<String, Reference<POMReader>> instances = new ConcurrentHashMap<>();

	public static POMReader getInstance(Resource file) throws IOException, SAXException {
		Reference<POMReader> ref = instances.get(file.getAbsolutePath());
		POMReader pr;
		if (ref != null) {
			pr = ref.get();
			if (pr != null) return pr;
		}
		pr = new POMReader(file);
		pr.read();
		instances.put(file.getAbsolutePath(), new SoftReference<POMReader>(pr));
		return pr;
	}

	private POMReader(Resource file) {
		this.file = file;
	}

	private void read() throws IOException, SAXException {

		Reader r = null;
		try {
			init(new InputSource(r = IOUtil.getReader(file.getInputStream(), (Charset) null)));
		}
		catch (SAXParseException saxe) {
			if (saxe.getMessage().indexOf("oslash") != -1) {
				IOUtil.closeEL(r);
				r = null;

				String str = IOUtil.toString(file, (Charset) null);
				// TODO PATCH make a better solution for that
				str = StringUtil.replace(str, "&oslash;", "ø", false);// (str, "oslash");// &oslash;
				IOUtil.write(file, str.getBytes(CharsetUtil.UTF8), false);
				init(new InputSource(r = IOUtil.getReader(file.getInputStream(), (Charset) null)));

			}
			else throw saxe;

		}
		finally {
			IOUtil.closeEL(r);
		}
	}

	/**
	 * Generelle Initialisierungsmetode der Konstruktoren.
	 * 
	 * @param saxParser String Klassenpfad zum Sax Parser.
	 * @param is InputStream auf die TLD.
	 * @throws SAXException
	 * @throws IOException
	 * @throws FunctionLibException
	 */
	private void init(InputSource is) throws SAXException, IOException {
		xmlReader = XMLUtil.createXMLReader();
		xmlReader.setContentHandler(this);
		xmlReader.setErrorHandler(this);
		xmlReader.setEntityResolver(new FunctionLibEntityResolver());
		xmlReader.parse(is);

	}

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		level++;

		if (level == 2) {
			if ("properties".equals(name)) insideProperties = true;
			else if ("dependencies".equals(name)) insideDependencies = true;
			else if ("parent".equals(name)) insideParent = true;
			else if ("dependencyManagement".equals(name)) insideDependencyManagements = true;
			else if ("repositories".equals(name)) insideRepositories = true;
		}
		else if (level == 3) {
			if (insideDependencies && "dependency".equals(name)) dependency = new Dependency();
			else if (insideDependencyManagements && "dependencies".equals(name)) insideDependencyManagement = true;
			else if (insideRepositories && "repository".equals(name)) repository = new Repository();
		}
		else if (level == 4) {
			if (insideDependencyManagement && "dependency".equals(name)) dependencyManagement = new Dependency();
		}

	}

	/*
	 * ,"modelVersion":xml.XmlRoot.modelVersion.XmlText ,"groupId":xml.XmlRoot.groupId.XmlText
	 * ,"artifactId":xml.XmlRoot.artifactId.XmlText ,"version":xml.XmlRoot.version.XmlText
	 * ,"name":xml.XmlRoot.name.XmlText ,"description":xml.XmlRoot.description.XmlText
	 * ,"groupId":xml.XmlRoot.groupId.XmlText
	 */
	@Override
	public void endElement(String uri, String name, String qName) {
		if (level == 2) {
			if ("properties".equals(name)) insideProperties = false;
			else if ("dependencies".equals(name)) insideDependencies = false;
			else if ("repositories".equals(name)) insideRepositories = false;
			else if ("dependencyManagement".equals(name)) insideDependencyManagements = false;
			else if ("parent".equals(name)) insideParent = false;
			else if ("groupId".equals(name)) this.groupId = content.toString().trim();
			else if ("artifactId".equals(name)) this.artifactId = content.toString().trim();
			else if ("version".equals(name)) this.version = content.toString().trim();
			else if ("packaging".equals(name)) this.packaging = content.toString().trim();
			else if ("name".equals(name)) this.name = content.toString().trim();
			else if ("description".equals(name)) this.description = content.toString().trim();
			else if ("url".equals(name)) this.url = content.toString().trim();
			else if ("modelVersion".equals(name)) this.modelVersion = content.toString().trim();
		}
		else if (level == 3) {
			if (insideProperties) properties.put(name.trim(), content.toString().trim());
			else if ("dependencies".equals(name)) insideDependencyManagement = false;
			else if ("dependency".equals(name) && dependency != null) {
				dependencies.add(dependency);
				dependency = null;
			}
			else if ("repository".equals(name) && repository != null) {
				repositories.add(repository);
				repository = null;
			}
			else if (insideParent) {
				if (parent == null) parent = new Dependency();
				if ("groupId".equals(name)) parent.groupId = content.toString().trim();
				else if ("artifactId".equals(name)) parent.artifactId = content.toString().trim();
				else if ("version".equals(name)) parent.version = content.toString().trim();
				else if ("scope".equals(name)) parent.scope = content.toString().trim();
				else if ("optional".equals(name)) parent.optional = content.toString().trim();
				else if (debug) aprint.e("!!!!!!! ==>" + name + ":" + content.toString().trim());
			}
		}
		else if (level == 4) {
			if (insideDependencyManagements && insideDependencyManagement && "dependency".equals(name)) {
				dependencyManagements.add(dependencyManagement);
				dependencyManagement = null;
			}
			else if (dependency != null) {
				if ("groupId".equals(name)) dependency.groupId = content.toString().trim();
				else if ("artifactId".equals(name)) dependency.artifactId = content.toString().trim();
				else if ("version".equals(name)) dependency.version = content.toString().trim();
				else if ("scope".equals(name)) dependency.scope = content.toString().trim();
				else if ("optional".equals(name)) dependency.optional = content.toString().trim();
				else if (debug) aprint.e("!!!!!!! ==>" + name + ":" + content.toString().trim());
			}
			else if (insideRepositories && repository != null) {
				if ("id".equals(name)) repository.id = content.toString().trim();
				else if ("name".equals(name)) repository.name = content.toString().trim();
				else if ("url".equals(name)) repository.url = content.toString().trim();
				else if (debug) aprint.e("???? ==>" + name + ":" + content.toString().trim());
			}
		}
		else if (level == 5) {
			if (insideDependencyManagements && insideDependencyManagement && dependencyManagement != null) {
				if ("groupId".equals(name)) dependencyManagement.groupId = content.toString().trim();
				else if ("artifactId".equals(name)) dependencyManagement.artifactId = content.toString().trim();
				else if ("version".equals(name)) dependencyManagement.version = content.toString().trim();
				else if ("scope".equals(name)) dependencyManagement.scope = content.toString().trim();
				else if ("optional".equals(name)) dependencyManagement.optional = content.toString().trim();
				else if (debug) aprint.e("xxxxxx ==>" + name + ":" + content.toString().trim());
			}
		}

		content.delete(0, content.length());
		level--;

	}

	@Override
	public void characters(char ch[], int start, int length) {
		content.append(ch, start, length);
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getPackaging() {
		return packaging;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getURL() {
		return url;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public List<Dependency> getDependencyManagements() {
		return dependencyManagements;
	}

	public List<Repository> getRepositories() {
		return repositories;
	}

	public Dependency getParent() {
		return parent;
	}

	public static class Dependency {
		public String groupId;
		public String artifactId;
		public String version;
		public String scope;
		public String optional;

		@Override
		public String toString() {
			return "groupId:" + groupId + ";artifactId:" + artifactId + ";version:" + version + ";scope:" + scope + ";optional:" + optional;
		}
	}

	public static class Repository {
		public String id;
		public String name;
		public String url;

		@Override
		public String toString() {
			return "id:" + id + ";name:" + name + ";url:" + url;
		}
	}
}