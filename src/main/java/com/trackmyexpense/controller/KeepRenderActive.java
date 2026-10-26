package com.trackmyexpense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KeepRenderActive {

	@GetMapping("trigger/keepfreeBEactive")
	public ResponseEntity<String> keepTriggering(){
		return ResponseEntity.ok("OK");
	}
}
