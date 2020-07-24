package com.sinha.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

	@RequestMapping("/welcome")
	public @ResponseBody ModelAndView helloWorld() {
		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello World! Test **********</div><br><br>";
		return new ModelAndView("welcome", "userId", message);
	}

	@RequestMapping(value = "/samplefile", method = RequestMethod.GET)
	public void getFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			String realPath = request.getSession().getServletContext().getRealPath("/resources/SampleInput.af");
			InputStream is = new FileInputStream(realPath);
			org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
			response.setContentType("text/af");
			response.setHeader("Content-disposition", "attachment; filename=SampleInput.af");
			response.flushBuffer();
		} catch (IOException ex) {
			LOGGER.info("Error writing file to output stream. ", ex);
			throw new RuntimeException("IOError writing file to output stream");
		}

	}
}