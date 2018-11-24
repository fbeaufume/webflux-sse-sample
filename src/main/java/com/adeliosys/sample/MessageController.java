package com.adeliosys.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

@RestController
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageProcessor processor;

    @PostMapping("/send")
    public String send(@RequestBody String message) {
        LOGGER.info("Received '{}'", message);
        processor.process(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " " + message);
        return "Done";
    }

    @GetMapping(path = "/receive", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> receive() {
        // Some FluxSink documentation and code samples:
        // - https://projectreactor.io/docs/core/release/reference/#producing.create
        // - https://www.baeldung.com/reactor-core
        // - https://www.e4developer.com/2018/04/14/webflux-and-servicing-client-requests-how-does-it-work/

        return Flux.create(sink -> {
            processor.register(sink::next);
        });
    }

    @GetMapping(path = "/timestamps", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> timestamps() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> LocalTime.now().toString());
    }
}
