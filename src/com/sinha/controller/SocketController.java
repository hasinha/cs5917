package com.sinha.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.sinha.model.ArgumentFramework;

@Controller
public class SocketController {

	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public ArgumentFramework send(ArgumentFramework af) throws Exception {
		return af;
	}
}
