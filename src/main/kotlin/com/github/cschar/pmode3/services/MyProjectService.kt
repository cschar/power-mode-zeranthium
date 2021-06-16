package com.github.cschar.pmode3.services

import com.github.cschar.pmode3.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println("My project service intialized")
        println(MyBundle.message("projectService", project.name))
    }
}
