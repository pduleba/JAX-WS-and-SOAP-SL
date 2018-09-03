package com.pduleba.bookservice.provider;

import java.io.*;

import javax.xml.bind.*;
import javax.xml.soap.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.w3c.dom.Document;

public class ContentProvider {

    private final MessageFactory messageFactory;
    private final XMLInputFactory xmlInputFactory;

    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;

    public ContentProvider(Class<?>... classesToBeBound) {
        super();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(classesToBeBound);
            unmarshaller = jaxbContext.createUnmarshaller();
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to create JAXBContext", e);
        }
        try {
            messageFactory = MessageFactory.newInstance();
        } catch (SOAPException e) {
            throw new RuntimeException("Unable to create MessageFactory", e);
        }
        xmlInputFactory = XMLInputFactory.newInstance();
    }

    public <T> JAXBElement<T> fromFile(File xmlFile, Class<T> rootType) {
        try {
            XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(new FileReader(xmlFile));

            return unmarshaller.unmarshal(reader, rootType);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read xml from file " + xmlFile, e);
        }
    }

    public void toFile(File xmlFile, Object jaxbElement) {
        try {
            marshaller.marshal(jaxbElement, xmlFile);
        } catch (Exception e) {
            throw new RuntimeException("Unable to write xml to file " + xmlFile, e);
        }
    }

    public <T> JAXBElement<T> fromEnvelope(File envelopeFile, Class<T> bodyType) {
        try (FileInputStream in = new FileInputStream(envelopeFile)) {

            SOAPMessage message = messageFactory.createMessage(null, in);
            Document document = message.getSOAPBody().extractContentAsDocument();

            return unmarshaller.unmarshal(document, bodyType);
        } catch (Exception e) {
            throw new RuntimeException("Unable to read envelope from file " + envelopeFile, e);
        }
    }


}
