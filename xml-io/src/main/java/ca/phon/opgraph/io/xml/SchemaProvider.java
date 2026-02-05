/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.io.xml;

import java.net.URL;
import java.util.List;

/**
 * Service interface for providing XML schema URLs for OpGraph validation.
 * Modules that define extension schemas should implement this interface
 * and declare it via {@code provides} in their module-info.
 */
public interface SchemaProvider {

	/**
	 * Gets the URLs to XML schema files provided by this module.
	 *
	 * @return list of schema URLs
	 */
	List<URL> getSchemaURLs();

}
