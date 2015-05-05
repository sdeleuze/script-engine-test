/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/handlebars")
public class HandlebarsController {

	private static String template = "<html>\n" +
			"    <head>\n" +
			"        <title>{{title}}</title>\n" +
			"    </head>\n" +
			"    <body>\n" +
			"        <ul>\n" +
			"            {{#each comments}}\n" +
			"                <li>{{author}} {{content}}</li>\n" +
			"            {{/each}}\n" +
			"        </ul>\n" +
			"    </body>\n" +
			"</html>";

	private NashornScriptEngine engine;

	@Autowired
	public HandlebarsController(NashornScriptEngine engine) {
		this.engine = engine;
	}

	@RequestMapping("/")
	String test() throws ScriptException, NoSuchMethodException {
		Map<String, Object> map = new HashMap<>();
		map.put("title", "Title example");
		List comments = Arrays.asList(new Comment("author1", "content1"), new Comment("author2", "content2"), new Comment("author3", "content3"));
		map.put("comments", comments);
		return (String)this.engine.invokeFunction("renderHandlebars", template, map);
	}

	@RequestMapping("/newglobal")
	String testNewGlobal() throws ScriptException, NoSuchMethodException {
		Map<String, Object> map = new HashMap<>();
		map.put("title", "Title example");
		List comments = Arrays.asList(new Comment("author1", "content1"), new Comment("author2", "content2"), new Comment("author3", "content3"));
		map.put("comments", comments);
		return (String)this.engine.invokeFunction("renderHandlebarsWithNewGlobal", template, map);
	}

}
