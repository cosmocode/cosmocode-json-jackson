package de.cosmocode.json;

import java.io.Closeable;

/**
 * This is a combined interface that extends {@link JSONRenderer} and {@link Closeable}.
 * It defines no further methods.
 * 
 * @author Oliver Lorenz
 * 
 * @see JSONRenderer
 * @see Closeable
 */
public interface CloseableJsonRenderer extends JSONRenderer, Closeable {

}
