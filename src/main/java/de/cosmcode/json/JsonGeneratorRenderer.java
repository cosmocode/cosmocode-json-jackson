package de.cosmcode.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.json.JSONException;
import org.json.extension.JSONEncoder;
import org.json.extension.NoObjectContext;

import com.google.common.base.Preconditions;

import de.cosmocode.json.AbstractJSONRenderer;
import de.cosmocode.json.JSON;
import de.cosmocode.json.JSONRenderer;
import de.cosmocode.json.RenderLevel;
import de.cosmocode.patterns.Adapter;

/**
 * <p> Implementation of the {@link JSONRenderer} interface that delegates
 * its calls to a {@link JsonGenerator}.
 * </p>
 * <p> This class does not support toString(), but writes directly into
 * the JsonGenerator instead. If you want toString()-Support, use
 * (...) instead.
 * </p>
 * @author Oliver Lorenz
 */
@Adapter(JSONRenderer.class)
public final class JsonGeneratorRenderer extends AbstractJSONRenderer implements JSONRenderer {
	
	public static final String ERR_TOSTRING_UNSUPPORTED = "JsonGeneratorRenderer writes directly";
	
	private final JsonGenerator generator;
    
    private final RenderLevel level;
    
    /**
     * indicates the depth of the current render process.
     * For example: this.object().object() would result in renderDepth = 2,
     * and this.object.endObject() would result in renderDepth = 0.
     * The default is 0 at start.
     */
    private int renderDepth;
	
    /**
     * Creates a new JsonRenderer from the given JsonGenerator.
     * @param generator the JsonGenerator to adapt
     */
	public JsonGeneratorRenderer(final JsonGenerator generator) {
		this(generator, JSONRenderer.DEFAULT_LEVEL);
	}
	
	public JsonGeneratorRenderer(final JsonGenerator generator, final RenderLevel level) {
		this.generator = Preconditions.checkNotNull(generator, "Generator");
		this.level = Preconditions.checkNotNull(level, "Level");
		
		// test if generator can write output
		if (this.generator.isClosed()) {
			throw new IllegalStateException("The given JsonGenerator is already closed");
		}
	}
	
	
	@Override
	public RenderLevel currentLevel() {
		return level;
	}

	@Override
	public JSONRenderer array() {
		try {
			generator.writeStartArray();
		} catch (final JsonGenerationException e) {
			throw new IllegalStateException(e);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}
	
	@Override
	public JSONRenderer endArray() {
		try {
			generator.writeEndArray();
			generator.flush();
		} catch (JsonGenerationException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}
	
	@Override
	public JSONRenderer object() {
		try {
			generator.writeStartObject();
		} catch (JsonGenerationException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}
	
	@Override
	public JSONRenderer endObject() {
		try {
			generator.writeEndObject();
			generator.flush();
		} catch (JsonGenerationException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return this;
	}
	
	@Override
	public JSONRenderer key(final CharSequence key) {
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
		if (generator.getCodec() == null) {
			return super.unknownValue(value);
		} else {
			try {
				generator.writeObject(value);
				generator.flush();
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
        try {
            pairs.encodeJSON(JSON.asJSONConstructor(this));
            return this;
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
	}
	
	@Override
	public JSONRenderer object(NoObjectContext pairs) {
        return object().pairs(pairs).endObject();
	}
	
	@Override
	public JSONRenderer object(JSONEncoder object) {
        if (object == null) return object().endObject();
        try {
            object.encodeJSON(JSON.asJSONConstructor(this));
            return this;
        } catch (JSONException e) {
            throw new IllegalArgumentException(e);
        }
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException(ERR_TOSTRING_UNSUPPORTED);
	}

}
