package mvm.rya.api.persist.query.join;

/*
 * #%L
 * mvm.rya.rya.api
 * %%
 * Copyright (C) 2014 Rya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import info.aduna.iteration.CloseableIteration;
import mvm.rya.api.RdfCloudTripleStoreConfiguration;
import mvm.rya.api.domain.RyaStatement;
import mvm.rya.api.domain.RyaType;
import mvm.rya.api.domain.RyaURI;
import mvm.rya.api.persist.RyaDAOException;

import java.util.Map;

/**
 * Date: 7/24/12
 * Time: 4:28 PM
 */
public interface Join<C extends RdfCloudTripleStoreConfiguration> {

    CloseableIteration<RyaStatement, RyaDAOException> join(C conf, RyaURI... preds)
            throws RyaDAOException;

    CloseableIteration<RyaURI, RyaDAOException> join(C conf, Map.Entry<RyaURI, RyaType>... predObjs)
                    throws RyaDAOException;
}
