package com.sinha.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinha.model.ArgumentFramework;
import com.sinha.service.ArgumentUtils;
import com.sinha.service.ServiceFactory;

@Controller
@RequestMapping("rest")
@ComponentScan
public class RestController {

	private static final Logger logger = LoggerFactory.getLogger(RestController.class);

	@Autowired
	private ServiceFactory serviceFactory;

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(@RequestParam(name = "file", required = false) MultipartFile file,
			@RequestParam(name = "rankingSemantic") String rankingSemantic, HttpServletResponse response)
			throws JsonProcessingException {

		List<String> lines = new ArrayList<>();
		if (file.isEmpty()) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return "Failed";
		}
		try {
			parseFile(file, response, lines);
		} catch (Exception e) {
			return e.getMessage();
		}
		ArgumentFramework af = ArgumentUtils.parseArguments(lines);
		serviceFactory.getRankingSemantic(rankingSemantic).generateRanks(af);
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(af);
	}

	private void parseFile(MultipartFile file, HttpServletResponse response, List<String> lines) throws Exception {
		try {
			InputStream stream = new BufferedInputStream(file.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			while (null != line) {
				logger.info("Read line: {}", line);
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
			stream.close();
		} catch (Exception e) {
			logger.error("Exception reading file: ", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new Exception("Could not read file");
		}
		if (CollectionUtils.isEmpty(lines)) {
			throw new Exception("No Arguments Supplied");
		}
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public @ResponseBody String testStr() {
		logger.info("Test");
		return "123";
	}
}
