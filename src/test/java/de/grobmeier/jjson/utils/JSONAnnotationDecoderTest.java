package de.grobmeier.jjson.utils;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

public class JSONAnnotationDecoderTest {

    @Test
    public void testDecode() throws Exception {
        JSONAnnotationDecoder decoder = new JSONAnnotationDecoder();
        AnnotatedSetTestClass result = 
            decoder.decode(AnnotatedSetTestClass.class, "{\"test1\":\"mytestvalue\"}");
        Assert.assertEquals("mytestvalue",result.getTest1());
    }

}