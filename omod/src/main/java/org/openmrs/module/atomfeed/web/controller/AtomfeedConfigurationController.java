package org.openmrs.module.atomfeed.web.controller;

import org.apache.commons.io.IOUtils;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.model.GeneralConfiguration;
import org.openmrs.module.atomfeed.api.service.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.atomfeed.utils.AtomfeedMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/module/atomfeed")
public class AtomfeedConfigurationController {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AtomfeedConfigurationController.class);

	private static final String CONFIGURATION_MODEL = "configuration";
	private static final String SAVE_CONFIG_SUCCESS = "atomfeed.configuration.json.save.success";
	private static final String SAVE_CONFIG_ERROR = "atomfeed.configuration.json.save.fail";
	private static final String ERRORS_INVALID_FILE = "atomfeed.configuration.errors.invalidFile";

	@Autowired
	private FeedConfigurationService feedConfigurationService;

	@RequestMapping("/configuration")
	public String get(ModelMap model,
			@RequestParam(value = AtomfeedMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage) {
		String configuration =
				AtomfeedUtils.writeFeedConfigurationToJsonString(feedConfigurationService.getGeneralConfiguration());
		model.addAttribute(CONFIGURATION_MODEL, configuration);
		model.addAttribute(AtomfeedMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
		return "/module/atomfeed/atomfeedConfiguration";
	}

	@RequestMapping(value = "/saveConfiguration", method = RequestMethod.POST)
	public String post(ModelMap model,
			@RequestParam("json") String json) {
		try {
			feedConfigurationService.saveConfig(json);
			AtomfeedMessageUtils.successMessage(model, SAVE_CONFIG_SUCCESS);
			return "redirect:/module/atomfeed/atomfeed.form";
		} catch (Exception e) {
			LOGGER.warn("Error during save:", e);
			AtomfeedMessageUtils.errorMessage(model, SAVE_CONFIG_ERROR);
		}
		return "redirect:/module/atomfeed/configuration.form";
	}

	@ResponseBody
	@RequestMapping("/verifyJson")
	public Map<String, Boolean> verifyJson(@RequestParam("json") String json) throws AtomfeedException {
		Map<String, Boolean> result = new HashMap<>();

		if (AtomfeedUtils.isValidateJson(json)) {
			result.put("isValid", true);
		} else  {
			LOGGER.warn("Invalid json.");
			result.put("isValid", false);
		}
		return result;
	}

	@RequestMapping(value = "/importFeedConfiguration", method = RequestMethod.POST)
	public String saveConfiguration(@RequestParam(value = "file") MultipartFile file,
			ModelMap model) throws AtomfeedException, IOException {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			IOUtils.copy(file.getInputStream(), writer, "UTF-8");
			String jsonContent = writer.toString();
			if (!AtomfeedUtils.isValidateJson(jsonContent)) {
				AtomfeedMessageUtils.errorMessage(model, ERRORS_INVALID_FILE);
				return "redirect:/module/atomfeed/configuration.form";
			}
			GeneralConfiguration feedConfiguration = AtomfeedUtils.parseJsonStringToFeedConfiguration(jsonContent);
			feedConfigurationService.saveConfig(feedConfiguration);
			AtomfeedMessageUtils.successMessage(model, SAVE_CONFIG_SUCCESS);
			return "redirect:/module/atomfeed/atomfeed.form";
		} catch (AtomfeedException e) {
			LOGGER.warn("Error during import configuration:", e);
			AtomfeedMessageUtils.errorMessage(model, ERRORS_INVALID_FILE);
		} finally {
			IOUtils.closeQuietly(writer);
		}
		return "redirect:/module/atomfeed/configuration.form";
	}
}
