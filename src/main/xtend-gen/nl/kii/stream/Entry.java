package nl.kii.stream;

import nl.kii.stream.StreamMessage;

/**
 * An entry is a stream message that contains either a value or stream state information.
 * Entries travel downwards a stream towards the listeners of the stream at the end.
 */
@SuppressWarnings("all")
public interface Entry<I extends Object, O extends Object> extends StreamMessage {
}
