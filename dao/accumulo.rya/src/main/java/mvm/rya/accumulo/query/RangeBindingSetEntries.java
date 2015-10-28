package mvm.rya.accumulo.query;

/*
 * #%L
 * mvm.rya.accumulo.rya
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

import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.openrdf.query.BindingSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Class RangeBindingSetCollection
 * Date: Feb 23, 2011
 * Time: 10:15:48 AM
 */
public class RangeBindingSetEntries {
    public Collection<Map.Entry<Range, BindingSet>> ranges;

    public RangeBindingSetEntries() {
        this(new ArrayList<Map.Entry<Range, BindingSet>>());
    }

    public RangeBindingSetEntries(Collection<Map.Entry<Range, BindingSet>> ranges) {
        this.ranges = ranges;
    }

    public Collection<BindingSet> containsKey(Key key) {
        //TODO: need to find a better way to sort these and pull
        //TODO: maybe fork/join here
        Collection<BindingSet> bss = new ArrayList<BindingSet>();
        for (Map.Entry<Range, BindingSet> entry : ranges) {
            if (entry.getKey().contains(key))
                bss.add(entry.getValue());
        }
        return bss;
    }
}
