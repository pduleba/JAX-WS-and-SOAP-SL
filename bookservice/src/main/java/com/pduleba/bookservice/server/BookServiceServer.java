package com.pduleba.bookservice.server;

import com.pduleba.bookservice.*;
import lombok.Getter;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.ext.logging.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsServiceFactoryBean;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;

public class BookServiceServer {

    private static final int PORT = 8080;

    @Getter
    private String url;
    private BookService clientCXF;

    public void start() {
        url = "http://localhost:" + PORT + "/BookService.svc";
        Bus bus = server(url, BookService.class, new BookServiceServerImpl());
        clientCXF = client(url, bus, BookService.class);
    }

    public void stop() {
        destroy();
    }

    private <T> Bus server(String url, Class<T> serviceClass, Object serviceBean) {
        JaxWsServiceFactoryBean svrFBean = new JaxWsServiceFactoryBean();
        svrFBean.setServiceClass(serviceClass);

        JaxWsServerFactoryBean serverBean = new JaxWsServerFactoryBean(svrFBean);

        // setting loggingFeature to print the received/sent messages
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        serverBean.getFeatures().add(loggingFeature);

        // setting interface
        serverBean.setServiceClass(serviceClass);
        // setting interface implementation bean
        serverBean.setServiceBean(serviceBean);

        // setting the endpoint
        serverBean.setAddress(url);

        // adding apache cxf bus
        serverBean.setBus(getBus());

        serverBean.create();

        return serverBean.getBus();
    }

    @SuppressWarnings("unchecked")
    private <T> T client(String url, Bus bus, Class<T> serviceClass) {
        JaxWsProxyFactoryBean clientBean = new JaxWsProxyFactoryBean();

        // setting interface
        clientBean.setServiceClass(serviceClass);

        // setting the endpoint
        clientBean.setAddress(url);

        // adding apache cxf bus
        clientBean.setBus(bus);

        return (T) clientBean.create();
    }

    private void destroy() {
        // If we don't destroy this the session support will spill over to other
        // tests and they will fail
        JettyHTTPServerEngineFactory.destroyForPort(PORT);
    }

    private Bus getBus() {
        Bus bus = BusFactory.newInstance().createBus();
        JettyHTTPServerEngineFactory jettyFactory = bus.getExtension(JettyHTTPServerEngineFactory.class);
        try {
            jettyFactory.createJettyHTTPServerEngine(PORT, "http").setSessionSupport(true);
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure Jetty");
        }
        return bus;
    }

    public GetBookResponse getBook(GetBookRequest request) {
        return clientCXF.getBook(request);
    }
}
