package example.demo;

import example.demo.verticle.ProcuerVerticle;
import example.demo.verticle.ServerVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@ComponentScan
@SpringBootApplication
public class VertxApplication {

	@Autowired
	private ServerVerticle serverVerticle;

	@Autowired
    private ProcuerVerticle procuerVerticle;


	public static void main(String[] args) {
		SpringApplication.run(VertxApplication.class);
	}

	@PostConstruct
	public void deployServerVerticle() {
        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr);
        options.setClustered(true);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                vertx.deployVerticle(serverVerticle, res2 -> {
                    if (res2.succeeded())
                        System.out.println("Deployed " + serverVerticle.getClass().getName());
                    else
                        res2.cause().printStackTrace();
                });
                vertx.deployVerticle(procuerVerticle, res2 -> {
                    if (res2.succeeded())
                        System.out.println("Deployed " + procuerVerticle.getClass().getName());
                    else
                        res2.cause().printStackTrace();
                });
            } else {
                res.cause().printStackTrace();
            }
        });
	}
}
