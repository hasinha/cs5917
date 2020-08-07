package com.sinha.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
			@RequestParam(name = "rankingSemantic") String rankingSemantic,
			@RequestParam(name = "extensionSemantic") String extensionSemantic, @RequestParam(name = "uid") String uid,
			HttpServletResponse response) throws Exception {

		List<String> lines = new ArrayList<>();
		boolean genExt = StringUtils.isNotBlank(extensionSemantic);
		if (!genExt) {
			extensionSemantic = "prefExt";
		}
		ArgumentFramework af = null;
		if (file.isEmpty()) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return "No file or Empty file input";
		}
		try {
			parseFile(file, response, lines);
			af = ArgumentUtils.parseArguments(lines);
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			return "Input file could not be parsed. Please check input and try again.";
		}
		long startTime = System.currentTimeMillis();
		serviceFactory.getRankingSemantic(rankingSemantic).generateRanks(af);
		logger.info("Time taken: {}", (System.currentTimeMillis() - startTime));
		serviceFactory.getReasoningSemantic(extensionSemantic).generateLabelings(af, uid, genExt);
//		ObjectMapper om = new ObjectMapper();
		response.setStatus(HttpStatus.CREATED.value());
		return "success";
	}

	private void parseFile(MultipartFile file, HttpServletResponse response, List<String> lines) throws Exception {
		try {
			InputStream stream = new BufferedInputStream(file.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			while (null != line) {
				logger.info("Read line: {}", line);
				lines.addAll(new ArrayList<>(Arrays.asList(line.split("\\."))));
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

	@RequestMapping(value = "/getUid", method = RequestMethod.GET)
	public @ResponseBody String testStr() {
		return UUID.randomUUID().toString();
	}
}
