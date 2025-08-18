package com.module.springboot.service.account.starter.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequireScopes(
    /**
     * The scopes required to access the annotated method.
     * If not specified, the method will require any scope.
     */
    val scopes: Array<String> = [],
)