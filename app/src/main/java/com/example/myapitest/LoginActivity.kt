package com.example.myapitest

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapitest.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.FirebaseException
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var idVerificacao = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        verifyLoggedUser()
        setUpView()
    }


    private fun verifyLoggedUser() {
        if (auth.currentUser != null) {
            startActivity(MainActivity.newIntent(this))
        }
    }

    private fun setUpView() {
        supportActionBar?.title = getString(R.string.login)
        binding.btnEnviarSMS.setOnClickListener {
            enviarSMSOnClick()
        }
        binding.btnVerificar.setOnClickListener {
            verificarOnClick()
        }
    }

    private fun verificarOnClick() {
        val codVerificacao = binding.etSmsCode.text.toString()

        if (codVerificacao.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.e_necessario_preencher_o_numero_de_telefone),
                Toast.LENGTH_SHORT
            ).show()
            binding.etSmsCode.requestFocus()
            return
        }

        val credencial = PhoneAuthProvider.getCredential(idVerificacao, codVerificacao)
        auth.signInWithCredential(credencial).addOnCompleteListener { task ->
            if(task.isSuccessful){
                startActivity(MainActivity.newIntent(this))
            }else{
                Toast.makeText(
                    this,
                    getString(R.string.erro_na_autenticacao_de_usuario),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun enviarSMSOnClick() {
        val phoneNumber = binding.etPhoneNumber.text.toString()
        if (phoneNumber.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.e_necessario_preencher_o_numero_de_telefone),
                Toast.LENGTH_SHORT
            ).show()
            binding.etPhoneNumber.requestFocus()
            return
        }

        val opcoes = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                }
                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.erro_desconhecido_ao_tentar_enviar_codigo_pelo_sms),
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    idVerificacao: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@LoginActivity.idVerificacao = idVerificacao
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.codigo_de_verificacao_enviado),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.tlSmsCode.visibility = View.VISIBLE
                    binding.btnVerificar.visibility = View.VISIBLE

                }

            }).build()
        PhoneAuthProvider.verifyPhoneNumber(opcoes)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}

