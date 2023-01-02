package com.qrcodecopier;

import io.micronaut.http.annotation.*;

@Controller("/backend")
public class BackendController {

    @Get(uri="/", produces="text/plain")
    public String index() {
        return "Example Response";
    }
}