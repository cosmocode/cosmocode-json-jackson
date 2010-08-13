package de.cosmocode.jackson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;

import de.cosmocode.rendering.Renderer;
import de.cosmocode.rendering.RenderingException;

/**
 * A Jackson {@link JsonGenerator} based {@link Renderer} which uses streaming.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class StreamingJacksonRenderer extends ForwardingJacksonRenderer {

    private final JsonGenerator generator;
    
    public StreamingJacksonRenderer(Writer writer) {
        try {
            this.generator = FACTORY.createJsonGenerator(writer);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public StreamingJacksonRenderer(OutputStream stream) {
        try {
            this.generator = FACTORY.createJsonGenerator(stream, JsonEncoding.UTF8);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    protected JsonGenerator generator() {
        return generator;
    }

    @Override
    public Renderer endList() throws RenderingException {
        super.endList();
        try {
            generator.flush();
        } catch (IOException e) {
            throw new RenderingException(e);
        }
        return this;
    }
    
    @Override
    public Renderer endMap() throws RenderingException {
        super.endMap();
        try {
            generator.flush();
        } catch (IOException e) {
            throw new RenderingException(e);
        }
        return this;
    }
    
    @Override
    public JsonGenerator build() throws RenderingException {
        // FIXME UnsupportedOperationException would be compliant with the spec
        if (generator.isClosed()) {
            return generator;
        } else {
            try {
                generator.close();
                return generator;
            } catch (IOException e) {
                throw new RenderingException(e);
            }
        }
    }

}
