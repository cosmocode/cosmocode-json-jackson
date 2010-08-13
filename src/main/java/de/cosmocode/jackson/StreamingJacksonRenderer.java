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
