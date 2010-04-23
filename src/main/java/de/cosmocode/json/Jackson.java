/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;

/**
 * A helper class that has a cached {@link JsonFactory} to create JsonGenerators
 * and provides some convenience methods to create {@link JSONRenderer}s.
 * 
 * @author Oliver Lorenz
 */
public final class Jackson {
    
    /**
     * The default JsonEncoding for {@link #createJsonRenderer(OutputStream)}.
     * This is {@linkplain JsonEncoding#UTF8 UTF8}.
     */
    public static final JsonEncoding DEFAULT_ENCODING = JsonEncoding.UTF8;
    
    /**
     * <p> This is a convience {@link JsonFactory} to encourage reuse of one JsonFactory.
     * It is immutable, so every configuration that differs from the default
     * should create its own JsonFactory.
     */
    public static final JsonFactory FACTORY = new ImmutableJsonFactory();
    
    private Jackson() {
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} backed by the Jackson framework.
     * </p>
     * <p> The returned JsonRenderer supports toString(), which closes the underlying
     * {@link JsonGenerator} and returns the current parsing process as a String.
     * Only toString() can be called after a call to toString(),
     * all other methods throw {@link IllegalStateException}s.
     * </p>
     * @return a {@link JSONRenderer} backed by the Jackson framework
     */
    public static CloseableJsonRenderer createJsonRenderer() {
        return new JacksonRenderer();
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} backed by the Jackson framework.
     * </p>
     * <p> The returned JsonRenderer supports toString(), which closes the underlying
     * {@link JsonGenerator} and returns the current parsing process as a String.
     * Only toString() can be called after a call to toString(),
     * all other methods throw {@link IllegalStateException}s.
     * </p>
     * @param level the RenderLevel of the JsonRenderer
     * @return a {@link JSONRenderer} backed by the Jackson framework
     */
    public static CloseableJsonRenderer createJsonRenderer(final RenderLevel level) {
        return new JacksonRenderer(level);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} backed by the Jackson framework.
     * It only supports the given {@link JsonGenerator.Feature}s,
     * all other features are disabled.
     * </p>
     * <p> The returned JsonRenderer supports toString(), which closes the underlying
     * {@link JsonGenerator} and returns the current parsing process as a String.
     * Only toString() can be called after a call to toString(),
     * all other methods throw {@link IllegalStateException}s.
     * </p>
     * @param features the features to enable
     * @return a {@link JSONRenderer} backed by the Jackson framework
     */
    public static CloseableJsonRenderer createJsonRenderer(final JsonGenerator.Feature... features) {
        return new JacksonRenderer(createFactory(features));
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that directly writes to the given OutputStream,
     * using UTF8 as character encoding.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * OutputStream and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param out the OutputStream to write on
     * @return a new JsonRenderer that writes its output on the given OutputStream.
     * @throws IOException if the creation fails due to low-level IO exceptions
     * 
     * @see JsonFactory#createJsonGenerator(OutputStream, JsonEncoding)
     */
    public static CloseableJsonRenderer createJsonRenderer(final OutputStream out)
        throws IOException {
        
        final JsonGenerator generator = FACTORY.createJsonGenerator(out, DEFAULT_ENCODING);
        return new JsonGeneratorRenderer(generator);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that directly writes to the given OutputStream,
     * using UTF8 as character encoding.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * OutputStream and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param out the OutputStream to write on
     * @param level the RenderLevel of the JsonRenderer
     * @return a new JsonRenderer that writes its output on the given OutputStream.
     * @throws IOException if the creation fails due to low-level IO exceptions
     * 
     * @see JsonFactory#createJsonGenerator(OutputStream, JsonEncoding)
     */
    public static CloseableJsonRenderer createJsonRenderer(final OutputStream out, final RenderLevel level)
        throws IOException {

        final JsonGenerator generator = FACTORY.createJsonGenerator(out, DEFAULT_ENCODING);
        return new JsonGeneratorRenderer(generator, level);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that directly writes to the given OutputStream,
     * using the given {@link JsonEncoding} as character encoding.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * OutputStream and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param out the OutputStream to write on
     * @param encoding the character encoding to use for writing on the stream
     * @return a new JsonRenderer that writes its output on the given OutputStream.
     * @throws IOException if the creation fails due to low-level IO exceptions
     * 
     * @see JsonFactory#createJsonGenerator(OutputStream, JsonEncoding)
     */
    public static CloseableJsonRenderer createJsonRenderer(final OutputStream out, final JsonEncoding encoding)
        throws IOException {
        
        final JsonGenerator generator = FACTORY.createJsonGenerator(out, encoding);
        return new JsonGeneratorRenderer(generator);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that directly writes to the given OutputStream,
     * using the given {@link JsonEncoding} as character encoding.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * OutputStream and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param out the OutputStream to write on
     * @param encoding the character encoding to use for writing on the stream
     * @param level the RenderLevel of the JsonRenderer
     * @return a new JsonRenderer that writes its output on the given OutputStream.
     * @throws IOException if the creation fails due to low-level IO exceptions
     * 
     * @see JsonFactory#createJsonGenerator(OutputStream, JsonEncoding)
     */
    public static CloseableJsonRenderer createJsonRenderer(
        final OutputStream out, final JsonEncoding encoding, final RenderLevel level)
        throws IOException {
        
        final JsonGenerator generator = FACTORY.createJsonGenerator(out, encoding);
        return new JsonGeneratorRenderer(generator, level);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that writes the rendered Json into the given {@link Writer}.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * Writer and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param writer a {@link Writer}; the rendered json is written to this Writer
     * @return a new JsonRenderer that writes its output to the given Writer.
     * 
     * @see JsonFactory#createJsonGenerator(Writer)
     */
    public static CloseableJsonRenderer createJsonRenderer(final Writer writer) {
        
        final JsonGenerator generator;
        try {
            generator = FACTORY.createJsonGenerator(writer);
        } catch (final IOException ioe) {
            throw new IllegalStateException("Illegal IOException during factory creation using Writer", ioe);
        }
        return new JsonGeneratorRenderer(generator);
    }
    
    /**
     * <p> Creates a {@link JSONRenderer} that writes the rendered Json into the given {@link Writer}.
     * </p>
     * <p> <strong> Important: </strong> The JsonRenderer that this method returns
     * does not support the toString() method. All data is directly written to the given
     * Writer and flushed after the last endArray() or endObject() call.
     * If you want to manually close it, then cast the returned object to {@link Closeable}
     * and close it.
     * </p>
     * @param writer a {@link Writer}; the rendered json is written to this Writer
     * @param level the RenderLevel of the JsonRenderer
     * @return a new JsonRenderer that writes its output to the given Writer.
     * 
     * @see JsonFactory#createJsonGenerator(Writer)
     */
    public static CloseableJsonRenderer createJsonRenderer(final Writer writer, final RenderLevel level) {
        
        final JsonGenerator generator;
        try {
            generator = FACTORY.createJsonGenerator(writer);
        } catch (final IOException ioe) {
            throw new IllegalStateException("Illegal IOException during factory creation using Writer", ioe);
        }
        return new JsonGeneratorRenderer(generator, level);
    }
    
    /**
     * Creates a new JsonFactory that has only the given features enabled,
     * and every other feature disabled. This overrides the defaults.
     * @param features the {@link JsonGenerator.Feature}s to enable
     * @return a new factory with only the given featues enabled
     */
    public static JsonFactory createFactory(final JsonGenerator.Feature... features) {
        final JsonFactory factory = new JsonFactory();
        
        // disable everything
        for (final JsonGenerator.Feature feature : JsonGenerator.Feature.values()) {
            factory.disable(feature);
        }
        
        // enable given features
        for (final JsonGenerator.Feature feature : features) {
            factory.enable(feature);
        }
        
        return factory;
    }
    
    /**
     * <p> An ImmutableJsonFactory extends {@link JsonFactory}
     * and supports every feature that JsonFactory provides,
     * except the configure, enable and disable methods.
     * In other words: this JsonFactory is always set to the default configuration.
     * </p>
     * 
     * @author Oliver Lorenz
     */
    public static final class ImmutableJsonFactory extends JsonFactory {
        
        @Override
        public JsonFactory disable(final JsonGenerator.Feature f) {
            throw new UnsupportedOperationException("ImmutableJsonFactory cannot be configured");
        }
        
        @Override
        public JsonFactory disable(final JsonParser.Feature f) {
            throw new UnsupportedOperationException("ImmutableJsonFactory cannot be configured");
        };
        
        @Override
        public JsonFactory enable(final JsonGenerator.Feature f) {
            throw new UnsupportedOperationException("ImmutableJsonFactory cannot be configured");
        };
        
        @Override
        public JsonFactory enable(final JsonParser.Feature f) {
            throw new UnsupportedOperationException("ImmutableJsonFactory cannot be configured");
        }
        
    }

}
