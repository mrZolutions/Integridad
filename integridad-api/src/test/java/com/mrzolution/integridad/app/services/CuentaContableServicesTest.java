package com.mrzolution.integridad.app.services;

import com.mrzolution.integridad.app.domain.CuentaContable;
import com.mrzolution.integridad.app.repositories.CuentaContableRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CuentaContableServicesTest {

    @InjectMocks
    CuentaContableServices cuentaContableServices;

    @Mock
    CuentaContableRepository cuentaContableRepository;

    CuentaContable cuentaContable;

    @Before
    public void setupTest () { cuentaContable = CuentaContable.newCuentaContableTest();}

    @Test
    public void getAll() {
        List<CuentaContable> cuentaContableList = new ArrayList<CuentaContable>();

        when(cuentaContableRepository.findAll()).thenReturn(cuentaContableList);
        cuentaContableServices.getAll();
        verify(cuentaContableRepository,times(1)).findAll();
    }

}
