package com.mrzolution.integridad.app.father;

import java.util.List;

/**
 * Created by daniel on 5/9/17.
 */
public class Father<F, C> {

    F dad;
    List<C> children;

    public Father(F dad, List<C> children) {
        this.dad = dad;
        this.children = children;
    }

    public List<C> obtainChildren(){
        return children;
    }

    public F getDad()  {
        return dad;
    }
}
