package mvm.rya.api;

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

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;

import java.util.ArrayList;
import java.util.Collection;

public class RdfCloudTripleStoreStatement extends StatementImpl {

    private Resource[] contexts; //TODO: no blank nodes

    public RdfCloudTripleStoreStatement(Resource subject, URI predicate, Value object) {
        super(subject, predicate, object);
    }

    public RdfCloudTripleStoreStatement(Resource subject, URI predicate, Value object,
                                        Resource... contexts) {
        super(subject, predicate, object);
        this.contexts = contexts;
    }

    public Resource[] getContexts() {
        return contexts;
    }

    public Collection<Statement> getStatements() {
        Collection<Statement> statements = new ArrayList<Statement>();

        if (getContexts() != null && getContexts().length > 1) {
            for (Resource contxt : getContexts()) {
                statements.add(new ContextStatementImpl(getSubject(),
                        getPredicate(), getObject(), contxt));
            }
        } else
            statements.add(this);

        return statements;
    }

    @Override
    public Resource getContext() {
        if (contexts == null || contexts.length == 0)
            return null;
        else return contexts[0];
    }
}
