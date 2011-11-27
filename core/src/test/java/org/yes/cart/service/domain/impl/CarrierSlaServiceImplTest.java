package org.yes.cart.service.domain.impl;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Carrier;
import org.yes.cart.service.domain.CarrierService;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CarrierSlaServiceImplTest extends BaseCoreDBTestCase {

    private CarrierService carrierService;

    protected IDataSet createDataSet() throws Exception {
        return new FlatXmlDataSet(getClass().getClassLoader().getResourceAsStream("initialdata_carrier.xml"), false);
    }

    @Before
    public void setUp() throws Exception {
        carrierService = (CarrierService) ctx().getBean(ServiceSpringKeys.CARRIER_SERVICE);
    }

    @Test
    public void testFindCarriersFilterByCurrency() {
        List<Carrier> carriers = carrierService.findCarriers(null, null, null, "RUB");
        assertEquals(1, carriers.size());
        assertEquals(1, carriers.get(0).getCarrierSla().size());
        carriers = carrierService.findCarriers(null, null, null, "MWZ"); // no one of carriers use web money
    }
}