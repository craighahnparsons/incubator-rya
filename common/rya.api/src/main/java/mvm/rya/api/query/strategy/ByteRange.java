package mvm.rya.api.query.strategy;

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

/**
 * Date: 1/10/13
 * Time: 12:47 PM
 */
public class ByteRange {

    private byte[] start;
    private byte[] end;

    public ByteRange(byte[] start, byte[] end) {
        this.start = start;
        this.end = end;
    }

    public byte[] getStart() {
        return start;
    }

    public byte[] getEnd() {
        return end;
    }
}
