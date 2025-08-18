package com.module.springboot.service.account.starter.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequireScope(
    /**
     * The scopes required to access the annotated method.
     * If not specified, the method will require any scope.
     */
    val scope: String
)