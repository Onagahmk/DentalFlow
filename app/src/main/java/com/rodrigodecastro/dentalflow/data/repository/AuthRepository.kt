package com.rodrigodecastro.dentalflow.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositório para lidar com todas as operações de autenticação e dados do usuário.
 * Esta classe encapsula a lógica de comunicação com o Firebase Authentication e o Firestore.
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Tenta autenticar um usuário com e-mail e senha no Firebase Authentication.
     * @return Um `Result` que encapsula o `FirebaseUser` em caso de sucesso ou a `Exception` em caso de falha.
     * A função é `suspend` para ser chamada a partir de uma coroutine.
     */
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra um novo usuário no sistema.
     * Este método realiza duas operações em sequência:
     * 1. Cria a conta de usuário no Firebase Authentication.
     * 2. Salva o nome do dentista em um documento separado no Firestore, usando o UID do usuário como chave.
     * @return Um `Result` com o `FirebaseUser` criado ou a `Exception` em caso de falha em qualquer etapa.
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            // 1. Cria o usuário no Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!

            // 2. Salva o nome do dentista no Firestore
            val dentistData = hashMapOf("name" to name)
            db.collection("dentists").document(firebaseUser.uid).set(dentistData).await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retorna o usuário atualmente logado no Firebase Authentication.
     * @return O `FirebaseUser` atual ou `null` se ninguém estiver logado.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Desconecta o usuário atualmente logado.
     */
    fun logout() {
        auth.signOut()
    }
}