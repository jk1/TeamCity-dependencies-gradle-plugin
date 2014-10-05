package com.github.jk1.tcdeps

import org.gradle.api.InvalidUserDataException
import spock.lang.Specification


class DependencyDescriptorSpec extends Specification {

    def "legal data should result in valid Gradle dependency notation"() {
        expect:
        DependencyDescriptor descriptor = new DependencyDescriptor(raw)
        descriptor.toDependencyNotation()[0] == notation

        where:
        raw                                                             | notation
        "btid:1.0:file.jar"                                             | "org:btid:1.0"
        [buildTypeId: "btid", version: "1.0", artifactPath: "file.jar"] | "org:btid:1.0"
    }

    def "illegal values should fail the build"() {
        when:
        new DependencyDescriptor(path)

        then:
        thrown(InvalidUserDataException)

        where:
        path << [null, "", ":", "btid:1.0", "btid::file.jar",
                 [foo: 'bar'],
                 [buildTypeId: "btid", version: "1.0"]
        ]
    }
}
