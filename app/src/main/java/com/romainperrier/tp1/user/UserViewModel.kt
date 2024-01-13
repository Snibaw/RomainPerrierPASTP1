package com.romainperrier.tp1.user

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.romainperrier.tp1.data.API
import com.romainperrier.tp1.data.User
import com.romainperrier.tp1.data.UserUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    // Les informations de l'utilisateur, vous pouvez les initialiser comme nécessaire
    private val _user = MutableStateFlow(User("ne fonctionne pas", "ne fonctionne pas"))
    val user: StateFlow<User> get() = _user


    // Met à jour les informations de l'utilisateur avec un UserUpdate
    fun updateUserInfo(userUpdate: UserUpdate) {
        viewModelScope.launch {
            val response = API.userWebService.update(userUpdate)
            // TODO: Gérer la réponse de l'API
        }
    }
}
