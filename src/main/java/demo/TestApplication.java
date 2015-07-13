package demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StreamUtils;

@SpringBootApplication
public class TestApplication {

    @Bean
    public NashornScriptEngine engine() throws IOException, ScriptException {
        NashornScriptEngine engine = (NashornScriptEngine)new ScriptEngineManager().getEngineByName("nashorn");
        engine.eval(StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("META-INF/resources/webjars/mustachejs/0.8.2/mustache.js"), StandardCharsets.UTF_8));
        engine.eval(StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("mustache/render.js"), StandardCharsets.UTF_8));
        engine.eval(StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("handlebars/polyfill.js"), StandardCharsets.UTF_8));
        engine.eval(StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("handlebars/handlebars-3.0.3.js"), StandardCharsets.UTF_8));
        engine.eval(StreamUtils.copyToString(getClass().getClassLoader().getResourceAsStream("handlebars/render.js"), StandardCharsets.UTF_8));
        return engine;
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
