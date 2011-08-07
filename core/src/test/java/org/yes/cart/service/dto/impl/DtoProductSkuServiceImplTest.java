package org.yes.cart.service.dto.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.*;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.SkuPriceDTOImpl;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.impl.BaseCoreDBTestCase;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoProductService;
import org.yes.cart.service.dto.DtoProductSkuService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class DtoProductSkuServiceImplTest extends BaseCoreDBTestCase {


    private DtoProductSkuService dtoService = null;
    private DtoProductService dtoProductService = null;
    private DtoFactory dtoFactory = null;
    private DtoAttributeService dtoAttrService = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dtoService = (DtoProductSkuService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_SKU_SERVICE);
        dtoProductService = (DtoProductService) ctx.getBean(ServiceSpringKeys.DTO_PRODUCT_SERVICE);
        dtoFactory = (DtoFactory) ctx.getBean(ServiceSpringKeys.DTO_FACTORY);
        dtoAttrService  = (DtoAttributeService) ctx.getBean(ServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
    }

    @After
    public void tearDown() {
        dtoService = null;
        dtoProductService = null;
        dtoFactory = null;
        dtoAttrService = null;
        super.tearDown();
    }

    @Test
    public void testCreate() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testUpdate() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);

            long pk = dto.getSkuId();
            dto.setName("new name");
            dto.setDescription("new description");
            dto.setBarCode("233456");            
            dto = dtoService.update(dto);
            assertEquals("new name", dto.getName());
            assertEquals("new description", dto.getDescription());
            assertEquals("233456", dto.getBarCode());

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }

    /**
     * Add price.
     */
    @Test
    public void testCreateSkuPrice() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);

            long pk = dto.getSkuId();

            SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
            skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
            skuPriceDTO.setProductSkuId(pk);
            skuPriceDTO.setShopId(10L);
            skuPriceDTO.setCurrency("EUR");
            skuPriceDTO.setQuantity(BigDecimal.ONE);

            dtoService.createSkuPrice(skuPriceDTO);
            dto = dtoService.getById(pk);

            assertEquals(1, dto.getPrice().size());
            assertEquals("EUR", dto.getPrice().iterator().next().getCurrency());
            assertEquals(10L, dto.getPrice().iterator().next().getShopId());
            assertTrue((new BigDecimal("1.23")).equals(dto.getPrice().iterator().next().getRegularPrice()));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }
    /**
     * Add price.
     */
    @Test
    public void testDeleteSkuPrice() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);

            long pk = dto.getSkuId();

            SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
            skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
            skuPriceDTO.setProductSkuId(pk);
            skuPriceDTO.setShopId(10L);
            skuPriceDTO.setCurrency("EUR");
            skuPriceDTO.setQuantity(BigDecimal.ONE);

            dtoService.createSkuPrice(skuPriceDTO);

            dto = dtoService.getById(pk);

            assertEquals(1, dto.getPrice().size());
            assertEquals("EUR", dto.getPrice().iterator().next().getCurrency());
            assertEquals(10L, dto.getPrice().iterator().next().getShopId());
            assertTrue((new BigDecimal("1.23")).equals(dto.getPrice().iterator().next().getRegularPrice()));

            dtoService.removeSkuPrice(dto.getPrice().iterator().next().getSkuPriceId());
            dto = dtoService.getById(pk);
            assertTrue(dto.getPrice().isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }

    /**
     * Update sku  price.
     */
    @Test
    public void testUpdateSkuPrice() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);

            long pk = dto.getSkuId();

            SkuPriceDTO skuPriceDTO = new SkuPriceDTOImpl();
            skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
            skuPriceDTO.setProductSkuId(pk);
            skuPriceDTO.setShopId(10L);
            skuPriceDTO.setCurrency("EUR");
            skuPriceDTO.setQuantity(BigDecimal.ONE);

            dtoService.createSkuPrice(skuPriceDTO);

            dto = dtoService.getById(pk);

            skuPriceDTO = dto.getPrice().iterator().next();

            assertEquals(1, dto.getPrice().size());
            assertEquals("EUR", skuPriceDTO.getCurrency());
            assertEquals(10L, skuPriceDTO.getShopId());
            assertTrue((new BigDecimal("1.23")).equals(skuPriceDTO.getRegularPrice()));


            Date date = new Date();
            skuPriceDTO.setRegularPrice(new BigDecimal("2.34"));
            skuPriceDTO.setSalePrice(new BigDecimal("2.33"));
            skuPriceDTO.setMinimalPrice(new BigDecimal("2.32"));
            skuPriceDTO.setSalefrom(date);
            skuPriceDTO.setSaletill(date);


            dtoService.updateSkuPrice(skuPriceDTO);
            dto = dtoService.getById(pk);

            skuPriceDTO = dto.getPrice().iterator().next();

            assertEquals(1, dto.getPrice().size());
            assertEquals("EUR", skuPriceDTO.getCurrency());
            assertEquals(10L, skuPriceDTO.getShopId());
            assertTrue((new BigDecimal("2.34")).equals(skuPriceDTO.getRegularPrice()));
            assertTrue((new BigDecimal("2.33")).equals(skuPriceDTO.getSalePrice()));
            assertTrue((new BigDecimal("2.32")).equals(skuPriceDTO.getMinimalPrice()));


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }


    @Test
    public void testCreateEntityAttributeValue() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);
            AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
            attrValueDTO.setAttributeDTO(dtoAttrService.getById(200L)); //SKUIMAGE0
            attrValueDTO.setSkuId(dto.getSkuId());
            attrValueDTO.setVal("image.jpg");
            dtoService.createEntityAttributeValue(attrValueDTO);
            dto = dtoService.getById(dto.getSkuId());
            assertFalse(dto.getAttribute().isEmpty());
            assertEquals("image.jpg", dto.getAttribute().iterator().next().getVal());            

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }


    @Test
    public void testGetEntityAttributes() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);
            AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
            attrValueDTO.setAttributeDTO(dtoAttrService.getById(200L)); //SKUIMAGE0
            attrValueDTO.setSkuId(dto.getSkuId());
            attrValueDTO.setVal("image.jpg");
            dtoService.createEntityAttributeValue(attrValueDTO);
            List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(dto.getSkuId());
            assertFalse(list.isEmpty());
            assertEquals(7, list.size()); // 7 images
            assertEquals("image.jpg", list.iterator().next().getVal());            

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }

    @Test
    public void testUpdateEntityAttributeValue() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);
            AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
            attrValueDTO.setAttributeDTO(dtoAttrService.getById(200L)); //SKUIMAGE0
            attrValueDTO.setSkuId(dto.getSkuId());
            attrValueDTO.setVal("image.jpg");
            dtoService.createEntityAttributeValue(attrValueDTO);
            dto = dtoService.getById(dto.getSkuId());
            assertFalse(dto.getAttribute().isEmpty());
            assertEquals("image.jpg", dto.getAttribute().iterator().next().getVal());
            dto.getAttribute().iterator().next().setVal("image2.jpeg");
            dtoService.updateEntityAttributeValue(dto.getAttribute().iterator().next());
            dto = dtoService.getById(dto.getSkuId());
            assertFalse(dto.getAttribute().isEmpty());
            assertEquals("image2.jpeg", dto.getAttribute().iterator().next().getVal());


        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }

    }


    @Test
    public void testDeleteAttributeValue() {
        try {
            ProductSkuDTO dto = getDto();
            dto = dtoService.create(dto);
            assertTrue(dto.getSkuId() > 0);
            AttrValueProductSkuDTO attrValueDTO = dtoFactory.getByIface(AttrValueProductSkuDTO.class);
            attrValueDTO.setAttributeDTO(dtoAttrService.getById(200L)); //SKUIMAGE0
            attrValueDTO.setSkuId(dto.getSkuId());
            attrValueDTO.setVal("image.jpg");
            dtoService.createEntityAttributeValue(attrValueDTO);
            dto = dtoService.getById(dto.getSkuId());
            assertFalse(dto.getAttribute().isEmpty());
            assertEquals("image.jpg", dto.getAttribute().iterator().next().getVal());
            dtoService.deleteAttributeValue(dto.getAttribute().iterator().next().getAttrvalueId());
            dto = dtoService.getById(dto.getSkuId());
            assertTrue(dto.getAttribute().isEmpty());

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void testGetAllProductSkus() {
        try {
            List<ProductSkuDTO> list = dtoService.getAllProductSkus(10000L);
            assertEquals(4, list.size());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }


    }

    private ProductSkuDTO getDto() throws UnableToCreateInstanceException, UnmappedInterfaceException {
        ProductSkuDTO dto = dtoService.getNew();
        ProductDTO productDto = dtoProductService.getById(9999);
        dto.setProductId(productDto.getProductId());
        dto.setBarCode("123123");
        dto.setCode("BENDER-V4");
        dto.setName("Bender version v");
        dto.setDescription("Bender version v");
        dto.setRank(5);
        return dto;
    }

}