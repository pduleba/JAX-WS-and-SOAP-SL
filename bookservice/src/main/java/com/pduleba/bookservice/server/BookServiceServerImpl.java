package com.pduleba.bookservice.server;

import java.io.File;

import com.pduleba.bookservice.*;
import com.pduleba.bookservice.provider.ContentProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@javax.jws.WebService(
        portName = "BookServiceSOAP",
        serviceName = "BookService",
        targetNamespace = "http://www.pduleba.com/BookService/",
        endpointInterface = "com.pduleba.bookservice.BookService")
class BookServiceServerImpl implements BookService {

    private final File RESPONSE_ENVELOPE_FILE = new File(
            "src\\main\\resources\\db\\GetBookResponse.xml");

    private ContentProvider responseContentProvider;

    public BookServiceServerImpl() {
        super();
        this.responseContentProvider = new ContentProvider(GetBookResponse.class);
    }

    @Override
    public GetBookResponse getBook(GetBookRequest request) {
        log.debug("Get book :: {}", request);

        GetBookResponse response = responseContentProvider.fromEnvelope(RESPONSE_ENVELOPE_FILE, GetBookResponse.class)
                .getValue();
        response.setID(request.getID());

        return response;
    }

    @Override
    public AddBookResponse addBook(AddBookRequest parameters) {
        throw new RuntimeException("Not implemented yet!");
    }

    @Override
    public GetAllBooksResponse getAllBooks(GetAllBooksRequest parameters) {
        throw new RuntimeException("Not implemented yet!");
    }

}
