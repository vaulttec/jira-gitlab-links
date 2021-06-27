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

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.comment.CommentService;
import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.event.issue.IssueEventBundleFactory;
import com.atlassian.jira.event.issue.IssueEventManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenRendererFactory;
import com.atlassian.jira.issue.link.RemoteIssueLink;
import com.atlassian.jira.issue.link.RemoteIssueLinkBuilder;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.web.action.issue.AbstractIssueLinkAction;

import org.apache.commons.lang.StringUtils;
import org.vaulttec.jira.gitlab.links.client.GitLabClient;
import org.vaulttec.jira.gitlab.links.client.model.Link;
import org.vaulttec.jira.gitlab.links.client.model.Link.Type;

public class CreateGitLabLinkAction extends AbstractIssueLinkAction {

	private static final long serialVersionUID = 1L;
	private final GitLabClient gitLabClient;
	private String url;

	public CreateGitLabLinkAction(GitLabClient gitLabClient, SubTaskManager subTaskManager,
			FieldScreenRendererFactory fieldScreenRendererFactory, FieldManager fieldManager,
			ProjectRoleManager projectRoleManager, CommentService commentService, UserUtil userUtil,
			RemoteIssueLinkService remoteIssueLinkService, EventPublisher eventPublisher,
			IssueEventManager issueEventManager, IssueEventBundleFactory issueEventBundleFactory) {
		super(subTaskManager, fieldScreenRendererFactory, fieldManager, projectRoleManager, commentService, userUtil,
				remoteIssueLinkService, eventPublisher, issueEventManager, issueEventBundleFactory);
		this.gitLabClient = gitLabClient;
	}

	@Override
	protected void doValidation() {

		// Validate comment and permissions
		super.doValidation();

		// Validate GitLab URL
		Link link = getLink(url);
		if (link != null) {
			RemoteIssueLink remoteIssueLink = new RemoteIssueLinkBuilder().issueId(getIssue().getLong("id")).url(url)
					.title(getTitle(link)).relationship("GitLab").applicationName("GitLab Links")
					.applicationType("org.vaulttec.gitlab-links").build();
			validationResult = remoteIssueLinkService.validateCreate(getLoggedInUser(), remoteIssueLink);
			if (!validationResult.isValid()) {
				addErrorCollection(validationResult.getErrorCollection());
			}
		}
	}

	private Link getLink(String url) {
		if (StringUtils.isBlank(url)) {
			addError("url", getText("org.vaulttec.gitlab-links.link.url.error.required"));
		} else if (!url.startsWith(gitLabClient.getServerUrl())) {
			addError("url", getText("org.vaulttec.gitlab-links.link.url.error.invalid"));
		} else {
			Link link = gitLabClient.getLink(url);
			if (link.getType() == Type.INVALID) {
				addError("url", getText("org.vaulttec.gitlab-links.link.url.error.unsupported"));
			} else {
				return link;
			}
		}
		return null;
	}

	private String getTitle(Link link) {
		switch (link.getType()) {
			case PROJECT:
				return getText("org.vaulttec.gitlab-links.link.project") + " " + link.getGroup() + " / "
						+ link.getProject();
			case BRANCH:
				return getText("org.vaulttec.gitlab-links.link.branch") + " " + link.getName();
			case FOLDER:
				return getText("org.vaulttec.gitlab-links.link.folder") + " " + link.getName();
			case FILE:
				return getText("org.vaulttec.gitlab-links.link.file") + " " + link.getName();
			case COMMIT:
				return getText("org.vaulttec.gitlab-links.link.commit") + " " + link.getName().substring(0, 8);
			case TAG:
				return getText("org.vaulttec.gitlab-links.link.tag") + " " + link.getName();
			case ISSUE:
				return getText("org.vaulttec.gitlab-links.link.issue") + " #" + link.getName();
			case MERGE_REQUEST:
				return getText("org.vaulttec.gitlab-links.link.mergeRequest") + " !" + link.getName();
			case MILESTONE:
				return getText("org.vaulttec.gitlab-links.link.milestone") + " " + link.getName();
			case RELEASE:
				return getText("org.vaulttec.gitlab-links.link.release") + " " + link.getName();
			default:
				return link.getUrl();
		}
	}

	@Override
	public String doDefault() throws Exception {
		url = gitLabClient.getServerUrl();
		return super.doDefault();
	}

	@RequiresXsrfCheck
	@Override
	protected String doExecute() {
		RemoteIssueLinkService.RemoteIssueLinkResult result = createLink();
		if (!result.isValid()) {
			addErrorCollection(result.getErrorCollection());
			return ERROR;
		}
		createAndDispatchComment();
		return returnComplete(getRedirectUrl());
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
