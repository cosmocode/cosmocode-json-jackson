package de.cosmocode.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONException;
import org.json.extension.JSONEncoder;
import org.json.extension.NoObjectContext;

import com.google.common.base.Preconditions;

import de.cosmocode.patterns.Adapter;

/**
 * <p> Implementation of the {@link JSONRenderer} interface that delegates
 * its calls to a {@link JsonGenerator}.
 * </p>
 * <p> This class does not support toString(), but writes directly into
 * the JsonGenerator instead. If you want toString()-Support, use
 * {@link JacksonRenderer} instead.
 * </p>
 * @author Oliver Lorenz
 */
@Adapter(JSONRenderer.class)
public final class JsonGeneratorRenderer extends AbstractJSONRenderer implements CloseableJsonRenderer {
    
    public static final String ERR_TOSTRING_UNSUPPORTED = "JsonGeneratorRenderer writes directly";
    public static final String ERR_BEFORE_FIRST = "Illegal initial call on a key or value method";
    public static final String ERR_AFTER_LAST = "Illegal call after last endObject() or endArray() call";
    
    private final JsonGenerator generator;
    
    private final RenderLevel level;
    
    /**
     * indicates the depth of the current render process.
     * For example: this.object().object() would result in renderDepth = 2,
     * and this.object().endObject() would result in renderDepth = 0.
     * It is -1 at start and never becomes negative again after the first successful method call.
     */
    private int renderDepth = -1;
    
    /**
     * Creates a new JsonRenderer from the given JsonGenerator.
     * @param generator the JsonGenerator to adapt
     */
    public JsonGeneratorRenderer(final JsonGenerator generator) {
        this(generator, JSONRenderer.DEFAULT_LEVEL);
    }
    
    public JsonGeneratorRenderer(final JsonGenerator generator, final RenderLevel level) {
        super();
        
        this.generator = Preconditions.checkNotNull(generator, "Generator");
        this.level = Preconditions.checkNotNull(level, "Level");
        
        // test if generator can write output
        if (this.generator.isClosed()) {
            throw new IllegalStateException("The given JsonGenerator is already closed");
        }
    }
    
    
    /**
     * <p> Checks {@code renderDepth > 0} to see if the render process is running.
     * It throws an IllegalStateException if that is not the case.
     * </p>
     * <p> The render process has started if the first call was array() or object()
     * (or a similar method that can be called first), and if we are not after the
     * last call.
     * </p>
     * 
     * @see #checkBeforeFirst()
     * @see #checkAfterLast()
     */
    protected void checkRenderRunning() {
        if (renderDepth > 0) {
            return;
        } else if (renderDepth == 0) {
            throw new IllegalStateException(ERR_AFTER_LAST);
        } else {
            // renderDepth < 0
            throw new IllegalStateException(ERR_BEFORE_FIRST);
        }
    }
    
    /**
     * <p> Checks {@code renderDepth < 0} to see if we are before the first call.
     * It throws an IllegalStateException if that's the case.
     * </p>
     * <p> The first call must be an array() or object() (or similar) call,
     * so only those methods don't have this check.
     * </p>
     */
    protected void checkBeforeFirst() {
        if (renderDepth < 0) {
            throw new IllegalStateException(ERR_BEFORE_FIRST);
        }
    }
    
    /**
     * <p> Checks {@code renderDepth == 0} to see if we are after the last call.
     * It throws an IllegalStateException if that's the case.
     * </p>
     * <p> The last call is reached if at least one array() or object() call
     * on this object was registered and if the sum of array() and object() calls
     * and the sum of the endArray() and endObject() calls are equal.
     * </p>
     * <p> Or, in other words: If the surrounding json-array or -object was finished.
     * </p>
     */
    protected void checkAfterLast() {
        if (renderDepth == 0) {
            throw new IllegalStateException(ERR_AFTER_LAST);
        }
    }
    
    public JsonGenerator getGenerator() {
        return generator;
    }
    
    @Override
    public RenderLevel currentLevel() {
        return level;
    }

    @Override
    public JSONRenderer array() {
        checkAfterLast();
        try {
            generator.writeStartArray();
            if (renderDepth < 0) renderDepth = 0;
            ++renderDepth;
        } catch (final JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer endArray() {
        checkRenderRunning();
        try {
            generator.writeEndArray();
            --renderDepth;
            if (renderDepth == 0) generator.flush();
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer object() {
        checkAfterLast();
        try {
            generator.writeStartObject();
            if (renderDepth < 0) renderDepth = 0;
            ++renderDepth;
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer endObject() {
        checkRenderRunning();
        try {
            generator.writeEndObject();
            --renderDepth;
            if (renderDepth == 0) generator.flush();
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer key(final CharSequence key) {
        checkRenderRunning();
        try {
            generator.writeFieldName(key == null ? "null" : key.toString());
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer nullValue() {
        checkRenderRunning();
        try {
            generator.writeNull();
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    protected JSONRenderer unknownValue(Object value) {
        checkAfterLast();
        if (generator.getCodec() == null) {
            return super.unknownValue(value);
        } else {
            try {
                generator.writeObject(value);
                if (this.renderDepth <= 0) {
                    this.renderDepth = 0;
                    generator.flush();
                }
            } catch (JsonGenerationException e) {
                throw new IllegalStateException(e);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            return this;
        }
    }
    
    @Override
    public JSONRenderer value(boolean value) {
        checkRenderRunning();
        try {
            generator.writeBoolean(value);
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer value(long value) {
        checkRenderRunning();
        try {
            generator.writeNumber(value);
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer value(double value) {
        checkRenderRunning();
        try {
            generator.writeNumber(value);
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer value(CharSequence value) {
        if (value == null) return nullValue();

        checkRenderRunning();
        try {
            generator.writeString(value.toString());
        } catch (JsonGenerationException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer pairs(NoObjectContext pairs) {
        if (pairs == null) return this;

        checkRenderRunning();
        try {
            pairs.encodeJSON(JSON.asJSONConstructor(this));
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        return this;
    }
    
    @Override
    public JSONRenderer object(NoObjectContext pairs) {
        return object().pairs(pairs).endObject();
    }
    
    @Override
    public JSONRenderer object(JSONEncoder object) {
        checkAfterLast();
        if (object == null) return object().endObject();
        try {
            object.encodeJSON(JSON.asJSONConstructor(this));
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
        if (renderDepth == 0) {
            try {
                generator.flush();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return this;
    }
    
    @Override
    public void close() throws IOException {
        if (generator.isClosed()) return;
        
        generator.flush();
        generator.close();
    }
    
    @Override
    public String toString() {
        throw new UnsupportedOperationException(ERR_TOSTRING_UNSUPPORTED);
    }

}
