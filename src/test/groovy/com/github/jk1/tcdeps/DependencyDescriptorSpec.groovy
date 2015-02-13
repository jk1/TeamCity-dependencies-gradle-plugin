package com.github.jk1.tcdeps

import com.github.jk1.tcdeps.model.DependencyDescriptor
import org.gradle.api.InvalidUserDataException
import spock.lang.Specification


class DependencyDescriptorSpec extends Specification {

    def "legal data should result in valid Gradle dependency notation"() {
        expect:
        DependencyDescriptor descriptor = DependencyDescriptor.create(raw)
        descriptor.toDependencyNotation()[0] == notation

        where:
        raw                                                             | notation
        "btid:1.0:file.jar"                                             | [group:'org', name:'btid', version:'1.0', changing:false]
        [buildTypeId: "btid", version: "1.0", artifactPath: "file.jar"] | [group:'org', name:'btid', version:'1.0', changing:false]
    }

    def "illegal values should fail the build"() {
        when:
        DependencyDescriptor.create(path)

        then:
        thrown(InvalidUserDataException)

        where:
        path << [null, "", ":", "btid:1.0", "btid::file.jar",
                 [foo: 'bar'],
                 [buildTypeId: "btid", version: "1.0"],
                 [buildTypeId: "btid", artifactId: "aid"]
        ]
    }
}
