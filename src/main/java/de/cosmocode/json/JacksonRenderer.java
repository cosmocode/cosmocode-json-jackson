package de.cosmocode.json;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

/**
 * <p> Implementation of the {@link JSONRenderer} interface that delegates
 * its calls to a {@link JsonGenerator}.
 * </p>
 *
 * @author Oliver Lorenz
 */
public final class JacksonRenderer extends ForwardingJSONRenderer implements CloseableJsonRenderer {
    
    private final StringWriter writer;
    
    private final JsonGeneratorRenderer renderer;
    
    /**
     * Creates a default JacksonRenderer, using a cached {@link JsonFactory} for creation.
     */
    public JacksonRenderer() {
        this(Jackson.FACTORY);
    }
    
    /**
     * Creates a default JacksonRenderer, using a cached {@link JsonFactory} for creation.
     * The RenderLevel is set to the given parameter.
     * @param level the RenderLevel for the {@link JSONRenderer}
     */
    public JacksonRenderer(final RenderLevel level) {
        this(Jackson.FACTORY, level);
    }
    
    /**
     * Creates a new JacksonRenderer that only has the given {@link JsonGenerator.Feature}s enabled.
     * @param features the features to enable
     */
    public JacksonRenderer(final JsonGenerator.Feature... features) {
        this(Jackson.createFactory(features));
    }
    
    /**
     * Creates a new JacksonRenderer from the given {@link JsonFactory}.
     * All calls are delegated to a {@link JsonGeneratorRenderer} adapter.
     * @param factory the factory to create the {@link JsonGenerator} from
     * 
     * @see JsonGeneratorRenderer#JsonGeneratorRenderer(JsonGenerator)
     */
    public JacksonRenderer(final JsonFactory factory) {
        super();
        
        this.writer = new StringWriter();
        
        final JsonGenerator generator;
        try {
            generator = factory.createJsonGenerator(writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given factory could not create a JsonGenerator", e);
        }
        this.renderer = new JsonGeneratorRenderer(generator);
    }
    
    /**
     * Creates a new JacksonRenderer from the given {@link JsonFactory}.
     * All calls are delegated to a {@link JsonGeneratorRenderer} adapter.
     * @param factory the factory to create the {@link JsonGenerator} from
     * @param level the RenderLevel for the {@link JSONRenderer}
     * 
     * @see JsonGeneratorRenderer#JsonGeneratorRenderer(JsonGenerator, RenderLevel)
     */
    public JacksonRenderer(final JsonFactory factory, final RenderLevel level) {
        super();
        
        this.writer = new StringWriter();
        
        final JsonGenerator generator;
        try {
            generator = factory.createJsonGenerator(writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("The given factory could not create a JsonGenerator", e);
        }
        this.renderer = new JsonGeneratorRenderer(generator, level);
    }

    @Override
    protected JSONRenderer delegate() {
        return renderer;
    }
    
    @Override
    public void close() throws IOException {
        renderer.close();
    }
    
    @Override
    public String toString() {
        if (renderer.getGenerator().isClosed()) {
            return writer.toString();
        } else {
            try {
                renderer.getGenerator().flush();
            } catch (IOException e) {
                throw new IllegalStateException("Flushing the JsonGeneratorRenderer failed", e);
            }
            return writer.toString();
        }
    }

}
