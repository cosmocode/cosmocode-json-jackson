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
