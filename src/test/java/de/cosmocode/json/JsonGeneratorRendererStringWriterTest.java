package de.cosmocode.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import de.cosmcode.json.JsonGeneratorRenderer;

@RunWith(BlockJUnit4ClassRunner.class)
public final class JsonGeneratorRendererStringWriterTest extends AbstractJSONRendererWriterTest {

    @Override
    protected JSONRenderer doCreate(final Writer writer) {
    	final JsonFactory factory = new JsonFactory();
        try {
        	final JsonGenerator generator = factory.createJsonGenerator(writer);
			return new JsonGeneratorRenderer(generator);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
    }
    
    @Override
    protected Writer createWriter() throws IOException {
        return new StringWriter();
    }
    
    @Override
    protected String toString(Writer writer) throws IOException {
        return writer.toString();
    }

}
