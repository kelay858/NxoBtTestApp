package com.example.nxobtintegration.ui

class AuthRepo {
    private var repo: AuthRepo? = null
    var accessToken: String = ""
    private fun getRepo(): AuthRepo? {
        if (repo == null) {
            repo = AuthRepo()
        }
        return repo
    }
}