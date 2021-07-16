package com.github.jk1.tcdeps.model

import org.gradle.api.InvalidUserDataException
import spock.lang.Specification


class ArtifactDescriptorSpec extends Specification {

    def "bare files should be parsed as name + extension"() {
        ArtifactDescriptor descriptor = new ArtifactDescriptor(raw)

        expect:
        descriptor.name.equals(name)
        descriptor.extension.equals(ext)
        !descriptor.hasPath()
        descriptor.path == null

        where:
        raw         | name    | ext
        "file.jar"  | "file"  | "jar"
        ".file.zip" | ".file" | "zip"
    }

    def "artifact path should be parsed correctly, if exists"() {
        ArtifactDescriptor descriptor = new ArtifactDescriptor(raw)

        expect:
        descriptor.name.equals(name)
        descriptor.extension.equals(ext)

        where:
        raw                         | name                    | ext
        "folder/file.ext"           | "folder/file"           | "ext"
        "dir/archive!/dir/file.ext" | "dir/archive!/dir/file" | "ext"
    }


    def "illegal values should fail the build"() {
        when:
        new ArtifactDescriptor(path)

        then:
        thrown(InvalidUserDataException)

        where:
        path << ["", null]
    }


}