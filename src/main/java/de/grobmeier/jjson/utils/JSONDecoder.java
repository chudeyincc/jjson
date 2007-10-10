/*
 *  Copyright 2007 Christian Grobmeier 
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed 
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 *  either express or implied. See the License for the specific 
 *  language governing permissions and limitations under the License.
 */
package de.grobmeier.jjson.utils;

import de.grobmeier.jjson.JSONObject;
import de.grobmeier.jjson.JSONString;
import de.grobmeier.jjson.JSONValue;

public class JSONDecoder {
    private JSONReader reader = null;
    /**
     * 
     */
    public JSONDecoder(final String json) {
        reader = new JSONReader(json);
    }
    
   
    enum Opener {
        jsonobject('{'), jsonarray('['), jsonstring('"');
        
        private char sign = ' ';
        
        private Opener(char s) {
            this.sign = s;
        }
    }
    
    enum Closer {
        jsonobject('}'), jsonarray(']'), jsonstring('"');
        
        private char sign = ' ';
        
        private Closer(char s) {
            this.sign = s;
        }
    }

    /**
     * 
     */
    private class JSONReader {
        private char[] json = null;
        private int index = 0;
        /**
         * @param _json
         */
        public JSONReader(final String _json) {
            json = _json.trim().toCharArray();
        }
        
        /**
         * Reads one character and points to the next sign.
         * @return the read character
         */
        public char read() {
            char result = ' ';
            if(json.length != 0) {
                result = json[index];
            }
             
            if(index + 1 < json.length) {
                index++;
            } else {
                index = 0;
            }
            return result;
        }
        
        /**
         * @return
         */
        public char current() {
            return json[index];
        }
        
        /**
         * @return
         */
        public boolean next() {
            if(index + 1 >= json.length) {
                index = 0;
                return false;
            } else {
                index++;
                return true;
            }
        }
        /**
         * @return
         */
        public char readBefore() {
            if(index - 1 >= 0 && index < json.length) {
                return json[index-1];
            } 
            return ' '; 
        }
    }

    /**
     * @param c
     * @return
     */
    private boolean isOpener(char c) {
        if(reader.readBefore() == '\\') {
            return false;
        }
        Opener[] opener = Opener.values();
        for (Opener item : opener) {
            if(item.sign == c) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param c
     * @return
     */
    private boolean isCloser(char c) {
        if(reader.readBefore() == '\\') {
            return false;
        }
        Closer[] closer = Closer.values();
        for (Closer item : closer) {
            if(item.sign == c) {
                return true;
            }
        }
        return false;
    }
    
    public JSONValue decode() {
        if(reader.current() == Opener.jsonobject.sign) {
            return decodeObject();
        } else if(reader.current() == Opener.jsonstring.sign) {
            return decodeString();
        }
        return null;
    }
    
    private JSONObject decodeObject() {
        JSONObject result = new JSONObject();
        
        boolean hasNext = true;
        while(hasNext) {
            // key must be a string
            reader.next();
            JSONString key = decodeString();
            
            while(reader.next()) {
                char temp = reader.current();
                if(temp == ':') {
                    reader.next();
                    break;
                }
            }
            
            JSONValue value = decode();
            result.put(key.toString(), value);
            reader.next();
            if(reader.current() == Closer.jsonobject.sign) {
                hasNext = false;
            } 
        }
        return result;
    }
    /**
     * @param input
     * @return
     */
    private JSONString decodeString() {
        StringBuilder result = new StringBuilder();
        if(reader.current() == Opener.jsonstring.sign) {
            while(reader.next()) {
                char temp = reader.current();
                // Strings cannot have a opener inside
                if(isCloser(temp)) {
                    break;
                } else {
                    result.append(temp);
                }
            }
        } else {
            return new JSONString("");
        }
        
        return new JSONString(result.toString());
    }
}