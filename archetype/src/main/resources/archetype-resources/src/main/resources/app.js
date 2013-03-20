load("vertx.js")

vertx.deployVerticle("${package}.MyVerticle")

console.log("Deploying myverticle")