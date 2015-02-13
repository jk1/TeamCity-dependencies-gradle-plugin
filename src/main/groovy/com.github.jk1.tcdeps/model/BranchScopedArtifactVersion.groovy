package com.github.jk1.tcdeps.model

import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project

/**
 * Stands for changing module version limited to a particular TeamCity feature branch
 */
class BranchScopedArtifactVersion extends ArtifactVersion {

    final def branch

    BranchScopedArtifactVersion(String version, String branch) {
        super(version)
        if (branch == null || branch.isEmpty()) {
            throw new InvalidUserDataException("branch should not be empty")
        }
        this.branch = branch
    }

    @Override
    def resolve(Project project, String btid) {
        String response = "<no response recorded>"
        try {
            HttpURLConnection connection = url(project.teamcityServer.url, btid).toURL().openConnection()
            response = connection.inputStream.withReader { Reader reader -> reader.text }
            if (connection.getResponseCode() == 200) {
                project.logger.info("$version has been resolved as $response in $branch")
                version = response
                changing = false
            } else {
                String message = "Unable to resolve $version in $branch. Server response: \n $response"
                throw new GradleException(message, e)
            }
        } catch (Exception e) {
            String message = "Unable to resolve $version in $branch. Server response: \n $response"
            throw new GradleException(message, e)
        }
    }

    private def url(String server, String btid) {
        def query = "buildType:$btid,branch:$branch,${versionPlaceholders[version]}/number"
        return "${server}/guestAuth/app/rest/builds/$query"
    }
}
