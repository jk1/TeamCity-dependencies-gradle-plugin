package com.github.jk1.tcdeps.processing

import com.github.jk1.tcdeps.model.DependencyDescriptor
import spock.lang.Specification

class ModuleVersionResolverSpec extends Specification {

    def "Module version resolver should ignore non-changing versions"(){
        ModuleVersionResolver resolver = new ModuleVersionResolver()
        DependencyDescriptor dependency = DependencyDescriptor.create('bt1:1.0.0:lib.zip')

        when:
        resolver.addDependency(dependency)

        then:
        dependency.version.version == '1.0.0'
    }

}
