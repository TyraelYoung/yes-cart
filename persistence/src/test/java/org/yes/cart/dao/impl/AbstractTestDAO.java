/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.dao.impl;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.FileOutputStream;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 16:13:01
 */
public abstract class AbstractTestDAO  {

    // Do not enable dump unless this is necessary as it is very slow.
    private static final boolean ENABLED_DUMPS = true;

    private ApplicationContext ctx;
    private SessionFactory sessionFactory;
    private Session session;
    private AbstractDatabaseTester dbTester;

    private PlatformTransactionManager transactionManager;
    private TransactionTemplate tx;

    @Before
    public void setUp()   {
        transactionManager =   ctx().getBean("transactionManager", PlatformTransactionManager.class);
        tx = new TransactionTemplate(transactionManager);
    }

    public TransactionTemplate getTx() {
        return tx;
    }

    @Rule
    public ExternalResource dbResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            ctx = createContext();
            sessionFactory = (SessionFactory) ctx().getBean("sessionFactory");
            session = sessionFactory.openSession();
            dbTester = createDatabaseTester();
            try {
                dbTester.onSetup();
            } catch (Exception e) {
                throw e;
            }
        }

        @Override
        protected void after() {
            try {
                dbTester.onTearDown();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //sessionFactory.close();
            session.close();
        }
    };

    public ApplicationContext ctx() {
        return ctx;
    }

    protected ApplicationContext createContext() {
        return new ClassPathXmlApplicationContext("testApplicationContext.xml");
    }

    protected AbstractDatabaseTester createDatabaseTester() throws Exception {
        AbstractDatabaseTester dbTester = new JdbcDatabaseTester("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testyesdb", "sa", "");
        dbTester.setSetUpOperation(DatabaseOperation.REFRESH);
        dbTester.setTearDownOperation(DatabaseOperation.NONE);
        dbTester.setDataSet(createDataSet());
        return dbTester;
    }

    protected IDataSet createDataSet() throws Exception {
        final FlatXmlDataSet dataSet = new FlatXmlDataSetBuilder()
                //.setColumnSensing(true) // This enables auto discovery of columns but breaks the tests, possibly our data needs clean up
                .build(getClass().getClassLoader().getResourceAsStream(createDataSetFile()));
        final ReplacementDataSet rDataSet = new ReplacementDataSet(dataSet);
        rDataSet.addReplacementObject("[NULL]", null);
        return rDataSet;
    }

    protected String createDataSetFile() {
        return "initialdata.xml";
    }

    protected void dumpDataBase(final String prefix, final String ... tables) throws Exception {

        if (!ENABLED_DUMPS) {
            System.out.println("DUMP: [DISABLED] - change parameter in AbstractTestDAO.ENABLED_DUMPS");
            return;
        }

        QueryDataSet queryDataSet = new QueryDataSet(dbTester.getConnection());
        for (String tableName : tables) {
            queryDataSet.addTable(tableName);
        }
        FileOutputStream fos = null;
        try {
            final File dump = new File("target/test-classes/" + prefix + "_dataset.xml");
            if (!dump.exists()) {
                dump.createNewFile();
            }
            System.out.println("DUMP: " + dump.getAbsoluteFile());
            fos = new FileOutputStream(dump);
            FlatXmlDataSet.write(queryDataSet, fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

    }

    /**
     * Get db connection to perform verifications.
     *
     * @return db connection
     * @throws Exception in case or error
     */
    public IDatabaseConnection getConnection() throws Exception {
        return dbTester.getConnection();
    }
}
