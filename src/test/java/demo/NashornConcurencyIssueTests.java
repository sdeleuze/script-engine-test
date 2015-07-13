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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.util.StreamUtils;

public class NashornConcurencyIssueTests {

	@BeforeClass
	public static void setup() throws IOException, ScriptException {
		engine = (NashornScriptEngine)new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("META-INF/resources/webjars/mustachejs/0.8.2/mustache.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("META-INF/resources/webjars/underscorejs/1.8.3/underscore.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("mustache/render.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("handlebars/polyfill.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("handlebars/handlebars-3.0.3.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("handlebars/render.js"), StandardCharsets.UTF_8));
		engine.eval(StreamUtils.copyToString(NashornConcurencyIssueTests.class.getClassLoader().getResourceAsStream("underscore/render.js"), StandardCharsets.UTF_8));
	}

	private static String handlebarsTemplate = "<html>\n" +
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

	private static String mustacheTemplate = "<html>\n" +
			"    <head>\n" +
			"        <title>{{title}}</title>\n" +
			"    </head>\n" +
			"    <body>\n" +
			"        <ul>\n" +
			"            {{#comments}}\n" +
			"                <li>{{author}} {{content}}</li>\n" +
			"            {{/comments}}\n" +
			"        </ul>\n" +
			"    </body>\n" +
			"</html>";

	private static String underscoreTemplate = "<html>\n" +
			"    <head>\n" +
			"        <title><%= title %></title>\n" +
			"    </head>\n" +
			"    <body>\n" +
			"        <ul>\n" +
			"<% _.each(comments, function(c) { %>" +
			"                <li><%= c.author %> <%= c.content %></li>\n" +
			"<% }) %>" +
			"        </ul>\n" +
			"    </body>\n" +
			"</html>";

	private static String renderedTemplate = "<html>\n" +
			"    <head>\n" +
			"        <title>Title example</title>\n" +
			"    </head>\n" +
			"    <body>\n" +
			"        <ul>\n" +
			"                <li>author1 content1</li>\n" +
			"                <li>author2 content2</li>\n" +
			"                <li>author3 content3</li>\n" +
			"        </ul>\n" +
			"    </body>\n" +
			"</html>";

	private static NashornScriptEngine engine;

	@Test
	public void mustache() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderMustache", mustacheTemplate, 50, 1);
	}

	@Test
	public void mustacheConcurrently() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderMustache", mustacheTemplate, 50, 2);
	}

	@Test
	public void handlebars() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderHandlebars", handlebarsTemplate, 50, 1);
	}

	@Test
	public void handlebarsConcurrently() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderHandlebars", handlebarsTemplate, 50, 2);
	}

	@Test
	public void handlebarsConcurrentlyWithNewGlobal() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderHandlebarsWithNewGlobal", handlebarsTemplate, 50, 2);
	}

	@Test
	public void underscore() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderUnderscore", underscoreTemplate, 50, 1);
	}

	@Test
	public void underscoreConcurrently() throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		renderTemplates("renderUnderscore", underscoreTemplate, 50, 2);
	}

	private void renderTemplates(String functionName, String template, int iterations, int nThreads) throws ScriptException, NoSuchMethodException, InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		List<Future<String>> results = new ArrayList<Future<String>>();

		for(int i = 0; i < iterations; i++) {
			results.add(executor.submit(() -> {
				try {
					return renderTemplate(functionName, template);
				}
				catch (ScriptException e) {
					return e.getMessage();
				}
				catch (NoSuchMethodException e) {
					return e.getMessage();
				}
			}));
		}

		for(int i = 0; i < iterations; i++) {
			Assert.assertEquals(renderedTemplate, results.get(i).get());
		}
		executor.shutdown();
	}


	private String renderTemplate(String functionName, String template) throws ScriptException, NoSuchMethodException {
		Map<String, Object> map = new HashMap<>();
		map.put("title", "Title example");
		List comments = Arrays.asList(new Comment("author1", "content1"), new Comment("author2", "content2"), new Comment("author3", "content3"));
		map.put("comments", comments);
		return (String) engine.invokeFunction(functionName, template, map);
	}

}
