package ca.gedge.opgraph;

/**
 * <p>Receives events from {@link Processor}s.  An event is triggered:
 * <ul>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * </p>
 */
@FunctionalInterface
public interface ProcessorListener {

	public void processorEvent(ProcessorEvent pe);

}
