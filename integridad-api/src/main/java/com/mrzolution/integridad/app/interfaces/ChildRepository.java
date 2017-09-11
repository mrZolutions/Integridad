package com.mrzolution.integridad.app.interfaces;

import java.util.UUID;

/**
 * Created by daniel on 5/5/17.
 */
public interface ChildRepository <F> {

    Iterable<UUID> findByFather(F father);

}
