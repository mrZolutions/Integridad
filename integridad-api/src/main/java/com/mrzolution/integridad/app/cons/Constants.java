package com.mrzolution.integridad.app.cons;

/**
 * Created by daniel
 */
public final class Constants {
    public static final String USER_TYPE_EMP_CODE = "EMP";
    public static final String USER_TYPE_ADM_CODE = "ADM";
    public static final String USER_TYPE_SAD_CODE = "SAD";
    
    public static final String ID_TYPE_RUC = "RUC";
    public static final String ID_TYPE_CEDULA = "CED";
    public static final String ID_TYPE_PASAPORTE = "PAS";
    public static final String ID_TYPE_IDENTIFICACION_DEL_EXTERIOR = "IEX";

    public static final String DATIL_LINK = "https://link.datil.co/invoices/issue";
    public static final String DATIL_RETENTION_LINK = "https://link.datil.co/retentions/issue";
    public static final String DATIL_CREDIT_NOTE_LINK = "https://link.datil.co/credit-notes/issue";

//    public static final String FACTURACION_LINK_AUTH = "http://localhost:3600/auth";
//    public static final String FACTURACION_LINK = "http://localhost:3600/send";

    public static final String FACTURACION_LINK_AUTH = "https://invoicesmrz.herokuapp.com/auth";
    public static final String FACTURACION_LINK = "https://invoicesmrz.herokuapp.com/invoices";
    public static final String RETENTION_LINK = "https://invoicesmrz.herokuapp.com/retentions";
}