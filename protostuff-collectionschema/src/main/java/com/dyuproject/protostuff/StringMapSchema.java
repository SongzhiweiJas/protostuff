//========================================================================
//Copyright 2007-2010 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff;

import java.io.IOException;
import java.util.Map;

/**
 * A utility schema for a {@link Map} with {@link String} keys and single-type object values.
 * Keys cannot be null otherwise the entry is ignored (not serialized).
 * Values however can be null.
 *
 * @author David Yu
 * @created Jun 25, 2010
 */
public class StringMapSchema<V> extends MapSchema<String,V>
{
    
    /**
     * The schema for Map<String,String>
     */
    public static final StringMapSchema<String> VALUE_STRING = new StringMapSchema<String>(null)
    {
        protected void putValueFrom(Input input, MapWrapper<String,String> wrapper, 
                String key) throws IOException
        {
            wrapper.put(key, input.readString());
        }

        protected void writeValueTo(Output output, int fieldNumber, String value, 
                boolean repeated) throws IOException
        {
            output.writeString(fieldNumber, value, repeated);
        }
    };
    
    /**
     * The schema of the message value.
     */
    public final Schema<V> vSchema;
    /**
     * The pipe schema of the message value.
     */
    public final Pipe.Schema<V> vPipeSchema;
    
    public StringMapSchema(Schema<V> vSchema)
    {
        this(vSchema, null);
    }
    
    public StringMapSchema(Schema<V> vSchema, Pipe.Schema<V> vPipeSchema)
    {
        this.vSchema = vSchema;
        this.vPipeSchema = vPipeSchema;
    }

    protected final String readKeyFrom(Input input) throws IOException
    {
        return input.readString();
    }
    
    protected void putValueFrom(Input input, MapWrapper<String,V> wrapper, String key) 
    throws IOException
    {
        wrapper.put(key, input.mergeObject(null, vSchema));
    }

    protected final void writeKeyTo(Output output, int fieldNumber, String value, 
            boolean repeated) throws IOException
    {
        output.writeString(fieldNumber, value, repeated);
    }
    
    protected void writeValueTo(Output output, int fieldNumber, V value, 
            boolean repeated) throws IOException
    {
        output.writeObject(fieldNumber, value, vSchema, repeated);
    }

    protected void transferKey(Pipe pipe, Input input, Output output, int number, 
            boolean repeated) throws IOException
    {
        input.transferByteRangeTo(output, true, number, repeated);
    }

    protected void transferValue(Pipe pipe, Input input, Output output, int number, 
            boolean repeated) throws IOException
    {
        if(vPipeSchema == null)
        {
            throw new RuntimeException("No pipe schema for key: " + 
                    vPipeSchema.typeClass().getName());
        }
        
        output.writeObject(number, pipe, vPipeSchema, repeated);
    }

}