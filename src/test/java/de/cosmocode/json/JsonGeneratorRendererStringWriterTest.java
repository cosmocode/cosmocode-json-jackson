package de.cosmocode.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Tests the {@link JsonGeneratorRenderer} on its abilities as a {@link JSONRenderer}.
 * It uses a StringWriter to create Strings from the output.
 * 
 * @author Oliver Lorenz
 */
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
    
    /**
     * <p> Tests the performance of the {@link JsonGeneratorRenderer}.
     * </p>
     * <p> <strong> Important: </strong> This is not a full scale performance test.
     * If performance issues are of importance, please make your own performance tests.
     * </p>
     */
    @Test
    public void performanceTest() {
        final int loopCount = 10000;
        final long start;
        final long end;
        final String result;
        
        start = System.currentTimeMillis();
        
        final JSONRenderer renderer = create();
        renderer.array();
        for (int i = 0; i < loopCount; i++) {
            renderer.object().
                key("test").value(true).
                key("double").value(i / 10).
                key("int").value(i);
            renderer.endObject();
            renderer.array().
                value(true).
                value("blubb");
            renderer.endArray();
        }
        renderer.endArray();
        result = toJSONString(renderer);
        
        end = System.currentTimeMillis();
        
        System.out.println("Results of Performance Test:");
        System.out.println("Needed " + (end - start) + " ms for " + loopCount + " loops.");
        System.out.println("Result is " + result.length() + " characters long");
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle infinity")
    public void valueFloatPositiveInfinite() {
        super.valueFloatPositiveInfinite();
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle infinity")
    public void valueFloatNegativeInfinite() {
        super.valueFloatNegativeInfinite();
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle NaN")
    public void valueFloatNaN() {
        super.valueFloatNaN();
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle infinity")
    public void valueDoublePositiveInfinite() {
        super.valueDoublePositiveInfinite();
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle infinity")
    public void valueDoubleNegativeInfinite() {
        super.valueDoubleNegativeInfinite();
    }
    
    @Override
    @Test
    @Ignore("The Jackson JsonGenerator can handle NaN")
    public void valueDoubleNaN() {
        super.valueDoubleNaN();
    }

}
