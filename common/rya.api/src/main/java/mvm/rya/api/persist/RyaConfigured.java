package mvm.rya.api.persist;

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

import mvm.rya.api.RdfCloudTripleStoreConfiguration;

/**
 * Date: 7/17/12
 * Time: 8:24 AM
 */
public interface RyaConfigured<C extends RdfCloudTripleStoreConfiguration> {

    public void setConf(C conf);

    public C getConf();
}
