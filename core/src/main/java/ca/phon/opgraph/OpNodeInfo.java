/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to describe an {@link OpNode}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OpNodeInfo {
	/** Name of the node */
	public String name();

	/** Short description for the node */
	public String description();

	/** A category for the node */ 
	public String category() default "";
	
	/** Control displaying node in library */
	public boolean showInLibrary() default true;
	
}