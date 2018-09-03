package com.pduleba.bookservice.client;

import java.io.File;

import com.pduleba.bookservice.GetBookRequest;
import com.pduleba.bookservice.GetBookResponse;
import com.pduleba.bookservice.provider.ContentProvider;
import com.pduleba.bookservice.server.BookServiceServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookServiceClient implements AutoCloseable {

    private final static int TIMEOUT = 10 * 1000 * 60 * 60 * 24;

    private final File REQUEST_ENVELOPE_FILE = new File(
            "src\\main\\resources\\db\\GetBookRequest.xml");

    private final BookServiceServer server = new BookServiceServer();

    public static void main(String[] args) {
        try (BookServiceClient main = new BookServiceClient()) {
            main.doExecute();
        } catch (Exception e) {
            log.error("Unable to start server", e);
        }
    }

    private void doExecute() throws InterruptedException {
        log.debug("Starting server...");
        server.start();
        log.debug("Server is running on {}", server.getUrl());
        log.debug("Server TEST start");
        GetBookResponse response = testRequestResponse();
        log.debug("Server response :: {}", response);
        Thread.sleep(TIMEOUT);
    }

    public GetBookResponse testRequestResponse() {
        ContentProvider provider = new ContentProvider(GetBookRequest.class);
        GetBookRequest request = provider.fromEnvelope(REQUEST_ENVELOPE_FILE, GetBookRequest.class)
                .getValue();

        return server.getBook(request);
    }

    @Override
    public void close() throws Exception {
        if (server != null) {
            try {
                server.stop();
                log.debug("Server stopped successfully");
            } catch (Exception e) {
                log.error("Unable to stop server");
            }
        }
    }

}
