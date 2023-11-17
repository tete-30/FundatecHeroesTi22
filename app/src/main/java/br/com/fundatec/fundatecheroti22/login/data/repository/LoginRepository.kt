package br.com.fundatec.fundatecheroti22.login.data.repository

import android.util.Log
import br.com.fundatec.fundatecheroti22.database.FHDataBase
import br.com.fundatec.fundatecheroti22.login.data.LoginRequest
import br.com.fundatec.fundatecheroti22.login.data.local.UserEntity
import br.com.fundatec.fundatecheroti22.login.data.remote.LoginResponse
import br.com.fundatec.fundatecheroti22.network.RetrofitNetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date


class LoginRepository {

    private val repository = RetrofitNetworkClient.createNetworkClient(
        baseUrl = "https://fundatec.herokuapp.com"
    ).create(LoginService::class.java)

    private val database: FHDataBase by lazy {
        FHDataBase.getInstance()
    }

    suspend fun createUser(
        name: String,
        email: String,
        password: String,

        ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = repository.createUser(
                    loginRequest = LoginRequest(
                        name = name,
                        email = email,
                        password = password,
                    )
                )
                response.isSuccessful
            } catch (ex: Exception) {
                Log.e("createUser", ex.message.toString())
                false
            }
        }
    }

    suspend fun login(
        email: String,
        password: String,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = repository.login(
                    email = email,
                    password = password,
                )
                response.body()?.tooUserEntity()?.let { userEntity ->
                    database.userDao().insertUser(userEntity)
                }
                response.isSuccessful
            } catch (ex: Exception) {
                Log.e("login", ex.message.toString())
                false
            }
        }
    }

    private fun LoginResponse.tooUserEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            password = password,
            date = Date ()
        )
    }

    suspend fun getDateCache(): Date? {
        return withContext(Dispatchers.IO) {
            database.userDao().getUserDate()
        }
    }

    suspend fun clearDateCache() {
        return withContext(Dispatchers.IO) {
            database.userDao().clearCache()
        }
    }

    suspend fun verificarUser(password: String, email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = repository.verificarUser(
                    password = password,
                    email = email,
                )
                response.isSuccessful
            } catch (ex: java.lang.Exception) {
                Log.e("verificarUser", ex.message.toString())
                false
            }
        }
    }


}