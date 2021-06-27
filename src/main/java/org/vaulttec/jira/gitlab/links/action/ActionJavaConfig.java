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
package org.vaulttec.jira.gitlab.links.action;

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.comment.CommentService;
import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.event.issue.IssueEventBundleFactory;
import com.atlassian.jira.event.issue.IssueEventManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenRendererFactory;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.util.UserUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaulttec.jira.gitlab.links.client.GitLabClient;

@Configuration
public class ActionJavaConfig {

    @Bean
    public CreateGitLabLinkAction createGitLabLinkAction(GitLabClient gitLabClient, SubTaskManager subTaskManager,
            FieldScreenRendererFactory fieldScreenRendererFactory, FieldManager fieldManager,
            ProjectRoleManager projectRoleManager, CommentService commentService, UserUtil userUtil,
            RemoteIssueLinkService remoteIssueLinkService, EventPublisher eventPublisher,
            IssueEventManager issueEventManager, IssueEventBundleFactory issueEventBundleFactory) {
        return new CreateGitLabLinkAction(gitLabClient, subTaskManager, fieldScreenRendererFactory, fieldManager,
                projectRoleManager, commentService, userUtil, remoteIssueLinkService, eventPublisher, issueEventManager,
                issueEventBundleFactory);
    }

    @Bean
    public SubTaskManager subTaskManager() {
        return importOsgiService(SubTaskManager.class);
    }

    @Bean
    public FieldScreenRendererFactory fieldScreenRendererFactory() {
        return importOsgiService(FieldScreenRendererFactory.class);
    }

    @Bean
    public FieldManager fieldManager() {
        return importOsgiService(FieldManager.class);
    }

    @Bean
    public ProjectRoleManager projectRoleManager() {
        return importOsgiService(ProjectRoleManager.class);
    }

    @Bean
    public CommentService commentService() {
        return importOsgiService(CommentService.class);
    }

    @Bean
    public UserUtil userUtil() {
        return importOsgiService(UserUtil.class);
    }

    @Bean
    public RemoteIssueLinkService remoteIssueLinkService() {
        return importOsgiService(RemoteIssueLinkService.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return importOsgiService(EventPublisher.class);
    }

    @Bean
    public IssueEventManager issueEventManager() {
        return importOsgiService(IssueEventManager.class);
    }

    @Bean
    public IssueEventBundleFactory issueEventBundleFactory() {
        return importOsgiService(IssueEventBundleFactory.class);
    }
}
