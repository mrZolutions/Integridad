package com.mrzolution.integridad.app.conf;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by daniel on 4/24/17.
 */

@EnableJpaRepositories(basePackages = "com.mrzolutions.integridad.app.repositories")
@EntityScan(basePackages = "com.mrzolutions.integridad.app.domain")
class PersistenceContext {
}