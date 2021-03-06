package nl.kii.stream.message

/** A command given to a stream. 
 * Commands travel upwards towards the source of a stream, to control the stream.
 */
interface StreamEvent extends StreamMessage { }

/** Request the next entry from the stream */
class Next implements StreamEvent { }

/** Request the stream to stop sending entries until after the next finish entry */
class Skip implements StreamEvent { }

/** Request the stream to close and stop sending */
class Close implements StreamEvent{ }

/** Warns that the buffer is full */
class Overflow implements StreamEvent {
	public val Entry<?,?> entry
	new(Entry<?,?> entry) { this.entry = entry }
}
