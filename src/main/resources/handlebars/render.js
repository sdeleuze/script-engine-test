function renderHandlebars(template, model) {

    // Create a real Javascript Object from the model Map 
    var data = {};
    for (var k in model) {
        // Convert Java Iterable and List to real Javascript arrays 
        if (model[k] instanceof Java.type("java.lang.Iterable")) {
            data[k] = Java.from(model[k]);
        } else {
            data[k] = model[k];
        }
    }
    // TODO Manage compiled template cache
    return Handlebars.compile(template).call(this, data);
}

function renderHandlebarsWithNewGlobal(template, model) {

    return loadWithNewGlobal({
        name: "render",
        script: "var template = arguments[0];" +
        "var model = arguments[1];" +
        "Handlebars = arguments[2];" +
        "var data = {};" +
        "for (var k in model) {" +
        "if (model[k] instanceof Java.type('java.lang.Iterable')) {" +
        "data[k] = Java.from(model[k]);" +
        "} else {" +
        "data[k] = model[k];" +
        "}" +
        "}" +
        "Handlebars.compile(template).call(this, data);"}, template, model, Handlebars);

}

