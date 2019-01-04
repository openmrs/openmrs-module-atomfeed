package org.openmrs.module.atomfeed.web.controller;

import org.openmrs.module.atomfeed.utils.AtomfeedMessageUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The Atomfeed module controller.
 */
@Controller
@RequestMapping(value = "/module/atomfeed")
public class AtomfeedModuleController {

	@RequestMapping("/atomfeed")
	public void manage(ModelMap model,
			@RequestParam(value = AtomfeedMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage) {
		model.addAttribute(AtomfeedMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
	}
}
