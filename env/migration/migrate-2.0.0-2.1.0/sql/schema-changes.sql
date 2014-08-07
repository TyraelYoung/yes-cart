--
--  Copyright 2009 Igor Azarnyi, Denys Pavlov
--
--     Licensed under the Apache License, Version 2.0 (the "License");
--     you may not use this file except in compliance with the License.
--     You may obtain a copy of the License at
--
--         http://www.apache.org/licenses/LICENSE-2.0
--
--     Unless required by applicable law or agreed to in writing, software
--     distributed under the License is distributed on an "AS IS" BASIS,
--     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--     See the License for the specific language governing permissions and
--     limitations under the License.
--

--
-- This script is for MySQL only with some Derby hints inline with comments
-- We highly recommend you seek YC's support help when upgrading your system
-- for detailed analysis of your code.
--
-- Upgrades organised in blocks representing JIRA tasks for which they are
-- necessary - potentially you may hand pick the upgrades you required but
-- to keep upgrade process as easy as possible for future we recommend full
-- upgrades
--


--
-- YC-379 Create and add wish list Items. Wish list items modifications to support price, tags and quantity
--

alter table TCUSTOMERWISHLIST add column TAG varchar(255);
alter table TCUSTOMERWISHLIST add column QTY decimal(19,2) not null default 1;
alter table TCUSTOMERWISHLIST add column REGULAR_PRICE_WHEN_ADDED decimal(19,2) not null default 0;
alter table TCUSTOMERWISHLIST add column VISIBILITY varchar(1) default 'P';
-- Deby uses numberic
-- alter table TCUSTOMERWISHLIST add column TAG varchar(255);
-- alter table TCUSTOMERWISHLIST add column QTY numeric(19,2) not null default 1;
-- alter table TCUSTOMERWISHLIST add column REGULAR_PRICE_WHEN_ADDED numeric(19,2) not null default 0;
-- alter table TCUSTOMERWISHLIST add column VISIBILITY varchar(1) default 'P';


--
-- YC-XXX coupon codes
--

alter table TPROMOTION add column COUPON_TRIGGERED bit not null default 0;

create table TPROMOTIONCOUPON (
    PROMOTIONCOUPON_ID bigint not null auto_increment,
    VERSION bigint not null default 0,
    CODE varchar(255) not null unique,
    PROMOTION_ID bigint not null,
    USAGE_LIMIT integer default 1,
    USAGE_LIMIT_PER_CUSTOMER integer default 1,
    USAGE_COUNT integer default 0,
    CREATED_TIMESTAMP datetime,
    UPDATED_TIMESTAMP datetime,
    CREATED_BY varchar(64),
    UPDATED_BY varchar(64),
    primary key (PROMOTIONCOUPON_ID)
);


create table TPROMOTIONCOUPONUSAGE (
    PROMOTIONCOUPONUSAGE_ID bigint not null auto_increment,
    VERSION bigint not null default 0,
    CUSTOMER_EMAIL varchar(255) not null,
    COUPON_ID bigint not null,
    CUSTOMERORDER_ID bigint not null,
    CREATED_TIMESTAMP datetime,
    UPDATED_TIMESTAMP datetime,
    CREATED_BY varchar(64),
    UPDATED_BY varchar(64),
    primary key (PROMOTIONCOUPONUSAGE_ID)
);

alter table TPROMOTIONCOUPON
    add index FK_PROMO_COUPON (PROMOTION_ID),
    add constraint FK_PROMO_COUPON
    foreign key (PROMOTION_ID)
    references TPROMOTION (PROMOTION_ID);

alter table TPROMOTIONCOUPONUSAGE
    add index FK_COUPON_USAGE (COUPON_ID),
    add constraint FK_COUPON_USAGE
    foreign key (COUPON_ID)
    references TPROMOTIONCOUPON (PROMOTIONCOUPON_ID);

alter table TPROMOTIONCOUPONUSAGE
    add index FK_ORD_COUPON_USAGE (CUSTOMERORDER_ID),
    add constraint FK_ORD_COUPON_USAGE
    foreign key (CUSTOMERORDER_ID)
    references TCUSTOMERORDER (CUSTOMERORDER_ID) on delete cascade;

