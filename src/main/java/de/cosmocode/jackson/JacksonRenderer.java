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
import java.io.StringWriter;
import java.io.Writer;

import org.codehaus.jackson.JsonGenerator;

import de.cosmocode.rendering.Renderer;
import de.cosmocode.rendering.RenderingException;

/**
 * Jackson based {@link Renderer} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class JacksonRenderer extends ForwardingJacksonRenderer {

    private final Writer writer = new StringWriter();
    private final JsonGenerator generator;
    
    public JacksonRenderer() {
        try {
            this.generator = FACTORY.createJsonGenerator(writer);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
    
    @Override
    protected JsonGenerator generator() {
        return generator;
    }
    
    @Override
    public String build() throws RenderingException {
        if (generator.isClosed()) {
            return writer.toString();
        } else {
            try {
                generator.close();
            } catch (IOException e) {
                throw new RenderingException(e);
            }
            return writer.toString();
        }
    }

}
