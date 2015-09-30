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

package org.yes.cart.service.domain.impl;

import org.hibernate.criterion.Restrictions;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.State;
import org.yes.cart.service.domain.StateService;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class StateServiceImpl extends BaseGenericServiceImpl<State> implements StateService {
    /**
     * Construct Service.
     * @param genericDao dao to use.
     */
    public StateServiceImpl(final GenericDAO<State, Long> genericDao) {
        super(genericDao);
    }

    public List<State> findByCountry(final String countryCode) {

        return getGenericDao().findByCriteria(
                Restrictions.eq("countryCode", countryCode));

    }

}