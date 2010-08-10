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

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.junit.UnitProvider;
import de.cosmocode.rendering.Renderer;

/**
 * Tests {@link JacksonRenderer}.
 *
 * @author Willi Schoenborn
 */
public final class JacksonRendererTest implements UnitProvider<Renderer> {

    @Override
    public Renderer unit() {
        return new JacksonRenderer();
    }
    
    /**
     * Tests with a simple object.
     * 
     * @since
     */
    @Test
    public void simple() {
        final Renderer unit = unit();
        unit.map().key("key").value("value").endMap();
        Assert.assertEquals("{\"key\":\"value\"}", unit.build());
    }

}
