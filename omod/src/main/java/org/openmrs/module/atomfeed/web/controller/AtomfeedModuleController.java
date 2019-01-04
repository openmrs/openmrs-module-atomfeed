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

	/**
	 * Sets the UI model attributes.
	 *
	 * @param model injected the page model object
	 * @param success injected the flag used to choose the type of alert message
	 * @param alertMessage injected the message used to display an alert message (display the alert if the value isn't null)
	 */
	@RequestMapping("/atomfeed")
	public void manage(ModelMap model,
			@RequestParam(value = AtomfeedMessageUtils.SUCCESS_MESSAGE, required = false) boolean success,
			@RequestParam(value = AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, required = false) String alertMessage) {
		model.addAttribute(AtomfeedMessageUtils.SUCCESS_MESSAGE, success);
		model.addAttribute(AtomfeedMessageUtils.ALERT_MESSAGE_MODEL, alertMessage);
	}
}
