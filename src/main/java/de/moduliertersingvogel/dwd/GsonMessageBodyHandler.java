package de.moduliertersingvogel.dwd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ApplicationScoped
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
	private static final String UTF_8 = "UTF-8";
	private Gson gson = new GsonBuilder().create();

	@Override
	public boolean isReadable(Class<?> type, Type genericType, java.lang.annotation.Annotation[] annotations,
			MediaType mediaType) {
		return true;
	}

	@Override
	public Object readFrom(Class<Object> clazz, Type type, java.lang.annotation.Annotation[] annotations,
			MediaType mediatype, MultivaluedMap<String, String> headers, InputStream instream)
			throws IOException, WebApplicationException {
		try (InputStreamReader streamReader = new InputStreamReader(instream, UTF_8)) {
			return gson.fromJson(streamReader, type);
		}
	}

	@Override
	public boolean isWriteable(Class<?> arg0, Type arg1, java.lang.annotation.Annotation[] arg2, MediaType arg3) {
		return true;
	}

	@Override
	public void writeTo(Object obj, Class<?> clazz, Type type, java.lang.annotation.Annotation[] annotations,
			MediaType mediatype, MultivaluedMap<String, Object> headers, OutputStream outstream)
			throws IOException, WebApplicationException {
		try (OutputStreamWriter writer = new OutputStreamWriter(outstream, UTF_8)) {
			final String content = gson.toJson(obj);
			writer.write(content);
		}
	}
}