-- Derby
-- alter table TPROMOTION add column COUPON_TRIGGERED smallint not null default 0;
--     create table TPROMOTIONCOUPON (
--         PROMOTIONCOUPON_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         CODE varchar(255) not null unique,
--         PROMOTION_ID bigint not null,
--         USAGE_LIMIT integer default 1,
--         USAGE_LIMIT_PER_CUSTOMER integer default 1,
--         USAGE_COUNT integer default 0,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         primary key (PROMOTIONCOUPON_ID)
--     );
--
--
--     create table TPROMOTIONCOUPONUSAGE (
--         PROMOTIONCOUPONUSAGE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
--         VERSION bigint not null default 0,
--         CUSTOMER_EMAIL varchar(255) not null,
--         COUPON_ID bigint not null,
--         CUSTOMERORDER_ID bigint not null,
--         CREATED_TIMESTAMP timestamp,
--         UPDATED_TIMESTAMP timestamp,
--         CREATED_BY varchar(64),
--         UPDATED_BY varchar(64),
--         primary key (PROMOTIONCOUPONUSAGE_ID)
--     );
--
--     alter table TPROMOTIONCOUPON
--         add constraint FK_PROMO_COUPON
--         foreign key (PROMOTION_ID)
--         references TPROMOTION;
--
--     alter table TPROMOTIONCOUPONUSAGE
--         add constraint FK_COUPON_USAGE
--         foreign key (COUPON_ID)
--         references TPROMOTIONCOUPON;
--
--     alter table TPROMOTIONCOUPONUSAGE
--         add constraint FK_ORD_COUPON_USAGE
--         foreign key (CUSTOMERORDER_ID)
--         references TCUSTOMERORDER
--         on delete cascade;


--
-- YC-388 missing Issue number on payment table
--

alter table TCUSTOMERORDERPAYMENT add column CARD_ISSUE_NUMBER varchar(4);

--
-- YC-389 Upgrade LiqPay PG to v 2
--


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (460, 'liqPayNoRefundPaymentGateway',
'LP_MERCHANT_ID',
'!!!PROVIDE VALUE!!!'
, 'Merchant Id', 'Merchant Id');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (461, 'liqPayNoRefundPaymentGateway',
'LP_MERCHANT_KEY',
'!!!PROVIDE VALUE!!!'
, 'Merchant signature', 'Merchant signature');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (462, 'liqPayNoRefundPaymentGateway',
'LP_RESULT_URL',
'http://@domain@/yes-shop/liqpayreturn'
, 'Page URL to show payment result', 'Page URL to show payment result');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (463, 'liqPayNoRefundPaymentGateway',
'LP_SERVER_URL',
'http://@domain@/yes-shop/liqpaycallback'
, 'Call back URL with payment result.', 'Call back URL with payment result. ');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (464, 'liqPayNoRefundPaymentGateway',
'LP_POST_URL',
'https://www.liqpay.com/api/'
, 'Form post url', 'Form post url');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (465, 'liqPayNoRefundPaymentGateway',
'LP_PAYWAY_URL',
'card'
, 'Payment method - card, liqpay', 'Payment method - card, liqpay');


INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (466, 'liqPayNoRefundPaymentGateway', 'name', 'LiqPay (No Refund)', 'Gateway name (default)', 'Gateway name (default)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (467, 'liqPayNoRefundPaymentGateway', 'name_en', 'LiqPay (No Refund)', 'Gateway name (EN)', 'Gateway name (EN)');

INSERT INTO TPAYMENTGATEWAYPARAMETER (PAYMENTGATEWAYPARAMETER_ID, PG_LABEL, P_LABEL, P_VALUE, P_NAME, P_DESCRIPTION)
VALUES (468, 'liqPayNoRefundPaymentGateway', 'name_ru', 'LiqPay (Без возвратов)', 'Название платежного шлюза (RU)', 'Название платежного шлюза (RU)');


UPDATE TSYSTEMATTRVALUE SET VAL = 'testPaymentGatewayLabel,courierPaymentGatewayLabel,cyberSourcePaymentGatewayLabel,authorizeNetAimPaymentGatewayLabel,authorizeNetSimPaymentGatewayLabel,payflowPaymentGatewayLabel,payPalNvpPaymentGatewayLabel,payPalExpressPaymentGatewayLabel,liqPayPaymentGatewayLabel,liqPayNoRefundPaymentGatewayLabel' WHERE ATTRVALUE_ID = 1012;

