package com.fitforge; // root package — @ComponentScan starts here and walks down, so every sub-package (controller, service, domain, factory, repository, strategy) gets scanned.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// one annotation = three in one:
//   @Configuration         — this class can define beans.
//   @EnableAutoConfiguration — spring looks at the classpath and wires infra automatically
//                             (starter-web -> embedded tomcat + thymeleaf; starter-data-jpa + h2 -> DataSource, repo proxies).
//   @ComponentScan         — finds every @Component/@Service/@Controller/@Repository under com.fitforge.
@SpringBootApplication
public class FitForgeApplication {
    public static void main(String[] args) {
        // boots everything: builds the bean container, scans components, wires dependencies,
        // starts embedded tomcat on :8080, and runs hibernate's schema generation.
        // first arg = the "primary source" spring uses to find config (by convention, this class itself).
        // args = command-line flags like --server.port=9090 can override properties.
        SpringApplication.run(FitForgeApplication.class, args);
    }
}
