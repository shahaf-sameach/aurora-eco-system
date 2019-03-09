package com.example.demo;

import com.example.demo.data.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Slf4j
@RestController
public class SseController {

    private final Collection<SseEmitter> emitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());


    @GetMapping("/data_stream")
    public SseCustomEmitter handle() {
        final SseCustomEmitter emitter = new SseCustomEmitter(0L);
        register(emitter);
        log.info("registered new emitter");
        return emitter;
    }

    @Async
    @EventListener
    public void onNewNotification(Payload notification) {
        for(SseEmitter emitter : emitters) {
            try {
                SseEmitter.SseEventBuilder sseEvent = SseEmitter.event().reconnectTime(1_000L).data(notification);
                emitter.send(sseEvent);
            } catch (Exception e) {
                complete(emitter);
                e.printStackTrace();
            }
        }
    }


    public void register(SseEmitter emitter) {
        emitter.onTimeout(() -> timeout(emitter));
        emitter.onCompletion(() -> complete(emitter));
        emitters.add(emitter);

    }

    private void complete(SseEmitter emitter) {
        log.info("emitter completed");
        emitters.remove(emitter);
    }

    private void timeout(SseEmitter emitter) {
        log.info("emitter timeout");
        emitters.remove(emitter);
    }

    private class SseCustomEmitter extends SseEmitter {

        public SseCustomEmitter(long timeout) {
            super(timeout);
        }

        @Override
        protected void extendResponse(ServerHttpResponse outputMessage) {
            super.extendResponse(outputMessage);

            HttpHeaders headers = outputMessage.getHeaders();
            if (headers.getContentType() == null) {
                headers.setContentType(new MediaType("text", "event-stream"));
                headers.setCacheControl("no-cache");
                headers.set("X-Accel-Buffering", "no");
            }
        }
    }
}
