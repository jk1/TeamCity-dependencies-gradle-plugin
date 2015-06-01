package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.BranchScopedArtifactVersion
import spock.lang.Specification

/**
 * Created by Nikita.Skvortsov
 * date: 31.05.2015.
 */
class VersionDescriptorSpec extends Specification {

  def "Branch scoped versions should produce correct REST request urls"() {
    when:
    BranchScopedArtifactVersion version = new BranchScopedArtifactVersion(versionValue, "branchName")

    then:
    version.url("server", "btid").toString() == restUrl

    where:
    versionValue |  restUrl
    "lastFinished" | "server/guestAuth/app/rest/builds/buildType:btid,branch:branchName/number"
    "TagName.tcbuildtag" | "server/guestAuth/app/rest/builds/buildType:btid,branch:branchName,tags:TagName/number"
    "lastSuccessful" | "server/guestAuth/app/rest/builds/buildType:btid,branch:branchName,status:SUCCESS/number"
  }
}
