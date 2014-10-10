package nl.kii.stream.source;

import nl.kii.stream.IStream;

/**
 * A source is a streamable source of information.
 */
@SuppressWarnings("all")
public interface StreamSource<R extends Object, T extends Object> {
  /**
   * Create a new stream and pipe source stream to this stream
   */
  public abstract IStream<R, T> stream();
  
  /**
   * Connect an existing stream as a listener to the source stream
   */
  public abstract StreamSource<R, T> pipe(final IStream<R, T> stream);
}
