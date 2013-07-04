/*
 * The MIT License
 *
 * Copyright 2013 Bigcommerce.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bigcommerce.api.transform;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author luke.eller
 */
public class ListMarshaller {

	/**
	 * Wrap List in ListWrapper, then leverage JAXBElement to supply root
	 * element information.
	 */
	private static void marshal(Marshaller marshaller, List<?> list, String name)
			throws JAXBException {
		QName qName = new QName(name);
		ListWrapper wrapper = new ListWrapper(list);
		JAXBElement<ListWrapper> jaxbElement = new JAXBElement<ListWrapper>(qName,
				ListWrapper.class, wrapper);
		marshaller.marshal(jaxbElement, System.out);
	}
	
	/**
     * Unmarshal XML to Wrapper and return List value.
     */
    private static <T> List<T> unmarshal(Unmarshaller unmarshaller,
            Class<T> clazz, String xmlLocation) throws JAXBException {
        StreamSource xml = new StreamSource(xmlLocation);
        ListWrapper<T> wrapper = (ListWrapper<T>) unmarshaller.unmarshal(xml,
                ListWrapper.class).getValue();
        return wrapper.getItems();
    }
}
