/*
 * GitLab Links for Jira
 * Copyright (c) 2021 Torsten Juergeleit
 * mailto:torsten AT vaulttec DOT org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaulttec.jira.gitlab.links.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaulttec.jira.gitlab.links.config.ConfigStore;

import com.atlassian.sal.api.net.RequestFactory;

@Configuration
public class GitLabClientJavaConfig {

	@Bean
	public GitLabClient gitlabClient(RequestFactory<?> requestFactory, ConfigStore configStore) {
		return new GitLabClientImpl(requestFactory, configStore);
	}
}
