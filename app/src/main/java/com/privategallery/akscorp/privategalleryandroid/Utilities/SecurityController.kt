package com.privategallery.akscorp.privategalleryandroid.Utilities

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.privategallery.akscorp.privategalleryandroid.Database.SignInPreference
import com.privategallery.akscorp.privategalleryandroid.R
import com.privategallery.akscorp.privategalleryandroid.Widgets.SquareImageView
import kotlinx.android.synthetic.main.pin_dialog.pin_dialog_text
import kotlinx.android.synthetic.main.pin_dialog.view.num_0
import kotlinx.android.synthetic.main.pin_dialog.view.num_1
import kotlinx.android.synthetic.main.pin_dialog.view.num_2
import kotlinx.android.synthetic.main.pin_dialog.view.num_3
import kotlinx.android.synthetic.main.pin_dialog.view.num_4
import kotlinx.android.synthetic.main.pin_dialog.view.num_5
import kotlinx.android.synthetic.main.pin_dialog.view.num_6
import kotlinx.android.synthetic.main.pin_dialog.view.num_7
import kotlinx.android.synthetic.main.pin_dialog.view.num_8
import kotlinx.android.synthetic.main.pin_dialog.view.num_9

/**
 * Sign in types
 *
 * [PIN] - use 4 number for access
 */
val PIN = 0
val NONE = -1

class SecurityController(val context: Context) {

    companion object {

        val LOGIN_DONE = 1
        val LOGIN_ERROR = 2
        val LOGIN_DENIDE = 3
        val LOGIN_NOT_SUBMIT = 4

    }

    private val sharedPreferences =
            context.getSharedPreferences(SignInPreference.NAME, Context.MODE_PRIVATE)


    lateinit var securityDialog: SecurityDialog

    val loginStatus: Int
        get() {
            if (::securityDialog.isInitialized)
                return securityDialog.loginStatus
            else
                return LOGIN_NOT_SUBMIT
        }

    /**
     * Show dialog for confirm any action
     *
     * @param type - can be any sign in type(for example it can be [PIN])
     * @param correctPasswordAction - function with action when password was submit
     * @param incorrectPasswordAction - function with action when password was submit, but incorrect
     */
    fun showSecurityDialog(securityDialog: SecurityDialog) {
        this.securityDialog = securityDialog
        securityDialog.showSecurityDialog()
    }

    fun dismissSecurityDialog() {
        if (::securityDialog.isInitialized)
            securityDialog.dismissDialog()
    }

    fun logout() {
        if (::securityDialog.isInitialized)
            securityDialog.logout()
    }

    fun getAppSecurityType(): Int {
        return sharedPreferences.getInt(SignInPreference.FIELDS.CURRENT_SECURITY_TYPE, -1)
    }
}

class PasswordControl(private val context: Context) {
    private val sharedPreferences =
            context.getSharedPreferences(SignInPreference.NAME, Context.MODE_PRIVATE);

    fun checkPassword(password: String): Boolean {
        val salt =
                sharedPreferences.getString(
                        SignInPreference.FIELDS.SALT,
                        "Kak tebe takoe Ilon Mask?"
                )
        val result = sharedPreferences.getString(SignInPreference.FIELDS.RESULT, "")

        val hash = Utilities.HashUtils.sha256(salt + password)

        if (hash == result)
            return true
        return false
    }

    fun savePassword(password: String, type: Int) {
        val salt = Utilities.getRandomString(16, 256)
        val hash = Utilities.HashUtils.sha256(salt + password)
        sharedPreferences.edit()
                .putString(SignInPreference.FIELDS.SALT, salt)
                .putInt(SignInPreference.FIELDS.CURRENT_SECURITY_TYPE, type)
                .putString(SignInPreference.FIELDS.RESULT, hash).apply()

    }

}

abstract class SecurityDialog(open val context: Context) {
    protected abstract var value: String
        protected set

    var loginStatus: Int = SecurityController.LOGIN_DENIDE
        protected set

    protected abstract fun clickAction()
    protected fun setKeyboardListener(view: View) {
        view.num_0.setOnClickListener {
            value += "0"
            clickAction()
        }
        view.num_1.setOnClickListener {
            value += "1"
            clickAction()
        }
        view.num_2.setOnClickListener {
            value += "2"
            clickAction()
        }
        view.num_3.setOnClickListener {
            value += "3"
            clickAction()
        }
        view.num_4.setOnClickListener {
            value += "4"
            clickAction()
        }
        view.num_5.setOnClickListener {
            value += "5"
            clickAction()
        }
        view.num_6.setOnClickListener {
            value += "6"
            clickAction()
        }
        view.num_7.setOnClickListener {
            value += "7"
            clickAction()
        }
        view.num_8.setOnClickListener {
            value += "8"
            clickAction()
        }
        view.num_9.setOnClickListener {
            value += "9"
            clickAction()
        }
    }

    abstract fun showSecurityDialog()
    abstract fun dismissDialog()
    abstract fun logout()

