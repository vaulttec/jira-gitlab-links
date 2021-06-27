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

import java.util.Map;

import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.issue.link.RemoteIssueLink;
import com.atlassian.jira.plugin.issuelink.AbstractIssueLinkRenderer;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.collect.Maps;

import org.vaulttec.jira.gitlab.links.client.GitLabClient;
import org.vaulttec.jira.gitlab.links.client.model.Commit;
import org.vaulttec.jira.gitlab.links.client.model.Issue;
import org.vaulttec.jira.gitlab.links.client.model.Link;
import org.vaulttec.jira.gitlab.links.client.model.MergeRequest;
import org.vaulttec.jira.gitlab.links.client.model.Milestone;
import org.vaulttec.jira.gitlab.links.client.model.Release;

public class GitLabLinkRenderer extends AbstractIssueLinkRenderer {

    private final GitLabClient gitLabClient;
    private final WebResourceUrlProvider urlProvider;
    private final JiraAuthenticationContext authenticationContext;
    private final DateTimeFormatter dateTimeFormatter;

    public GitLabLinkRenderer(GitLabClient gitLabClient, WebResourceUrlProvider urlProvider,
            JiraAuthenticationContext authenticationContext, DateTimeFormatter dateTimeFormatter) {
        this.gitLabClient = gitLabClient;
        this.urlProvider = urlProvider;
        this.authenticationContext = authenticationContext;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Map<String, Object> getInitialContext(RemoteIssueLink remoteIssueLink, Map<String, Object> context) {
        return createInitialContext(remoteIssueLink);
    }

    private Map<String, Object> createInitialContext(RemoteIssueLink remoteIssueLink) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", remoteIssueLink.getId());
        map.put("url", remoteIssueLink.getUrl());
        map.put("title", remoteIssueLink.getTitle());
        map.put("iconUrl", getIconUrl());
        map.put("iconTooltip", "GitLab");
        return map;
    }

    @Override
    public Map<String, Object> getFinalContext(RemoteIssueLink remoteIssueLink, Map<String, Object> context) {
        Map<String, Object> map = createInitialContext(remoteIssueLink);
        ApplicationUser loggedInUser = authenticationContext.getLoggedInUser();

        // Only access GitLab API if API key and a logged-in user are provided
        if (gitLabClient.hasApiKey() && loggedInUser != null) {
            Link link = gitLabClient.getLink(remoteIssueLink.getUrl());
            switch (link.getType()) {
                case COMMIT:
                    Commit commit = gitLabClient.getCommit(link.getGroupAndProject(), link.getName(),
                            loggedInUser.getUsername());
                    if (commit != null) {
                        map.put("summary", commit.getTitle());
                    }
                    break;
                case ISSUE:
                    Issue issue = gitLabClient.getIssue(link.getGroupAndProject(), link.getName(),
                            loggedInUser.getUsername());
                    if (issue != null) {
                        map.put("summary", issue.getTitle());
                        map.put("statusName", issue.getState());
                        if (issue.isClosed()) {
                            map.put("statusColor", "current");
                        } else if (issue.isOpened()) {
                            map.put("statusColor", "success");
                        } else {
                            map.put("statusColor", "default");
                        }
                    }
                    break;
                case MERGE_REQUEST:
                    MergeRequest mergeRequest = gitLabClient.getMergeRequest(link.getGroupAndProject(), link.getName(),
                            loggedInUser.getUsername());
                    if (mergeRequest != null) {
                        map.put("summary", mergeRequest.getTitle());
                        map.put("statusName", mergeRequest.getState());
                        if (mergeRequest.isClosed()) {
                            map.put("statusColor", "error");
                        } else if (mergeRequest.isMerged()) {
                            map.put("statusColor", "current");
                        } else if (mergeRequest.isOpened()) {
                            map.put("statusColor", "success");
                        } else {
                            map.put("statusColor", "default");
                        }
                    }
                    break;
                case MILESTONE:
                    Milestone milestone = gitLabClient.getMilestone(
                            link.isInGroup() ? link.getProject() : link.getGroupAndProject(), link.getName(),
                            loggedInUser.getUsername(), link.isInGroup());
                    if (milestone != null) {
                        map.put("summary", milestone.getTitle());
                        map.put("statusName", milestone.getState());
                        if (milestone.isClosed()) {
                            map.put("statusColor", "error");
                        } else if (milestone.isActive()) {
                            map.put("statusColor", "success");
                        } else {
                            map.put("statusColor", "default");
                        }
                    }
                    break;
                case RELEASE:
                    Release release = gitLabClient.getRelease(link.getGroupAndProject(), link.getName(),
                            loggedInUser.getUsername());
                    if (release != null) {
                        map.put("summary", release.getName() + " ("
                                + dateTimeFormatter.forUser(loggedInUser).format(release.getReleasedAt()) + ")");
                    }
                    break;
                default:
                    break;
            }
        }
        return map;
    }

    @Override
    public boolean requiresAsyncLoading(RemoteIssueLink remoteIssueLink) {
        return true;
    }

    private String getIconUrl() {
        return urlProvider.getStaticPluginResourceUrl("org.vaulttec.jira-gitlab-links:resources",
                "images/gitlab-logo.png", UrlMode.RELATIVE);
    }
}
