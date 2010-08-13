package de.cosmocode.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import de.cosmocode.rendering.AbstractRenderer;
import de.cosmocode.rendering.Renderer;
import de.cosmocode.rendering.RenderingException;

/**
 * Abstract {@link JsonGenerator} based {@link Renderer}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
abstract class ForwardingJacksonRenderer extends AbstractRenderer {

    static final JsonFactory FACTORY = new JsonFactory();

    /**
     * Provides access to the underlying generator.
     * 
     * @since 1.0
     * @return the current generator
     */
    protected abstract JsonGenerator generator();
    
    @Override
    public Renderer list() throws RenderingException {
        try {
            generator().writeStartArray();
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer endList() throws RenderingException {
        try {
            generator().writeEndArray();
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer map() throws RenderingException {
        try {
            generator().writeStartObject();
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer endMap() throws RenderingException {
        try {
            generator().writeEndObject();
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer key(CharSequence key) throws RenderingException {
        try {
            generator().writeFieldName(key == null ? "null" : key.toString());
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer nullValue() throws RenderingException {
        try {
            generator().writeNull();
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer value(boolean value) throws RenderingException {
        try {
            generator().writeBoolean(value);
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer value(long value) throws RenderingException {
        try {
            generator().writeNumber(value);
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer value(double value) throws RenderingException {
        try {
            generator().writeNumber(value);
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public Renderer value(CharSequence value) throws RenderingException {
        if (value == null) return nullValue();
        try {
            generator().writeString(value.toString());
            return this;
        } catch (IOException e) {
            throw new RenderingException(e);
        }
    }

}