    /**
     * @param view with sign in input fields for authorization
     * @return alert dialog for show
     */
    protected fun getSecurityDialog(view: View): AlertDialog {
        val securityDialog = AlertDialog.Builder(context)

        securityDialog.setView(view)

        securityDialog.setCancelable(false)

        return securityDialog.create()
    }
}

/**
 * @param acceptAction - action which will be called when PIN will be correct
 * @param rejectAction -  action which will be called when PIN will be incorrect
 */
class LoginPinDialog(
        override val context: Context,
        private val acceptAction: (loginPinDialog: LoginPinDialog) -> Unit,
        private val rejectAction: (loginPinDialog: LoginPinDialog) -> Unit = {}
) : SecurityDialog(context) {


    public override var value = ""
    private var authFailedCounter: Int = 10

    /**
     * Dialog with sign in inputs layout. It can be different sign types in screen
     */
    lateinit var securityDialog: AlertDialog

    /**
     * PIN points with current submit progress
     */
    private val pinPoints =
            listOf(R.id.pin_point_1, R.id.pin_point_2, R.id.pin_point_3, R.id.pin_point_4)

    override fun dismissDialog() {
        securityDialog.dismiss()
    }

    override fun logout() {
        value = ""
        loginStatus = SecurityController.LOGIN_NOT_SUBMIT
    }

    override fun showSecurityDialog() {
        securityDialog = getSecurityDialog(getPinView()!!)
        securityDialog.show()
    }

    fun setMessage(message: String) {
        securityDialog.pin_dialog_text.text = message
    }

    override fun clickAction() {
        fillPinPoint(value.length - 1)

        if (value.length == 4) {

            val passwordControl = PasswordControl(context)
            if (passwordControl.checkPassword(value)) {
                securityDialog.cancel()
                super.loginStatus = SecurityController.LOGIN_DONE
                acceptAction(this)
            } else {
                authFailedCounter--
                clearPinPoints()
                setMessage(context.getString(R.string.incorrect_pin))
                super.loginStatus = SecurityController.LOGIN_DENIDE
                rejectAction(this)
            }
            value = ""
        }

    }

    private fun fillPinPoint(pos: Int) {
        securityDialog.findViewById<SquareImageView>(pinPoints[pos])
                .setImageResource(R.drawable.pin_point_fill)
    }

    fun clearPinPoints() {
        for (point in pinPoints)
            securityDialog.findViewById<SquareImageView>(point)
                    .setImageResource(R.drawable.pin_point_empty)
    }

    private fun getPinView(): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.pin_dialog, null)
        setKeyboardListener(view)
        return view
    }
}

/**
 * @param acceptAction - action which will be called when PIN will be correct
 */
class EstablishPinDialog(
        override val context: Context,
        private val acceptAction: (loginPinDialog: EstablishPinDialog) -> Unit = {}
) : SecurityDialog(context) {


    public override var value = ""

    /**
     * Dialog with sign in inputs layout. It can be different sign types in screen
     */
    private lateinit var securityDialog: AlertDialog

    /**
     * PIN points with current submit progress
     */
    private val pinPoints =
            listOf(R.id.pin_point_1, R.id.pin_point_2, R.id.pin_point_3, R.id.pin_point_4)

    override fun dismissDialog() {
        securityDialog.dismiss()
    }

    override fun logout() {
        value = ""
        loginStatus = SecurityController.LOGIN_NOT_SUBMIT
    }

    override fun showSecurityDialog() {
        securityDialog = getSecurityDialog(getPinView()!!)
        securityDialog.show()
    }

    fun setMessage(message: String) {
        securityDialog.pin_dialog_text.text = message
    }

    override fun clickAction() {
        fillPinPoint(value.length - 1)

        if (value.length == 4) {
            val passwordControl = PasswordControl(context)
            passwordControl.savePassword(value, PIN)
            value = ""
            securityDialog.cancel()
            acceptAction(this)
        }

    }

    private fun fillPinPoint(pos: Int) {
        securityDialog.findViewById<SquareImageView>(pinPoints[pos])
                .setImageResource(R.drawable.pin_point_fill)
    }

    fun clearPinPoints() {
        for (point in pinPoints)
            securityDialog.findViewById<SquareImageView>(point)
                    .setImageResource(R.drawable.pin_point_empty)
    }

    private fun getPinView(): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.pin_dialog, null)
        setKeyboardListener(view)
        return view
    }
}

/**
 * @param acceptAction - action which will be called when PIN will be correct
 */
class EstablishNoneDialog(
        override val context: Context,
        private val acceptAction: (establishNoneDialog: EstablishNoneDialog) -> Unit = {}
) : SecurityDialog(context) {


    public override var value = ""

    override fun dismissDialog() {
    }

    override fun logout() {
        value = ""
        loginStatus = SecurityController.LOGIN_DONE
    }

    override fun showSecurityDialog() {
        val passwordControl = PasswordControl(context)
        passwordControl.savePassword(value, NONE)
        acceptAction(this)
        loginStatus = SecurityController.LOGIN_DONE
    }

    override fun clickAction() {

    }
}



