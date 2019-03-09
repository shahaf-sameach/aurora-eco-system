package example.demo.verticle;

import example.demo.config.ApplicationConfiguration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ServerVerticle extends AbstractVerticle {

    @Autowired
    private ApplicationConfiguration applicationConfiguration;

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(router()::accept).listen(Integer.parseInt(applicationConfiguration.port()), res -> {
            if (res.succeeded()) {
                log.info("Server is online, listening on port " + applicationConfiguration.port() );
            } else {
                log.info("Server Failed to bind!");
            }
        });
    }

    private Router router() {
        Router router = Router.router(vertx);

        router.route("/msg").handler(routingContext -> {
            String msg = routingContext.request().getParam("payload");
            HttpServerResponse response = routingContext.response();

            if (msg == null)
                response.setStatusCode(400).end();
            else {
                vertx.eventBus().publish("events", msg);
                log.info("published " + msg);
                response.setStatusCode(200).end();
            }
        });

        return router;
    }


}
