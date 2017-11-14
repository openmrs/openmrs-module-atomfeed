/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.atomfeed.page.controller;

import org.apache.commons.io.IOUtils;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.atomfeed.api.FeedConfigurationService;
import org.openmrs.module.atomfeed.api.exceptions.AtomfeedException;
import org.openmrs.module.atomfeed.api.model.FeedConfiguration;
import org.openmrs.module.atomfeed.api.utils.AtomfeedUtils;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.module.uicommons.util.InfoErrorMessageUtil;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Controller
public class LoadFeedConfPageController {

	private static final String SAVE_CONFIG_ERROR = "atomfeed.configuration.json.save.fail";
	private static final String SAVE_CONFIG_SUCCESS = "atomfeed.configuration.json.save.success";
	private static final String VIEW_PROVIDER = "loadFeedConf";

	protected static final Logger LOGGER = LoggerFactory.getLogger(LoadFeedConfPageController.class);

	@Autowired
	FeedConfigurationService feedConfigurationService;

	@Autowired
	@Qualifier("messageSourceService")
	private MessageSourceService messageSourceService;

	public String get(PageModel model,
					  @RequestParam(value = "importStatus", required = false) String importStatus,
					  @SpringBean("atomfeed.feedConfigurationService") FeedConfigurationService feedConfigurationService) {
		String configuration = AtomfeedUtils.writeFeedConfigurationToJsonString(feedConfigurationService.getAllFeedConfigurations());
		model.addAttribute("configuration", configuration);
		model.addAttribute("importStatus", importStatus);
		return VIEW_PROVIDER;
	}
	
	public String post(PageModel model,
           @SpringBean("atomfeed.feedConfigurationService") FeedConfigurationService feedConfigurationService,
		   @RequestParam("json") String json, HttpSession session, UiUtils ui) {

		try {
			feedConfigurationService.saveConfig(json);
			InfoErrorMessageUtil.flashInfoMessage(session, ui.message(SAVE_CONFIG_SUCCESS));

			return "redirect:/atomfeed/atomfeed.page";
		} catch (Exception e) {
			LOGGER.warn("Error during save:", e);

			session.setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE, ui.message(SAVE_CONFIG_ERROR));
		}

		return null;
	}
	
	@ResponseBody
	@RequestMapping("/atomfeed/verifyJson")
	public SimpleObject verifyJson(@RequestParam("json") String json) throws AtomfeedException {
		SimpleObject result = new SimpleObject();

		if (AtomfeedUtils.isValidateJson(json)) {
			result.put("isValid", true);
		} else {
			LOGGER.warn("Invalid json.");
			result.put("isValid", false);
		}
		
		return result;
	}

	@RequestMapping(value = "/atomfeed/importFeedConfiguration", method = RequestMethod.POST)
	public String importSyncConfiguration(@RequestParam("file") MultipartFile file,
										  ModelMap model) throws AtomfeedException, IOException {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			IOUtils.copy(file.getInputStream(), writer, "UTF-8");

			List<FeedConfiguration> feedConfigurations = AtomfeedUtils.parseJsonStringToFeedConfiguration(writer.toString());
			feedConfigurationService.saveConfig(feedConfigurations);
			model.addAttribute("importStatus", "");
			return "redirect:/atomfeed/atomfeed.page";
		} catch (AtomfeedException e) {
			LOGGER.warn("Error during import configuration:", e);
			model.addAttribute("importStatus", messageSourceService.getMessage("atomfeed.configuration.errors.invalidFile"));
		} finally {
			IOUtils.closeQuietly(writer);
		}

		return "redirect:/atomfeed/LoadFeedConf.page";
	}
}
