package com.megumi.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@FeignClient(name = "msg-service")
public interface RemoteMsgService {

    @GetMapping("/api/msg/mail/send")
    String sendMsgToEmail(@RequestParam("email") String email,
                          @RequestParam("type") String type);

    @GetMapping("/api/msg/phone/send")
    String sendMsgToPhone(@RequestParam("phone") String phone,
                          @RequestParam("type") String type);

    @GetMapping("/api/msg/validate")
    String validateCode(@RequestParam("code") String code,
                        @RequestParam("info") String info,
                        @RequestParam("type") String type);
}
