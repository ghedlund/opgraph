package ca.phon.opgraph.app.commands;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for command hooks.  This annotation should
 * declare the type of {@link HookableCommand} to which
 * the hook will attach.
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hook {

	public Class<? extends HookableCommand> command();
	
}
