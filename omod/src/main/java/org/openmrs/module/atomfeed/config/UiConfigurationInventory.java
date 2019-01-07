package org.openmrs.module.atomfeed.config;

import org.openmrs.annotation.OpenmrsProfile;
import org.openmrs.ui.framework.StandardModuleUiConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenmrsProfile(modules = { "uicommons:*.*" })
public class UiConfigurationInventory {

	@Bean
	public StandardModuleUiConfiguration createUiConfigurationBean() {
		StandardModuleUiConfiguration standardModuleUiConfiguration = new StandardModuleUiConfiguration();
		standardModuleUiConfiguration.setModuleId("atomfeed");
		return standardModuleUiConfiguration;
	}
}
