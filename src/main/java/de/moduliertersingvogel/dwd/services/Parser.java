package de.moduliertersingvogel.dwd.services;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser {

	private static final Logger logger = LogManager.getFormatterLogger("dwd");

	private List<String> placemarks = new ArrayList<>();
	private List<LocalDateTime> timesteps = new ArrayList<>();
	private Map<String, List<Float>> precipitationMap = new HashMap<>();

	public Parser(final Path filepath) throws KMLParserException {
		logger.debug("Creating new Parser for file: %s.", filepath.toString());

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {

				boolean timestep = false;
				boolean placemark = false;
				boolean forecast = false;
				boolean placemarkdescription = false;
				boolean precipitation = false;
				boolean precipitationvalue = false;

				String placemarkname = "";

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					if (qName.equalsIgnoreCase("dwd:TimeStep")) {
						timestep = true;
					}
					if (qName.equalsIgnoreCase("kml:Placemark")) {
						placemark = true;
					}

					if (placemark && qName.equalsIgnoreCase("kml:description")) {
						placemarkdescription = true;
					}

					if (qName.equalsIgnoreCase("dwd:Forecast")) {
						forecast = true;
					}

					if (forecast) {
						String parameter = attributes.getValue("dwd:elementName");
						if (forecast && parameter != null && parameter.equalsIgnoreCase("R101")) {
							precipitation = true;
						}
					}

					if (precipitation && qName.equalsIgnoreCase("dwd:value")) {
						precipitationvalue = true;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (qName.equalsIgnoreCase("dwd:TimeStep")) {
						timestep = false;
					}
					if (qName.equalsIgnoreCase("kml:Placemark")) {
						placemark = false;
					}
					if (qName.equalsIgnoreCase("dwd:Forecast")) {
						forecast = false;
						precipitation = false;
					}
					if (qName.equalsIgnoreCase("dwd:value")) {
						precipitationvalue = false;
					}
					if (placemarkdescription && qName.equalsIgnoreCase("kml:description")) {
						placemarkdescription = false;
					}
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					if (timestep) {
						final String text = new String(ch, start, length);
						try {
							timesteps.add(LocalDateTime.parse(text,
									DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
						} catch (DateTimeParseException e) {
							logger.catching(e);
						}
					}

					if (placemarkdescription) {
						placemarkname = new String(ch, start, length);
						placemarks.add(placemarkname);
					}

					if (precipitationvalue) {
						String[] stringvalues = new String(ch, start, length).trim().split("\\s+");
						List<Float> values = Arrays.stream(stringvalues).map(s -> {
							try {
								return Float.valueOf(s);
							} catch (NumberFormatException e) {
								return (Float) null;
							}
						}).collect(Collectors.toList());
						precipitationMap.put(placemarkname.replaceAll("[ /?]", ""), Collections.unmodifiableList(values));
					}
				}
			};

			try (ZipFile zip = new ZipFile(filepath.toFile())) {
				// The zip file contains just 1 element.
				ZipEntry entry = zip.entries().nextElement();
				InputStream xmlStream = zip.getInputStream(entry);
				saxParser.parse(xmlStream, handler);
				xmlStream.close();
			}
			catch(Exception e) {
				throw new KMLParserException(e);
			}

			logger.debug("Parser finished");

		} catch (ParserConfigurationException | SAXException e) {
			throw new KMLParserException(e);
		}
	}

	public List<String> getPlacemarks() {
		return Collections.unmodifiableList(placemarks);
	}

	public Map<String, List<Float>> getPrecipitationMap() {
		return Collections.unmodifiableMap(precipitationMap);
	}

	public List<LocalDateTime> getTimestamps() {
		return Collections.unmodifiableList(timesteps);
	}

	public class KMLParserException extends Exception {
		private static final long serialVersionUID = 2074053632465555162L;

		public KMLParserException(Throwable cause) {
			super(cause);
		}
	}
}
