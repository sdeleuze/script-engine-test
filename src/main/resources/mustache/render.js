function renderMustache(template, model) {
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
    return Mustache.render(template, data);
}