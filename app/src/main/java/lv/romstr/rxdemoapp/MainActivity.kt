package lv.romstr.rxdemoapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import lv.romstr.rxdemoapp.api.RequestType
import lv.romstr.rxdemoapp.api.numbers

@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    private val numbers = numbers()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mathCheck.checks()
            .mergeWith(dateCheck.checks())
            .mergeWith(yearCheck.checks())
            .doOnNext { selectOnly(it) }
            .subscribe { updateTexts(it) }

        Observables.combineLatest(
            input.textChanges().map(CharSequence::isNotBlank),
            randomCheck.checkedChanges()
        ) { a, b -> a || b }
            .subscribe { button.isEnabled = it }

        button.clicks()
            .map { selectedRequest() }
            .subscribe { fetch(it) }
    }

    private fun selectedRequest(): RequestType {
        val number = if (randomCheck.isChecked) "random" else input.text.toString()
        val day = dayInput.text.toString()
        return when {
            mathCheck.isChecked -> RequestType.MathRequest(number)
            yearCheck.isChecked -> RequestType.YearRequest(number)
            dateCheck.isChecked -> if (randomCheck.isChecked) {
                RequestType.RandomDateRequest
            } else {
                RequestType.DateRequest(number, day)
            }
            else -> RequestType.TriviaRequest(number)
        }
    }

    private fun selectOnly(checkBox: CheckBox) {
        mathCheck.isChecked = false
        dateCheck.isChecked = false
        yearCheck.isChecked = false
        checkBox.isChecked = true
    }

    private fun updateTexts(checkBox: CheckBox) {
        dayInput.isVisible = false
        dayInputLabel.isVisible = false
        inputLabel.text = when (checkBox) {
            dateCheck -> "Enter month".also {
                dayInput.isVisible = true
                dayInputLabel.isVisible = true
            }
            yearCheck -> "Enter the year:"
            else -> "Enter your number:"
        }
    }

    private fun CheckBox.checks() = checkedChanges().skipInitialValue().filter { it }.map { this }

    private fun fetch(request: RequestType) {
        request.invoke(numbers)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progress.isVisible = true }
            .doOnSubscribe { result.text = "" }
            .doAfterTerminate { progress.isVisible = false }
            .subscribe(result::setText) { toast(it.message ?: "Error occured") }
    }

    private fun toast(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}
