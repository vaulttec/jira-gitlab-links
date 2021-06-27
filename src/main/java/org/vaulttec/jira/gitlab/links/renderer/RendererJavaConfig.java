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
package org.vaulttec.jira.gitlab.links.renderer;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaulttec.jira.gitlab.links.client.GitLabClient;

@Configuration
public class RendererJavaConfig {

    @Bean
    public GitLabLinkRenderer gitLabLinkRenderer(GitLabClient gitLabClient,
            WebResourceUrlProvider webResourceUrlProvider, JiraAuthenticationContext authenticationContext,
            DateTimeFormatter dateTimeFormatter) {
        return new GitLabLinkRenderer(gitLabClient, webResourceUrlProvider, authenticationContext, dateTimeFormatter);
    }

    @Bean
    public WebResourceUrlProvider webResourceUrlProvider() {
        return importOsgiService(WebResourceUrlProvider.class);
    }

    @Bean
    public JiraAuthenticationContext authenticationContext() {
        return importOsgiService(JiraAuthenticationContext.class);
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return importOsgiService(DateTimeFormatter.class);
    }
}
